package com.monika.gramasethu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    Button viewMapButton,
            reportBridgeButton,
            alertButton,
            sosButton,
            searchWeatherButton;

    TextView weatherText;

    EditText cityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // WEATHER TEXT

        weatherText =
                findViewById(R.id.weatherText);

        // CITY INPUT

        cityInput =
                findViewById(R.id.cityInput);

        // SEARCH WEATHER BUTTON

        searchWeatherButton =
                findViewById(R.id.searchWeatherButton);

        // LOAD DEFAULT WEATHER

        loadWeather("Tumkur");

        // SEARCH WEATHER

        searchWeatherButton.setOnClickListener(v -> {

            String city =
                    cityInput.getText()
                            .toString()
                            .trim();

            if(!city.isEmpty()) {

                loadWeather(city);

            }

        });

        // MAP BUTTON

        viewMapButton =
                findViewById(R.id.viewMapButton);

        viewMapButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(HomeActivity.this,
                            MapActivity.class);

            startActivity(intent);

        });

        // REPORT BUTTON

        reportBridgeButton =
                findViewById(R.id.reportBridgeButton);

        reportBridgeButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(HomeActivity.this,
                            ReportActivity.class);

            startActivity(intent);

        });

        // ALERT BUTTON

        alertButton =
                findViewById(R.id.alertButton);

        alertButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(HomeActivity.this,
                            AlertActivity.class);

            startActivity(intent);

        });

        // SOS BUTTON

        sosButton =
                findViewById(R.id.sosButton);

        sosButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_DIAL);

            intent.setData(
                    android.net.Uri.parse("tel:112"));

            startActivity(intent);

        });

    }

    // WEATHER API

    private void loadWeather(String city) {

        new Thread(() -> {

            try {

                String apiKey =
                        BuildConfig.WEATHER_API_KEY;

                String link =
                        "https://api.openweathermap.org/data/2.5/weather?q="
                                + city
                                + "&appid="
                                + apiKey
                                + "&units=metric";

                URL url =
                        new URL(link);

                HttpURLConnection connection =
                        (HttpURLConnection)
                                url.openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()));

                StringBuilder result =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {

                    result.append(line);

                }

                JSONObject jsonObject =
                        new JSONObject(result.toString());

                JSONObject main =
                        jsonObject.getJSONObject("main");

                double temperature =
                        main.getDouble("temp");

                String weather =
                        jsonObject.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("main");

                runOnUiThread(() -> {

                    String alertMessage =
                            " Location : " + city
                                    + "\n Weather : "
                                    + weather
                                    + "\n Temperature : "
                                    + temperature
                                    + "°C";

                    weatherText.setText(alertMessage);

                });

            }

            catch (Exception e) {

                runOnUiThread(() -> {

                    weatherText.setText(
                            "Unable to load weather updates");

                });

            }

        }).start();

    }

}