@echo off
SETLOCAL EnableDelayedExpansion

echo Running pre-commit checks...

:: First, automatically format code
echo Formatting code with Spotless...
call gradlew.bat spotlessApply
set FORMAT_RESULT=%ERRORLEVEL%

if %FORMAT_RESULT% neq 0 (
    echo Failed to format code. Check for errors in your code.
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

:: Return the status
if %CHECKSTYLE_RESULT% neq 0 (
    echo Checkstyle check failed. Please fix the reported issues.
    exit /b 1
)

echo Pre-commit checks passed! Code formatting was automatically applied.
exit /b 0
