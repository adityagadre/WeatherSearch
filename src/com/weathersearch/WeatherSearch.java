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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.android.Facebook;

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

	public void cmdWeather_click(View view) {
		EditText e = (EditText) findViewById(R.id.editText1);
		e.setText("1FB Logg ed in");
		Session s;
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				// TODO Auto-generated method stub
				EditText e = (EditText) findViewById(R.id.editText1);
				e.setText("FB Logged in");
			}
		});                                                                                            
	}
//android.support.v4.content.LocalBroadcastManager
	public void cmdForecast_click(View view) {
		EditText e = (EditText) findViewById(R.id.editText1);
		e.setText("Hello2");
	}

	public void btnSearch_Click(View view) {
		StringBuilder s = new StringBuilder("");
		String x, json;
		TextView tv;
		
		
		tv=(TextView)findViewById(R.id.cmdWeather);
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cmdWeather_click(v);
			}
		});
		
		
		
		EditText e = (EditText) findViewById(R.id.editText1);
		e.setText("Hello2");
		HttpClient cli = new DefaultHttpClient();
		HttpGet get = new HttpGet(
				"http://cs-server.usc.edu:12375/examples/servlet/WeatherServlet?location=90007&type=zip&tempUnit=f");
		// HttpGet get=new HttpGet("http://www.google.com");
		try {
			HttpResponse res = cli.execute(get);
			StatusLine sl = res.getStatusLine();
			e.setText("1");
			if (sl.getStatusCode() == 200) {
				e.setText("2");
				HttpEntity ent = res.getEntity();
				InputStream is = ent.getContent();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				while ((x = br.readLine()) != null) {
					s.append(x);
				}
				is.close();
				json = s.toString();
				e.setText("3");
				// e.setText("4");
				// e.setText(json);

				JSONObject jo = new JSONObject(json);
				JSONObject weather = jo.getJSONObject("weather");
				JSONObject loc = weather.getJSONObject("location");
				JSONObject cond = weather.getJSONObject("condition");
				JSONObject units = weather.getJSONObject("units");
				String unit = "\u00b0" + units.getString("temperature");

				tv = (TextView) findViewById(R.id.txtCity);
				tv.setText(loc.getString("city"));

				tv = (TextView) findViewById(R.id.txtState);
				tv.setText(loc.getString("region") + ", "
						+ loc.getString("country"));

				tv = (TextView) findViewById(R.id.txtCondition);
				tv.setText(cond.getString("text"));

				tv = (TextView) findViewById(R.id.txtTemp);
				tv.setText(cond.getString("temp") + unit);

				// degree="\u00b0"

				// tv=(TextView)findViewById(R.id.txtCondition);

				get = new HttpGet(weather.getString("img"));
				res = cli.execute(get);
				sl = res.getStatusLine();
				if (sl.getStatusCode() == 200) {
					Bitmap bmp;
					ent = res.getEntity();
					is = ent.getContent();
					bmp = BitmapFactory.decodeStream(is);
					ImageView iv = (ImageView) findViewById(R.id.imgCond);
					iv.setImageBitmap(bmp);
				}

				JSONArray forecast = weather.getJSONArray("forcast");

				TableLayout tbl = (TableLayout) findViewById(R.id.tblWeather);

				int i;

				for (i = 0; i < 5; i++) {

					JSONObject obj = forecast.getJSONObject(i);

					TableRow row = new TableRow(this);

					tv = new TextView(this);
					tv.setText(obj.getString("day"));
					row.addView(tv);

					tv = new TextView(this);
					tv.setText(obj.getString("text"));
					row.addView(tv);

					tv = new TextView(this);
					tv.setText(obj.getString("high") + unit);
					row.addView(tv);

					tv = new TextView(this);
					tv.setText(obj.getString("low") + unit);
					row.addView(tv);

					// tv.setBackgroundColor(Color.WHITE);

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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

}
