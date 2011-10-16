package com.mapitprices.Utilities;

import android.util.Log;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.User;
import com.mapitprices.WheresTheCheapBeer.Constants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class RestClient {

    public static String ExecuteJSONCommand(String url, JSONObject data) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        StringEntity se = null;
        try {
            se = new StringEntity(data.toString());
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            httppost.setHeader("Accept-Encoding", "gzip");
            httppost.setHeader("SessionToken", User.getInstance().getSessionToken());

//            ResponseHandler responseHandler = new BasicResponseHandler();
            HttpResponse response = httpclient.execute(httppost);

            if (!Constants.DEBUGMODE) {
                GoogleAnalyticsTracker.getInstance().dispatch();
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }

                String result = convertStreamToString(instream);

                Log.i("Http Response", result);
                instream.close();

                return result;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static String ExecuteCommand(String url, List<NameValuePair> data) {
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
            if (!Constants.DEBUGMODE) {
                GoogleAnalyticsTracker.getInstance().dispatch();
            }


            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
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

    public static String ExecuteGetCommand(String url) {
        Log.i("Http Request", url);

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet post = new HttpGet(url);

        try {
            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Accept-Encoding", "gzip"));

            String sessionToken = User.getInstance().getSessionToken();
            headers.add(new BasicHeader("SessionToken", sessionToken));

            //headers.add(new BasicHeader("Content-Type", "application/json"));
            post.setHeaders(headers.toArray(new Header[0]));

            HttpResponse response = httpclient.execute(post);
            GoogleAnalyticsTracker.getInstance().dispatch();

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
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


    public static String ExecuteCommand(String url) {
        return ExecuteCommand(url, null);
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
