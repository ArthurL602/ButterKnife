package com.ljb.butterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.butterknife.annotations.BindView;

public class SecondActivity extends AppCompatActivity {
    @BindView(R.id.tv_1)
    TextView tv3;
    @BindView(R.id.tv_2)
    TextView tv4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
