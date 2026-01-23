document.addEventListener('DOMContentLoaded', function() {
    const passwordInput = document.getElementById('password');
    const confirmInput = document.getElementById('confirm-password');
    const passwordError = document.getElementById('password-error');
    const registerForm = document.getElementById('register-form');
    const messageBox = document.getElementById('message-box');

    // Hàm hiển thị trạng thái (Đưa ra ngoài để dùng chung)
    function showStatus(msg, type) {
        messageBox.classList.remove('hidden', 'bg-green-100', 'text-green-700', 'bg-red-100', 'text-red-700',
            'dark:bg-green-900/30', 'dark:text-green-400', 'dark:bg-red-900/30', 'dark:text-red-400');
        if (type === 'success') {
            messageBox.classList.add('bg-green-100', 'text-green-700', 'dark:bg-green-900/30', 'dark:text-green-400');
        } else {
            messageBox.classList.add('bg-red-100', 'text-red-700', 'dark:bg-red-900/30', 'dark:text-red-400');
        }
        messageBox.textContent = msg;
    }

    // 1. Kiểm tra mật khẩu (Real-time)
    function validatePasswords() {
        if (confirmInput.value.length > 0 && passwordInput.value !== confirmInput.value) {
            passwordError.classList.remove('hidden');
            confirmInput.classList.add('border-primary', 'ring-1', 'ring-primary'); // Thêm ring cho rõ
        } else {
            passwordError.classList.add('hidden');
            confirmInput.classList.remove('border-primary', 'ring-1', 'ring-primary');
        }
    }

    passwordInput.addEventListener('input', validatePasswords);
    confirmInput.addEventListener('input', validatePasswords);

    // 2. Xử lý gửi Form
    registerForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        if (passwordInput.value !== confirmInput.value) {
            showStatus('Passwords do not match!', 'error');
            return;
        }

        const submitBtn = registerForm.querySelector('button[type="submit"]');

        // Trạng thái Loading
        messageBox.classList.add('hidden');
        submitBtn.disabled = true;
        submitBtn.classList.add('opacity-50', 'cursor-not-allowed'); // Thêm hiệu ứng disable
        submitBtn.innerHTML = '<svg class="animate-spin h-5 w-5 mr-3 inline-block" viewBox="0 0 24 24"></svg> Processing...';

        const formData = new FormData(registerForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(registerForm.action, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const contentType = response.headers.get("content-type");
            let result;
            if (contentType && contentType.includes("application/json")) {
                result = await response.json();
            } else {
                result = { message: await response.text() };
            }

            if (response.ok) {
                showStatus(result.message || 'Account created successfully!', 'success');
                registerForm.reset();
            } else {
                showStatus(result.message || 'Registration failed.', 'error');
            }
        } catch (error) {
            console.error("Fetch Error:", error);
            showStatus('Network error!', 'error');
        } finally {
            submitBtn.disabled = false;
            submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
            submitBtn.innerText = 'Create Account';
        }
    });
});