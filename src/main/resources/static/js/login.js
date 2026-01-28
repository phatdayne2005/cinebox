document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const submitBtn = e.target.querySelector('button[type="submit"]');
    const messageBox = document.getElementById('message-box'); // Đảm bảo bạn có ID này giống bên register

    try {
        // 1. Lấy dữ liệu từ form và chuyển thành Object
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData.entries());

        const urlParams = new URLSearchParams(window.location.search);
        const continueUrl = urlParams.get('continue') || '/';

        if (typeof api === 'undefined') {
            throw new Error("Hệ thống cấu hình lỗi (thiếu api-config.js)");
        }

        // 2. Trạng thái Loading (mượt mà như register)
        if (messageBox && !messageBox.classList.contains('hidden')) {
            messageBox.style.opacity = "0.5";
        }
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.classList.add('opacity-50', 'cursor-not-allowed');
            submitBtn.innerHTML = '<span class="inline-block animate-spin mr-2">↻</span> Processing...';
        }

        // 3. Gửi request dạng JSON
        // Lưu ý: Không dùng URLSearchParams cho body nữa để nó tự hiểu là JSON
        const response = await api.post(`/auth/login?continue=${encodeURIComponent(continueUrl)}`, data);

        // 4. Nếu thành công
        if (response.data && response.data.redirectTo) {
            window.location.href = response.data.redirectTo;
        } else {
            window.location.href = "/";
        }

    } catch (error) {
        if (messageBox) messageBox.style.opacity = "1";

        console.error("LỖI ĐĂNG NHẬP:", error);
        let errorMsg = "Có lỗi xảy ra, vui lòng thử lại.";

        // Xử lý message lỗi từ Server
        if (error.response) {
            if (error.response.status === 400 || error.response.status === 401) {
                errorMsg = "Your email or password is incorrect";
            } else if (error.response.data && error.response.data.message) {
                errorMsg = error.response.data.message;
            }
        } else {
            errorMsg = error.message;
        }

        // Sử dụng hàm showMessage của bạn
        showMessage(errorMsg, 'error');

    } finally {
        // 5. Reset trạng thái nút bấm
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
            submitBtn.innerHTML = 'Login';
        }
    }
});

function showMessage(text, type) {
    const box = document.getElementById('message-box');
    box.innerText = text;

    // Reset classes: Xóa hidden và xóa luôn animate-pulse để không bị nhấp nháy
    box.classList.remove('hidden', 'animate-pulse', 'bg-red-100', 'text-red-700', 'bg-green-100', 'text-green-700', 'dark:bg-red-900/30', 'dark:text-red-400', 'dark:bg-green-900/30', 'dark:text-green-400');

    if (type === 'error') {
        box.classList.add('bg-red-100', 'text-red-700', 'dark:bg-red-900/30', 'dark:text-red-400');
    } else {
        box.classList.add('bg-green-100', 'text-green-700', 'dark:bg-green-900/30', 'dark:text-green-400');
    }
}