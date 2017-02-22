package com.aprivate.leon.weatherreporting;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by leon on 22/2/2017.
 */

public class WeatherService extends AsyncTask<Void, String, String> {

    private String temperature,date,condition,humidity,wind;
    private Bitmap icon = null;
    private Context context;
    ArrayList<String> weather = new ArrayList<String>();
    private final static String OPENWEATHERMAP_KEY = "428cb5f99ddafb0a4546735c2f494599";
    private double longitude;
    private double latitude;

    public void setContext(Context mContext){
        this.context = mContext;
    }

    @Override
    protected String doInBackground(Void... params) {
        String qResult = "";
        //deprecated httpClient and yahooapis
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        HttpGet httpGet = new HttpGet("http://weather.yahooapis.com/forecastrss?w=2295425&u=c&#8221");

        try {

            Log.d("doInBackground","Calling: http://api.openweathermap.org/data/2.5/forecast/daily?lat="+this.latitude+"&lon="+this.longitude+"&cnt=2&mode=xml&appid="+OPENWEATHERMAP_KEY);
            URL urlObj = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+this.latitude+"&lon="+this.longitude+"&cnt=2&mode=xml&appid="+OPENWEATHERMAP_KEY);

            //deprecated HttpClient
//            HttpResponse response = httpClient.execute(httpGet,
//                    localContext);
//            HttpEntity entity = response.getEntity();
            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();

            //deprecated.
            //if (entity != null) {
            if (urlConnection != null) {
                //InputStream inputStream = entity.getContent();
                InputStream inputStream = urlConnection.getInputStream();
                Reader in = new InputStreamReader(inputStream);
                BufferedReader bufferedreader = new BufferedReader(in);
                StringBuilder stringBuilder = new StringBuilder();
                String stringReadLine = null;
                while ((stringReadLine = bufferedreader.readLine()) != null) {
                    stringBuilder.append(stringReadLine + "\n");
                }
                qResult = stringBuilder.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG)
                    .show();
        }

        Document dest = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder parser;
        try {
            parser = dbFactory.newDocumentBuilder();
            dest = parser
                    .parse(new ByteArrayInputStream(qResult.getBytes()));
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
            Toast.makeText(this.context, e1.toString(), Toast.LENGTH_LONG)
                    .show();
        } catch (SAXException e) {
            e.printStackTrace();
            Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG)
                    .show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG)
                    .show();
        }


        NodeList timeNodes = dest.getElementsByTagName(
                "time");

        for(int i = 0 ; i < timeNodes.getLength();i++ ){
            Node symbolNode = timeNodes.item(i).getFirstChild();
            weather.add(symbolNode.getAttributes()
                    .getNamedItem("name").getNodeValue().toString());
        }


        return qResult;
    }

    /**
     * Before async task exectured.
     */
    protected void onPreExecute(){

        //unsafe
        MainActivity curContext = ((MainActivity)this.context);
        curContext.showLoadingDialog();

        this.latitude = curContext.getLatitude();
        this.longitude = curContext.getLongitude();

//        dialog = new ProgressDialog(this.context);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setMessage("Loading…");
//        dialog.setCancelable(false);
//        dialog.show();
    }

    /**
     * After async task completed.
     * @param result
     */
    protected void onPostExecute(String result) {
        System.out.println("POST EXECUTE");
        ((MainActivity)this.context).hideLoadingDialog();
        ((MainActivity) this.context).onWeatherResult(weather);
    }
}

