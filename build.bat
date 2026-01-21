@echo off
:: This kills the specific class file
if exist Sqlite.class del Sqlite.class

:: If you want to kill ALL class files in the folder, use:
:: del *.class

echo Compiling...
javac -cp ".;Lib/*" Sqlite.java

if %errorlevel% equ 0 (
    echo Running...
    java -cp ".;Lib/*" Sqlite
)
pause