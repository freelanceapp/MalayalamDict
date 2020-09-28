package com.elamblakatt.dict_eng_malayalam.Utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.elamblakatt.dict_eng_malayalam.HomeActivity;
import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;


/**
 * Created by admin on 1/30/2016.
 */
public class AlarmService extends IntentService
{

    private NotificationManager mManager;
    private SharedPreferences shrdPref;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AlarmService(String name) {
        super(name);
    }

    public AlarmService() {
        super("AlarmService");
    }
    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);


    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        SharedPreferences preferences = this.getApplicationContext().getSharedPreferences("MyPref", getApplicationContext().MODE_PRIVATE);

        boolean dailyNotification=preferences.getBoolean("DailyNotification", true);
        WordDaoImpl wordDao;
        if(dailyNotification)
        {
            Word todayWord =null;
            String upperString="";
            while(todayWord==null) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int wordIndex = pref.getInt("WordIndex", 1);
                wordIndex=wordIndex+1;
                wordDao = new WordDaoImpl(new DatabaseOpenHelper(this.getApplicationContext()).getReadableDatabase());
                todayWord = wordDao.getWordFromId(wordIndex);
                if (todayWord != null) {
                    String word = todayWord.getEnglish_meaning();
                    if(word!=null) {
                        upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
                    }
                    SharedPreferences.Editor myeditor = pref.edit();
                    myeditor.putInt("WordIndex", wordIndex) ;
                    myeditor.commit();
                    }
                else
                {
                    wordIndex=wordIndex+1;
                    SharedPreferences.Editor myeditor = pref.edit();
                    myeditor.putInt("WordIndex", wordIndex) ;
                    myeditor.commit();
                }
            }



            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("TodayVerse", "" + upperString);
            editor.commit();
            mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);


            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("Word of Day");

            bigTextStyle.setSummaryText("Click to see the message");
            bigTextStyle.bigText(upperString);

            PendingIntent pendingNotificationIntent;// = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            // notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // notification.setLatestEventInfo(this.getApplicationContext(), "Today's Verse", todayVerse, pendingNotificationIntent);
            intent = new Intent(this.getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("TODAY_VERSE", upperString);
            pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap iconLarge = BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.getApplicationContext())


                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(iconLarge)
                    .setContentTitle("Word of Day")
                    .setContentText(upperString)
                    .setStyle(bigTextStyle)
                    .setAutoCancel(true)
                    .setContentIntent(pendingNotificationIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getApplicationContext());
            notificationManager.notify(1, notificationBuilder.build());
        }

    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
