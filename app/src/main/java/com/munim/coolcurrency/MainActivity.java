package com.munim.coolcurrency;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.munim.coolcurrency.util.SystemUiHider;
import com.yalantis.phoenix.PullToRefreshView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

    private static final String base_url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%3D%22CURRPAIR%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private static final String replacementDummy = "CURRPAIR";
    private static final String baseCurrency = "USD";
    private String currentFromCurrency;
    private String currentToCurrency;
    private double currentRate;
    private double val1;
    private HashMap<String, Double> rates;
    private String[] countries;
    private PullToRefreshView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText et = (EditText) findViewById(R.id.some_text);
        et.setBackgroundResource(android.R.color.transparent);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    val1 = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    val1 = 0;
                }
                update();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /* Manually setting flags */
        View bt = findViewById(R.id.from_curr1);
        bt.setBackgroundResource(R.drawable.usa);
        bt = findViewById(R.id.to_curr1);
        bt.setBackgroundResource(R.drawable.usa);
        bt = findViewById(R.id.from_curr2);
        bt.setBackgroundResource(R.drawable.india);
        bt = findViewById(R.id.to_curr2);
        bt.setBackgroundResource(R.drawable.india);
        bt = findViewById(R.id.from_curr3);
        bt.setBackgroundResource(R.drawable.uae);
        bt = findViewById(R.id.to_curr3);
        bt.setBackgroundResource(R.drawable.uae);

        SharedPreferences pref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        rates = new HashMap<String, Double>();
        if (pref == null) {
            countries = new String[]{"USD", "INR", "AED"};
            rates.put("USD", 1.0);
            rates.put("INR", 63.5449);
            rates.put("AED", 3.6732);

        } else {
            String s = pref.getString("currencies", null);
            countries =  s == null ? new String[3]: s.split(":");
            rates.put(countries[0], Double.parseDouble(pref.getString("curr0", "1.0")));
            rates.put(countries[1], Double.parseDouble(pref.getString("curr1", "1.0")));
            rates.put(countries[2], Double.parseDouble(pref.getString("curr2", "1.0")));
        }
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override

            public void onRefresh() {
                RateFinder r = new RateFinder();
                r.execute();
            }
        });
        currentFromCurrency = countries[0];
        currentToCurrency = countries[0];
        currentRate = 1.0;
        update();
    }

    public void send(View view) {
        TextView textView = (TextView) findViewById(R.id.some_text);
        int id = view.getId();
        FloatingActionsMenu parentMenu = (FloatingActionsMenu) view.getParent();
        switch (id) {
            case R.id.from_curr1:
                currentFromCurrency = countries[0];
                break;
            case R.id.from_curr2:
                currentFromCurrency = countries[1];
                break;
            case R.id.from_curr3:
                currentFromCurrency = countries[2];
                break;
            case R.id.to_curr1:
                currentToCurrency = countries[0];
                break;
            case R.id.to_curr2:
                currentToCurrency = countries[1];
                break;
            case R.id.to_curr3:
                currentToCurrency = countries[2];
                break;
            default:
                break;
        }
        currentRate = rates.get(currentToCurrency) / rates.get(currentFromCurrency);
        update();
        parentMenu.collapse();
        onWindowFocusChanged(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("currencies", countries[0] + ":" + countries[1] + ":" + countries[2]);
        editor.putString("curr0", rates.get(countries[0]).toString());
        editor.putString("curr1", rates.get(countries[1]).toString());
        editor.putString("curr2", rates.get(countries[2]).toString());
        editor.commit();
    }

    /** Makes a request to Yahoo Currency API */
    public void makeYqlRequest() {
        String url1 = String.format(sanitize(base_url), baseCurrency + countries[0]);
        String url2 = String.format(sanitize(base_url), baseCurrency + countries[1]);
        String url3 = String.format(sanitize(base_url), baseCurrency + countries[2]);
        HttpRequestHandler handler1 = new HttpRequestHandler(url1);
        HttpRequestHandler handler2 = new HttpRequestHandler(url2);
        HttpRequestHandler handler3 = new HttpRequestHandler(url3);
        handler1.makeRequest();
        handler2.makeRequest();
        handler3.makeRequest();
        JSONObject json1 = handler1.getJSONObject();
        JSONObject json2 = handler2.getJSONObject();
        JSONObject json3 = handler3.getJSONObject();
        try {
            rates.put(countries[0], json1.getJSONObject("query").
                    getJSONObject("results").getJSONObject("rate").getDouble("Rate"));
            rates.put(countries[1], json2.getJSONObject("query").
                    getJSONObject("results").getJSONObject("rate").getDouble("Rate"));
            rates.put(countries[2], json3.getJSONObject("query").
                    getJSONObject("results").getJSONObject("rate").getDouble("Rate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** Updates the numbers on the screen */
    public void update() {
        TextView prompt1 = (TextView) findViewById(R.id.FromPrompt);
        TextView prompt2 = (TextView) findViewById(R.id.ToPrompt);
        prompt1.setText("From " + currentFromCurrency);
        prompt2.setText("To " + currentToCurrency);
        currentRate = rates.get(currentToCurrency) / rates.get(currentFromCurrency);
        TextView t2 = (TextView) findViewById(R.id.some_text2);
        t2.setText(String.format("%.5f",(val1 * currentRate)));
    }

    private class RateFinder extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            makeYqlRequest();
            Log.i("RateFinder", "%%%%%%%%%%%%%%%%READY");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            update();
            mPullToRefreshView.setRefreshing(false);
        }
    }

    private static String sanitize(String s) {
        return s.replace("%", "%%").replace(replacementDummy, "%s");
    }
}