@echo off
chcp 65001 >nul
cd /d "%~dp0"
set "CLIENT_SRC=%~dp0client\src\main\java\com\example\doanjavaclient"
set "CLIENT_CLASSES=%~dp0client\target\classes"
if not exist "%CLIENT_CLASSES%" mkdir "%CLIENT_CLASSES%"
echo ============================================================
echo DOAN JAVA - BIEN DICH CLIENT CONSOLE
echo ============================================================
javac -encoding UTF-8 -d "%CLIENT_CLASSES%" "%CLIENT_SRC%\SocketClientProtocol.java" "%CLIENT_SRC%\SocketClientDatLich.java" "%CLIENT_SRC%\KiemThuNhieuClient.java" "%CLIENT_SRC%\ClientConsole.java"
if errorlevel 1 (
    echo Bien dich Client that bai. Hay kiem tra JDK da duoc cai va them vao PATH.
    pause
    exit /b 1
)
echo.
echo Dang mo Client Console...
java -cp "%CLIENT_CLASSES%" com.example.doanjavaclient.ClientConsole 127.0.0.1 9999
pause
