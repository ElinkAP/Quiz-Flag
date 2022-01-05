package com.ewelion85.flagsquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
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

public class MainActivityFragment extends Fragment {


    /* Znacznik uzywany przy zapisie bledow w dzienniku Log */
    private static final String TAG = "QuizWithFlags Activity";

    /* Liczba flag bioracych udzial w quizie */
    private static final int FLAGS_IN_QUIZ = 10;

    /* Nazwy plikow z obrazami flag */
    private List<String> fileNameList;

    /* Lista plikow3 z obrazami flag bioracych udzial w biezacym quizie */
    private List<String> quizCountriesList;

    /* Wybrane obszary biorace udzial w quizie */
    private Set<String> regionSet;

    /* Poprawna nazwa kraju przypisana do biezacej flagi */
    private String correctAnswer;

    /* Calkowita liczba odpowiedzi */
    private int totalGuesses;

    /* Liczba poprawnych odpowiedzi */
    private int correctAnswers;

    /* Liczba wierszy przyciskow odpowiedzi wyswietlanych na ekranie */
    private int guessRows;

    /* Obiekt sluzacy do losowania */
    private SecureRandom random;

    /* Obiekt uzywany podczas opozniania procesu ladowania kolejnej flagi w quizie */
    private Handler handler;

    /* Animacja blednej odpowiedzi */
    private Animation shakeAnimation;

    /* Glowny rozklad aplikacji */
    private LinearLayout quizLinearLayout;

    /* Widok wyswietlajacy numer biezacego pytania quizu */
    private TextView questionNumberTextView;

    /* Widok wyswietlajacy biezaca flage */
    private ImageView flagImageView;

    /* Tablica zawierajaca wiersze przyciskow odpowiedzi */
    private LinearLayout[] guessLinearLayouts;

    /* Widok wyswietlajacy poprawna odpowiedz w quizie */
    private TextView answerTextView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        /* Zainicjowanie graficznego interfejsu uzytkownika dla fragmentu */
        super.onCreateView(inflater, container, savedInstanceState);

