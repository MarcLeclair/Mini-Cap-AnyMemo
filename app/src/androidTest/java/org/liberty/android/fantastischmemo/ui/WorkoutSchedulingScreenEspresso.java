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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WorkoutSchedulingScreenEspresso {

    @Rule
    public ActivityTestRule<AnyMemo> mActivityTestRule = new ActivityTestRule<>(AnyMemo.class);

    @Test
    public void workoutSchedulingScreenEspresso() {
        String TAG = WorkoutSchedulingScreen.class.getSimpleName();

        //catch the exception in case this dialogue box does not show up
        //it only shows up on a brand new installed anyMemo application
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

        //click on the 2nd tab
        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.tabs),
                                0),
                        1),
                        isDisplayed()));
        tabView.perform(click());

        //click on the + button to add a new db
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.add_db_fab),
                        childAtPosition(
                                allOf(withId(R.id.main_content),
                                        childAtPosition(
                                                withId(R.id.drawer_layout),
                                                0)),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        //write the name of the db
        ViewInteraction editText = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.custom),
                                childAtPosition(
                                        withId(R.id.customPanel),
                                        0)),
                        0),
                        isDisplayed()));
        editText.perform(replaceText("a"), closeSoftKeyboard());

        //press on ok to save the new db
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton2.perform(scrollTo(), click());

        //click on the first db (new one gets added on top)
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.file_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)));
        recyclerView.perform(actionOnItemAtPosition(2, click()));

        //click on the Preview/edit form the drop down menu
        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.edit),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        linearLayout.perform(scrollTo(), click());

        //add a new card
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.new_button), withText("New"),
                        childAtPosition(
                                allOf(withId(R.id.buttons_root),
                                        childAtPosition(
                                                withId(R.id.root),
                                                2)),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        //set the question to "a"
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_dialog_question_entry),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                1)));
        appCompatEditText.perform(scrollTo(), replaceText("a"), closeSoftKeyboard());

        //set the answer to "a"
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_dialog_answer_entry),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatEditText2.perform(scrollTo(), replaceText("a"), closeSoftKeyboard());


        //click on save
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.save), withContentDescription("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        //click on back button
        pressBack();

        //select the first db again (newly added is added on top)
        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.file_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)));
        recyclerView2.perform(actionOnItemAtPosition(2, click()));

        //click on "Add to workout mode"
        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.study_mode),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                1)));
        linearLayout2.perform(scrollTo(), click());

        //assert there is a Start date button, which allows the user to add the start date of
        // their workout
        ViewInteraction button = onView(
                allOf(withId(R.id.start_date_button),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        2),
                                0),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        //assert there is a number of days radio button that allows the user to add the number of
        // days for their workout
        ViewInteraction radioButton = onView(
                allOf(withId(R.id.num_days_button),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        radioButton.check(matches(isDisplayed()));


        //assert there is a notification checkbox that allows the user to add a notification
        // before their workout starts
        ViewInteraction checkBox = onView(
                allOf(withId(R.id.notification),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        4),
                                0),
                        isDisplayed()));
        checkBox.check(matches(isDisplayed()));

        //there is a positive button that allows the user to add the deck to workout mode
        ViewInteraction button2 = onView(
                allOf(withId(R.id.button_ok),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        5),
                                1),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        //there is a cancel button that cancels all operations the user has done to schedule a
        // new workout
        ViewInteraction button3 = onView(
                allOf(withId(R.id.button_cancel),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        5),
                                0),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));
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
