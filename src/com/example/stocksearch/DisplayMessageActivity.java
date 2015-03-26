package com.example.stocksearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayMessageActivity extends ActionBarActivity {
	String[] titleArr, linkArr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE1);
	    String link = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
	    //System.out.println(number);
	    //LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
	    ListView listView = (ListView) findViewById(R.id.list);
	    titleArr = message.split("\n");
	    linkArr = link.split("\n");
	    listView.setAdapter(new ArrayAdapter<String>(this, R.layout.dropdown, titleArr));
	    setListViewHeightBasedOnChildren(listView);
	    
	    listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position,
        	long id) {
        		int index = (int) id;
        		createDialog(linkArr[index]);
        	}
	    });
	    
	    //String html = "<html><body link='black'>";
	    //for (int i=0;i<titleArr.length;i++){
	    	//html += "<div style='border-bottom:solid 1px grey'><a href='" + linkArr[i] + "' style='text-decoration:none'>" + titleArr[i] + "</a></div>";
	    	/*TextView textView = new TextView(this);
	    	textView.setTextSize(15);
	    	textView.setText(titleArr[i]);
	    	layout.addView(textView);*/
	    //}
	    //html += "</body></html>";
	    //listView.loadData(html, "text/html", "utf-8");

	    // Create the text view
	    //TextView textView = (TextView) findViewById(R.id.newsresult);
	    //textView.setTextSize(15);
	    //textView.setText(message);
	    Toast.makeText(getBaseContext(), "Showing " + titleArr.length + " headlines", Toast.LENGTH_LONG).show();

	    // Set the text view as the activity layout
	    //setContentView(textView);  
	}
	
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {   
            return;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
        	View listItem = listAdapter.getView(i, null, listView);
        	listItem.measure(0, 0);
        	totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
	
    public void createDialog(String link){
    	final String url;
    	url = link;
    	String[] listItem = {"View","Cancel"};
		new AlertDialog.Builder(this).setTitle("View News").setItems(listItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0){
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else {
					dialog.dismiss();
				}
			}
		}).show();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_display_message,
					container, false);
			return rootView;
		}
	}

}
