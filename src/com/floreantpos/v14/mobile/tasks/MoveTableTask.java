package com.floreantpos.v14.mobile.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.floreantpos.v14.mobile.activity.GV;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public abstract class MoveTableTask extends AsyncTask<String, Void, Void> {

    private int ticketId;
    private int tableNo;
    private int userId;

    public MoveTableTask(int ticketId, int tableNo, int userId) {
        this.ticketId=ticketId;
        this.userId=userId;
        this.tableNo=tableNo;
    }

    @Override
    protected Void doInBackground(String... urls) {

        String uri = GV.URL + "/MoveTableServlet";

        try {

            URL url = new URL(uri);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tableNo", String.valueOf(tableNo)));
            params.add(new BasicNameValuePair("ticketId", String.valueOf(ticketId)));
            params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder temp = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                temp.append((inputLine));
            }

            in.close();

        } catch (Exception e) {
            Log.d("FloreantPOS", e.getMessage());
        }

        return null;
    }

    @Override
    public void onPostExecute(final Void response) {

        onFinished();

    }

    public abstract void onFinished() ;

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
