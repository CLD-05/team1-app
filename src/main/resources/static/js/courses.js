document.addEventListener("DOMContentLoaded", () => {
    fetchStudentInfo(); // 1. 학생 정보 먼저 가져오기
    fetchCourses();     // 2. 강의 목록 가져오기
    
const enrollmentLink = document.getElementById('myEnrollmentLink');
    if (enrollmentLink) {
        enrollmentLink.addEventListener('click', (e) => {
            e.preventDefault();
            const token = localStorage.getItem('jwtToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                return;
            }
            window.location.href = '/enrollments.html'; 
        });
    }
});
// 수강 신청
function enroll(courseId) {
    // 키 이름을 'jwtToken'으로 수정!
    const token = localStorage.getItem('jwtToken'); 

    fetch('/api/enrollments', {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token // 토큰 전달!
        },
        body: JSON.stringify({ courseId: courseId })
	}).then(async (res) => { // async 추가
        if (res.ok) {
            alert('신청 성공');
            fetchCourses();
        } else {
            // 서버에서 보낸 에러 메시지를 받아옵니다.
            const errorData = await res.text(); 
            // 서버 메시지가 있다면 보여주고, 없다면 기본 메시지 출력
            alert('신청 실패: 이미 신청했거나 마감된 강의입니다.');
        }
    }).catch(err => {
        console.error(err);
        alert('네트워크 오류가 발생했습니다.');
    });
}

// 1. 학생 정보 가져오기 (API 필요: /api/student/info)
async function fetchStudentInfo() {
    try {
        const token = localStorage.getItem('jwtToken'); // 키 이름 수정!
        const response = await fetch('/api/auth/info', {
            method: 'GET',
            headers: { 'Authorization': 'Bearer ' + token } // 헤더 추가!
        });
        
        if (!response.ok) throw new Error("데이터 가져오기 실패");
        
        const data = await response.json();
        document.getElementById('student-name').innerText = data.name + ' 님';
        document.getElementById('student-initial').innerText = data.name.charAt(0);
    } catch (e) {
        console.error("학생 정보를 가져오는데 실패했습니다.", e);
    }
}

// 2. 강의 목록 가져오기
async function fetchCourses(page = 0) {
    const keyword = document.getElementById('search-keyword').value;
    const response = await fetch(`/api/courses?name=${keyword}&page=${page}`);
    const data = await response.json();
    
    const tbody = document.getElementById('course-table-body');
    const meta = document.getElementById('table-meta');
    tbody.innerHTML = '';
    meta.innerText = `총 ${data.totalElements}개 강의`;

    if (data.content.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6"><div class="empty-state">검색 결과가 없습니다</div></td></tr>';
        return;
    }

    data.content.forEach(course => {
        const row = `<tr>
            <td><span class="course-code">${course.courseCode}</span></td>
            <td>${course.courseName}</td>
            <td>${getBadge(course.courseType)}</td>
            <td>${course.credits}학점</td>
            <td>
                <div class="capacity-bar-wrap">
                    <div class="capacity-bar">
                        <div class="capacity-fill ${course.currentEnrollment >= course.capacity ? 'full' : ''}" 
                             style="width:${course.capacity > 0 ? (course.currentEnrollment * 100 / course.capacity) : 0}%"></div>
                    </div>
                    <span class="capacity-text">${course.currentEnrollment}/${course.capacity}</span>
                </div>
            </td>
            <td>${getEnrollBtn(course)}</td>
        </tr>`;
        tbody.innerHTML += row;
    });
}

// 헬퍼 함수들
function getBadge(type) {
    const types = { 'MAJOR_REQUIRED': '전공필수', 'MAJOR_ELECTIVE': '전공선택', 'GENERAL_REQUIRED': '교양필수', 'GENERAL_ELECTIVE': '교양선택' };
    return `<span class="badge">${types[type] || type}</span>`;
}

function getEnrollBtn(course) {
    if (course.currentEnrollment >= course.capacity) return '<span class="full-text">마감</span>';
    return `<button class="enroll-btn" onclick="enroll(${course.courseId})">신청</button>`;
}

function logout() {
    // 1. 저장된 JWT 토큰을 로컬 스토리지에서 삭제
    localStorage.removeItem('jwtToken');
    
    // 2. 사용자에게 알림
    alert('로그아웃 되었습니다.');
    
    // 3. 로그인 페이지로 이동
    window.location.href = '/index.html'; 
}