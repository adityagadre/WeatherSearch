package com.weathersearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.android.Facebook;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class WeatherSearch extends Activity {

	JSONObject jo;
	JSONObject weather;
	JSONObject loc;
	JSONObject cond;
	JSONObject units;
	String strForecast;
	String unit;

	public class AsyncJson extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpClient cli = new DefaultHttpClient();
			HttpGet get = new HttpGet(params[0]);

			StringBuilder s = new StringBuilder("");
			String x, json = "";
			// HttpGet get=new HttpGet("http://www.google.com");

			HttpResponse res;
			try {
				res = cli.execute(get);

				StatusLine sl = res.getStatusLine();
				if (sl.getStatusCode() == 200) {
					HttpEntity ent = res.getEntity();
					InputStream is = ent.getContent();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is));
					while ((x = br.readLine()) != null) {
						s.append(x);
					}
					is.close();
					json = s.toString();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			populatedata(result);
		}
	}
	
	public class AsyncImage extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			HttpClient cli = new DefaultHttpClient();
			HttpGet get = new HttpGet(params[0]);
			Bitmap bmp=null;
			// HttpGet get=new HttpGet("http://www.google.com");

			HttpResponse res;
			try {
				res = cli.execute(get);

				StatusLine sl = res.getStatusLine();
				if (sl.getStatusCode() == 200) {
					HttpEntity ent = res.getEntity();
					InputStream is = ent.getContent();
					
					bmp = BitmapFactory.decodeStream(is);
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			showImage(result);
		}
	}

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

	public void cmdWeatherD_click(View view) {
		String[] s = { "Post Current Weather", "Cancel" };
		AlertDialog.Builder b = new AlertDialog.Builder(WeatherSearch.this);
		b.setTitle("Post To Facebook");
		b.setItems(s, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0)
					cmdWeather_click();

			}
		});
		b.show();
	}

	public void cmdWeather_click() {
		Session s = Session.getActiveSession();
		if (s != null) {
			postWeather();
			return;
		}

		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				postWeather();
			}
		});
	}

	public void postWeather() {
		try {
			Bundle params = new Bundle();

			params.putString("name",
					loc.getString("city") + ", " + loc.getString("region")
							+ ", " + loc.getString("country"));

			params.putString("caption",
					"The current condition of " + loc.getString("city")
							+ " is " + cond.getString("text"));
			params.putString("description",
					"Temperature is " + cond.getString("temp") + unit);
			params.putString("link", weather.getString("feed"));
			params.putString("picture", weather.getString("img"));

			JSONObject prop = new JSONObject(
					"{\"Look at details: \": {\"text\": \"here\", \"href\": \""
							+ weather.getString("link") + "\"}}");
			params.putString("properties", prop.toString());

			WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
					WeatherSearch.this, Session.getActiveSession(), params)
					.setOnCompleteListener(new WebDialog.OnCompleteListener() {

						@Override
						public void onComplete(Bundle values,
								FacebookException error) {

							if (error == null) {
								String postid;
								postid = values.getString("post_id");
								if (postid == null) {
									Toast toast = Toast.makeText(
											WeatherSearch.this,
											"Post was not published",
											Toast.LENGTH_SHORT);
									toast.show();
								} else {
									Toast toast = Toast.makeText(
											WeatherSearch.this,
											"Post was published",
											Toast.LENGTH_SHORT);
									toast.show();
								}
							}

						}
					})).build();
			feedDialog.show();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void cmdForecastD_click(View view) {
		String[] s = { "Post Weather Forecast", "Cancel" };
		AlertDialog.Builder b = new AlertDialog.Builder(WeatherSearch.this);
		b.setTitle("Post To Facebook");
		b.setItems(s, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0)
					cmdForecast_click();

			}
		});
		b.show();
	}

	public void cmdForecast_click() {
		Session s = Session.getActiveSession();
		if (s != null) {
			postForecast();
			return;
		}

		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				postForecast();
			}
		});
	}

	public void postForecast() {
		try {
			Bundle params = new Bundle();

			params.putString("name",
					loc.getString("city") + ", " + loc.getString("region")
							+ ", " + loc.getString("country"));

			params.putString("caption",
					"Weather forecast for city  " + loc.getString("city")
							+ ". ");
			params.putString("description", strForecast);
			params.putString("link", weather.getString("feed"));
			params.putString("picture",
					"http://www-scf.usc.edu/~csci571/2013Fall/hw8/weather.jpg");

			JSONObject prop = new JSONObject(
					"{\"Look at details: \": {\"text\": \"here\", \"href\": \""
							+ weather.getString("link") + "\"}}");
			params.putString("properties", prop.toString());

			WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
					WeatherSearch.this, Session.getActiveSession(), params)
					.setOnCompleteListener(new WebDialog.OnCompleteListener() {

						@Override
						public void onComplete(Bundle values,
								FacebookException error) {

							if (error == null) {
								String postid;
								postid = values.getString("post_id");
								if (postid == null) {
									Toast toast = Toast.makeText(
											WeatherSearch.this,
											"Post was not published",
											Toast.LENGTH_SHORT);
									toast.show();
								} else {
									Toast toast = Toast.makeText(
											WeatherSearch.this,
											"Post was published",
											Toast.LENGTH_SHORT);
									toast.show();
								}
							}

						}
					})).build();
			feedDialog.show();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public void btnSearch_Click(View view) {
		TextView tv;

		EditText e = (EditText) findViewById(R.id.editText1);
		String l = e.getText().toString();
		String type = null;
		String tempU;

		if (l.matches("\\d{5,5}")) {
			type = "zip";
		} else if (l.matches("\\d*")) {
			Toast toast = Toast.makeText(this,
					"Invalid Zip Code: Must be 5 digits\nExample:90089",
					Toast.LENGTH_SHORT);
			toast.show();
			return;
		} else if (l
				.matches("([a-zA-Z][a-zA-Z0-9 \\.']*,[ ]*){1,2}[a-zA-Z0-9 ]*")) {
			type = "city";
		} else {
			Toast toast = Toast
					.makeText(
							this,
							"Invalid Location: Must be a city with state or country\nExample: Los Angeles, CA",
							Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		try {
			l = java.net.URLEncoder.encode(l, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		RadioButton rb;
		rb = (RadioButton) findViewById(R.id.tmpC);
		if (rb.isChecked()) {
			tempU = "c";
		} else {
			tempU = "f";
		}

		String[] params = { "http://cs-server.usc.edu:12375/examples/servlet/WeatherServlet?location="
				+ l + "&type=" + type + "&tempUnit=" + tempU };
		(new AsyncJson()).execute(params);
	}

	public void showImage(Bitmap bmp)
	{
		ImageView iv = (ImageView) findViewById(R.id.imgCond);
		 iv.setVisibility(0); 
		 iv.setImageBitmap(bmp);
	}
	
	public void populatedata(String json) {
		TextView tv;
		try {
			jo = new JSONObject(json);

			try {
				weather = jo.getJSONObject("weather");
			} catch (JSONException e1) {
				tv = (TextView) findViewById(R.id.txtCity);
				tv.setText("No Results Found!");
				tv.setVisibility(0);

				tv = (TextView) findViewById(R.id.txtState);
				tv.setVisibility(4);
				tv = (TextView) findViewById(R.id.txtCondition);
				tv.setVisibility(4);
				tv = (TextView) findViewById(R.id.txtTemp);
				tv.setVisibility(4);
				ImageView iv = (ImageView) findViewById(R.id.imgCond);
				iv.setVisibility(4);
				TableLayout tbl = (TableLayout) findViewById(R.id.tblWeather);
				tbl.removeAllViews();

				tv = (TextView) findViewById(R.id.txtForecast);
				tv.setVisibility(4);

				tv = (TextView) findViewById(R.id.cmdForecast);
				tv.setVisibility(4);
				tv = (TextView) findViewById(R.id.cmdWeather);
				tv.setVisibility(4);

				return;
			}

			loc = weather.getJSONObject("location");
			cond = weather.getJSONObject("condition");
			units = weather.getJSONObject("units");
			unit = "\u00b0" + units.getString("temperature");

			tv = (TextView) findViewById(R.id.txtCity);
			tv.setText(loc.getString("city"));
			tv.setVisibility(0);

			tv = (TextView) findViewById(R.id.txtState);
			tv.setText(loc.getString("region") + ", "
					+ loc.getString("country"));
			tv.setVisibility(0);

			tv = (TextView) findViewById(R.id.txtCondition);
			tv.setText(cond.getString("text"));
			tv.setVisibility(0);

			tv = (TextView) findViewById(R.id.txtTemp);
			tv.setText(cond.getString("temp") + unit);
			tv.setVisibility(0);

			String[] params={weather.getString("img")};
			(new AsyncImage()).execute(params);
			

			JSONArray forecast = weather.getJSONArray("forcast");

			TableLayout tbl = (TableLayout) findViewById(R.id.tblWeather);
			tbl.removeAllViews();
			int i;
			strForecast = "";

			TableRow row = new TableRow(this);
			row.setBackgroundColor(Color.LTGRAY);

			int headersize = 16;
			int rowfont = 14;

			tv = new TextView(this);
			tv.setText("Day");
			tv.setTextSize(headersize);
			tv.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.thead));
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setWidth(90);
			row.addView(tv);

			tv = new TextView(this);
			tv.setText("Weather");
			tv.setTextSize(headersize);
			tv.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.thead));
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setWidth(190);
			row.addView(tv);

			tv = new TextView(this);
			tv.setText("High");
			tv.setTextSize(headersize);
			tv.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.thead));
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setWidth(90);
			row.addView(tv);

			tv = new TextView(this);
			tv.setText("Low");
			tv.setTextSize(headersize);
			tv.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.thead));
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setWidth(90);
			row.addView(tv);

			tbl.addView(row);

			for (i = 0; i < 5; i++) {

				JSONObject obj = forecast.getJSONObject(i);

				row = new TableRow(this);

				tv = new TextView(this);
				tv.setText(obj.getString("day"));
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextSize(rowfont);
				if ((i & 1) == 0) {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row1));
				} else {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row2));
				}
				row.addView(tv);

				tv = new TextView(this);
				tv.setText(obj.getString("text"));
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextSize(rowfont);
				if ((i & 1) == 0) {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row1));
				} else {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row2));
				}
				row.addView(tv);

				tv = new TextView(this);
				tv.setText(obj.getString("high") + unit);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextSize(rowfont);
				tv.setTextColor(0xffFF9900);
				if ((i & 1) == 0) {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row1));
				} else {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row2));
				}
				row.addView(tv);

				tv = new TextView(this);
				tv.setText(obj.getString("low") + unit);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextSize(rowfont);
				tv.setTextColor(0xff3399FF);
				if ((i & 1) == 0) {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row1));
				} else {
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.row2));
				}
				row.addView(tv);

				tbl.addView(row);

				// Append Forecast to the string
				strForecast += obj.getString("day") + ": "
						+ obj.getString("text") + ", " + obj.getString("high")
						+ unit + "/" + obj.getString("low") + unit;
				if (i < 4)
					strForecast += ";";
				else
					strForecast += ".";

			}
			tbl.setBackgroundColor(Color.WHITE);

			tv = (TextView) findViewById(R.id.cmdWeather);
			tv.setVisibility(0);
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					cmdWeatherD_click(v);
				}
			});

			tv = (TextView) findViewById(R.id.txtForecast);
			tv.setVisibility(0);

			tv = (TextView) findViewById(R.id.cmdForecast);
			tv.setVisibility(0);
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					cmdForecastD_click(v);

				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

}
