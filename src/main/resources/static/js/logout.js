document.addEventListener('DOMContentLoaded', function() {
    const logoutBtn = document.getElementById('logout-button');

    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault(); // Chặn hành vi click mặc định

            // Hiển thị loading hoặc disable nút nếu cần
            logoutBtn.style.opacity = '0.5';
            logoutBtn.style.pointerEvents = 'none';

            // Gửi request đến /auth/logout
            // Lưu ý: Cookies sẽ tự động được trình duyệt đính kèm theo request
            axios.post('/auth/logout')
                .then(response => {
                    console.log('Logout successful');
                    // Sau khi logout thành công, điều hướng về trang chủ hoặc trang login
                    window.location.href = '/';
                })
                .catch(error => {
                    console.error('Logout failed:', error);
                    // Ngay cả khi lỗi (ví dụ token hết hạn), thường ta vẫn nên xóa session ở client
                    window.location.href = '/login';
                });
        });
    }
});