# Hệ Thống Đặt Lịch Khám Bệnh Trực Tuyến - CareHealth

**CareHealth** là hệ thống quản lý và đặt lịch khám bệnh trực tuyến được xây dựng trên nền tảng **Java 21** và **Spring Boot**, tích hợp đồng thời **giao diện Web (HTTP)** và **kênh giao tiếp TCP Socket đa luồng** để xử lý nghiệp vụ đặt lịch theo thời gian thực.

---

## 1. Tổng Quan Kiến Trúc & Công Nghệ

Hệ thống được thiết kế theo mô hình phân tầng, chia sẻ chung tầng xử lý nghiệp vụ (`Service`) và cơ sở dữ liệu:

* **Ngôn ngữ & Nền tảng:** Java 21 (JDK 21), Spring Boot, Spring Security, Spring Data JPA.
* **Giao diện người dùng (Frontend):** Thymeleaf, Tailwind CSS, HTML5/JavaScript.
* **Cơ sở dữ liệu:** Microsoft SQL Server (`HeThongDatLichKhamBenh`).
* **Giao tiếp mạng & Đa luồng:**
  * **Kênh Web (Port `8080`):** Phục vụ các chức năng quản trị hệ thống, bác sĩ và bệnh nhân qua trình duyệt.
  * **Kênh TCP Socket (Port `9999`):** Socket Server chạy song song bên trong ứng dụng Spring Boot, sử dụng Thread Pool (`ExecutorService`) để tiếp nhận nhiều kết nối đồng thời từ Client Console và gọi trực tiếp vào `DatLichKhamService` nhằm đảm bảo tính toàn vẹn dữ liệu khi tranh chấp khung giờ.

---

## 2. Các Chức Năng Chính

### Phân hệ Quản Trị Viên (Admin Portal)
* **Quản lý Lịch hẹn (`/admin/dashboard`):** Theo dõi thống kê lịch hẹn theo trạng thái (`Chờ xác nhận`, `Đã xác nhận`, `Đã khám`, `Đã hủy`), tìm kiếm và xem chi tiết hồ sơ bệnh án, sinh hiệu, đơn thuốc.
* **Quản lý Chuyên khoa (`/admin/chuyenKhoa`):** Thêm mới, cập nhật thông tin chuyên khoa và phân bổ/gán phòng khám trực thuộc (`PhongKham`).
* **Quản lý Bác sĩ (`/admin/bacSi`):** Cấp tài khoản, cập nhật học hàm/học vị, chuyên khoa và thông tin chuyên môn.
* **Quản lý Lịch làm việc (`/admin/heThong/lichLamViec`):** Tạo ca làm việc hàng loạt theo tuần/tháng cho bác sĩ và phòng khám.
* **Quản lý Tài khoản (`/admin/taiKhoan`):** Tìm kiếm, phân loại vai trò và khóa/mở khóa trạng thái hoạt động của tài khoản.

### Phân hệ Kiểm Thử Đặt Lịch Đa Luồng (TCP Socket Client)
* Chương trình `ClientConsole` độc lập kết nối tới Socket Server (`127.0.0.1:9999`).
* Hỗ trợ kiểm tra kết nối (`PING`/`PONG`), đặt lịch đơn lẻ và **kiểm thử đa luồng (`KiemThuNhieuClient`)** sử dụng `CountDownLatch` để đồng bộ nhiều Client cùng bắn yêu cầu `BOOK` vào một khung giờ tại cùng một thời điểm.

---

## 3. Yêu Cầu Môi Trường (Prerequisites)

Trước khi cài đặt và chạy dự án, đảm bảo máy tính đã cài đặt đầy đủ:
1. **Java Development Kit (JDK) 21** (ví dụ: Microsoft Build of OpenJDK 21 hoặc Oracle JDK 21).
2. **Microsoft SQL Server** (phiên bản 2019 trở lên) và **SQL Server Management Studio (SSMS)**.
3. **IDE:** IntelliJ IDEA (khuyến nghị) hoặc Eclipse / VS Code.
4. **Maven** (có thể dùng Maven Wrapper `mvnw` đi kèm trong dự án).

---

## 4. Hướng Dẫn Cài Đặt & Cấu Hình

### Bước 1: Thiết lập Cơ sở dữ liệu
1. Mở **SSMS**, tạo mới một database tên `HeThongDatLichKhamBenh` (hoặc chạy script SQL khởi tạo cấu trúc bảng và dữ liệu mẫu đi kèm dự án).
2. Đảm bảo dịch vụ **SQL Server (MSSQLSERVER)** đang chạy và bật giao thức **TCP/IP** trên cổng mặc định `1433` trong *SQL Server Configuration Manager*.

### Bước 2: Cấu hình kết nối trong Spring Boot
Mở file `src/main/resources/application.properties` (hoặc `application.yml`) và cập nhật thông tin đăng nhập SQL Server của bạn:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=HeThongDatLichKhamBenh;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=your_password_here

