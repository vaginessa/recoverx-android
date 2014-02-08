package com.ledelete.recoverx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListActivity extends Activity {
	
	protected ArrayList<String> deviceBoard = new ArrayList<String>();
	
	protected ListView listViewBrand = null;
	protected ImageView imageViewCloud = null;
	protected TextView textViewDevice = null;

    private Void debugSystemOut(String log)
    {
        if(BuildConfig.DEBUG)
        {
            System.out.println(log);
        }
        return null;
    }
	
	private OnItemClickListener listenerListViewDevice = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View viewvariable, int position, long id) {
    		
    		String text = listViewBrand.getItemAtPosition(position).toString();
    		
    		debugSystemOut(text);
    		
    		Intent intent = new Intent(DeviceListActivity.this, RecoveryListActivity.class);
    		intent.putExtra("DEVICE", text);    		
    		startActivity(intent);
        }
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        
        listViewBrand = (ListView)findViewById(R.id.listView1);
        imageViewCloud = (ImageView)findViewById(R.id.imageView1);
        textViewDevice = (TextView)findViewById(R.id.textView1);

		listViewBrand.setOnItemClickListener(listenerListViewDevice);
		imageViewCloud.setVisibility(View.INVISIBLE);
		textViewDevice.setVisibility(View.INVISIBLE);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		Intent intent = getIntent();
		
		if (intent != null) {
			new ParseXmlDeviceAsync().execute(intent.getStringExtra("BRAND"));
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
	
	// ParseXmlDeviceAsync class //////////
	
		public class ParseXmlDeviceAsync extends AsyncTask<String, String, String> {

			protected ProgressDialog Dialog;
			
			@Override
		    protected void onPreExecute() {
				Dialog = new ProgressDialog(DeviceListActivity.this);
				Dialog.setMessage("Searching..");
				Dialog.setCancelable(false);
		        Dialog.show();
		    }
			
		    @Override
			protected String doInBackground(String... params) {
		    	
		    	try 
		    	{
		    		String content;
		    		
		        	InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/recoverx/manifest.xml");
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

	                        if(tagName.equalsIgnoreCase("id:0")) 
	                        {
	                        	if (parser.getAttributeValue(null, "brand").equals(params[0]))
	                        	{
	                        		content = parser.getAttributeValue(null, "id");
		                            debugSystemOut("Device = " + content);
		                            deviceBoard.add(content);
	                        	}
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
		    	
				imageViewCloud.setVisibility(View.VISIBLE);
				textViewDevice.setVisibility(View.VISIBLE);
		    	
		    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(DeviceListActivity.this, android.R.layout.simple_list_item_1, deviceBoard);

		    	listViewBrand.setAdapter(adapter);
		    }
		}

}

