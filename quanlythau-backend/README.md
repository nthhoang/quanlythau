# Backend – Hệ thống Quản lý Thầu Xây Dựng

Spring Boot 3 (Java 21) + MySQL + JWT.

## Module đã có
- Auth (đăng ký/đăng nhập, JWT, 2 vai trò CHU_THAU/THO)
- Quản lý công trình (CRUD, phân công thợ)
- Chấm công kiểu kiosk (nhận diện khuôn mặt, không GPS, hỗ trợ nhiều công trình/ngày)
- Tính lương (đơn giá công snapshot theo thời điểm, tăng ca theo %, tạm ứng, chốt lương)
- Vật tư (CRUD, quét hóa đơn bằng Gemini Vision, điều chuyển giữa công trình)
- Chi phí và lãi/lỗ theo thời gian thực (tổng hợp nhân công + vật tư + chi phí khác, cảnh báo theo ngưỡng)

## Chạy
```bash
mvn spring-boot:run
```
Sửa `src/main/resources/application.yml` (username/password MySQL, và `gemini.api-key` nếu muốn dùng quét hóa đơn AI) trước khi chạy lần đầu.
