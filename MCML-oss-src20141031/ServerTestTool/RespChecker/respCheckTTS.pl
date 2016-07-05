#!/usr/bin/perl

use utf8;
use XML::TreePP;
require "checkCommon.pl";

my $progName = "respCheckTTS.pl";

## check command-line args
if ($#ARGV < 1) {
	print "usage:\n";
	print "	perl $progName ARG1 ARG2\n";
	print "		ARG1 : Request File Path\n";
	print "		ARG2 : Response File Path\n";
	print "\n";
	die;
}

## get command-line args
my $reqFilePath = $ARGV[0];
my $respFilePath = $ARGV[1];

## file check
# 
&checkFileExisting($reqFilePath);
&checkFileExisting($respFilePath);

## create instance of XML::TreePP
# request XML
my $tppReq = XML::TreePP->new();
$tppReq->set( output_encoding => "UTF-8");
$tppReq->set( utf8_flag => 1);
# response XML
my $tppResp = XML::TreePP->new();
$tppResp->set( output_encoding => "UTF-8");
$tppResp->set( utf8_flag => 1);

## get tree data of response mcml
# Request MCML
my $reqXml = $tppReq->parsefile($reqFilePath);
# Response MCML
my $respXml = $tppResp->parsefile($respFilePath);


################ Response Check START ################

my $resVal = 0;

### TTS001
my $serviceName = $respXml->{MCML}->{Server}->{Response}->{'-Service'};
$resVal = &printResEqualOrNot("TTS001", $serviceName, "TTS", "invalid service name : \"$serviceName\"");

### TTS002
my $t_Res_Data = $respXml->{MCML}->{Server}->{Response}->{Output}->{Data};
$resVal = &checkChildKeyExisting("TTS002", $t_Res_Data, "Audio", "\<Text\> tag not found.");

### TTS003
if ($resVal == 0) {
	my $t_Res_ModelType = $respXml->{MCML}->{Server}->{Response}->{Output}->{Data}->{Audio}->{ModelType};
	$resVal = &checkChildKeyExisting("TTS003", $t_Res_ModelType, "LanguagE", "\<Language\> tag not found.");
} else {
	&printResNotAvailable("TTS003", "because the result of [TTS002] is NG.");
	$resVal = -2;
}

### TTS004
if ($resVal == 0) {
	my $a_Res_Language_ID = $respXml->{MCML}->{Server}->{Response}->{Output}->{Data}->{Audio}->{ModelType}->{Language}->{'-ID'};
	my $a_Req_LanguageType_ID = $reqXml->{MCML}->{Server}->{Request}->{TargetOutput}->{LanguageType}->{'-ID'};
	$resVal = &printResEqualOrNot("TTS004", $a_Res_Language_ID, $a_Req_LanguageType_ID, "Different Language : \"$a_Res_Language_ID\" and \"$a_Req_LanguageType_ID\"");
} elsif ($resVal == -1) {
	&printResNotAvailable("TTS004", "because the result of [TTS003] is NG.");
} else {
	&printResNotAvailable("TTS004", "because the result of [TTS002] is NG.");
}

################ Response Check END ################

exit;
