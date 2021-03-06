package com.dorianmercier.mediamanager.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import androidx.room.Room;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.SettingDAO;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestHandler {

    public static String domain = "192.168.137.1";
    public static String port = "8080";

    public RequestHandler(Context context) {
        SettingDAO settingDAO;
        AppDatabase db;
        db = Room.databaseBuilder(context, AppDatabase.class, "MediaManagerDatabase").build();
        settingDAO = db.settingDAO();

        new Thread(new Runnable() {
            public void run() {
                String tmp_domain = settingDAO.findSetting("IP");
                if(tmp_domain !=null) domain = tmp_domain;

                String tmp_port = settingDAO.findSetting("port");
                if(tmp_port != null) port = tmp_port;
            }
        }).start();
    }

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
            //URL url = new URL("http://10.0.2.2:8080/get_index");
            URL url = new URL("http://"+domain+":"+port+"/get_index");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.connect();
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

    private static Bitmap readStreamIcon(InputStream in) {
        return BitmapFactory.decodeStream(in);
    }

    public static Bitmap get_icon(int year, int month, int day, int hour, int minute, int second, int size) {

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("year", year);
            jsonBody.put("month", month);
            jsonBody.put("day", day);
            jsonBody.put("hour", hour);
            jsonBody.put("minute", minute);
            jsonBody.put("second", second);
            jsonBody.put("size", size);
        }
        catch(JSONException e) {
            e.printStackTrace();
            Log.e("Error generating JSON", e.toString());
            return null;
        }

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

        try {
            Log.d("Just before making request get_icon", "message");
            //URL url = new URL("http://10.0.2.2:8080/get_icon");
            URL url = new URL("http://"+domain+":"+port+"/get_icon");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            Log.d("get_icon request", "Output set");
                /*
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(jsonBody);
                oos.flush();*/

            //Log.d("get_icon request", "Body: " + bos);

            //OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

            String json = jsonBody.toString();

            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

            OutputStream out = urlConnection.getOutputStream();
            out.write(jsonBytes, 0, jsonBytes.length);

            Log.d("get_icon request", "Body set");

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStreamIcon(in);
            }
            catch(Exception e) {
                e.printStackTrace();
                Log.e("Error get_icon()", e.toString());
                return null;
            }
            finally {
                urlConnection.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error get_icon()", e.toString());
            return null;
        }
    }

    public static Bitmap get_picture(int year, int month, int day, int hour, int minute, int second) {

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("year", year);
            jsonBody.put("month", month);
            jsonBody.put("day", day);
            jsonBody.put("hour", hour);
            jsonBody.put("minute", minute);
            jsonBody.put("second", second);
        }
        catch(JSONException e) {
            e.printStackTrace();
            Log.e("Error generating JSON", e.toString());
            return null;
        }

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

        try {
            Log.d("Just before making request get_picture", "message");
            //URL url = new URL("http://10.0.2.2:8080/get_icon");
            URL url = new URL("http://"+domain+":"+port+"/get_picture");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            Log.d("get_picture request", "Output set");

            String json = jsonBody.toString();

            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

            OutputStream out = urlConnection.getOutputStream();
            out.write(jsonBytes, 0, jsonBytes.length);

            Log.d("get_picture request", "Body set");

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStreamIcon(in);
            }
            catch(Exception e) {
                e.printStackTrace();
                Log.e("Error picture request", e.toString());
                return null;
            }
            finally {
                urlConnection.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error picture request", e.toString());
            return null;
        }
    }
}
