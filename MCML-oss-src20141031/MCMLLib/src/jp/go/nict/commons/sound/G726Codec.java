package jp.go.nict.commons.sound;

import java.nio.ByteBuffer;

/**
 * STMD G711 Codec.
 * Title: STMD G711 Codec.
 * @author Kimura Noriyuki
 * @version 2.00
 * @since 2011/06/10
 */
public class G726Codec extends Object {
    /** a Law on log-PCM(g.711) algorism. */
    private int law;

    /* State for G726 encoder and decoder parameters. */
    private short sr0, sr1; /* Reconstructed signal with delays 0 and 1 */

    private short a1r, a2r; /* Triggered 2nd order predictor coeffs. */

    private short b1r; /* Triggered 6nd order predictor coeffs */

    private short b2r;

    private short b3r;

    private short b4r;

    private short b5r;

    private short b6r;

    private short dq5; /* Quantized difference signal with delays 5 to 0 */

    private short dq4;

    private short dq3;

    private short dq2;

    private short dq1;

    private short dq0;

    private short dmsp; /* Short term average of the F(I) sequence */

    private short dmlp; /* Long term average of the F(I) sequence */

    private short apr; /* Triggered unlimited speed control parameter */

    private short yup; /* Fast quantizer scale factor */

    private short tdr; /* Triggered tone detector */

    private short pk0, pk1; /* sign of dq+sez with delays 0 and 1 */

    private long ylp; /* Slow quantizer scale factor */

    /**
     * Default Constructor.
     */
    public G726Codec() {
        super();
        /* The Default Law is u-law */
        this.law = G711Codec.U_LAW;
    }

    /**
     * Constructor.
     */
    public G726Codec(int law) {
        super();
        this.law = law;
    }

    /**
     * Setter of a Law on log-PCM(g.711).
     */
    public void setLaw(int logPcmLaw) {
        this.law = logPcmLaw;
    }

    /**
     * Getter of a Law on log-PCM(g.711).
     */
    public int getLaw() {
        return this.law;
    }

    /**
     * Encode method.
     */
    public ByteBuffer encode(ByteBuffer targetByteBuffer, int law,
            short bitrate, boolean doReset) {
        if (!this.isValidBitRate(bitrate)) {
            return null;
        }

        /* Input Buffer */
//        ByteBuffer inputByteBuffer = targetByteBuffer.asReadOnlyBuffer();
//        inputByteBuffer.clear();

        /* Output Buffer */
//        ByteBuffer outputByteBuffer = ByteBuffer.allocate(targetByteBuffer
//                .capacity() * 2);
//        outputByteBuffer = outputByteBuffer.order(targetByteBuffer.order());
//        ShortBuffer outputShortBuffer = outputByteBuffer.asShortBuffer();
        
        
        /* Input Buffer */
        ByteBuffer inputByteBuffer = targetByteBuffer.duplicate()
                .asReadOnlyBuffer();
        inputByteBuffer.order(targetByteBuffer.order());
        
        /* Buffer with Linear Samples for Output */
        ByteBuffer outputByteBuffer = ByteBuffer.allocate(inputByteBuffer
                .remaining());
        outputByteBuffer.order(targetByteBuffer.order());
        outputByteBuffer.clear();
        

        short s = 0;
        short d = 0, i = 0;
        short y = 0;
        short sigpk = 0;
        short sr = 0, tr = 0;
        short yu = 0;
        short al = 0, fi = 0, dl = 0, ap = 0, dq = 0, ds = 0, se = 0, ax = 0, td = 0, sl = 0, wi = 0;
        short u1 = 0, u2 = 0, u3 = 0, u4 = 0, u5 = 0, u6 = 0;
        short a1 = 0, a2 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0;
        short dqln = 0;
        short a1p = 0, a2p = 0, a1t = 0, a2t = 0, b1p = 0, b2p = 0, b3p = 0, b4p = 0, b5p = 0, b6p = 0, dq6 = 0, pk2 = 0, sr2 = 0, wa1 = 0, wa2 = 0, wb1 = 0, wb2 = 0, wb3 = 0, wb4 = 0, wb5 = 0, wb6 = 0;
        short dml = 0, dln = 0, app = 0, dql = 0, dms = 0;
        short dqs = 0, tdp = 0;
        short sez = 0;
        short yut = 0;
        long yl = 0;
        
        @SuppressWarnings("unused")
        long j = 0;
        
        short[] returnValueArray = { 0, 0 };

        /* Invert even bits if A law */
        // if(law == G711Codec.A_LAW)
        // {
        // for(j = 0; j < smpno; j++)
        // targetByteBuffer[j] ^= 85;
        // }
        /*
         * Process all desired samples in targetByteBuffer to out_buf; The
         * comments about general blocks are given as in G.726, and refer to:
         * 4.1.1 Input PCM format conversion and difference signal computation
         * 4.1.2 Adaptive quantizer 4.1.3 Inverse adaptive quantizer 4.1.4
         * Quantizer scale factor adaptation 4.1.5 Adaptation speed control
         * 4.1.6 Adaptive predictor and reconstructed signal calculator 4.1.7
         * Tone and transition detector 4.1.8 (Only in the decoder)
         */
        for (; inputByteBuffer.hasRemaining(); doReset = false) {
//dpsc      s = (short) (0x000000FF & inputByteBuffer.get());
        	s = (short) (0x000000FF & inputByteBuffer.getShort());

            /* Process `known-state' part of 4.2.6 */
            sr2 = delayd(doReset, this.sr1);
            this.sr1 = delayd(doReset, this.sr0);
            a2 = delaya(doReset, this.a2r);
            a1 = delaya(doReset, this.a1r);
            wa2 = fmult(a2, sr2);
            wa1 = fmult(a1, this.sr1);

            dq6 = delayd(doReset, this.dq5);
            this.dq5 = delayd(doReset, this.dq4);
            this.dq4 = delayd(doReset, this.dq3);
            this.dq3 = delayd(doReset, this.dq2);
            this.dq2 = delayd(doReset, this.dq1);
            this.dq1 = delayd(doReset, this.dq0);

            b1 = delaya(doReset, this.b1r);
            b2 = delaya(doReset, this.b2r);
            b3 = delaya(doReset, this.b3r);
            b4 = delaya(doReset, this.b4r);
            b5 = delaya(doReset, this.b5r);
            b6 = delaya(doReset, this.b6r);

            wb1 = fmult(b1, this.dq1);
            wb2 = fmult(b2, this.dq2);
            wb3 = fmult(b3, this.dq3);
            wb4 = fmult(b4, this.dq4);
            wb5 = fmult(b5, this.dq5);
            wb6 = fmult(b6, dq6);

            returnValueArray = accum(wa1, wa2, wb1, wb2, wb3, wb4, wb5, wb6);
            se = returnValueArray[0];
            sez = returnValueArray[1];

            /* Process 4.2.1 */
            sl = expand(s, law);
            d = subta(sl, se);

            /* Process delays and `know-state' part of 4.2.5 */
            dms = delaya(doReset, this.dmsp);
            dml = delaya(doReset, this.dmlp);
            ap = delaya(doReset, this.apr);
            al = lima(ap);

            /* Process `know-state' parts of 4.2.4 */
            yu = delayb(doReset, this.yup);
            yl = delayc(doReset, this.ylp);
            y = mix(al, yu, yl);

            /* Process 4.2.2 */
            returnValueArray = log(d);
            dl = returnValueArray[0];
            ds = returnValueArray[1];
            dln = subtb(dl, y);
            i = quan(bitrate, dln, ds);

            /* Save ADPCM quantized sample into output buffer */
//            outputShortBuffer.put(i);
            outputByteBuffer.putShort(i);

            /* Process 4.2.3 */
            returnValueArray = reconst(bitrate, i);
            dqln = returnValueArray[0];
            dqs = returnValueArray[1];
            dql = adda(dqln, y);
            dq = antilog(dql, dqs);

            /* Part of 4.2.5 */
            fi = functf(bitrate, i);
            this.dmsp = filta(fi, dms);
            this.dmlp = filtb(fi, dml);

            /* Remaining part of 4.2.4 */
            wi = functw(bitrate, i);
            yut = filtd(wi, y);
            this.yup = limb(yut);
            this.ylp = filte(this.yup, yl);

            /* Process `known-state' part of 4.2.7 */
            td = delaya(doReset, this.tdr);
            tr = trans(td, yl, dq);

            /* More `known-state' parts of 4.2.6: update of `pk's */
            pk2 = delaya(doReset, this.pk1);
            this.pk1 = delaya(doReset, this.pk0);
            returnValueArray = addc(dq, sez);
            this.pk0 = returnValueArray[0];
            sigpk = returnValueArray[1];

            /* 4.2.6: find sr0 */
            sr = addb(dq, se);
            this.sr0 = floatb(sr);

            /* 4.2.6: find dq0 */
            this.dq0 = floata(dq);

            /* 4.2.6: prepar a2(r) */
            a2t = upa2(this.pk0, this.pk1, pk2, a2, a1, sigpk);
            a2p = limc(a2t);
            this.a2r = trigb(tr, a2p);

            /* 4.2.6: prepar a1(r) */
            a1t = upa1(this.pk0, this.pk1, a1, sigpk);
            a1p = limd(a1t, a2p);
            this.a1r = trigb(tr, a1p);

            /* Remaining of 4.2.7 */
            tdp = tone(a2p);
            this.tdr = trigb(tr, tdp);

            /* Remaining of 4.2.5 */
            ax = subtc(this.dmsp, this.dmlp, tdp, y);
            app = filtc(ax, ap);
            this.apr = triga(tr, app);

            /* Remaining of 4.2.6: update of all `b's */
            u1 = xor(this.dq1, dq); /* Here, b1 */
            b1p = upb(bitrate, u1, b1, dq);
            this.b1r = trigb(tr, b1p);

            u2 = xor(this.dq2, dq); /* Here, b2 */
            b2p = upb(bitrate, u2, b2, dq);
            this.b2r = trigb(tr, b2p);

            u3 = xor(this.dq3, dq); /* Here, b3 */
            b3p = upb(bitrate, u3, b3, dq);
            this.b3r = trigb(tr, b3p);

            u4 = xor(this.dq4, dq); /* Here, b4 */
            b4p = upb(bitrate, u4, b4, dq);
            this.b4r = trigb(tr, b4p);

            u5 = xor(this.dq5, dq); /* Here, b5 */
            b5p = upb(bitrate, u5, b5, dq);
            this.b5r = trigb(tr, b5p);

            u6 = xor(dq6, dq); /* At last, b6 */
            b6p = upb(bitrate, u6, b6, dq);
            this.b6r = trigb(tr, b6p);
        }

        return outputByteBuffer;
    }

