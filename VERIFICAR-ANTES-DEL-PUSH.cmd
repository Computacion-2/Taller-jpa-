@echo off
cd /d "%~dp0"
powershell.exe -NoProfile -File "%~dp0scripts\verificar-entrega.ps1"
set "verificationExit=%ERRORLEVEL%"
pause
exit /b %verificationExit%
