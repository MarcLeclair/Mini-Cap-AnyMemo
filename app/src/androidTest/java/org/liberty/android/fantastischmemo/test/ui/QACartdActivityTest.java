/*
package org.liberty.android.fantastischmemo.test.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.filters.SmallTest;
import android.view.Display;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Test;
import org.liberty.android.fantastischmemo.common.AMEnv;
import org.liberty.android.fantastischmemo.modules.AppComponents;
import org.liberty.android.fantastischmemo.test.BaseTest;
import org.liberty.android.fantastischmemo.ui.QACardActivity;
import org.liberty.android.fantastischmemo.utils.AMFileUtil;
import org.liberty.android.fantastischmemo.utils.AMPrefUtil;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class QACardActivityTest extends BaseTest {
    private int hintCounter = 3;
    private String word = "la tête";



    @SmallTest
    @Test
    public void displayLetterHintTest() {
        displayLetterHint();

        int bitmapWidth = (int) (0.1 * screenWidth);
        int[] colorArray = new int[bitmapWidth * bitmapWidth];
        Bitmap b = Bitmap.createBitmap(colorArray, bitmapWidth, bitmapWidth, Bitmap.Config.ARGB_8888);
        Bitmap test = cardImageGetter.scaleBitmap(b);
        assertEquals(bitmapWidth, test.getWidth());
    }

}
*/