    /**
     * Decode method.
     */
    public ByteBuffer decode(ByteBuffer targetByteBuffer, int law,
            short bitrate, boolean doReset) {
        if (!this.isValidBitRate(bitrate)) {
            return null;
        }

        /* Input Buffer */
        ByteBuffer inputByteBuffer = targetByteBuffer.duplicate()
                .asReadOnlyBuffer();
        inputByteBuffer.order(targetByteBuffer.order());

        short y = 0;
        short sigpk = 0;
        short sr = 0, tr = 0;
        /* these are unique to the decoder */
        short sp = 0, dlnx = 0, dsx = 0, sd = 0, slx = 0, dlx = 0, dx = 0;
        long yl = 0;
        short yu = 0;
        short al = 0, fi = 0, ap = 0, dq = 0, se = 0, ax = 0, td = 0, wi = 0;
        short u1 = 0, u2 = 0, u3 = 0, u4 = 0, u5 = 0, u6 = 0;
        short a1 = 0, a2 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0;
        short dqln = 0;
        short a1p = 0, a2p = 0, a1t = 0, a2t = 0, b1p = 0, b2p = 0, b3p = 0, b4p = 0, b5p = 0, b6p = 0, dq6 = 0, pk2 = 0, sr2 = 0, wa1 = 0, wa2 = 0, wb1 = 0, wb2 = 0, wb3 = 0, wb4 = 0, wb5 = 0, wb6 = 0;
        short dml = 0, app = 0, dql = 0, dms = 0;
        short dqs = 0, tdp = 0;
        short sez = 0;
        short yut = 0;

        /* Buffer with Linear Samples for Output */
        ByteBuffer outputByteBuffer = ByteBuffer.allocate(inputByteBuffer
                .remaining());
        outputByteBuffer.order(targetByteBuffer.order());
        outputByteBuffer.clear();

        while (inputByteBuffer.hasRemaining()) {
            /* Process `known-state' part of 4.2.6 */
            sr2 = delayd(doReset, sr1);
            sr1 = delayd(doReset, sr0);
            a2 = delaya(doReset, a2r);
            a1 = delaya(doReset, a1r);
            wa2 = fmult(a2, sr2);
            wa1 = fmult(a1, sr1);

            dq6 = delayd(doReset, dq5);
            dq5 = delayd(doReset, dq4);
            dq4 = delayd(doReset, dq3);
            dq3 = delayd(doReset, dq2);
            dq2 = delayd(doReset, dq1);
            dq1 = delayd(doReset, dq0);

            b1 = delaya(doReset, b1r);
            b2 = delaya(doReset, b2r);
            b3 = delaya(doReset, b3r);
            b4 = delaya(doReset, b4r);
            b5 = delaya(doReset, b5r);
            b6 = delaya(doReset, b6r);

            wb1 = fmult(b1, dq1);
            wb2 = fmult(b2, dq2);
            wb3 = fmult(b3, dq3);
            wb4 = fmult(b4, dq4);
            wb5 = fmult(b5, dq5);
            wb6 = fmult(b6, dq6);

            short[] returnValuesArray = accum(wa1, wa2, wb1, wb2, wb3, wb4,
                    wb5, wb6);
            se = returnValuesArray[0];
            sez = returnValuesArray[1];

            /* Process delays and `know-state' part of 4.2.5 */
            dms = delaya(doReset, dmsp);
            dml = delaya(doReset, dmlp);
            ap = delaya(doReset, apr);
            al = lima(ap);

            /* Process `know-state' parts of 4.2.4 */
            yu = delayb(doReset, yup);
            yl = delayc(doReset, ylp);
            y = mix(al, yu, yl);

            /* Retrieve ADPCM sample from input buffer */
            short sampleValue = inputByteBuffer.getShort();
            /* Process 4.2.3 */
            returnValuesArray = reconst(bitrate, sampleValue);
            dqln = returnValuesArray[0];
            dqs = returnValuesArray[1];
            dql = adda(dqln, y);
            dq = antilog(dql, dqs);

            /* Process `known-state' part of 4.2.7 */
            td = delaya(doReset, tdr);
            tr = trans(td, yl, dq);

            /* Part of 4.2.5 */
            fi = functf(bitrate, sampleValue);
            dmsp = filta(fi, dms);
            dmlp = filtb(fi, dml);

            /* Remaining part of 4.2.4 */
            wi = functw(bitrate, sampleValue);
            yut = filtd(wi, y);
            yup = limb(yut);
            ylp = filte(yup, yl);

            /* More `known-state' parts of 4.2.6: update of `pk's */
            pk2 = delaya(doReset, pk1);
            pk1 = delaya(doReset, pk0);
            returnValuesArray = addc(dq, sez);
            pk0 = returnValuesArray[0];
            sigpk = returnValuesArray[1];

            /* 4.2.6: find sr0 */
            sr = addb(dq, se);
            sr0 = floatb(sr);

            /* 4.2.6: find dq0 */
            dq0 = floata(dq);

            /* Process 4.2.8 */
            sp = compress(sr, law);
            slx = expand(sp, law);
            dx = subta(slx, se);
            returnValuesArray = log(dx);
            dlx = returnValuesArray[0];
            dsx = returnValuesArray[1];
            dlnx = subtb(dlx, y);
            sd = sync(bitrate, sampleValue, sp, dlnx, dsx, law);

            /* Save output PCM word in output buffer */
            outputByteBuffer.putShort(sd);

            /* 4.2.6: prepar a2(r) */
            a2t = upa2(pk0, pk1, pk2, a2, a1, sigpk);
            a2p = limc(a2t);
            a2r = trigb(tr, a2p);

            /* 4.2.6: prepar a1(r) */
            a1t = upa1(pk0, pk1, a1, sigpk);
            a1p = limd(a1t, a2p);
            a1r = trigb(tr, a1p);

            /* Remaining of 4.2.7 */
            tdp = tone(a2p);
            tdr = trigb(tr, tdp);

            /* Remaining of 4.2.5 */
            ax = subtc(dmsp, dmlp, tdp, y);
            app = filtc(ax, ap);
            apr = triga(tr, app);

            /* Remaining of 4.2.6: update of all `b's */
            u1 = xor(dq1, dq); /* Here, b1 */
            b1p = upb(bitrate, u1, b1, dq);
            b1r = trigb(tr, b1p);

            u2 = xor(dq2, dq); /* Here, b2 */
            b2p = upb(bitrate, u2, b2, dq);
            b2r = trigb(tr, b2p);

            u3 = xor(dq3, dq); /* Here, b3 */
            b3p = upb(bitrate, u3, b3, dq);
            b3r = trigb(tr, b3p);

            u4 = xor(dq4, dq); /* Here, b4 */
            b4p = upb(bitrate, u4, b4, dq);
            b4r = trigb(tr, b4p);

            u5 = xor(dq5, dq); /* Here, b5 */
            b5p = upb(bitrate, u5, b5, dq);
            b5r = trigb(tr, b5p);

            u6 = xor(dq6, dq); /* At last, b6 */
            b6p = upb(bitrate, u6, b6, dq);
            b6r = trigb(tr, b6p);

            doReset = false;
        }

        outputByteBuffer.flip();

        return outputByteBuffer;
    }

