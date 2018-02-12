
package org.liberty.android.fantastischmemo.test.ui;
import android.support.test.filters.SmallTest;
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

    // 0.5 * length of word is equal to the number of clicks on letter option
    //if word is 7 letters, round down to 3 letters as the number of clicks
    private int middleHintCounter = word.length() / 2;

    // number of clicks on letter hint option exceeds word length
    private int pastLastCharacterHintCounter = word.length() + 1;

    String TAG = "displayLetterHintTest";


    @SmallTest
    @Test
    public void displayLetterHintTest() {
        boolean valid = false;

        //use spy to only mock the getCurrentCard method
        QuizActivity.QuizActivityPublicAccess spyQuizActivity = spy(QuizActivity
                .QuizActivityPublicAccess.class);

        //mock the card behavior
        Card mockCard = Mockito.mock(Card.class);

        //stub getAnswer to return word
        Mockito.when(mockCard.getAnswer()).thenReturn(word);

        //stub getCurrentCard behavior of spyQuizActivity which is used inside of displayLetterHint
        Mockito.when(spyQuizActivity.getCurrentCard()).thenReturn(mockCard);

        //compare returned values from displayLetterHint with values that should be displayed
        Log.d(TAG, "displayLetterHintTest!!!:"+ spyQuizActivity.displayLetterHint(20)+"!");
        if (spyQuizActivity.displayLetterHint(1).equals("l _ _ _ _ _ _") &&
            spyQuizActivity.displayLetterHint(middleHintCounter).equals("la   _ _ _ _") &&
            spyQuizActivity.displayLetterHint(pastLastCharacterHintCounter).equals("la  tête"))
        {
            valid = true;

        }
        assertTrue(valid);
    }
}