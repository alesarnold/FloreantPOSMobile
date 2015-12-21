package com.floreantpos.v14.mobile.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.floreantpos.v14.mobile.activity.GV;
import com.floreantpos.response.MenuCategoryResponse;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MenuCategoriesTask extends AsyncTask<String, Void, List<MenuCategoryResponse>> {

    public MenuCategoriesTask() {
    }

    @Override
    protected List<MenuCategoryResponse> doInBackground(String... urls) {

        List<MenuCategoryResponse> result = null;

        String uri = GV.URL + "/MenuCategoriesServlet";

        try {

            URL url = new URL(uri);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
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

            MenuCategoryResponse[] responseArray = new Gson().fromJson(temp.toString(),MenuCategoryResponse[].class);

            result = responseArray == null ? new ArrayList<MenuCategoryResponse>() : Arrays.asList(responseArray);

        } catch (Exception e) {
            Log.d("FloreantPOS", e.getMessage());
        }

        return result;
    }

    @Override
    public void onPostExecute(final List<MenuCategoryResponse> responses) {

       if (responses==null) {
           onFailed();
       } else {
           onSuccess(responses);
       }

    }

    public abstract void onSuccess(List<MenuCategoryResponse> responses);

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
