package com.example.android.restful;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.output);

    }

    public void runClickHandler(View view) {
        output.append("Button clicked\n");
    }

    public void clearClickHandler(View view) {
        output.setText("");
    }
}
