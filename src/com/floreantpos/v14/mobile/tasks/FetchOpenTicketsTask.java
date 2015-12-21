package com.floreantpos.v14.mobile.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.floreantpos.v14.mobile.activity.GV;
import com.floreantpos.response.TicketResponse;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FetchOpenTicketsTask extends AsyncTask<Void, Void, List<TicketResponse>> {

    public FetchOpenTicketsTask() {

    }

    @Override
    protected List<TicketResponse> doInBackground(Void... urls) {

        List<TicketResponse> result = null;

        String uri = GV.URL + "/FetchOpenTicketsServlet";

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
//            writer.write(getQuery(params));
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

            TicketResponse[] array = new Gson().fromJson(temp.toString(),TicketResponse[].class);

            if (array==null) {
                result = new ArrayList<TicketResponse>();
            } else {
                result = Arrays.asList(array);
            }

        } catch (Exception e) {
            Log.d("FloreantPOS", e.getMessage());
        }

        return result;
    }

    @Override
    public void onPostExecute(final List<TicketResponse> response) {

        if (response==null) {
            onFailed();
        } else {
            onSuccess(response);
        }
    }

    public abstract void onSuccess(List<TicketResponse> response);

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
