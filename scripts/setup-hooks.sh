#!/bin/sh
# Cross-platform setup script for git hooks

# Create hooks directory if it doesn't exist
mkdir -p .git/hooks

# Copy hooks
cp scripts/git-hooks/pre-commit .git/hooks/
cp scripts/git-hooks/commit-msg .git/hooks/

# Make hooks executable
chmod +x .git/hooks/pre-commit
chmod +x .git/hooks/commit-msg

# For Windows systems, also setup .bat files
if [ -n "$WINDIR" ] || [ -n "$windir" ]; then
    cp scripts/git-hooks/pre-commit.bat .git/hooks/
    icacls .git/hooks/pre-commit.bat /grant Everyone:RX > /dev/null 2>&1
fi

echo "Git hooks have been set up successfully!"
echo "You can now commit your code and formatting will be automatically applied."
