package com.example.jack.sensors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import static android.R.attr.port;

public class SettingActivity extends AppCompatActivity {
    private EditText e1;
    private EditText e2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);

        e1 = (EditText) findViewById(R.id.e1);
        e2 = (EditText) findViewById(R.id.e2);

        e1.setText(((Const) this.getApplication()).getIp().toString());
        e2.setText(((Const) this.getApplication()).getPort().toString());
        Integer port = Integer.parseInt(e2.getText().toString());


        ((Const) this.getApplication()).setIp(e1.getText().toString());
        ((Const) this.getApplication()).setPort(port);


    }
}
