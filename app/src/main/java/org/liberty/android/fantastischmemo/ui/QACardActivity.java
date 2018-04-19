/*

Copyright (C) 2012 Haowen Ning

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package org.liberty.android.fantastischmemo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.common.base.Strings;

import org.apache.commons.io.FilenameUtils;
import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AMEnv;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.entity.Option;
import org.liberty.android.fantastischmemo.entity.Setting;
import org.liberty.android.fantastischmemo.service.AnyMemoService;
import org.liberty.android.fantastischmemo.ui.loader.CardTTSUtilLoader;
import org.liberty.android.fantastischmemo.ui.loader.MultipleLoaderManager;
import org.liberty.android.fantastischmemo.ui.loader.SettingLoader;
import org.liberty.android.fantastischmemo.utils.AMFileUtil;
import org.liberty.android.fantastischmemo.utils.AMStringUtils;
import org.liberty.android.fantastischmemo.utils.CardTTSUtil;
import org.liberty.android.fantastischmemo.utils.HintUtil;
import org.liberty.android.fantastischmemo.widget.AnyMemoWidgetProvider;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * The base class for all activities that displays cards.
 * <p>
 * To use this class, subclass it and call the startInit() in the onCreate() method.
 * onPostInit() is used for customized initialization. Additionally, the caller can register
 * a Loader that needs loading asynchronized. Call getMultipleLoaderManager() and register the loaders
 * before calling startInit().
 * <p>
 * Override getContentView() for loading a customized layout that is compatible with qa_card_layout.
 */
public abstract class QACardActivity extends BaseActivity {
    public static String EXTRA_DBPATH = "dbpath";

    private String dbPath;

    private String dbName;

    private AnyMemoDBOpenHelper dbOpenHelper;

    private Card currentCard;

    /**
     * The card may not be displayed immediate after setting.
     * The currentDisplayedCard stores the current displayed card in the view.
     * It is used for animation purpose.
     */
    private Card currentDisplayedCard = null;

    private static final int SETTING_LOADER_ID = 0;

    private static final int CARD_TTS_UTIL_LOADER_ID = 1;

    private Setting setting;

    private boolean isAnswerShown = true;

    private TextView smallTitleBar;

    private CardTTSUtil cardTTSUtil;

    private GestureLibrary gestureLibrary;

    private EditText inputSpellTest;

    String TAG = "spellingHint";

    private HintUtil hintUtil;

    Button btn;

    /**
     * This needs to be defined before onCreate so in onCreate, all loaders will
     * be registered with the right manager.
     */
    @Inject
    MultipleLoaderManager multipleLoaderManager;

    @Inject
    AMFileUtil amFileUtil;

    @Inject
    Option option;

    /**
     * This is for testing only.
     */
    public CardTTSUtil getCardTTSUtil() {
        return cardTTSUtil;
    }

    /**
     * Subclasses should call this method instead of creating
     * a new instance of multipleLoaderManager.
     */
    public MultipleLoaderManager getMultipleLoaderManager() {
        return multipleLoaderManager;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityComponents().inject(this);
        setContentView(getContentView());
        hintUtil = new HintUtil();


    }

