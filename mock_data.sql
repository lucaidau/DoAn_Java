USE HeThongDatLichKhamBenh;
GO

-- =========================================================
-- 1. DỮ LIỆU BẢNG TAI_KHOAN (Tạo 20 tài khoản mẫu)
-- =========================================================
SET IDENTITY_INSERT tai_khoan ON;
INSERT INTO tai_khoan (id, ten_dang_nhap, mat_khau, email, so_dien_thoai, vai_tro, trang_thai) VALUES
(1, 'admin01', 'hash', 'admin1@gmail.com', '0901111111', 'ADMIN', 1),
(2, 'admin02', 'hash', 'admin2@gmail.com', '0901111122', 'ADMIN', 1),
(3, 'bs_nguyenvanA', 'hash', 'bs.a@gmail.com', '0902222201', 'BAC_SI', 1),
(4, 'bs_tranthiB', 'hash', 'bs.b@gmail.com', '0902222202', 'BAC_SI', 1),
(5, 'bs_levanC', 'hash', 'bs.c@gmail.com', '0902222203', 'BAC_SI', 1),
(6, 'bs_phamthid', 'hash', 'bs.d@gmail.com', '0902222204', 'BAC_SI', 1),
(7, 'bs_hoangvano', 'hash', 'bs.e@gmail.com', '0902222205', 'BAC_SI', 1),
(8, 'bs_vuthip', 'hash', 'bs.f@gmail.com', '0902222206', 'BAC_SI', 1),
(9, 'bs_dangvanc', 'hash', 'bs.g@gmail.com', '0902222207', 'BAC_SI', 1),
(10, 'bs_buitih', 'hash', 'bs.h@gmail.com', '0902222208', 'BAC_SI', 1),
(11, 'bn_nguoigia1', 'hash', 'bn1@gmail.com', '0903333301', 'BENH_NHAN', 1),
(12, 'bn_nguoigia2', 'hash', 'bn2@gmail.com', '0903333302', 'BENH_NHAN', 1),
(13, 'bn_nguoigia3', 'hash', 'bn3@gmail.com', '0903333303', 'BENH_NHAN', 1),
(14, 'bn_nguoigia4', 'hash', 'bn4@gmail.com', '0903333304', 'BENH_NHAN', 1),
(15, 'bn_nguoigia5', 'hash', 'bn5@gmail.com', '0903333305', 'BENH_NHAN', 1),
(16, 'bn_nguoigia6', 'hash', 'bn6@gmail.com', '0903333306', 'BENH_NHAN', 1),
(17, 'bn_nguoigia7', 'hash', 'bn7@gmail.com', '0903333307', 'BENH_NHAN', 1),
(18, 'bn_nguoigia8', 'hash', 'bn8@gmail.com', '0903333308', 'BENH_NHAN', 1),
(19, 'bn_nguoigia9', 'hash', 'bn9@gmail.com', '0903333309', 'BENH_NHAN', 1),
(20, 'bn_nguoigia10', 'hash', 'bn10@gmail.com', '0903333310', 'BENH_NHAN', 1);

--INSERT INTO tai_khoan (id, ten_dang_nhap, mat_khau, email, so_dien_thoai, vai_tro, trang_thai) VALUES
--(21, 'bn_nguoigia11', 'hash', 'bn11@gmail.com', '0903333311', 'BENH_NHAN', 1);
SET IDENTITY_INSERT tai_khoan OFF;
GO

-- =========================================================
-- 2. DỮ LIỆU BẢNG CHUYEN_KHOA (Tạo các chuyên khoa phổ biến)
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
(8, N'Khoa Cơ Xương Khớp', N'Điều trị các bệnh lý về xương khớp, cột sống.');
SET IDENTITY_INSERT chuyen_khoa OFF;
GO

