package org.liberty.android.fantastischmemo.utils;

import org.liberty.android.fantastischmemo.entity.Card;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;


/**
 * Created by melid on 2018-03-11.
 */

public class HintUtil {

    public HintUtil() {
    }

    public String generateLetterHint(int count, String word) {
        //this function is called in the displayLetterHint method, line 276
        //  String word = getCurrentCard().getAnswer();
        Random generator = new Random();
        StringBuilder string = new StringBuilder();
        int randomLength = generator.nextInt(word.length());
        char temp;
        temp = (char) generator.nextInt(randomLength);
        string.append(temp);
        for (int i = 0; i < word.length(); i++) {


            if (i <= count - 1) { //show letters up to the number of clicks for hint
                if (word.charAt(i) == ' ') {
                    string.append(" ");
                }
                string.append(word.charAt(i));
            } else {

                string.append(" _");
            }
        }
        return string.toString();
    }


    public List<Card> shuffleHintDeck(List<Card> deck) {
        //Using Yates shuffle algorithm
        int n = deck.size();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < deck.size(); i++) {
            int randomValue = i + random.nextInt(n - i);
            Card randomElement = deck.get(randomValue);
            deck.set(randomValue, deck.get(i));
            deck.set(i, randomElement);
        }
        return deck;
    }
}
