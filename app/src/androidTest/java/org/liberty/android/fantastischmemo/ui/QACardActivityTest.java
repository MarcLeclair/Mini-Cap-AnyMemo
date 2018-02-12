package org.liberty.android.fantastischmemo.ui;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by rswll_000 on 2018-02-11.
 */
public class QACardActivityTest extends BaseTest{
    private String word = "la tete";


    @Test
    public void showMcHintTest() {
        boolean valid = false;

        QuizActivity.QuizActivityPublicAccess spyQuizActivity = spy(QuizActivity.QuizActivityPublicAccess.class);

        Card mockCard1 = Mockito.mock(Card.class);
        Card mockCard2 = Mockito.mock(Card.class);
        Card mockCard3 = Mockito.mock(Card.class);
        Card mockCard4 = Mockito.mock(Card.class);

        Mockito.when(mockCard1.getAnswer()).thenReturn(word);
        Mockito.when(mockCard2.getAnswer()).thenReturn(word);
        Mockito.when(mockCard3.getAnswer()).thenReturn(word);
        Mockito.when(mockCard4.getAnswer()).thenReturn(word);

        Mockito.when(spyQuizActivity.getCurrentCard()).thenReturn(mockCard1);
        Mockito.when(spyQuizActivity.getCurrentCard()).thenReturn(mockCard2);
        Mockito.when(spyQuizActivity.getCurrentCard()).thenReturn(mockCard3);
        Mockito.when(spyQuizActivity.getCurrentCard()).thenReturn(mockCard4);

        if (mockCard1.getAnswer() != mockCard2.getAnswer() && mockCard2.getAnswer() != mockCard3.getAnswer() && mockCard3.getAnswer() != mockCard4.getAnswer()){
            valid = true;
        }
        assrtTrue(valid);
    }

}