-- =========================================================
-- 3. DỮ LIỆU BẢNG BAC_SI (Tạo 8 bác sĩ tương ứng với các tài khoản ID 3-10)
-- =========================================================
SET IDENTITY_INSERT bac_si ON;
INSERT INTO bac_si (id, tai_khoan_id, chuyen_khoa_id, ho_ten, hoc_ham_hoc_vi, so_nam_kinh_nghiem, gioi_thieu) VALUES
(1, 3, 1, N'Nguyễn Văn A', N'Thạc sĩ, Bác sĩ', 10, N'Chuyên gia Nội tổng quát với hơn 10 năm kinh nghiệm.'),
(2, 4, 2, N'Trần Thị B', N'Bác sĩ CKI', 8, N'Tận tâm, yêu trẻ em, nhiều năm kinh nghiệm tại Nhi đồng.'),
(3, 5, 3, N'Lê Văn C', N'Bác sĩ', 5, N'Chuyên gia Răng Hàm Mặt thẩm mỹ.'),
(4, 6, 4, N'Phạm Thị D', N'PGS.TS, Bác sĩ', 20, N'Trưởng khoa Tim mạch, chuyên gia đầu ngành.'),
(5, 7, 5, N'Hoàng Văn E', N'Thạc sĩ, Bác sĩ', 7, N'Chuyên khoa Da liễu thẩm mỹ và điều trị bệnh lý da.'),
(6, 8, 6, N'Vũ Thị F', N'Bác sĩ CKI', 12, N'Chuyên khám và phẫu thuật các bệnh về mắt.'),
(7, 9, 7, N'Đặng Văn G', N'Bác sĩ', 6, N'Chuyên khoa Tai Mũi Họng người lớn và trẻ em.'),
(8, 10, 8, N'Bùi Thị H', N'Thạc sĩ, Bác sĩ', 9, N'Chuyên điều trị cơ xương khớp và phục hồi chức năng.');
SET IDENTITY_INSERT bac_si OFF;
GO

-- =========================================================
-- 4. DỮ LIỆU BẢNG BENH_NHAN (Tạo 10 bệnh nhân ứng với tài khoản ID 11-20)
-- =========================================================
SET IDENTITY_INSERT benh_nhan ON;
INSERT INTO benh_nhan (id, tai_khoan_id, ho_ten, ngay_sinh, gioi_tinh, dia_chi, tien_su_benh) VALUES
(1, 11, N'Nguyễn Văn Quân', '1995-05-12', N'Nam', N'Hà Nội', N'Không có'),
(2, 12, N'Lê Thị Hoa', '1998-10-20', N'Nữ', N'Hồ Chí Minh', N'Dị ứng hải sản nhẹ'),
(3, 13, N'Trần Minh Tuấn', '1990-03-15', N'Nam', N'Đà Nẵng', N'Viêm dạ dày cấp'),
(4, 14, N'Phạm Thu Trang', '2000-12-01', N'Nữ', N'Hải Phòng', N'Không'),
(5, 15, N'Hoàng Quốc Việt', '1985-07-22', N'Nam', N'Cần Thơ', N'Huyết áp cao'),
(6, 16, N'Vũ Đình Nam', '1992-09-09', N'Nam', N'Nam Định', N'Không'),
(7, 17, N'Đỗ Thị Mai', '1997-04-18', N'Nữ', N'Nghệ An', N'Hen suyễn nhẹ'),
(8, 18, N'Bùi Văn Hùng', '1988-11-30', N'Nam', N'Thanh Hóa', N'Không'),
(9, 19, N'Ngô Thị Lan', '2001-02-14', N'Nữ', N'Bình Dương', N'Không'),
(10, 20, N'Dương Trung Kiên', '1994-06-25', N'Nam', N'Quảng Ninh', N'Đau cột sống');
--INSERT INTO benh_nhan (id, tai_khoan_id, ho_ten, ngay_sinh, gioi_tinh, dia_chi, tien_su_benh) VALUES
--(11, 21, N'Ngô Vân Anh', '2006-12-01', N'Nữ', N'Kiên Giang', N'Không có');
--UPDATE benh_nhan
--SET nhom_mau = N'B+',
--    so_cccd = '000000000011',
--    ma_bhyt = 'GD4000000000011'
--WHERE id = 11;
SET IDENTITY_INSERT benh_nhan OFF;
GO

