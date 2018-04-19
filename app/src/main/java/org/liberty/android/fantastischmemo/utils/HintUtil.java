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
    public static int unusedIndex(int count){
        Random rng = new Random();
        int selected;
        do{
            selected = rng.nextInt(count);
        }
        while(used.contains(selected));{
            used.add(selected);
        }
        return selected;

    }


    public static String generateLetterHint(int  count, String word) {
        //this function is called in the displayLetterHint method, line 276
        //  String word = getCurrentCard().getAnswer();
        StringBuilder string = new StringBuilder();
        Random rng = new Random();


        char[] letters = word.toCharArray();
        char[] answers = new char[letters.length];
        int selected = rng.nextInt(letters.length);
            for (int i = 0; i < word.length(); i++) {
                //if (i == selected) was what it used to be
                if (used.contains(i)) {

                    answers[i] = letters[i];
                } else {
                    answers[i] = '_';
                }
            }

            string.append(answers);

        return string.toString();
    }

        // string.append(letters[rng.nextInt(letters.length)]);

        //while (wordGuessed != true) {
            /*for (int i = 0; i < answers.length; i++) {
            answers[i] = letters[rng.nextInt(letters.length)];
            }*/
        //}

       /* if (wordGuessed == letterGuessed){
            string.append(wordGuessed);
        }*/ /*int randomLoc = rng.nextInt(letters.length);
        int temp = letters[i];
        letters[i] = letters[randomLoc];
        letters[randomLoc] = (char) temp;*/



    /*char[] letters = word.toCharArray();
        boolean[] hints = new boolean[word.length()];
        char[] answers = new char[letters.length];
        Arrays.fill(hints, false);

        int selected = rng.nextInt(hints.length);

        while (hints[selected]) {
            for (int i = 0; i < letters.length; i++) {
                    if (hints[i]) {
                        answers[i] = letters[i];

                    }
                    string.append(word.charAt(i));

            }
        }
        hints[selected] = true;
        return string.toString();
    }*/

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
