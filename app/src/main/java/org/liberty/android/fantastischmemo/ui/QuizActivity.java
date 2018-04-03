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

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.common.base.Strings;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.entity.Category;
import org.liberty.android.fantastischmemo.entity.Option;
import org.liberty.android.fantastischmemo.entity.Setting;
import org.liberty.android.fantastischmemo.modules.AppComponents;
import org.liberty.android.fantastischmemo.queue.QueueManager;
import org.liberty.android.fantastischmemo.queue.QuizQueueManager;
import org.liberty.android.fantastischmemo.scheduler.Scheduler;
import org.liberty.android.fantastischmemo.ui.loader.DBLoader;
import org.liberty.android.fantastischmemo.utils.DictionaryUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class QuizActivity extends QACardActivity {
    public static String EXTRA_CATEGORY_ID = "category_id";
    public static String EXTRA_START_CARD_ORD = "start_card_ord";
    public static String EXTRA_QUIZ_SIZE = "quiz_size";
    public static String EXTRA_SHUFFLE_CARDS = "shuffle_cards";
    public static String EXTRA_QUIZ_HINT = "quiz_hint";
    public static String EXTRA_START_CARD_ID = "start_card_id";

    /* UI elements */
    private GradeButtonsFragment gradeButtonsFragment;
    private ImageView mImageView;
    /* Settings */
    private Setting setting;
    private Option option;

    /* Utils */
    @Inject
    DictionaryUtil dictionaryUtil;

    private QuizQueueManager queueManager;

    private int startCardId = -1;
    private int categoryId = -1;
    private int startCardOrd = -1;
    private int quizSize = -1;


    private ImageView rlIcon1;
    private ImageView rlIcon2;
    private ImageView rlIcon3;


    private boolean isNewCardsCompleted = false;

    private boolean shuffleCards = false;

    private boolean showQuizHint = false;

    private int totalQuizSize = -1;
    //the hintCounter is to count how many times the user clicks the letter hint button
    private int letterHintCounter = 0;

    //Random variable declared
    private static SecureRandom random = new SecureRandom();

    @Override
    public int getContentView() {
        return R.layout.qa_card_layout_study;
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
        setting = getSetting();
        option = getOption();

        createQueue();

        // Keep track the initial total quiz size.
        totalQuizSize = queueManager.getNewQueueSize();

        /* Run the learnQueue init in a separate thread */
        if (startCardId != -1) {
            setCurrentCard(queueManager.dequeuePosition(startCardId));
        } else {
            setCurrentCard(queueManager.dequeue());
        }
        if (getCurrentCard() == null) {
            showNoItemDialog();
            return;
        }
        setupGradeButtons();
        displayCard(false);
        setSmallTitle(getActivityTitleString());
        setTitle(getDbName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        categoryId = extras.getInt(EXTRA_CATEGORY_ID, -1);
        startCardOrd = extras.getInt(EXTRA_START_CARD_ORD, -1);
        quizSize = extras.getInt(EXTRA_QUIZ_SIZE, -1);
        shuffleCards = extras.getBoolean(EXTRA_SHUFFLE_CARDS, false);
        showQuizHint = extras.getBoolean(EXTRA_QUIZ_HINT, false);

        if (showQuizHint == true) {
            final ImageView fabIconNew = new ImageView(this);
            fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.hint));
            final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                    .setContentView(fabIconNew)
                    .build();

            SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
            rlIcon1 = new ImageView(this);
            rlIcon2 = new ImageView(this);
            rlIcon3 = new ImageView(this);

            rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.hang_man));
            rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.multiple_choice));
            rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.picture_icon));

            final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                    .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                    .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                    .attachTo(rightLowerButton)
                    .build();

            rlIcon1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    letter_hint();
                }
            });
            rlIcon2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isAnswerShown()) {
                        multiple_choie();
                    }
                }
            });

            rlIcon3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isAnswerShown()) {
                        showPictureHint();
                    }
                }
            });
            rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
                @Override
                public void onMenuOpened(FloatingActionMenu menu) {
                    // Rotate the icon of rightLowerButton 45 degrees clockwise
                    fabIconNew.setRotation(0);
                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                    animation.start();
                }

                @Override
                public void onMenuClosed(FloatingActionMenu menu) {
                    // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                    fabIconNew.setRotation(45);
                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                    animation.start();
                }


            });
        }

        Log.d("Quiz hint mode: ", String.valueOf(showQuizHint));
        if (savedInstanceState != null) {
            startCardId = savedInstanceState.getInt(EXTRA_START_CARD_ID, -1);
        }

        getMultipleLoaderManager().registerLoaderCallbacks(3, new QuizQueueManagerLoaderCallbacks
                (), false);

        startInit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Card currentCard = getCurrentCard();
        if (currentCard != null) {
            outState.putInt(EXTRA_START_CARD_ID, currentCard.getId());
        }
    }

    public boolean onCreateHintOptions(MenuItem item) {

        Button letterButton = (Button) findViewById(R.id.letter_hint);
        Button pictureButton = (Button) findViewById(R.id.picture_hint);
        Button choiceButton = (Button) findViewById(R.id.multiple_choice_hint);

        letterButton.setVisibility(View.VISIBLE);
        pictureButton.setVisibility(View.VISIBLE);
        choiceButton.setVisibility(View.VISIBLE);
        showHintOption();
        switch (item.getItemId()) {/*
            if (R.id.picture_hint | R.id.multiple_choice_hint | R.id.letter_hint){
                return true;
            }*/

            case R.id.picture_hint: {
                if (!isAnswerShown()) {
                    showPictureHint();
                    //resetting the letter counter every time the picture is called
                    letterHintCounter = 0;
                }
                break;
            }
            case R.id.multiple_choice_hint: {

                List<Card> mcCards = new ArrayList<>();

                //find random index for array
                for (int i = 0; i < 3; i++) {
                    int randomNumb = random.nextInt(queueManager.getAllCards().size());
                    while (randomNumb == 0) {
                        randomNumb = random.nextInt(queueManager.getAllCards().size());
                    }
                    mcCards.add(queueManager.getAllCards().get(randomNumb));
                }

                if (!isAnswerShown()) {
                    showMcHint(mcCards);
                    //resetting the letter counter every time the multiple choice is called
                    letterHintCounter = 0;
                }
                break;
            }
            case R.id.letter_hint: {

                if (!isAnswerShown()) {
                    //every time the button gets clicked, counter increases by 1
                    letterHintCounter++;
                    //showLetterhint() is in QACardActivity.java
                    showLetterHint(letterHintCounter);
                }

                //reset the counter if the answer is shown
                //otherwise it will affect the next answer which will not start from 0
                else {
                    letterHintCounter = 0;
                }
            }
        }
        return  false;

    }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.quiz_activity_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.menu_lookup: {
                    dictionaryUtil.showLookupListDialog("" + getCurrentCard().getQuestion() + " " +
                            getCurrentCard().getAnswer());
                    break;
                }
                case R.id.menu_speak_question: {
                    speakQuestion();
                    break;
                }
                case R.id.menu_speak_answer: {
                    speakAnswer();
                    break;
                }
                case R.id.hint_option: {
                    //Method that allows the user to view the available hint options
                    showHintOption();
                    break;

                }
                case R.id.menu_paint: {
                    Intent myIntent = new Intent(this, PaintActivity.class);
                    startActivity(myIntent);
                }
            }
            return false;
        }


        protected boolean onClickQuestionText () {
            if ((option.getSpeakingType() == Option.SpeakingType.AUTOTAP
                    || option.getSpeakingType() == Option.SpeakingType.TAP)) {
                speakQuestion();
            } else {
                onClickQuestionView();
            }
            return true;
        }

        @Override
        protected boolean onClickAnswerText () {
            if (!isAnswerShown()) {
                onClickAnswerView();
            } else if ((option.getSpeakingType() == Option.SpeakingType.AUTOTAP
                    || option.getSpeakingType() == Option.SpeakingType.TAP)) {
                speakAnswer();
            }
            return true;
        }

        @Override
        protected boolean onClickQuestionView () {
            if (!isAnswerShown()) {
                displayCard(true);
            }
            return true;
        }

        @Override
        protected boolean onClickAnswerView () {
            if (!isAnswerShown()) {
                displayCard(true);
                //when the answer of a flash card is shown, the counter of the letterhint is set back
                // to 0
                letterHintCounter = 0;
            } else if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED && isAnswerShown()) {
                displayCard(false);
            }
            return true;
        }

        @Override
        protected boolean onVolumeUpKeyPressed () {
            if (isAnswerShown()) {
                gradeButtonsFragment.gradeCurrentCard(0);
                Toast.makeText(this, getString(R.string.grade_text) + " 0", Toast.LENGTH_SHORT).show();
            } else {
                displayCard(true);
            }

            return true;
        }

        @Override
        protected boolean onVolumeDownKeyPressed () {
            if (isAnswerShown()) {
                gradeButtonsFragment.gradeCurrentCard(3);
                Toast.makeText(this, getString(R.string.grade_text) + " 3", Toast.LENGTH_SHORT).show();
            } else {
                displayCard(true);
            }
            return true;
        }

        public static class QuizQueueManagerLoader extends
                DBLoader<QueueManager> {

            private int filterCategoryId = -1;

            private int startCardOrd = -1;

            private int quizSize = 0;

            private boolean shuffleCards = false;

            @Inject
            Scheduler scheduler;

            public QuizQueueManagerLoader(AppComponents appComponents,
                                          String dbPath, int filterCategoryId,
                                          int startCardOrd, int quizSize,
                                          boolean shuffleCards) {
                super(appComponents.applicationContext(), dbPath);
                appComponents.inject(this);

                this.filterCategoryId = filterCategoryId;

                this.startCardOrd = startCardOrd;

                this.quizSize = quizSize;

                this.shuffleCards = shuffleCards;

            }

            @Override
            public QueueManager dbLoadInBackground() {
                Category filterCategory = null;

                if (filterCategoryId != -1) {
                    filterCategory = dbOpenHelper.getCategoryDao().queryForId(filterCategoryId);
                }

                QuizQueueManager.Builder builder = new QuizQueueManager.Builder()
                        .setDbOpenHelper(dbOpenHelper)
                        .setScheduler(scheduler)
                        .setStartCardOrd(startCardOrd)
                        .setFilterCategory(filterCategory)
                        .setShuffle(shuffleCards);

                if (startCardOrd != -1) {
                    builder.setStartCardOrd(startCardOrd)
                            .setQuizSize(quizSize);
                }

                return builder.build();
            }

        }

        private class QuizQueueManagerLoaderCallbacks implements
                LoaderManager.LoaderCallbacks<QueueManager> {
            @Override
            public Loader<QueueManager> onCreateLoader(int arg0, Bundle arg1) {
                Loader<QueueManager> loader = new QuizQueueManagerLoader(appComponents(), getDbPath(),
                        categoryId, startCardOrd, quizSize, shuffleCards);
                loader.forceLoad();
                return loader;
            }

            @Override
            public void onLoadFinished(Loader<QueueManager> loader, QueueManager queueManager) {
                QuizActivity.this.queueManager = (QuizQueueManager) queueManager;
                getMultipleLoaderManager().checkAllLoadersCompleted();
            }

            @Override
            public void onLoaderReset(Loader<QueueManager> arg0) {
                // Do nothing now
            }
        }


            private void createQueue () {
            }

            @Override
            public void onPostDisplayCard () {
                // When displaying new card, we should stop the TTS reading.
                getCardTTSUtil().stopSpeak();

                if (isAnswerShown()) {
                    gradeButtonsFragment.setVisibility(View.VISIBLE);
                } else {
                    // The grade button should be gone for double sided cards.
                    if (setting.getCardStyle() == Setting.CardStyle.DOUBLE_SIDED) {
                        gradeButtonsFragment.setVisibility(View.GONE);
                    } else {
                        gradeButtonsFragment.setVisibility(View.INVISIBLE);
                    }
                }
            }

            private void setupGradeButtons () {
                gradeButtonsFragment = new GradeButtonsFragment();

                Bundle args = new Bundle();
                args.putString(GradeButtonsFragment.EXTRA_DBPATH, getDbPath());
                gradeButtonsFragment.setArguments(args);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.buttons_root, gradeButtonsFragment);
                ft.commit();

                gradeButtonsFragment.setOnCardChangedListener(onCardChangedListener);
            }

            private CharSequence getActivityTitleString () {
                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.quiz_text) + ": " + (totalQuizSize - queueManager
                        .getNewQueueSize()) + "/" + totalQuizSize + " ");
                sb.append(getString(R.string.review_short_text) + ": " + queueManager.getReviewQueueSize
                        () + " ");
                sb.append(getString(R.string.id_text) + ": " + getCurrentCard().getId() + " ");
                if (!Strings.isNullOrEmpty(getCurrentCard().getCategory().getName())) {
                    sb.append(getString(R.string.category_short_text) + ": " + getCurrentCard()
                            .getCategory().getName());
                }
                return sb.toString();
            }

    /* Called when all quiz is completed */
            private void showCompleteAllDialog () {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.quiz_completed_text)
                        .setMessage(R.string.quiz_complete_summary)
                        .setPositiveButton(R.string.back_menu_text, flushAndQuitListener)
                        .setCancelable(false)
                        .show();
            }

    /* Called when all new cards are completed. */
            private void showCompleteNewDialog ( int correct){
                LayoutInflater layoutInflater
                        = (LayoutInflater) getApplicationContext().getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.quiz_summary_dialog, null);
                TextView scoreView = (TextView) view.findViewById(R.id.score_text);
                int score = correct * 100 / totalQuizSize;

                scoreView.setText("" + score + "% (" + correct + "/" + totalQuizSize + ")");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.quiz_completed_text)
                        .setView(view)
                        .setPositiveButton(R.string.review_text, null)
                        .setNegativeButton(R.string.cancel_text, flushAndQuitListener)
                        .setCancelable(false)
                        .show();
            }
            //multiple choice option
            private void multiple_choie () {
                List<Card> mcCards = new ArrayList<>();
                //find random index for array
                for (int i = 0; i < 3; i++) {
                    int randomNumb = random.nextInt(queueManager.getAllCards().size());
                    while (randomNumb == 0) {
                        randomNumb = random.nextInt(queueManager.getAllCards().size());
                    }
                    mcCards.add(queueManager.getAllCards().get(randomNumb));
                }

                if (!isAnswerShown()) {
                    showMcHint(mcCards);
                    //resetting the letter counter every time the multiple choice is called
                    letterHintCounter = 0;
                }
            }

            //letter hint option
            private void letter_hint () {
                if (!isAnswerShown()) {
                    //every time the button gets clicked, counter increases by 1
                    letterHintCounter++;
                    //showLetterhint() is in QACardActivity.java
                    showLetterHint(letterHintCounter);
                }

                //reset the counter if the answer is shown
                //otherwise it will affect the next answer which will not start from 0
                else {
                    letterHintCounter = 0;
                }
            }
            // Current flush is not functional. So this method only quit and does not flush
            // the queue.
            private DialogInterface.OnClickListener flushAndQuitListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    };

            private GradeButtonsFragment.OnCardChangedListener onCardChangedListener =
                    new GradeButtonsFragment.OnCardChangedListener() {
                        public void onCardChanged(Card prevCard, Card updatedCard) {
                            gradeButtonsFragment.setVisibility(View.INVISIBLE);

                            // Run the task to update the updatedCard in the queue
                            // and dequeue the next card
                            ChangeCardTask task = new ChangeCardTask(QuizActivity.this, updatedCard);
                            task.execute();
                        }
                    };

            // Task to change the card after a card is graded
            // It needs to update the old card and dequeue the new card
            // and display it.
            private class ChangeCardTask extends AsyncTask<Void, Void, Card> {

                private int newQueueSizeBeforeDequeue;

                private int reviewQueueSizeBeforeDequeue;

                private Card updatedCard;

                public ChangeCardTask(Context context, Card updatedCard) {
                    this.updatedCard = updatedCard;
                }

                @Override
                protected Card doInBackground(Void... voids) {
                    queueManager.remove(getCurrentCard());
                    queueManager.update(updatedCard);

                    // Keep track of two values to dermine when to display dialog
                    // to promote the quiz completion
                    newQueueSizeBeforeDequeue = queueManager.getNewQueueSize();
                    reviewQueueSizeBeforeDequeue = queueManager.getReviewQueueSize();

                    Card nextCard = queueManager.dequeue();
                    return nextCard;
                }

                @Override
                protected void onPostExecute(Card result) {
                    setProgressBarIndeterminateVisibility(false);
                    if (result == null) {
                        showCompleteAllDialog();
                        return;
                    }

                    if (newQueueSizeBeforeDequeue <= 0 && !isNewCardsCompleted) {
                        showCompleteNewDialog(totalQuizSize - reviewQueueSizeBeforeDequeue);
                        isNewCardsCompleted = true;
                    }

                    // Stat data
                    setCurrentCard(result);
                    displayCard(false);
                    setSmallTitle(getActivityTitleString());
                }
            }

            private void showNoItemDialog () {
                new AlertDialog.Builder(this)
                        .setTitle(this.getString(R.string.memo_no_item_title))
                        .setMessage(this.getString(R.string.memo_no_item_message))
                        .setNeutralButton(getString(R.string.back_menu_text), new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                    /* Finish the current activity and go back to the last activity.
                     * It should be the open screen. */
                                finish();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            }

            //adding an inner class to override the getCurrentCard of QuizActivity.java with public instead
            // of protected access. The protected method is not accessible when mock testing in the
            // QACardActivityTest.java. Therefore, we must override it to give it public access.
            public class QuizActivityPublicAccess extends QuizActivity {
                @Override
                public Card getCurrentCard() {
                    return super.getCurrentCard();
                }
            }
        }
