package com.cempod.love_to_job;

import static com.cempod.love_to_job.R.mipmap.ic_launcher_round;
import static java.util.Calendar.DAY_OF_YEAR;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class JobService extends Service {
    public JobService() {
    }

    SimpleDateFormat myHour = new SimpleDateFormat("HH");
    SimpleDateFormat myMinute = new SimpleDateFormat("mm");
    DecimalFormat precision = new DecimalFormat("0.00");
    int cycle22 = 0;
    Boolean init = false;
    Date today;
    int set = 0;
    int startHour = 0;
    int finishHour = 0;
    int startMinute = 0;
    int finishMinute = 0;
    String jobStatus, jobProcent;
    private final IBinder binder = (IBinder) new LocalBinder();

    public class LocalBinder extends Binder {
        JobService getService() {
            // Return this instance of LocalService so clients can call public methods
            return JobService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    public void onCreate() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "Main",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("My channel");
            channel.enableLights(false);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
Calendar cToday = new GregorianCalendar();
        cToday = Calendar.getInstance();
        Context ct = this;
        SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        if (!settings.contains("SAVED")) {
            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.putString("SAVED", "YES");
            prefEditor.putInt("startHour", 9);
            prefEditor.putInt("startMinute", 0);
            prefEditor.putInt("finishHour", 18);
            prefEditor.putInt("finishMinute", 0);
            prefEditor.putInt("set",0);
            prefEditor.putInt("cycle22",cycle22);
            prefEditor.putLong("today", cToday.getTimeInMillis());
            prefEditor.apply();
            startHour = 9;
            finishHour = 0;
            startMinute = 18;
            finishMinute = 0;
            set = 0;
            cycle22 = 0;
            today = cToday.getTime();
init=false;
        } else {
            startHour = settings.getInt("startHour", 0);
            finishHour = settings.getInt("finishHour", 0);
            startMinute = settings.getInt("startMinute", 0);
            finishMinute = settings.getInt("finishMinute", 0);
            set = settings.getInt("set",0);
            today = new Date(settings.getLong("today",0));
            cycle22 = settings.getInt("cycle22",0);
            init = true;
        }


        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Date date = new Date();
                    int progVisible = 0;
                    int prog = 0;
                    SimpleDateFormat timeToScreen = new SimpleDateFormat("HH:mm:ss");
                    int notiIcon;
                    if (ifJob(date) && ifJobDay(date)) {
                        jobStatus = "Работаем, осталось " + getTimeToHome(date);
                        jobProcent = precision.format(getProcentage(date)) + "% отработали";
                        notiIcon = R.drawable.ic_work;
                        progVisible = 100;
                        prog = (int) getProcentage(date);
                    } else {
                        jobStatus = "Отдыхаем";
                        notiIcon = R.drawable.ic_home;
                        if(ifJobDay(date)){
                            jobProcent = "И ждём выходные";
                        }else{
                            jobProcent = "Сегодня выходной";
                        }

                        progVisible = 0;
                    }

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(ct, "CHANNEL_ID")
                                    .setSmallIcon(notiIcon)
                                    .setContentTitle(jobStatus)
                                    .setContentText(jobProcent)
                                    .setOnlyAlertOnce(true)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                            R.drawable.noti))
                                    .setProgress(progVisible, prog, false);


                    Notification notification = builder.build();

                    startForeground(1, notification);
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

    public Boolean ifJob(Date date) {
        boolean a = false, b = false;
        if (Integer.parseInt(myHour.format(date)) >= startHour) {
            if (Integer.parseInt(myHour.format(date)) > startHour) {
                a = true;
            } else {
                if (Integer.parseInt(myMinute.format(date)) >= startMinute) {
                    a = true;
                } else {
                    a = false;
                }
            }
        } else {
            a = false;
        }
        if (a == true) {

            if (Integer.parseInt(myHour.format(date)) <= finishHour) {
                if (Integer.parseInt(myHour.format(date)) < finishHour) {
                    b = true;
                } else {
                    if (finishMinute > 0) {
                        if (Integer.parseInt(myMinute.format(date)) < finishMinute) {
                            b = true;
                        } else {
                            b = false;
                        }
                    } else {
                        b = false;
                    }
                }
            } else {
                b = false;
            }

        }


        if (a == true && b == true) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean ifJobDay(Date date) {
        switch (set) {
            case (0):
                if (ifJob52(date)) {
                    return true;
                } else {
                    return false;
                }
            case (1):
                if (ifJob22(date)) {
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }



    public Boolean ifJob52(Date date){
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        if((c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)||(c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)){
return false;
        }else {
            return true;
        }
    }

    public Boolean ifJob22(Date date){
        Calendar c, cToday;
        c = new GregorianCalendar();
        c.setTime(date);
        cToday = new GregorianCalendar();
        cToday.setTime(today);
        if(c.get(DAY_OF_YEAR) == cToday.get(DAY_OF_YEAR)){
           if(cycle22 <= 1){
               return true;
           }
           else{
               return false;
           }
        }else{


if(incCycle(c.get(DAY_OF_YEAR),cToday.get(DAY_OF_YEAR))+cycle22>3){
    cycle22 = incCycle(c.get(DAY_OF_YEAR),cToday.get(DAY_OF_YEAR))+cycle22 - 4;
}
else{
    cycle22 += incCycle(c.get(DAY_OF_YEAR),cToday.get(DAY_OF_YEAR));
}
            today = date;
            SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.putInt("cycle22",cycle22);
            prefEditor.putLong("today",c.getTimeInMillis());
            prefEditor.apply();
        }
        if(cycle22 <= 1){
            return true;
        }
        else{
            return false;
        }
    }

    private int incCycle(int c, int cToday) {

        int val = 0;
        if(c>cToday){
             val = c-cToday;
            if(val<4){
                return val;
            }
            else{
                return val % 4;
            }
        }
        else{
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);
            if (cal.getActualMaximum(DAY_OF_YEAR) > 365){
                val = 366-cToday + c;
            }
            else{
                val = 365-cToday + c;
            }
            if(val<4){
                return val;
            }
            else{
                return val % 4;
            }
        }


    }
public boolean isInit(){
        return init;
}
    public String getTimeToHome(Date date){
        int hour = 0, min = 0;
if(finishMinute == 0) {
    if (Integer.parseInt(myMinute.format(date))>0) {
        min = 60 - Integer.parseInt(myMinute.format(date));
        hour = finishHour - Integer.parseInt(myHour.format(date)) -1;
    }else{
        min = 0;
        hour = finishHour - Integer.parseInt(myHour.format(date)) ;
    }

}else{
    if(Integer.parseInt(myMinute.format(date))> finishMinute){
        min = 60 - (Integer.parseInt(myMinute.format(date))-finishMinute);
        hour = finishHour - Integer.parseInt(myHour.format(date)) -1;
    }else{
        min = finishMinute - Integer.parseInt(myMinute.format(date));
        hour = finishHour - Integer.parseInt(myHour.format(date)) ;
    }
}

        return(Integer.toString(hour)+"ч "+Integer.toString(min)+ "м" );
    }

    public float getProcentage(Date date){
        float proc = 0;
        int hour = 0, min = 0;
        if(finishMinute == 0) {
            if (Integer.parseInt(myMinute.format(date))>0) {
                min = 60 - Integer.parseInt(myMinute.format(date));
                hour = finishHour - Integer.parseInt(myHour.format(date)) -1;
            }else{
                min = 0;
                hour = finishHour - Integer.parseInt(myHour.format(date)) ;
            }

        }else{
            if(Integer.parseInt(myMinute.format(date))> finishMinute){
                min = 60 - (Integer.parseInt(myMinute.format(date))-finishMinute);
                hour = finishHour - Integer.parseInt(myHour.format(date)) -1;
            }else{
                min = finishMinute - Integer.parseInt(myMinute.format(date));
                hour = finishHour - Integer.parseInt(myHour.format(date)) ;
            }
        }
        proc = 100f-((hour*60 + min)*100f)/(((finishHour*60f)+finishMinute)-((startHour*60f)+startMinute));
        return proc;
    }

    public void onDestroy() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Сервис закрылся :(", Toast.LENGTH_SHORT);
        //toast.show();
    }
}