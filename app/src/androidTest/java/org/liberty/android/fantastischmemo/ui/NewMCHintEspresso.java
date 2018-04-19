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
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewMCHintEspresso {

    @Rule
    public ActivityTestRule<AnyMemo> mActivityTestRule = new ActivityTestRule<>(AnyMemo.class);

    @Test
    public void newHintMCEspresso() {
        final String TAG = "Espresso logger";
        try {
            //view is displayed logic
        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton.perform(scrollTo(), click());
        } catch (NoMatchingViewException e) {
            //catch exception if there is no dialogue box with an OK text
            //sometimes this text will only appear on the first time of running the test
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
                                4)));
        linearLayout.perform(scrollTo(), click());

        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.use_quiz_checkbox), withText("Use hints for this quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatCheckBox.perform(scrollTo(), click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.start_quiz_button), withText("Start quiz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction floatingActionButton = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.content),
                                childAtPosition(
                                        withId(R.id.decor_content_parent),
                                        0)),
                        1),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction imageView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(android.R.id.content),
                                3),
                        0),
                        isDisplayed()));
        imageView.perform(click());

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
