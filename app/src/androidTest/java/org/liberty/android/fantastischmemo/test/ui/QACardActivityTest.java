
package org.liberty.android.fantastischmemo.test.ui;

import android.util.Log;

import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.test.BaseTest;
import org.liberty.android.fantastischmemo.ui.QuizActivity;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class QACardActivityTest extends BaseTest {
    private String word = "la tête";
    private int startHintCounter = 1;
    private int middleHintCounter = word.length() / 2;
    private int pastLastCharacterHintCounter = word.length() + 1;
    private String TAG = "private letter hint";

    @Test
    public void displayLetterHintTest() {
        boolean valid = false;

        QuizActivity.QuizActivityPublicAccess mockQuizActivity = spy(QuizActivity
                .QuizActivityPublicAccess.class);
        Card mockCard = Mockito.mock(Card.class);
        Mockito.when(mockCard.getAnswer()).thenReturn(word);
        Mockito.when(mockQuizActivity.getCurrentCard()).thenReturn(mockCard);
        Log.d(TAG, "displayLetterHintTest:" + mockQuizActivity.getCurrentCard().getAnswer());
        Log.d(TAG, "displayLetterHintTest:" + mockQuizActivity.displayLetterHint(1) + "!");
        if (mockQuizActivity.displayLetterHint(1).equals("l _ _ _ _ _ _")) {
            valid = true;
        }
        assertTrue(valid);
    }
}