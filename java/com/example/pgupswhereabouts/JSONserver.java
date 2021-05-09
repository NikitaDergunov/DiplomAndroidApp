package com.example.pgupswhereabouts;

import android.os.AsyncTask;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONserver extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    String url;

    JSONserver(String url,AsyncResponse delegate){
        this.url=url;
        this.delegate=delegate;
    }

    @Override
    protected String doInBackground(String... strings) {
        String jsonstr = strings[0];
        String respstr =null;
        try {
            //String urll =
            URL mUrl = new URL(url);

            HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpURLConnection.setConnectTimeout(15000);
            mHttpURLConnection.setReadTimeout(15000);
            mHttpURLConnection.setRequestMethod("POST");
            mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive"); //Add Header
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.setDoOutput(true);
            mHttpURLConnection.setUseCaches(false);
            mHttpURLConnection.connect();

            //Отправляем запрос с данными JSON
            DataOutputStream out = new DataOutputStream(mHttpURLConnection.getOutputStream());
            JSONObject jsonObject = new JSONObject(jsonstr);
            String sendStr = URLEncoder.encode(jsonObject.toString(),"utf-8");
            out.writeBytes(sendStr);
            out.flush();
            out.close();
            System.out.println("=========Отправленно:");
            System.out.println(sendStr);
            System.out.println("Отправка завершена ====================");


            BufferedReader br = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream()));
            String line;
            StringBuffer sb = new StringBuffer("");
            while((line=br.readLine())!=null){
                //Декодируем
                line = URLDecoder.decode(line,"utf-8");
                sb.append(line);
            }
            System.out.println("The data received is:");
            respstr = sb.toString();
            System.out.println(respstr);
            br.close();

            //Отключаемся
            mHttpURLConnection.disconnect();

        }
        catch (NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
            respstr ="cancel";
        }
        catch (FileNotFoundException fileNotFoundException){ //Если не пришло ответа, ловим
            fileNotFoundException.printStackTrace();
            respstr = "cancel";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respstr;
    }


    @Override
    protected void onPostExecute(String result) {
        //Отправляем результат указонному delegate
        delegate.processFinish(result);
    }

}