package org.liberty.android.fantastischmemo.ui;


import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.util.Log;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
/**
 * Created by Brandon on 3/18/2018.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class spellTestEspressoTest {

    @Rule
    public ActivityTestRule<AnyMemo> mActivityTestRule = new ActivityTestRule<>(AnyMemo.class);

    @Test
    public void spellTestEspressoTest() {
        final String TAG = "Espresso logger";
        try {
            ViewInteraction appCompatButton = onView(
                    allOf(withId(android.R.id.button1), withText("OK"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    3)));
            appCompatButton.perform(scrollTo(), click());

            ViewInteraction tabView = onView(
                    allOf(childAtPosition(
                            childAtPosition(
                                    withId(R.id.tabs),
                                    0),
                            2),
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

            ViewInteraction linearLayout = onView(
                    allOf(withId(R.id.source_anymemo),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.v4.widget.NestedScrollView")),
                                            0),
                                    0),
                            isDisplayed()));
            linearLayout.perform(click());

            DataInteraction linearLayout2 = onData(anything())
                    .inAdapterView(allOf(withId(R.id.file_list),
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    1)))
                    .atPosition(9);
            linearLayout2.perform(click());

            DataInteraction linearLayout3 = onData(anything())
                    .inAdapterView(allOf(withId(R.id.file_list),
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    1)))
                    .atPosition(4);
            linearLayout3.perform(click());

            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(android.R.id.button1), withText("Yes"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    3)));
            appCompatButton2.perform(scrollTo(), click());

            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(android.R.id.button1), withText("OK"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    3)));
            appCompatButton3.perform(scrollTo(), click());

            pressBack();

            ViewInteraction tabView2 = onView(
                    allOf(childAtPosition(
                            childAtPosition(
                                    withId(R.id.tabs),
                                    0),
                            0),
                            isDisplayed()));
            tabView2.perform(click());

            ViewInteraction viewPager2 = onView(
                    allOf(withId(R.id.viewpager),
                            childAtPosition(
                                    allOf(withId(R.id.main_content),
                                            childAtPosition(
                                                    withId(R.id.drawer_layout),
                                                    0)),
                                    1),
                            isDisplayed()));
            viewPager2.perform(swipeRight());

            ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.recent_item_more_button), withText("MORE"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.FrameLayout")),
                                            0),
                                    2),
                            isDisplayed()));
            appCompatButton4.perform(click());

            ViewInteraction linearLayout4 = onView(
                    allOf(withId(R.id.study),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    0)));
            linearLayout4.perform(scrollTo(), click());

            openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

            ViewInteraction appCompatTextView = onView(
                    allOf(withId(R.id.title), withText("Type out the answer"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.v7.view.menu.ListMenuItemView")),
                                            0),
                                    0),
                            isDisplayed()));
            appCompatTextView.perform(click());

            ViewInteraction editText = onView(
                    allOf(childAtPosition(
                            allOf(withId(android.R.id.custom),
                                    childAtPosition(
                                            withClassName(is("android.widget.FrameLayout")),
                                            0)),
                            0),
                            isDisplayed()));
            editText.perform(replaceText("la tête"), closeSoftKeyboard());

            ViewInteraction appCompatButton5 = onView(
                    allOf(withId(android.R.id.button1), withText("Verify Answer"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.ScrollView")),
                                            0),
                                    3)));
            appCompatButton5.perform(scrollTo(), click());
        }catch (NoMatchingViewException e) {
            //catch exception if there is no dialogue box with an OK text
            //sometimes this text will only appear on the first time of running the test
            Log.d(TAG, "letterHintEspresso() returned: exception, could not find text with OK" );
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