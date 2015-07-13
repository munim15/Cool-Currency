package com.munim.coolcurrency;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class SettingsActivity extends Activity {

    private String[] countryList = {"USA (USD)", "India (INR)", "UAE (AED)"};
    private Integer[] img = {R.drawable.usd, R.drawable.inr, R.drawable.aed};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences pref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String s = pref.getString("currencies", null);
        Log.i("SETTINGS ACTIVITY", s);
        String[] countries =  s == null ? new String[3]: s.split(":");
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setAdapter(new CustomAdapter(this, R.layout.row, countryList, img));
        spinner1.setSelection(0);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(new CustomAdapter(this, R.layout.row, countryList, img));
        spinner2.setSelection(1);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner3.setAdapter(new CustomAdapter(this, R.layout.row, countryList, img));
        spinner3.setSelection(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        CustomAdapter(Context context, int textViewResourceId, String[] objects, Integer[] img) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.country);
            label.setText(countryList[position]);
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            icon.setImageResource(img[position]);
            label.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int height = label.getMeasuredHeight();
            icon.getLayoutParams().height = height;
            icon.getLayoutParams().width = height;
            return row;
        }
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().toLowerCase().contains(myString)){
                index = i;
            }
        }
        return index;
    }
}
