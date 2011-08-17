package com.mapitprices.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RestClient {

    public static String ExecuteCommand(String url, List<NameValuePair> data) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        try {
            if (data != null) {
                post.setEntity(new UrlEncodedFormEntity(data));
            }

            Log.i("Http Request", url);

            HttpResponse response = httpclient.execute(post);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);

                Log.i("Http Response", result);
                //JSONObject json = new JSONObject(result);
                instream.close();

                return result;
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject ExecuteCommand(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        httpget.setHeader("Content-Type", "application/json");

        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            Log.i("HTTP Response", response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);

                Log.i("Http Response", result);

                JSONObject json = new JSONObject(result);
                instream.close();

                return json;
            } else {
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

        }

        return null;
    }

    private static String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
