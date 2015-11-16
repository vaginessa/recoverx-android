package com.ledelete.recoverx;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class FinalIntentActivity extends Activity {
	
	protected String position = null;	
	protected String recovery = null;
	protected String device = null;
	protected String link = null;
	protected String type = null;
	protected String version = null;
	protected String signature = null;
	protected String beta = null;
    protected String msg = null;
	
	protected ImageView imageViewInstall = null;
	protected TextView textViewInstallation = null;
	protected TextView textViewDevice = null;
	protected TextView textViewRecovery = null;
	protected Button beginDownloadButton = null;

    private Void debugSystemOut(String log)
    {
        if(BuildConfig.DEBUG)
        {
            System.out.println(log);
        }
        return null;
    }
	
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
	
	private OnClickListener listenerBeginDownloadButton = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FinalIntentActivity.this);
			
			if (sp.getBoolean("booleanDevMode", false))
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(FinalIntentActivity.this);

				alert.setMessage(Html.fromHtml("<small>Dev Mode is currently enabled, RecoverX will only download your recovery without installing it</small>"));

				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						if (haveInternet())
						{
							new DownloadFileAsync().execute(link);
						}
						else
						{
							Toast.makeText(FinalIntentActivity.this, "You are disconnected from Internet", Toast.LENGTH_LONG).show();
						}
					}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
                        // Nothing
                    }
				});

				alert.show();
			}
			else
			{
				if (haveInternet())
				{
					new DownloadFileAsync().execute(link);
				}
				else
				{
					Toast.makeText(FinalIntentActivity.this, "You're disconnected from Internet", Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_intent);
        
        imageViewInstall = (ImageView)findViewById(R.id.imageView1);
        textViewInstallation = (TextView)findViewById(R.id.textView1);
        textViewDevice = (TextView)findViewById(R.id.textView2);
        textViewRecovery = (TextView)findViewById(R.id.textView3);
        beginDownloadButton = (Button)findViewById(R.id.button1);
        
		imageViewInstall.setVisibility(View.INVISIBLE);
    	textViewInstallation.setVisibility(View.INVISIBLE);
    	textViewDevice.setVisibility(View.INVISIBLE);
    	textViewRecovery.setVisibility(View.INVISIBLE);
        beginDownloadButton.setOnClickListener(listenerBeginDownloadButton);
        beginDownloadButton.setVisibility(View.INVISIBLE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
        	ActionBar actionBar = getActionBar();
        	actionBar.setDisplayHomeAsUpEnabled(true);
		}
        
        Intent intent = getIntent();
        
        if (intent != null) 
        {
        	position = intent.getStringExtra("POSITION");
            recovery = intent.getStringExtra("RECOVERY");
            device = intent.getStringExtra("DEVICE");
            
			new ParseXmlDownloadAsync().execute(intent.getStringExtra("DEVICE"));
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


	
	// ParseXmlDownloadAsync class //////////
	
	public class ParseXmlDownloadAsync extends AsyncTask<String, String, String> {
		
		protected ProgressDialog Dialog;
		
		@Override
	    protected void onPreExecute() {
			Dialog = new ProgressDialog(FinalIntentActivity.this);
			Dialog.setMessage("Searching..");
			Dialog.setCancelable(false);
	        Dialog.show();
	    }
		
	    @Override
		protected String doInBackground(String... params) {
	    	try {
	        	InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/recoverx/manifest.xml");
	            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
	            parser.setInput(is, null);
	            int eventType = parser.getEventType();
	            
	            while(eventType != XmlPullParser.END_DOCUMENT) {
	            	
	            	if (eventType == START_DOCUMENT)
	            	{
	            		// At the beginning of the document
	            	}
	            	else if (eventType == START_TAG)
                    {
                        // Get tag name
                        String tagName = parser.getName();

                        if (tagName.equalsIgnoreCase("id:" + position)) {
                            version = parser.getAttributeValue(null, "version");
                            signature = parser.getAttributeValue(null, "signature");
                            beta = parser.getAttributeValue(null, "beta");

                            if (parser.getAttributeValue(null, "id").equals(params[0])) {
                                link = parser.nextText();
                                type = link.substring(link.length() - 3);

                                debugSystemOut("Link = " + link);
                                debugSystemOut("Type = " + type);
                                debugSystemOut("Version = " + version);
                                debugSystemOut("Signature = " + signature);
                                debugSystemOut("Beta = " + beta);

                                return null;
                            }
                        }
                        else if (tagName.equalsIgnoreCase("msg"))
                        {
                            msg = parser.nextText();
                        }
                    }
	                // Jump to next event
	                eventType = parser.next();
	            }
	        }
	        // Exceptions
	        catch (XmlPullParserException e) 
	        {
	        	debugSystemOut(e.toString());
	        } 
	        catch (IOException e) 
	        {
	        	debugSystemOut(e.toString());
	        }
	    	catch (NullPointerException e)
	    	{
	    		// Error in the xml file
	    		debugSystemOut(e.toString());
	    	}
			return null;
	    }
	    
	    @Override
	    protected void onPostExecute(String unused) {
	    	Dialog.dismiss();

            if (!msg.equals("null"))
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(FinalIntentActivity.this);

                alert.setMessage(Html.fromHtml("<small>" + msg + "</small>"));
                alert.setCancelable(false);

                alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    }
                });

                alert.show();
            }

            textViewDevice.setText(recovery + " for " + device);
	        
	        if (signature.equals("official"))
	        {
	        	if (version.equals("null") && beta.equals("true"))
		        {
		        	textViewRecovery.setText("Official recovery (beta)");
		        }
		        else if (version.equals("null") && beta.equals("false"))
		        {
		        	textViewRecovery.setText("Official recovery");
		        }
		        else if (!version.equals("null") && beta.equals("true"))
		        {
		        	textViewRecovery.setText(version + " - Official recovery (beta)");
		        }
		        else if (!version.equals("null") && beta.equals("false"))
		        {
		        	textViewRecovery.setText(version + " - Official recovery");
		        }
		        else
		        {
                    // Error !!!
		        	return;
		        }
	        }
	        else if (signature.equals("custom"))
	        {
	        	if (version.equals("null") && beta.equals("true"))
		        {
		        	textViewRecovery.setText("Custom recovery (beta)");
		        }
		        else if (version.equals("null") && beta.equals("false"))
		        {
		        	textViewRecovery.setText("Custom recovery");
		        }
		        else if (!version.equals("null") && beta.equals("true"))
		        {
		        	textViewRecovery.setText(version + " - Custom recovery (beta)");
		        }
		        else if (!version.equals("null") && beta.equals("false"))
		        {
		        	textViewRecovery.setText(version + " - Custom recovery");
		        }
		        else
		        {
                    // Error !!!
		        	return;
		        }
	        }
	        else
	        {
                // Error !!!
	        	return;
	        }


            imageViewInstall.setVisibility(View.VISIBLE);
            textViewDevice.setVisibility(View.VISIBLE);
            textViewRecovery.setVisibility(View.VISIBLE);
            textViewInstallation.setVisibility(View.VISIBLE);
            beginDownloadButton.setVisibility(View.VISIBLE);
	    }
	}
	
	
	// DownloadFileAsync class //////////
	
	private class DownloadFileAsync extends AsyncTask<String, Void, Void> {
		
		protected String error;
		protected ProgressDialog Dialog;
	        
	    protected void onPreExecute() {
	    	Dialog = new ProgressDialog(FinalIntentActivity.this);
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
		        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download/recoverx." + type);
		        
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
	        	Intent intent = new Intent(FinalIntentActivity.this, InstallActivity.class);
		    	intent.putExtra("TYPE", type);
		    	startActivity(intent);
	        } 
	        else 
	        {
	        	Toast.makeText(FinalIntentActivity.this, "Unable to download, server overloaded", Toast.LENGTH_LONG).show();
	        }
	    }
	}
		
				
}
