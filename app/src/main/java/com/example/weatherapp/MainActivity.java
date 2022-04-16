package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText input_data;
    private Button get_weather_button;
    private TextView data_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_data = findViewById(R.id.input_data);
        get_weather_button = findViewById(R.id.get_weather_button);
        data_output = findViewById(R.id.data_output);

        get_weather_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_data.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.empty_input, Toast.LENGTH_LONG).show();
                } else {
                    String city = input_data.getText().toString().trim();
                    String api_key = "1ca4d5567c05442661f75e56eda70092";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + api_key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            data_output.setText("Запрос отправлен. Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }

                try{
                    if(reader != null){
                        reader.close();
                }}catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response != null) {
                try {
                    JSONObject response_json = new JSONObject(response);

                    int Resp_Code = response_json.getInt("cod");

                    if (Resp_Code == 200) {
                        Double Temprature = response_json.getJSONObject("main").getDouble("temp");
                        Double Temprature_Like = response_json.getJSONObject("main").getDouble("feels_like");
                        Double Pressure = response_json.getJSONObject("main").getDouble("pressure");
                        Double Humidity = response_json.getJSONObject("main").getDouble("humidity");

                        String output_text = "Температура: " + Temprature.toString() + "\u00B0С\n";
                        output_text += "По ощущениям: " + Temprature_Like.toString() + "\u00B0С\n";
                        output_text += "Давление: " + Pressure.toString() + " мм. рт. ст.\n";
                        output_text += "Влажность: " + Humidity.toString() + "%";
                        data_output.setText(String.valueOf(output_text));
                    } else {
                        String err1 = "Ощибка получения данных. Код: " + String.valueOf(Resp_Code);
                        data_output.setText(err1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String err2 = "Ощибка получения данных. Ответ не получен.";
                data_output.setText(err2);
            }


        }
    }
}