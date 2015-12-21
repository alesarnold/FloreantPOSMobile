package com.floreantpos.v14.mobile.tasks;

import android.os.AsyncTask;
import com.floreantpos.v14.mobile.activity.GV;
import com.floreantpos.response.LoginResponse;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public abstract class LoginTask extends AsyncTask<String, Void, LoginResponse> {

    private String secretKey;

    public LoginTask(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected LoginResponse doInBackground(String... urls) {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        LoginResponse result = null;

        String uri = GV.URL + "/LoginServlet";

        try {

            URL url = new URL(uri);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("secretKey", secretKey));
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

            result = new Gson().fromJson(temp.toString(),LoginResponse.class);

        } catch (Exception e) {
            result = new LoginResponse();
            result.status=false;
            result.message=e.getMessage();
        }

        return result;
    }

    @Override
    public void onPostExecute(final LoginResponse response) {

        if (response==null){
            onFailed("");
        } else if (response.status) {
           onSuccess(response);
       } else {
           onFailed(response.message);
       }

    }

    public abstract void onSuccess(LoginResponse loginResponse);

    public abstract void onFailed(String msg);


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
