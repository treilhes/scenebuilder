rem @echo off
setlocal enabledelayedexpansion

set APPNAME=FAKEAppName
set CFGFILE=%APPNAME%.cfg
set "SCRIPTDIR=%~dp0"
rem === Get the directory where this script resides ===
set "APPDIR=%SCRIPTDIR%app\"

rem === Path to the config file ===
set "CFGFILE=%APPDIR%%CFGFILE%"

rem === Initialize variables ===
set "CLASSPATH="
set "MAINCLASS="
set "JAVAOPTS="

rem === Read the config file line by line ===
for /f "usebackq tokens=1* delims== " %%A in ("%CFGFILE%") do (
    set "KEY=%%A"
    set "VALUE=%%B"

    rem --- Ignore section headers like [Application] ---
    if /I "!KEY:~0,1!" NEQ "[" (
        rem Replace $APPDIR with the actual path
        set "VALUE=!VALUE:$APPDIR=%APPDIR%!"

        rem --- Handle each type of line ---
        if /I "!KEY!"=="app.classpath" (
            if defined CLASSPATH (
                set "CLASSPATH=!CLASSPATH!;!VALUE!"
            ) else (
                set "CLASSPATH=!VALUE!"
            )
        ) else if /I "!KEY!"=="app.mainclass" (
            set "MAINCLASS=!VALUE!"
        ) else if /I "!KEY!"=="java-options" (
            if defined JAVAOPTS (
                set "JAVAOPTS=!JAVAOPTS! !VALUE!"
            ) else (
                set "JAVAOPTS=!VALUE!"
            )
        )
    )
)

rem === Run the Java application ===
echo Running: java %JAVAOPTS% -cp "%CLASSPATH%" %MAINCLASS%
java %JAVAOPTS% -cp "%CLASSPATH%" %MAINCLASS%

endlocal