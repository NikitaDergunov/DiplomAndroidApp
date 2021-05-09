package com.example.pgupswhereabouts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends CommonCodeActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button logout = (Button) findViewById(R.id.logoutButton);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Выход здесь
        SharedPreferences settings = getApplicationContext().getSharedPreferences("Credantials", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = settings.edit();
        e.putBoolean("logged on",false);
        e.remove("login");
        e.remove("md5");
        e.commit();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }
}