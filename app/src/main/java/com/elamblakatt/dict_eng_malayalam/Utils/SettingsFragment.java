package com.elamblakatt.dict_eng_malayalam.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.fragment.SeachHistoryListActivity;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SettingsFragment extends Fragment implements
		View.OnClickListener, TimePickerDialog.OnTimeSetListener
{
	private AppCompatActivity mActivity;
	private LinearLayout linlaySettingBg,linlayVerseTime,linlayTimeLbl;
	private RelativeLayout rellayONOFF_Lbl,relativelayoutShare,relativelayoutRate,relativelayoutHistory;
	private TimePickerDialog fromTimePickerDialog;
	private TextView txtvwVerseTimeSet,tv_verse_time_lbl,txtvwON_OFF,tv_SetTheme;
	private int hour,min;
    private Context context;
	private Toolbar toolbar;

	private Spinner spinner1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.settings_activity, container, false);
		context=getActivity();
		toolbar=(Toolbar)view.findViewById(R.id.toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
		linlaySettingBg=(LinearLayout)view.findViewById(R.id.linlaySettingBg);
		tv_verse_time_lbl=(TextView)view.findViewById(R.id.tv_verse_time_lbl);
		txtvwVerseTimeSet=(TextView)view.findViewById(R.id.txtvwVerseTimeSet);
		txtvwON_OFF=(TextView)view.findViewById(R.id.txtvwON_OFF);

		linlayVerseTime=(LinearLayout)view.findViewById(R.id.linlayVerseTime);
		linlayTimeLbl=(LinearLayout)view.findViewById(R.id.linlayTimeLbl);
		rellayONOFF_Lbl=(RelativeLayout)view.findViewById(R.id.rellayONOFF_Lbl);
		relativelayoutShare = (RelativeLayout)view.findViewById(R.id.relativelayoutShare);
		relativelayoutRate = (RelativeLayout)view.findViewById(R.id.relativelayoutRate);
		relativelayoutHistory = (RelativeLayout)view.findViewById(R.id.relativelayoutHistory);

		linlayVerseTime.setOnClickListener(this);
		linlayTimeLbl.setOnClickListener(this);
		txtvwON_OFF.setOnClickListener(this);
		relativelayoutShare.setOnClickListener(this);
		relativelayoutRate.setOnClickListener(this);
		relativelayoutHistory.setOnClickListener(this);

		// Spinner item selection Listener
		setVerseSettings();
		return view;
	}

		void setVerseSettings()
	{
		SharedPreferences preferences = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);

		boolean dailyNotification=preferences.getBoolean("DailyNotification", true);

		if(dailyNotification) {
			hour = Integer.parseInt(preferences.getString("VerseHour", "" + 6));
			min = Integer.parseInt(preferences.getString("VerseMin", "" + 30));
			txtvwVerseTimeSet.setText(convertTimeToAMPM(hour + ":" + min));

			txtvwON_OFF.setText("ON");
			txtvwON_OFF.setTextColor(context.getResources().getColor(R.color.white));
			rellayONOFF_Lbl.setBackgroundResource(R.drawable.curved_bg_app_sel);

			txtvwVerseTimeSet.setVisibility(View.VISIBLE);
		}
		else
		{
			txtvwVerseTimeSet.setVisibility(View.GONE);
			txtvwON_OFF.setText("OFF");
			txtvwON_OFF.setTextColor(context.getResources().getColor(R.color.blackColor));
			rellayONOFF_Lbl.setBackgroundResource(R.drawable.curved_bg_app);
		}
	}


	@Override
	public void onClick(View view)
	{
		if(view==txtvwON_OFF)
		{

			SharedPreferences preferences = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
			boolean dailyNotification=preferences.getBoolean("DailyNotification", true);
			if(dailyNotification)
			{
				txtvwON_OFF.setText("OFF");
				txtvwON_OFF.setTextColor(context.getResources().getColor(R.color.blackColor));
				rellayONOFF_Lbl.setBackgroundResource(R.drawable.curved_bg_app);
				txtvwVerseTimeSet.setVisibility(View.GONE);

				SharedPreferences.Editor editor = context.getSharedPreferences("MyPref", context.MODE_PRIVATE).edit();
				editor.putBoolean("DailyNotification", false);
				editor.commit();
				Utils.cancelReminderDaileVerse(context);
//				disableAlarm();
			}
			else
			{
				txtvwVerseTimeSet.setVisibility(View.VISIBLE);
				txtvwON_OFF.setText("ON");
				txtvwON_OFF.setTextColor(context.getResources().getColor(R.color.white));
				rellayONOFF_Lbl.setBackgroundResource(R.drawable.curved_bg_app_sel);
				SharedPreferences.Editor editor = context.getSharedPreferences("MyPref", context.MODE_PRIVATE).edit();
				editor.putBoolean("DailyNotification", true);
				editor.commit();
				Utils.RegisterAlarmBroadcast(context);

			}
		}
		else if(view==linlayTimeLbl)
		{
//			fromTimePickerDialog = TimePickerDialog.newInstance(this,
//					hour, min, false, false);//, "Select time for Verse");
			fromTimePickerDialog = TimePickerDialog.newInstance(this,hour,min,false);
			fromTimePickerDialog.show(getActivity().getSupportFragmentManager(),
					"timepicker");
		}
		else if (view==relativelayoutHistory){
			Intent intent = new Intent(context, SeachHistoryListActivity.class);
			startActivity(intent);
		}
		else if (view==relativelayoutShare){
			shareAction();
		}
		else if (view==relativelayoutRate){
			rateApp();
		}

	}
	private void rateApp() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
		startActivity(intent);
	}

	private void shareAction()
	{
		String strSelected="Install the "+getString(R.string.app_name)+" app. "+getString(R.string.app_link);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, strSelected);
		startActivity(Intent.createChooser(intent, "Share with"));

	}



	@Override
	public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

		SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyPref", context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("VerseHour", ""+hourOfDay);
		editor.commit();
		SharedPreferences.Editor editor1 = pref.edit();
		editor1.putString("VerseMin", "" + minute);
		editor1.commit();
		hour=hourOfDay;
		min=minute;
		txtvwVerseTimeSet.setText(convertTimeToAMPM(hour + ":" + min));

		Utils.RegisterAlarmBroadcast(context);

	}

	String convertTimeToAMPM(String time)
	{
		Date dateObj=null;
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");

			dateObj = sdf.parse(time);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new SimpleDateFormat("K:mm a").format(dateObj);
	}




}
