// enrollments.js 파일의 최상단
document.addEventListener("DOMContentLoaded", () => {
    console.log("페이지 로딩 완료, 데이터 불러오기 시작!"); // 콘솔에 찍히는지 확인
    loadEnrollments(); 
});
async function loadEnrollments() {
    try {
        const token = localStorage.getItem('jwtToken'); 
        
        // 토큰이 있으면 헤더에 추가해서 요청합니다.
        const headers = { 'Content-Type': 'application/json' };
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }

        const response = await fetch('/api/enrollments', {
            method: 'GET',
            headers: headers
        });

        if (!response.ok) {
            // 401이나 403 에러면 로그인 페이지로 보내는 것도 방법입니다.
            if (response.status === 401) window.location.href = '/index.html';
            throw new Error('데이터를 불러올 수 없습니다.');
        }
        
        const data = await response.json();
        
        // --- 이후 화면 업데이트 로직은 동일합니다 ---
        document.getElementById('studentName').innerText = data.studentName + ' 님';
        document.getElementById('avatar').innerText = data.studentName.substring(0, 1);
        document.getElementById('totalCredits').innerText = data.totalCredits;
        document.getElementById('enrollmentCount').innerText = data.enrollments.length;
        
        const tbody = document.getElementById('enrollmentList');
        tbody.innerHTML = '';
        
        if (data.enrollments.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5">신청한 강의가 없습니다.</td></tr>`;
            return;
        }

        data.enrollments.forEach(e => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${e.courseCode}</td>
                <td>${e.courseName}</td>
                <td>${e.credits}학점</td>
                <td>${formatDate(e.createdAt)}</td>
                <td><button onclick="cancelEnrollment(${e.enrollmentId})" class="cancel-btn">취소</button></td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

async function cancelEnrollment(enrollmentId) {
    if (!confirm("정말 취소하시겠습니까?")) return;

    const token = localStorage.getItem('jwtToken');
    
    const response = await fetch(`/api/enrollments/${enrollmentId}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + token }
    });

    if (response.ok) {
        alert("취소되었습니다.");
        loadEnrollments(); // 목록 새로고침
    } else {
        alert("취소에 실패했습니다.");
    }
}

function formatDate(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function logout() {
    // 1. 저장된 JWT 토큰을 로컬 스토리지에서 삭제
    localStorage.removeItem('jwtToken');
    
    // 2. 사용자에게 알림
    alert('로그아웃 되었습니다.');
    
    // 3. 로그인 페이지로 이동
    window.location.href = '/index.html'; 
}