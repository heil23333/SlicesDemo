package fsc.com.slicesdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() != null && getIntent().hasExtra("action")) {
            TextView textView = findViewById(R.id.text);
            textView.setText(getIntent().getStringExtra("action"));
        }
    }
}
