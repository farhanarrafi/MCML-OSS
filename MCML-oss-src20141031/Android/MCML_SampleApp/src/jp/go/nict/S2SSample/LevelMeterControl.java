// Copyright 2013, NICT
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of NICT nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package jp.go.nict.S2SSample;

import android.graphics.Color;
import android.os.Handler;
import android.widget.TextView;

/**
 * Level meter control class.
 * 
 */
public class LevelMeterControl {
    // Color
    protected int colorLevelBlue = Color.BLUE;
    protected int colorLevelGreen = Color.GREEN;
    protected int colorLevelYellow = Color.YELLOW;
    protected int colorLevelRed = Color.RED;
    protected int colorBackGround = Color.BLACK;

    // Color percentage
    protected double color_ratio_brue = 0.3;
    protected double color_ratio_green = 0.3;
    protected double color_ratio_yellow = 0.2;
    protected double color_ratio_red = 1 - (color_ratio_brue
            + color_ratio_green + color_ratio_yellow);

    // Sets each color.
    protected int mColorGreenNumber = 0;
    protected int mColorYellowNumber = 0;
    protected int mColorRedNumber = 0;

    protected int mMaxLevelNumber = 0;

    // Level Meter View
    TextView[] mLevelView = null;

    /** Handler for screen control */
    Handler mHandler = new Handler();

    /** For level meter display */
    public double mRatio = 0;

    /**
     * Constructor
     * 
     * @param levelView
     *          Gets level meter to display in TextView.
     */
    public LevelMeterControl(TextView[] levelView) {
        mMaxLevelNumber = levelView.length;
        mLevelView = levelView;
        mColorGreenNumber = (int) (mMaxLevelNumber * color_ratio_brue);
        mColorYellowNumber = (int) (mMaxLevelNumber * color_ratio_green)
                + mColorGreenNumber;
        mColorRedNumber = (int) (mMaxLevelNumber * color_ratio_yellow)
                + mColorYellowNumber;
        if (mColorRedNumber > mMaxLevelNumber) {
            mColorRedNumber = mMaxLevelNumber;
        }
    }

    /**
     * Displays level meter.
     * 
     * @param dRatio
     *           Specifies level meter for displaying in 0.0 to 1.0.
     */
    public void setLevelMeter(double dRatio) {
        mRatio = dRatio;
        mHandler.post(new Runnable() {
            public void run() {
                int iBrightNumber = (int) (mMaxLevelNumber * mRatio);
                if (iBrightNumber >= mMaxLevelNumber) {
                    iBrightNumber = mMaxLevelNumber - 1;
                }

                for (int i = 0; i < mMaxLevelNumber; i++) {
                    if (i >= iBrightNumber) {
                        mLevelView[i].setBackgroundColor(colorBackGround);
                    } else {
                        if ((i >= mColorRedNumber) || (i == mMaxLevelNumber)) {
                            mLevelView[i].setBackgroundColor(colorLevelRed);
                        } else if (i >= mColorYellowNumber) {
                            mLevelView[i].setBackgroundColor(colorLevelYellow);
                        } else if (i >= mColorGreenNumber) {
                            mLevelView[i].setBackgroundColor(colorLevelGreen);
                        } else {
                            mLevelView[i].setBackgroundColor(colorLevelBlue);
                        }
                    }
                }
            }
        });
    }

}
