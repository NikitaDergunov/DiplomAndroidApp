package com.example.pgupswhereabouts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.DayOfWeek;

public class MainActivity extends CommonCodeActivity {


    String server_url;
    TableLayout tableLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);
        tableLayout = (TableLayout) findViewById(R.id.abcenceTable);
        tableLayout.setStretchAllColumns(true);
        server_url = getString(R.string.server_url);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(getString(R.string.cred), Context.MODE_PRIVATE);
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("url", server_url);
        ContextCompat.startForegroundService(this, serviceIntent);
        if(!settings.contains("logged on")){
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        }

        try {//Получаем расписание
            jsoNserver = new JSONserver(server_url,this);
            jsoNserver.execute(jsonFactory.buildJSON("sced"));
            jsoNserver = new JSONserver(server_url,this);
            jsoNserver.execute(jsonFactory.buildJSON("classtime"));
            jsoNserver = new JSONserver(server_url,this);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray absArr=null;
        try {
            absArr = new JSONArray(readFromFile(FILE_LOG));
            buildTable(absArr);//Выводим таблицу посещений
        } catch (JSONException e) {
            e.printStackTrace();

        }
        catch (NullPointerException exception){
           exception.printStackTrace();
        }
        super.onStart();
    }


    @Override
    protected void onResume() {
        Log.d("resumed","resumed");
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buildTable(JSONArray absArr) throws JSONException {
        int ROWS = absArr.length()-1;
        //Очищаем таблицу
        int count=tableLayout.getChildCount();
        for(int i=1;i<count;i++)
            tableLayout.removeView(tableLayout.getChildAt(i));
        for (int i = ROWS; i >=0 ; i--) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            JSONObject jsonObject = absArr.getJSONObject(i);
            //absvar
            TextView abs = new TextView(this);
            abs.setText(jsonObject.getString("absvar"));
            //Time
            TextView t1 = new TextView(this);
            t1.setText(jsonObject.getString("time"));
            //День недели
            TextView t2 = new TextView(this);
            t2.setText(DayOfWeek.of(Integer.parseInt(jsonObject.getString("day"))).toString());
            tableRow.addView(abs);
            tableRow.addView(t1);
            tableRow.addView(t2);
            tableLayout.addView(tableRow);
        }
    }
}