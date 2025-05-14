@echo off
SETLOCAL EnableDelayedExpansion

echo Running pre-commit checks...

:: Stash any changes not being committed
git stash -q --keep-index

:: First, automatically format code
echo Formatting code with Spotless...
call gradlew.bat spotlessApply
set FORMAT_RESULT=%ERRORLEVEL%

if %FORMAT_RESULT% neq 0 (
    echo Failed to format code. Check for errors in your code.
    git stash pop -q
    exit /b 1
)

:: If formatting changed anything, add those changes to the commit
git diff --quiet
if %ERRORLEVEL% neq 0 (
    git add -u
)

:: Run checkstyle
echo Running Checkstyle...
call gradlew.bat checkstyleMain
set CHECKSTYLE_RESULT=%ERRORLEVEL%

:: Pop the stash
git stash pop -q

:: Return the status
if %CHECKSTYLE_RESULT% neq 0 (
    echo Checkstyle check failed. Please fix the reported issues.
    exit /b 1
)

echo Pre-commit checks passed! Code formatting was automatically applied.
exit /b 0