    /**
     * Call this method to start the initialization process.
     * Must be called in UI thread.
     */
    public final void startInit() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dbPath = extras.getString(EXTRA_DBPATH);
        }

        dbOpenHelper = AnyMemoDBOpenHelperManager.getHelper(this, dbPath);

        dbPath = extras.getString(EXTRA_DBPATH);

        dbName = FilenameUtils.getName(dbPath);

        // Load gestures
        loadGestures();

        multipleLoaderManager.registerLoaderCallbacks(SETTING_LOADER_ID,
                new SettingLoaderCallbacks(), false);
        multipleLoaderManager.registerLoaderCallbacks(CARD_TTS_UTIL_LOADER_ID,
                new CardTTSUtilLoaderCallbacks(), true);
        multipleLoaderManager
                .setOnAllLoaderCompletedRunnable(onPostInitRunnable);
        multipleLoaderManager.startLoading();
    }

    /**
     * Override to load customized layout.
     */
    protected int getContentView() {
        return R.layout.qa_card_layout;
    }

    protected void setCurrentCard(Card card) {
        currentCard = card;
    }

    protected Card getCurrentCard() {
        return currentCard;
    }

    protected String getDbPath() {
        return dbPath;
    }

    protected String getDbName() {
        return dbName;
    }

    private boolean hintView = true;

    public CardFragment.Builder getDefaultQuestionFragment(Setting.Align questionAlign, String
            questionTypefaceValue, String[] imageSearchPaths) {
        return new CardFragment.Builder(getCurrentCard().getQuestion())
                .setTextAlignment(questionAlign)
                .setTypefaceFromFile(questionTypefaceValue)
                .setTextOnClickListener(onQuestionTextClickListener)
                .setCardOnClickListener(onQuestionViewClickListener)
                .setTextFontSize(setting.getQuestionFontSize())
                .setTypefaceFromFile(setting.getQuestionFont())
                .setDisplayInHtml(setting.getDisplayInHTMLEnum().contains(Setting.CardField.QUESTION))
                .setHtmlLinebreakConversion(setting.getHtmlLineBreakConversion())
                .setImageSearchPaths(imageSearchPaths)
                .setCardField(Setting.CardField.QUESTION)
                .setTextColor(setting.getQuestionTextColor())
                .setBackgroundColor(setting.getQuestionBackgroundColor());
    }

    public CardFragment.Builder getDefaultNoteFragment(Setting.Align answerAlign, String
            answerTypefaceValue, String[] imageSearchPaths) {
        return new CardFragment.Builder(getCurrentCard().getNote())
                .setTextAlignment(answerAlign)
                .setTypefaceFromFile(answerTypefaceValue)
                .setCardOnClickListener(onAnswerViewClickListener)
                .setTextFontSize(setting.getAnswerFontSize())
                .setTypefaceFromFile(setting.getAnswerFont())
                .setDisplayInHtml(setting.getDisplayInHTMLEnum().contains(Setting.CardField.ANSWER))
                .setHtmlLinebreakConversion(setting.getHtmlLineBreakConversion())
                .setImageSearchPaths(imageSearchPaths)
                .setCardField(Setting.CardField.NOTE);
    }

    public CardFragment.Builder getDefaultShowAnswerFragment(String answerTypefaceValue) {
        return new CardFragment.Builder("?\n" + getString(R.string.memo_show_answer))
                .setTextAlignment(Setting.Align.CENTER)
                .setTypefaceFromFile(answerTypefaceValue)
                .setTextOnClickListener(onAnswerTextClickListener)
                .setCardOnClickListener(onAnswerViewClickListener)
                .setTextFontSize(setting.getAnswerFontSize())
                .setTypefaceFromFile(setting.getAnswerFont())
                .setTextColor(setting.getAnswerTextColor())
                .setBackgroundColor(setting.getAnswerBackgroundColor());

    }

    protected void displayMcHint(boolean enableMcHint, List<Card> list) {

        list.add(getCurrentCard());

        List<Card> shuffled = hintUtil.shuffleHintDeck(list);

        Card card1 = shuffled.get(0);
        Card card2 = shuffled.get(1);
        Card card3 = shuffled.get(2);
        Card card4 = shuffled.get(3);

        String questionTypeface = setting.getQuestionFont();
        String answerTypeface = setting.getAnswerFont();

        Setting.Align questionAlign = setting.getQuestionTextAlign();
        Setting.Align answerAlign = setting.getAnswerTextAlign();

        String questionTypefaceValue = null;
        String answerTypefaceValue = null;
        List<CardFragment.Builder> fragments = new ArrayList<>(4);

        /* Set the typeface of question and answer */
        if (!Strings.isNullOrEmpty(questionTypeface)) {
            questionTypefaceValue = questionTypeface;

        }
        if (!Strings.isNullOrEmpty(answerTypeface)) {
            answerTypefaceValue = answerTypeface;
        }

        final String[] imageSearchPaths = {
            /* Relative path */
                "",
            /* Relative path with db name */
                "" + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/dbname/ */
                AMEnv.DEFAULT_IMAGE_PATH + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/ */
                AMEnv.DEFAULT_IMAGE_PATH,
        };

        // Buttons view can be null if it is not decleared in the layout XML
        View buttonsView = findViewById(R.id.buttons_root);

        if (buttonsView != null) {
            // Make sure the buttons view are also handling the event for the answer view
            // e. g. clicking on the blank area of the buttons layout to reveal the answer
            // or flip the card.
            buttonsView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onQuestionViewClickListener.onClick(v);
                }
            });

            // Also the buttons should match the color of the view above.
            // It could be the question if it is the double sided card with only question shown
            // or answer view's color.
            if (!setting.isDefaultColor()) {
                if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED && !enableMcHint &&
                        setting.getQuestionBackgroundColor() != null) {
                    buttonsView.setBackgroundColor(setting.getQuestionBackgroundColor());
                } else if (setting.getAnswerBackgroundColor() != null) {
                    buttonsView.setBackgroundColor(setting.getAnswerBackgroundColor());
                }
            }
        }

        CardFragment.Builder questionFragmentBuilder = getDefaultQuestionFragment(questionAlign,
                questionTypefaceValue, imageSearchPaths);


        CardFragment.Builder answerFragmentBuilder = new CardFragment.Builder(
                "1. " + card1.getAnswer() + "\n2. " + card2.getAnswer() +
                        "\n3. " + card3.getAnswer() + "\n4. " + card4.getAnswer())
                .setTextAlignment(answerAlign)
                .setTypefaceFromFile(answerTypefaceValue)
                .setTextOnClickListener(onAnswerTextClickListener)
                .setCardOnClickListener(onAnswerViewClickListener)
                .setTextFontSize(setting.getAnswerFontSize())
                .setTypefaceFromFile(setting.getAnswerFont())
                .setDisplayInHtml(setting.getDisplayInHTMLEnum().contains(Setting.CardField.ANSWER))
                .setHtmlLinebreakConversion(setting.getHtmlLineBreakConversion())
                .setImageSearchPaths(imageSearchPaths)
                .setCardField(Setting.CardField.ANSWER)
                .setBackgroundColor(setting.getAnswerBackgroundColor())
                .setTextColor(setting.getAnswerTextColor());

        // Note is currently shared some settings with Answer
        CardFragment.Builder noteFragmentBuilder = getDefaultNoteFragment(answerAlign,
                answerTypefaceValue, imageSearchPaths);

        // Long click to launch image viewer if the card has an image
        questionFragmentBuilder.setTextOnLongClickListener(
                generateImageOnLongClickListener(getCurrentCard().getQuestion(), imageSearchPaths));
        answerFragmentBuilder.setTextOnLongClickListener(
                generateImageOnLongClickListener(card1.getAnswer(), imageSearchPaths));

        fragments.add(questionFragmentBuilder);
        fragments.add(answerFragmentBuilder);
        fragments.add(noteFragmentBuilder);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (setting.getCardStyle() == Setting.CardStyle.SINGLE_SIDED) {

            singleSided(enableMcHint, ft, fragments);

        } else if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED) {
            FlipableCardFragment fragment = new FlipableCardFragment();
            Bundle b = new Bundle(1);
            CardFragment.Builder[] builders = {questionFragmentBuilder, answerFragmentBuilder, noteFragmentBuilder};
            b.putSerializable(FlipableCardFragment.EXTRA_CARD_FRAGMENT_BUILDERS, builders);
            if (enableMcHint) {
                b.putInt(FlipableCardFragment.EXTRA_INITIAL_POSITION, 1);
            } else {
                b.putInt(FlipableCardFragment.EXTRA_INITIAL_POSITION, 0);
            }

            fragment.setArguments(b);

            configCardFragmentTransitionAnimation(ft);

            ft.replace(R.id.card_root, fragment);
            ft.commit();
        } else {
            assert false : "Card logic not implemented for style: " + setting.getCardStyle();
        }

        isAnswerShown = false;

        // Set up the small title bar
        // It is default "GONE" so it won't take any space
        // if there is no text
        smallTitleBar = (TextView) findViewById(R.id.small_title_bar);

        // Only copy to clipboard if answer is show
        // as a feature request:
        // http://code.google.com/p/anymemo/issues/detail?id=239
        if (enableMcHint == true) {
            copyToClipboard();
        }

        currentDisplayedCard = getCurrentCard();
        onPostDisplayCard();
    }

    protected void displayLetterHint(boolean enableLetterHint, int letterHintCounter) {

        // First prepare the text to display


        String questionTypeface = setting.getQuestionFont();
        String answerTypeface = setting.getAnswerFont();

        Setting.Align questionAlign = setting.getQuestionTextAlign();
        Setting.Align answerAlign = setting.getAnswerTextAlign();

        String questionTypefaceValue = null;
        String answerTypefaceValue = null;
        List<CardFragment.Builder> fragments = new ArrayList<>(4);

        /* Set the typeface of question and answer */
        if (!Strings.isNullOrEmpty(questionTypeface)) {
            questionTypefaceValue = questionTypeface;

        }
        if (!Strings.isNullOrEmpty(answerTypeface)) {
            answerTypefaceValue = answerTypeface;
        }

        final String[] imageSearchPaths = {
            /* Relative path */
                "",
            /* Relative path with db name */
                "" + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/dbname/ */
                AMEnv.DEFAULT_IMAGE_PATH + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/ */
                AMEnv.DEFAULT_IMAGE_PATH,
        };

        // Buttons view can be null if it is not decleared in the layout XML
        View buttonsView = findViewById(R.id.buttons_root);

        if (buttonsView != null) {
            // Make sure the buttons view are also handling the event for the answer view
            // e. g. clicking on the blank area of the buttons layout to reveal the answer
            // or flip the card.
            buttonsView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onQuestionViewClickListener.onClick(v);
                }
            });

            // Also the buttons should match the color of the view above.
            // It could be the question if it is the double sided card with only question shown
            // or answer view's color.
            if (!setting.isDefaultColor()) {
                if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED && !enableLetterHint &&
                        setting.getQuestionBackgroundColor() != null) {
                    buttonsView.setBackgroundColor(setting.getQuestionBackgroundColor());
                } else if (setting.getAnswerBackgroundColor() != null) {
                    buttonsView.setBackgroundColor(setting.getAnswerBackgroundColor());
                }
            }
        }

        CardFragment.Builder questionFragmentBuilder = getDefaultQuestionFragment(questionAlign,
                questionTypefaceValue, imageSearchPaths);

        CardFragment.Builder showHintFragmentBuilder = new CardFragment.Builder
                (hintUtil.generateLetterHint(letterHintCounter, getCurrentCard().getAnswer()))
                .setTextAlignment(answerAlign)
                .setTypefaceFromFile(answerTypefaceValue)
                .setTextOnClickListener(onAnswerTextClickListener)
                .setCardOnClickListener(onAnswerViewClickListener)
                .setTextFontSize(setting.getAnswerFontSize())
                .setTypefaceFromFile(setting.getAnswerFont())
                .setDisplayInHtml(setting.getDisplayInHTMLEnum().contains(Setting.CardField.HINT))
                .setHtmlLinebreakConversion(setting.getHtmlLineBreakConversion())
                .setImageSearchPaths(imageSearchPaths)
                .setCardField(Setting.CardField.ANSWER)
                .setBackgroundColor(setting.getAnswerBackgroundColor())
                .setTextColor(setting.getAnswerTextColor());

        // Note is currently shared some settings with Answer
        CardFragment.Builder noteFragmentBuilder = getDefaultNoteFragment(answerAlign,
                answerTypefaceValue, null);

        // Long click to launch image viewer if the card has an image
        questionFragmentBuilder.setTextOnLongClickListener(
                generateImageOnLongClickListener(getCurrentCard().getQuestion(), imageSearchPaths));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        fragments.add(showHintFragmentBuilder);
        fragments.add(questionFragmentBuilder);
        fragments.add(noteFragmentBuilder);

        if (setting.getCardStyle() == Setting.CardStyle.SINGLE_SIDED) {

            singleSided(enableLetterHint, ft, fragments);

        } else if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED) {
            FlipableCardFragment fragment = new FlipableCardFragment();
            Bundle b = new Bundle(1);
            CardFragment.Builder[] builders = {questionFragmentBuilder, showHintFragmentBuilder, noteFragmentBuilder};
            b.putSerializable(FlipableCardFragment.EXTRA_CARD_FRAGMENT_BUILDERS, builders);
            if (enableLetterHint) {
                b.putInt(FlipableCardFragment.EXTRA_INITIAL_POSITION, 1);
            } else {
                b.putInt(FlipableCardFragment.EXTRA_INITIAL_POSITION, 0);
            }

            fragment.setArguments(b);

            configCardFragmentTransitionAnimation(ft);

            ft.replace(R.id.card_root, fragment);
            ft.commit();
        } else {
            assert false : "Card logic not implemented for style: " + setting.getCardStyle();
        }

        // Set up the small title bar
        // It is defualt "GONE" so it won't take any space
        // if there is no text
        smallTitleBar = (TextView) findViewById(R.id.small_title_bar);

        // Only copy to clipboard if answer is show
        // as a feature request:
        // http://code.google.com/p/anymemo/issues/detail?id=239
        if (enableLetterHint == true) {
            copyToClipboard();
        }

        currentDisplayedCard = getCurrentCard();

        onPostDisplayCard();
    }

    //public List<Card> displayMcCards
    // Important class that display the card using fragment
    // the enableSpellingTest parameter is handled differently on single
    // sided card and double sided card.
    protected void displaySpellingTest(boolean enableSpellingTest) {
        final boolean enableSpellingTest_local = enableSpellingTest;
        final List<CardFragment.Builder> fragments = new ArrayList<>(4);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Question: " + getCurrentCard().getQuestion());
        alertDialog.setMessage("Enter your answer in the field below");

        final EditText input = new EditText(this);
        input.setHint("Try to answer if you can");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Verify Answer",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String answer = input.getText().toString();
                        //if user leaves an empty field, warn them
                        if (answer.compareTo("") == 0) {
                            Toast.makeText(getApplicationContext(),
                                    "Empty field, enter an answer!", Toast.LENGTH_SHORT).show();
                        }
                        //if the user enters at least 1 character
                        else if (answer.compareTo("") > 0) {

                            //if the user enters the right answer
                            if (getCurrentCard().getAnswer().equals(answer)) {
                                Toast.makeText(getApplicationContext(),
                                        "Good Answer!", Toast.LENGTH_SHORT).show();

                                String questionTypeface = setting.getQuestionFont();
                                String answerTypeface = setting.getAnswerFont();

                                Setting.Align questionAlign = setting.getQuestionTextAlign();
                                Setting.Align answerAlign = setting.getAnswerTextAlign();

                                String questionTypefaceValue = null;
                                String answerTypefaceValue = null;

                                CardFragment.Builder questionFragmentBuilder = getDefaultQuestionFragment(questionAlign,
                                        questionTypefaceValue, null);


                                CardFragment.Builder answerFragmentBuilder = new CardFragment.Builder(getCurrentCard().getAnswer())
                                        .setTextAlignment(answerAlign)
                                        .setTypefaceFromFile(answerTypefaceValue)
                                        .setTextOnClickListener(onAnswerTextClickListener)
                                        .setCardOnClickListener(onAnswerViewClickListener)
                                        .setTextFontSize(setting.getAnswerFontSize())
                                        .setTypefaceFromFile(setting.getAnswerFont())
                                        .setDisplayInHtml(setting.getDisplayInHTMLEnum().contains(Setting.CardField.ANSWER))
                                        .setHtmlLinebreakConversion(setting.getHtmlLineBreakConversion())
                                        .setCardField(Setting.CardField.ANSWER)
                                        .setBackgroundColor(setting.getAnswerBackgroundColor())
                                        .setTextColor(setting.getAnswerTextColor());

                                // Note is currently shared some settings with Answer
                                CardFragment.Builder noteFragmentBuilder = getDefaultNoteFragment(answerAlign,
                                        answerTypefaceValue, null);

                                fragments.add(answerFragmentBuilder);
                                fragments.add(questionFragmentBuilder);
                                fragments.add(noteFragmentBuilder);


                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                                if (setting.getCardStyle() == Setting.CardStyle.SINGLE_SIDED) {

                                    singleSided(enableSpellingTest_local, ft, fragments);

                                } else {
                                    assert false : "Card logic not implemented for style: " + setting.getCardStyle();
                                }

                                isAnswerShown = enableSpellingTest_local;

                                // Set up the small title bar
                                // It is defualt "GONE" so it won't take any space
                                // if there is no text
                                smallTitleBar = (TextView) findViewById(R.id.small_title_bar);

                                // Only copy to clipboard if answer is show
                                // as a feature request:
                                // http://code.google.com/p/anymemo/issues/detail?id=239
                                if (enableSpellingTest_local == true) {
                                    copyToClipboard();
                                }

                                currentDisplayedCard = getCurrentCard();
                                onPostDisplayCard();

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Wrong Answer", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    protected void displayCard(boolean showAnswer) {
        // First prepare the text to display

        String questionTypeface = setting.getQuestionFont();
        String answerTypeface = setting.getAnswerFont();

        Setting.Align questionAlign = setting.getQuestionTextAlign();
        Setting.Align answerAlign = setting.getAnswerTextAlign();

        String questionTypefaceValue = null;
        String answerTypefaceValue = null;
        /* Set the typeface of question and answer */
        if (!Strings.isNullOrEmpty(questionTypeface)) {
            questionTypefaceValue = questionTypeface;

        }
        if (!Strings.isNullOrEmpty(answerTypeface)) {
            answerTypefaceValue = answerTypeface;
        }

        final String[] imageSearchPaths = {
            /* Relative path */
                "",
            /* Relative path with db name */
                "" + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/dbname/ */
                AMEnv.DEFAULT_IMAGE_PATH + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/ */
                AMEnv.DEFAULT_IMAGE_PATH,
        };

        // Buttons view can be null if it is not decleared in the layout XML
        View buttonsView = findViewById(R.id.buttons_root);

        if (buttonsView != null) {
            // Make sure the buttons view are also handling the event for the answer view
            // e. g. clicking on the blank area of the buttons layout to reveal the answer
            // or flip the card.
            buttonsView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onQuestionViewClickListener.onClick(v);
                }
            });

            // Also the buttons should match the color of the view above.
            // It could be the question if it is the double sided card with only question shown
            // or answer view's color.
            if (!setting.isDefaultColor()) {
                if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED && !showAnswer &&
                        setting.getQuestionBackgroundColor() != null) {
                    buttonsView.setBackgroundColor(setting.getQuestionBackgroundColor());
                } else if (setting.getAnswerBackgroundColor() != null) {
                    buttonsView.setBackgroundColor(setting.getAnswerBackgroundColor());
                }
            }
        }

        CardFragment.Builder questionFragmentBuilder = getDefaultQuestionFragment(questionAlign,
                questionTypefaceValue, imageSearchPaths);


        CardFragment.Builder answerFragmentBuilder = new CardFragment.Builder(getCurrentCard().getAnswer())
                .setTextAlignment(answerAlign)
                .setTypefaceFromFile(answerTypefaceValue)
                .setTextOnClickListener(onAnswerTextClickListener)
                .setCardOnClickListener(onAnswerViewClickListener)
                .setTextFontSize(setting.getAnswerFontSize())
                .setTypefaceFromFile(setting.getAnswerFont())
                .setDisplayInHtml(setting.getDisplayInHTMLEnum().contains(Setting.CardField.ANSWER))
                .setHtmlLinebreakConversion(setting.getHtmlLineBreakConversion())
                .setImageSearchPaths(imageSearchPaths);

        CardFragment.Builder showAnswerFragmentBuilder = getDefaultShowAnswerFragment
                (answerTypefaceValue);


        answerFragmentBuilder
                .setBackgroundColor(setting.getAnswerBackgroundColor())
                .setTextColor(setting.getAnswerTextColor());

        // Note is currently shared some settings with Answer
        CardFragment.Builder noteFragmentBuilder = getDefaultNoteFragment(answerAlign,
                answerTypefaceValue, imageSearchPaths);

        // Long click to launch image viewer if the card has an image
        questionFragmentBuilder.setTextOnLongClickListener(
                generateImageOnLongClickListener(getCurrentCard().getQuestion(), imageSearchPaths));
        answerFragmentBuilder.setTextOnLongClickListener(
                generateImageOnLongClickListener(getCurrentCard().getAnswer(), imageSearchPaths));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (setting.getCardStyle() == Setting.CardStyle.SINGLE_SIDED) {
            TwoFieldsCardFragment fragment = new TwoFieldsCardFragment();
            Bundle b = new Bundle();

            // Handle card field setting.
            List<CardFragment.Builder> builders1List = new ArrayList<CardFragment.Builder>(4);
            if (setting.getQuestionFieldEnum().contains(Setting.CardField.QUESTION)) {
                builders1List.add(questionFragmentBuilder);
            }
            if (setting.getQuestionFieldEnum().contains(Setting.CardField.ANSWER)) {
                builders1List.add(answerFragmentBuilder);
            }
            if (setting.getQuestionFieldEnum().contains(Setting.CardField.NOTE)) {
                builders1List.add(noteFragmentBuilder);
            }

            List<CardFragment.Builder> builders2List = new ArrayList<CardFragment.Builder>(4);
            if (!showAnswer) {
                builders2List.add(showAnswerFragmentBuilder);
            }
            if (setting.getAnswerFieldEnum().contains(Setting.CardField.QUESTION)) {
                builders2List.add(questionFragmentBuilder);
            }
            if (setting.getAnswerFieldEnum().contains(Setting.CardField.ANSWER)) {
                builders2List.add(answerFragmentBuilder);
            }
            if (setting.getAnswerFieldEnum().contains(Setting.CardField.NOTE)) {
                builders2List.add(noteFragmentBuilder);
            }

            CardFragment.Builder[] builders1 = new CardFragment.Builder[builders1List.size()];
            builders1List.toArray(builders1);
            CardFragment.Builder[] builders2 = new CardFragment.Builder[builders2List.size()];
            builders2List.toArray(builders2);

            b.putSerializable(TwoFieldsCardFragment.EXTRA_FIELD1_CARD_FRAGMENT_BUILDERS, builders1);
            b.putSerializable(TwoFieldsCardFragment.EXTRA_FIELD2_CARD_FRAGMENT_BUILDERS, builders2);
            if (showAnswer) {
                b.putInt(TwoFieldsCardFragment.EXTRA_FIELD2_INITIAL_POSITION, 0);
            } else {
                b.putInt(TwoFieldsCardFragment.EXTRA_FIELD2_INITIAL_POSITION, 0);
            }
            b.putInt(TwoFieldsCardFragment.EXTRA_QA_RATIO, setting.getQaRatio());
            b.putInt(TwoFieldsCardFragment.EXTRA_SEPARATOR_COLOR, setting.getSeparatorColor());
            fragment.setArguments(b);

            configCardFragmentTransitionAnimation(ft);

            ft.replace(R.id.card_root, fragment);
            ft.commit();
        } else if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED) {
            FlipableCardFragment fragment = new FlipableCardFragment();
            Bundle b = new Bundle(1);
            CardFragment.Builder[] builders = {questionFragmentBuilder, answerFragmentBuilder, noteFragmentBuilder};
            b.putSerializable(FlipableCardFragment.EXTRA_CARD_FRAGMENT_BUILDERS, builders);
            if (showAnswer) {
                b.putInt(FlipableCardFragment.EXTRA_INITIAL_POSITION, 1);
            } else {
                b.putInt(FlipableCardFragment.EXTRA_INITIAL_POSITION, 0);
            }

            fragment.setArguments(b);

            configCardFragmentTransitionAnimation(ft);

            ft.replace(R.id.card_root, fragment);
            ft.commit();
        } else {
            assert false : "Card logic not implemented for style: " + setting.getCardStyle();
        }

        isAnswerShown = showAnswer;

        // Set up the small title bar
        // It is defualt "GONE" so it won't take any space
        // if there is no text
        smallTitleBar = (TextView) findViewById(R.id.small_title_bar);

        // Only copy to clipboard if answer is show
        // as a feature request:
        // http://code.google.com/p/anymemo/issues/detail?id=239
        if (showAnswer == true) {
            copyToClipboard();
        }

        currentDisplayedCard = getCurrentCard();
        onPostDisplayCard();
    }

    protected void displayPictureHint(boolean enablePictureHint) {
       // hintView = false;
        if (getCurrentCard().getImgPath() == null) {
            getCurrentCard().setImgPath("");
        }

        if (getCurrentCard().getImgPath().isEmpty()) {
            final int ACTIVITY_EDIT = 11;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Picture Found")
                    .setMessage("You do not have a picture associated with this flashcard. Would you like to add a picture now (HTML link only, i.e: www.website.com\\yourpicture.jpg")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(QACardActivity.this, CardEditor.class);
                            i.putExtra(CardEditor.EXTRA_DBPATH, dbPath);
                            i.putExtra(CardEditor.EXTRA_CARD_ID, getCurrentCard().getId());
                            i.putExtra(CardEditor.EXTRA_IS_EDIT_NEW, false);
                            startActivityForResult(i, ACTIVITY_EDIT);

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        } else {
            String questionTypeface = setting.getQuestionFont();
            String answerTypeface = setting.getAnswerFont();

            Setting.Align questionAlign = setting.getQuestionTextAlign();
            Setting.Align answerAlign = setting.getAnswerTextAlign();

            String questionTypefaceValue = null;
            String answerTypefaceValue = null;

            List<CardFragment.Builder> fragments = new ArrayList<>(4);

            /* Set the typeface of question and answer */
            if (!Strings.isNullOrEmpty(questionTypeface)) {
                questionTypefaceValue = questionTypeface;

            }
            if (!Strings.isNullOrEmpty(answerTypeface)) {
                answerTypefaceValue = answerTypeface;
            }

            final String[] imageSearchPaths = {
                /* Relative path */
                    "",
                /* Relative path with db name */
                    "" + FilenameUtils.getName(dbPath),
                /* Try the image in /sdcard/anymemo/images/dbname/ */
                    AMEnv.DEFAULT_IMAGE_PATH + FilenameUtils.getName(dbPath),
                /* Try the image in /sdcard/anymemo/images/ */
                    AMEnv.DEFAULT_IMAGE_PATH,
            };

            // Buttons view can be null if it is not decleared in the layout XML
            View buttonsView = findViewById(R.id.buttons_root);

            if (buttonsView != null) {
                // Make sure the buttons view are also handling the event for the answer view
                // e. g. clicking on the blank area of the buttons layout to reveal the answer
                // or flip the card.
                buttonsView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        onQuestionViewClickListener.onClick(v);
                    }
                });

                // Also the buttons should match the color of the view above.
                // It could be the question if it is the double sided card with only question shown
                // or answer view's color.
                if (!setting.isDefaultColor()) {
                    if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED &&
                            setting.getQuestionBackgroundColor() != null) {
                        buttonsView.setBackgroundColor(setting.getQuestionBackgroundColor());
                    } else if (setting.getAnswerBackgroundColor() != null) {
                        buttonsView.setBackgroundColor(setting.getAnswerBackgroundColor());
                    }
                }
            }

            CardFragment.Builder questionFragmentBuilder = getDefaultQuestionFragment(questionAlign,
                    questionTypefaceValue, imageSearchPaths);

            CardFragment.Builder answerFragmentBuilder = new CardFragment.Builder(getCurrentCard().getImgPath())
                    .setTextAlignment(answerAlign)
                    .setTypefaceFromFile(answerTypefaceValue)
                    .setTextOnClickListener(onAnswerTextClickListener)
                    .setCardOnClickListener(onAnswerViewClickListener)
                    .setTextFontSize(setting.getAnswerFontSize())
                    .setTypefaceFromFile(setting.getAnswerFont())
                    .setPictureHint(true)
                    .setHtmlLinebreakConversion(setting.getHtmlLineBreakConversion())
                    .setImageSearchPaths(imageSearchPaths)
                    .setCardField(Setting.CardField.ANSWER)
                    .setBackgroundColor(setting.getAnswerBackgroundColor())
                    .setTextColor(setting.getAnswerTextColor());

            // Note is currently shared some settings with Answer
            CardFragment.Builder noteFragmentBuilder = getDefaultNoteFragment(answerAlign,
                    answerTypefaceValue, imageSearchPaths);

            // Long click to launch image viewer if the card has an image
            questionFragmentBuilder.setTextOnLongClickListener(
                    generateImageOnLongClickListener(getCurrentCard().getQuestion(), imageSearchPaths));
            answerFragmentBuilder.setTextOnLongClickListener(
                    generateImageOnLongClickListener(getCurrentCard().getAnswer(), imageSearchPaths));

            fragments.add(answerFragmentBuilder);
            fragments.add(noteFragmentBuilder);
            fragments.add(questionFragmentBuilder);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            if (setting.getCardStyle() == Setting.CardStyle.SINGLE_SIDED) {

                singleSided(enablePictureHint, ft, fragments);

            } else if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED) {
                FlipableCardFragment fragment = new FlipableCardFragment();
                Bundle b = new Bundle(1);
                CardFragment.Builder[] builders = {questionFragmentBuilder, answerFragmentBuilder, noteFragmentBuilder};
                b.putSerializable(FlipableCardFragment.EXTRA_CARD_FRAGMENT_BUILDERS, builders);
                b.putInt(FlipableCardFragment.EXTRA_INITIAL_POSITION, 0);


                fragment.setArguments(b);

                configCardFragmentTransitionAnimation(ft);

                ft.replace(R.id.card_root, fragment);
                ft.commit();
            } else {
                assert false : "Card logic not implemented for style: " + setting.getCardStyle();
            }


            // Set up the small title bar
            // It is defualt "GONE" so it won't take any space
            // if there is no text
            smallTitleBar = (TextView) findViewById(R.id.small_title_bar);


            currentDisplayedCard = getCurrentCard();
            onPostDisplayCard();
        }
    }

    protected boolean isAnswerShown() {
        return isAnswerShown;
    }

    protected AnyMemoDBOpenHelper getDbOpenHelper() {
        return dbOpenHelper;
    }

    protected Setting getSetting() {
        return setting;
    }

    protected Option getOption() {
        return option;
    }

    /**
     * Called the loaders are done. Override it for customized initialization
     */
    protected void onPostInit() {
        View buttonsView = findViewById(R.id.buttons_root);
        if (buttonsView != null && !setting.isDefaultColor() && setting.getAnswerBackgroundColor() != null) {
            buttonsView.setBackgroundColor(setting.getAnswerBackgroundColor());
        }
    }

    private class SettingLoaderCallbacks implements
            LoaderManager.LoaderCallbacks<Setting> {

        @Override
        public Loader<Setting> onCreateLoader(int arg0, Bundle arg1) {
            Loader<Setting> loader = new SettingLoader(QACardActivity.this, dbPath);
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Setting> loader, Setting setting) {
            QACardActivity.this.setting = setting;
            multipleLoaderManager.checkAllLoadersCompleted();
        }

        @Override
        public void onLoaderReset(Loader<Setting> arg0) {
            // Do nothing now
        }
    }

    private class CardTTSUtilLoaderCallbacks implements
            LoaderManager.LoaderCallbacks<CardTTSUtil> {
        @Override
        public Loader<CardTTSUtil> onCreateLoader(int arg0, Bundle arg1) {
            Loader<CardTTSUtil> loader = new CardTTSUtilLoader(QACardActivity.this, dbPath);
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<CardTTSUtil> loader, CardTTSUtil cardTTSUtil) {
            QACardActivity.this.cardTTSUtil = cardTTSUtil;
            multipleLoaderManager.checkAllLoadersCompleted();
        }

        @Override
        public void onLoaderReset(Loader<CardTTSUtil> arg0) {
            // Do nothing now
        }
    }

    @Override
    public void onDestroy() {
        AnyMemoDBOpenHelperManager.releaseHelper(dbOpenHelper);

        if (cardTTSUtil != null) {
            cardTTSUtil.release();
        }

        multipleLoaderManager.destroy();

        /* Update the widget because StudyActivity can be accessed though widget*/
        Intent myIntent = new Intent(this, AnyMemoService.class);
        myIntent.putExtra("request_code", AnyMemoService.CANCEL_NOTIFICATION);
        startService(myIntent);

        AnyMemoWidgetProvider.updateWidget(this);
        super.onDestroy();
    }

    // Set the small title to display additional informaiton
    public void setSmallTitle(CharSequence text) {
        if (text != null && !Strings.isNullOrEmpty(text.toString())) {
            smallTitleBar.setText(text);
            smallTitleBar.setVisibility(View.VISIBLE);
        } else {
            smallTitleBar.setVisibility(View.GONE);
        }

    }

    /* Called when the card is displayed. */
    protected void onPostDisplayCard() {
        // Nothing
    }

    protected boolean speakQuestion() {
        cardTTSUtil.speakCardQuestion(getCurrentCard());
        return true;
    }

    protected boolean speakAnswer() {
        cardTTSUtil.speakCardAnswer(getCurrentCard());
        return true;
    }

    protected boolean showLetterHint(int letterHintCounter) {
        //the method is in the line 206
        displayLetterHint(true, letterHintCounter);
        return true;
    }

    protected boolean showSpellingTest() {
        //method used from line
        displaySpellingTest(true);
        return true;
    }

    protected boolean showMcHint(List<Card> deck) {
        displayMcHint(true, deck);
        return true;
    }

    protected boolean showPictureHint() {
        displayPictureHint(true);
        return true;
    }



    private void loadGestures() {
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLibrary.load()) {
            Log.e(TAG, "Gestures can not be load");
        }

        GestureOverlayView gestureOverlay = (GestureOverlayView) findViewById(R.id.gesture_overlay);
        gestureOverlay.addOnGesturePerformedListener(onGesturePerformedListener);

        // Set if gestures are enabled if set on preference
        gestureOverlay.setEnabled(option.getGestureEnabled());
    }


    // Default implementation is to handle the double sided card correctly.
    // Return true if the event is handled, else return false
    protected boolean onClickQuestionView() {
        if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED) {
            displayCard(true);
            return true;
        }
        return false;
    }

    protected boolean onClickAnswerView() {
        if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED) {
            displayCard(false);
            return true;
        }
        return false;
    }

    protected boolean onClickQuestionText() {
        if (!onClickQuestionView()) {
            speakQuestion();
        }
        return true;
    }

    protected boolean onClickAnswerText() {
        if (!onClickAnswerView()) {
            speakAnswer();
        }
        return true;
    }


    protected void onGestureDetected(GestureName gestureName) {
        // Nothing
    }

    // Return true if handled. Default not handle it.
    // This method will only be called if the volume key shortcut option is enabled.
    protected boolean onVolumeUpKeyPressed() {
        return false;
    }

    // Return true if handled. Default not handle it.
    protected boolean onVolumeDownKeyPressed() {
        return false;
    }

    // Do not handle the key down event. We handle it in onKeyUp
    // This method will only be called if the volume key shortcut option is enabled.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (option.getVolumeKeyShortcut()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    // handle the key event
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (option.getVolumeKeyShortcut()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                return onVolumeUpKeyPressed();
            }

            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                return onVolumeDownKeyPressed();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    // Copy to clipboard
    protected void copyToClipboard() {
        String copiedText = "";
        switch (option.getCopyClipboard()) {
            case QUESTION:
                copiedText = "" + currentCard.getQuestion();
                break;
            case ANSWER:
                copiedText = "" + currentCard.getAnswer();
                break;
            case BOTH:
                copiedText = "" + currentCard.getQuestion() + " " + currentCard.getAnswer();
                break;
            default:
                copiedText = "";
        }
        if (!Strings.isNullOrEmpty(copiedText)) {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            // Some Samsung device doesn't have ClipboardManager. So check
            // the null here to prevent crash.
            if (cm != null) {
                try {
                    cm.setText(copiedText);
                } catch (NullPointerException npe) {
                    // This is a Samsung clipboard bug. setPrimaryClip() can throw
                    // a NullPointerException if Samsung's /data/clipboard directory is full.
                    // Fortunately, the text is still successfully copied to the clipboard.
                    Log.e(TAG, "Got null pointer exception when copying text to clipboard");
                }
            }
        }
    }

    private Runnable onPostInitRunnable = new Runnable() {
        public void run() {
            onPostInit();
        }
    };

    /**
     * Configure the animation for the card transition in a fragment transaction.
     */
    private void configCardFragmentTransitionAnimation(FragmentTransaction ft) {
        if (option.getEnableAnimation()) {
            // The first card to display.
            if (currentDisplayedCard == null) {
                // Android support library bug prevent the exit animation displayed for nested fragment.
                // So the R.anim.slide_right_out is not actaully used.
                ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
            } else if (currentDisplayedCard.getOrdinal() > currentCard.getOrdinal()) {
                // Make sure the animation is the in the right direction.
                ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            } else if (currentDisplayedCard.getOrdinal() < currentCard.getOrdinal()) {
                ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
            } else {
                // No animation for not changing the card.
            }
        }
    }

    /**
     * Generate the OnLongClickListener on the card fragment
     *
     * @param cardText         the text on the card.
     * @param imageSearchPaths the paths to search image
     * @return the OnLongClickListener.
     */
    private CardFragment.OnLongClickListener generateImageOnLongClickListener(final String cardText, final String[] imageSearchPaths) {
        return new CardFragment.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                // Find the files in the card field
                List<String> filesFound = AMStringUtils.findFileInCardText(cardText,
                        new String[]{"jpg", "png", "bmp", "gif", "jpeg"});

                if (filesFound.size() == 0) {
                    Log.v(TAG, "No Images found for: " + cardText);
                    return false;
                }

                // Search the image file path for the file name found
                // Only the first image is used.
                List<File> imageFiles = amFileUtil.findFileInPaths(filesFound.get(0), imageSearchPaths);
                if (imageFiles.size() == 0) {
                    Log.w(TAG, "Image: " + filesFound.get(0) + "  not found for search paths.");
                    return false;
                }

                // Display the image in an image viewer
                Uri uri = Uri.parse("file://" + imageFiles.get(0).getAbsolutePath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
                return true;
            }
        };
    }

    private CardFragment.OnClickListener onQuestionTextClickListener = new CardFragment.OnClickListener() {

        @Override
        public void onClick(View v) {
            onClickQuestionText();
        }
    };

    private CardFragment.OnClickListener onAnswerTextClickListener = new CardFragment.OnClickListener() {

        @Override
        public void onClick(View v) {
            onClickAnswerText();
        }
    };

    private CardFragment.OnClickListener onQuestionViewClickListener = new CardFragment.OnClickListener() {

        @Override
        public void onClick(View v) {
            onClickQuestionView();
        }
    };
    private CardFragment.OnClickListener onAnswerViewClickListener = new CardFragment.OnClickListener() {

        @Override
        public void onClick(View v) {
            onClickAnswerView();
        }
    };

    private GestureOverlayView.OnGesturePerformedListener onGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {

        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            List<Prediction> predictions = gestureLibrary.recognize(gesture);
            if (predictions.size() > 0 && predictions.get(0).score > 3.0) {

                GestureName name = GestureName.parse(predictions.get(0).name);
                // Run the callback on the Activity.
                onGestureDetected(name);
            }

        }
    };

    private void singleSided(boolean hint, FragmentTransaction ft, List<CardFragment.Builder>
            cardFragments) {
        TwoFieldsCardFragment fragment = new TwoFieldsCardFragment();
        Bundle b = new Bundle();

        List<CardFragment.Builder> builders1List = new ArrayList<>(4);
        List<CardFragment.Builder> builders2List = new ArrayList<>(4);
        for (CardFragment.Builder each : cardFragments) {
            // Handle card field setting.

            //this is to verify the question part, which is the top part of the screen
            if (setting.getQuestionFieldEnum().contains(Setting.CardField.QUESTION) && each
                    .getCardField() == Setting.CardField.QUESTION) {
                builders1List.add(each);
            } else if (setting.getQuestionFieldEnum().contains(Setting.CardField.ANSWER) && each
                    .getCardField() == Setting.CardField.ANSWER) {
                builders1List.add(each);
            } else if (setting.getQuestionFieldEnum().contains(Setting.CardField.NOTE) && each
                    .getCardField() == Setting.CardField.NOTE) {
                builders1List.add(each);
            } else if (setting.getQuestionFieldEnum().contains(Setting.CardField.HINT) && each
                    .getCardField() == Setting.CardField.HINT) {
                builders1List.add(each);
            }

            //this is for the answer part, which is the bottom part of the screen. We need
            // another if else statement for that
            if (!hint && each.getCardField() == null) {
                builders2List.add(each);
            } else if (setting.getAnswerFieldEnum().contains(Setting.CardField.QUESTION) && each
                    .getCardField() == Setting.CardField.QUESTION) {
                builders2List.add(each);
            } else if (setting.getAnswerFieldEnum().contains(Setting.CardField.ANSWER) && each
                    .getCardField() == Setting.CardField.ANSWER) {
                builders2List.add(each);
            } else if (setting.getAnswerFieldEnum().contains(Setting.CardField.NOTE) && each
                    .getCardField() == Setting.CardField.NOTE) {
                builders2List.add(each);
            }

        }

        CardFragment.Builder[] builders1 = new CardFragment.Builder[builders1List.size()];
        builders1List.toArray(builders1);
        CardFragment.Builder[] builders2 = new CardFragment.Builder[builders2List.size()];
        builders2List.toArray(builders2);

        b.putSerializable(TwoFieldsCardFragment.EXTRA_FIELD1_CARD_FRAGMENT_BUILDERS, builders1);
        b.putSerializable(TwoFieldsCardFragment.EXTRA_FIELD2_CARD_FRAGMENT_BUILDERS, builders2);
        if (hint) {
            b.putInt(TwoFieldsCardFragment.EXTRA_FIELD2_INITIAL_POSITION, 0);
        } else {
            b.putInt(TwoFieldsCardFragment.EXTRA_FIELD2_INITIAL_POSITION, 0);
        }
        b.putInt(TwoFieldsCardFragment.EXTRA_QA_RATIO, setting.getQaRatio());
        b.putInt(TwoFieldsCardFragment.EXTRA_SEPARATOR_COLOR, setting.getSeparatorColor());
        fragment.setArguments(b);

        configCardFragmentTransitionAnimation(ft);

        ft.replace(R.id.card_root, fragment);
        ft.commit();
    }
}
