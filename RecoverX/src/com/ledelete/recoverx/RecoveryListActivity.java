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

public class RecoveryListActivity extends Activity {
	
	protected ArrayList<String> recoveryBoard = new ArrayList<String>();
	
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
	
	private OnItemClickListener listenerListViewRecovery = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View viewvariable, int position, long id) {
    		ListView listViewBrand = (ListView)findViewById(R.id.listView1);
    		String text = listViewBrand.getItemAtPosition(position).toString();
    		
    		debugSystemOut(text);
    		
    		Intent intent = new Intent(RecoveryListActivity.this, FinalIntentActivity.class);
    		Intent intent2 = getIntent();
    		
    		intent.putExtra("POSITION", Integer.toString(position));
    		intent.putExtra("RECOVERY", text);
    		intent.putExtra("DEVICE", intent2.getStringExtra("DEVICE"));
    		startActivity(intent);
        }
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recovery_list);
        
        listViewBrand = (ListView)findViewById(R.id.listView1);
        imageViewCloud = (ImageView)findViewById(R.id.imageView1);
        textViewDevice = (TextView)findViewById(R.id.textView1);
        
		listViewBrand.setOnItemClickListener(listenerListViewRecovery);
		imageViewCloud.setVisibility(View.INVISIBLE);
		textViewDevice.setVisibility(View.INVISIBLE);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		Intent intent = getIntent();
		
		if (intent != null) {
			new ParseXmlRecoveryAsync().execute(intent.getStringExtra("DEVICE"));
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

	
	// ParseXmlRecoveryAsync class //////////
	
			public class ParseXmlRecoveryAsync extends AsyncTask<String, String, String> {
				
				protected ProgressDialog Dialog;
				
				@Override
			    protected void onPreExecute() {
					Dialog = new ProgressDialog(RecoveryListActivity.this);
					Dialog.setMessage("Searching..");
					Dialog.setCancelable(false);
			        Dialog.show();
			    }
				
			    @Override
				protected String doInBackground(String... params) {
			    	try {
			    		String recovery;
			    		
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
		                        	if (parser.getAttributeValue(null, "id").equals(params[0]))
		                        	{
		                        		if (parser.getAttributeValue(null, "recovery") != null)
		                        		{
		                        			recovery = parser.getAttributeValue(null, "recovery");
		                        			debugSystemOut("Recovery = " + recovery);
		                        			recoveryBoard.add(recovery);
		                        		}
		                        	}
		                        }
		                        else if(tagName.equalsIgnoreCase("id:1")) 
		                        {
		                        	if (parser.getAttributeValue(null, "id").equals(params[0]))
		                        	{
		                        		if (parser.getAttributeValue(null, "recovery") != null)
		                        		{
		                        			recovery = parser.getAttributeValue(null, "recovery");
		                        			debugSystemOut("Recovery = " + recovery);
		                        			recoveryBoard.add(recovery);
		                        		}
		                        	}
		                        }
		                        else if(tagName.equalsIgnoreCase("id:2")) 
		                        {
		                        	if (parser.getAttributeValue(null, "id").equals(params[0]))
		                        	{
		                        		if (parser.getAttributeValue(null, "recovery") != null)
		                        		{
		                        			recovery = parser.getAttributeValue(null, "recovery");
		                        			debugSystemOut("Recovery = " + recovery);
		                        			recoveryBoard.add(recovery);
		                        		}
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
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecoveryListActivity.this, android.R.layout.simple_list_item_1, recoveryBoard);

			    	listViewBrand.setAdapter(adapter);
			    }
			}
}
