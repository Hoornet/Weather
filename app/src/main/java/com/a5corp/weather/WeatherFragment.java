package com.a5corp.weather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;
    Button button;
    ProgressDialog pd;
    TextView detailsField[] = new TextView[10] , weatherIcon[] = new TextView[11];
    TextView windView , humidityView , directionView, dailyView, updatedField, cityField;
    double tc;
    Handler handler;
    int Clicks = 0;
    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject[] json = RemoteFetch.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    public void Units(JSONObject json1)
    {
        try {
            int bool = Clicks % 2;
            switch (bool) {
                case 0 :
                    double Fah = json1.getJSONObject("main").getDouble("temp") * 1.8 + 32;
                    int F = (int) Fah;
                    button.setText(Integer.toString(F) + "°F");
                    ++Clicks;
                    break;
                case 1:
                    button.setText((int) Math.round(json1.getJSONObject("main").getDouble("temp")) + "°C");
                    ++Clicks;
                    break;
            }
        }
        catch (Exception ex)
        {
            Log.e("Not Found" , "BFFK");
        }
    }

    public void changeCity(String city)
    {
        updateWeatherData(city);
    }

    private void renderWeather(JSONObject[] jsonj){
        try {
            button.setVisibility(View.INVISIBLE);
            Clicks = 0;
            Log.i("Showed" , "Done");
            final JSONObject json = jsonj[0] , json1 = jsonj[1];
            tc = json1.getJSONObject("main").getDouble("temp");
            int a = (int) Math.round(json1.getJSONObject("main").getDouble("temp"));
            //button.setText("°C");         //℃
            cityField.setText(json.getJSONObject("city").getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("city").getString("country"));
            cityField.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                    alertDialog.setTitle("City Information");
                    try {
                        alertDialog.setMessage(json.getJSONObject("city").getString("name").toUpperCase(Locale.US) +
                                ", " +
                                json.getJSONObject("city").getString("country"));
                        alertDialog.show();
                        Log.i("Load", "BFFK");
                    } catch (Exception ex) {
                        Log.e("Error", "FFFF");
                    }
                }
            });
            Log.i("Location" , "Location Received");
            JSONObject details[] = new JSONObject[10];
            for (int i = 0; i < 10; ++i)
            {
                details[i] = json.getJSONArray("list").getJSONObject(i);
            }
            Log.i("Objects" , "JSON Objects Created");
            for (int i = 0; i < 10; ++i)
            {
                final JSONObject J = details[i];
                String date1 = details[i].getString("dt");
                Date expiry = new Date(Long.parseLong(date1) * 1000);
                String date = new SimpleDateFormat("EE, dd").format(expiry);
                SpannableString ss1=  new SpannableString(date + "\n"
                + details[i].getJSONObject("temp").getLong("max") + "°" + "      "
                + details[i].getJSONObject("temp").getLong("min") + "°" + "\n");
                ss1.setSpan(new RelativeSizeSpan(1.1f), 0,7, 0); // set size
                ss1.setSpan(new RelativeSizeSpan(1.4f) , 8 , 11 , 0);
                detailsField[i].setText(ss1);
                Log.i("Details[" + Integer.toString(i) + "]", "Infor String " + Integer.toString(i + 1) + " loaded");
                setWeatherIcon(details[i].getJSONArray("weather").getJSONObject(0).getInt("id") , i);
                detailsField[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                        alertDialog.setTitle("Weather Information");
                        try{
                            String date1 = J.getString("dt");
                            Date expiry = new Date(Long.parseLong(date1) * 1000);
                            String date = new SimpleDateFormat("EE, dd MMMM yyyy").format(expiry);
                        alertDialog.setMessage(date +
                                "\n" + J.getJSONArray("weather").getJSONObject(0).getString("description").toUpperCase(Locale.US) +
                                "\n" + "Maximum: " + J.getJSONObject("temp").getLong("max") + " ℃" +
                                "\n" + "Minimum:  " + J.getJSONObject("temp").getLong("min") + " ℃" +
                                "\n" + "Morning:    " + J.getJSONObject("temp").getLong("morn") + " ℃" +
                                "\n" + "At Night:    " + J.getJSONObject("temp").getLong("night") + " ℃" +
                                "\n" + "Evening:    " + J.getJSONObject("temp").getLong("eve") + " ℃" +
                                "\n" + "Humidity:  " + J.getString("humidity") + "%" +
                                "\n" + "Pressure:  " + J.getString("pressure") + " hPa" +
                                "\n" + "Wind:         " + J.getString("speed") + "km/h");
                            alertDialog.show();
                        Log.i("Load" , "BFFK");}
                        catch (Exception e) {
                            Log.e("Error", "FO");
                        }
                    }
                });
                weatherIcon[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                        alertDialog.setTitle("Weather Information");
                        try{
                            String date1 = J.getString("dt");
                            Date expiry = new Date(Long.parseLong(date1) * 1000);
                            String date = new SimpleDateFormat("EE, dd MMMM yyyy").format(expiry);
                            alertDialog.setMessage(date +
                                    "\n" + J.getJSONArray("weather").getJSONObject(0).getString("description").toUpperCase(Locale.US) +
                                    "\n" + "Maximum: " + J.getJSONObject("temp").getLong("max") + " ℃" +
                                    "\n" + "Minimum:  " + J.getJSONObject("temp").getLong("min") + " ℃" +
                                    "\n" + "Morning:    " + J.getJSONObject("temp").getLong("morn") + " ℃" +
                                    "\n" + "At Night:    " + J.getJSONObject("temp").getLong("night") + " ℃" +
                                    "\n" + "Evening:    " + J.getJSONObject("temp").getLong("eve") + " ℃" +
                                    "\n" + "Humidity:  " + J.getString("humidity") + "%" +
                                    "\n" + "Pressure:  " + J.getString("pressure") + " hPa" +
                                    "\n" + "Wind:         " + J.getString("speed") + "km/h");
                            alertDialog.show();
                            Log.i("Load" , "BFFK");}
                        catch (Exception e) {
                            Log.e("Error", "FO");
                        }
                    }
                });
            }
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json1.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);
            int deg = json1.getJSONObject("wind").getInt("deg");
            if (deg < 90)
                directionView.setText(getActivity().getString(R.string.top_right));
            else if (deg == 90)
                directionView.setText(getActivity().getString(R.string.right));
            else if (deg < 180)
                directionView.setText(getActivity().getString(R.string.bottom_right));
            else if (deg == 180)
                directionView.setText(getActivity().getString(R.string.down));
            else if (deg < 270)
                directionView.setText(getActivity().getString(R.string.bottom_left));
            else if (deg == 270)
                directionView.setText(getActivity().getString(R.string.left));
            else
                directionView.setText(getActivity().getString(R.string.top_left));
            setWeatherIcon(json1.getJSONArray("weather").getJSONObject(0).getInt("id"),10);
            humidityView.setText("HUMIDITY:\n" + json1.getJSONObject("main").getInt("humidity") + "%");
            Log.i("Humidity Loaded" , "Done");
            windView.setText("WIND:\n" + json1.getJSONObject("wind").getDouble("speed") + "km/h");
            Log.i("Wind Loaded" , "Done");
            Log.i("10" , "Weather Icon 11 Set");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v)
                {
                    Units(json1);
                }
            });
            weatherIcon[10].setOnClickListener(new View.OnClickListener()
            {
                public void onClick (View v)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                    alertDialog.setTitle("Weather Information");
                    try{
                        String d1 = new java.text.SimpleDateFormat("hh:mm:ss a").format(new Date(json1.getJSONObject("sys").getLong("sunrise")*1000));
                        String d2 = new java.text.SimpleDateFormat("hh:mm:ss a").format(new Date(json1.getJSONObject("sys").getLong("sunset")*1000));
                        alertDialog.setMessage(json1.getJSONArray("weather").getJSONObject(0).getString("description").toUpperCase(Locale.US) +
                                "\n" + "TEMPERATURE :\t " + json1.getJSONObject("main").getInt("temp") + " ℃" +
                                "\n" + "Maximum:\t " + json1.getJSONObject("main").getDouble("temp_max") + " ℃" +
                                "\n" + "Minimum:\t " + json1.getJSONObject("main").getDouble("temp_min") + " ℃" +
                                "\n" + "Humidity:\t   " + json1.getJSONObject("main").getString("humidity") + "%" +
                                "\n" + "Pressure:\t   " + json1.getJSONObject("main").getString("pressure") + " hPa" +
                                "\n" + "Wind:\t        " + json1.getJSONObject("wind").getString("speed") + "km/h" +
                                "\n" + "Sunrise:\t  " + d1 +
                                "\n" + "Sunset:\t  " + d2);
                        alertDialog.show();
                        Log.i("Load" , "BFFK");}
                    catch (Exception e) {
                        Log.e("Error", "FO");
                    }
                }
            });
            button.setText(Integer.toString(a) + "°C");
            button.setVisibility(View.VISIBLE);
        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        humidityView = (TextView) rootView.findViewById(R.id.humidity_view);
        windView = (TextView) rootView.findViewById(R.id.wind_view);
        directionView = (TextView)rootView.findViewById(R.id.direction_view);
        directionView.setTypeface(weatherFont);
        dailyView = (TextView)rootView.findViewById(R.id.daily_view);
        dailyView.setText("DAILY");
        button = (Button)rootView.findViewById(R.id.button1);
        button.setText("°C");
        for (int i = 0; i < 11; ++i)
        {
            String f = "details_view" + (i + 1) , g = "weather_icon" + (i + 1);
            if (i != 10) {
                int resID = getResources().getIdentifier(f, "id", getContext().getPackageName());
                detailsField[i] = (TextView) rootView.findViewById(resID);
            }
            int resIDI = getResources().getIdentifier(g, "id" , getContext().getPackageName());
            weatherIcon[i] = (TextView)rootView.findViewById(resIDI);
            weatherIcon[i].setTypeface(weatherFont);
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    private void setWeatherIcon(int id , int i){
        String icon = "";
        switch(id) {
            case 501 : icon = getActivity().getString(R.string.weather_drizzle);
                break;
            case 500 : icon = getActivity().getString(R.string.weather_drizzle);
                break;
            case 502 : icon = getActivity().getString(R.string.weather_rainy);
                break;
            case 503 : icon = getActivity().getString(R.string.weather_rainy);
                break;
            case 504 : icon = getActivity().getString(R.string.weather_rainy);
                break;
            case 511 : icon = getActivity().getString(R.string.weather_rain_wind);
                break;
            case 520 : icon = getActivity().getString(R.string.weather_shower_rain);
                break;
            case 521 : icon = getActivity().getString(R.string.weather_drizzle);
                break;
            case 522 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 531 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 200 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 201 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 202 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 210 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 211 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 212 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 221 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 230 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 231 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 232 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 300 : icon = getActivity().getString(R.string.weather_shower_rain);
                break;
            case 301 : icon = getActivity().getString(R.string.weather_shower_rain);
                break;
            case 302 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                break;
            case 310 : icon = getActivity().getString(R.string.weather_shower_rain);
                break;
            case 311 : icon = getActivity().getString(R.string.weather_shower_rain);
                break;
            case 312 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                break;
            case 313 : icon = getActivity().getString(R.string.weather_rain_drizzle);
                break;
            case 314 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                break;
            case 321 : icon = getActivity().getString(R.string.weather_heavy_drizzle);
                break;
            case 600 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 601 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 602 : icon = getActivity().getString(R.string.weather_heavy_snow);
                break;
            case 611 : icon = getActivity().getString(R.string.weather_sleet);
                break;
            case 612 : icon = getActivity().getString(R.string.weather_heavy_snow);
                break;
            case 903 :
            case 615 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 616 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 620 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 621 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 622 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 701 :
            case 702 :
            case 721 : icon = getActivity().getString(R.string.weather_smoke);
                break;
            case 751 :
            case 761 :
            case 731 : icon = getActivity().getString(R.string.weather_dust);
                break;
            case 741 : icon = getActivity().getString(R.string.weather_foggy);
                break;
            case 762 : icon = getActivity().getString(R.string.weather_volcano);
                break;
            case 771 :
            case 900 :
            case 781 : icon = getActivity().getString(R.string.weather_tornado);
                break;
            case 904 : icon = getActivity().getString(R.string.weather_sunny);
                break;
            case 800 : icon = getActivity().getString(R.string.weather_sunny);
                break;
            case 801 : icon = getActivity().getString(R.string.weather_cloudy);
                break;
            case 802 : icon = getActivity().getString(R.string.weather_cloudy);
                break;
            case 803 : icon = getActivity().getString(R.string.weather_cloudy);
                break;
            case 804 : icon = getActivity().getString(R.string.weather_cloudy);
                break;
            case 901 : icon = getActivity().getString(R.string.weather_storm);
                break;
            case 902 : icon = getActivity().getString(R.string.weather_hurricane);
                break;
        }
        Log.i(Integer.toString(id) , Integer.toString(i));
        weatherIcon[i].setText(icon);
    }
}
