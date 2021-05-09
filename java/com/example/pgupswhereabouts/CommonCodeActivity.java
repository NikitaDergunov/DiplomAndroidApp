package com.example.pgupswhereabouts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;

public class CommonCodeActivity extends AppCompatActivity implements AsyncResponse {
    JSONFactory jsonFactory;
    JSONserver jsoNserver;
    String server_url;

    public final static String FILE_LOG="abcencelog.txt";
    public final static String FILE_SCED="scedule.txt";
    public final static String FILE_TIME="times.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_code);
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);

         server_url = getString(R.string.server_url);
         jsoNserver = new JSONserver(server_url,this);
         SharedPreferences settings = getApplicationContext().getSharedPreferences("Credantials", Context.MODE_PRIVATE);
         System.out.println("In common code" + settings.getAll());
         jsonFactory = new JSONFactory(getApplicationContext());
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //TextView headerView = (TextView) findViewById(R.id.selectedMenuItem);
        switch(id){
            case R.id.action_settings :
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_scedule:
                Intent intent2 = new Intent(this, SceduleActivity.class);
                startActivity(intent2);
                return true;
            case R.id.action_log:
                Intent intent3 = new Intent(this, MainActivity.class);
                startActivity(intent3);
                return true;

        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void processFinish(String output) {//Станция приема сообщений от сервера
        if(output == "cancel"){//Если сервер не отправил сообщения
            System.out.println("Uh-oh something went wrong on Server!");
        }
        else {
        try {
            JSONObject json = new JSONObject(output);
            switch (json.getString("querry")){
                case("sced")://Получаем расписание и записываем его в файл
                    JSONArray sceduleModelList =  json.getJSONArray("scedule");
                    writeToFile(sceduleModelList.toString(),FILE_SCED,"PRIVATE");
                break;
                case("classtime")://Получаем время начала пар и записываем его в файл
                    JSONArray times = json.getJSONArray("starttime");
                    writeToFile(times.toString(),FILE_TIME,"PRIVATE");
                break;
                case("failure"):
                    System.out.println("failure!");
                break;
                case("login")://Ответ от сервера при авторизации
                    if(!json.getBoolean("logged on")) //Если логин не верен
                    {
                        Toast toast = Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG);
                        toast.show();
                        System.out.println("login failure");
                    Intent intent = new Intent(this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                    }
                    else{//Если верен
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("Credantials", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("logged on",true);
                        editor.apply();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);

                    }
                    break;
            }
        }catch (NullPointerException nullPointerException){
            System.out.println("Uh-oh Server is unavalible!");
            nullPointerException.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }}
    }
    public void writeToFile(String data,String filename, String mode){
        FileOutputStream fos = null;

        if(mode=="PRIVATE"){
        try{
            fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(data.getBytes());

        }catch (IOException ioException){
            ioException.printStackTrace();
        }finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
    }}
        if(mode=="APPEND"){
            try{
                fos = openFileOutput(filename, MODE_PRIVATE);
                String previous = readFromFile(filename);
                previous = previous.substring(0,previous.lastIndexOf("]")) + "," + data.substring(1)+ "]";//Сохраняем формат JSONArray
                fos.write(previous.getBytes());

            }catch (IOException ioException){
                ioException.printStackTrace();
            }finally{
                try{
                    if(fos!=null)
                        fos.close();
                }
                catch(IOException ex){
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    public String readFromFile(String filename){
        FileInputStream fin = null;
        String text = null;

        try {
            fin = openFileInput(filename);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            text = new String (bytes);

        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }}

        return text;
    }


}