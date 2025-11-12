@echo off
setlocal
cd /d "%~dp0"

REM 自动识别当前分支
for /f "delims=" %%b in ('git rev-parse --abbrev-ref HEAD') do set BRANCH=%%b
if not defined BRANCH set BRANCH=main

REM 可选：把曾误跟踪的大文件目录从索引移除一次（本地文件保留）
git rm -r --cached "videos" 2>NUL
git rm -r --cached "video-service\storage" 2>NUL

set "MSG=%*"
if not defined MSG set "MSG=chore: sync %DATE% %TIME%"

git add -A
git diff --cached --quiet && echo [Info] 没有改动需要提交。 || git commit -m "%MSG%"

git pull --rebase origin %BRANCH%
git push origin %BRANCH%

echo [Done] 已推送到分支 %BRANCH%.
endlocal