server.port=8080
```

### Bước 3: Cấu hình JDK 21 trong IntelliJ IDEA
1. Mở dự án trong IntelliJ IDEA.
2. Vào **File → Project Structure → Project**:
   * **SDK:** Chọn đúng phiên bản **JDK 21**.
   * **Language level:** Chọn **21**.
3. Kiểm tra tương tự ở mục **Modules** để đảm bảo không bị xung đột với các phiên bản JDK khác trên máy.

---

## 5. Hướng Dẫn Khởi Chạy Dự Án

### Phần A: Khởi chạy Web Server & TCP Socket Server
1. Tìm file khởi chạy chính `DoAnJavaApplication.java` trong package `com.example.doanjava`.
2. Nhấn **Run** (`Shift + F10`).
3. Quan sát cửa sổ **Run Console**, khi thấy cả hai thông báo sau nghĩa là hệ thống đã sẵn sàng:
   * Tomcat khởi động thành công trên cổng **`8080`**.
   * TCP Socket Server lắng nghe thành công tại **`127.0.0.1:9999`**.
4. Truy cập giao diện Web tại địa chỉ: `http://localhost:8080`

### Phần B: Chạy Client Console Kiểm Thử TCP Socket

#### Cách 1: Chạy trực tiếp trong IntelliJ IDEA
1. Mở thư mục dự án `client` trong IntelliJ IDEA và chọn **SDK là JDK 21**.
2. Vào **Run → Edit Configurations...**, tạo cấu hình **Application** mới cho lớp `com.example.doanjavaclient.ClientConsole`:
   * **Program arguments:** `127.0.0.1 9999`
   * **VM options:** `-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8`
3. Nhấn **Run** để mở giao diện menu điều khiển trên console.

#### Cách 2: Chạy bằng PowerShell (Gọi trực tiếp JDK 21)
Mở PowerShell tại thư mục `client` và thực hiện các lệnh sau (thay đổi đường dẫn JDK 21 phù hợp với máy của bạn):

```powershell
# 1. Thiết lập bảng mã UTF-8
chcp 65001

# 2. Biên dịch mã nguồn Client
$javac21 = "C:\Users\Oil\.jdks\ms-21.0.13.0.2\bin\javac.exe"
New-Item -ItemType Directory -Force target\classes | Out-Null

& $javac21 -encoding UTF-8 -d target\classes `
  src\main\java\com\example\doanjavaclient\SocketClientProtocol.java `
  src\main\java\com\example\doanjavaclient\SocketClientDatLich.java `
  src\main\java\com\example\doanjavaclient\KiemThuNhieuClient.java `
  src\main\java\com\example\doanjavaclient\ClientConsole.java

# 3. Khởi chạy Client Console
$java21 = "C:\Users\Oil\.jdks\ms-21.0.13.0.2\bin\java.exe"
& $java21 "-Dfile.encoding=UTF-8" "-Dsun.stdout.encoding=UTF-8" "-Dsun.stderr.encoding=UTF-8" -cp "target\classes" com.example.doanjavaclient.ClientConsole 127.0.0.1 9999
```

---

## 6. Kịch Bản Kiểm Thử Đặt Lịch Đồng Thời (Demo)

1. **Chuẩn bị dữ liệu:** Mở SSMS truy vấn bảng `dbo.lich_lam_viec` và `dbo.benh_nhan` để lấy:
   * Một ca làm việc còn trống khung giờ (ví dụ: `08:00 - 08:20`).
   * Danh sách các ID bệnh nhân hợp lệ liên tiếp (ví dụ: `11`, `12`, `13`).
2. **Kiểm tra kết nối:** Trên menu `ClientConsole`, chọn phím `3` (`PING`) để xác nhận Server phản hồi `PONG`.
3. **Chạy kiểm thử đa luồng:**
   * Chọn phím `1` trên menu `ClientConsole`.
   * Nhập số lượng Client (ví dụ: `3`), ID bệnh nhân đầu tiên (`11`), ID bác sĩ, ID lịch làm việc, ngày khám (`yyyy-MM-dd`), khung giờ (`08:00 - 08:20`) và triệu chứng.
   * Nhấn `ENTER` để đồng loạt phát tín hiệu gửi lệnh `BOOK`.
4. **Đối chiếu kết quả:**
   * **Client Console:** Chỉ có **1 Client** nhận trạng thái `DAT LICH THANH CONG`, các Client còn lại bị từ chối với thông báo khung giờ không còn trống.
   * **Server Console:** Hiển thị log `[ACCEPT]` và các Worker Thread (`dat-lich-worker-1..3`) xử lý đồng thời.
   * **SQL Server:** Bảng `dbo.lich_hen` chỉ ghi nhận duy nhất **1 bản ghi** hợp lệ cho khung giờ đã kiểm thử.