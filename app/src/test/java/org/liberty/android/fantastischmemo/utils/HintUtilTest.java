package org.liberty.android.fantastischmemo.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.utils.HintUtil;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

/**
 * Created by melid on 2018-03-19.
 */

public class HintUtilTest {
    private int middleHintCounter;
    private int pastLastCharacterHintCounter;
    private HintUtil hintUtil;
    private String word;
    private List<Card> cards;
    private List<Card> copyOfDeckNotShuffled;
    private Card card1;
    private Card card2;
    private Card card3;


    @Before
    public void setUp() {
        word = "la tête";

        // 0.5 * length of word is equal to the number of clicks on letter option
        //if word is 7 letters, round down to 3 letters as the number of clicks
        middleHintCounter = word.length() / 2;

        // number of clicks on letter hint option exceeds word length
        pastLastCharacterHintCounter = word.length() + 1;

        String TAG = "displayLetterHintTest";

        hintUtil = new HintUtil();

        cards = new ArrayList<>();
        copyOfDeckNotShuffled = new ArrayList<>();

        card1 = Mockito.mock(Card.class);
        card2 = Mockito.mock(Card.class);
        card3 = Mockito.mock(Card.class);

        cards.add(card1);
        cards.add(card2);
        cards.add(card3);

        Mockito.when(card1.getId()).thenReturn(1);
        Mockito.when(card2.getId()).thenReturn(2);
        Mockito.when(card3.getId()).thenReturn(3);

    }

    @After
    public void tearDown() {
        hintUtil = null;
    }

    @SmallTest
    @Test
    public void generateLetterHintTest() {
        boolean valid = false;
        String result = hintUtil.generateLetterHint(1, word);
        String result2 = hintUtil.generateLetterHint(middleHintCounter, word);
        String result3 = hintUtil.generateLetterHint(pastLastCharacterHintCounter, word);
        Assert.assertEquals("l _ _ _ _ _ _", result);
        Assert.assertEquals("la   _ _ _ _", result2);
        Assert.assertEquals("la  tête", result3);

    }

    @SmallTest
    @Test
    public void shuffleHintDeck() {

        copyOfDeckNotShuffled.add(card1);
        copyOfDeckNotShuffled.add(card2);
        copyOfDeckNotShuffled.add(card3);

        List<Card> deckShuffled = hintUtil.shuffleHintDeck(cards);
        Boolean sameOrder = true;

        for (int i = 0; i < copyOfDeckNotShuffled.size(); i++) {
            if (copyOfDeckNotShuffled.get(i).getId() != deckShuffled.get(i).getId()) {
                sameOrder = false;
            }
        }
        assertTrue(!(copyOfDeckNotShuffled.equals(deckShuffled)));
        assertTrue(!sameOrder);
    }
}
