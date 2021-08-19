package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask< String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {

            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";

                while ((line = reader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = new JSONObject(result).getJSONArray("weather");
                JSONObject weatherDescription = jsonArray.getJSONObject(0);
                String weatherDescriptionString = weatherDescription.getString("description");
                String weatherInfo = jsonObject.getString("main");

                weatherInfo = weatherInfo.replace("temp", "Temperature");
                weatherInfo = weatherInfo.replace(",", "\n");
                weatherInfo = weatherInfo.replace("feels_like", "Feels like");
                weatherInfo = weatherInfo.replace("_min", " Min");
                weatherInfo = weatherInfo.replace("_max", " Max");
                weatherInfo = weatherInfo.replace("pressure", "Pressure");
                weatherInfo = weatherInfo.replace("humidity", "Humidity");
                weatherInfo = weatherInfo.replace("sea_level", "Sea Level");
                weatherInfo = weatherInfo.replace("grnd_level", "Ground Level");
                weatherInfo = weatherInfo.replace("\"", " ");
                weatherInfo = weatherInfo.replace("{", "");
                weatherInfo = weatherInfo.replace("}", "");

                show.setText(" "+weatherDescriptionString+"\n"+weatherInfo);
                show.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityname);
        search = findViewById(R.id.search);
        show = findViewById(R.id.show);

        show.setVisibility(View.INVISIBLE);

        final String[] temp = {""};


        search.setOnClickListener(view -> {

            String city = cityName.getText().toString();

            if(city != null){
            url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=98a4dcca32b7ded8f5ef5a48818578cb";
            }
            else{
                Toast.makeText(MainActivity.this, "Enter a city", Toast.LENGTH_SHORT).show();
            }
            getWeather task = new getWeather();
            try {
                temp[0] = task.execute(url).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (temp[0] == null){
                show.setText(R.string.weather_fetch_error);
                show.setVisibility(View.VISIBLE);
            }
        });
    }
}