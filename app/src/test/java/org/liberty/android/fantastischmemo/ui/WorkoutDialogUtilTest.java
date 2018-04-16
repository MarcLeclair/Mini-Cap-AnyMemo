package org.liberty.android.fantastischmemo.ui;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.utils.DateUtil;
import org.liberty.android.fantastischmemo.utils.WorkoutDialogUtil;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by melid on 2018-04-15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class WorkoutDialogUtilTest {

    @Mock
    private AnyMemoDBOpenHelper helper;
    @Mock
    private CardDao dao;
    private WorkoutDialogUtil dialogUtil;
    private List<Card> realCards;
    private Card realCard1;
    private Card realCard2;
    private Card realCard3;
    private Date date;
    private Date date2;
    private Date date3;
    private Calendar cal;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
        dialogUtil = new WorkoutDialogUtil();
        realCards = new ArrayList<>();

        //create 3 consecutive days
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
        //  cards = null;
        dialogUtil = null;
        cal = null;
        date = null;
        date2 = null;
        date3 = null;
    }

    @SmallTest
    @Test
    public void setWorkoutDatesTest() {
        //to test that the cards have appropriate dates, we must use actual card objects to be
        // able to retrieve the dates that is set for them
        realCard1 = new Card();
        realCard2 = new Card();
        realCard3 = new Card();

        realCards.add(realCard1);
        realCards.add(realCard2);
        realCards.add(realCard3);

        Mockito.when(helper.getCardDao()).thenReturn(dao);
        Mockito.when(dao.getAllCards(null)).thenReturn(realCards);

        List<Card> result = dialogUtil.setWorkoutModeDates(helper, 3, date, true);

        Assert.assertEquals(result, dao.getAllCards(null));
        //the size that the dao's deck of cards returns a value that should be equal to the size of
        // cards
        Assert.assertEquals(dao.getAllCards(null).size(), 3);
        //since we have 3 cards, and the numDays is 3, this means 1 card is studied per day,
        // starting on the date assigned to the variable "date"
        //each card is studied on a consecutive day
        Assert.assertEquals(date, realCard1.getLearningDate());
        Assert.assertEquals(date2, realCard2.getLearningDate());
        Assert.assertEquals(date3, realCard3.getLearningDate());
    }

    @SmallTest
    @Test
    public void setCardsDates() {
        //set each card a date
        realCard1 = new Card();
        realCard1.setLearningDate(DateUtil.getDate(1, 1, 1));
        realCard2 = new Card();
        realCard2.setLearningDate(DateUtil.getDate(1, 1, 1));
        realCard3 = new Card();
        realCard3.setLearningDate(DateUtil.getDate(1, 1, 1));

        realCards.add(realCard1);
        realCards.add(realCard2);
        realCards.add(realCard3);

        Date newDate = DateUtil.getDate(2, 2, 2);

        Mockito.when(helper.getCardDao()).thenReturn(dao);
        Mockito.when(dao.getAllCards(null)).thenReturn(realCards);

        List<Card> incompleteCards = dialogUtil.setCardsDates(helper, newDate, realCards);

        Assert.assertEquals(incompleteCards, realCards);
        Assert.assertEquals(realCard1.getLearningDate(), newDate);
        Assert.assertEquals(realCard2.getLearningDate(), newDate);
        Assert.assertEquals(realCard3.getLearningDate(), newDate);

    }
}
