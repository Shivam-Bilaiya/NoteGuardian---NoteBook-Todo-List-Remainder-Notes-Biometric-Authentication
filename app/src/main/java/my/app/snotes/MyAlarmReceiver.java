package my.app.snotes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;


public class MyAlarmReceiver extends BroadcastReceiver {
    public final String  channel_Id = "100000";
    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, intent.getStringExtra("title"),intent.getStringExtra("description"));

    }

    private void createNotification(Context context, CharSequence title, CharSequence description) {
        Intent intent = new Intent(context, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1001, intent,PendingIntent.FLAG_MUTABLE);
        NotificationChannel channel = new NotificationChannel(channel_Id,"1", NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);



        Notification notification=new Notification.Builder(context.getApplicationContext(),channel_Id)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentText(description)
                .setChannelId(channel_Id)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_notes_24)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.snotesicon))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[] { 1000, 1000})
                .setTicker("SNOTES")
                .build();


        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(1002,notification);
    }


}
