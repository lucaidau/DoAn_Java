@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"
echo ============================================================
echo KHOI DONG DOAN JAVA: SERVER TRUOC - CLIENT SAU
echo ============================================================
start "DoAn Java - SERVER" "%ComSpec%" /k ""%~dp0CHAY_SERVER.bat""
echo Dang cho TCP Socket Server tai 127.0.0.1:9999 san sang...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$deadline=(Get-Date).AddMinutes(3); while((Get-Date) -lt $deadline) { $client=New-Object System.Net.Sockets.TcpClient; try { $client.Connect('127.0.0.1',9999); $client.Close(); exit 0 } catch { $client.Dispose(); Start-Sleep -Seconds 2 } }; exit 1"
if errorlevel 1 (
    echo Khong thay Socket Server sau 3 phut. Hay xem cua so SERVER de kiem tra loi SQL Server hoac Maven.
    pause
    exit /b 1
)
echo Server da san sang. Dang mo Client Console va trinh duyet Web...
start "DoAn Java - CLIENT CONSOLE" "%ComSpec%" /k ""%~dp0CHAY_CLIENT.bat""
start "" "http://localhost:8080"
endlocal
