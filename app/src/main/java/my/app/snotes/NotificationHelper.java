//package shivam.notesapp.snotes;
//
//import android.app.AlarmManager;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//
//import java.util.Calendar;
//
//public class NotificationHelper {
//    public static int ALARM_TYPE_RTC = 100;
//
//
//    /**
//     * This is the real time /wall clock time
//     * @param context
//     */
//
//
//
//        public static void scheduleRepeatingRTCNotification(Context context, String hour, String min) {
//            //get calendar instance to be able to select what time notification should be scheduled
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            //Setting time of the day (8am here) when notification will be sent every day (default)
//            calendar.set(Calendar.HOUR_OF_DAY,
//                    Integer.getInteger(hour, 15),
//                    Integer.getInteger(min, 10));
//
//            //Setting intent to class where Alarm broadcast message will be handled
//            Intent intent = new Intent(context, AlarmNotification.class);
//            //Setting alarm pending intent
//            PendingIntent alarmIntentRTC = PendingIntent.getBroadcast(context, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            //getting instance of AlarmManager service
//            AlarmManager alarmManagerRTC = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//            //Setting alarm to wake up device every day for clock time.
//            //AlarmManager.RTC_WAKEUP is responsible to wake up device for sure, which may not be good practice all the time.
//            // Use this when you know what you're doing.
//            //Use RTC when you don't need to wake up device, but want to deliver the notification whenever device is woke-up
//            //We'll be using RTC.WAKEUP for demo purpose only
//            alarmManagerRTC.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentRTC);
//        }
//
//    public static NotificationManager getNotificationManager(Context context) {
//        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//    }
//
//    }
