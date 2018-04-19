package org.liberty.android.fantastischmemo.utils;

import org.liberty.android.fantastischmemo.entity.Card;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


/**
 * Created by melid on 2018-03-11.
 */

public class HintUtil {

    public HintUtil() {

    }


    static HashSet<Integer> used = new HashSet<>();
    public static String generateLetterHint(int count, String word) {
        //this function is called in the displayLetterHint method, line 276
        //String word = getCurrentCard().getAnswer();
        StringBuilder string = new StringBuilder();
        Random rng = new Random();
        int newRandom;
        do {
            newRandom = rng.nextInt(word.length());
        } while (used.contains(newRandom));
        used.add(newRandom);

        char[] letters = word.toCharArray();
        char[] answers = new char[letters.length];

        //check if each index exists in the used list, if it does, copy the corresponding letter to answers, otherwise copy _ to answers
        for (int i = 0; i < word.length(); i++) {
            if (i <= count -1) {
                if (used.contains(i)) {
                    answers[i] = letters[i];
                }
            }
            else {
                answers[i] = '_';
            }
        }
        string.append(answers);

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
