package com.ewelion_p.flagsquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class QuizActivityFragment extends Fragment {


    /* TAG used to track errors */
    private static final String TAG = "QuizWithFlags Activity";

    /* Amount of flags in quiz */
    private static final int FLAGS_IN_QUIZ = 10;

    /* Names of files with flags */
    private List<String> fileNameList;

    /* List of files (countries) included in quiz */
    private List<String> quizCountriesList;

    /* Selected regions included in quiz */
    private Set<String> regionSet;

    /* Correct name of the country linked with a flag */
    private String correctAnswer;

    /* Amount of all answers */
    private int totalGuesses;

    /* Amount of correct answers */
    private int correctAnswers;

    /* Amount of rows with buttons (with names of countries) */
    private int guessRows;

    /* Object used to generate strong random number */
    private SecureRandom random;

    /* Object used to delay loading next flag */
    private Handler handler;

    /* Animation for wrong answer */
    private Animation shakeAnimation;

    /* Main layout for quiz */
    private LinearLayout quizLinearLayout;

    /* TextView with a number of the current question */
    private TextView questionNumberTextView;

    /* ImageView with a current flag */
    private ImageView flagImageView;

    /* Array with the buttons with possible answers */
    private LinearLayout[] guessLinearLayouts;

    /* TextView showing the correct answer or info that the answer is wrong */
    private TextView answerTextView;

    /* Object used to play a sound after a correct answer */
    MediaPlayer player;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        /* Gets a layout for the fragment */
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        /* Initialisation of the main widgets */
        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        /* Initialization of the animation */
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        /* Initialization of 'user interface' */
        quizLinearLayout = view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        flagImageView = view.findViewById(R.id.flagImageView);

        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);
        answerTextView = view.findViewById(R.id.answerTextView);

        /* Registers listeners for the buttons */
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        /* Sets text for the textView with question number */
        questionNumberTextView.setText(getString(R.string.question, 1, FLAGS_IN_QUIZ));

        /* Registers a sound for the player */
        player = MediaPlayer.create(getActivity(), R.raw.applause);


        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void updateGuessRows(SharedPreferences sharedPreferences) {

        /* Gets information how many buttons to show */
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);

        /* Amount of rows with buttons (5 buttons per row) */
        guessRows = Integer.parseInt(choices) / 2;

        /* Hides all rows with buttons */
        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility(View.GONE);
        }

        /* Shows the selected amount of buttons */
        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }

    }


    public void updateRegions(SharedPreferences sharedPreferences) {

        /* Gets the selected regions */


        regionSet = sharedPreferences.getStringSet(MainActivity.REGIONS, null);


    }

    public void resetQuiz() {

        /* Gets access to Assets */
        AssetManager assets = getActivity().getAssets();

        /* Clears the list with names of flags */
        fileNameList.clear();

        /* Gets names of files with flags from selected regions */
        try {

            /* Loop through every region = every directory in Assets folder */
            for (String region : regionSet) {

                /* Gets files names from every region directory */
                String[] paths = assets.list(region);

                /* Removes .png from every file name */
                for (String path : paths) {
                    fileNameList.add(path.replace(".png", ""));
                }

            }

        } catch (IOException ex) {
            Log.e(TAG, "Error while loading the flags", ex);
        }

        /* Resets correct answers and all answers counters */
        correctAnswers = 0;
        totalGuesses = 0;

        /* Clears a list of countries */
        quizCountriesList.clear();

        /* Initialisation of variables used to draw flags */
        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();

        /* Draws flags */
        while (flagCounter <= FLAGS_IN_QUIZ) {

            /* Chooses a random number from 0 to amount of flags selected in quiz */
            int randomIndex = random.nextInt(numberOfFlags);

            /* Gets a name of file of randomly selected index */
            String fileName = fileNameList.get(randomIndex);

            /* If the file has not been selected yet, adds the file to the list */
            if (!quizCountriesList.contains(fileName)) {
                quizCountriesList.add(fileName);
                ++flagCounter;
            }
        }

        /* load a flag */
        loadNextFlag();

    }

    private void loadNextFlag() {

        /* Gets a name of the current/selected flag */
        String nextImage = quizCountriesList.remove(0);

        /* Updates the TextView */
        correctAnswer = nextImage;

        /* Clears the TextView */
        answerTextView.setText("");

        /* Shows the number of the current question */
        questionNumberTextView.setText(getString(R.string.question, (correctAnswers + 1), FLAGS_IN_QUIZ));

        /* Gets a name of the region of the randomly selected flag */
        String region = nextImage.substring(0, nextImage.indexOf("-"));

        /* Gets access to Assets */
        AssetManager assets = getActivity().getAssets();

        try (InputStream inputStreamFlag = assets.open(region + "/" + nextImage + ".png")) {

            /* Loads a flag as a Drawable object */
            Drawable flag = Drawable.createFromStream(inputStreamFlag, nextImage);

            /* Sets the Drawable object in the ImageView widget */
            flagImageView.setImageDrawable(flag);

            /* Flag appears on the screen (animation) */
            animate(false);

        } catch (IOException e) {
            Log.e(TAG, "loadNextFlag: Error while loading " + nextImage, e);
        }

        /* Shuffles file names */
        Collections.shuffle(fileNameList);

        /* Adds the correct answer at the end of the list */
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        /* Adds text to the buttons with possible answers */
        for (int row = 0; row < guessRows; row++) {
            for (int column = 0; column < 2; column++) {

                /* Gets access to a button and changes its state to 'enabled' */
                Button guessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                guessButton.setEnabled(true);

                /* Gets a name of the country and adds as a text to the button */
                String fileName = fileNameList.get((row * 2) + column);
                guessButton.setText(getCountryName(fileName));
            }
        }

        /* Adds a correct answer to a random button */
        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        String countryName = getCountryName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(countryName);
    }


    private String getCountryName(String fileName) {
        return fileName.substring(fileName.indexOf("-") + 1).replace("_", " ");
    }

    private void animate(boolean outAnimate) {

        /* No animation for 1st flag */
        if (correctAnswers == 0) return;

        /* Find a centre of the quizLinearLayout */
        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;

        /* Calculate a radius of the animation */
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());

        /* Animation object */
        Animator animator;

        /* Animation for hiding a flag */
        if (outAnimate) {

            /* Creates an animation */
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, radius, 0);

            /* When the animation finishes.. */
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextFlag();
                }
            });
        }

        /* Opening flag animation */
        else {
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, 0, radius);
        }

        /* Sets time for the animation */
        animator.setDuration(500);

        /* Starts animation */
        animator.start();

    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /* Gets text from the selected button */
            Button guessButton = (Button) v;
            String guess = guessButton.getText().toString();
            String answer = getCountryName(correctAnswer);

            /* Incrementation of all answers */
            ++totalGuesses;

            /* If the answer is correct */
            if (guess.equals(answer)) {

                /* Incrementation of correct answers */
                ++correctAnswers;

                /* Lets user know that the answer was correct */
                answerTextView.setText(getString(R.string.correct_answer_reply, answer));
                player.start();
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getContext().getTheme()));

                /* Disable all buttons */
                disableButtons();

                /* If the user answered all questions... */
                if (correctAnswers == FLAGS_IN_QUIZ) {

                    /* Creates AlertDialog object with a text and a reset quiz button */
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.YourAlertDialogTheme);
                    builder.setTitle(R.string.quiz_results);
                    builder.setMessage(getString(R.string.results, totalGuesses, (1000 / (double) totalGuesses)));
                    builder.setPositiveButton(R.string.reset_quiz, (dialog, which) -> resetQuiz());

                    builder.setCancelable(false);
                    builder.show();
                }

                /* If the user has not answer all questions.. */
                else {

                    /* Wait 2s and load next flag */
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animate(true);
                        }
                    }, 3000);
                }
            }

            /* If the answer is incorrect... */
            else {
                /* Plays animation for a shaking flag */
                flagImageView.startAnimation(shakeAnimation);

                /* Lets the user know that the answer was incorrect */
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));

                /* Disables a button with the incorrect answer */
                guessButton.setEnabled(false);
            }


        }
    };

    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int column = 0; column < 2; column++) {
                guessRow.getChildAt(column).setEnabled(false);
            }
        }
    }

}