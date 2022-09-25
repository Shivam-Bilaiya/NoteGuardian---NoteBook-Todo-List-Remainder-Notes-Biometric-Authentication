package my.app.snotes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

public class AlarmNotification extends BroadcastReceiver {

    public final String  channel_Id = "1";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_MUTABLE);
        NotificationChannel channel = new NotificationChannel(channel_Id,"1", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);


        Notification notification=new Notification.Builder(context.getApplicationContext(),channel_Id)
                .setContentTitle("It's time to plan your day")
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentText("Making a good habit of planing your day so that you will not miss any task")
                .setChannelId(channel_Id)
                .setSmallIcon(R.drawable.ic_baseline_notes_24)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.snotesicon))
                .build();


        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(1,notification);

    }
}