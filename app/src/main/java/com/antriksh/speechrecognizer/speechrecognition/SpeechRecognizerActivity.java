package com.antriksh.speechrecognizer.speechrecognition;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SpeechRecognizerActivity extends AppCompatActivity {

    private static final String TAG = "SpeechRecognizerActivity";
    private SpeechRecognizer speechRecognizer;
    private final int PERMISSION_AUDIO = 1000;
    private TextView status,subStatus,result;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_recongnizer);

        status = (TextView) findViewById(R.id.status);
        subStatus = (TextView) findViewById(R.id.sub_status);
        result = (TextView) findViewById(R.id.result);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_AUDIO);
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListenerImpl(this));

        // listener
        Button button = (Button) findViewById(R.id.start_recognize);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecognizeSpeech();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSION_AUDIO &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.start_recognize).setEnabled(true);
            subStatus.setText("");
            status.setText("");
        }else{
            Toast.makeText(this, "Audio Permission Required", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecognizeSpeech() {

        Intent intent = RecognizerIntent.getVoiceDetailsIntent(getApplicationContext());
        speechRecognizer.startListening(intent);

        ((TextView)findViewById(R.id.status)).setText("");
        ((TextView)findViewById(R.id.sub_status)).setText("");
        findViewById(R.id.start_recognize).setEnabled(false);
    }

    private class RecognitionListenerImpl implements RecognitionListener {

        //Use this caller to call methods of the parent activity
        private SpeechRecognizerActivity caller;

        RecognitionListenerImpl(SpeechRecognizerActivity a) {
            caller = a;
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            status.setText("Ready for speech");
            Log.d(TAG,"On Ready");
        }

        @Override
        public void onBeginningOfSpeech() {
            status.setText("Beginning of speech");
            result.setText("");
            Log.d(TAG,"beginning of speech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            subStatus.setText("");
        }

        @Override
        public void onEndOfSpeech() {
            status.setText("End of speech");
            subStatus.setText("Processing...");
            Log.v(TAG,"end of speech");
            findViewById(R.id.start_recognize).setEnabled(true);
        }

        @Override
        public void onError(int error) {
            findViewById(R.id.start_recognize).setEnabled(true);
            status.setText("Current status: Error");
            subStatus.setText("");
            Log.v(TAG,"on error");
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:

                    subStatus.setText("ERROR_AUDIO");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:

                    subStatus.setText("ERROR_CLIENT");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:

                    subStatus.setText("ERROR_INSUFFICIENT_PERMISSIONS");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:

                    subStatus.setText("ERROR_NETWORK");
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:

                    subStatus.setText("ERROR_NETWORK_TIMEOUT");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:

                    subStatus.setText("ERROR_NO_MATCH");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:

                    subStatus.setText("ERROR_RECOGNIZER_BUSY");
                    break;
                case SpeechRecognizer.ERROR_SERVER:

                    subStatus.setText("ERROR_SERVER");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:

                    subStatus.setText("ERROR_SPEECH_TIMEOUT");
                    break;
                default:
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            status.setText("Partial results");
            Log.v(TAG,"on results");
        }

        @Override
        public void onResults(Bundle data) {
            status.setText("Results: ");
            subStatus.setText("");
            Log.v(TAG,"on results");

            ArrayList<String> results = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (String s : results) {
                result.append(s + "\n");
            }

        }
    }
}
