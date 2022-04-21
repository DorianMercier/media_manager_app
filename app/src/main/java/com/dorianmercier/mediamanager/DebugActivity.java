package com.dorianmercier.mediamanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.http.RequestHandler;

import java.util.ArrayList;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
    }

    public void buttonHandler(View view) {
        Log.d("buttonHandler", new String("Entering buttonHandler function"));
        switch(view.getId()) {
            case R.id.buttonDebugGetIndex:
                String message = "We are in buttonDebugGetIndex";
                Log.d("buttonHandler", message);
                new Thread(new Runnable() {
                    public void run() {
                        ArrayList<Media> index = RequestHandler.requestIndex();
                        TextView textView = findViewById(R.id.textDebug);
                        StringBuilder final_text = new StringBuilder("[\n    ");
                        String curr_media;
                        assert index != null;
                        for(Media media : index) {
                            curr_media = "    {\n";
                            curr_media += "        \"year\": " + media.year + ",\n";
                            curr_media += "        \"month\": " + media.month + ",\n";
                            curr_media += "        \"day\": " + media.day + ",\n";
                            curr_media += "        \"hour\": " + media.hour + ",\n";
                            curr_media += "        \"minute\": " + media.minute + ",\n";
                            curr_media += "        \"second\": " + media.second + ",\n";
                            curr_media += "    },\n";
                            final_text.append(curr_media);
                        }
                        textView.setText(final_text.toString());
                    }
                }).start();
                break;
            default:
                break;

        }
    }
}