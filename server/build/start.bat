@echo off
if "%1"=="" (
    start /WAIT /HIGH /B cmd.exe /V /C %~s0 weiter_machen
    goto:eof
)
java -jar awserver.jar