package com.munim.coolcurrency;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.munim.coolcurrency.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

    private String currentFromCurrency;
    private String currentToCurrency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText et = (EditText) findViewById(R.id.some_text);
        et.setBackgroundResource(android.R.color.transparent);
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

        currentFromCurrency = "USD";
        currentToCurrency = "USD";
    }

    public void send(View view) {
        TextView textView = (TextView) findViewById(R.id.some_text);
        int id = view.getId();
        FloatingActionsMenu parentMenu = (FloatingActionsMenu) view.getParent();
        switch (id) {
            case R.id.from_curr1:
                currentFromCurrency = "USD";
                break;
            case R.id.from_curr2:
                currentFromCurrency = "INR";
                break;
            case R.id.from_curr3:
                currentFromCurrency = "AED";
                break;
            case R.id.to_curr1:
                currentToCurrency = "USD";
                break;
            case R.id.to_curr2:
                currentToCurrency = "INR";
                break;
            case R.id.to_curr3:
                currentToCurrency = "AED";
                break;
            default:
                break;
        }
        textView.setText("From: " + currentFromCurrency + "\nTo: " + currentToCurrency);
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


}
