// 1. KHÔNG DÙNG IMPORT. Axios đã có sẵn từ CDN trong file HTML
const http = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

// 2. Request Interceptor
http.interceptors.request.use((config) => {
    return config;
}, (error) => {
    return Promise.reject(error);
});

// 3. Response Interceptor
http.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                // Gọi API refresh
                await http.post('/auth/refresh');
                return http(originalRequest);
            } catch (refreshError) {
                console.error("Session expired. Redirecting to login...");
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

// 4. KHÔNG DÙNG EXPORT DEFAULT. Dùng window để login.js có thể gọi được
window.api = http;