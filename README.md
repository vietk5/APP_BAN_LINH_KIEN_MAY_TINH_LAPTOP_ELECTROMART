🛒 ElectroMart — Ứng dụng bán linh kiện máy tính & laptop

Môn: Lập trình Di động — Nhóm 6

🔁 Luồng tổng quát
<span style="background:#eef; padding:2px 8px; border-radius:6px;"> Android (Retrofit) → <b>Controller</b> → <b>Service</b> → <b>Repository</b> → <b>MySQL</b> → <b>Mapper</b> → <b>DTO</b> → Android </span>
⚙️ Backend (Spring Boot) — com.electromart.backend
📁 Thư mục chính

<span style="color:#0969da"><b>controller/</b></span>
Lớp REST Controller (Spring MVC). Nhận HTTP request, kiểm tra input cơ bản, gọi Service xử lý và trả JSON + HTTP status.
<i>Ví dụ:</i> ProductController, CategoryController.

<span style="color:#0969da"><b>service/</b></span>
Chứa <b>business logic</b>. Kết hợp nhiều repository, áp dụng luật nghiệp vụ, phân trang/sắp xếp, xử lý ngoại lệ domain.
<i>Ví dụ:</i> ProductService.

<span style="color:#0969da"><b>repository/</b></span>
Spring Data JPA: thực hiện CRUD/truy vấn DB qua interface.
<i>Ví dụ:</i> ProductRepository, SanPhamRepository, LoaiSanPhamRepository.

<span style="color:#0969da"><b>model/base/</b></span>
Các <b>JPA Entity</b> ánh xạ bảng DB (cột, khóa, quan hệ).
<i>Ví dụ:</i> SanPham, LoaiSanPham, ThuongHieu, KhachHang, NguoiDung, GioHangItemEntity,
PhieuGiamGia, PhuongThucThanhToan, PhieuThanhToan, TokenForgetPassword, Product
<small>⚠️ Nếu có cả Product và SanPham, cần phân vai trò rõ ràng để tránh trùng lặp.</small>

<span style="color:#0969da"><b>dto/</b></span>
<b>Data Transfer Object</b> trả cho app: chỉ giữ trường cần thiết, ẩn thông tin nhạy cảm, tách Entity ↔ payload.
<i>Ví dụ:</i> ProductDto.

<span style="color:#0969da"><b>mapper/</b></span>
Chuyển đổi <b>Entity ↔ DTO</b> tập trung, dễ kiểm soát và test.
<i>Ví dụ:</i> ProductMapper.

<span style="color:#0969da"><b>BackendApplication</b></span>
Điểm khởi động Spring Boot, bật component scan & auto-configuration.

📁 src/main/resources/

<span style="color:#8250df"><b>application.properties</b></span> — cấu hình JDBC (Railway), Hikari, JPA (ddl-auto, show-sql), CORS, logging…

<span style="color:#8250df"><b>data.sql</b></span> — seed dữ liệu mẫu phục vụ dev/test.

<span style="color:#8250df"><b>static/</b></span> — tài nguyên tĩnh (ảnh/logo) nếu cần.

<span style="color:#8250df"><b>templates/</b></span> — view server-side (Thymeleaf/Freemarker). API JSON thuần → thường để trống.

📁 Gốc dự án

<span style="color:#1f883d"><b>pom.xml</b></span> — khai báo dependencies & plugin build (Spring Boot, JPA, MySQL, Lombok…).

<span style="color:#1f883d"><b>mvnw</b></span>, <span style="color:#1f883d"><b>mvnw.cmd</b></span> — Maven Wrapper (build không cần cài Maven global).

<span style="color:#1f883d"><b>target/</b></span> — thư mục build output (jar, classes).

<span style="color:#1f883d"><b>test/</b></span> — Unit/Integration tests cho Service/Repository/Controller.

📱 Android (Java)
🔁 Luồng tổng quát
<span style="background:#eef; padding:2px 8px; border-radius:6px;"> Activity/Fragment (UI) → <b>ApiClient / ApiService</b> (Retrofit) → nhận JSON (DTO) → <b>Model UI</b> → hiển thị qua <b>Adapter/RecyclerView</b> </span>
📁 Thư mục chính

<span style="color:#0969da"><b>activity/</b></span>
Các màn hình chính của app: Home, Login, Register, ForgetPassword, Category, Cart, Profile…
Nhiệm vụ: ràng buộc view, điều hướng, gọi API, đổ dữ liệu vào RecyclerView.

<span style="color:#0969da"><b>adapters/</b></span>
RecyclerView Adapter & ViewHolder để bind dữ liệu lên item layout, quản lý click/interaction.
<i>Ví dụ:</i> ProductAdapter, CategoryAdapter.

<span style="color:#0969da"><b>models/</b></span>
<b>Model UI</b> phục vụ hiển thị (có thể khác DTO/Entity). Trường & kiểu dữ liệu tối ưu cho UI (ví dụ giá là chuỗi đã format).
<i>Ví dụ:</i> Product, Category.

<span style="color:#0969da"><b>remote/</b></span>
Lớp gọi mạng:
<b>ApiService</b> (khai báo endpoints GET/POST với Retrofit) & <b>ApiClient</b> (khởi tạo Retrofit, baseUrl, converter, logging).

<span style="color:#0969da"><b>util/</b></span>
Tiện ích dùng chung: format tiền VNĐ, hằng số BASE_URL, helper…

📁 app/src/main/res/

<span style="color:#8250df"><b>layout/</b></span> — XML UI cho màn hình & item list
<i>Ví dụ:</i> activity_home.xml, activity_login.xml, item_product.xml, bottom_navigation.xml, header_layout.xml

<span style="color:#8250df"><b>drawable/</b></span> — ảnh/icon/shape, placeholder, error image

<span style="color:#8250df"><b>values/</b></span> — colors.xml, strings.xml, themes.xml, dimens.xml (chuẩn hoá style & text)

<span style="color:#8250df"><b>manifest/AndroidManifest.xml</b></span> — khai báo Activity, quyền mạng (INTERNET), launcher, intent-filter, theme

⚒️ Gradle (project & module)

<span style="color:#1f883d"><b>build.gradle.kts (root)</b></span> / <span style="color:#1f883d"><b>settings.gradle.kts</b></span> — cấu hình chung project, repositories, modules

<span style="color:#1f883d"><b>app/build.gradle.kts</b></span> — cấu hình module app (compileSdk, minSdk), dependencies Android, plugins

<span style="color:#1f883d"><b>gradle-wrapper.properties</b></span> — phiên bản Gradle dùng để build

✅ Ghi chú quan trọng

DTO ≠ Model UI: DTO phản ánh payload API; Model UI tối ưu hiển thị (format sẵn).

Mapper tập trung: Dễ thay đổi schema & test.

Ảnh sản phẩm: Lưu URL công khai (CDN/S3/Cloudinary/GitHub Releases/Drive public link).

Kết nối emulator: Dùng http://10.0.2.2:8080 khi gọi về backend trên máy dev. Build thật → đổi sang domain/IP server.

CORS: Mở CORS ở backend cho domain/port app khi test thiết bị thật.
