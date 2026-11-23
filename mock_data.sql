USE HeThongDatLichKhamBenh;
GO

-- =========================================================
-- 0. DỌN DẸP DỮ LIỆU CŨ & RESET IDENTITY
-- =========================================================
DELETE FROM don_thuoc;
DELETE FROM ho_so_benh_an;
DELETE FROM sinh_hieu;
DELETE FROM lich_hen;
DELETE FROM lich_lam_viec;
DELETE FROM benh_nhan;
DELETE FROM bac_si;
DELETE FROM phong_kham;
DELETE FROM chuyen_khoa;
DELETE FROM tai_khoan;

DBCC CHECKIDENT ('don_thuoc', RESEED, 0);
DBCC CHECKIDENT ('ho_so_benh_an', RESEED, 0);
DBCC CHECKIDENT ('sinh_hieu', RESEED, 0);
DBCC CHECKIDENT ('lich_hen', RESEED, 0);
DBCC CHECKIDENT ('lich_lam_viec', RESEED, 0);
DBCC CHECKIDENT ('benh_nhan', RESEED, 0);
DBCC CHECKIDENT ('bac_si', RESEED, 0);
DBCC CHECKIDENT ('phong_kham', RESEED, 0);
DBCC CHECKIDENT ('chuyen_khoa', RESEED, 0);
DBCC CHECKIDENT ('tai_khoan', RESEED, 0);
GO

-- =========================================================
-- 1. DỮ LIỆU BẢNG TAI_KHOAN (42 tài khoản: 2 Admin, 20 Bác sĩ, 20 Bệnh nhân)
-- =========================================================
SET IDENTITY_INSERT tai_khoan ON;
INSERT INTO tai_khoan (id, ten_dang_nhap, mat_khau, email, so_dien_thoai, vai_tro, trang_thai) VALUES
(1, 'admin01', 'hash', 'admin1@gmail.com', '0901111101', 'ADMIN', 1),
(2, 'admin02', 'hash', 'admin2@gmail.com', '0901111102', 'ADMIN', 1),
-- 20 Tài khoản Bác sĩ (ID 3 -> 22)
(3, 'bs_nguyenvana', 'hash', 'bs.a@gmail.com', '0902222201', 'BAC_SI', 1),
(4, 'bs_tranthib', 'hash', 'bs.b@gmail.com', '0902222202', 'BAC_SI', 1),
(5, 'bs_levanc', 'hash', 'bs.c@gmail.com', '0902222203', 'BAC_SI', 1),
(6, 'bs_phamthid', 'hash', 'bs.d@gmail.com', '0902222204', 'BAC_SI', 1),
(7, 'bs_hoangvane', 'hash', 'bs.e@gmail.com', '0902222205', 'BAC_SI', 1),
(8, 'bs_vuthif', 'hash', 'bs.f@gmail.com', '0902222206', 'BAC_SI', 1),
(9, 'bs_dangvang', 'hash', 'bs.g@gmail.com', '0902222207', 'BAC_SI', 1),
(10, 'bs_buithih', 'hash', 'bs.h@gmail.com', '0902222208', 'BAC_SI', 1),
(11, 'bs_ngovani', 'hash', 'bs.i@gmail.com', '0902222209', 'BAC_SI', 1),
(12, 'bs_duongthik', 'hash', 'bs.k@gmail.com', '0902222210', 'BAC_SI', 1),
(13, 'bs_lyvanl', 'hash', 'bs.l@gmail.com', '0902222211', 'BAC_SI', 1),
(14, 'bs_dothim', 'hash', 'bs.m@gmail.com', '0902222212', 'BAC_SI', 1),
(15, 'bs_hovann', 'hash', 'bs.n@gmail.com', '0902222213', 'BAC_SI', 1),
(16, 'bs_phanthis', 'hash', 'bs.s@gmail.com', '0902222214', 'BAC_SI', 1),
(17, 'bs_trinhvanp', 'hash', 'bs.p@gmail.com', '0902222215', 'BAC_SI', 1),
(18, 'bs_truongthiq', 'hash', 'bs.q@gmail.com', '0902222216', 'BAC_SI', 1),
(19, 'bs_dinhvanr', 'hash', 'bs.r@gmail.com', '0902222217', 'BAC_SI', 1),
(20, 'bs_lamthit', 'hash', 'bs.t@gmail.com', '0902222218', 'BAC_SI', 1),
(21, 'bs_maivanu', 'hash', 'bs.u@gmail.com', '0902222219', 'BAC_SI', 1),
(22, 'bs_caothiv', 'hash', 'bs.v@gmail.com', '0902222220', 'BAC_SI', 1),
-- 20 Tài khoản Bệnh nhân (ID 23 -> 42)
(23, 'bn_nguyenquang1', 'hash', 'bn1@gmail.com', '0903333301', 'BENH_NHAN', 1),
(24, 'bn_lehoa2', 'hash', 'bn2@gmail.com', '0903333302', 'BENH_NHAN', 1),
(25, 'bn_trantuan3', 'hash', 'bn3@gmail.com', '0903333303', 'BENH_NHAN', 1),
(26, 'bn_phamtrang4', 'hash', 'bn4@gmail.com', '0903333304', 'BENH_NHAN', 1),
(27, 'bn_hoangviet5', 'hash', 'bn5@gmail.com', '0903333305', 'BENH_NHAN', 1),
(28, 'bn_vunam6', 'hash', 'bn6@gmail.com', '0903333306', 'BENH_NHAN', 1),
(29, 'bn_domai7', 'hash', 'bn7@gmail.com', '0903333307', 'BENH_NHAN', 1),
(30, 'bn_buihung8', 'hash', 'bn8@gmail.com', '0903333308', 'BENH_NHAN', 1),
(31, 'bn_ngolan9', 'hash', 'bn9@gmail.com', '0903333309', 'BENH_NHAN', 1),
(32, 'bn_duongkien10', 'hash', 'bn10@gmail.com', '0903333310', 'BENH_NHAN', 1),
(33, 'bn_ngovananh11', 'hash', 'bn11@gmail.com', '0903333311', 'BENH_NHAN', 1),
(34, 'bn_lyminh12', 'hash', 'bn12@gmail.com', '0903333312', 'BENH_NHAN', 1),
(35, 'bn_trinhha13', 'hash', 'bn13@gmail.com', '0903333313', 'BENH_NHAN', 1),
(36, 'bn_truongson14', 'hash', 'bn14@gmail.com', '0903333314', 'BENH_NHAN', 1),
(37, 'bn_dinhdung15', 'hash', 'bn15@gmail.com', '0903333315', 'BENH_NHAN', 1),
(38, 'bn_lamngoc16', 'hash', 'bn16@gmail.com', '0903333316', 'BENH_NHAN', 1),
(39, 'bn_maiphuong17', 'hash', 'bn17@gmail.com', '0903333317', 'BENH_NHAN', 1),
(40, 'bn_caotuan18', 'hash', 'bn18@gmail.com', '0903333318', 'BENH_NHAN', 1),
(41, 'bn_phanlinh19', 'hash', 'bn19@gmail.com', '0903333319', 'BENH_NHAN', 1),
(42, 'bn_hothanh20', 'hash', 'bn20@gmail.com', '0903333320', 'BENH_NHAN', 1);
SET IDENTITY_INSERT tai_khoan OFF;
GO

