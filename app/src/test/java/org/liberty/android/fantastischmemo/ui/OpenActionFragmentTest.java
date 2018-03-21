package org.liberty.android.fantastischmemo.ui;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import android.util.Log;

/**
 * Created by melid on 2018-03-18.
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({OpenActionsFragment.class, Log.class})
public class OpenActionFragmentTest {
    AnyMemoDBOpenHelper helper;
    CardDao dao;
    int numDays;
    List<Card> cards;
    List<Card> realCards;
    Card card1;
    Card card2;
    Card card3;
    Card realCard1;
    Card realCard2;
    Card realCard3;
    OpenActionsFragment oaf;
    Date date;
    Date date2;
    Date date3;
    Calendar cal;

    @Before
    public void setUp() throws Exception {
        //add all necessary objects that will be used in the tests
        //must mock the log, otherwise a runtime error is thrown
        //      PowerMockito.mockStatic(Log.class);
        cards = new ArrayList<>();
        realCards = new ArrayList<>();

        oaf = new OpenActionsFragment();
        helper = Mockito.mock(AnyMemoDBOpenHelper.class);
        dao = Mockito.mock(CardDao.class);

        card1 = Mockito.mock(Card.class);
        card2 = Mockito.mock(Card.class);
        card3 = Mockito.mock(Card.class);

        cards.add(card1);
        cards.add(card2);
        cards.add(card3);

        realCard1 = new Card();
        realCard2 = new Card();
        realCard3 = new Card();

        realCards.add(realCard1);
        realCards.add(realCard2);
        realCards.add(realCard3);


        //create 3 consecutive days
        numDays = cards.size();
        cal = Calendar.getInstance();
        date = cal.getTime();
        cal.add(Calendar.DATE, 1);
        date2 = cal.getTime();
        cal.add(Calendar.DATE, 1);
        date3 = cal.getTime();
    }

    @After
    public void tearDown() throws Exception {
        //reassign to null all real objects
        cards = null;
        cal = null;
        date = null;
        date2 = null;
        date3 = null;
    }

    @SmallTest
    @Test
    public void setWorkoutModeDatesTest() {
        Mockito.when(helper.getCardDao()).thenReturn(dao);
        Mockito.when(dao.getAllCards(null)).thenReturn(cards);

        //the size that the dao's deck of cards returns a value that should be equal to the size of
        // cards
        Assert.assertEquals(dao.getAllCards(null).size(), cards.size());
        //if the deck is not empty, this method should return true
        //in our case the deck is made of 3 cards
        Assert.assertTrue(oaf.setWorkoutModeDates(helper, numDays, date));

    }

    @SmallTest
    @Test
    public void verifyDatesForEachCard() {
        //to test that the cards have appropriate dates, we must use actual card objects to be
        // able to retrieve the dates that is set for them
        Mockito.when(helper.getCardDao()).thenReturn(dao);
        Mockito.when(dao.getAllCards(null)).thenReturn(realCards);
        oaf.setWorkoutModeDates(helper, numDays, date);
        //the size that the dao's deck of cards returns a value that should be equal to the size of
        // cards
        Assert.assertEquals(dao.getAllCards(null).size(), cards.size());
        //since we have 3 cards, and the numDays is 3, this means 1 card is studied per day,
        // starting on the date assigned to the variable "date"
        //each card is studied on a consecutive day
        Assert.assertEquals(date, realCard1.getLearningDate());
        Assert.assertEquals(date2, realCard2.getLearningDate());
        Assert.assertEquals(date3, realCard3.getLearningDate());

    }

}