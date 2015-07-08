package com.munim.coolcurrency;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by munim on 7/7/15.
 */
public class HttpRequestHandler {

    private URL url;
    private String rawContent;

    public HttpRequestHandler(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void makeRequest() {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            readStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readStream(InputStream inp) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inp));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                rawContent += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRawContent() {
        return rawContent;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = null;
        try {
             obj = new JSONObject(rawContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
