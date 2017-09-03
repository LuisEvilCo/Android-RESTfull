package com.example.android.restful;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String JSON_URL =
            "http://560057.youcanlearnit.net/services/json/itemsfeed.php";
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.output);
    }

    public void runClickHandler(View view) {

    }

    public void clearClickHandler(View view) {
        output.setText("");
    }

}
