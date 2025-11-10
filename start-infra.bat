@echo off
REM ---- 启动本机 Nacos + Seata（使用你的实际安装路径） ----

echo [INFRA] Starting Nacos...
start "nacos" cmd /c ""C:\Users\ASUS\tools\nacos\nacos\bin\startup.cmd" -m standalone"

echo [INFRA] Starting Seata...
start "seata" cmd /c ""C:\Users\ASUS\tools\seata\apache-seata-2.5.0-incubating-bin\seata-server\bin\seata-server.bat" -m file"

echo [INFRA] Done.
exit /b 0
