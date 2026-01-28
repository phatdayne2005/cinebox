document.addEventListener('DOMContentLoaded', function () {
    const passwordInput = document.getElementById('password');
    const confirmInput = document.getElementById('confirm-password');
    const passwordError = document.getElementById('password-error');
    const registerForm = document.getElementById('register-form');
    const messageBox = document.getElementById('message-box');
    const submitBtn = registerForm.querySelector('button[type="submit"]');

    function showStatus(msg, type) {
        // Nếu thông báo đang hiện và nội dung giống hệt, chỉ cần tạo hiệu ứng "rung" để báo hiệu
        if (!messageBox.classList.contains('hidden') && messageBox.textContent === msg) {
            messageBox.classList.remove('animate-shake'); // Reset animation
            void messageBox.offsetWidth; // Trigger reflow để restart animation
            messageBox.classList.add('animate-shake');
            return;
        }

        // Nếu là nội dung mới, cập nhật class nhưng KHÔNG dùng hidden để tránh giật layout
        messageBox.classList.remove('hidden', 'bg-green-100', 'text-green-700', 'bg-red-100', 'text-red-700',
            'dark:bg-green-900/30', 'dark:text-green-400', 'dark:bg-red-900/30', 'dark:text-red-400');

        if (type === 'success') {
            messageBox.classList.add('bg-green-100', 'text-green-700', 'dark:bg-green-900/30', 'dark:text-green-400');
        } else {
            messageBox.classList.add('bg-red-100', 'text-red-700', 'dark:bg-red-900/30', 'dark:text-red-400');
        }
        messageBox.textContent = msg;
    }

    registerForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        if (passwordInput.value !== confirmInput.value) {
            showStatus('Passwords do not match!', 'error');
            return;
        }

        // THAY ĐỔI TẠI ĐÂY: Không ẩn messageBox, chỉ làm mờ nó để báo hiệu đang xử lý
        if (!messageBox.classList.contains('hidden')) {
            messageBox.style.opacity = "0.5";
        }

        submitBtn.disabled = true;
        submitBtn.classList.add('opacity-50', 'cursor-not-allowed');
        const originalBtnText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<span class="inline-block animate-spin mr-2">↻</span> Processing...';

        const formData = new FormData(registerForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(registerForm.action, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            let result;
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                result = await response.json();
            } else {
                result = { message: await response.text() };
            }

            // Reset opacity trước khi hiện thông báo mới
            messageBox.style.opacity = "1";

            if (response.ok) {
                showStatus(result.message || 'Account created successfully!', 'success');
                registerForm.reset();
            } else {
                showStatus(result.message || 'Registration failed.', 'error');
            }
        } catch (error) {
            messageBox.style.opacity = "1";
            showStatus('Network error!', 'error');
        } finally {
            submitBtn.disabled = false;
            submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
            submitBtn.innerHTML = 'Create Account';
        }
    });
});