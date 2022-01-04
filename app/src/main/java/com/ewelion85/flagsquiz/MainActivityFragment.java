package com.ewelion85.flagsquiz;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)

    {



        /* Zainicjowanie graficznego interfejsu uzytkownika dla fragmentu */
//        super.onCreateView(inflater, container, savedInstanceState);

        /* Pobranie rozkladu dla fragmentu */
        View view = inflater.inflate(R.layout.fragment_main, container, false);

//        /* Inicjalizacja wybranych pol */
//        fileNameList = new ArrayList<>();
//        quizCountriesList = new ArrayList<>();
//        random = new SecureRandom();
//        handler = new Handler();
//
//        /* Inicjalizacja animacji */
//        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
//        shakeAnimation.setRepeatCount(3);

//        /* Inicjalizacja komponentow graficznego interfejsu uzytkownika */
//        quizLinearLayout = view.findViewById(R.id.quizFragment);
//        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
//        flagImageView = view.findViewById(R.id.flagImageView);
//
//        guessLinearLayouts = new LinearLayout[4];
//        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
//        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
//        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
//        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);
//        answerTextView = view.findViewById(R.id.answerTextView);

       /* Konfiguracja nasluchiwania zdarzen w przyciskach odpowiedzi */
//        for (LinearLayout row: guessLinearLayouts){
//            for (int column = 0; column < row.getChildCount(); column++){
//                Button button = (Button) row.getChildAt(column);
////                button.setOnClickListener(guessButtonListener);
//            }
//        }

        /* Wyswietlenie formatowanego tekstu w widoku TextView */
//        questionNumberTextView.setText(getString(R.string.question, 1, FLAGS_IN_QUIZ));

        /* Zwroc widok fragmentu do wyswietlenia */
        return view;

    }

    public void updateGuessRows(SharedPreferences sharedPreferences){

        /* Pobranie informacji o ilosci przyciskow odpowiedzi do wyswietlenia */
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);

        /* Liczba wierszy z przyciskami odpowiedzi do wyswietlenia */
        guessRows = Integer.parseInt(choices) / 2;

        /* Ukrycie wszystkich wierszy z przyciskami */
        for (LinearLayout layout : guessLinearLayouts){
            layout.setVisibility(View.GONE);
        }

        /* Wyswietlenie okreslonej liczby wierszy z przyciskami odpowiedzi */
        for (int row = 0; row < guessRows; row++){
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }

    }

    public void updateRegions(SharedPreferences sharedPreferences){

        /* Pobranie informacji na temat wybranych przez uzytkownika obszarow */
        regionSet = sharedPreferences.getStringSet(MainActivity.REGIONS, null);
    }

    public void resetQuiz(){

        /* Uzyskaj dostep do folderu assets */
        AssetManager assets = getActivity().getAssets();

        /* Wyczysc liste z nazwami flag */
        fileNameList.clear();

        /* Pobierz nazwy plikow obrazow flag z wybranych przez uzytkownika obszarow */
       try {

           /* Petla przechodzaca przez kazdy obszar - czyli przez kazdy folder w folderze assets */
           for (String region : regionSet){

               /* Pobranie nazw wszystkich plikow znajdujacych sie w folderze danego obszaru */
               String[] paths = assets.list(region);

               /* Usuniecie z nazw plikow ich rozszerzenia formatu */
               for (String path : paths){
                   fileNameList.add(path.replace(".png", ""));
               }

           }

       } catch (IOException ex){
           Log.e(TAG, "Error while loading the flags");
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
        while (flagCounter <= FLAGS_IN_QUIZ){

            /* Wybierz losowa wartosc z zakresu od "0" do "liczby flag" bioracych udzial w quizie */
            int randomIndex = random.nextInt(numberOfFlags);

            /* Pobierz nazwe pliku o wylosowanym indeksie */
            String fileName = fileNameList.get(randomIndex);

            /* Jezeli plik o tej nazwie nie zostal jeszcze wylosowany, to dodaj go do listy wybranych krajow */
            if (!quizCountriesList.contains(fileName)){
                quizCountriesList.add(fileName);
                ++flagCounter;
            }
        }

        /* Zaladuj flage */
//        loadNextFlag();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }

}