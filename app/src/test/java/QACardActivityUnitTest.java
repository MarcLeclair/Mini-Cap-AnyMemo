/**
 * Created by rswll_000 on 2018-03-17.
 */
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.ui.QuizActivity;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class QACardActivityUnitTest {

    @Test
    public void showMcHintTest() {
        boolean sameOrder = true;

        QuizActivity spyQuizActivity = spy(QuizActivity.class);

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

        List<Card> deckShuffled = spyQuizActivity.shuffleHintDeck(deckNotShuffled);
        for (int i = 0; i < deckNotShuffled.size(); i++) {
            Log.d(TAG, "showMcHintTest: " + " shuffled card id " + deckNotShuffled.get(i).getId());
            if (copyOfDeckNotShuffled.get(i).getId() != deckShuffled.get(i).getId()) {
                sameOrder = false;
            }
        }
        Assert.assertTrue(!(copyOfDeckNotShuffled.equals(deckShuffled)));
        Assert.assertTrue(!sameOrder);
    }

}
