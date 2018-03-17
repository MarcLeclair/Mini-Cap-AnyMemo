package org.liberty.android.fantastischmemo.test.db;

/**
 * Created by Wei on 3/4/2018.
 */
import android.support.test.filters.SmallTest;
import android.util.Log;

import java.util.*;
import java.text.*;

import org.junit.Test;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.test.AbstractExistingDBTest;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
public class CardTest extends AbstractExistingDBTest {
    String TAG = "CardTest.java";

    @SmallTest
    @Test
    public void testLearningDate() throws Exception {


        String date ="2002-11-30 00:00:00.000000";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date startDate = df.parse(date);
        Card card = getCurrentCard();
        card.setLearningDate(startDate);
        Log.d(TAG, "testLearningDate: " +startDate);
        Card savedCard = setCard(card);

        assertEquals(startDate, savedCard.getLearningDate());
    }

    private Card getCurrentCard() throws SQLException {
        CardDao cardDao = helper.getCardDao();
        return cardDao.queryForId(1);
    }
    private Card setCard(Card card) throws SQLException {
        CardDao cardDao = helper.getCardDao();
        cardDao.update(card);
        return cardDao.queryForId(1);
    }
}