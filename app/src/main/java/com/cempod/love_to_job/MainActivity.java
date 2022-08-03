package com.cempod.love_to_job;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    JobService mService;
    boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService

        startForegroundService(new Intent(this, JobService.class));
        Intent intent = new Intent(this, JobService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            JobService.LocalBinder binder = (JobService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if (mBound) {
//сюда обращаться к методам
                ProgressBar progressBar = findViewById(R.id.progressBar);
                TextView procentageBox = findViewById(R.id.procentageBox);
                DecimalFormat precision = new DecimalFormat("0.00");
                SimpleDateFormat timeToScreen = new SimpleDateFormat("HH:mm:ss");
                TextView mainTime = findViewById(R.id.mainTime);
                TextView annotation = findViewById(R.id.annotation);
if(!mService.isInit()){
    goSet();
}
                Thread myThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Date date = new Date();
                            mainTime.post(new Runnable() {
                                @Override
                                public void run() {
                               mainTime.setText(timeToScreen.format(date));
                                }
                            });
                            if(mService.ifJob(date)&&mService.ifJobDay(date)){
                                progressBar.post(new Runnable() {
                                    public void run() {
                                       progressBar.setProgress((int)mService.getProcentage(date));
                                    }
                                });
                                procentageBox.post(new Runnable() {
                                    public void run() {
                                        procentageBox.setText(precision.format(mService.getProcentage(date))+"%");
                                    }
                                });
                                annotation.post(new Runnable() {
                                    @Override
                                    public void run() {
                                   annotation.setText("Осталось "+ mService.getTimeToHome(date));
                                    }
                                });
                            }else{
                                progressBar.post(new Runnable() {
                                    public void run() {
                                        progressBar.setProgress(0);
                                    }
                                });
                                procentageBox.post(new Runnable() {
                                    public void run() {
                                        procentageBox.setText(":3");
                                    }
                                });
                                annotation.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        annotation.setText("отдыхай");
                                    }
                                });
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                myThread.start();


            }
            }





        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        menu.add(0,1,0,"Настройки");
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId()==1){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
public void goSet(){
    Intent intent = new Intent(this, Settings.class);
    startActivity(intent);
}

}