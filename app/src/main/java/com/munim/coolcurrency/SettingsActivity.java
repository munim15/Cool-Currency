package com.munim.coolcurrency;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/** Saves 3 Quick access currency preferences.
 * @author Munim Ali
 */
public class SettingsActivity extends Activity {

    private String[] countryList = {"USA (USD)", "India (INR)", "UAE (AED)", "European Union (EUR)",
                                    "China (CNY)", "Japan (JPY)", "United Kingdom (GBP)"};
    private Integer[] img = {R.drawable.usd, R.drawable.inr, R.drawable.aed, R.drawable.eur,
                             R.drawable.cny, R.drawable.jpy, R.drawable.gbp};
    private int[] positions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final SharedPreferences pref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String s = pref.getString("currencies", null);
        String[] countries =  s == null ? new String[3]: s.split(":");
        if (pref.getInt("pos1", -1) == -1) {
            positions = new int[] {0, 1, 2};
        } else {
            positions = new int[] {pref.getInt("pos1", -1), pref.getInt("pos2", -1), pref.getInt("pos3", -1)};
        }
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setAdapter(new CustomAdapter(this, R.layout.row, countryList, img, 0));
        spinner1.setSelection(positions[0]);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(new CustomAdapter(this, R.layout.row, countryList, img, 1));
        spinner2.setSelection(positions[1]);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner3.setAdapter(new CustomAdapter(this, R.layout.row, countryList, img, 2));
        spinner3.setSelection(positions[2]);
        Button bttn = (Button) findViewById(R.id.done);
        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("pos1", positions[0]);
                edit.putInt("pos2", positions[1]);
                edit.putInt("pos3", positions[2]);
                String str1 = countryList[positions[0]].substring(countryList[positions[0]].length() - 4,countryList[positions[0]].length() - 1).toLowerCase();
                String str2 = countryList[positions[1]].substring(countryList[positions[1]].length() - 4,countryList[positions[1]].length() - 1).toLowerCase();
                String str3 = countryList[positions[2]].substring(countryList[positions[2]].length() - 4,countryList[positions[2]].length() - 1).toLowerCase();
                edit.putString("currencies", str1 + ":" + str2 + ":" + str3);
                edit.putBoolean("shouldRefresh", true);
                edit.apply();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

        private int number;

        CustomAdapter(Context context, int textViewResourceId, String[] objects, Integer[] img, int number) {
            super(context, textViewResourceId, objects);
            this.number = number;
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
            positions[number] = position;
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
