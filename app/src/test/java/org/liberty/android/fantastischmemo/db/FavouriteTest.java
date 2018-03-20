package org.liberty.android.fantastischmemo.db;

/**
 * Created by OmarHammoud on 2018-03-19.
 */

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.entity.LearningData;
import org.liberty.android.fantastischmemo.ui.CardListActivity;
import org.robolectric.RobolectricTestRunner;


import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class FavouriteTest {
    private  Card card = new Card();

    String TAG = "FavouriteTest.java";

    @Before
    public void setUp() {
        card = new Card();
    }


    @After
    public void tearDown() {
        card = null;
    }

    @SmallTest
    @Test
    public void testDefaults() {
        assertEquals(card.getLearningData(),null);
    }

    @SmallTest
    @Test
    public void testFavfont() throws Exception {
        setUp();
        LearningData ld = new LearningData();
        ld.setFavourite(1);
        card.setLearningData(ld);
        assertEquals(ld.getFavourite(), card.getLearningData().getFavourite());
    }
}