-- =========================================================
-- 2. DỮ LIỆU BẢNG CHUYEN_KHOA (20 chuyên khoa)
-- =========================================================
SET IDENTITY_INSERT chuyen_khoa ON;
INSERT INTO chuyen_khoa (id, ten_chuyen_khoa, mo_ta) VALUES
(1, N'Khoa Nội tổng quát', N'Khám và điều trị các bệnh lý nội khoa phổ biến.'),
(2, N'Khoa Nhi', N'Chuyên khoa chăm sóc sức khỏe trẻ em và sơ sinh.'),
(3, N'Khoa Răng Hàm Mặt', N'Khám, chữa bệnh và thẩm mỹ các vấn đề về răng hàm mặt.'),
(4, N'Khoa Tim mạch', N'Chẩn đoán và điều trị các bệnh lý về tim và mạch máu.'),
(5, N'Khoa Da liễu', N'Điều trị các bệnh về da, tóc, móng và thẩm mỹ da.'),
(6, N'Khoa Mắt', N'Khám và điều trị các bệnh lý về mắt.'),
(7, N'Khoa Tai Mũi Họng', N'Chuyên điều trị các bệnh lý vùng tai, mũi, họng.'),
(8, N'Khoa Cơ Xương Khớp', N'Điều trị các bệnh lý về xương khớp, cột sống.'),
(9, N'Khoa Thần kinh', N'Chẩn đoán và điều trị các bệnh lý hệ thần kinh trung ương và ngoại biên.'),
(10, N'Khoa Tiêu hóa - Gan mật', N'Khám và nội soi điều trị các bệnh đường tiêu hóa, gan mật.'),
(11, N'Khoa Hô hấp', N'Điều trị viêm phổi, hen suyễn, COPD và các bệnh đường hô hấp.'),
(12, N'Khoa Nội tiết - Đái tháo đường', N'Quản lý bệnh tiểu đường, tuyến giáp và rối loạn chuyển hóa.'),
(13, N'Khoa Sản Phụ khoa', N'Chăm sóc sức khỏe sinh sản, thai sản và phụ khoa.'),
(14, N'Khoa Tiết niệu - Nam học', N'Điều trị sỏi thận, viêm đường tiết niệu và nam khoa.'),
(15, N'Khoa Ung bướu', N'Tầm soát, chẩn đoán sớm và tư vấn điều trị ung thư.'),
(16, N'Khoa Dị ứng - Miễn dịch lâm sàng', N'Chẩn đoán và điều trị dị ứng thuốc, thực phẩm, mề đay.'),
(17, N'Khoa Phục hồi chức năng', N'Vật lý trị liệu và phục hồi chức năng sau chấn thương, đột quỵ.'),
(18, N'Khoa Tâm lý - Tâm thần', N'Tư vấn tâm lý, điều trị rối loạn lo âu, trầm cảm, mất ngủ.'),
(19, N'Khoa Dinh dưỡng', N'Tư vấn chế độ dinh dưỡng bệnh lý, béo phì, suy dinh dưỡng.'),
(20, N'Khoa Y học cổ truyền', N'Khám chữa bệnh bằng đông y, châm cứu, bấm huyệt.');
SET IDENTITY_INSERT chuyen_khoa OFF;
GO

