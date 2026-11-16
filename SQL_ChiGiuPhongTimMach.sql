USE HeThongDatLichKhamBenh;
GO
-- Chạy sau khi đã sao lưu database. Không xóa lịch hẹn hoặc hồ sơ bệnh nhân.
SET XACT_ABORT OFF;
BEGIN TRANSACTION;

DECLARE @phongTimMachId BIGINT;
SELECT TOP (1) @phongTimMachId = id
FROM dbo.phong_kham
WHERE ten_phong = N'Phòng khám Tim mạch 204'
ORDER BY id;

IF @phongTimMachId IS NULL
BEGIN
    INSERT INTO dbo.phong_kham (ten_phong, khu_vuc)
    VALUES (N'Phòng khám Tim mạch 204', N'Tầng 2 - Khoa Tim mạch');
    SET @phongTimMachId = SCOPE_IDENTITY();
END;

-- Gán mọi lịch hẹn đã phân phòng về phòng Tim mạch, giữ nguyên lịch sử lịch hẹn.
UPDATE dbo.lich_hen
SET phong_kham_id = @phongTimMachId
WHERE phong_kham_id IS NOT NULL AND phong_kham_id <> @phongTimMachId;

-- Giữ nguyên liên kết dịch vụ của lịch hẹn cũ để bảo toàn dữ liệu lịch sử.
-- Các lịch hẹn tạo mới không còn ghi dịch vụ hay thanh toán.

-- Xóa phòng khám thừa sau khi đã chuyển khóa ngoại.
DELETE FROM dbo.phong_kham WHERE id <> @phongTimMachId;

-- Giữ lại bảng dich_vu_kham/thanh_toan để tránh phá các Entity/Repository hiện có;
-- phiên bản Java thay thế không tạo bản ghi thanh toán khi tiếp nhận bệnh nhân.
COMMIT TRANSACTION;

SELECT id, ten_phong, khu_vuc FROM dbo.phong_kham;
SELECT COUNT(*) AS so_lich_hen FROM dbo.lich_hen;
GO

SELECT @@TRANCOUNT AS SoGiaoDichDangMo;
ROLLBACK TRANSACTION;
