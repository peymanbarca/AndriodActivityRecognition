package com.example.jack.sensors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {
    private EditText e1;
    private EditText e2;
    private Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);

        e1 = (EditText) findViewById(R.id.e1);
        e2 = (EditText) findViewById(R.id.e2);
        b1= (Button) findViewById(R.id.login) ;

        e1.setText(((Const) this.getApplication()).getIp().toString());
        e2.setText(((Const) this.getApplication()).getPort().toString());
        final Integer port = Integer.parseInt(e2.getText().toString());

        final Const cst = (Const) this.getApplication();

        b1.setOnClickListener(new View.OnClickListener()   {

            @Override
            public void onClick(View v)
            {

//                ((Const) this.getApplication()).setIp(e1.getText().toString());
//                ((Const) this.getApplication()).setPort(port);
                cst.setIp(e1.getText().toString());
                cst.setPort(port);
                startActivity(new Intent(SettingActivity.this, SensorsActivity.class));
                finish();

            }



        });




    }
}
