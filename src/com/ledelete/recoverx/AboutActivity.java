package com.ledelete.recoverx;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

@SuppressWarnings("deprecation")
public class AboutActivity extends PreferenceActivity {
	
	protected Preference recoverxXdaLink = null;
	protected Preference recoverxGooglePlusLink = null;
	protected Preference recoverxWebsiteLink = null;
    protected Preference recoverxYoutubeLink = null;
	protected Preference recoverxVersion = null;

	private OnPreferenceClickListener listenerRecoverxXdaLink = new Preference.OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://forum.xda-developers.com/showthread.php?t=2595072"));
			startActivity(browserIntent);
			
			return false;
		}
	};
	
	private OnPreferenceClickListener listenerRecoverxGooglePlusLink = new Preference.OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://plus.google.com/+YohannLEON"));
			startActivity(browserIntent);
			
			return false;
		}
	};

    private OnPreferenceClickListener listenerRecoverxYoutubeLink = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://youtu.be/DR7rNnqhUw0"));
            startActivity(browserIntent);

            return false;
        }
    };
	
	private OnPreferenceClickListener listenerRecoverxWebsiteLink = new Preference.OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://ledelete.shost.ca"));
			startActivity(browserIntent);
			
			return false;
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about);
		
		recoverxXdaLink = findPreference("recoverxXdaLink");
		recoverxGooglePlusLink = findPreference("recoverxGooglePlusLink");
		recoverxWebsiteLink= findPreference("recoverxWebsiteLink");
        recoverxYoutubeLink= findPreference("recoverxYoutubeLink");
		recoverxVersion = findPreference("recoverxVersion");
		
		recoverxXdaLink.setOnPreferenceClickListener(listenerRecoverxXdaLink);		
		recoverxGooglePlusLink.setOnPreferenceClickListener(listenerRecoverxGooglePlusLink);
        recoverxYoutubeLink.setOnPreferenceClickListener(listenerRecoverxYoutubeLink);
		recoverxWebsiteLink.setOnPreferenceClickListener(listenerRecoverxWebsiteLink);
		
		String appVersion = null;
		
		try
		{
			appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		}
		catch (Exception e)
		{
			return;
		}
		
		if (appVersion == null)
		{
			recoverxVersion.setSummary("Created by Yohann LEON (LEDelete), Creative Commons Attribution");
		}
		else
		{
			recoverxVersion.setSummary("Version " + appVersion + "\n\nCreated by Yohann LEON (LEDelete)\nCreative Commons Attribution");
		}
			
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
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
