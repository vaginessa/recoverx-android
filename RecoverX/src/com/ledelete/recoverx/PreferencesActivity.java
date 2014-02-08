package com.ledelete.recoverx;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends PreferenceActivity {
	
	protected Preference advancedPreference = null;
	protected Preference addDevice = null;
    protected Preference reportBug = null;

    private Void debugSystemOut(String log)
    {
        if(BuildConfig.DEBUG)
        {
            System.out.println(log);
        }
        return null;
    }
	
	private OnPreferenceClickListener listenerAdvancedPreference = new Preference.OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			Intent intent = new Intent(PreferencesActivity.this, AdvancedPreferencesActivity.class);
    		startActivity(intent);
			
			return false;
		}
	};
	
	private OnPreferenceClickListener listenerAddDevice = new Preference.OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://ledelete.shost.ca/recoverx/form.php"));
			startActivity(browserIntent);
			
			return false;
		}
	};

    private OnPreferenceClickListener listenerReportBug = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://ledelete.shost.ca/recoverx/bugreport.php"));
            startActivity(browserIntent);

            return false;
        }
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		advancedPreference = findPreference("advancedPreferences");
		addDevice = findPreference("addDevice");
        reportBug = findPreference("reportBug");

		advancedPreference.setOnPreferenceClickListener(listenerAdvancedPreference);
		addDevice.setOnPreferenceClickListener(listenerAddDevice);
        reportBug.setOnPreferenceClickListener(listenerReportBug);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);

        if (sp.getBoolean("booleanAnalytics", true))
        {
            EasyTracker.getInstance(PreferencesActivity.this).activityStart(PreferencesActivity.this);
            debugSystemOut("analytics start");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);

        if (sp.getBoolean("booleanAnalytics", true))
        {
            EasyTracker.getInstance(PreferencesActivity.this).activityStop(PreferencesActivity.this);
            debugSystemOut("analytics stop");
        }
    }
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
}
