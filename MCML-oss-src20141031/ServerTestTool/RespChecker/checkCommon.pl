#!/usr/bin/perl

use utf8;

################ Common Var START ################
my $testNum;
my $resNum;
my $errMsg;
my $retVal;
################ Common Var END ################


## function check child key existing
sub checkChildKeyExisting {
	$testNum = $_[0];
	my $targetTag = $_[1];
	my $targetKey = $_[2];
	$errMsg = $_[3];
	
	$retVal = 0;
	
	$resNum = 0;
	foreach my $key_TargetTag (keys %{$targetTag}) {
		if ($key_TargetTag eq $targetKey) {
			$resNum = 1;
		}
	}
	if ($resNum == 1) {
		print "[${testNum}] : OK\n";
	} else {
		print "[${testNum}] : NG";
		if ($errMsg ne "") {
			print "\t($errMsg)\n";
		} else {
			print "\n";
		}
		$retVal = -1;
	}
	
	return $retVal;
}

## function print equals or not
sub printResEqualOrNot {
	$testNum = $_[0];
	my $value1 = $_[1];
	my $value2 = $_[2];
	$errMsg = $_[3];
	
	$retVal = 0;
	
	if ($value1 eq $value2) {
		print "[${testNum}] : OK\n";
	} else {
		print "[${testNum}] : NG";
		if ($errMsg ne "") {
			print "\t($errMsg)\n";
		} else {
			print "\n";
		}
		$retVal = -1;
	}
	
	return $retVal;
}

## function print not available
sub printResNotAvailable {
	$testNum = $_[0];
	$errMsg = $_[1];
	
	print "[${testNum}] : N/A";
	if ($errMsg ne "") {
		print "\t($errMsg)\n";
	} else {
		print "\n";
	}
}


## function check file exsiting
sub checkFileExisting {
	my $targetFilePath = $_[0];
	unless (-f $targetFilePath) {
		print "invalid File Path : $targetFilePath\n";
		die;
	}
}

1;