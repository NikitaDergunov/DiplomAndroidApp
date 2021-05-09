package com.example.pgupswhereabouts;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
//import android.support.annotation.Nullable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//import android.support.v4.app.NotificationCompat;
public class ForegroundService extends Service implements AsyncResponse,OnLocationUpdateListener {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public final static String FILE_LOG="abcencelog.txt";
    public final static String FILE_TIME="times.txt";
    public String url=null;
    LocationHandler locationHandler;
    private Location mLocation;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate() {
        locationHandler = new LocationHandler(this,this);
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
               // .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        url = intent.getStringExtra("url");
        if(true){
        JSONArray times=null;
        ArrayList<Date> tim = new ArrayList<Date>();
        //Устанавливаем время начала пар для Timer
        try {
            times = new JSONArray(readFromFile(FILE_TIME));
            SimpleDateFormat f = new SimpleDateFormat("hh:mm:ss");
            for(int i = 0;i<times.length();i++){
             tim.add(f.parse(times.getString(i)));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, tim.get(i).getHours());
                calendar.set(Calendar.MINUTE, tim.get(i).getMinutes());
                calendar.set(Calendar.SECOND, 0);
                tim.set(i,calendar.getTime());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SharedPreferences settings = getApplicationContext().getSharedPreferences("Credantials", Context.MODE_PRIVATE);
        Timer timer  = new Timer();
        for(int i = 0;i<tim.size();i++) {
            //Устанавливаем таймеры проверки присутствия
            if(Calendar.getInstance().getTime().before(tim.get(i))){
            timer.schedule(new SampleTask(new LoggerSceduler(this,settings)), tim.get(i));
            }
        }
        }

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
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

    @Override
    public void processFinish(String output) {

        try {
            JSONObject json = new JSONObject(output);
            switch (json.getString("querry")){
            case("logclass"):
                String absvar = "Не включен gps";
                if(mLocation!=null) {
                    double latitude = mLocation.getLatitude();
                    double longitude = mLocation.getLongitude();

                    JSONArray rectjson = json.getJSONArray("rect");
                    double[] rect = {0, 0, 0, 0};
                    for (int i = 0; i < rectjson.length(); i++) {
                        rect[i] = rectjson.getDouble(i);
                    }

                    //ЕСЛИ МЫ ЕСТЬ В КЛАССЕ
                    if (latitude < rect[0] && latitude > rect[2] && longitude > rect[1] && longitude < rect[3]) {
                        absvar = "Присутствие";
                    }
                    else {
                        absvar = "Отсутсвие";
                    }
                }

                Time timecurrent = new Time(System.currentTimeMillis());
                Calendar c = Calendar.getInstance();
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                JSONObject absjson = new JSONObject();
                absjson.put("absvar",absvar);
                absjson.put("day",dayOfWeek);
                absjson.put("time",timecurrent);
                writeToFile(absjson.toString(),FILE_LOG,"APPEND");
                System.out.println("ansjson = " + absjson.toString());
                break;
            case("logabs"):

                break;
        }
    } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChange(Location location) {
        mLocation = location;

    }

    @Override
    public void onError(String error) {

    }
    private  class LoggerSceduler extends Thread{
        AsyncResponse delegate;
        SharedPreferences settings;
        LoggerSceduler(AsyncResponse delegate,SharedPreferences settings){
          this.delegate=delegate;
          this.settings = settings;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            JSONserver jsoNserver = new JSONserver(url,delegate);
            JSONFactory jsonFactory = new JSONFactory(getApplicationContext());
            try {
                for(int i = 0;i<50;i++){
                    System.out.println("SEEEEEEEEEEND");
                }
                jsoNserver.execute(jsonFactory.buildJSON("logclass"));
                jsoNserver=new JSONserver(url,delegate);
                jsoNserver.execute(jsonFactory.buildJSON("logabs"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public class SampleTask extends TimerTask {
        Thread myThreadObj;

        SampleTask (Thread t){
            this.myThreadObj=t;
        }
        public void run() {

            myThreadObj.run();
        }
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
                    ex.printStackTrace();
                }
            }}
        else{
            try{

                fos = openFileOutput(filename, MODE_PRIVATE);
                JSONArray jsonArray = new JSONArray();

                    String previous = readFromFile(filename);
                if(previous.contains("]")){
                    previous = previous.substring(0 ,previous.lastIndexOf("]")) + "," + data.substring(1) + "]";//Сохраняем формат JSONArray
                    JSONArray jsonPrev = new JSONArray(previous);
                    jsonArray=new JSONArray(jsonPrev.toString());
                }
                   JSONObject jsonData = new JSONObject(data);
                   jsonArray.put(jsonData);
                fos.write(jsonArray.toString().getBytes());
           }catch (IOException ioException){
             ioException.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();

            } finally{
                try{
                    if(fos!=null)
                        fos.close();
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }

    }
}