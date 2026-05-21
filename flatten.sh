#!/bin/bash
cd "/c/Users/Admin-pc/OneDrive/Documents/GitHub/Inventory-Management-System"

echo ""
echo "============================================"
echo "FLATTEN NESTED BACKEND-JAVA DIRECTORY"
echo "============================================"
echo "Current directory: $(pwd)"
echo ""
echo "=== Initial Git Status ==="
git status --short
echo ""

declare -i errors=0

echo "=== PHASE 1: Moving src directory ==="
git mv backend-java/backend-java/src backend-java/src
if [ $? -eq 0 ]; then
  echo "✓ src directory moved"
else
  echo "✗ Error moving src"
  ((errors++))
fi
echo ""

echo "=== PHASE 2: Moving .mvn directory ==="
git mv backend-java/backend-java/.mvn backend-java/.mvn
if [ $? -eq 0 ]; then
  echo "✓ .mvn directory moved"
else
  echo "✗ Error moving .mvn"
  ((errors++))
fi
echo ""

echo "=== PHASE 3: Moving mvnw script ==="
git mv backend-java/backend-java/mvnw backend-java/mvnw
if [ $? -eq 0 ]; then
  echo "✓ mvnw moved"
else
  echo "✗ Error moving mvnw"
  ((errors++))
fi
echo ""

echo "=== PHASE 4: Moving mvnw.cmd script ==="
git mv backend-java/backend-java/mvnw.cmd backend-java/mvnw.cmd
if [ $? -eq 0 ]; then
  echo "✓ mvnw.cmd moved"
else
  echo "✗ Error moving mvnw.cmd"
  ((errors++))
fi
echo ""

echo "=== PHASE 5: Moving .gitignore ==="
git mv backend-java/backend-java/.gitignore backend-java/.gitignore
if [ $? -eq 0 ]; then
  echo "✓ .gitignore moved"
else
  echo "✗ Error moving .gitignore"
  ((errors++))
fi
echo ""

echo "=== PHASE 6: Moving .gitattributes ==="
git mv backend-java/backend-java/.gitattributes backend-java/.gitattributes
if [ $? -eq 0 ]; then
  echo "✓ .gitattributes moved"
else
  echo "✗ Error moving .gitattributes"
  ((errors++))
fi
echo ""

echo "=== PHASE 7: Moving HELP.md ==="
git mv backend-java/backend-java/HELP.md backend-java/HELP.md
if [ $? -eq 0 ]; then
  echo "✓ HELP.md moved"
else
  echo "✗ Error moving HELP.md"
  ((errors++))
fi
echo ""

echo "=== PHASE 8: Moving .vscode directory ==="
git mv backend-java/backend-java/.vscode backend-java/.vscode
if [ $? -eq 0 ]; then
  echo "✓ .vscode directory moved"
else
  echo "✗ Error moving .vscode"
  ((errors++))
fi
echo ""

echo "=== PHASE 9: Moving lib directory ==="
if [ -d "backend-java/backend-java/lib" ]; then
  git mv backend-java/backend-java/lib backend-java/lib
  if [ $? -eq 0 ]; then
    echo "✓ lib directory moved"
  else
    echo "✗ Error moving lib"
    ((errors++))
  fi
else
  echo "⊘ lib directory does not exist - skipping"
fi
echo ""

echo "=== PHASE 10: Removing nested empty backend-java directory ==="
git rm -r backend-java/backend-java
if [ $? -eq 0 ]; then
  echo "✓ Nested backend-java/backend-java removed"
else
  echo "✗ Error removing nested directory"
  ((errors++))
fi
echo ""

echo "============================================"
echo "FLATTEN OPERATION COMPLETE"
echo "============================================"
echo ""
echo "=== Final Git Status ==="
git status
echo ""

if [ $errors -gt 0 ]; then
  echo "⚠ WARNING: $errors error(s) encountered"
  exit 1
else
  echo "✓ SUCCESS: All operations completed without errors"
  exit 0
fi
