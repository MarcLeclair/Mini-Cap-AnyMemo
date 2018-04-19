package org.liberty.android.fantastischmemo.ui;

import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.entity.Card;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melid on 2018-04-15.
 */

@RunWith(MockitoJUnitRunner.class)
public class WorkoutIncompleteLauncherDialogFragmentTest {
    WorkoutIncompleteLauncherDialogFragment fragment;
    @Mock
    private TextView text;
    @Mock
    private Card card1;
    @Mock
    private Card card2;
    private List<Card> cards;

    @Before
    public void setUp() {
        fragment = new WorkoutIncompleteLauncherDialogFragment();
        cards = new ArrayList<>();

    }

    public void tearDown() {
        cards = null;
        fragment = null;
    }

    @SmallTest
    @Test
    public void displayCardsSingleCardTest() {
        cards.add(card1);
        Mockito.when(card1.getQuestion()).thenReturn("question1");
        Mockito.when(card1.getAnswer()).thenReturn("answer1");
        String returnedText = fragment.displayCards(cards, text);
        Assert.assertEquals("question: " + card1.getQuestion() + " answer: " + card1.getAnswer() +
                "\n", returnedText);
    }

    @SmallTest
    @Test
    public void displayCardsMultipleCardTest() {
        cards.add(card1);
        cards.add(card2);
        Mockito.when(card1.getQuestion()).thenReturn("question1");
        Mockito.when(card1.getAnswer()).thenReturn("answer1");
        Mockito.when(card1.getQuestion()).thenReturn("question2");
        Mockito.when(card1.getAnswer()).thenReturn("answer2");

        String returnedText = fragment.displayCards(cards, text);
        Assert.assertEquals(
                "question: " + card1.getQuestion() + " answer: " + card1.getAnswer() + "\n"
                        + "question: " + card2.getQuestion() + " answer: " + card2.getAnswer() + "\n"
                , returnedText);
    }

    @SmallTest
    @Test
    public void displayCardsNoAnswerCardTest() {
        cards.add(card1);
        cards.add(card2);
        Mockito.when(card1.getQuestion()).thenReturn("question1");
        Mockito.when(card1.getAnswer()).thenReturn("");
        Mockito.when(card1.getQuestion()).thenReturn("question2");
        Mockito.when(card1.getAnswer()).thenReturn("");

        String returnedText = fragment.displayCards(cards, text);
        Assert.assertEquals(
                "question: " + card1.getQuestion() + " answer: " + card1.getAnswer() + "\n"
                        + "question: " + card2.getQuestion() + " answer: " + card2.getAnswer() + "\n"
                , returnedText);
    }

    @SmallTest
    @Test
    public void displayCardsNoQuestionCardTest() {
        cards.add(card1);
        cards.add(card2);
        Mockito.when(card1.getQuestion()).thenReturn("");
        Mockito.when(card1.getAnswer()).thenReturn("answer1");
        Mockito.when(card1.getQuestion()).thenReturn("");
        Mockito.when(card1.getAnswer()).thenReturn("answer2");

        String returnedText = fragment.displayCards(cards, text);
        Assert.assertEquals(
                "question: " + card1.getQuestion() + " answer: " + card1.getAnswer() + "\n"
                        + "question: " + card2.getQuestion() + " answer: " + card2.getAnswer() + "\n"
                , returnedText);
    }

    @SmallTest
    @Test
    public void displayEmptyCardTest() {
        cards.add(card1);
        cards.add(card2);
        Mockito.when(card1.getQuestion()).thenReturn("");
        Mockito.when(card1.getAnswer()).thenReturn("");
        Mockito.when(card1.getQuestion()).thenReturn("");
        Mockito.when(card1.getAnswer()).thenReturn("");

        String returnedText = fragment.displayCards(cards, text);
        Assert.assertEquals(
                "question: " + card1.getQuestion() + " answer: " + card1.getAnswer() + "\n"
                        + "question: " + card2.getQuestion() + " answer: " + card2.getAnswer() + "\n"
                , returnedText);
    }
}
