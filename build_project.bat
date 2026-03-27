@echo off
setlocal
echo --- [Step 1] Cleaning and Compiling with Maven ---
call mvn clean compile
if errorlevel 1 (
    echo [ERROR] Compile failed. Fix errors above before continuing.
    pause
    exit /b 1
)

echo --- [Step 2] Copying Dependencies (first time only) ---
if not exist "lib\javalin-6.1.3.jar" (
    call mvn dependency:copy-dependencies -DoutputDirectory=lib
) else (
    echo Dependencies already present, skipping...
)

echo --- [Step 3] Verifying Static Resources ---
if not exist "target\classes\static\signin.html" (
    echo [WARNING] signin.html not found in target/classes/static.
    echo Ensure your HTML files are in src/main/resources/static
)

echo --- [Step 4] Launching Flora Catalogue Server ---
echo Base URL: http://localhost:8080/signin.html
echo Press Ctrl+C to stop the server.
echo ------------------------------------------------
java -cp "target/classes;lib/*" back

pause