package com.ledelete.recoverx;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    protected ImageView imageViewCloud = null;
    protected TextView welcomeTextView = null;
    protected Button startButton = null;

    Usage use = new Usage();
    
    private boolean haveInternet(){
        NetworkInfo info = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        
        if (info==null || !info.isConnected()) 
        {
            return false;
        }
        if (info.isRoaming()) 
        {
        	return true;
        }
        return true;
    }
    
    private OnClickListener listenerBeginProcedure = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

			if (haveInternet())
			{
				new DownloadFileAsync().execute("http://ledelete.shost.ca/recoverx/android/manifest.xml");
			}
			else
			{
				Toast.makeText(MainActivity.this, "You're disconnected from Internet", Toast.LENGTH_LONG).show();
			}
		}
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        imageViewCloud = (ImageView)findViewById(R.id.imageView1);
        welcomeTextView = (TextView)findViewById(R.id.textView1);
        startButton = (Button)findViewById(R.id.button1);

        imageViewCloud.setVisibility(View.INVISIBLE);
        welcomeTextView.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.INVISIBLE);

        rootOps();

        Button beginProcedure = (Button)findViewById(R.id.button1);
        beginProcedure.setOnClickListener(listenerBeginProcedure);
	}
	
	@Override
	public void onStart() {
	    super.onStart();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (sp.getBoolean("booleanAnalytics", true))
        {
            EasyTracker.getInstance(MainActivity.this).activityStart(MainActivity.this);
            use.debugSystemOut("analytics start");
        }
	}

	@Override
	public void onStop() {
	    super.onStop();

	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

    	if (sp.getBoolean("booleanAnalytics", true))
		{
    		EasyTracker.getInstance(MainActivity.this).activityStop(MainActivity.this);
       	    use.debugSystemOut("analytics stop");
		}    
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {				
		switch (item.getItemId())
        {
	        case R.id.actionSettings:	    	    	    	    
	        	Intent intent0 = new Intent(MainActivity.this, PreferencesActivity.class);
				startActivity(intent0);
				return true;
	        case R.id.actionHelp:	        	
	        	Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://forum.xda-developers.com/showthread.php?t=2595072"));
				startActivity(browserIntent);
	        	return true;
	        case R.id.actionAbout:        	
	        	Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent1);
				return true;
        }
        return false;
	}

    // Startup sequence //////////

    private void busyboxOps()
    {
        File busybox1 = new File("/system/bin/busybox");
        File busybox2 = new File("/system/xbin/busybox");


        if (!busybox1.exists() && !busybox2.exists())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

            alert.setMessage(Html.fromHtml("<small>Please, install busybox on your device in order to continue</small>"));
            alert.setCancelable(false);

            alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    finish();
                }
            });

            alert.show();
        }
        else
        {
            finalOps();
        }
    }

    private void finalOps()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File dir0 = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx");
            File dir1 = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/cache");
            File dir2 = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download");

            if (!dir0.exists())
            {
                dir0.mkdirs();
            }

            if (!dir1.exists())
            {
                dir1.mkdirs();
            }

            if (!dir2.exists())
            {
                dir2.mkdirs();
            }

            imageViewCloud.setVisibility(View.VISIBLE);
            welcomeTextView.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.VISIBLE);
        }
        else
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

            alert.setMessage(Html.fromHtml("<small>It seems that your external storage is unmounted<br/>Please, insert or mount your external storage</small>"));
            alert.setCancelable(false);

            alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    finish();
                }
            });

            alert.show();
        }
    }

    private void rootOps()
    {
        if (!use.checkSu())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage(Html.fromHtml("<small>RecoverX was unable to obtain root access</small>"));
            alert.setCancelable(false);

            alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                finish();
            }
        });

            alert.show();

            return;
        }
        else
        {
            tosOps();
        }
    }

    private void tosOps()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (!sp.getBoolean("booleanUserAcceptTos", false))
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

            alert.setMessage(Html.fromHtml("<small>In order to works, RecoverX needs an unlocked bootloader for some devices<br/><br/>Moreover, RecoverX is dangerous and could brick your device<br/><br/>Finally, keep in mind that this program is still in beta<br/><br/>I'm not responsible for bricked devices or thermonuclear war !</small>"));
            alert.setCancelable(false);

            alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    Editor edit = sp.edit();
                    edit.putBoolean("booleanUserAcceptTos", true);
                    edit.commit();

                    busyboxOps();
                }
            });

            alert.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    finish();
                }
            });

            alert.show();
        }
        else
        {
            busyboxOps();
        }
    }

	// DownloadFileAsync class //////////
	
		private class DownloadFileAsync extends AsyncTask<String, Void, Void> {
				
			protected String error;
			protected ProgressDialog Dialog;
		        
		    protected void onPreExecute() {
		    	Dialog = new ProgressDialog(MainActivity.this);
		        Dialog.setTitle("Downloading..");
		        Dialog.setCancelable(false);
		        Dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		        Dialog.show();
		    }
		    
			protected Void doInBackground(String... urls) {
		     	int count;
		        	
		        try {
		        	
		        	URL url = new URL(urls[0]);
		        	HttpURLConnection conexion2 = (HttpURLConnection)url.openConnection();
		        	conexion2.setRequestMethod("HEAD");
		        	
			        int lenghtOfFile = conexion2.getContentLength();
			        
			        URLConnection conexion = url.openConnection();
			        conexion.setDoOutput(true);
			        conexion.connect();
		
			        InputStream input = new BufferedInputStream(url.openStream());			        
			        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/recoverx/manifest.xml");
			        
			        byte data[] = new byte[1024];
			    	
			        long total = 0;
		
			        while ((count = input.read(data)) != -1) {
			            total += count;
			            Dialog.setProgress((int)((total*100)/lenghtOfFile));
			            output.write(data, 0, count);
			        }
		
			        output.flush();
			        output.close();
			        input.close();
		        } 
		        catch (Exception e) {
		            error = e.getMessage();
		        }
		            
		        return null;
		    }
		        
		    protected void onPostExecute(Void unused) {
		        Dialog.dismiss();
		        
		        if (error == null) 
		        {		        	
		        	Intent intent = new Intent(MainActivity.this, BrandListActivity.class);
					startActivity(intent);
		        } 
		        else 
		        {
		        	Toast.makeText(MainActivity.this, "Unable to download, server overloaded", Toast.LENGTH_LONG).show();
                }
		    }
		}
}