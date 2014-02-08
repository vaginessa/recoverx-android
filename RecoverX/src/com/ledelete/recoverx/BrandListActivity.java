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

public class BrandListActivity extends Activity {
	
	ArrayList<String> brandBoard = new ArrayList<String>();
	
	protected ListView listViewBrand = null;
	protected ImageView imageViewCloud = null;
	protected TextView textViewBrand = null;

    private Void debugSystemOut(String log)
    {
        if(BuildConfig.DEBUG)
        {
            System.out.println(log);
        }
        return null;
    }
	
	private OnItemClickListener listenerListViewBrand = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View viewvariable, int position, long id) {
    		
    		String text = listViewBrand.getItemAtPosition(position).toString();
    		
    		debugSystemOut(text);
    		
    		Intent intent = new Intent(BrandListActivity.this, DeviceListActivity.class);
    		intent.putExtra("BRAND", text);
    		startActivity(intent);
    		
    		//new ParseXmlDeviceAsync().execute(text);
        }
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_list);
        
        listViewBrand = (ListView)findViewById(R.id.listView1);
        imageViewCloud = (ImageView)findViewById(R.id.imageView1);
        textViewBrand = (TextView)findViewById(R.id.textView1);
        
		listViewBrand.setOnItemClickListener(listenerListViewBrand);
		imageViewCloud.setVisibility(View.INVISIBLE);	
		textViewBrand.setVisibility(View.INVISIBLE);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		new ParseXmlBrandAsync().execute();
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
	
	// ParseXmlBrandAsync class //////////
	
		public class ParseXmlBrandAsync extends AsyncTask<Void, Void, Void> {

			protected ProgressDialog Dialog;
			
			@Override
		    protected void onPreExecute() {
				Dialog = new ProgressDialog(BrandListActivity.this);
				Dialog.setMessage("Searching..");
				Dialog.setCancelable(false);
		        Dialog.show();
		    }
			
		    @Override
			protected Void doInBackground(Void... params) {
		        try {
		        	String content = null;
		        	
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
	                        	if (!parser.getAttributeValue(null, "brand").equals(content))
	                        	{
	                        		content = parser.getAttributeValue(null, "brand");
	                        		debugSystemOut("Brand = " + content);
		                            brandBoard.add(content);
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
		    protected void onPostExecute(Void unused) {
		    	Dialog.dismiss();
		    	
				imageViewCloud.setVisibility(View.VISIBLE);
				textViewBrand.setVisibility(View.VISIBLE);
		    	
		    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(BrandListActivity.this, android.R.layout.simple_list_item_1, brandBoard);
		    	
		    	listViewBrand.setAdapter(adapter);
		    	
		    }
		}


}
