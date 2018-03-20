package org.liberty.android.fantastischmemo.test.ui;

import android.util.Log;

import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.ui.QuizActivity;
import org.liberty.android.fantastischmemo.utils.HintUtil;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class QACardActivityUnitTest {
    private String word = "la tête";

    // 0.5 * length of word is equal to the number of clicks on letter option
    //if word is 7 letters, round down to 3 letters as the number of clicks
    private int middleHintCounter = word.length() / 2;

    // number of clicks on letter hint option exceeds word length
    private int pastLastCharacterHintCounter = word.length() + 1;

    String TAG = "displayLetterHintTest";


    @Test
    public void displayLetterHintTest() {
        boolean valid = false;

        //use spy to only mock the getCurrentCard method
        Class<QuizActivity.QuizActivityPublicAccess> spyQuizActivity = spy(QuizActivity
                .QuizActivityPublicAccess.class);

        Class<HintUtil> spyHintUtil = spy(HintUtil.class);

        //mock the card behavior
        Card mockCard = Mockito.mock(Card.class);

        //stub getAnswer to return word
        Mockito.when(mockCard.getAnswer()).thenReturn(word);

        //stub getCurrentCard behavior of spyQuizActivity which is used inside of displayLetterHint
        Mockito.when(spyQuizActivity.getCurrentCard()).thenReturn(mockCard);

        //compare returned values from displayLetterHint with values that should be displayed
        Log.d(TAG, "displayLetterHintTest!!!:" + spyHintUtil.generateLetterHint(20, word) + "!");
        if (spyHintUtil.generateLetterHint(1, word).equals("l _ _ _ _ _ _") &&
                spyHintUtil.generateLetterHint(middleHintCounter, word).equals("la   _ _ _ _") &&
                spyHintUtil.generateLetterHint(pastLastCharacterHintCounter, word).equals("la  " +
                        "tête")) {
            valid = true;

        }
        assertTrue(valid);
    }

    @Test
    public void showMcHintTest() {
        boolean sameOrder = true;

        Class<HintUtil> spyHintUtil = spy(HintUtil.class);

        Card mockCard1 = Mockito.mock(Card.class);
        Card mockCard2 = Mockito.mock(Card.class);
        Card mockCard3 = Mockito.mock(Card.class);
        Card mockCard4 = Mockito.mock(Card.class);

        Mockito.when(mockCard1.getId()).thenReturn(1);
        Mockito.when(mockCard2.getId()).thenReturn(2);
        Mockito.when(mockCard3.getId()).thenReturn(3);
        Mockito.when(mockCard4.getId()).thenReturn(4);

        List<Card> deckNotShuffled = new ArrayList<>();
        List<Card> copyOfDeckNotShuffled = new ArrayList<>();
        deckNotShuffled.add(mockCard1);
        deckNotShuffled.add(mockCard2);
        deckNotShuffled.add(mockCard3);
        deckNotShuffled.add(mockCard4);
        copyOfDeckNotShuffled.add(mockCard1);
        copyOfDeckNotShuffled.add(mockCard2);
        copyOfDeckNotShuffled.add(mockCard3);
        copyOfDeckNotShuffled.add(mockCard4);

        for (int i = 0; i < deckNotShuffled.size(); i++) {
            Log.d(TAG, "showMcHintTest: " + "not shuffled card id : " + deckNotShuffled.get(i)
                    .getId());
        }

        List<Card> deckShuffled = spyHintUtil.shuffleHintDeck(deckNotShuffled);
        for (int i = 0; i < deckNotShuffled.size(); i++) {
            Log.d(TAG, "showMcHintTest: " + " shuffled card id " + deckNotShuffled.get(i).getId());
            if (copyOfDeckNotShuffled.get(i).getId() != deckShuffled.get(i).getId()) {
                sameOrder = false;
            }
        }
        assertTrue(!(copyOfDeckNotShuffled.equals(deckShuffled)));
        assertTrue(!sameOrder);
    }

}