@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ============================================================
echo DOAN JAVA - WEB SERVER + TCP SOCKET SERVER
echo ============================================================
echo Dang khoi dong Spring Boot. Socket Server se tu mo cong 9999 khi app san sang.
echo Web: http://localhost:8080
where mvn >nul 2>&1
if %errorlevel%==0 (
    call mvn spring-boot:run
) else (
    call mvnw.cmd spring-boot:run
)
pause