-- =========================================================
-- 5. DỮ LIỆU BẢNG LICH_LAM_VIEC (Tạo 20 ca làm việc mẫu cho các bác sĩ)
-- =========================================================
SET IDENTITY_INSERT lich_lam_viec ON;
INSERT INTO lich_lam_viec (id, bac_si_id, ngay_lam_viec, ten_ca, gio_bat_dau, gio_ket_thuc, so_luong_toida) VALUES
(1, 1, '2026-06-10', N'Ca sáng', '08:00:00', '11:30:00', 10),
(2, 1, '2026-06-10', N'Ca chiều', '13:30:00', '17:00:00', 10),
(3, 2, '2026-06-10', N'Ca sáng', '08:00:00', '11:30:00', 8),
(4, 3, '2026-06-11', N'Ca sáng', '08:00:00', '11:30:00', 10),
(5, 3, '2026-06-11', N'Ca chiều', '13:30:00', '17:00:00', 10),
(6, 4, '2026-06-11', N'Ca sáng', '08:00:00', '11:30:00', 5),
(7, 5, '2026-06-12', N'Ca sáng', '08:00:00', '11:30:00', 12),
(8, 5, '2026-06-12', N'Ca chiều', '13:30:00', '17:00:00', 12),
(9, 6, '2026-06-12', N'Ca sáng', '08:00:00', '11:30:00', 10),
(10, 7, '2026-06-13', N'Ca sáng', '08:00:00', '11:30:00', 10),
(11, 7, '2026-06-13', N'Ca chiều', '13:30:00', '17:00:00', 10),
(12, 8, '2026-06-13', N'Ca sáng', '08:00:00', '11:30:00', 8),
(13, 1, '2026-06-14', N'Ca sáng', '08:00:00', '11:30:00', 10),
(14, 2, '2026-06-14', N'Ca chiều', '13:30:00', '17:00:00', 10),
(15, 3, '2026-06-15', N'Ca sáng', '08:00:00', '11:30:00', 10),
(16, 4, '2026-06-15', N'Ca chiều', '13:30:00', '17:00:00', 5),
(17, 5, '2026-06-16', N'Ca sáng', '08:00:00', '11:30:00', 10),
(18, 6, '2026-06-16', N'Ca chiều', '13:30:00', '17:00:00', 10),
(19, 7, '2026-06-17', N'Ca sáng', '08:00:00', '11:30:00', 10),
(20, 8, '2026-06-17', N'Ca chiều', '13:30:00', '17:00:00', 10),
(21, 7, '2026-11-07', N'Ca sáng', '08:00:00', '11:30:00', 10),
(22, 7, '2026-11-08', N'Ca chiều', '13:00:00', '16:30:00', 10);
SET IDENTITY_INSERT lich_lam_viec OFF;
GO

-- =========================================================
-- 6. DỮ LIỆU BẢNG LICH_HEN (Tạo 20 lịch hẹn khám mẫu)
-- =========================================================
SET IDENTITY_INSERT lich_hen ON;
INSERT INTO lich_hen (id, benh_nhan_id, bac_si_id, lich_lam_viec_id, ngay_kham, khung_gio_kham, trieu_chung_benh, trang_thai) VALUES
(1, 1, 1, 1, '2026-06-10', '08:00 - 08:30', N'Đau đầu, chóng mặt kéo dài', 'DA_KHAM'),
(2, 2, 2, 3, '2026-06-10', '08:30 - 09:00', N'Bé sốt cao và ho khan', 'DA_KHAM'),
(3, 3, 3, 4, '2026-06-11', '09:00 - 09:30', N'Đau nhức răng hàm số 8', 'CHO_XAC_NHAN'),
(4, 4, 4, 6, '2026-06-11', '10:00 - 10:30', N'Đau tức ngực nhẹ khi vận động', 'CHO_XAC_NHAN'),
(5, 5, 5, 7, '2026-06-12', '08:00 - 08:30', N'Nổi mẩn đỏ ngứa vùng tay chân', 'DA_XAC_NHAN'),
(6, 6, 6, 9, '2026-06-12', '08:30 - 09:00', N'Mờ mắt, mỏi mắt khi nhìn máy tính', 'CHO_XAC_NHAN'),
(7, 7, 7, 10, '2026-06-13', '09:00 - 09:30', N'Đau rát họng, khản tiếng', 'CHO_XAC_NHAN'),
(8, 8, 8, 12, '2026-06-13', '09:30 - 10:00', N'Đau mỏi vùng thắt lưng', 'CHO_XAC_NHAN'),
(9, 9, 1, 13, '2026-06-14', '08:00 - 08:30', N'Mệt mỏi, chán ăn, mất ngủ', 'CHO_XAC_NHAN'),
(10, 10, 2, 14, '2026-06-14', '14:00 - 14:30', N'Tái khám định kỳ cho trẻ', 'CHO_XAC_NHAN'),
(11, 1, 3, 15, '2026-06-15', '08:30 - 09:00', N'Cạo vôi răng định kỳ', 'CHO_XAC_NHAN'),
(12, 2, 4, 16, '2026-06-15', '15:00 - 15:30', N'Kiểm tra huyết áp tim mạch', 'CHO_XAC_NHAN'),
(13, 3, 5, 17, '2026-06-16', '08:00 - 08:30', N'Trị mụn trứng cá', 'CHO_XAC_NHAN'),
(14, 4, 6, 18, '2026-06-16', '14:00 - 14:30', N'Đo kính cận thị mới', 'CHO_XAC_NHAN'),
(15, 5, 7, 19, '2026-06-17', '08:30 - 09:00', N'Viêm mũi dị ứng thời tiết', 'CHO_XAC_NHAN'),
(16, 6, 8, 20, '2026-06-17', '14:30 - 15:00', N'Đau mỏi vai gáy', 'CHO_XAC_NHAN'),
(17, 7, 1, 1, '2026-06-10', '09:00 - 09:30', N'Rối loạn tiền đình', 'DA_HUY'),
(18, 8, 2, 3, '2026-06-10', '09:00 - 09:30', N'Tiêu chảy cấp ở trẻ nhỏ', 'DA_KHAM'),
(19, 9, 3, 4, '2026-06-11', '09:30 - 10:00', N'Đau ê buốt răng cửa', 'CHO_XAC_NHAN'),
(20, 10, 5, 7, '2026-06-12', '09:00 - 09:30', N'Viêm da cơ địa', 'CHO_XAC_NHAN');
SET IDENTITY_INSERT lich_hen OFF;
GO

