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

## Demo tạm thời qua Cloudflare Quick Tunnel

Chỉ dùng với dữ liệu giả. Quick Tunnel tạo URL HTTPS công khai ngẫu nhiên; bất kỳ ai có URL đều có thể mở ứng dụng, và URL sẽ mất khi dừng tunnel. Không dùng cách này cho dữ liệu thật hoặc vận hành tài chính.

Thực hiện các lệnh từ thư mục gốc của repository. Đảm bảo MySQL đang chạy, file `.env` ở thư mục gốc đã có các biến cấu hình cần thiết, và backend dùng một cơ sở dữ liệu demo riêng. Không đưa cổng MySQL ra Internet.

1. Khởi động backend trong terminal thứ nhất. Thư mục làm việc phải là `quanlythau-backend` để mặc định `../uploads` trỏ về thư mục `uploads` ở gốc repository, dùng chung với các ảnh hiện có. Có thể đặt `UPLOAD_DIR` trong `.env` để chỉ định một thư mục lưu ảnh khác:

   ```bash
   cd quanlythau-backend
   set -a; . ../.env; set +a
   mvn spring-boot:run
   ```

2. Khởi động frontend trong terminal thứ hai:

   ```bash
   npm --prefix quanlythau-frontend run dev
   ```

3. Mở tunnel tới frontend trong terminal thứ ba (không tunnel cổng `8080`, `8081` hoặc `3306`):

   ```bash
   cloudflared tunnel --url http://127.0.0.1:5173
   ```

4. Mở URL `https://...trycloudflare.com` do lệnh hiển thị. Dừng tunnel bằng `Ctrl+C`.

Frontend proxy API, WebSocket và ảnh tới backend cục bộ; trình duyệt trên điện thoại chỉ gọi cùng origin `https://...trycloudflare.com`, còn Vite chuyển tiếp các đường dẫn `/api`, `/uploads` và `/ws` tới `127.0.0.1:8081`. Backend cho phép origin localhost khi phát triển và HTTPS Quick Tunnel cho demo. Backend và database không cần lắng nghe trên giao diện mạng công cộng. Để chạy lâu dài với dữ liệu thật, cần domain riêng, tunnel có tên, cấu hình origin phù hợp, xác thực/kiểm soát truy cập phù hợp, secrets mới và quy trình sao lưu/khôi phục đã kiểm thử.
