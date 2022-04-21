package com.dorianmercier.mediamanager.http;

import android.util.JsonReader;
import android.util.Log;

import com.dorianmercier.mediamanager.Database.Media;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestHandler {

    private static ArrayList<Media> readStreamIndex(InputStream in) throws IOException {
        return readJsonStream(in);
    }


    private static ArrayList<Media> readJsonStream(InputStream in) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return readMediaArray(reader);
        }
    }

    private static ArrayList<Media> readMediaArray(JsonReader reader) throws IOException {
        ArrayList<Media> messages = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMedia(reader));
        }
        reader.endArray();
        return messages;
    }

    private static Media readMedia(JsonReader reader) throws IOException {
        int year = 1970;
        int month = 1;
        int day = 1;
        int hour = 0;
        int minute = 0;
        int second = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "year":
                    year = reader.nextInt();
                    break;
                case "month":
                    month = reader.nextInt();
                    break;
                case "day":
                    day = reader.nextInt();
                    break;
                case "hour":
                    hour = reader.nextInt();
                    break;
                case "minute":
                    minute = reader.nextInt();
                    break;
                case "second":
                    second = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new Media(year, month, day, hour, minute, second);
    }


    public static ArrayList<Media> requestIndex() {

        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            Log.e("Install all-trusting trust manager", "failed", e);
        }

        try {
            Log.d("Just before making request", "message");
            URL url = new URL("http://10.0.2.2:8080/get_index");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Log.d("Input stream: ", in.toString());
                return readStreamIndex(in);
            }
            finally {
                urlConnection.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error requestIndex()", e.toString());
            return null;
        }
    }
}
