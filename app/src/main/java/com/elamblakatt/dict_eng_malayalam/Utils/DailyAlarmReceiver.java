package com.elamblakatt.dict_eng_malayalam.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.elamblakatt.dict_eng_malayalam.HomeActivity;
import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;

import static android.content.Context.MODE_PRIVATE;
import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by admin on 1/30/2016.
 */
public class DailyAlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                Utils.RegisterAlarmBroadcast(context);
             }
        }
        else {

            sendNotification(intent,context);
//            ComponentName comp = new ComponentName(context.getPackageName(),
//                    AlarmService.class.getName());
//            startWakefulService(context, (intent.setComponent(comp)));
        }
    }



    private void sendNotification(Intent intent,Context context) {

        SharedPreferences preferences = context.getSharedPreferences("MyPref", MODE_PRIVATE);

        boolean dailyNotification = preferences.getBoolean("DailyNotification", true);
        WordDaoImpl wordDao;
        if (dailyNotification) {
            Word todayWord = null;
            String upperString = "";
            while (todayWord == null) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                int wordIndex = pref.getInt("WordIndex", 1);
                wordIndex = wordIndex + 1;
                wordDao = new WordDaoImpl(new DatabaseOpenHelper(context).getReadableDatabase());
                todayWord = wordDao.getWordFromId(wordIndex);
                if (todayWord != null) {
                    String word = todayWord.getEnglish_meaning();
                    if (word != null) {
                        upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
                    }
                    SharedPreferences.Editor myeditor = pref.edit();
                    myeditor.putInt("WordIndex", wordIndex);
                    myeditor.commit();
                } else {
                    wordIndex = wordIndex + 1;
                    SharedPreferences.Editor myeditor = pref.edit();
                    myeditor.putInt("WordIndex", wordIndex);
                    myeditor.commit();
                }
            }


            SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("TodayVerse", "" + upperString);
            editor.commit();

            int notificationId = 1;
            String channelId = "DailyWord";
            String channelName = "DailyWord";


            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("Today Verse");

            bigTextStyle.setSummaryText("Click to see the message");
            bigTextStyle.bigText(upperString);


            PendingIntent pendingNotificationIntent;// = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            // notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // notification.setLatestEventInfo(this.getApplicationContext(), "Today's Verse", todayVerse, pendingNotificationIntent);
            intent = new Intent(context, HomeActivity.class);
            intent.setAction("VIEW");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("TODAY_VERSE", upperString);


            pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap iconLarge = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(iconLarge)
                    .setContentTitle("Word of Day")
                    .setContentText(upperString)
                    .setStyle(bigTextStyle)
                    .setAutoCancel(true)
                    .setContentIntent(pendingNotificationIntent);
            notificationBuilder.addAction(R.mipmap.ic_launcher, "View", pendingNotificationIntent);
            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getApplicationContext());
            mNotificationManager.notify(1, notificationBuilder.build());

        }

    }




}