        /* Pobranie rozkladu dla fragmentu */
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        /* Inicjalizacja wybranych pol */
        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        /* Inicjalizacja animacji */
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        /* Inicjalizacja komponentow graficznego interfejsu uzytkownika */
        quizLinearLayout = view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        flagImageView = view.findViewById(R.id.flagImageView);

        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);
        answerTextView = view.findViewById(R.id.answerTextView);

        /* Konfiguracja nasluchiwania zdarzen w przyciskach odpowiedzi */
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        /* Wyswietlenie formatowanego tekstu w widoku TextView */
        questionNumberTextView.setText(getString(R.string.question, 1, FLAGS_IN_QUIZ));

        /* Zwroc widok fragmentu do wyswietlenia */
        return view;

    }

    public void updateGuessRows(SharedPreferences sharedPreferences) {

        /* Pobranie informacji o ilosci przyciskow odpowiedzi do wyswietlenia */
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);

        /* Liczba wierszy z przyciskami odpowiedzi do wyswietlenia */
        guessRows = Integer.parseInt(choices) / 2;

        /* Ukrycie wszystkich wierszy z przyciskami */
        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility(View.GONE);
        }

        /* Wyswietlenie okreslonej liczby wierszy z przyciskami odpowiedzi */
        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }

    }

    public void updateRegions(SharedPreferences sharedPreferences) {

        /* Pobranie informacji na temat wybranych przez uzytkownika obszarow */
        regionSet = sharedPreferences.getStringSet(MainActivity.REGIONS, null);
    }

    public void resetQuiz() {

        /* Uzyskaj dostep do folderu assets */
        AssetManager assets = getActivity().getAssets();

        /* Wyczysc liste z nazwami flag */
        fileNameList.clear();

        /* Pobierz nazwy plikow obrazow flag z wybranych przez uzytkownika obszarow */
        try {

            /* Petla przechodzaca przez kazdy obszar - czyli przez kazdy folder w folderze assets */
            for (String region : regionSet) {

                /* Pobranie nazw wszystkich plikow znajdujacych sie w folderze danego obszaru */
                String[] paths = assets.list(region);

                /* Usuniecie z nazw plikow ich rozszerzenia formatu */
                for (String path : paths) {
                    fileNameList.add(path.replace(".png", ""));
                }

            }

        } catch (IOException ex) {
            Log.e(TAG, "Error while loading the flags", ex);
        }

        /* Zresetowanie liczby poprawnych i wszystkich udzielonych odpowiedzi */
        correctAnswers = 0;
        totalGuesses = 0;

        /* Wyczyszczenie listy krajow */
        quizCountriesList.clear();

        /* Inicjalizacja zmiennych wykorzystywanych przy losowaniu flag */
        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();

        /* Losowanie flag */
        while (flagCounter <= FLAGS_IN_QUIZ) {

            /* Wybierz losowa wartosc z zakresu od "0" do "liczby flag" bioracych udzial w quizie */
            int randomIndex = random.nextInt(numberOfFlags);

            /* Pobierz nazwe pliku o wylosowanym indeksie */
            String fileName = fileNameList.get(randomIndex);

            /* Jezeli plik o tej nazwie nie zostal jeszcze wylosowany, to dodaj go do listy wybranych krajow */
            if (!quizCountriesList.contains(fileName)) {
                quizCountriesList.add(fileName);
                ++flagCounter;
            }
        }

        /* Zaladuj flage */
        loadNextFlag();

    }

    private void loadNextFlag() {

        /* Ustalenie nazwy pliku biezacej flagi */
        String nextImage = quizCountriesList.remove(0);

        /* Zaktualizowanie widoku TextView */
        correctAnswer = nextImage;

        /* Wyczyszczenie widoku TextView */
        answerTextView.setText("");

        /* Wyswietlenie numeru biezacego pytania*/
        questionNumberTextView.setText(getString(R.string.question, (correctAnswers + 1), FLAGS_IN_QUIZ));

        /* Pobieranie nazwy obszaru biezacej flagi */
        String region = nextImage.substring(0, nextImage.indexOf("-"));

        /* Uzyskanie dostepu do folderu assets */
        AssetManager assets = getActivity().getAssets();

        try (InputStream inputStreamFlag = assets.open(region + "/" + nextImage + ".png")) {

            /* Zaladowanie obrazu flagi jako obiekt Drawable */
            Drawable flag = Drawable.createFromStream(inputStreamFlag, nextImage);

            /* Obsadzenie obiektu Drawable (flagi) w widoku ImageView */
            flagImageView.setImageDrawable(flag);

            /* Animacja wejscia flagi na ekran */
            animate(false);

        } catch (IOException e) {
            Log.e(TAG, "loadNextFlag: Error while loading " + nextImage, e);
        }

        /* Przemieszanie nazw plikow */
        Collections.shuffle(fileNameList);

        /* Umieszczenie prawidlowej odpowiedzi na koncu listy */
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        /* Dodanie tekstu do przyciskow odpowiedzi */
        for (int row = 0; row < guessRows; row++) {
            for (int column = 0; column < 2; column++) {

                /* Uzyskanie dostepu do przycisku i zmienienie jego stanu na 'enabled' */
                Button guessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                guessButton.setEnabled(true);

                /* Pobierz nazwe kraju i ustaw ja w widoku Button */
                String fileName = fileNameList.get((row * 2) + column);
                guessButton.setText(getCountryName(fileName));
            }
        }

        /* Dodanie poprawnej odpowiedzi do losowo wybranego przycisku */
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

        /* Nie tworzymy animacji przy wyswietlaniu pierwszej flagi */
        if (correctAnswers == 0) return;

        /* Find a centre of the quizLinearLayout */
        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;

        /* Calculate a radius of the animation */
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());

        /* Zdefiniowanie obiektu animacji */
        Animator animator;

        /* Wariant animacji akrywajacej flage */
        if (outAnimate) {

            /* Utworzenie aniamcji */
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, radius, 0);

            /* Gdy animacja sie skonczy... */
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

        /* Okreslenie czasu trwania animacji */
        animator.setDuration(500);

        /* Uruchomienie animacji */
        animator.start();

    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /* Pobranie nacisnietego przycisku oraz wyswietlanego przez niego tekstu */
            Button guessButton = (Button) v;
            String guess = guessButton.getText().toString();
            String answer = getCountryName(correctAnswer);

            /* Inkrementacja liczby odpowiedzi udzielonych przez uzytkownika w quizie */
            ++totalGuesses;

            /* Jezeli udzielona odpowiedz jest poprawna */
            if (guess.equals(answer)) {

                /* Inkrementacja liczby poprawnych odpowiedzi */
                ++correctAnswers;

                /* Wyswietlenie informacji zwrotnej dla uzytkownika o udzieleniu poprawnej odpowiedzi */
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getContext().getTheme()));

                /* Dezaktywacja wszystkich przyciskow odpowiedzi */
                disableButtons();

                /* Jezeli uzytkownik udzielil odpowiedzi na wszystkie pytania */
                if (correctAnswers == FLAGS_IN_QUIZ){
                    /* Utworzenie obiektu AlertDialog ze spersonalizowanym tekstem oraz przyciskiem */
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Quiz results");
                    builder.setMessage(getString(R.string.results, totalGuesses, (1000 / (double) totalGuesses)));
                    builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetQuiz();
                        }
                    });

                    builder.setCancelable(false);
                    builder.show();
                }

                /* Jezeli uzytkownik nie udzielil odpowiedzi na wszystkie pytania */
                else {

                    /* Wait 2s and load next flag */
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animate(true);
                        }
                    }, 2000);

                }

            }

            /* Jezeli udzielona odpowiedz nie jest poprawna */
            else {
                /* Odtworzenie animacji trzesacej sie flagi */
                flagImageView.startAnimation(shakeAnimation);

                /* Wyswietlenie informacji zwrotnej dla uzytkownika o udzieleniu blednej odpowiedzi */
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));

                /* Dezaktywacja przycisku z bledna odpowiedzia */
                guessButton.setEnabled(false);
            }


        }
    };

    private void disableButtons(){
        for (int row = 0; row < guessRows; row++){
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int column = 0; column <2; column++){
                guessRow.getChildAt(column).setEnabled(false);
            }
        }
    }

}