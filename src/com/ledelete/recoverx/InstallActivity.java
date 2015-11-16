package com.ledelete.recoverx;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.analytics.tracking.android.EasyTracker;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import static org.xmlpull.v1.XmlPullParser.START_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class InstallActivity extends Activity {
	
	protected String pathInfo = null;
	protected String errorDescription = null;
	protected boolean installSuccess = false;
	
	protected ImageView okImageView = null;
	protected TextView totalTextView = null;
	protected TextView concludingTextView = null;
	protected TextView descriptionTextView = null;
	protected Button helpButton = null;

    Usage use = new Usage();

    private boolean unpackZip(String zippath, String zipname, String destpath)
    {
        InputStream is;
        ZipInputStream zis;

        try
        {
            if (zippath.substring(zippath.length() - 1).equals("/"))
            {
                is = new FileInputStream(zippath + zipname);
            }
            else
            {
                is = new FileInputStream(zippath + "/" + zipname);
            }

            zis = new ZipInputStream(new BufferedInputStream(is));
            byte[] buffer = new byte[1024];

            ZipEntry ze;
            String filename;
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                if (ze.isDirectory())
                {
                    File newfolder;

                    if (destpath.substring(destpath.length() - 1).equals("/"))
                    {
                        newfolder = new File(destpath + filename);
                    }
                    else
                    {
                        newfolder = new File(destpath + "/" + filename);
                    }

                    newfolder.mkdirs();
                }

                FileOutputStream fos;

                if (destpath.substring(destpath.length() - 1).equals("/"))
                {
                    fos = new FileOutputStream(destpath + filename);
                }
                else
                {
                    fos = new FileOutputStream(destpath + "/" + filename);
                }

                while ((count = zis.read(buffer)) != -1)
                {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean emptyCache(File path)
    {
        try
        {
            if (path.isDirectory())
            {
                for (File child : path.listFiles())
                {
                    emptyCache(child);
                }
            }

            if (!path.getName().equals("cache"))
            {
                path.delete();
            }

            return true;
        }
        catch (Exception e)
        {
            use.debugSystemOut(e.toString());
            return false;
        }
    }

    private boolean emptyDownload(File path)
    {
        try
        {
            if (path.isDirectory())
            {
                for (File child : path.listFiles())
                {
                    emptyDownload(child);
                }
            }

            if (!path.getName().equals("download"))
            {
                path.delete();
            }

            return true;
        }
        catch (Exception e)
        {
            use.debugSystemOut(e.toString());
            return false;
        }
    }

	private Void recursiveRecovery(File[] file1){

	    int i = 0;
	    String recoveryPartName[] = {"recovery", "Recovery", "RECOVERY", "SOS", "Sos", "sos", "SS", "USP", "UP"};
	    
	    String filePath;
	    
	    if(file1!=null)
         {
        	 
        	 while(i!=file1.length){
        		 
        		 filePath = file1[i].getAbsolutePath();
        		 if(file1[i].isDirectory())
        		 {
        			 File file[] = file1[i].listFiles();	        			 
        			 recursiveRecovery(file);
        		 }
        		 i++;

                 for (String aRecoveryPartName : recoveryPartName) {
                     File userFile = new File(filePath);
                     String fileName = userFile.getName();

                     if (fileName.equals(aRecoveryPartName)) {
                         use.debugSystemOut(fileName + " partition found to " + filePath);
                         pathInfo = filePath;
                     }
                 }
        	 }
	         
         }
		return null;
	}
	
	private Void recursiveBoot(File[] file1){
	    	
	    int i = 0;
	    String bootPartName[] = {"boot", "LNX", "BOOT", "lnx", "Boot", "Lnx"};
	    
	    String filePath;
	    if(file1!=null)
	    {
	    	while(i!=file1.length){
	       		 
	       		 filePath = file1[i].getAbsolutePath();
	       		 if(file1[i].isDirectory())
	       		 {
	       			 File file[] = file1[i].listFiles();	        			 
	       			 recursiveBoot(file);
	       		 }
	       		 i++;

                for (String aBootPartName : bootPartName) {
                    File userFile = new File(filePath);
                    String fileName = userFile.getName();

                    if (fileName.equals(aBootPartName)) {
                        use.debugSystemOut(fileName + " partition found to " + filePath);
                        pathInfo = filePath;
                    }
                }
	    	 }
	    }
		return null;
	}
	
	private OnClickListener listenerHelpButton = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (installSuccess)
			{
                AlertDialog.Builder alert = new AlertDialog.Builder(InstallActivity.this);

                alert.setMessage(Html.fromHtml("<small>It would be nice to rate my app as a reward for my hard work ;)</small>"));

                alert.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=com.ledelete.recoverx"));
                        startActivity(browserIntent);
                    }
                });

                alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        InstallActivity.this.moveTaskToBack(true);
                    }
                });

                alert.show();
			}
			else
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://ledelete.shost.ca/recoverx/android/web/help.html"));
				startActivity(browserIntent);
			}
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install);
        
        okImageView = (ImageView)findViewById(R.id.imageView1);
        totalTextView = (TextView)findViewById(R.id.textView1);
        concludingTextView = (TextView)findViewById(R.id.textView2);
        descriptionTextView = (TextView)findViewById(R.id.textView3);
        helpButton = (Button)findViewById(R.id.button1);
        
		okImageView.setVisibility(View.INVISIBLE);
		totalTextView.setVisibility(View.INVISIBLE);
		concludingTextView.setVisibility(View.INVISIBLE);
		descriptionTextView.setVisibility(View.INVISIBLE);
		helpButton.setOnClickListener(listenerHelpButton);
		helpButton.setVisibility(View.INVISIBLE);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
        
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(InstallActivity.this);
		
		if (sp.getBoolean("booleanDevMode", false))
		{
    		okImageView.setImageResource(R.drawable.clear);
			okImageView.setVisibility(View.VISIBLE);
			
			totalTextView.setText("Everything done !");
			totalTextView.setVisibility(View.VISIBLE);
			
			concludingTextView.setText("RecoverX has successfully downloaded your recovery");
			concludingTextView.setVisibility(View.VISIBLE);
			
			descriptionTextView.setText("RecoverX has downloaded your recovery package without installing it as the Dev Mode is enabled\n\nThe package is located to /sdcard/recoverx/download");
			descriptionTextView.setVisibility(View.VISIBLE);
		}
		else
		{
			Intent intent = getIntent();
			
			if (intent != null) {
				use.debugSystemOut(intent.getStringExtra("TYPE"));
				new InstallRecoveryAsync().execute(intent.getStringExtra("TYPE"));
			}
		}
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(InstallActivity.this);

        if (sp.getBoolean("booleanAnalytics", true))
        {
            EasyTracker.getInstance(InstallActivity.this).activityStart(InstallActivity.this);
            use.debugSystemOut("analytics start");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(InstallActivity.this);

        if (sp.getBoolean("booleanAnalytics", true))
        {
            EasyTracker.getInstance(InstallActivity.this).activityStop(InstallActivity.this);
            use.debugSystemOut("analytics stop");
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
	
	// InstallRecoveryAsync class //////////
	
	public class InstallRecoveryAsync extends AsyncTask<String, Void, Void> {
		
		protected ProgressDialog Dialog;
		
		@Override
	    protected void onPreExecute() {
			Dialog = new ProgressDialog(InstallActivity.this);
			Dialog.setMessage("Installing recovery..");
			Dialog.setCancelable(false);
	        Dialog.show();
		}
		
	    @Override
		protected Void doInBackground(String... type) {
			try {

				use.debugSystemOut(type[0]);
				
				if (type[0].equals("img"))
				{
					File checker = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download/recoverx.img");
					
					if (checker.exists())
					{
						File path = new File("/dev/block/platform");
						recursiveRecovery(path.listFiles());
						
						if (pathInfo == null)
						{
							use.debugSystemOut("Error, failed to find the recovery partition");
							installSuccess = false;
							errorDescription = "RecoverX failed to find the recovery partition";
						}
						else
						{
							SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(InstallActivity.this);
							
							if (sp.getBoolean("booleanCustomFlash", false) && sp.getString("stringCustomFlash", null) != null)
							{
                                use.suBoard.add(sp.getString("stringCustomFlash", null));
								if (use.execSu())
								{
									use.debugSystemOut("IMG successfully installed");
									installSuccess = true;
								}
								else
								{
									use.debugSystemOut("Can't obtain root access");
									installSuccess = false;
									errorDescription = "RecoverX couldn't obtain root access";
								}
							}
							else
							{
                                use.suBoard.add("dd if=/sdcard/recoverx/download/recoverx.img of=" + pathInfo);
								if (use.execSu())
								{
									use.debugSystemOut("IMG successfully installed");
									installSuccess = true;
								}
								else
								{
									use.debugSystemOut("Can't obtain root access");
									installSuccess = false;
									errorDescription = "RecoverX couldn't obtain root access";
								}
							}					
						}
					}
					else
					{
						use.debugSystemOut("Error, can't find the img recovery package");
						installSuccess = false;
						errorDescription = "RecoverX couldn't find the recovery package";
					}
				}
				else if (type[0].equals("elf"))
				{
					File checker = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download/recoverx.elf");
					
					if (checker.exists())
					{
						File path = new File("/dev/block/platform");
						recursiveBoot(path.listFiles());
						
						if (pathInfo == null)
						{
							use.debugSystemOut("Error, failed to find the boot partition");
							installSuccess = false;
							errorDescription = "RecoverX failed to find the boot partition";
						}
						else
						{
							SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(InstallActivity.this);
							
							if (sp.getBoolean("booleanCustomFlash", false) && sp.getString("stringCustomFlash", null) != null)
							{
                                use.suBoard.add(sp.getString("stringCustomFlash", null));
								if (use.execSu())
								{
									use.debugSystemOut("ELF successfully installed");
									installSuccess = true;
								}
								else
								{
									use.debugSystemOut("Can't obtain root access");
									installSuccess = false;
									errorDescription = "RecoverX couldn't obtain root access";
								}
							}
							else
							{
                                use.suBoard.add("dd if=/sdcard/recoverx/download/recoverx.elf of=" + pathInfo);
								if (use.execSu())
								{
									use.debugSystemOut("ELF successfully installed");
									installSuccess = true;
								}
								else
								{
									use.debugSystemOut("Can't obtain root access");
									installSuccess = false;
									errorDescription = "RecoverX couldn't obtain root access";
								}
							}
						}
					}
					else
					{
						use.debugSystemOut("Error, can't find the elf recovery package");
						installSuccess = false;
						errorDescription = "RecoverX couldn't find the recovery package";
					}
				}
				else if (type[0].equals("zip"))
				{
                    File checker = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download/recoverx.zip");

                    if (checker.exists())
                    {
                        if (unpackZip(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download", "recoverx.zip", Environment.getExternalStorageDirectory().getPath() + "/recoverx/cache"))
                        {
                            try {
                                String content = null;
                                ArrayList<String> resPath = new ArrayList<String>();

                                InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/recoverx/cache/install.xml");
                                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                                parser.setInput(is, null);
                                int eventType = parser.getEventType();

                                while(eventType != XmlPullParser.END_DOCUMENT) {

                                    if (eventType == XmlPullParser.START_DOCUMENT)
                                    {
                                        // At the beginning of the document
                                    }
                                    else if (eventType == XmlPullParser.START_TAG)
                                    {
                                        // Get tag name
                                        String tagName = parser.getName();

                                        if(tagName.equalsIgnoreCase("cmd"))
                                        {
                                            content = parser.nextText();
                                            use.suBoard.add(content);
                                        }
                                        else if (tagName.equalsIgnoreCase("res"))
                                        {
                                            content = parser.nextText();
                                            resPath.add(content);
                                        }
                                    }
                                    // Jump to next event
                                    eventType = parser.next();
                                }

                                use.execSu();

                                for (int i = 0; i < resPath.size(); i++)
                                {
                                    if(!use.checkExist(resPath.get(i)))
                                    {
                                        use.debugSystemOut("Error, some files were not installed");
                                        installSuccess = false;
                                        errorDescription = "RecoverX was unable to install some files";

                                        File cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/cache");
                                        emptyCache(cacheDir);

                                        File downloadDir = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download");
                                        emptyDownload(downloadDir);

                                        return null;
                                    }
                                }
                            }
                            // Exceptions
                            catch (XmlPullParserException e)
                            {
                                use.debugSystemOut(e.toString());
                            }
                            catch (IOException e)
                            {
                                use.debugSystemOut(e.toString());
                            }
                            catch (NullPointerException e)
                            {
                                // Error in the xml file
                                use.debugSystemOut(e.toString());
                            }

                            File toDel = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/cache");
                            emptyCache(toDel);

                            use.debugSystemOut("ZIP successfully installed");
                            installSuccess = true;
                        }
                        else
                        {
                            use.debugSystemOut("Error, unable to unzip recovery package");
                            installSuccess = false;
                            errorDescription = "RecoverX was unable to unzip the recovery package";
                        }
                    }
                    else
                    {
                        use.debugSystemOut("Error, can't find the zip recovery package");
                        installSuccess = false;
                        errorDescription = "RecoverX couldn't find the recovery package";
                    }

                    File downloadDir = new File(Environment.getExternalStorageDirectory().getPath() + "/recoverx/download");
                    emptyDownload(downloadDir);
				}
				else
				{
					use.debugSystemOut("Error, can't find the type of the recovery");
					installSuccess = false;
					errorDescription = "RecoverX couldn't find the type of the recovery";
				}
				
			}
            catch (Exception e)
            {
				e.printStackTrace();
			}
			return null;
	    }
	    
	    @Override
	    protected void onPostExecute(Void unused) {
	    	Dialog.dismiss();
	    	
	    	if (installSuccess)
	    	{
	    		okImageView.setImageResource(R.drawable.clear);

				totalTextView.setText("Everything done !");
				concludingTextView.setText("RecoverX has successfully installed your recovery");
				descriptionTextView.setText("To enter in recovery mode, reboot and\n\n- Press back button on boot\n- Or press volume buttons on boot\n- Or tap the screen on boot");
				helpButton.setText("Okay");
	    	}
	    	else
	    	{
	    		okImageView.setImageResource(R.drawable.warning);

				totalTextView.setText("Something failed !");
				concludingTextView.setText("RecoverX has encountered a problem");
				descriptionTextView.setText(errorDescription);
				helpButton.setText("I need some help !");
	    	}

            okImageView.setVisibility(View.VISIBLE);
            totalTextView.setVisibility(View.VISIBLE);
            concludingTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            helpButton.setVisibility(View.VISIBLE);
	    }
	}	
}