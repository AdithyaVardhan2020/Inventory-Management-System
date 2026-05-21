@echo off
REM Navigate to the repository
cd /d "C:\Users\Admin-pc\OneDrive\Documents\GitHub\Inventory-Management-System"

REM Show initial status
echo.
echo ============================================
echo FLATTEN NESTED BACKEND-JAVA DIRECTORY
echo ============================================
echo Current directory: %CD%
echo.
echo === Initial Git Status ===
git status --short
echo.

REM Perform all git mv operations
setlocal enabledelayedexpansion
set errors=0

REM Function-like macro to do git mv
set "moves=0"

echo === PHASE 1: Moving src directory ===
git mv backend-java/backend-java/src backend-java/src
if errorlevel 1 (set /a errors+=1) else (echo ✓ src directory moved)

echo.
echo === PHASE 2: Moving .mvn directory ===
git mv backend-java/backend-java/.mvn backend-java/.mvn
if errorlevel 1 (set /a errors+=1) else (echo ✓ .mvn directory moved)

echo.
echo === PHASE 3: Moving mvnw script ===
git mv backend-java/backend-java/mvnw backend-java/mvnw
if errorlevel 1 (set /a errors+=1) else (echo ✓ mvnw moved)

echo.
echo === PHASE 4: Moving mvnw.cmd script ===
git mv backend-java/backend-java/mvnw.cmd backend-java/mvnw.cmd
if errorlevel 1 (set /a errors+=1) else (echo ✓ mvnw.cmd moved)

echo.
echo === PHASE 5: Moving .gitignore ===
git mv backend-java/backend-java/.gitignore backend-java/.gitignore
if errorlevel 1 (set /a errors+=1) else (echo ✓ .gitignore moved)

echo.
echo === PHASE 6: Moving .gitattributes ===
git mv backend-java/backend-java/.gitattributes backend-java/.gitattributes
if errorlevel 1 (set /a errors+=1) else (echo ✓ .gitattributes moved)

echo.
echo === PHASE 7: Moving HELP.md ===
git mv backend-java/backend-java/HELP.md backend-java/HELP.md
if errorlevel 1 (set /a errors+=1) else (echo ✓ HELP.md moved)

echo.
echo === PHASE 8: Moving .vscode directory ===
git mv backend-java/backend-java/.vscode backend-java/.vscode
if errorlevel 1 (set /a errors+=1) else (echo ✓ .vscode directory moved)

echo.
echo === PHASE 9: Moving lib directory ===
if exist "backend-java/backend-java/lib" (
    git mv backend-java/backend-java/lib backend-java/lib
    if errorlevel 1 (set /a errors+=1) else (echo ✓ lib directory moved)
) else (
    echo ⊘ lib directory does not exist - skipping
)

echo.
echo === PHASE 10: Removing nested empty backend-java directory ===
git rm -r backend-java/backend-java
if errorlevel 1 (set /a errors+=1) else (echo ✓ Nested backend-java/backend-java removed)

echo.
echo ============================================
echo FLATTEN OPERATION COMPLETE
echo ============================================
echo.
echo === Final Git Status ===
git status
echo.

if %errors% gtr 0 (
    echo WARNING: %errors% error(s) encountered
) else (
    echo SUCCESS: All operations completed without errors
)

echo.
echo === Staged Changes (ready to commit) ===
git diff --cached --name-status

echo.
pause
