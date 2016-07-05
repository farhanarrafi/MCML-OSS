@echo off

set service_name=%1
set target_lang=%2

if "%service_name%"=="" (
	echo "invalid service name (blank)"
	exit /b
)

if "%target_lang%"=="" (
	echo "invalid language code (blank)"
	exit /b
)

for /f "usebackq tokens=*" %%i in (`cd`) do @set cur_dir=%%i
set date_time=%date:~-10,4%%date:~-5,2%%date:~-2,2%

set src_xml=../input/%service_name%/%service_name%_%target_lang%.xml
set res_xml=../output/%service_name%_%target_lang%_output_%date_time%.xml
set src_bin=../input/%service_name%/%service_name%_%target_lang%.raw
set res_bin=../output/%service_name%_%target_lang%_output_%date_time%.wav

cd McmlChecktool
if not exist "%src_xml%" ( echo "Not Found : %src_xml%" & cd %cur_dir% & exit /b )

if "%service_name%"=="ASR" (
	if not exist "%src_bin%" ( echo "Not Found : %src_bin%" & cd %cur_dir% & exit /b )
	java -jar McmlCheckTool.jar -sourceXml %src_xml% -sourceBinary %src_bin% -resultXml %res_xml%
) else if "%service_name%"=="MT" (
	java -jar McmlCheckTool.jar -sourceXml %src_xml% -resultXml %res_xml%
) else if "%service_name%"=="TTS" (
	java -jar McmlCheckTool.jar -sourceXml %src_xml% -resultXml %res_xml% -resultBinary %res_bin%
)
cd %cur_dir%

echo "================================="

cd RespChecker
perl respCheck%service_name%.pl %src_xml% %res_xml%
cd %cur_dir%

echo "================================="

exit /b
