package com.example.stocksearch;

//import static android.support.v4.app.FragmentActivity.TAG;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE1 = "com.example.stocksearch.MESSAGE";
	public final static String EXTRA_MESSAGE2 = "com.example.stocksearch.LINK";
	public String url = "http://cs-server.usc.edu:36938/examples/servlet/HelloWorldExample?company=";
	public String autoURL;
	String chartURL, name, companyStr, lastPrice, changeNum, percent, symbol;
	private Button facebook;
	TextView company, color, price, key, value, stocknews;
	AutoCompleteTextView autoText;
	ImageView imView;
	String newsStr = "", linkStr = "", autoSymbol = ""; 
	ArrayList<String> autoStr = new ArrayList<String>();
	int newsNum;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	/** Called when the user clicks the Send button */
	public void displayNews(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		//EditText editText = (EditText) findViewById(R.id.edit_message);
		//String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE1, newsStr);
		intent.putExtra(EXTRA_MESSAGE2, linkStr);
		startActivity(intent);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        
        autoText = (AutoCompleteTextView)findViewById(R.id.edit_symbol);
        
        // call AsynTask to perform network operation on separate thread
        //String autoSymbol = autoText.getText().toString();
        /*String autoURL = "http://autoc.finance.yahoo.com/autoc?query=" + autoSymbol + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
        new AutoAsyncTask().execute(autoURL);*/
        
        /*ArrayList<String> result = new ArrayList<String>();
        result.add("1111111");
        result.add("1222222");
        autoText.setAdapter(new ArrayAdapter<String>(
              this,
              android.R.layout.simple_dropdown_item_1line,
              result)
        );*/
        autoText.addTextChangedListener(new TextWatcher(){
        	@Override  
            public void afterTextChanged(Editable s) {  
                //Log.d(TAG, "afterTextChanged");  
            }  
  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
                //Log.d(TAG, "beforeTextChanged:" + s + "-" + start + "-" + count + "-" + after);
            }
            
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
                //Log.d(TAG, "onTextChanged:" + s + "-" + "-" + start + "-" + before + "-" + count);  
                //System.out.println(s);
                autoSymbol = s.toString();
                if (autoSymbol.length() < 5){
                	autoURL = "http://autoc.finance.yahoo.com/autoc?query=";
                	autoURL = autoURL + autoSymbol + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
                	//System.out.println(autoURL);
                	outputAuto(autoSymbol);
                }
            }
        });
        
        
        Button button = (Button) findViewById(R.id.button);
        
        button.setOnClickListener(new View.OnClickListener() {             
            public void onClick(View v) {                 
            	// Perform action on click 
            	AutoCompleteTextView autoText = (AutoCompleteTextView)findViewById(R.id.edit_symbol);
            	String symbol = autoText.getText().toString();
            	System.out.println(symbol);
            	if (symbol.equals("")){
            		company.setText("");
					color.setText("");
					color.setCompoundDrawables(null, null, null, null);
					key.setText("");
					value.setText("");
					price.setText("");
					imView.setVisibility(View.GONE);
					Button newsBtn = (Button) findViewById(R.id.newsbtn);
					newsBtn.setVisibility(View.GONE);
					Button facebook = (Button) findViewById(R.id.facebook);
					facebook.setVisibility(View.GONE);
            		Toast.makeText(getBaseContext(), "Please enter company name/symbol", Toast.LENGTH_LONG).show();
            	} else {
            		url = "http://cs-server.usc.edu:36938/examples/servlet/HelloWorldExample?company=";
            		url = url + symbol;
            		System.out.println(url);
            		new HttpAsyncTask().execute(url);
            	}
            }         });
        
        autoText.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position,
        	long id) {
        		String comInfo = autoText.getText().toString();
        		String[] comSymbols = comInfo.split(",");
        		String comSymbol = comSymbols[0];
        		System.out.println("COMSYMBOL  " + comSymbol);
        		autoText.setText(comSymbol);
        		url = "http://cs-server.usc.edu:36938/examples/servlet/HelloWorldExample?company=";
        		url = url + comSymbol;
        		new HttpAsyncTask().execute(url);
        	}
        });
        
        facebook = (Button) findViewById (R.id.facebook);
        


        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
        	if (savedInstanceState != null) {
        		session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
            	session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        facebook.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) { onClickLogin(); }
                });
        /*facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MYLoginActivity.class);
                startActivity(intent);
            }
        });*/
    }
    
    public void outputAuto(String autoSymbol){
        new AutoAsyncTask().execute(autoURL);
        System.out.println("output" + autoURL);
        autoText.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.dropdown,
                autoStr)
          );
    }
    

    public static String GET(String url){
    	//System.out.println("GET" + url);
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        //System.out.println(result);
        return result;
 
    }
    
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String str) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
        	try {
				JSONObject json = new JSONObject(str);
				JSONObject result = json.getJSONObject("result");
				name = result.getString("Name");
				String compare = "Stock Information Not Found!";
				if (!name.equals(compare)){
				symbol = result.getString("Symbol");
				//System.out.println(symbol);
				JSONObject quote = result.getJSONObject("quote");
				lastPrice = quote.getString("LastTradePriceOnly");
				changeNum = quote.getString("Change");
				percent = quote.getString("ChangeInPercent");
				String changeType = quote.getString("ChangeType");				
				
				companyStr = name + " (" + symbol + ")";
				company = (TextView) findViewById(R.id.name);
				company.setText(companyStr);
				price = (TextView) findViewById(R.id.lastPrice);
				price.setText(lastPrice);
				//ImageView image = (ImageView) findViewById(R.id.image);
				String colorStr = changeNum + " (" + percent + ")";
				color = (TextView) findViewById(R.id.color);
				
				if (changeType.equals("+")){
					Drawable img_up;
					Resources res = getResources();
					img_up = res.getDrawable(R.drawable.up);
					//image.setImageResource(R.drawable.up);
					img_up.setBounds(0, 0, img_up.getMinimumWidth(), img_up.getMinimumHeight());
					color.setCompoundDrawables(img_up, null, null, null);
					color.setText(colorStr);
					color.setTextColor(android.graphics.Color.GREEN);
				}
				else{
					Drawable img_down;
					Resources res = getResources();
					img_down = res.getDrawable(R.drawable.down);
					img_down.setBounds(0, 0, img_down.getMinimumWidth(), img_down.getMinimumHeight());
					color.setCompoundDrawables(img_down, null, null, null);
					//image.setImageResource(R.drawable.down);
					color.setText(colorStr);
					color.setTextColor(android.graphics.Color.RED);
				}
				
				String keyStr = "Prev Close: \nOpen: \nBid: \nAsk: \n1st Yr Target: \nDay Range: \n52wk Range: \nVolume: \nAvg Vol (3m): \nMarket Cap: ";
				key = (TextView) findViewById(R.id.key);
				key.setText(keyStr);
				
				//String param[] = {"PreviousClose","DaysHigh","Open","YearLow","Bid","Volume","Ask","AverageDailyVolume","OneyrTargetPrice","MarketCapitalization"};
				String valueStr = quote.getString("PreviousClose") + "\n" + quote.getString("Open") + "\n" + quote.getString("Bid") + "\n" + quote.getString("Ask") + "\n";
				valueStr += quote.getString("OneYearTargetPrice") + "\n" + quote.getString("DaysLow") + "-" + quote.getString("DaysLow") + "\n" + quote.getString("YearLow");
				valueStr += "-" + quote.getString("YearHigh") + "\n" + quote.getString("Volumn") + "\n" + quote.getString("AverageDailyVolumn") + "\n" + quote.getString("MarketCapitalization");
				value = (TextView) findViewById(R.id.value);
				value.setText(valueStr);
				
				chartURL = result.getString("StockChartImageURL");
				System.out.println(chartURL);
				imView = (ImageView) findViewById(R.id.stockchart);   
				imView.setVisibility(View.VISIBLE);
				imView.setTag(chartURL);
				new DownloadImagesTask().execute(imView);
				
				//get the news from servlet
				JSONObject news = result.getJSONObject("News");
				JSONArray items = news.getJSONArray("Item");
				JSONObject item = items.getJSONObject(0);
				if (!item.getString("Title").equals("Financial Company News is not available")){
					Button newsBtn = (Button) findViewById(R.id.newsbtn);
					newsBtn.setText("News Headlines");
					//newsBtn.setWidth(100);
					newsBtn.setVisibility(View.VISIBLE);
					facebook = (Button) findViewById(R.id.facebook);
					facebook.setText("Facebook");
					facebook.setVisibility(View.VISIBLE);
					newsNum = items.length();
					
					for (int i=0;i<items.length();i++){
						item = items.getJSONObject(i);
						String title = item.getString("Title");
						String link = item.getString("Link");
						newsStr += title + "\n";
						linkStr += link + "\n";
						//stocknews = (TextView) findViewById(R.id.news);
						//stocknews.setText(title);
					}
					//System.out.println(newsStr);
					//System.out.println(linkStr);
				} else {
					Button newsBtn = (Button) findViewById(R.id.newsbtn);
					newsBtn.setVisibility(View.GONE);
					facebook = (Button) findViewById(R.id.facebook);
					facebook.setVisibility(View.GONE);
				}
        		}else{
        			companyStr = "Stock Information Not Found!";
					company = (TextView) findViewById(R.id.name);
					company.setText(companyStr);
					color.setText("");
					color.setCompoundDrawables(null, null, null, null);
					key.setText("");
					value.setText("");
					price.setText("");
					imView.setVisibility(View.GONE);
					Button newsBtn = (Button) findViewById(R.id.newsbtn);
					newsBtn.setVisibility(View.GONE);
					Button facebook = (Button) findViewById(R.id.facebook);
					facebook.setVisibility(View.GONE);
        		}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
       }
        
        public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        	ImageView imageView = null;

        	@Override
        	protected Bitmap doInBackground(ImageView... imageViews) {
        	    this.imageView = imageViews[0];
        	    return download_Image((String)imageView.getTag());
        	}

        	@Override
        	protected void onPostExecute(Bitmap result) {
        	    imageView.setImageBitmap(result);
        	}

        	private Bitmap download_Image(String url) {
        		Bitmap bm = null;
        	    try {
        	        URL aURL = new URL(url);
        	        URLConnection conn = aURL.openConnection();
        	        conn.connect();
        	        InputStream is = conn.getInputStream();
        	        BufferedInputStream bis = new BufferedInputStream(is);
        	        bm = BitmapFactory.decodeStream(bis);
        	        bis.close();
        	        is.close();
        	    } catch (IOException e) {
        	        Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
        	    } 
        	    return bm;
        	}
        
        }
    }
    
    private class AutoAsyncTask extends AsyncTask<String, Void, String> {
    	String result = "";
        @Override
        protected String doInBackground(String... urls) {
        	System.out.println("async" + urls[0]);
            //return GET(urls[0]);
        	String line = "";
		    
			HttpURLConnection autoConn = null;
			try {
				autoConn = (HttpURLConnection) (new URL(urls[0])).openConnection();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    try {
				autoConn.connect();
				int response = autoConn.getResponseCode();
				System.out.println(response);
				InputStreamReader inputStr = new InputStreamReader(autoConn.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStr);
		        while((line = bufferedReader.readLine()) != null)
		            result += line;
		 
		        inputStr.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String str) {
        	try {
        		String subStr = result.substring(39, result.length()-1);
        		//System.out.println(subStr);
        		JSONObject json = new JSONObject(subStr);
				JSONObject resultset = json.getJSONObject("ResultSet");
				JSONArray jsonresult = resultset.getJSONArray("Result");
				//System.out.println(jsonresult.length());
				if (jsonresult.length() > 0){
					for (int i=0;i<jsonresult.length();i++){
						JSONObject jsonObj = jsonresult.getJSONObject(i);
						//System.out.println(jsonObj.getString("symbol"));
						autoStr.add(jsonObj.getString("symbol") + ", " + jsonObj.getString("name") + "(" + jsonObj.getString("exch") + ")");
						//System.out.println(autoStr);
					}
				} /*else {
					autoStr[0] = " ";
				}*/
        	}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

	private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }
	
	private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //updateView();
        	String description = "Last Trade Price: " + lastPrice + ", \nChange: " + changeNum + "(" + percent + ")";
        	String link = "http://finance.yahoo.com/q?s=" + symbol;
        	publishFeedDialog(name, "Stock Information of " + companyStr, description, link, chartURL);
        }
    }
	
	private void publishFeedDialog(String name, String caption, String des, String link, String pic) {
	    Bundle params = new Bundle();
	    params.putString("name", name);
	    params.putString("caption", caption);
	    params.putString("description", des);
	    params.putString("link", link);
	    params.putString("picture", pic);

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(this,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(MainActivity.this,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(MainActivity.this.getApplicationContext(), 
	                            "Posted Successfully", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(MainActivity.this.getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(MainActivity.this.getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
