package com.aprivate.leon.weatherreporting;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by leon on 21/2/2017.
 */
public class SpeechRecognizerListener implements RecognitionListener {

    private static final String TAG = "ListenerEvent";
    private Context context;

    public void setContext(Context context){
        this.context = context;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "onError code "+ error);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults");
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++)
        {
            //logging results.
            Log.d(TAG, "result " + data.get(i));
        }

        //pass to context for processing
        if(this.context != null){
            //Dangerous casting here, for 1 activity App should be fine.
            MainActivity mainActivity = (MainActivity)context;
            mainActivity.onSpeechResults(data);
        }

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent");
    }
}
