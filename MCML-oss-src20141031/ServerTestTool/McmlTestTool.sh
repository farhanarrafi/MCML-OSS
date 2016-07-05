#!/usr/bin/bash

## command-line args
service_name=$1
target_lang=$2

if [ ! ${service_name} ]; then
	echo "invalid service name (blank)"
	exit
fi
if [ ! ${target_lang} ]; then
	echo "invalid language code (blank)"
	exit
fi

# args 1=filepath
fileExist() {
	tgt_path=$1
	if [ -e ${tgt_path} ]; then
		echo "" > /dev/null
	else
		echo "Not Found : ${tgt_path}"
		exit
	fi
}

javaExcept() {
	echo "Exit because of McmlCheckTool Exception"
	exit
}

cur_dir=`pwd`
date_time=`date +"%Y%m%d%H%M%S"`

src_xml="../input/"${service_name}"/"${service_name}"_"${target_lang}".xml"
res_xml="../output/"${service_name}"_"${target_lang}"_output_"${date_time}".xml"

cd McmlCheckTool
fileExist ${src_xml}
if [ ${service_name} = "ASR" ]; then
	src_bin="../input/"${service_name}"/"${service_name}"_"${target_lang}".raw"	
	java -jar McmlCheckTool.jar -sourceXml ${src_xml} -sourceBinary ${src_bin} -resultXml ${res_xml} || javaExcept
elif [ ${service_name} = "MT" ]; then
	java -jar McmlCheckTool.jar -sourceXml ${src_xml} -resultXml ${res_xml} || javaExcept
elif [ ${service_name} = "TTS" ]; then
	res_bin="../output/"${service_name}"_"${target_lang}"_output_"${date_time}".wav"
	
	java -jar McmlCheckTool.jar -sourceXml ${src_xml} -resultXml ${res_xml} -resultBinary ${res_bin} || javaExcept
fi
cd ${cur_dir}

echo "================================="

cd RespChecker
perl ./respCheck${service_name}.pl ${src_xml} ${res_xml}
cd ${cur_dir}

echo "================================="

exit