-- =========================================================
-- 3. DỮ LIỆU BẢNG PHONG_KHAM (20 phòng khám gắn với 20 chuyên khoa)
-- =========================================================
SET IDENTITY_INSERT phong_kham ON;
INSERT INTO phong_kham (id, ten_phong, khu_vuc, chuyen_khoa_id, trang_thai) VALUES
(1, N'Phòng khám Nội 101', N'Tầng 1 - Khu A', 1, 1),
(2, N'Phòng khám Nhi 102', N'Tầng 1 - Khu A', 2, 1),
(3, N'Phòng khám Răng Hàm Mặt 103', N'Tầng 1 - Khu B', 3, 1),
(4, N'Phòng khám Tim mạch 201', N'Tầng 2 - Khu A', 4, 1),
(5, N'Phòng khám Da liễu 202', N'Tầng 2 - Khu A', 5, 1),
(6, N'Phòng khám Mắt 203', N'Tầng 2 - Khu B', 6, 1),
(7, N'Phòng khám Tai Mũi Họng 204', N'Tầng 2 - Khu B', 7, 1),
(8, N'Phòng khám Cơ Xương Khớp 301', N'Tầng 3 - Khu A', 8, 1),
(9, N'Phòng khám Thần kinh 302', N'Tầng 3 - Khu A', 9, 1),
(10, N'Phòng khám Tiêu hóa 303', N'Tầng 3 - Khu B', 10, 1),
(11, N'Phòng khám Hô hấp 304', N'Tầng 3 - Khu B', 11, 1),
(12, N'Phòng khám Nội tiết 401', N'Tầng 4 - Khu A', 12, 1),
(13, N'Phòng khám Sản Phụ khoa 402', N'Tầng 4 - Khu A', 13, 1),
(14, N'Phòng khám Tiết niệu 403', N'Tầng 4 - Khu B', 14, 1),
(15, N'Phòng khám Ung bướu 404', N'Tầng 4 - Khu B', 15, 1),
(16, N'Phòng khám Dị ứng 501', N'Tầng 5 - Khu A', 16, 1),
(17, N'Phòng khám Phục hồi chức năng 502', N'Tầng 5 - Khu A', 17, 1),
(18, N'Phòng khám Tâm lý 503', N'Tầng 5 - Khu B', 18, 1),
(19, N'Phòng khám Dinh dưỡng 504', N'Tầng 5 - Khu B', 19, 1),
(20, N'Phòng khám Y học cổ truyền 105', N'Tầng 1 - Khu C', 20, 1);
SET IDENTITY_INSERT phong_kham OFF;
GO

-- =========================================================
-- 4. DỮ LIỆU BẢNG BAC_SI (20 bác sĩ tương ứng tài khoản 3 -> 22 và chuyên khoa 1 -> 20)
-- =========================================================
SET IDENTITY_INSERT bac_si ON;
INSERT INTO bac_si (id, tai_khoan_id, chuyen_khoa_id, ho_ten, hoc_ham_hoc_vi, so_nam_kinh_nghiem, gioi_thieu, trang_thai) VALUES
(1, 3, 1, N'Nguyễn Văn A', N'Thạc sĩ, Bác sĩ', 10, N'Chuyên gia Nội tổng quát với hơn 10 năm kinh nghiệm.', 1),
(2, 4, 2, N'Trần Thị B', N'Bác sĩ CKI', 8, N'Tận tâm, yêu trẻ em, nhiều năm kinh nghiệm tại Nhi đồng.', 1),
(3, 5, 3, N'Lê Văn C', N'Bác sĩ', 5, N'Chuyên gia Răng Hàm Mặt thẩm mỹ.', 1),
(4, 6, 4, N'Phạm Thị D', N'PGS.TS, Bác sĩ', 20, N'Trưởng khoa Tim mạch, chuyên gia đầu ngành.', 1),
(5, 7, 5, N'Hoàng Văn E', N'Thạc sĩ, Bác sĩ', 7, N'Chuyên khoa Da liễu thẩm mỹ và điều trị bệnh lý da.', 1),
(6, 8, 6, N'Vũ Thị F', N'Bác sĩ CKI', 12, N'Chuyên khám và phẫu thuật các bệnh về mắt.', 1),
(7, 9, 7, N'Đặng Văn G', N'Bác sĩ', 6, N'Chuyên khoa Tai Mũi Họng người lớn và trẻ em.', 1),
(8, 10, 8, N'Bùi Thị H', N'Thạc sĩ, Bác sĩ', 9, N'Chuyên điều trị cơ xương khớp và phục hồi chức năng.', 1),
(9, 11, 9, N'Ngô Văn I', N'Tiến sĩ, Bác sĩ', 15, N'Chuyên gia điều trị đau đầu, mất ngủ, tai biến mạch máu não.', 1),
(10, 12, 10, N'Dương Thị K', N'Bác sĩ CKII', 14, N'Chuyên nội soi tiêu hóa và điều trị bệnh lý gan mật.', 1),
(11, 13, 11, N'Lý Văn L', N'Thạc sĩ, Bác sĩ', 11, N'Chuyên điều trị hen phế quản và viêm phổi tắc nghẽn.', 1),
(12, 14, 12, N'Đỗ Thị M', N'Bác sĩ CKI', 9, N'Chuyên quản lý bệnh đái tháo đường và bệnh tuyến giáp.', 1),
(13, 15, 13, N'Hồ Văn N', N'Bác sĩ CKII', 16, N'Chuyên gia sản phụ khoa và siêu âm chẩn đoán trước sinh.', 1),
(14, 16, 14, N'Phan Thị S', N'Thạc sĩ, Bác sĩ', 8, N'Chuyên điều trị sỏi tiết niệu và bệnh lý thận.', 1),
(15, 17, 15, N'Trịnh Văn P', N'PGS.TS, Bác sĩ', 22, N'Chuyên gia đầu ngành về tầm soát và điều trị ung bướu.', 1),
(16, 18, 16, N'Trương Thị Q', N'Bác sĩ CKI', 7, N'Chuyên điều trị các bệnh dị ứng cơ địa, hen dị ứng.', 1),
(17, 19, 17, N'Đinh Văn R', N'Thạc sĩ, Bác sĩ', 10, N'Chuyên phục hồi chức năng vận động sau chấn thương.', 1),
(18, 20, 18, N'Lâm Thị T', N'Tiến sĩ, Bác sĩ', 13, N'Chuyên gia trị liệu tâm lý và rối loạn giấc ngủ.', 1),
(19, 21, 19, N'Mai Văn U', N'Thạc sĩ, Bác sĩ', 6, N'Chuyên tư vấn dinh dưỡng lâm sàng cho trẻ em và người lớn.', 1),
(20, 22, 20, N'Cao Thị V', N'Bác sĩ CKII', 18, N'Chuyên châm cứu, cấy chỉ và điều trị bằng y học cổ truyền.', 1);
SET IDENTITY_INSERT bac_si OFF;
GO

