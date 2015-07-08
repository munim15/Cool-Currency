package com.munim.coolcurrency;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.munim.coolcurrency.util.SystemUiHider;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

    private String currentFromCurrency;
    private String currentToCurrency;
    private double currentRate;
    private double val1;
    private HashMap<String, Double> rates;
    private String[] countries;

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
        EditText et2 = (EditText) findViewById(R.id.some_text2);
        et2.setBackgroundResource(android.R.color.transparent);

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

        countries = new String[] {"USD", "INR", "AED"};
        rates = new HashMap<String, Double>();
        rates.put("USD", 1.0);
//        rates.put("INR", 63.5449);
//        rates.put("AED", 3.6732);
        RateFinder r = new RateFinder();
        r.execute();
        currentFromCurrency = "USD";
        currentToCurrency = "USD";
        currentRate = 1.0;
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

    /** Makes a request to Yahoo Currency API */
    public void makeYqlRequest() {
        String base_url1 = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%3D%22USDINR%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        String base_url2 = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%3D%22USDAED%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        HttpRequestHandler handler1 = new HttpRequestHandler(base_url1);
        HttpRequestHandler handler2 = new HttpRequestHandler(base_url2);
        handler1.makeRequest();
        handler2.makeRequest();
        JSONObject json1 = handler1.getJSONObject();
        JSONObject json2 = handler2.getJSONObject();
        try {
            rates.put("INR", json1.getJSONObject("query").
                    getJSONObject("results").getJSONObject("rate").getDouble("Rate"));
            rates.put("AED", json2.getJSONObject("query").
                    getJSONObject("results").getJSONObject("rate").getDouble("Rate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** Updates the numbers on the screen */
    public void update() {
        TextView t2 = (TextView) findViewById(R.id.some_text2);
        t2.setText(String.format("%.5f",(val1 * currentRate)));
    }

    private class RateFinder extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            makeYqlRequest();
            System.out.println("%%%%%%%%%%%%%%%%READY");
            return null;
        }
    }

}
