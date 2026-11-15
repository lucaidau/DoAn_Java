
USE HeThongDatLichKhamBenh;
GO
-- -- Bổ sung thông tin hành chính cho bệnh nhân --
IF COL_LENGTH('dbo.benh_nhan', 'nhom_mau') IS NULL ALTER TABLE dbo.benh_nhan ADD nhom_mau NVARCHAR(5) NULL;
IF COL_LENGTH('dbo.benh_nhan', 'so_cccd') IS NULL ALTER TABLE dbo.benh_nhan ADD so_cccd VARCHAR(12) NULL;
IF COL_LENGTH('dbo.benh_nhan', 'ma_bhyt') IS NULL ALTER TABLE dbo.benh_nhan ADD ma_bhyt VARCHAR(30) NULL;
GO
-- -- Danh mục phòng khám --
IF OBJECT_ID('dbo.phong_kham', 'U') IS NULL
CREATE TABLE dbo.phong_kham (id BIGINT IDENTITY(1,1) PRIMARY KEY, ten_phong NVARCHAR(100) NOT NULL, khu_vuc NVARCHAR(200) NULL);
GO
-- -- Danh mục dịch vụ khám --
IF OBJECT_ID('dbo.dich_vu_kham', 'U') IS NULL
CREATE TABLE dbo.dich_vu_kham (id BIGINT IDENTITY(1,1) PRIMARY KEY, ten_dich_vu NVARCHAR(150) NOT NULL, gia_kham DECIMAL(18,2) NULL);
GO
-- -- Liên kết phòng và dịch vụ với từng lịch hẹn --
IF COL_LENGTH('dbo.lich_hen', 'phong_kham_id') IS NULL ALTER TABLE dbo.lich_hen ADD phong_kham_id BIGINT NULL;
IF COL_LENGTH('dbo.lich_hen', 'dich_vu_kham_id') IS NULL ALTER TABLE dbo.lich_hen ADD dich_vu_kham_id BIGINT NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_lichhen_phongkham') ALTER TABLE dbo.lich_hen ADD CONSTRAINT FK_lichhen_phongkham FOREIGN KEY (phong_kham_id) REFERENCES dbo.phong_kham(id);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_lichhen_dichvu') ALTER TABLE dbo.lich_hen ADD CONSTRAINT FK_lichhen_dichvu FOREIGN KEY (dich_vu_kham_id) REFERENCES dbo.dich_vu_kham(id);
GO
-- -- Sinh hiệu ghi nhận theo từng lần khám --
IF OBJECT_ID('dbo.sinh_hieu', 'U') IS NULL
CREATE TABLE dbo.sinh_hieu (id BIGINT IDENTITY(1,1) PRIMARY KEY, lich_hen_id BIGINT NOT NULL UNIQUE, huyet_ap_tam_thu INT NULL, huyet_ap_tam_truong INT NULL, nhip_tim INT NULL, spo2 INT NULL, can_nang DECIMAL(6,2) NULL, chieu_cao DECIMAL(6,2) NULL, thoi_gian_do DATETIME2 NULL, CONSTRAINT FK_sinhhieu_lichhen FOREIGN KEY (lich_hen_id) REFERENCES dbo.lich_hen(id), CONSTRAINT CK_sinhhieu_spo2 CHECK (spo2 BETWEEN 0 AND 100), CONSTRAINT CK_sinhhieu_chieucao CHECK (chieu_cao > 0), CONSTRAINT CK_sinhhieu_cannang CHECK (can_nang > 0));
GO
-- -- Trạng thái thanh toán thực tế theo từng lịch hẹn --
IF OBJECT_ID('dbo.thanh_toan', 'U') IS NULL
CREATE TABLE dbo.thanh_toan (id BIGINT IDENTITY(1,1) PRIMARY KEY, lich_hen_id BIGINT NOT NULL UNIQUE, so_tien DECIMAL(18,2) NOT NULL, trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHUA_THANH_TOAN', phuong_thuc VARCHAR(30) NULL, ma_giao_dich VARCHAR(100) NULL, CONSTRAINT FK_thanhtoan_lichhen FOREIGN KEY (lich_hen_id) REFERENCES dbo.lich_hen(id));
GO
-- -- Lịch sử khám là dữ liệu từ hồ sơ bệnh án đã hoàn thành, không sao chép --
IF OBJECT_ID('dbo.lich_su_kham', 'V') IS NULL EXEC('CREATE VIEW dbo.lich_su_kham AS SELECT h.id AS ho_so_id, l.id AS lich_hen_id, l.benh_nhan_id, l.bac_si_id, l.ngay_kham, h.chan_doan, h.loi_dan_bac_si, h.ngay_tao FROM dbo.ho_so_benh_an h INNER JOIN dbo.lich_hen l ON l.id = h.lich_hen_id WHERE l.trang_thai = ''DA_KHAM''');
GO
-- -- Dữ liệu mẫu danh mục, chỉ thêm khi chưa tồn tại --
IF NOT EXISTS (SELECT 1 FROM dbo.phong_kham WHERE ten_phong = N'Phòng khám Nội 101') INSERT dbo.phong_kham(ten_phong, khu_vuc) VALUES (N'Phòng khám Nội 101', N'Tầng 1 - Khoa Nội tổng quát');
IF NOT EXISTS (SELECT 1 FROM dbo.phong_kham WHERE ten_phong = N'Phòng khám Tim mạch 204') INSERT dbo.phong_kham(ten_phong, khu_vuc) VALUES (N'Phòng khám Tim mạch 204', N'Tầng 2 - Khoa Tim mạch');
IF NOT EXISTS (SELECT 1 FROM dbo.phong_kham WHERE ten_phong = N'Phòng khám Nhi 102') INSERT dbo.phong_kham(ten_phong, khu_vuc) VALUES (N'Phòng khám Nhi 102', N'Tầng 1 - Khoa Nhi');
IF NOT EXISTS (SELECT 1 FROM dbo.dich_vu_kham WHERE ten_dich_vu = N'Khám Nội tổng quát') INSERT dbo.dich_vu_kham(ten_dich_vu, gia_kham) VALUES (N'Khám Nội tổng quát', 150000);
IF NOT EXISTS (SELECT 1 FROM dbo.dich_vu_kham WHERE ten_dich_vu = N'Khám Tim mạch') INSERT dbo.dich_vu_kham(ten_dich_vu, gia_kham) VALUES (N'Khám Tim mạch', 300000);
IF NOT EXISTS (SELECT 1 FROM dbo.dich_vu_kham WHERE ten_dich_vu = N'Khám Nhi') INSERT dbo.dich_vu_kham(ten_dich_vu, gia_kham) VALUES (N'Khám Nhi', 180000);
IF NOT EXISTS (SELECT 1 FROM dbo.dich_vu_kham WHERE ten_dich_vu = N'Khám Tai Mũi Họng') INSERT dbo.dich_vu_kham(ten_dich_vu, gia_kham) VALUES (N'Khám Tai Mũi Họng', 150000);
GO
-- -- Sinh hiệu demo có thể thay đổi; không giả lập dữ liệu cho bệnh nhân chưa đo --
IF NOT EXISTS (SELECT 1 FROM dbo.sinh_hieu WHERE lich_hen_id = 1)
INSERT dbo.sinh_hieu(lich_hen_id,huyet_ap_tam_thu,huyet_ap_tam_truong,nhip_tim,spo2,can_nang,chieu_cao,thoi_gian_do)
SELECT 1, 120, 80, 76, 98, 65.0, 170.0, '2026-06-10T07:50:00' WHERE EXISTS (SELECT 1 FROM dbo.lich_hen WHERE id = 1);
GO

-- -- Bổ sung trường tiếp nhận và hồ sơ khám --
IF COL_LENGTH('dbo.lich_hen', 'ghi_chu_tiep_nhan') IS NULL ALTER TABLE dbo.lich_hen ADD ghi_chu_tiep_nhan NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.benh_nhan', 'tien_su_gia_dinh') IS NULL ALTER TABLE dbo.benh_nhan ADD tien_su_gia_dinh NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.benh_nhan', 'thoi_quen_sinh_hoat') IS NULL ALTER TABLE dbo.benh_nhan ADD thoi_quen_sinh_hoat NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.ho_so_benh_an', 'kham_tim_mach') IS NULL ALTER TABLE dbo.ho_so_benh_an ADD kham_tim_mach NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.ho_so_benh_an', 'kham_ho_hap') IS NULL ALTER TABLE dbo.ho_so_benh_an ADD kham_ho_hap NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.ho_so_benh_an', 'kham_tieu_hoa') IS NULL ALTER TABLE dbo.ho_so_benh_an ADD kham_tieu_hoa NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.ho_so_benh_an', 'chan_doan_phu') IS NULL ALTER TABLE dbo.ho_so_benh_an ADD chan_doan_phu NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.ho_so_benh_an', 'can_lam_sang') IS NULL ALTER TABLE dbo.ho_so_benh_an ADD can_lam_sang NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.ho_so_benh_an', 'huong_dieu_tri') IS NULL ALTER TABLE dbo.ho_so_benh_an ADD huong_dieu_tri NVARCHAR(MAX) NULL;
GO
-- -- Danh mục thuốc chỉ lưu tên và đơn vị, bác sĩ quyết định liều dùng --
IF OBJECT_ID('dbo.thuoc', 'U') IS NULL CREATE TABLE dbo.thuoc (id BIGINT IDENTITY(1,1) PRIMARY KEY, ten_thuoc NVARCHAR(150) NOT NULL UNIQUE, don_vi NVARCHAR(30) NOT NULL);
GO
IF NOT EXISTS (SELECT 1 FROM dbo.thuoc WHERE ten_thuoc = N'Paracetamol 500mg') INSERT dbo.thuoc(ten_thuoc, don_vi) VALUES(N'Paracetamol 500mg', N'Viên');
IF NOT EXISTS (SELECT 1 FROM dbo.thuoc WHERE ten_thuoc = N'Oresol') INSERT dbo.thuoc(ten_thuoc, don_vi) VALUES(N'Oresol', N'Gói');
IF NOT EXISTS (SELECT 1 FROM dbo.thuoc WHERE ten_thuoc = N'Smecta') INSERT dbo.thuoc(ten_thuoc, don_vi) VALUES(N'Smecta', N'Gói');
IF NOT EXISTS (SELECT 1 FROM dbo.thuoc WHERE ten_thuoc = N'Siro ho Prospan') INSERT dbo.thuoc(ten_thuoc, don_vi) VALUES(N'Siro ho Prospan', N'Chai');
GO
-- -- Kiểm tra các bảng cần thiết --
SELECT * FROM dbo.phong_kham;
SELECT * FROM dbo.dich_vu_kham;
SELECT * FROM dbo.thuoc;



-- -- Thêm phòng khám Tai Mũi Họng nếu chưa tồn tại --
IF NOT EXISTS (
    SELECT 1
    FROM dbo.phong_kham
    WHERE ten_phong = N'Phòng khám Tai Mũi Họng 301'
)
BEGIN
    INSERT INTO dbo.phong_kham (ten_phong, khu_vuc)
    VALUES (
        N'Phòng khám Tai Mũi Họng 301',
        N'Tầng 3 - Khoa Tai Mũi Họng'
    );
END;

-- -- Kiểm tra phòng khám vừa thêm --
SELECT *
FROM dbo.phong_kham
WHERE ten_phong = N'Phòng khám Tai Mũi Họng 301';
