@echo off
REM ---- 启动本机 Nacos + Seata ----

echo [INFRA] Starting Nacos...
start "nacos" cmd /k ""C:\dev\nacos\bin\startup.cmd" -m standalone"

echo [INFRA] Starting Seata...
start "seata" cmd /k ""C:\Users\ASUS\tools\seata\apache-seata-2.5.0-incubating-bin\seata-server\bin\seata-server.bat" -m file"

echo [INFRA] Done.
exit /b 0
