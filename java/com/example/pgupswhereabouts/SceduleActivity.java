package com.example.pgupswhereabouts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SceduleActivity extends CommonCodeActivity {
      List<SceduleModel> sceduleModelList;
      SceduleModel s1,s2;
      JSONArray scedule,times;
      TableLayout tableLayout;
    String[] time = {"9:00-10:30","10:45-12:15"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scedule);
        try {
            jsoNserver = new JSONserver(server_url,this);
            jsoNserver.execute(jsonFactory.buildJSON("classtime"));
            jsoNserver = new JSONserver(server_url,this);
            jsoNserver.execute(jsonFactory.buildJSON("sced"));
             scedule = new JSONArray(readFromFile(FILE_SCED));
             times = new JSONArray(readFromFile(FILE_TIME));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sceduleModelList = new ArrayList<SceduleModel>();
        tableLayout = (TableLayout) findViewById(R.id.sceduleTable);
        tableLayout.setStretchAllColumns(true);
        try {
            buildTable(scedule,times);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void buildTable(JSONArray sceduleModelList, JSONArray times) throws JSONException {
        int ROWS = sceduleModelList.length();
       // int COLOUMNS = 4;

        for (int i = 0; i < ROWS; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            //Time
            TextView tim = new TextView(this);
            System.out.println("Time ;" + times.get(  sceduleModelList.getJSONObject(i).getInt("classN")  ).toString());
            tim.setText(times.get(i).toString());
            //Subject
            TextView t1 = new TextView(this);
            t1.setText(sceduleModelList.getJSONObject(i).getString("subject"));
            //room
            TextView t2 = new TextView(this);
            t2.setText(sceduleModelList.getJSONObject(i).getString("room"));

            TextView t3 = new TextView(this);
            t3.setText(sceduleModelList.getJSONObject(i).getString("type"));
            tableRow.addView(tim);
            tableRow.addView(t1);
            tableRow.addView(t2);
            tableRow.addView(t3);
            tableLayout.addView(tableRow);
        }


    }

}