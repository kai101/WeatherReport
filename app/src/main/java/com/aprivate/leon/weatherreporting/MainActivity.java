package com.aprivate.leon.weatherreporting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,TextToSpeech.OnInitListener {

    private static final int REQUEST_MICROPHONE = 200;
    private static final String ERROR_RECOGNITION = "I don't understand, please repeat.";
    private static final String ERROR_TTS_LANG_NOT_SUPPORTED = "TTS This Language is not supported";
    private static final String ERROR_TTS_INIT_FAILED = "TTS Initialization Failed!";
    private static final String TAG = "MainActivity";


    /**
     * Speech Recognizer for speech input
     */
    private SpeechRecognizer sr;


    /**
     * Android Permission Related parameter and settings
     */
    private Boolean permissionToRecordAccepted = false;
    private String[] permissions = {
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    } ;

    //UI for loading weather data.
    ProgressDialog dialog;

    /**
     * Google API and location settings.
     */
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private boolean isLocationSet;
    private double longitude;
    private double latitude;

    /**
     * TextToSpeech for Voice output.
     */
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check permission required for this activity first
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            permissionToRecordAccepted = true;
//        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_MICROPHONE);
//        }

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

        //using google play services for location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //init tts
        tts = new TextToSpeech(this, this);


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
        String triggerWord = "weather";
        for (int i = 0; i < data.size(); i++)
        {
            if(found){
                break;
            }
            sentence = data.get(i);
            words = Arrays.asList(sentence.split(" "));

            //loop each word
            for (int j = 0; j < words.size(); j++){
                if(words.get(j).equals(triggerWord)){
                    found = true;
                    break;
                }
            }
        }


        if(found){
            reportWeather();
        }else{
            this.appSpeak(ERROR_RECOGNITION);
        }
    }

    /**
     * Callback function for SpeechRecognizer Listener.
     * @param data Array List for results from recognizer
     */
    public void onSpeechResults(ArrayList<String> data){
        this.processIdea1(data);
    }

    private void reportWeather(){
        WeatherService weatherService = new WeatherService();
        weatherService.setContext(this);
        weatherService.execute();
    }

    /**
     * Callback function for weather service result.
     * @param results Array List for the weather condition from today and tomorrow.
     */
    public void onWeatherResult(ArrayList<String>  results){
        String text = "Today's weather is "+results.get(0)+ " and tomorrow weather will be " +results.get(1)+".";
        this.appSpeak(text);
    }



    private void appSpeak(String text){
        TextView debugBox = (TextView) findViewById(R.id.txt_debug);
        debugBox.setText(text);

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Provide other service to initiate a loading screen.
     */
    public void showLoadingDialog(){
        if(!(this.dialog != null && dialog.isShowing())){
            this.dialog = new ProgressDialog(this);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setMessage("Loading…");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
    }

    /**
     * Provide other service to remove a loading screen.
     */
    public void hideLoadingDialog(){
        if(this.dialog.isShowing()){
            this.dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        //shutdown SpeechRecognizer
        if(sr != null) {
            sr.stopListening();
            sr.destroy();
        }

        //shutdown TextToSpeech
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        if(mGoogleApiClient!=null){

            if(mGoogleApiClient.isConnected()){

                mGoogleApiClient.disconnect();
            }

        }
        super.onDestroy();
    }

    /**
     * Google Play Service onConnected.
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"onConnected mGoogleApiClient");
        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                this.isLocationSet = true;
                this.longitude = mLastLocation.getLongitude();
                this.latitude = mLastLocation.getLatitude();
                Log.d(TAG,"api Lat: "+mLastLocation.getLatitude());
                Log.d(TAG,"api Long: "+mLastLocation.getLongitude());
//                Intent intent = new Intent();
//                intent.putExtra("Longitude", mLastLocation.getLongitude());
//                intent.putExtra("Latitude", mLastLocation.getLatitude());
//                setResult(1,intent);
//                finish();

            }
        } catch (SecurityException e) {

        }
    }

    /**
     * Provide longitude of current device if connected with google play service.
     * @return Double Longitude of the device.
     */
    public double getLongitude(){
        if(this.isLocationSet)
            return this.longitude;
        else
            return 0.0;
    }

    /**
     * Provide latitude of current device if connected with google play service.
     * @return Double Latitude of the device.
     */
    public double getLatitude(){
        if(this.isLocationSet)
            return this.latitude;
        else
            return 0.0;
    }


    /**
     * Google Play Service connection suspended event.
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended code "+i);
        finish();
    }

    /**
     * Google Play Service connection fail event.
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed code" +connectionResult.getErrorCode()+ " "+ connectionResult.getErrorMessage());
        finish();
    }

    /**
     * TextToSpeech initialization.
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG,ERROR_TTS_LANG_NOT_SUPPORTED);
            }

        } else {
            Log.e(TAG, ERROR_TTS_INIT_FAILED);
        }
    }



}
