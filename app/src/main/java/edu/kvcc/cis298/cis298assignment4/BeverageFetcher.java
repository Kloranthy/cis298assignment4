package edu.kvcc.cis298.cis298assignment4;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * used to retrieve the list of beverages from a remote source
 */
public class BeverageFetcher {
    // String constant for logging
    private static final String TAG = "CrimeFetcher";

    public List<Beverage> fetchBeverages() {
        List<Beverage> beverages = new LinkedList<Beverage>();

        try {
            String url = Uri.parse(
                    "http://barnesbrothers.homeserver.com/beverageapi"
            )
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getURLString(url);
            Log.i(TAG, "received JSON: " + jsonString);
            JSONArray jsonArray = new JSONArray(jsonString);
            parseBeverages(beverages, jsonArray);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (JSONException jsone) {
            jsone.printStackTrace();
        }
        return beverages;
    }

    private String getURLString(String urlSpec) throws IOException {
        return new String(
                getURLBytes(urlSpec)
        );
    }

    private byte[] getURLBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = httpURLConnection.getInputStream();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(
                        httpURLConnection.getResponseMessage() + ": with " + urlSpec
                );
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            // even though the book didn't close in stream
            // better safe than sorry
            inputStream.close();
            return outputStream.toByteArray();
        }
        finally {
            httpURLConnection.disconnect();
        }
    }

    private void parseBeverages(
        List<Beverage> beverages,
        JSONArray jsonArray
    )
        throws IOException, JSONException
    {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            
        }
    }
}
