const http = axios.create({
    baseURL: 'http://localhost:8080/api',
});

// 1. Request Interceptor (Vẫn như cũ)
http.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) config.headers['Authorization'] = `Bearer ${token}`;
    return config;
});

// 2. Response Interceptor (Nơi xử lý Logic Refresh)
http.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Nếu lỗi 401 và chưa từng thử refresh cho request này
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true; // Đánh dấu để tránh lặp vô tận

            try {
                // Gọi API refresh (Server sẽ tự đọc Refresh Token từ Cookie)
                // Lưu ý: Dùng chính axios gốc hoặc một instance khác để tránh interceptor cũ
                const res = await axios.post('/auth/refresh', {}, { withCredentials: true });

                if (res.status === 200) {
                    const newAccessToken = res.data.accessToken;

                    // Lưu token mới vào storage
                    localStorage.setItem('accessToken', newAccessToken);

                    // Cập nhật lại header cho request cũ và thực hiện lại nó
                    originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                    return http(originalRequest);
                }
            } catch (refreshError) {
                // Nếu refresh cũng lỗi (hết hạn refresh token) -> Cho cook luôn
                console.error("Refresh token expired. Logging out...");
                localStorage.removeItem('accessToken');
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);