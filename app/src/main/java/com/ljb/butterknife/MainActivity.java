package com.ljb.butterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.butterknife.ButterKnife;
import com.butterknife.annotations.BindView;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv_1)
    TextView mTv1;
    @BindView(R.id.tv_2)
    TextView mTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTv1.setText(111+"");
        mTv2.setText(111+"");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