-- =========================================================
-- 5. DỮ LIỆU BẢNG BENH_NHAN (20 bệnh nhân ứng với tài khoản ID 23 -> 42)
-- =========================================================
SET IDENTITY_INSERT benh_nhan ON;
INSERT INTO benh_nhan (id, tai_khoan_id, ho_ten, ngay_sinh, gioi_tinh, dia_chi, tien_su_benh, nhom_mau, so_cccd, ma_bhyt, tien_su_gia_dinh, thoi_quen_sinh_hoat) VALUES
(1, 23, N'Nguyễn Văn Quân', '1995-05-12', N'Nam', N'Hà Nội', N'Không có', N'O+', '001095000001', 'GD4010000000001', N'Không có tiền sử bệnh di truyền', N'Ngủ đủ 7 tiếng, ít vận động'),
(2, 24, N'Lê Thị Hoa', '1998-10-20', N'Nữ', N'Hồ Chí Minh', N'Dị ứng hải sản nhẹ', N'A+', '079198000002', 'GD4790000000002', N'Không có', N'Ăn uống điều độ, tập thể dục 3 lần/tuần'),
(3, 25, N'Trần Minh Tuấn', '1990-03-15', N'Nam', N'Đà Nẵng', N'Viêm dạ dày cấp', N'B+', '048090000003', 'GD4480000000003', N'Bố có tiền sử dạ dày', N'Hay uống cà phê buổi sáng'),
(4, 26, N'Phạm Thu Trang', '2000-12-01', N'Nữ', N'Hải Phòng', N'Không', N'AB+', '031300000004', 'GD4310000000004', N'Không có', N'Sinh hoạt điều độ'),
(5, 27, N'Hoàng Quốc Việt', '1985-07-22', N'Nam', N'Cần Thơ', N'Huyết áp cao', N'O-', '092085000005', 'GD4920000000005', N'Cha mẹ tăng huyết áp', N'Ăn mặn, ít tập thể dục'),
(6, 28, N'Vũ Đình Nam', '1992-09-09', N'Nam', N'Nam Định', N'Không', N'A-', '036092000006', 'GD4360000000006', N'Không có', N'Chạy bộ mỗi sáng'),
(7, 29, N'Đỗ Thị Mai', '1997-04-18', N'Nữ', N'Nghệ An', N'Hen suyễn nhẹ', N'B-', '040197000007', 'GD4400000000007', N'Không có', N'Nhạy cảm khi thời tiết thay đổi'),
(8, 30, N'Bùi Văn Hùng', '1988-11-30', N'Nam', N'Thanh Hóa', N'Không', N'O+', '038088000008', 'GD4380000000008', N'Không có', N'Ngồi làm việc nhiều giờ'),
(9, 31, N'Ngô Thị Lan', '2001-02-14', N'Nữ', N'Bình Dương', N'Không', N'A+', '074301000009', 'GD4740000000009', N'Không có', N'Ngủ muộn, dùng điện thoại nhiều'),
(10, 32, N'Dương Trung Kiên', '1994-06-25', N'Nam', N'Quảng Ninh', N'Đau cột sống', N'B+', '022094000010', 'GD4220000000010', N'Không có', N'Thường xuyên khuân vác'),
(11, 33, N'Ngô Vân Anh', '2006-12-01', N'Nữ', N'Kiên Giang', N'Không có', N'B+', '091306000011', 'GD4910000000011', N'Không có', N'Sinh hoạt bình thường'),
(12, 34, N'Lý Quang Minh', '1982-08-19', N'Nam', N'Đồng Nai', N'Tiểu đường tuýp 2', N'O+', '075082000012', 'GD4750000000012', N'Có người thân đái tháo đường', N'Kiêng đường, đi bộ 30 phút/ngày'),
(13, 35, N'Trịnh Thu Hà', '1996-01-25', N'Nữ', N'Huế', N'Viêm mũi dị ứng', N'A+', '046196000013', 'GD4460000000013', N'Không có', N'Dùng máy lạnh thường xuyên'),
(14, 36, N'Trương Thái Sơn', '1979-04-10', N'Nam', N'Khánh Hòa', N'Rối loạn mỡ máu', N'AB-', '056079000014', 'GD4560000000014', N'Không có', N'Ăn nhiều đồ chiên rán'),
(15, 37, N'Đinh Tiến Dũng', '1999-09-02', N'Nam', N'Bắc Ninh', N'Không', N'O+', '027099000015', 'GD4270000000015', N'Không có', N'Tập gym đều đặn'),
(16, 38, N'Lâm Bảo Ngọc', '2003-07-17', N'Nữ', N'Long An', N'Cận thị nặng', N'B+', '080303000016', 'GD4800000000016', N'Không có', N'Học tập và làm việc trên máy tính nhiều'),
(17, 39, N'Mai Lan Phương', '1991-11-11', N'Nữ', N'Vũng Tàu', N'Thiếu máu nhẹ', N'A+', '077191000017', 'GD4770000000017', N'Không có', N'Hay chóng mặt khi đứng dậy nhanh'),
(18, 40, N'Cao Anh Tuấn', '1987-03-28', N'Nam', N'Thái Nguyên', N'Gout nhẹ', N'O-', '019087000018', 'GD4190000000018', N'Không có', N'Hạn chế bia rượu và nội tạng'),
(19, 41, N'Phan Thùy Linh', '1993-05-06', N'Nữ', N'Quảng Nam', N'Không', N'B+', '049193000019', 'GD4490000000019', N'Không có', N'Ăn uống đầy đủ, tập yoga'),
(20, 42, N'Hồ Đức Thành', '1975-12-20', N'Nam', N'Lâm Đồng', N'Thoái hóa đốt sống cổ', N'A+', '068075000020', 'GD4680000000020', N'Không có', N'Ngồi làm việc lâu, ít giãn cơ');
SET IDENTITY_INSERT benh_nhan OFF;
GO

