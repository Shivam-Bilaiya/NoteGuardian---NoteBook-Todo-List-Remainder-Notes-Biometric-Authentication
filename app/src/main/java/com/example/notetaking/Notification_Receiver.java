package com.example.notetaking;

import static android.content.Context.MODE_PRIVATE;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class Notification_Receiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "this.is.my.channelId";//you can add any id you want
    Notification.Builder builder;
    public static  final  String shared_pref = "sharedPrefs";
    public static final String toggleBtn = "toggleBtn";
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification();


    }







}

class NotificationHelper {

    ActivityResultLauncher<Intent> activityResultLauncherForAddNote;
    NoteViewModel noteViewModel;


    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    NotificationHelper(Context context) {
        mContext = context;
    }

    void createNotification()
    {

        Intent intent = new Intent(mContext , SplashScreen.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                    0 /* Request code */, intent,
                    PendingIntent.FLAG_MUTABLE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
            mBuilder.setSmallIcon(R.drawable.ic_baseline_notes_24);
            mBuilder.setContentTitle("SNotes")
                    .setContentText("It's time to plan your day")
                    .setAutoCancel(false)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        }
        else{
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                    0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
            mBuilder.setSmallIcon(R.drawable.ic_baseline_notes_24);
            mBuilder.setContentTitle("SNotes")
                    .setContentText("It's time to plan your day")
                    .setAutoCancel(false)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        }




    }










}
