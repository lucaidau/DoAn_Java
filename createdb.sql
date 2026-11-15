-- 0. Tạo Database mới
CREATE DATABASE HeThongDatLichKhamBenh;
GO

-- Sử dụng Database vừa tạo
USE HeThongDatLichKhamBenh;
GO

-- 1. Bảng TAI_KHOAN (Quản lý đăng nhập và phân quyền)
CREATE TABLE tai_khoan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ten_dang_nhap VARCHAR(50) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    so_dien_thoai VARCHAR(15),
    vai_tro VARCHAR(20) NOT NULL, -- ADMIN, BAC_SI, BENH_NHAN
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 2. Bảng CHUYEN_KHOA (Quản lý các chuyên khoa y tế)
CREATE TABLE chuyen_khoa (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ten_chuyen_khoa NVARCHAR(100) NOT NULL,
    mo_ta NVARCHAR(MAX)
);
GO

-- 3. Bảng BAC_SI (Thông tin chi tiết bác sĩ)
CREATE TABLE bac_si (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    tai_khoan_id BIGINT UNIQUE NOT NULL,
    chuyen_khoa_id BIGINT NOT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    hoc_ham_hoc_vi NVARCHAR(50), -- Ví dụ: Thạc sĩ, Bác sĩ CKI, PGS.TS
    so_nam_kinh_nghiem INT,
    gioi_thieu NVARCHAR(MAX),
    FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id) ON DELETE CASCADE,
    FOREIGN KEY (chuyen_khoa_id) REFERENCES chuyen_khoa(id) ON DELETE NO ACTION
);
GO

-- 4. Bảng BENH_NHAN (Thông tin hồ sơ y tế bệnh nhân)
CREATE TABLE benh_nhan (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    tai_khoan_id BIGINT NOT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh NVARCHAR(10),
    dia_chi NVARCHAR(255),
    tien_su_benh NVARCHAR(MAX),
    FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id) ON DELETE CASCADE
);
GO

-- 5. Bảng LICH_LAM_VIEC (Quản lý ca trực/khám của bác sĩ)
CREATE TABLE lich_lam_viec (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    bac_si_id BIGINT NOT NULL,
    ngay_lam_viec DATE NOT NULL,
    ten_ca NVARCHAR(20) NOT NULL, -- Ca sáng, Ca chiều
    gio_bat_dau TIME NOT NULL,
    gio_ket_thuc TIME NOT NULL,
    so_luong_toida INT DEFAULT 10,
    FOREIGN KEY (bac_si_id) REFERENCES bac_si(id) ON DELETE CASCADE
);
GO

-- 6. Bảng LICH_HEN (Thông tin bệnh nhân đặt lịch hẹn khám)
CREATE TABLE lich_hen (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    benh_nhan_id BIGINT NOT NULL,
    bac_si_id BIGINT NOT NULL,
    lich_lam_viec_id BIGINT NOT NULL,
    ngay_kham DATE NOT NULL,
    khung_gio_kham VARCHAR(30) NOT NULL, -- Ví dụ: 08:00 - 08:30
    trieu_chung_benh NVARCHAR(MAX),
    trang_thai VARCHAR(30) DEFAULT 'CHO_XAC_NHAN', -- CHO_XAC_NHAN, DA_XAC_NHAN, DA_KHAM, DA_HUY
    ngay_tao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (benh_nhan_id) REFERENCES benh_nhan(id) ON DELETE NO ACTION,
    FOREIGN KEY (bac_si_id) REFERENCES bac_si(id) ON DELETE NO ACTION,
    FOREIGN KEY (lich_lam_viec_id) REFERENCES lich_lam_viec(id) ON DELETE NO ACTION
);
GO

-- 7. Bảng HO_SO_BENH_AN (Kết quả chẩn đoán sau khi bác sĩ khám xong)
CREATE TABLE ho_so_benh_an (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    lich_hen_id BIGINT UNIQUE NOT NULL,
    chan_doan NVARCHAR(MAX) NOT NULL,
    loi_dan_bac_si NVARCHAR(MAX),
    ngay_tao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (lich_hen_id) REFERENCES lich_hen(id) ON DELETE CASCADE
);
GO

-- 8. Bảng DON_THUOC (Chi tiết đơn thuốc được kê)
CREATE TABLE don_thuoc (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ho_so_benh_an_id BIGINT NOT NULL,
    ten_thuoc NVARCHAR(150) NOT NULL,
    lieu_luong NVARCHAR(100) NOT NULL, -- Ví dụ: Sáng 1 viên, tối 1 viên
    so_luong INT NOT NULL,
    huong_dan_su_dung NVARCHAR(255), -- Ví dụ: Uống sau khi ăn no
    FOREIGN KEY (ho_so_benh_an_id) REFERENCES ho_so_benh_an(id) ON DELETE CASCADE
);
GO