@echo off
if "%1"=="" (
    start /WAIT /HIGH /B cmd.exe /V /C %~s0 weiter_machen
    goto:eof
)
java -cp "awserver.jar;dependencies\mysql-connector-j-8.3.0.jar" content.Init
