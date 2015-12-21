package com.floreantpos.v14.mobile.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.floreantpos.v14.mobile.activity.GV;
import org.apache.http.NameValuePair;

import java.io.*;
import java.net.*;
import java.util.List;

public abstract class LogoutTask extends AsyncTask<Void, Void, String> {


    public LogoutTask() {
    }

    @Override
    protected String doInBackground(Void... urls) {

        String uri = GV.URL + "/LogoutServlet";

        try {

            URL url = new URL(uri);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.flush();
            writer.close();
            os.close();

        } catch (Exception e) {
            Log.d("FloreantPOS", e.getMessage());
        }

        return "";
    }

    @Override
    public void onPostExecute(final String response) {
    }


    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
