package org.liberty.android.fantastischmemo.db;

/**
 * Created by Wei on 3/17/2018.
 */
//import android.support.test.filters.SmallTest;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.entity.Card;
import org.robolectric.RobolectricTestRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class CardTest {

    private final Card card = new Card();

    String TAG = "CardTest.java";

    @SmallTest
    @Test
    public void testDefaults() {
        assertEquals(card.getLearningDate(),null);
    }

    @SmallTest
    @Test
    public void testSetLearningDate() throws Exception {
        String date ="2002-11-30 00:00:00.000000";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date startDate = df.parse(date);
        card.setLearningDate(startDate);
        assertEquals(startDate, card.getLearningDate());
    }


}
