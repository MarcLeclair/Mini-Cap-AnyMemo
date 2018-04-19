package org.liberty.android.fantastischmemo.ui;


import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MultiPlayerHostEspresso {

    @Rule
    public ActivityTestRule<AnyMemo> mActivityTestRule = new ActivityTestRule<>(AnyMemo.class);

    @Test
     
    public void multiPlayerHostEspressoTest() {

        String TAG = MultiPlayerHostEspresso.class.getSimpleName();
        try {
            ViewInteraction appCompatButton = onView(
                    allOf(withId(android.R.id.button1), withText("OK"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    3)));
            appCompatButton.perform(scrollTo(), click());
        } catch (NoMatchingViewException e) {
            Log.d(TAG, "Exception thrown, OK button not found");
        }
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.appbar),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.appbar),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)),
                        6),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction materialEditText = onView(
                allOf(withId(R.id.userName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container),
                                        0),
                                2),
                        isDisplayed()));
        materialEditText.perform(click());

        ViewInteraction materialEditText2 = onView(
                allOf(withId(R.id.userName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container),
                                        0),
                                2),
                        isDisplayed()));
        materialEditText2.perform(click());

        ViewInteraction materialEditText3 = onView(
                allOf(withId(R.id.userName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container),
                                        0),
                                2),
                        isDisplayed()));
        materialEditText3.perform(replaceText("sdgd"), closeSoftKeyboard());

        ViewInteraction materialEditText4 = onView(
                allOf(withId(R.id.userName), withText("sdgd"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container),
                                        0),
                                2),
                        isDisplayed()));
        materialEditText4.perform(pressImeActionButton());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.hostGame), withText("Host Game"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction materialEditText5 = onView(
                allOf(withId(R.id.gameName),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        materialEditText5.perform(replaceText("e"), closeSoftKeyboard());

        ViewInteraction materialEditText6 = onView(
                allOf(withId(R.id.gameName), withText("e"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        materialEditText6.perform(pressImeActionButton());

        ViewInteraction materialEditText7 = onView(
                allOf(withId(R.id.numberOfPlayers),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        materialEditText7.perform(click());

        ViewInteraction materialEditText8 = onView(
                allOf(withId(R.id.numberOfPlayers),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        materialEditText8.perform(replaceText("1"), closeSoftKeyboard());

        ViewInteraction materialEditText9 = onView(
                allOf(withId(R.id.numberOfPlayers), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        materialEditText9.perform(pressImeActionButton());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.startGame), withText("Start Game"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton3.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
