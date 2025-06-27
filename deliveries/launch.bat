@echo off
setlocal
if defined PSModulePath (
    rem Siamo in PowerShell
    powershell -Command "chcp 65001 | Out-Null; [Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $env:JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'; java -jar IS25-AM17-Windows.jar client"
) else (
    rem Siamo in CMD
    chcp 65001 >nul
    set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
    java -jar IS25-AM17-Windows.jar client
)