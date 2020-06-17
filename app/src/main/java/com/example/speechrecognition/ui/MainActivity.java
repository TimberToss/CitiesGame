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
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.speechrecognition.R;
import com.example.speechrecognition.adapter.MyAdapter;
import com.example.speechrecognition.data.state.Resource;
import com.example.speechrecognition.databinding.ActivityMainBinding;
import com.example.speechrecognition.data.entity.CityForRecyclerView;
import com.example.speechrecognition.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.speechrecognition.constants.Constants.MY_TAG;

public class MainActivity extends AppCompatActivity implements RestartGameCallback {
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private static final String DIALOG_TAG = "restart dialog";
    private static final String DICTATION_MODE = "\"android.speech.extra.DICTATION_MODE\"";
    private ActivityMainBinding binding;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private TextToSpeech textToSpeech;
    private ArrayList<CityForRecyclerView> currentCities;
    private MyAdapter adapter;
    private MainActivityViewModel viewModel;
    private String lastAppCity = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkRecordAudioPermission();

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.lettersLiveData.observe(this, resource -> {
            if (resource.getStatus() == Resource.DataStatus.SUCCESS) {
                hideProgressBar();
                binding.recordButton.setEnabled(true);

            } else if (resource.getStatus() == Resource.DataStatus.ERROR) {
                hideProgressBar();

            } else if (resource.getStatus() == Resource.DataStatus.LOADING) {
                showProgressBar();
                binding.recordButton.setEnabled(false);
            }
        });
        viewModel.downloadData();
        speechRecognizerInit();
        textToSpeechInit();
        recyclerViewAdapterInit();


        binding.recordButton.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_UP:
                    speechRecognizer.stopListening();
                    binding.speechText.setHint(
                            getResources().getText(R.string.speak).toString());
                    break;

                case MotionEvent.ACTION_DOWN:
                    binding.speechText.setHint(
                            getResources().getText(R.string.listening).toString());
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

    private void speechRecognizerInit() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault().getLanguage());

        // add these two rows because otherwise onResults invokes twice
        speechRecognizerIntent.putExtra(DICTATION_MODE, true);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

        speechRecognizer.setRecognitionListener(createRecognitionListener());
    }

    private void textToSpeechInit() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                if (textToSpeech.isLanguageAvailable(new Locale(Locale.getDefault().getLanguage()))
                        == TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.setLanguage(new Locale(Locale.getDefault().getLanguage()));
                } else {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
                textToSpeech.setPitch(1.3f);
                textToSpeech.setSpeechRate(0.7f);
            } else if (status == TextToSpeech.ERROR) {
                Toast.makeText(this, R.string.tts_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void recyclerViewAdapterInit() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        currentCities = new ArrayList<>();
        adapter = new MyAdapter(currentCities);
        binding.recyclerView.setAdapter(adapter);
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private MainActivity getActivity() {
        return this;
    }

    private void outputErrorToUser(int id) {
        currentCities.add(new CityForRecyclerView(getResources().getText(id).toString(),
                CityForRecyclerView.CityType.USER_CITY));
        adapter.notifyDataSetChanged();
        binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void restartGame() {
        lastAppCity = null;
        currentCities.clear();
        adapter.notifyDataSetChanged();
        viewModel.downloadData();
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
                    adapter.notifyDataSetChanged();
                    binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());

                    switch (viewModel.checkUserCity(result, lastAppCity)) {

                        case SUCCESS:
                            lastAppCity = viewModel.createAnswer(result);

                            textToSpeech.speak(lastAppCity, TextToSpeech.QUEUE_FLUSH,
                                    null, Long.toString(this.hashCode()));

                            currentCities.add(new CityForRecyclerView(result,
                                    CityForRecyclerView.CityType.USER_CITY));

                            currentCities.add(new CityForRecyclerView(lastAppCity,
                                    CityForRecyclerView.CityType.APP_CITY));

                            adapter.notifyDataSetChanged();
                            binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                            break;

                        case INCORRECT_BEGIN_LETTER:
                            outputErrorToUser(R.string.incorrect_begin_letters);
                            break;

                        case CHOOSE_INCORRECT_BEGIN_LETTER:
                            outputErrorToUser(R.string.choose_incorrect_begin_letters);
                            break;

                        case CITIES_FROM_LETTER_ENDED:
                            RestartGameDialog dialog = new RestartGameDialog(
                                    getActivity(), R.string.cities_from_letter_ended);
                            dialog.show(getSupportFragmentManager(), DIALOG_TAG);
                            break;

                        case USER_ENTER_LAST_CITY:
                            RestartGameDialog restartGameDialog = new RestartGameDialog(
                                    getActivity(), R.string.user_enter_last_city);
                            restartGameDialog.show(getSupportFragmentManager(), DIALOG_TAG);
                            break;

                        case ALREADY_USED_CITY:
                            outputErrorToUser(R.string.already_used_city);
                            break;

                        case UNKNOWN_CITY:
                            outputErrorToUser(R.string.unknown_city);
                            break;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
        textToSpeech.shutdown();
    }
}
