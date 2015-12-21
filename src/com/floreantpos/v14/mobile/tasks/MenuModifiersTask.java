package com.floreantpos.v14.mobile.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.floreantpos.v14.mobile.activity.GV;
import com.floreantpos.response.MenuModifierResponse;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MenuModifiersTask extends AsyncTask<String, Void, List<MenuModifierResponse> > {

    private int menuItemId;

    public MenuModifiersTask(int menuItemId) {
        this.menuItemId =menuItemId;
    }

    @Override
    protected List<MenuModifierResponse>  doInBackground(String... urls) {

        List<MenuModifierResponse>  result = null;

        String uri = GV.URL + "/MenuItemModifiersServlet";

        try {

            URL url = new URL(uri);

            CookieManager cookieManager = new CookieManager();

            CookieHandler.setDefault(cookieManager);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("menuItemId", String.valueOf(menuItemId)));
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

            MenuModifierResponse[] items = new Gson().fromJson(temp.toString(),MenuModifierResponse[].class);

            if (items!=null) {
                result = Arrays.asList(items);
            }



        } catch (Exception e) {
            Log.d("FloreantPOS", e.getMessage());
        }

        return result;
    }

    @Override
    public void onPostExecute(final List<MenuModifierResponse> response) {

       if (response==null) {
           onFailed();
       } else {
           onSuccess(response);
       }

    }

    public abstract void onSuccess(List<MenuModifierResponse> response);

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
