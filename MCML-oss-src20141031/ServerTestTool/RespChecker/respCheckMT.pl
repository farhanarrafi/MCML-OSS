#!/usr/bin/perl

use utf8;
use XML::TreePP;
require "checkCommon.pl";

my $progName = "respCheckMT.pl";

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

### MT001
my $serviceName = $respXml->{MCML}->{Server}->{Response}->{'-Service'};
$resVal = &printResEqualOrNot("MT001", $serviceName, "MT", "invalid service name : \"$serviceName\"");

### MT002
my $t_Res_Data = $respXml->{MCML}->{Server}->{Response}->{Output}->{Data};
$resVal = &checkChildKeyExisting("MT002", $t_Res_Data, "Text", "\<Text\> tag not found.");

### MT003
if ($resVal == 0) {
	my $t_Res_ModelType = $respXml->{MCML}->{Server}->{Response}->{Output}->{Data}->{Text}->{ModelType};
	$resVal = &checkChildKeyExisting("MT003", $t_Res_ModelType, "Language", "\<Language\> tag not found.");
} else {
	&printResNotAvailable("MT003", "because the result of [MT002] is NG.");
	$resVal = -2;
}

### MT004
if ($resVal == 0) {
	my $a_Res_Language_ID = $respXml->{MCML}->{Server}->{Response}->{Output}->{Data}->{Text}->{ModelType}->{Language}->{'-ID'};
	my $a_Req_LanguageType_ID = $reqXml->{MCML}->{Server}->{Request}->{TargetOutput}->{LanguageType}->{'-ID'};
	$resVal = &printResEqualOrNot("MT004", $a_Res_Language_ID, $a_Req_LanguageType_ID, "Different Language : \"$a_Res_Language_ID\" and \"$a_Req_LanguageType_ID\"");
} elsif ($resVal == -1) {
	&printResNotAvailable("MT004", "because the result of [MT003] is NG.");
} else {
	&printResNotAvailable("MT004", "because the result of [MT002] is NG.");
}

################ Response Check END ################

exit;
