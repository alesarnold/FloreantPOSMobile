package com.floreantpos.v14.mobile.tasks;

import android.os.AsyncTask;
import com.floreantpos.v14.mobile.activity.GV;
import com.floreantpos.response.TicketItemResponse;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AddMenuItemToTicketTask extends AsyncTask<String, Void, TicketItemResponse> {

    private int ticketId;
    private int menuItemId;
    private int userId;

    public AddMenuItemToTicketTask(int   ticketId,int menuItemId, int userId) {
        this.ticketId=ticketId;
        this.menuItemId=menuItemId;
        this.userId=userId;
    }

    @Override
    protected TicketItemResponse doInBackground(String... urls) {

        TicketItemResponse result = null;

        String uri = GV.URL + "/AddMenuItemToTicketServlet";

        try {

            URL url = new URL(uri);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("ticketId", String.valueOf(ticketId)));
            params.add(new BasicNameValuePair("menuItemId", String.valueOf(menuItemId)));
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

            result = new Gson().fromJson(temp.toString(),TicketItemResponse.class);

        } catch (Exception e) {
           e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onPostExecute(final TicketItemResponse response) {

       if (response==null) {
           onFailed();
       } else {
           onSuccess(response);
       }

    }

    public abstract void onSuccess(TicketItemResponse ticketResponse);

    public abstract void onFailed();


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
