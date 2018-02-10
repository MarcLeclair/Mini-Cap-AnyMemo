package org.liberty.android.fantastischmemo.ui;


import android.os.SystemClock;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import org.liberty.android.fantastischmemo.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class letterHintEspresso {

    @Rule
    public ActivityTestRule<AnyMemo> mActivityTestRule = new ActivityTestRule<>(AnyMemo.class);

    @Test
    public void letterHintEspresso() {
        final String TAG = "Espresso logger";
        try {
             //view is displayed logic
            ViewInteraction appCompatButton = onView(
                    allOf(withId(android.R.id.button1), withText("OK"),
                            childAtPosition(
                                    allOf(withClassName(is("com.android.internal.widget" +
                                                    ".ButtonBarLayout")),
                                            childAtPosition(
                                                    withClassName(is("android.widget.LinearLayout")),
                                                    3)),
                                    3),
                            isDisplayed()));
            appCompatButton.perform(click());
        } catch (NoMatchingViewException e) {
            //view not displayed logic
            Log.d(TAG, "letterHintEspresso() returned: exception, could not find text with OK" );
        }


        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.recent_item_more_button), withText("MORE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.quiz),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        linearLayout.perform(scrollTo(), click());

        //click on start quiz
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.start_quiz_button), withText("Start quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatButton3.perform(scrollTo(), click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        //click on letter hint from top menu
        int repetitions = 10;
        for (int i= repetitions; i<= repetitions - 1; i++) {
            ViewInteraction appCompatTextView = onView(
                    allOf(withId(R.id.title), withText("Letter hint"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.v7.view.menu" +
                                                    ".ListMenuItemView")),
                                            0),
                                    0),
                            isDisplayed()));
            appCompatTextView.perform(click());
        }

//        ViewInteraction linearLayout2 = onView(
//                allOf(withId(R.id.root),
//                        withParent(allOf(withId(R.id.field1),
//                                childAtPosition(
//                                        withId(R.id.parentField),
//                                        2))),
//                        isDisplayed()));
//        linearLayout2.perform(click());

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