-- =========================================================
-- 6. DỮ LIỆU BẢNG LICH_LAM_VIEC (Ca sáng 07:30-11:30, Ca chiều 13:30-17:00)
-- =========================================================
SET IDENTITY_INSERT lich_lam_viec ON;
INSERT INTO lich_lam_viec (id, bac_si_id, phong_kham_id, ngay_lam_viec, ten_ca, gio_bat_dau, gio_ket_thuc, so_luong_toida) VALUES
(1,  1,  1,  '2026-11-23', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(2,  2,  2,  '2026-11-23', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(3,  3,  3,  '2026-11-23', N'Ca chiều', '13:30:00', '17:00:00', 8),
(4,  4,  4,  '2026-11-23', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(5,  5,  5,  '2026-11-23', N'Ca chiều', '13:30:00', '17:00:00', 10),
(6,  6,  6,  '2026-11-24', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(7,  7,  7,  '2026-11-24', N'Ca sáng',  '07:30:00', '11:30:00', 12),
(8,  8,  8,  '2026-11-24', N'Ca chiều', '13:30:00', '17:00:00', 12),
(9,  9,  9,  '2026-11-24', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(10, 10, 10, '2026-11-24', N'Ca chiều', '13:30:00', '17:00:00', 10),
(11, 11, 11, '2026-11-25', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(12, 12, 12, '2026-11-25', N'Ca sáng',  '07:30:00', '11:30:00', 8),
(13, 13, 13, '2026-11-25', N'Ca chiều', '13:30:00', '17:00:00', 10),
(14, 14, 14, '2026-11-25', N'Ca chiều', '13:30:00', '17:00:00', 10),
(15, 15, 15, '2026-11-26', N'Ca sáng',  '07:30:00', '11:30:00', 6),
(16, 16, 16, '2026-11-26', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(17, 17, 17, '2026-11-26', N'Ca chiều', '13:30:00', '17:00:00', 10),
(18, 18, 18, '2026-11-26', N'Ca chiều', '13:30:00', '17:00:00', 8),
(19, 19, 19, '2026-11-27', N'Ca sáng',  '07:30:00', '11:30:00', 10),
(20, 20, 20, '2026-11-27', N'Ca chiều', '13:30:00', '17:00:00', 10),
(21, 1,  1,  '2026-11-27', N'Ca chiều', '13:30:00', '17:00:00', 10),
(22, 4,  4,  '2026-11-27', N'Ca sáng',  '07:30:00', '11:30:00', 10);
SET IDENTITY_INSERT lich_lam_viec OFF;
GO

-- =========================================================
-- 7. DỮ LIỆU BẢNG LICH_HEN (Đúng cột theo file tạo DB)
-- =========================================================
SET IDENTITY_INSERT lich_hen ON;
INSERT INTO lich_hen (id, benh_nhan_id, lich_lam_viec_id, ngay_kham, khung_gio_kham, trieu_chung_benh, trang_thai, ngay_tao) VALUES
(1,  1,  1,  '2026-11-23', '07:30 - 07:50', N'Đau đầu, chóng mặt kéo dài', 'DA_KHAM', '2026-11-20 09:00:00'),
(2,  2,  2,  '2026-11-23', '07:50 - 08:10', N'Bé sốt cao và ho khan', 'DA_KHAM', '2026-11-20 09:15:00'),
(3,  3,  3,  '2026-11-23', '13:50 - 14:10', N'Đau nhức răng hàm số 8', 'DA_KHAM', '2026-11-20 10:00:00'),
(4,  4,  4,  '2026-11-23', '08:10 - 08:30', N'Đau tức ngực nhẹ khi vận động', 'DA_KHAM', '2026-11-20 10:30:00'),
(5,  5,  5,  '2026-11-23', '14:10 - 14:30', N'Nổi mẩn đỏ ngứa vùng tay chân', 'DA_KHAM', '2026-11-20 11:00:00'),
(6,  6,  6,  '2026-11-24', '07:30 - 07:50', N'Mờ mắt, mỏi mắt khi nhìn máy tính', 'DA_KHAM', '2026-11-21 08:30:00'),
(7,  7,  7,  '2026-11-24', '07:50 - 08:10', N'Đau rát họng, khản tiếng', 'DA_KHAM', '2026-11-21 09:00:00'),
(8,  8,  8,  '2026-11-24', '13:30 - 13:50', N'Đau mỏi vùng thắt lưng', 'DA_KHAM', '2026-11-21 09:45:00'),
(9,  9,  9,  '2026-11-24', '08:10 - 08:30', N'Mất ngủ kéo dài, đau nửa đầu', 'DA_KHAM', '2026-11-21 10:20:00'),
(10, 10, 10, '2026-11-24', '14:30 - 14:50', N'Đau thượng vị, ợ chua sau ăn', 'DA_KHAM', '2026-11-21 11:10:00'),
(11, 11, 11, '2026-11-25', '07:30 - 07:50', N'Khó thở nhẹ khi thay đổi thời tiết', 'DA_KHAM', '2026-11-21 14:00:00'),
(12, 12, 12, '2026-11-25', '07:50 - 08:10', N'Kiểm tra đường huyết định kỳ', 'DA_KHAM', '2026-11-21 14:30:00'),
(13, 13, 13, '2026-11-25', '13:30 - 13:50', N'Khám thai định kỳ tuần 12', 'DA_KHAM', '2026-11-21 15:00:00'),
(14, 14, 14, '2026-11-25', '13:50 - 14:10', N'Tiểu buốt, đau âm ỉ vùng hông lưng', 'DA_KHAM', '2026-11-21 15:30:00'),
(15, 15, 15, '2026-11-26', '07:50 - 08:10', N'Tầm soát tuyến giáp định kỳ', 'DA_KHAM', '2026-11-22 08:00:00'),
(16, 16, 16, '2026-11-26', '08:10 - 08:30', N'Nổi mề đay toàn thân sau ăn hải sản', 'DA_KHAM', '2026-11-22 08:40:00'),
(17, 17, 17, '2026-11-26', '13:30 - 13:50', N'Tập vật lý trị liệu khớp gối sau chấn thương', 'DA_KHAM', '2026-11-22 09:20:00'),
(18, 18, 18, '2026-11-26', '14:30 - 14:50', N'Căng thẳng stress công việc, lo âu', 'DA_KHAM', '2026-11-22 10:00:00'),
(19, 19, 19, '2026-11-27', '07:50 - 08:10', N'Tư vấn thực đơn tăng cân lành mạnh', 'DA_KHAM', '2026-11-22 10:45:00'),
(20, 20, 20, '2026-11-27', '13:30 - 13:50', N'Đau mỏi vai gáy mãn tính, châm cứu', 'DA_KHAM', '2026-11-22 11:30:00');
SET IDENTITY_INSERT lich_hen OFF;
GO

-- =========================================================
-- 8. DỮ LIỆU BẢNG SINH_HIEU (20 bản ghi sinh hiệu cho 20 lịch hẹn)
-- =========================================================
SET IDENTITY_INSERT sinh_hieu ON;
INSERT INTO sinh_hieu (id, lich_hen_id, huyet_ap_tam_thu, huyet_ap_tam_truong, nhip_tim, spo2, can_nang, chieu_cao, thoi_gian_do) VALUES
(1,  1,  115, 75, 76, 98, 65.5, 172.0, '2026-11-23 07:20:00'),
(2,  2,  100, 65, 95, 97, 22.0, 118.0, '2026-11-23 07:40:00'),
(3,  3,  120, 80, 78, 99, 68.0, 170.0, '2026-11-23 13:40:00'),
(4,  4,  135, 85, 88, 98, 52.0, 160.0, '2026-11-23 08:00:00'),
(5,  5,  140, 90, 82, 98, 74.5, 168.0, '2026-11-23 14:00:00'),
(6,  6,  118, 76, 74, 99, 64.0, 171.0, '2026-11-24 07:20:00'),
(7,  7,  110, 70, 80, 98, 50.5, 158.0, '2026-11-24 07:40:00'),
(8,  8,  125, 82, 75, 99, 70.0, 175.0, '2026-11-24 13:20:00'),
(9,  9,  112, 72, 79, 98, 48.0, 156.0, '2026-11-24 08:00:00'),
(10, 10, 122, 80, 77, 99, 66.0, 169.0, '2026-11-24 14:20:00'),
(11, 11, 115, 75, 84, 96, 49.5, 162.0, '2026-11-25 07:20:00'),
(12, 12, 138, 88, 80, 98, 76.0, 167.0, '2026-11-25 07:40:00'),
(13, 13, 110, 70, 82, 99, 54.0, 161.0, '2026-11-25 13:20:00'),
(14, 14, 130, 85, 78, 98, 72.0, 173.0, '2026-11-25 13:40:00'),
(15, 15, 120, 78, 74, 99, 63.5, 170.0, '2026-11-26 07:40:00'),
(16, 16, 114, 74, 86, 98, 47.5, 159.0, '2026-11-26 08:00:00'),
(17, 17, 118, 76, 75, 99, 53.0, 163.0, '2026-11-26 13:20:00'),
(18, 18, 128, 84, 85, 98, 69.0, 171.0, '2026-11-26 14:20:00'),
(19, 19, 108, 68, 72, 99, 44.0, 157.0, '2026-11-27 07:40:00'),
(20, 20, 132, 86, 76, 98, 67.5, 166.0, '2026-11-27 13:20:00');
SET IDENTITY_INSERT sinh_hieu OFF;
GO

-- =========================================================
-- 9. DỮ LIỆU BẢNG HO_SO_BENH_AN (20 hồ sơ bệnh án cho 20 lịch hẹn)
-- =========================================================
SET IDENTITY_INSERT ho_so_benh_an ON;
INSERT INTO ho_so_benh_an (id, lich_hen_id, chan_doan, loi_dan_bac_si, ngay_tao) VALUES
(1,  1,  N'Thiếu máu nhẹ, suy nhược thần kinh tạm thời', N'Nghỉ ngơi nhiều, ăn uống đủ chất, uống thuốc theo đơn.', '2026-11-23 08:00:00'),
(2,  2,  N'Viêm đường hô hấp trên ở trẻ em', N'Cho bé uống thuốc đúng giờ, giữ ấm cổ, tái khám nếu sốt cao.', '2026-11-23 08:20:00'),
(3,  3,  N'Viêm quanh thân răng khôn hàm dưới trái', N'Vệ sinh răng miệng sạch, uống kháng sinh và giảm đau đủ liều.', '2026-11-23 14:20:00'),
(4,  4,  N'Rối loạn nhịp tim nhẹ do căng thẳng', N'Hạn chế cà phê, thức khuya, tái khám đo điện tim sau 2 tuần.', '2026-11-23 08:40:00'),
(5,  5,  N'Viêm da tiếp xúc dị ứng', N'Tránh tiếp xúc hóa chất tẩy rửa, bôi thuốc đều đặn.', '2026-11-23 14:40:00'),
(6,  6,  N'Khô mắt và điều tiết kém do dùng thiết bị điện tử', N'Nhỏ nước mắt nhân tạo, cho mắt nghỉ ngơi mỗi 30 phút.', '2026-11-24 08:00:00'),
(7,  7,  N'Viêm họng hạt cấp tính', N'Súc họng nước muối sinh lý, kiêng nước đá lạnh.', '2026-11-24 08:20:00'),
(8,  8,  N'Căng cơ thắt lưng cấp', N'Tránh mang vác nặng, chườm ấm vùng lưng mỗi tối.', '2026-11-24 14:00:00'),
(9,  9,  N'Đau đầu vận mạch (Migraine)', N'Ngủ đủ giấc, tránh căng thẳng, uống thuốc khi có cơn đau.', '2026-11-24 08:40:00'),
(10, 10, N'Trào ngược dạ dày thực quản (GERD)', N'Chia nhỏ bữa ăn, không nằm ngay sau khi ăn, kiêng đồ chua cay.', '2026-11-24 15:00:00'),
(11, 11, N'Viêm phế quản nhẹ', N'Uống nhiều nước ấm, giữ ấm ngực khi trời lạnh.', '2026-11-25 08:00:00'),
(12, 12, N'Đái tháo đường tuýp 2 ổn định', N'Duy trì chế độ ăn giảm tinh bột, tập thể dục 30 phút/ngày.', '2026-11-25 08:20:00'),
(13, 13, N'Thai 12 tuần phát triển bình thường', N'Bổ sung sắt và axit folic đầy đủ, hẹn siêu âm độ mờ da gáy.', '2026-11-25 14:00:00'),
(14, 14, N'Nhiễm khuẩn đường tiết niệu nhẹ', N'Uống đủ 2-2.5 lít nước mỗi ngày, không nhịn tiểu.', '2026-11-25 14:20:00'),
(15, 15, N'Nhân giáp lành tính kích thước nhỏ', N'Theo dõi định kỳ mỗi 6 tháng, chưa cần can thiệp phẫu thuật.', '2026-11-26 08:20:00'),
(16, 16, N'Mề đay cấp tính do dị ứng thực phẩm', N'Kiêng hải sản và thực phẩm lạ trong 2 tuần tới.', '2026-11-26 08:40:00'),
(17, 17, N'Phục hồi chức năng dây chằng chéo trước sau mổ', N'Tập các bài co duỗi nhẹ nhàng theo hướng dẫn của kỹ thuật viên.', '2026-11-26 14:00:00'),
(18, 18, N'Rối loạn lo âu lan tỏa mức độ nhẹ', N'Tập thiền, hít thở sâu, giảm tải áp lực công việc.', '2026-11-26 15:00:00'),
(19, 19, N'Thiếu cân nhẹ (BMI 17.8)', N'Bổ sung thêm bữa phụ giàu đạm và tinh bột tốt.', '2026-11-27 08:20:00'),
(20, 20, N'Hội chứng cổ vai gáy do thoái hóa', N'Châm cứu 10 buổi, hạn chế cúi đầu lâu.', '2026-11-27 14:00:00');
SET IDENTITY_INSERT ho_so_benh_an OFF;
GO

-- =========================================================
-- 10. DỮ LIỆU BẢNG DON_THUOC (20 dòng đơn thuốc gắn với 20 hồ sơ bệnh án)
-- =========================================================
SET IDENTITY_INSERT don_thuoc ON;
INSERT INTO don_thuoc (id, ho_so_benh_an_id, ten_thuoc, lieu_luong, so_luong, huong_dan_su_dung) VALUES
(1,  1,  N'Paracetamol 500mg', N'Sáng 1 viên, Tối 1 viên', 10, N'Uống khi đau đầu'),
(2,  2,  N'Hapacol 250mg', N'Uống khi sốt > 38.5 độ', 6, N'Cách nhau ít nhất 4-6 tiếng'),
(3,  3,  N'Augmentin 625mg', N'Sáng 1 viên, Tối 1 viên', 14, N'Uống ngay sau khi ăn'),
(4,  4,  N'Magne B6', N'Sáng 1 viên, Chiều 1 viên', 20, N'Uống sau ăn'),
(5,  5,  N'Loratadin 10mg', N'Tối 1 viên', 10, N'Uống trước khi đi ngủ'),
(6,  6,  N'Nước mắt nhân tạo Systane', N'Nhỏ 1-2 giọt/lần x 4 lần/ngày', 1, N'Nhỏ khi mỏi mắt'),
(7,  7,  N'Alpha Choay', N'Sáng 2 viên, Chiều 2 viên', 20, N'Ngậm dưới lưỡi'),
(8,  8,  N'Meloxicam 7.5mg', N'Sáng 1 viên, Tối 1 viên', 10, N'Uống sau khi ăn no'),
(9,  9,  N'Flunarizin 5mg', N'Tối 1 viên', 15, N'Uống trước khi ngủ'),
(10, 10, N'Omeprazol 20mg', N'Sáng 1 viên', 14, N'Uống trước ăn sáng 30 phút'),
(11, 11, N'Acetylcystein 200mg', N'Sáng 1 gói, Chiều 1 gói', 14, N'Pha với nước ấm uống sau ăn'),
(12, 12, N'Metformin 500mg', N'Sáng 1 viên, Tối 1 viên', 30, N'Uống trong hoặc ngay sau bữa ăn'),
(13, 13, N'Obimin Plus', N'Sáng 1 viên', 30, N'Uống sau ăn sáng'),
(14, 14, N'Ciprofloxacin 500mg', N'Sáng 1 viên, Tối 1 viên', 10, N'Uống nhiều nước'),
(15, 15, N'Selen hữu cơ 100mcg', N'Sáng 1 viên', 30, N'Uống sau ăn'),
(16, 16, N'Fexofenadin 180mg', N'Sáng 1 viên', 10, N'Uống với nước lọc'),
(17, 17, N'Glucosamin 1500mg', N'Sáng 1 viên', 30, N'Uống sau bữa ăn'),
(18, 18, N'Rotunda 30mg', N'Tối 2 viên', 20, N'Uống trước khi đi ngủ 30 phút'),
(19, 19, N'Sữa dinh dưỡng Ensure Gold', N'2 ly/ngày', 1, N'Pha 6 muỗng với 195ml nước ấm'),
(20, 20, N'Hoạt huyết nhất nhất', N'Sáng 2 viên, Tối 2 viên', 30, N'Uống sau ăn');
SET IDENTITY_INSERT don_thuoc OFF;
GO