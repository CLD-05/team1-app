document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        // --- 핵심 수정 부분 시작 ---
        const data = await response.json(); // 서버가 보낸 응답을 JSON으로 변환
        localStorage.setItem('jwtToken', data.token); // 응답에 담긴 토큰을 저장!
        // --- 핵심 수정 부분 끝 ---
        
        window.location.href = '/courses.html';
    } else {
        alert('로그인 실패: 이메일 또는 비밀번호를 확인하세요.');
    }
});