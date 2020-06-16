package com.example.speechrecognition.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.speechrecognition.adapter.MyAdapter;
import com.example.speechrecognition.data.entity.City;
import com.example.speechrecognition.data.state.Resource;
import com.example.speechrecognition.model.CityForRecyclerView;
import com.example.speechrecognition.databinding.ActivityMainBinding;
import com.example.speechrecognition.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.example.speechrecognition.constants.Constants.MY_TAG;

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

    private LiveData<Resource<City>> citiesLiveData;
    private LiveData<Resource<String>> lettersLiveData;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.lettersLiveData.observe(this, resource -> {
            if (resource.getStatus() == Resource.DataStatus.SUCCESS) {
                hideProgressBar();
                binding.recordButton.setEnabled(true);
                binding.editText.setEnabled(true);

            } else if (resource.getStatus() == Resource.DataStatus.ERROR) {
                hideProgressBar();

            } else if (resource.getStatus() == Resource.DataStatus.LOADING) {
                binding.recordButton.setEnabled(false);
                binding.editText.setEnabled(false);
            }
        });
        viewModel.downloadData();

//        binding.editText.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                hideKeyboard();
//                // do something, e.g. set your TextView here via .setText()
////                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
////                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                return true;
//            }
//            return false;
//        });

        checkRecordAudioPermission();
        speechRecognizerInit();
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

        // add these two rows because otherwise onResults invokes twice
        speechRecognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getLanguage());

        speechRecognizer.setRecognitionListener(createRecognitionListener());
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

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                    String result = matches.get(0);
                    binding.speechText.setText(result);
                    currentCities.add(new CityForRecyclerView(result, CityForRecyclerView.CityType.USER_CITY));
                    adapter.notifyDataSetChanged();
                    binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    Log.d(MY_TAG, "before check");
                    if (viewModel.checkUserCity(result)) {
                        String ourCity = viewModel.createAnswer(result);
                        binding.speechText.setText(ourCity);
                        currentCities.add(new CityForRecyclerView(ourCity, CityForRecyclerView.CityType.APP_CITY));
                        adapter.notifyDataSetChanged();
                        binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
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
