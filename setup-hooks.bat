@echo off
echo Setting up Git hooks...

if not exist .git\hooks mkdir .git\hooks

echo Copying git hooks...
copy /Y scripts\git-hooks\pre-commit .git\hooks\pre-commit
copy /Y scripts\git-hooks\pre-commit.bat .git\hooks\pre-commit.bat
copy /Y scripts\git-hooks\commit-msg .git\hooks\commit-msg

echo Making hooks executable...
:: This ensures the hook works in both Git Bash and Windows environments
type scripts\git-hooks\pre-commit > .git\hooks\pre-commit
icacls .git\hooks\pre-commit /grant Everyone:RX >nul 2>&1
icacls .git\hooks\pre-commit.bat /grant Everyone:RX >nul 2>&1
type scripts\git-hooks\pre-commit >> .git\hooks\pre-commit.tmp
move /y .git\hooks\pre-commit.tmp .git\hooks\pre-commit

echo Git hooks have been set up successfully!
echo You can now commit your code and formatting will be automatically applied.
