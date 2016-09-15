package com.wanghaisheng.floatwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.wanghaisheng.floatwindow.service.FloatViewService;

public class MainActivity extends AppCompatActivity {

    Button btnShowFloatView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowFloatView = (Button) findViewById(R.id.btn_show_flowview);
        btnShowFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FloatViewService.class);
                startService(intent);
                finish();
            }
        });
    }
}
