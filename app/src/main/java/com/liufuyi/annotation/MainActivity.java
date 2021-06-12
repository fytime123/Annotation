package com.liufuyi.annotation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.liufuyi.annotationlib.AnnotationInject;
import com.liufuyi.annotationlib.OnClick;
import com.liufuyi.annotationlib.OnLongClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button button1 = findViewById(R.id.click1);
//        Button button2 = findViewById(R.id.click2);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        button2.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return false;
//            }
//        });

        AnnotationInject.injectEvent(this);
    }

    @OnClick(R.id.click1)
    public void onClick(View view){
        Log.v("liufuyi2","@OnClick(R.id.click1)");
    }

    @OnLongClick(R.id.click2)
    public boolean onLongClick(View view){
        Log.v("liufuyi2","@OnLongClick(R.id.click2)");
        return false;
    }
}