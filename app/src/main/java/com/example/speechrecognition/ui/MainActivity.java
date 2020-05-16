package com.example.speechrecognition.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.speechrecognition.adapter.MyAdapter;
import com.example.speechrecognition.model.CityForRecyclerView;
import com.example.speechrecognition.databinding.ActivityMainBinding;
import com.example.speechrecognition.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private ActivityMainBinding binding;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private FirebaseFirestore firestore;
    private ArrayList<ArrayList<String>> data;
    private ArrayList<CityForRecyclerView> currentCities;
    private MyAdapter adapter;
    private LinearLayoutManager layoutManager;
    private MainActivityViewModel viewModel;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(MainActivityViewModel.class);
        try {
            System.out.println("3333");
            System.out.println(viewModel.getAllCities("russianName"));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        checkRecordAudioPermission();
        speechRecognizerInit();
        firestoreInit();
        recyclerViewAdapterInit();


        binding.recordButton.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_UP:
                    speechRecognizer.stopListening();
                    binding.speechText.setHint("You will see text here");
                    break;

                case MotionEvent.ACTION_DOWN:
                    binding.speechText.setHint("Listening...");
                    speechRecognizer.startListening(speechRecognizerIntent);
                    break;
            }
            return false;
        });
    }

    private void checkRecordAudioPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_AUDIO_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE && grantResults.length == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void speechRecognizerInit() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        speechRecognizer.setRecognitionListener(createRecognitionListener());
    }

    private void firestoreInit() {

//        firestore = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = firestore
//                .collection("cities")
//                .document("citiesList");

//        documentReference.get().addOnCompleteListener(task -> {
////            data = new ArrayList<>();
//            if (task.getResult() != null) {
////                task.getResult().
//                    System.out.println(task.getResult().getData().keySet());
//                    System.out.println(task.getResult().getData().values());
//
//            }
//        });
//        query.get().addOnCompleteListener(task -> {
//            data = new ArrayList<>();
//            if (task.getResult() != null) {
//                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
//                    snapshot.toObject(String.class);
//                }
//            }
//        });
    }

    private void recyclerViewAdapterInit() {
        layoutManager = new LinearLayoutManager(this);
//        layoutManager.scrollToPosition(0);
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        currentCities = new ArrayList<>();
//        currentCities.add(new City("Абакан", City.CityType.APP_CITY));
//        currentCities.add(new City("Нибиру", City.CityType.USER_CITY));
//        currentCities.add(new City("Уругвай", City.CityType.APP_CITY));
//        currentCities.add(new City("Йорк", City.CityType.USER_CITY));
//        currentCities.add(new City("Киров", City.CityType.APP_CITY));
        adapter = new MyAdapter(currentCities);
        binding.recyclerView.setAdapter(adapter);
    }

    private RecognitionListener createRecognitionListener() {
        return new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    binding.speechText.setText(matches.get(0));
                    currentCities.add(new CityForRecyclerView(matches.get(0), CityForRecyclerView.CityType.USER_CITY));
                    adapter.notifyDataSetChanged();
                    binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        };
    }
}
