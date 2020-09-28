package com.elamblakatt.dict_eng_malayalam.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.elamblakatt.dict_eng_malayalam.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jasmine on 11/01/17.
 */

public class Utils {

    public static final int DAILY_REMINDER_REQUEST_CODE=100;

    public static String getSDCARDPath(Context cntxt) {
        return Environment.getExternalStorageDirectory().toString() + "/"
                + cntxt.getString(R.string.app_name);
    }

    public static boolean checkSDCARDPresent() {
        Boolean isSDPresent = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            return isSDPresent;
        } else {
            return isSDPresent;
        }
    }

    /**
     * Generates random number between 0 and 100
     *
     * @return
     */
    public static int GetRandomNo() {
        int min = 1000;
        int max = 4000;

        Random r = new Random();
        int num = r.nextInt(max - min + 1) + min;
        return num;
    }

    public static boolean isNotEmpty(Object... toCheck) {
        for (Object o : toCheck) {
            if (Utils.isEmpty(o)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(Double... toCheck) {
        for (Double o : toCheck) {
            if (Utils.isEmpty(o)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Double toCheck) {
        return toCheck == null || toCheck == 0;
    }

    public static boolean isEmpty(Object toCheck) {
        if (toCheck instanceof String)
            return isEmpty((String) toCheck);
        if (toCheck instanceof String[])
            return isEmpty((String[]) toCheck);
        if (toCheck instanceof Collection<?>)
            return isEmpty((Collection<?>) toCheck);
        if (toCheck instanceof Map<?, ?>)
            return isEmpty((Map<?, ?>) toCheck);
        return toCheck == null;
    }

    public static boolean isEmpty(String toCheck) {
        return toCheck == null || toCheck.trim().length() == 0
                || toCheck.equals("null");
    }

    public static boolean isEmpty(String[] toCheck) {
        return toCheck == null || toCheck.length == 0;
    }

    public static boolean isEmpty(Collection<?> toCheck) {
        Boolean t = toCheck == null || toCheck.size() == 0;
        if (!t) {
            boolean allEmpty = true;
            for (Object o : toCheck) {
                if (!isEmpty(o)) {
                    allEmpty = false;
                    break;
                }
            }
            t = allEmpty;
        }
        return t;
    }

    public static void addToSharedPref(String keyValue, Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(keyValue, true);
        editor.commit();
    }

    public static void removefromSharedPref(String keyValue, Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(keyValue, false);
        editor.commit();
    }

    public static boolean isPresentInSharedPref(String keyValue, Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(keyValue,false);
    }

    public  static void RegisterAlarmBroadcast(Context context)
    {

        SharedPreferences preferences = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
        int hour= Integer.parseInt(preferences.getString("VerseHour", "6").toString());
        int min= Integer.parseInt(preferences.getString("VerseMin", "30").toString());
        Calendar calNow = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        if (calendar.compareTo(calNow) <= 0) {
            // Today Set time passed, count to tomorrow
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.SECOND, 0);
        cancelReminderDaileVerse(context);

        ComponentName receiver = new ComponentName(context, DailyAlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        PendingIntent pendingIntent;



        Intent myIntent = new Intent(context.getApplicationContext(), DailyAlarmReceiver.class);
        myIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), DAILY_REMINDER_REQUEST_CODE, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);



//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
////            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        else {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//
//        }





    }

    public static String getMonthName(int month)
    {
        switch(month+1){
            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "Mar";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "Jun";

            case 7:
                return "Jul";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            case 12:
                return "Dec";
        }

        return "";


    }

    public static String getFormattedDisplayDate(String updDate) {
        try {
            String dateStr = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date current = sdf.parse(updDate);
      /*  SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

            Date date = (Date) formatter.parse(updDate);
*/
            Timestamp time=new Timestamp(current.getTime());
            if (Utils.isNotEmpty(time)) {
                Date dt = time;
                Date currDate = new Date();

                long dateDiff = currDate.getTime() - dt.getTime();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
                if (seconds < 60) {
                    dateStr = "1 min";
                } else {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
                    if (minutes < 60) {
                        if (minutes == 1) {
                            dateStr = minutes + " min";
                        } else {
                            dateStr = minutes + " min";
                        }
                    } else {
                        long hours = TimeUnit.MILLISECONDS.toHours(dateDiff);
                        if (hours < 24) {
                            if (hours == 1) {
                                dateStr = hours + " hr";
                            } else {
                                dateStr = hours + " hr";
                            }
                        } else {
                            long days = TimeUnit.MILLISECONDS.toDays(dateDiff);
                            if (days < 7) {
                                if (days == 1)
                                    dateStr = days + " d";
                                else
                                    dateStr = days + " d";
                            } else {
                                if (days < 30) {
                                    dateStr = (int) Math.ceil((double)days / 7) + " wk";
                                } else {
                                    if (days < 365) {
                                        long daysDiff = days - 30;
                                        if(daysDiff > 29){
                                            dateStr = (int) Math.ceil((double)days / 30) + " m";
                                        }else{
                                            dateStr = (int) Math.floor((double)days / 30) + " m";
                                        }
                                    } else {
                                        dateStr = (int) Math.ceil((double)days / 365) + " yr";
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return dateStr!="" ? dateStr+" ago":dateStr;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return  null;

        }
    }


    public static void cancelReminderDaileVerse(Context context)
    {
        // Disable a receiver
        ComponentName receiver = new ComponentName(context, DailyAlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, DailyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static String getCurrentDateAndTimeInString() {

        Timestamp timeCurr = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
        Date current;
        try {
            current = sdf.parse(currentDateandTime);
            timeCurr = new Timestamp(current.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return currentDateandTime;
    }


//    public static void getPathName(final  Context context,final PathGeterInterface pathGeterInterface)
//    {
//        final Dialog dialogPhoto = new Dialog(context);
//        dialogPhoto.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogPhoto.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        LayoutInflater inflate = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//        View view = inflate.inflate(R.layout.dialog_add_path, null);
//        TextView txtvwClose = (TextView) view.findViewById(R.id.xtxtvwDone);
//        final EditText edtxtPath=(EditText)view.findViewById(R.id.edtxttPath);
//        txtvwClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(edtxtPath.getText().toString().length()>0)
//                {
//                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(edtxtPath.getWindowToken(), 0);
//
//                    dialogPhoto.dismiss();
//                    pathGeterInterface.setPath(edtxtPath.getText().toString());
//                }
//                else
//                {
//                    edtxtPath.setError("Pls enter path");
//                    edtxtPath.requestFocus();
//                }
//            }
//        });
//        dialogPhoto.setContentView(view);
//        dialogPhoto.show();
//
//    }
}