    /**
     * Private method to check bit rate.
     */
    private boolean isValidBitRate(short bitrate) {
        return ((bitrate == ADPCMCodec.BITRATE_16K)
                || (bitrate == ADPCMCodec.BITRATE_24K)
                || (bitrate == ADPCMCodec.BITRATE_32K) || (bitrate == ADPCMCodec.BITRATE_40K));
    }

    /**
     * Private calculattion method for Codec.
     */
    private short delaya(boolean doReset, short value) {
        return (!doReset) ? value : (short) 0;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short delayb(boolean doReset, short x) {
        return (!doReset) ? x : (short) 544;
    }

    /**
     * Private calculattion method for Codec.
     */
    private long delayc(boolean doReset, long x) {
        return (!doReset) ? x : (long) 34816;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short delayd(boolean doReset, short value) {
        return (!doReset) ? value : (short) 32;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short lima(short ap) {
        return (ap >= 256) ? (short) 64 : (short) (ap >> 2);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short[] accum(short wa1, short wa2, short wb1, short wb2,
            short wb3, short wb4, short wb5, short wb6) {
        long sezi;
        long wa11, wa21, wb11, wb21, wb31, wb41, wb51, wb61, sei;

        /* Preamble */
        wa11 = (long) wa1;
        wa21 = (long) wa2;
        wb11 = (long) wb1;
        wb21 = (long) wb2;
        wb31 = (long) wb3;
        wb41 = (long) wb4;
        wb51 = (long) wb5;
        wb61 = (long) wb6;

        /* Sum of partial signal estimate */
        sezi = (((((((((wb11 + wb21) & 65535) + wb31) & 65535) + wb41) & 65535) + wb51) & 65535) + wb61) & 65535;

        /* Complete sum for signal estimate */
        sei = (((sezi + wa21) & 65535) + wa11) & 65535;

        /* Array to return values */
        short[] returnValuesArray = { (short) (sei >> 1), (short) (sezi >> 1) };

        return returnValuesArray;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short fmult(short An, short SRn) {
        /* Parameters */
        long anmag, anexp, wanmag, anmant;
        long wanexp, srnexp, an, ans, wanmant, srnmant;
        long wan, wans, srns, srn1;

        /* Preamble */
        an = An & 65535;
        srn1 = SRn & 65535;

        /* Sign */
        ans = an & 32768;
        ans = (ans >> 15);

        /* Convert 2's complement to signed magnitude */
        anmag = (ans == 0) ? (an >> 2) : ((16384 - (an >> 2)) & 8191);

        /* Exponent */
        if (anmag >= 4096)
            anexp = 13;
        else if (anmag >= 2048)
            anexp = 12;
        else if (anmag >= 1024)
            anexp = 11;
        else if (anmag >= 512)
            anexp = 10;
        else if (anmag >= 256)
            anexp = 9;
        else if (anmag >= 128)
            anexp = 8;
        else if (anmag >= 64)
            anexp = 7;
        else if (anmag >= 32)
            anexp = 6;
        else if (anmag >= 16)
            anexp = 5;
        else if (anmag >= 8)
            anexp = 4;
        else if (anmag >= 4)
            anexp = 3;
        else if (anmag >= 2)
            anexp = 2;
        else if (anmag == 1)
            anexp = 1;
        else
            anexp = 0;

        /* Compute mantissa w/a 1 in the most sig. bit */
        anmant = (anmag == 0) ? (1 << 5) : ((anmag << 6) >> anexp);

        /* Split floating point word into sign, exponent and mantissa */
        srns = (srn1 >> 10);
        srnexp = (srn1 >> 6) & 15;
        srnmant = srn1 & 63;

        /* Floating point multiplication */
        wans = srns ^ ans;
        wanexp = srnexp + anexp;
        wanmant = ((srnmant * anmant) + 48) >> 4;

        /* Convert floating point to magnitude */
        wanmag = (wanexp <= 26) ? (wanmant << 7) >> (26 - wanexp)
                : ((wanmant << 7) << (wanexp - 26)) & 32767;

        /* Convert mag. to 2's complement */
        wan = (wans == 0) ? wanmag : ((65536 - wanmag) & 65535);

        return (short) wan;

    }

    /**
     * Private calculattion method for Codec.
     */
    private short mix(short al, short yu, long yl) {
        /* Parameters */
        long difm, difs, prod;
        
        @SuppressWarnings("unused")
        long prodm, al1;
        @SuppressWarnings("unused")
        long yu1, dif;

        /* Compute difference */
        dif = (yu + 16384 - (yl >> 6)) & 16383;
        difs = (dif >> 13);

        /* Compute magnitude of difference */
        difm = (difs == 0) ? dif : ((16384 - dif) & 8191);

        /* Compute magnitude of product */
        prodm = ((difm * al) >> 6);

        /* Convert magnitude to two's complement */
        prod = (difs == 0) ? prodm : ((16384 - prodm) & 16383);

        return (short) (((yl >> 6) + prod) & 8191);
    }

    /** Initialized data Tables for reconst method. */
    static short reconstTable[][] = {
            { 116, 365, 365, 116 },
            { 2048, 135, 273, 373, 373, 273, 135, 2048 },
            { 2048, 4, 135, 213, 273, 323, 373, 425, 425, 373, 323, 273, 213,
                    135, 4, 2048 },
            { 2048, 4030, 28, 104, 169, 224, 274, 318, 358, 395, 429, 459, 488,
                    514, 539, 566, 566, 539, 514, 488, 459, 429, 395, 358, 318,
                    274, 224, 169, 104, 28, 4030, 2048 } };

    /**
     * Private calculattion method for Codec.
     */
    private short[] reconst(short bitrate, short i) {
        /* Return values Array */
        short[] returnValuesArray = new short[2];

        if (bitrate == ADPCMCodec.BITRATE_32K) {
            /* Extract sign */
            returnValuesArray[1] = (short) (i >> 3);
            /* Table look-up */
            returnValuesArray[0] = reconstTable[2][i];
        }
        /* ............... end of 32 kbit part ................. */
        else if (bitrate == ADPCMCodec.BITRATE_24K) {
            /* Extract sign */
            returnValuesArray[1] = (short) (i >> 2);
            /* Table look-up */
            returnValuesArray[0] = reconstTable[1][i];
        }
        /* ............... end of 24 kbit part ................. */
        else if (bitrate == ADPCMCodec.BITRATE_16K) {
            /* Extract sign */
            returnValuesArray[1] = (short) (i >> 1);
            /* Table look-up */
            returnValuesArray[0] = reconstTable[0][i];
        }
        /* ............... end of 16 kbit part ................. */
        else {
            /* Extract sign */
            returnValuesArray[1] = (short) (i >> 4);
            /* Table look-up */
            returnValuesArray[0] = reconstTable[3][i];
        }
        /* ................ end of 40 kbit part ................... */

        return returnValuesArray;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short adda(short dqln, short y) {
        return (short) ((dqln + (y >> 2)) & 4095);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short antilog(short dql, short dqs) {
        long dqmag;
        long ds, dmn, dex, dqt;

        /* Extract 4-bit exponent */
        ds = (dql >> 11);
        dex = (dql >> 7) & 15;

        /* Extract 7-bit mantissa */
        dmn = dql & 127;

        dqt = dmn + 128;

        /* Convert mantissa to linear using the approx. 2**x = 1+x */
        dqmag = (ds != 0) ? 0 : ((dqt << 7) >> (14 - dex));

        /* Attach sign bit to signed mag. word */
        return (short) ((short) (dqs << 15) + dqmag);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short trans(short td, long yl, short dq) {
        short dqmag;
        long dqthr;
        short ylint;
        long dqmag1;
        short ylfrac;
        long thr1, thr2;

        dqmag = (short) (dq & 32767);

        ylint = (short) (yl >> 15);

        ylfrac = (short) ((yl >> 10) & 31);

        thr1 = (ylfrac + 32) << ylint;

        thr2 = (ylint > 9) ? 31744 : thr1;

        dqthr = (thr2 + (thr2 >> 1)) >> 1;

        dqmag1 = dqmag;

        return (short) ((dqmag1 > dqthr && td == 1) ? 1 : 0);
    }

    /** Initialized data Tables for functf method. */
    static short functfTable[][] = { { 0, 0, 0, 1, 1, 1, 3, 7 },
            { 0, 1, 2, 7 }, { 0, 7 },
            { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 3, 4, 5, 6, 6 } };

    /**
     * Private calculattion method for Codec.
     */
    private short functf(short bitrate, short i) {
        short im;
        short is;
        short fi;

        if (bitrate == ADPCMCodec.BITRATE_32K) {
            is = (short) (i >> 3);

            im = (short) ((is == 0) ? (i & 7) : ((15 - i) & 7));

            fi = functfTable[0][im];
        }
        /* ................ end of 32 kbit part ................. */

        else if (bitrate == ADPCMCodec.BITRATE_24K) {
            is = (short) (i >> 2);

            im = (short) ((is == 0) ? (i & 3) : ((7 - i) & 3));

            fi = functfTable[1][im];
        }
        /* ................ end of 24 kbit part ................. */
        else if (bitrate == ADPCMCodec.BITRATE_16K) {
            is = (short) (i >> 1);

            im = (short) ((is == 0) ? (i & 1) : ((3 - i) & 1));

            fi = functfTable[2][im];
        }
        /* ................ end of 16 kbit part ................. */

        else {
            is = (short) (i >> 4);

            im = (short) ((is == 0) ? (i & 15) : ((31 - i) & 15));

            fi = functfTable[3][im];
        }
        /* ................ end of 40 kbit part ................. */

        return fi;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short filta(short fi, short dms) {
        short difs, difsx;
        short dif;

        /* Compute difference */
        dif = (short) (((fi << 9) + 8192 - dms) & 8191);
        difs = (short) (dif >> 12);

        /* Time constant is 1/32, sign extension */
        difsx = (short) ((difs == 0) ? (dif >> 5) : ((dif >> 5) + 3840));

        return (short) (((difsx + dms) & 4095));
    }

    /**
     * Private calculattion method for Codec.
     */
    private short filtb(short fi, short dml) {
        long difs, difsx;
        long fi1;
        long dif, dml1;

        /* Preamble */
        fi1 = fi;
        dml1 = dml;

        /* Compute difference */
        dif = ((fi1 << 11) + 32768 - dml1) & 32767;
        difs = (dif >> 14);

        /* Time constant is 1/28, sign extension */
        difsx = (difs == 0) ? (dif >> 7) : ((dif >> 7) + 16128);

        return (short) ((difsx + dml1) & 16383);
    }

    /** Initialized data Tables for functf method. */
    static short functwTable[][] = {
            { 4084, 18, 41, 64, 112, 198, 355, 1122 },
            { 4092, 30, 137, 582 },
            { 4074, 439 },
            { 14, 14, 24, 39, 40, 41, 58, 100, 141, 179, 219, 280, 358, 440,
                    529, 696 } };

    /**
     * Private calculattion method for Codec.
     */
    private short functw(short bitrate, short i) {
        short wi;

        if (bitrate == ADPCMCodec.BITRATE_32K) {
            short im, is;

            is = (short) (i >> 3);

            im = (short) ((is == 0) ? (i & 7) : ((15 - i) & 7));

            /* Scale factor multiplier */
            wi = functwTable[0][im];
        }
        /* ................. end of 32 kbit part .............. */
        else if (bitrate == ADPCMCodec.BITRATE_24K) {
            short im, is;

            is = (short) (i >> 2);

            im = (short) ((is == 0) ? (i & 3) : ((7 - i) & 3));

            wi = functwTable[1][im];
        }
        /* ................. end of 24 kbit part .............. */
        else if (bitrate == ADPCMCodec.BITRATE_16K) {
            short im, is;

            is = (short) (i >> 1);

            im = (short) ((is == 0) ? (i & 1) : ((3 - i) & 1));

            wi = functwTable[2][im];
        }
        /* ................. end of 16 kbit part .............. */
        else {
            short im, is;

            is = (short) (i >> 4);

            im = (short) ((is == 0) ? (i & 15) : ((31 - i) & 15));

            wi = functwTable[3][im];
        }
        /* ................. end of 40 kbit part .............. */

        return wi;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short filtd(short wi, short y) {
        long difs, difsx;
        long y1;
        long wi1, dif;

        /* Compute difference */
        wi1 = wi;
        y1 = y;
        dif = ((wi1 << 5) + 131072 - y1) & 131071;
        difs = (dif >> 16);

        /* Time constant is 1/32; sign extension */
        difsx = (difs == 0) ? (dif >> 5) : ((dif >> 5) + 4096);

        return (short) ((y1 + difsx) & 8191);

    }

    /**
     * Private calculattion method for Codec.
     */
    private short limb(short yut) {
        short gell, geul;
        short yup;

        geul = (short) (((yut + 11264) & 16383) >> 13);
        gell = (short) (((yut + 15840) & 16383) >> 13);

        if (gell == 1)
            yup = 544; /* Lower limit is 1.06 */
        else if (geul == 0)
            yup = 5120; /* Upper limit is 10.0 */
        else
            yup = yut;

        return yup;
    }

    /**
     * Private calculattion method for Codec.
     */
    private long filte(short yup, long yl) {
        long difs, difsx;
        long dif, dif1, yup1;

        /* Compute difference */
        yup1 = yup;
        dif1 = 1048576 - yl;
        dif = (yup1 + (dif1 >> 6)) & 16383;
        difs = (dif >> 13);

        /* Sign extension */
        difsx = (difs == 0) ? dif : (dif + 507904);

        return (yl + difsx) & 524287;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short[] addc(short dq, short sez) {
        /* Return values Array */
        short[] returnValuesArray = new short[2];

        long sezi;
        short sezs;
        long dqsez, dq1;
        long dqi;
        short dqs;
        long sez1;

        /* Preamble */
        dq1 = dq & 65535;
        sez1 = sez;

        /* Get sign */
        dqs = (short) ((dq >> 15) & 1);

        /* Convert signed magnitude to 2's compelemnent */
        dqi = (dqs == 0) ? dq1 : ((65536 - (dq1 & 32767)) & 65535);

        sezs = (short) ((sez >> 14));

        /* Sign extension */
        sezi = (sezs == 0) ? sez1 : (sez1 + 32768);

        dqsez = (dqi + sezi) & 65535;

        returnValuesArray[0] = (short) (dqsez >> 15);
        returnValuesArray[1] = (short) ((dqsez == 0) ? 1 : 0);

        return returnValuesArray;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short addb(short dq, short se) {
        long dq1, se1;
        long dqi, sei;
        short dqs, ses;

        /* Preamble */
        dq1 = dq & 65535;
        se1 = se;

        /* Sign */
        dqs = (short) ((dq >> 15) & 1);

        /* Convert signed magnitude to 2's complement */
        dqi = (dqs == 0) ? dq1 : ((65536 - (dq1 & 32767)) & 65535);

        ses = (short) (se >> 14);

        /* Sign extension */
        sei = (ses == 0) ? se1 : ((1 << 15) + se1);

        return (short) ((dqi + sei) & 65535);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short floatb(short sr) {
        long mant;
        long mag, exp_, srr, srs;

        /* Preamble */
        srr = sr & 65535;

        /* Sign */
        srs = (srr >> 15);

        /* Compute magnitude */
        mag = (srs == 0) ? srr : ((65536 - srr) & 32767);

        /* Exponent */
        if (mag >= 16384)
            exp_ = 15;
        else if (mag >= 8192)
            exp_ = 14;
        else if (mag >= 4096)
            exp_ = 13;
        else if (mag >= 2048)
            exp_ = 12;
        else if (mag >= 1024)
            exp_ = 11;
        else if (mag >= 512)
            exp_ = 10;
        else if (mag >= 256)
            exp_ = 9;
        else if (mag >= 128)
            exp_ = 8;
        else if (mag >= 64)
            exp_ = 7;
        else if (mag >= 32)
            exp_ = 6;
        else if (mag >= 16)
            exp_ = 5;
        else if (mag >= 8)
            exp_ = 4;
        else if (mag >= 4)
            exp_ = 3;
        else if (mag >= 2)
            exp_ = 2;
        else if (mag == 1)
            exp_ = 1;
        else
            exp_ = 0;

        /* Compute mantissa w/a 1 in the most sig. bit */
        mant = (mag == 0) ? (1 << 5) : ((mag << 6) >> exp_);

        /* Combine sign, exponent and mantissa (1,4,6) bits in a word */
        return (short) ((srs << 10) + (exp_ << 6) + mant);

    }

    /**
     * Private calculattion method for Codec.
     */
    private short floata(short dq) {
        long mant;
        long mag, exp_;
        long dqs;

        dqs = (dq >> 15) & 1;

        /* Compute magnitude */
        mag = dq & 32767;

        /* Exponent */
        if (mag >= 16384)
            exp_ = 15;
        else if (mag >= 8192)
            exp_ = 14;
        else if (mag >= 4096)
            exp_ = 13;
        else if (mag >= 2048)
            exp_ = 12;
        else if (mag >= 1024)
            exp_ = 11;
        else if (mag >= 512)
            exp_ = 10;
        else if (mag >= 256)
            exp_ = 9;
        else if (mag >= 128)
            exp_ = 8;
        else if (mag >= 64)
            exp_ = 7;
        else if (mag >= 32)
            exp_ = 6;
        else if (mag >= 16)
            exp_ = 5;
        else if (mag >= 8)
            exp_ = 4;
        else if (mag >= 4)
            exp_ = 3;
        else if (mag >= 2)
            exp_ = 2;
        else if (mag == 1)
            exp_ = 1;
        else
            exp_ = 0;

        /* Compute mantissa w/a 1 in the most sig. bit */
        mant = (mag == 0) ? (1 << 5) : ((mag << 6) >> exp_);
        /* Combine sign, exponent and mantissa (1,4,6) bits in a word */
        return (short) ((dqs << 10) + (exp_ << 6) + mant);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short compress(short sr, int law) {
        short sp;
        short imag, iesp, ofst;
        short ofst1 = 0;
        long i;
        long im;
        short is;
        long srr;

        is = (short) (sr >> 15);
        srr = (sr & 65535);

        /* Convert 2-complement to signed magnitude */
        im = (is == 0) ? srr : ((65536 - srr) & 32767);

        /* Compress ... */
        if (law == G711Codec.A_LAW) {

            /* Next line added by J.Patel to fix a with test vector ri40fa.o */
            im = (sr == -32768) ? 2 : im; /* *** */

            imag = (short) ((is == 0) ? (im >> 1) : ((im + 1) >> 1));

            if (is != 0) {
                --imag;
            }

            /* Saturation */
            if (imag > 4095)
                imag = 4095;

            iesp = 7;
            for (i = 1; i <= 7; ++i) {
                imag += imag;
                if (imag >= 4096)
                    break;
                iesp = (short) (7 - i);
            }

            imag &= 4095;

            imag = (short) (imag >> 8);
            sp = (short) ((is == 0) ? imag + (iesp << 4) : imag + (iesp << 4)
                    + 128);

            /* Sign bit inversion */
            sp ^= 128;
        } else {
            imag = (short) im;

            if (imag > 8158)
                imag = 8158; /* Saturation */

            ++imag;
            iesp = 0;
            ofst = 31;

            if (imag > ofst) {
                for (iesp = 1; iesp <= 8; ++iesp) {
                    ofst1 = ofst;
                    ofst += (1 << (iesp + 5));
                    if (imag <= ofst)
                        break;
                }
                imag -= ofst1 + 1;
            }

            imag /= (1 << (iesp + 1));

            sp = (short) ((is == 0) ? (imag + (iesp << 4)) : (imag
                    + (iesp << 4) + 128));

            /* Sign bit inversion */
            sp ^= 128;
            sp ^= 127;
        }

        return sp;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short expand(short s, int law) {
        long mant, iexp;
        short s1, ss, sig, ssm, ssq, sss;

        s1 = s;

        if (law == G711Codec.A_LAW) {
            /* Invert sign bit */
            s1 ^= 128;
            if (s1 >= 128) {
                s1 += -128;
                sig = 4096;
            } else {
                sig = 0;

            }
            iexp = s1 / 16;

            mant = s1 - (iexp << 4);
            ss = (short) ((iexp == 0) ? ((mant << 1) + 1 + sig)
                    : ((1 << (iexp - 1)) * ((mant << 1) + 33) + sig));

            sss = (short) (ss / 4096);
            ssm = (short) (ss & 4095);
            ssq = (short) (ssm << 1);
        } else {
            /* Invert sign bit */
            s1 ^= 128;
            if (s1 >= 128) {
                s1 += -128;
                s1 ^= 127;
                sig = 8192;
            } else {
                sig = 0;
                s1 ^= 127;
            }
            iexp = s1 / 16;

            mant = s1 - (iexp << 4);

            ss = (short) ((iexp == 0) ? ((mant << 1) + sig) : ((1 << iexp)
                    * ((mant << 1) + 33) - 33 + sig));

            sss = (short) (ss / 8192);
            ssq = (short) (ss & 8191);
        }

        return (short) ((sss == 0) ? ssq : ((16384 - ssq) & 16383));
    }

    /**
     * Private calculattion method for Codec.
     */
    private short subta(short sl, short se) {
        long se1;
        long sl1, sei, sli;
        short ses, sls;

        sls = (short) (sl >> 13);

        sl1 = sl;
        se1 = se;

        /* Sign extension */
        sli = (sls == 0) ? sl1 : (sl1 + 49152);

        ses = (short) (se >> 14);

        /* Sign extension */
        sei = (ses == 0) ? se1 : (se1 + 32768);

        /* 16 bit TC */
        return (short) ((sli + 65536 - sei) & 65535);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short[] log(short d) {
        /* Return values Array */
        short[] returnValuesArray = new short[2];

        short dl;
        short ds;

        long mant;
        long d1;
        long dqm, exp_;

        ds = (short) (d >> 15);

        d1 = d;

        /* Convert from 2-complement to signed magnitude */
        dqm = (ds != 0) ? ((65536 - d1) & 32767) : d1;

        /* Compute exponent */
        if (dqm >= 16384)
            exp_ = 14;
        else if (dqm >= 8192)
            exp_ = 13;
        else if (dqm >= 4096)
            exp_ = 12;
        else if (dqm >= 2048)
            exp_ = 11;
        else if (dqm >= 1024)
            exp_ = 10;
        else if (dqm >= 512)
            exp_ = 9;
        else if (dqm >= 256)
            exp_ = 8;
        else if (dqm >= 128)
            exp_ = 7;
        else if (dqm >= 64)
            exp_ = 6;
        else if (dqm >= 32)
            exp_ = 5;
        else if (dqm >= 16)
            exp_ = 4;
        else if (dqm >= 8)
            exp_ = 3;
        else if (dqm >= 4)
            exp_ = 2;
        else if (dqm >= 2)
            exp_ = 1;
        else
            exp_ = 0;

        /* Compute approximation log2(1+x) = x */
        mant = ((dqm << 7) >> exp_) & 127;

        /* Combine mantissa and exponent (7 and 4) bits into a 11-bit word */
        dl = (short) ((exp_ << 7) + mant);

        returnValuesArray[0] = dl;
        returnValuesArray[1] = ds;

        return returnValuesArray;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short subtb(short dl, short y) {
        return (short) ((dl + 4096 - (y >> 2)) & 4095);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short sync(short bitrate, short i, short sp, short dlnx, short dsx,
            int law) {
        short mask, id = 0, im, is, ss;

        if (bitrate == ADPCMCodec.BITRATE_32K) {
            is = (short) (i >> 3);

            im = (short) ((is == 0) ? (i + 8) : (i & 7));

            /* Find value of `id' as in Table 17/G.726 */
            if (dlnx >= 3972)
                id = 9;
            else if (dlnx >= 2048)
                id = 7;
            else if (dlnx >= 400)
                id = 15;
            else if (dlnx >= 349)
                id = 14;
            else if (dlnx >= 300)
                id = 13;
            else if (dlnx >= 246)
                id = 12;
            else if (dlnx >= 178)
                id = 11;
            else if (dlnx >= 80)
                id = 10;
            else
                id = 9;

            /* Account for the negative part of the table */
            if (dsx != 0) {
                id = (short) (15 - id);
            }

            if (id == 8)
                id = 7;
        }
        /* ............... end of 32 kbit part ................. */
        else if (bitrate == ADPCMCodec.BITRATE_24K) {
            is = (short) (i >> 2);

            im = (short) ((is == 0) ? (i + 4) : (i & 3));

            /* Find value of `id' as in the Table 18/G.726 */
            if (dlnx >= 2048)
                id = 3;
            else if (dlnx >= 331)
                id = 7;
            else if (dlnx >= 218)
                id = 6;
            else if (dlnx >= 8)
                id = 5;
            else if (dlnx >= 0)
                id = 3;

            if (dsx != 0) {
                id = (short) (7 - id);
            }

            if (id == 4)
                id = 3;
        }
        /* ............... end of 24 kbit part ................. */
        else if (bitrate == ADPCMCodec.BITRATE_16K) {
            is = (short) (i >> 1);

            im = (short) ((is == 0) ? (i + 2) : (i & 1));

            /* Find value of `id' as in the Table 19/G.726 */
            if (dlnx >= 2048)
                id = 2;
            else if (dlnx >= 261)
                id = 3;
            else if (dlnx >= 0)
                id = 2;

            if (dsx != 0) {
                id = (short) (3 - id);
            }

        }
        /* ............... end of 16 kbit part ................. */
        else {
            is = (short) (i >> 4);

            im = (short) ((is == 0) ? (i + 16) : (i & 15));

            /* Find value of `id' as in the Table 16/G.726 */

            if (dlnx >= 4080)
                id = 18;
            else if (dlnx >= 3974)
                id = 17;
            else if (dlnx >= 2048)
                id = 15;
            else if (dlnx >= 553)
                id = 31;
            else if (dlnx >= 528)
                id = 30;
            else if (dlnx >= 502)
                id = 29;
            else if (dlnx >= 475)
                id = 28;
            else if (dlnx >= 445)
                id = 27;
            else if (dlnx >= 413)
                id = 26;
            else if (dlnx >= 378)
                id = 25;
            else if (dlnx >= 339)
                id = 24;
            else if (dlnx >= 298)
                id = 23;
            else if (dlnx >= 250)
                id = 22;
            else if (dlnx >= 198)
                id = 21;
            else if (dlnx >= 139)
                id = 20;
            else if (dlnx >= 68)
                id = 19;
            else if (dlnx >= 0)
                id = 18;

            if (dsx != 0) {
                id = (short) (31 - id);
            }

            if (id == 16)
                id = 15;

        }
        /* ............... end of 40 kbit part ................. */

        /* Choose sd as sp, sp+ or sp- */

        ss = (short) ((sp & 128) >> 7);
        mask = (short) (sp & 127);

        if (law == G711Codec.A_LAW) /* ......... A-law */
        {
            if (id > im && ss == 1 && mask == 0)
                ss = 0;
            else if (id > im && ss == 1 && mask != 0)
                mask--;
            else if (id > im && ss == 0 && mask != 127)
                mask++;
            else if (id < im && ss == 1 && mask != 127)
                mask++;
            else if (id < im && ss == 0 && mask == 0)
                ss = 1;
            else if (id < im && ss == 0 && mask != 0)
                mask--;
        } else { /* ......... u-law */
            if (id > im && ss == 1 && mask == 127) {
                ss = 0;
                mask--;
            } else if (id > im && ss == 1 && mask != 127)
                mask++;
            else if (id > im && ss == 0 && mask != 0)
                mask--;
            else if (id < im && ss == 1 && mask != 0)
                mask--;
            else if (id < im && ss == 0 && mask == 127)
                ss = 1;
            else if (id < im && ss == 0 && mask != 127)
                mask++;
        }

        return (short) (mask + (ss << 7));
    }

    /**
     * Private calculattion method for Codec.
     */
    private short upa2(short pk0, short pk1, short pk2, short a2, short a1,
            short sigpk) {
        long uga2a, uga2b, uga2s;
        long a11, a21, fa, fa1;
        short a1s, a2s;
        long ua2;
        long uga2, ula2;
        short pks1, pks2;

        /* Preamble */
        a11 = a1 & 65535;
        a21 = a2 & 65535;

        /* 1 bit xors */
        pks1 = (short) (pk0 ^ pk1);
        pks2 = (short) (pk0 ^ pk2);

        uga2a = (pks2 == 0) ? 16384 : 114688;

        a1s = (short) (a1 >> 15);

        /* Implement f(a1) w/ limiting at +/-(1/2) */
        if (a1s == 0)
            fa1 = (a11 <= 8191) ? (a11 << 2) : (8191 << 2);
        else
            fa1 = (a11 >= 57345) ? ((a11 << 2) & 131071) : (24577 << 2);

        /* Attach sign to the result of f(a1) */
        fa = (pks1 != 0) ? fa1 : ((131072 - fa1) & 131071);

        uga2b = (uga2a + fa) & 131071;
        uga2s = (uga2b >> 16);

        uga2 = (sigpk == 1) ? 0 : ((uga2s != 0) ? ((uga2b >> 7) + 64512)
                : (uga2b >> 7));

        a2s = (short) (a2 >> 15);

        ula2 = (a2s == 0) ? (65536 - (a21 >> 7)) & 65535
                : (65536 - ((a21 >> 7) + 65024)) & 65535;

        /* Compute update */
        ua2 = (uga2 + ula2) & 65535;

        return (short) ((a21 + ua2) & 65535);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short limc(short a2t) {
        long a2p1, a2t1, a2ll, a2ul;

        a2t1 = a2t & 65535;

        a2ul = 12288; /* Upper limit of +.75 */
        a2ll = 53248; /* Lower limit of -.75 */

        if (a2t1 >= 32768 && a2t1 <= a2ll)
            a2p1 = a2ll;
        else if (a2t1 >= a2ul && a2t1 <= 32767)
            a2p1 = a2ul;
        else
            a2p1 = a2t1;

        return (short) a2p1;

    }

    /**
     * Private calculattion method for Codec.
     */
    private short trigb(short tr, short ap) {
        return (tr == 0) ? ap : 0;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short upa1(short pk0, short pk1, short a1, short sigpk) {
        long a11, a1s, ua1;
        long ash;
        short pks;
        long uga1, ula1;

        /* Preamble */
        a11 = a1 & 65535;

        pks = (short) ((pk0) ^ (pk1));

        /* Gain is +/- (3/256) */
        uga1 = (sigpk == 1) ? 0 : ((pks == 0) ? 192 : 65344);

        a1s = (a11 >> 15);

        /* Leak factor is (1/256) */
        ash = (a11 >> 8);
        ula1 = ((a1s == 0) ? (65536 - ash) : (65536 - (ash + 65280))) & 65535;

        /* Compute update */
        ua1 = (uga1 + ula1) & 65535;

        return (short) ((a11 + ua1) & 65535);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short limd(short a1t, short a2p) {
        long a1p1, a2p1, a1t1, ome, a1ll, a1ul;

        /* Preamble */
        a1t1 = a1t & 65535;
        a2p1 = a2p & 65535;

        /* (1-epsilon), where epsilon = (1/16) */
        ome = 15360;

        /* Compute upper limit */
        a1ul = (ome + 65536 - a2p1) & 65535;

        /* Compute lower limit */
        a1ll = (a2p1 + 65536 - ome) & 65535;

        if (a1t1 >= 32768 && a1t1 <= a1ll)
            a1p1 = a1ll;
        else if (a1t1 >= a1ul && a1t1 <= 32767)
            a1p1 = a1ul;
        else
            a1p1 = a1t1;

        return (short) a1p1;
    }

    /**
     * Private calculattion method for Codec.
     */
    private short tone(short a2p) {
        long a2p1;

        a2p1 = a2p & 65535;

        return (short) ((a2p1 >= 32768 && a2p1 < 53760) ? 1 : 0);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short subtc(short dmsp, short dmlp, short tdp, short y) {
        long difm, difs, dthr, dmlp1, dmsp1;
        long dif;

        /* Preamble */
        dmsp1 = dmsp;
        dmlp1 = dmlp;

        /* Compute difference */
        dif = ((dmsp1 << 2) + 32768 - dmlp1) & 32767;
        difs = (dif >> 14);

        /* Compute magnitude of difference */
        difm = (difs == 0) ? dif : ((32768 - dif) & 16383);

        /* Compute threshold */
        dthr = (dmlp1 >> 3);

        /* Quantize speed control parameter */
        return (short) ((y >= 1536 && difm < dthr && tdp == 0) ? 0 : 1);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short filtc(short ax, short ap) {
        short difs, difsx;
        short dif;

        /* Compute difference */
        dif = (short) (((ax << 9) + 2048 - ap) & 2047);
        difs = (short) (dif >> 10);

        /* Time constant is 1/16, sign extension */
        difsx = (short) ((difs == 0) ? (dif >> 4) : ((dif >> 4) + 896));

        return (short) ((difsx + ap) & 1023);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short triga(short tr, short app) {
        return (short) ((tr == 0) ? (app) : 256);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short xor(short dqn, short dq) {
        short dqns;
        short dqs;

        dqs = (short) ((dq >> 15) & 1);

        dqns = (short) (dqn >> 10);

        return (short) (dqs ^ dqns);
    }

    /**
     * Private calculattion method for Codec.
     */
    private short upb(short bitrate, short u, short b, short dq) {
        short dqmag;
        long bb, bs, ub;
        long ugb, ulb;
        short param;
        short leak;

        /* Preamble */
        bb = b & 65535;

        dqmag = (short) (dq & (short) 32767);
        if (bitrate != ADPCMCodec.BITRATE_40K) {
            leak = 8;
            param = (short) 65280;
        } else {
            leak = 9;
            param = (short) 65408;
        }
        /* gain is 0 or +/- (1/128) */
        ugb = (dqmag == 0) ? 0 : ((u == 0) ? 128 : (short) 65408);

        bs = (bb >> 15);

        /* Leak factor is (1/256 or 1/512 for 40 kbit/s) */

        ulb = (bs == 0) ? ((65536 - (bb >> leak)) & 65535)
                : ((65536 - ((bb >> leak) + param)) & (short) 65535);

        /* Compute update */
        ub = (ugb + ulb) & 65535;
        /* aux = bb + ub; */

        return (short) ((bb + ub) & 65535);
    }

    /**
     * Private calculattion method for Codec.
     * 
     * @param bitrate
     * @param dln
     * @param ds
     * @return
     */
    private short quan(short bitrate, short dln, short ds) {
        short i = 0;

        if (bitrate == 4) {
            if (dln >= 3972)
                i = 1;
            else if (dln >= 2048)
                i = 15;
            else if (dln >= 400)
                i = 7;
            else if (dln >= 349)
                i = 6;
            else if (dln >= 300)
                i = 5;
            else if (dln >= 246)
                i = 4;
            else if (dln >= 178)
                i = 3;
            else if (dln >= 80)
                i = 2;
            else
                i = 1;

            /* Adjust for sign */
            if (ds != 0)
                i = (short) (15 - i);

            if (i == 0)
                i = 15;
        } /* ......... end of 32 kbit part ........... */

        else if (bitrate == 3) {
            if (dln >= 2048)
                i = 7;
            else if (dln >= 331)
                i = 3;
            else if (dln >= 218)
                i = 2;
            else if (dln >= 8)
                i = 1;
            else if (dln >= 0)
                i = 7;

            /* Adjust for sign */
            if (ds != 0)
                i = (short) (7 - i);

            if (i == 0)
                i = 7;
        } /* ......... end of 24 kbit part ........... */

        else if (bitrate == 2) {
            if (dln >= 2048)
                i = 0;
            else if (dln >= 261)
                i = 1;
            else
                i = 0;

            /* Adjust for sign */
            if (ds != 0)
                i = (short) (3 - i);
        } /* ......... end of 16 kbit part ........... */

        else {
            if (dln >= 4080)
                i = 2;
            else if (dln >= 3974)
                i = 1;
            else if (dln >= 2048)
                i = 31;
            else if (dln >= 553)
                i = 15;
            else if (dln >= 528)
                i = 14;
            else if (dln >= 502)
                i = 13;
            else if (dln >= 475)
                i = 12;
            else if (dln >= 445)
                i = 11;
            else if (dln >= 413)
                i = 10;
            else if (dln >= 378)
                i = 9;
            else if (dln >= 339)
                i = 8;
            else if (dln >= 298)
                i = 7;
            else if (dln >= 250)
                i = 6;
            else if (dln >= 198)
                i = 5;
            else if (dln >= 139)
                i = 4;
            else if (dln >= 68)
                i = 3;
            else if (dln >= 0)
                i = 2;

            if (ds != 0)
                i = (short) (31 - i);

            if (i == 0)
                i = 31;

        } /* ......... end of 40 kbit part ........... */

        return i;
    }
    /* ........................ end of G726_quan() ........................ */
}
