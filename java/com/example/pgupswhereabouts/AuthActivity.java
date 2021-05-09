package com.example.pgupswhereabouts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthActivity extends CommonCodeActivity implements View.OnClickListener {
    private String name, password;
    EditText loginName ;
     EditText loginPassword ;
     Button loginButton;
     Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        loginName = (EditText) findViewById(R.id.loginName);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        context = this;
        loginButton.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        Log.d("tag","Here");
        switch (v.getId()){
            case R.id.loginButton:
               //Получаем пароль и логин от пользователя
                name = loginName.getText().toString();
                password = loginPassword.getText().toString();
                //Хешируем пароль
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                md.update(password.getBytes());
                byte[] digest = md.digest();
                String myHash = Base64.encodeToString(digest,Base64.DEFAULT);
                //Сохраняем в файл
                SharedPreferences settings = getApplicationContext().getSharedPreferences("Credantials", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("login",name);
                editor.putString("md5",myHash);
                editor.commit();

                jsonFactory.setLoginAndPassword(name,myHash);
                try {
                    //Отправляем запрос авторизации на сервер
                    jsoNserver = new JSONserver(server_url,this);
                    jsoNserver.execute(jsonFactory.buildJSON("login"));//продолжение в Common
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;}
    }

    }
