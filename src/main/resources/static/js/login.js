document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault(); // Chặn load lại trang

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData.entries());

    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // Hoặc x-www-form-urlencoded tùy Server
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            const token = result.accessToken; // Giả sử server trả về { "accessToken": "..." }

            // Lưu vào localStorage
            localStorage.setItem('accessToken', token);

            // Chuyển hướng người dùng
            window.location.href = '/dashboard';
        } else {
            alert('Đăng nhập thất bại!');
        }
    } catch (error) {
        console.error('Lỗi:', error);
    }
});