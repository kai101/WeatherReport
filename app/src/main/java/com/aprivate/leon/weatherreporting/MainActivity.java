package com.aprivate.leon.weatherreporting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_MICROPHONE = 200;
    private static final String TAG = "MainActivity";
    private SpeechRecognizer sr;
    private Boolean permissionToRecordAccepted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check permission required for this activity first
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            permissionToRecordAccepted = true;
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
        }

        //bind button onClick to this
        Button pressButton = (Button) findViewById(R.id.btn_press);
        pressButton.setOnClickListener(this);

        //init speechRecognizer
        sr = SpeechRecognizer.createSpeechRecognizer(this);

        //init listener
        SpeechRecognizerListener srListener = new SpeechRecognizerListener();
        srListener.setContext(this);

        //set listener
        sr.setRecognitionListener(srListener);



    }

    public void onClick(View v) {
        int vId = v.getId();
        switch(vId){
            case R.id.btn_press : this.speechInit(v);break;
            default: ;//do nothing
        }
    }

    private void speechInit(View v){

        if(permissionToRecordAccepted){//double checking for sure.
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            sr.startListening(intent);
            Log.i("SpeechRecognizer","startListening");
        }

    }

    /**
     * Callback handling when permission approved or denied.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_MICROPHONE:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    /**
     * processIdea1 single trigger word. "weather"
     * @param data ArrayList of string from speech recognizer.
     */
    private void processIdea1(ArrayList<String> data){
        //process idea 1;
        String sentence = "";
        List<String> words;
        Boolean found = false;
        for (int i = 0; i < data.size(); i++)
        {
            if(found){
                break;
            }
            sentence = data.get(i);
            words = Arrays.asList(sentence.split(" "));

            //loop each word
            for (int j = 0; j < words.size(); j++){
                if(words.get(j).equals("weather")){
                    found = true;
                    break;
                }
            }
        }


        if(found){
            reportWeather();
        }
    }


    private void processIdea2(ArrayList data){
        //process idea 2;
        for (int i = 0; i < data.size(); i++)
        {


        }
    }

    public void onSpeechResults(ArrayList data){

        this.processIdea1(data);
        this.processIdea2(data);
    }

    private void reportWeather(){
        Log.d(TAG,"TODO: report Weather");
    }

    @Override
    protected void onDestroy() {
        // your onStop code
        if(sr != null) {
            sr.stopListening();
            sr.destroy();
        }
        super.onDestroy();
    }
}
