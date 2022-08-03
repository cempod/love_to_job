package com.cempod.love_to_job;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Settings extends AppCompatActivity {
    TimePicker startTimer;
    TimePicker endTimer;
    RadioButton radio22;
    RadioButton radio52;
    int set = 0;
    int cycle22 = 0;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Настройки");
         startTimer = findViewById(R.id.startTimePicker);
         endTimer = findViewById(R.id.endTimePicker);
        startTimer.setIs24HourView(true);
        endTimer.setIs24HourView(true);
        SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        startTimer.setHour(settings.getInt("startHour",0));
        startTimer.setMinute(settings.getInt("startMinute",0));
        endTimer.setHour(settings.getInt("finishHour",0));
        endTimer.setMinute(settings.getInt("finishMinute",0));
        set = settings.getInt("set",0);
        cycle22 = settings.getInt("cycle22",0);

        radio22 = findViewById(R.id.radio22);
        radio52 = findViewById(R.id.radio52);
        switch (set){
            case(0):
                radio22.setChecked(false);
                radio52.setChecked(true);
                break;
            case(1):
                radio52.setChecked(false);
                radio22.setChecked(true);
                break;
        }
        //dialog.show(getSupportFragmentManager(), "custom");
    }
    Dialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveBtn(View view){
        if ((startTimer.getHour()>endTimer.getHour())||((startTimer.getHour()==endTimer.getHour())&&(startTimer.getMinute()>endTimer.getMinute()))){
            AlertDialog warningDialog = new AlertDialog.Builder(
                    Settings.this).setMessage("Начальное время не может быть позже конечного!")
                    .setTitle("Внимание!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    }).create();

            warningDialog.show();
            endTimer.setHour(startTimer.getHour());
            endTimer.setMinute(startTimer.getMinute());
        }else{
        SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        Button saveButton = findViewById(R.id.button);
Calendar cal = new GregorianCalendar();
cal = Calendar.getInstance();
        int sH,sM,fH,fM;
        sH = startTimer.getHour();
        sM = startTimer.getMinute();
        fH = endTimer.getHour();
        fM = endTimer.getMinute();
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("SAVED", "YES");
        prefEditor.putInt("startHour",sH);
        prefEditor.putInt("startMinute",sM);
        prefEditor.putInt("finishHour",fH);
        prefEditor.putInt("finishMinute",fM);
        prefEditor.putInt("cycle22",cycle22);
prefEditor.putLong("today",cal.getTimeInMillis() );
       if(radio22.isChecked()){
           prefEditor.putInt("set",1);
       }
       if (radio52.isChecked()){
           prefEditor.putInt("set",0);
       }

        prefEditor.apply();
        stopService(new Intent(this, JobService.class));
        startService(new Intent(this, JobService.class));
    }
    }

    public void makeDial(View view){
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), "custom");
    }

    public void okClicked(int setVal) {
        cycle22 = setVal;
    }


}