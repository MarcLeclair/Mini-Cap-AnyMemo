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
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import org.liberty.android.fantastischmemo.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.allOf;
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
            //catch exception if there is no dialogue box with an OK text
            //sometimes this text will only appear on the first time of running the test
            Log.d(TAG, "letterHintEspresso() returned: exception, could not find text with OK" );
        }

        //click on More on the RHS of the quiz name, a dropdown menu will appear
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.recent_item_more_button), withText("MORE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());

        //click on quiz, a dialog box will ask to confirm the start of quiz
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

        //click on letter hint from top menu for an arbitrary number of clicks
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