-- =========================================================
-- 7. DỮ LIỆU BẢNG HO_SO_BENH_AN (Tạo hồ sơ cho các lịch hẹn đã khám - ID 1, 2, 18)
-- =========================================================
SET IDENTITY_INSERT ho_so_benh_an ON;
INSERT INTO ho_so_benh_an (id, lich_hen_id, chan_doan, loi_dan_bac_si) VALUES
(1, 1, N'Thiếu máu nhẹ, suy nhược thần kinh tạm thời', N'Nghỉ ngơi nhiều, ăn uống đủ chất, uống thuốc theo đơn.'),
(2, 2, N'Viêm đường hô hấp trên ở trẻ em', N'Cho bé uống thuốc đúng giờ, giữ ấm cổ, tái khám nếu sốt cao.'),
(3, 18, N'Rối loạn tiêu hóa cấp tính', N'Bổ sung điện giải Oresol, ăn đồ lỏng, tránh sữa và đồ dầu mỡ.');
SET IDENTITY_INSERT ho_so_benh_an OFF;
GO

-- =========================================================
-- 8. DỮ LIỆU BẢNG DON_THUOC (Tạo các dòng đơn thuốc gắn với hồ sơ bệnh án)
-- =========================================================
SET IDENTITY_INSERT don_thuoc ON;
INSERT INTO don_thuoc (id, ho_so_benh_an_id, ten_thuoc, lieu_luong, so_luong, huong_dan_su_dung) VALUES
(1, 1, N'Paracetamol 500mg', N'Sáng 1 viên, Tối 1 viên', 10, N'Uống khi đau đầu'),
(2, 1, N'Hoạt huyết dưỡng não', N'Sáng 1 viên, Chiều 1 viên', 20, N'Uống sau khi ăn no'),
(3, 2, N'Hapacol 250mg', N'Uống khi sốt > 38.5 độ', 5, N'Cách nhau ít nhất 4-6 tiếng'),
(4, 2, N'Siro ho Prospan', N'Sáng 5ml, Chiều 5ml', 1, N'Uống sau khi ăn'),
(5, 3, N'Oresol dạng gói', N'Pha 1 gói với 200ml nước', 10, N'Uống thay nước lọc khi bị tiêu chảy'),
(6, 3, N'Smecta', N'Sáng 1 gói, Chiều 1 gói', 6, N'Uống trước khi ăn');
SET IDENTITY_INSERT don_thuoc OFF;
GO

select * from benh_nhan
