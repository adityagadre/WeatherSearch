package com.weathersearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WeatherSearch extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather_search, menu);
        return true;
    }
    
    public void btnSearch_Click(View view)
    {
    	StringBuilder s=new StringBuilder("");
    	String x,json;
    	EditText e=(EditText)findViewById(R.id.editText1);
    	e.setText("Hello2");
    	HttpClient cli=new DefaultHttpClient();
    	HttpGet get=new HttpGet("http://cs-server.usc.edu:12375/examples/servlet/WeatherServlet?location=90007&type=zip&tempUnit=f");
    	//HttpGet get=new HttpGet("http://www.google.com");
    	try {
			HttpResponse res=cli.execute(get);
			StatusLine sl=res.getStatusLine();
			e.setText("1");
			if(sl.getStatusCode()==200)
			{
				e.setText("2");
				HttpEntity ent=res.getEntity();
				InputStream is=ent.getContent();
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				while((x=br.readLine())!=null)
				{
					s.append(x);
				}
				json=s.toString();
				e.setText("3");
				//e.setText("4");
				//e.setText(json);
				
				JSONObject jo= new JSONObject(json);
				JSONObject weather=jo.getJSONObject("weather");
				e.setText("4");
				e.setText(weather.getString("img"));
				
				TableLayout tbl=(TableLayout) findViewById(R.id.tblWeather);
				
				int i;
				
				for (i=0;i<5;i++){
					TableRow row=new TableRow(this);
					TextView tv;
					
					tv=new TextView(this);
					tv.setText("a"+i);
					row.addView(tv);
					
					tv=new TextView(this);
					tv.setText("b"+i);
					row.addView(tv);
					
					tv=new TextView(this);
					tv.setText("c"+i);
					row.addView(tv);
					
					tv=new TextView(this);
					tv.setText("d"+i);
					row.addView(tv);
					
					tv=new TextView(this);
					tv.setText("e"+i);
					row.addView(tv);
					
					
					
					//tv.setBackgroundColor(Color.WHITE);
					
					
					tbl.addView(row);
				}
				tbl.setBackgroundColor(Color.WHITE);
				
			}
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    }
    
}
