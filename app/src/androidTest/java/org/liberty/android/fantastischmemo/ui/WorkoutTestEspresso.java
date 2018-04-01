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
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WorkoutTestEspresso {

    @Rule
    public ActivityTestRule<AnyMemo> mActivityTestRule = new ActivityTestRule<>(AnyMemo.class);

    @Test
    public void workoutTest() {
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
                allOf(withId(R.id.study_mode),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                1)));
        linearLayout.perform(scrollTo(), click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.start_date_button), withText("Add start date"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.num_days_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.num_days_input_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("5"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.button_ok), withText("Add to workout"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                1),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.tabs),
                                0),
                        4),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.viewpager),
                        childAtPosition(
                                allOf(withId(R.id.main_content),
                                        childAtPosition(
                                                withId(R.id.drawer_layout),
                                                0)),
                                1),
                        isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.button),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recent_open_list),
                                        0),
                                1),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.start_workout), withText("START WORKOUT!"),
                        childAtPosition(
                                allOf(withId(R.id.expandableLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                3)),
                                1),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.root),
                        withParent(allOf(withId(R.id.field2),
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2))),
                        isDisplayed()));
        linearLayout2.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.grade_button_5), withText("Easy\n5.0 day"),
                        childAtPosition(
                                allOf(withId(R.id.grade_buttons_anki),
                                        childAtPosition(
                                                withId(R.id.buttons_root),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction linearLayout3 = onView(
                allOf(withId(R.id.root),
                        withParent(allOf(withId(R.id.field2),
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2))),
                        isDisplayed()));
        linearLayout3.perform(click());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.grade_button_5), withText("Easy\n5.0 day"),
                        childAtPosition(
                                allOf(withId(R.id.grade_buttons_anki),
                                        childAtPosition(
                                                withId(R.id.buttons_root),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction linearLayout4 = onView(
                allOf(withId(R.id.root),
                        withParent(allOf(withId(R.id.field2),
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2))),
                        isDisplayed()));
        linearLayout4.perform(click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.grade_button_5), withText("Easy\n5.0 day"),
                        childAtPosition(
                                allOf(withId(R.id.grade_buttons_anki),
                                        childAtPosition(
                                                withId(R.id.buttons_root),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton9.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.card_text_view), withText("?\nShow answer"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.root),
                                        1),
                                0)));
        appCompatTextView.perform(scrollTo(), click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.grade_button_5), withText("Easy\n5.0 day"),
                        childAtPosition(
                                allOf(withId(R.id.grade_buttons_anki),
                                        childAtPosition(
                                                withId(R.id.buttons_root),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton10.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.card_text_view), withText("?\nShow answer"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.root),
                                        1),
                                0)));
        appCompatTextView2.perform(scrollTo(), click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.grade_button_5), withText("Easy\n5.0 day"),
                        childAtPosition(
                                allOf(withId(R.id.grade_buttons_anki),
                                        childAtPosition(
                                                withId(R.id.buttons_root),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton11.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.card_text_view), withText("?\nShow answer"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.root),
                                        1),
                                0)));
        appCompatTextView3.perform(scrollTo(), click());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.grade_button_5), withText("Easy\n5.0 day"),
                        childAtPosition(
                                allOf(withId(R.id.grade_buttons_anki),
                                        childAtPosition(
                                                withId(R.id.buttons_root),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton12.perform(click());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(android.R.id.button1), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton13.perform(scrollTo(), click());

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
