package jp.go.nict.common.LicenseManager;

import java.util.Date;
import java.util.Calendar;

/**
 * @author Kimura Noriyuki
 * @version 2.00
 * @since 2011/06/10
 */
public class SimpleLicenseCertification {
    static boolean mFlagCertification = false;
    static boolean mFlagExecDateCheck = false;

    static final int LICENSE_YEAR = 2020;
    static final int LICENSE_MONTH = 4;
    static final int LICENSE_DATE = 30;

    public SimpleLicenseCertification() {
    }

    public static boolean isCertification() {
        if (!mFlagExecDateCheck) {
            mFlagCertification = isDateCheck();
            mFlagExecDateCheck = true;
        }

        if (!mFlagCertification) {
            System.out.println("The license file exceeded time limit.");
            System.exit(1);
        }

        return mFlagCertification;
    }

    private static boolean isDateCheck() {
        Date dToday = new Date();
        Calendar cLicenseLimitDate = Calendar.getInstance();

        cLicenseLimitDate.set(LICENSE_YEAR, LICENSE_MONTH - 1, LICENSE_DATE);

        Date dLicenseLimitDate = cLicenseLimitDate.getTime();

        if (dLicenseLimitDate.compareTo(dToday) < 0) {
            return false;
        } else {
            return true;
        }
    }

}
