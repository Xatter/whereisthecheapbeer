package com.mapitprices.Utilities;

import android.util.Log;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.User;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class RestClient {

    public static String ExecuteCommand(String url, List<NameValuePair> data, GoogleAnalyticsTracker tracker) {
        Log.i("Http Request", url);

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        try {
            if (data != null) {
                post.setEntity(new UrlEncodedFormEntity(data));
            }

            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Accept-Encoding", "gzip"));

            String sessionToken = User.getInstance().getSessionToken();
            headers.add(new BasicHeader("SessionToken", sessionToken));

            //headers.add(new BasicHeader("Content-Type", "application/json"));
            post.setHeaders(headers.toArray(new Header[0]));

            HttpResponse response = httpclient.execute(post);
            tracker.dispatch();

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if(contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
                {
                    instream = new GZIPInputStream(instream);
                }

                String result = convertStreamToString(instream);

                Log.i("Http Response", result);
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

    public static String ExecuteCommand(String url, GoogleAnalyticsTracker tracker) {
        return ExecuteCommand(url, null, tracker);

        //httpget.setHeader("Content-Type", "application/json");
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
