package com.example.pgupswhereabouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;

public class JSONFactory {
    String login = null;
    String password = null;
    SharedPreferences settings;
    Context context;
    public final static String FILE_LOG="abcencelog.txt";
    public JSONFactory(Context settings){
       this.settings = settings.getSharedPreferences("Credantials", Context.MODE_PRIVATE);
       this.context = settings;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String buildJSON(String querry) throws JSONException {
        JSONObject json = new JSONObject();
        login =  settings.getString("login","");
        password = settings.getString("md5","");
        json.put("login",login);
        json.put("password", password);
        json.put("querry",querry);
        switch (querry){
            case ("login"):
                break;
            case ("sced"):
                //Запрос расписания на сегодняшний день
                Calendar c = Calendar.getInstance();
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                json.put("day",dayOfWeek);
                break;
            case ("logabs"):
              //Получаем результат проверки на присутствия из файла. Туда его записывает ForegroundService
                try {
                    JSONObject logLast = new JSONObject();
                    JSONArray logArr = new JSONArray(readFromFile(FILE_LOG));
                    logLast = logArr.getJSONObject(logArr.length()-1);
                    json.put("absvar",logLast.getString("absvar"));
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
               json.put("login",login);
                break;
        }
        return json.toString();
    }
    public  void setLoginAndPassword(String login, String password){
        this.login = login;
        this.password = password;
        System.out.println(password);
    }
    public String readFromFile(String filename){
        FileInputStream fin = null;
        String text = null;

        try {
            fin = context.openFileInput(filename);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            text = new String (bytes);

        }
        catch(IOException ex) {

            ex.printStackTrace();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                ex.printStackTrace();
            }}

        return text;
    }
}
