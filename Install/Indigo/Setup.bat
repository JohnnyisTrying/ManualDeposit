@echo off
cls 

SET RETURN=INSTALLMAP
goto MapConfDrive
:INSTALLMAP
if %MAPFAIL% neq NO goto dieNice

if exist C:\Indigo goto CheckVersion
echo.
echo Couldnt find INDIGO on this machine, so ill install it now
echo This may take a couple of minutes.
echo.
pause
md C:\Indigo
goto CopyFiles

pause
exit



:CheckVersion
fc I:\indigo_release\TestUtil\Version.txt c:\INDIGO\testUtil\Version.txt
if %errorlevel% equ 0 goto MENU
echo.
echo A new version of indigo is ready to install on this computer
echo This may take a couple of minutes to complete
pause

:CopyFiles
call i:\indigo_release\testutil\resync.bat
exit


:MENU
fc I:\indigo_release\TestUtil\notes.txt c:\INDIGO\testUtil\notes.txt
if %errorlevel% neq 0 goto displaynotes

color 1F
cd c:\indigo\
set RETURN=MENU
cls
type c:\indigo\testutil\Version.txt
echo.
echo.
echo       INDIGO Test suite
echo.
echo       1. Launch INDIGO
echo       2. Switch to UAT config
echo       3. Switch to Hokai config
echo       4. Switch to local config on this machine
echo.
echo       M. Map Config Drive
echo       C. Start Config Utility
echo       R. Reinstall INDIGO
echo       N. View the release notes
echo       Q. Quit
echo.

set choice=
set /p choice=      Enter option: 

echo.
if not '%choice%'=='' set choice=%choice:~0,1%
if '%choice%'=='1' goto Launch
if '%choice%'=='2' goto ConfUAT
if '%choice%'=='3' goto ConfHokai
if '%choice%'=='4' goto ConfLocal

if /i '%choice%'=='M' goto MapConfDrive
if /i '%choice%'=='C' goto Metadata
if /i '%choice%'=='R' goto CopyFiles
if /i '%choice%'=='N' goto reviewnotes
if /i '%choice%'=='Q' exit

::
echo.
echo.
echo "%choice%" is not a valid option - try again
echo.
pause
goto MENU
::

:Launch
echo a12345678A| c:\indigo\testutil\clip.exe
c:\indigo\indigo.exe
exit

:Metadata
c:\indigo\indigo.exe /metadata
exit


:displaynotes
copy I:\indigo_release\TestUtil\notes.txt c:\INDIGO\testUtil\notes.txt
c:\indigo\testutil\n.exe c:\indigo\testutil\notes.txt
goto MENU

:reviewnotes
c:\indigo\testutil\n.exe I:\indigo\testutil\notes.txt
goto MENU



:MapConfDrive
set MAPFAIL=NO
if exist I:\indigo_release\ goto driveAlreadyMounted
net use I: \\nls1\DATA\Fileplan\DSIG\NONCLI~1\NDHADI~1\3INTEG~1\Applications\INDIGO\joint_config\
if %errorlevel% neq 0 goto driveMountFailure
echo.
echo The configuration drive has been mounted :)
echo.
pause
goto %RETURN%

:driveMountFailure
cls
echo.
echo The configuration drive could not be mounted.
echo Please contact the NDHA Integration team.
echo.
pause
set MAPFAIL=YES
goto %RETURN%

:driveAlreadyMounted
echo.
echo Looks like the config drive is mounted on I:
echo.
set MAPFAIL=NO
goto %RETURN%


:ConfUAT
copy testutil\uat.properties Application.properties
if %errorlevel% neq 0 goto dieNice
cls
echo.
echo UAT Configuration applied
echo.
pause
goto %RETURN%


:ConfHokai
copy testutil\hokai.properties Application.properties
if %errorlevel% neq 0 goto copyFail
cls
echo.
echo HOKAI Configuration applied
echo.
pause
goto %RETURN%

:ConfLocal
copy testutil\local.properties Application.properties
if %errorlevel% neq 0 goto copyFail
cls
echo.
echo LOCAL Configuration applied
echo.
pause
goto %RETURN%


:dieNice
echo.
echo !!!!! WHOA THERE !!!!!
echo That didnt work at all, contact your nearest geek
echo.
pause
exit 



dir program.ext | find /i "program.ext" > dir.txt
for /f "tokens=1 delims= " %%a in (dir.txt) do set XDate="%%a

