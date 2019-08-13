package com.example.teamproject;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConfirmActivity extends AppCompatActivity {
    private Button reviewB, ReturnB;  // 리뷰남기기 버튼 / 메인화면으로 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        init();
        //  리뷰남기기 버튼 클릭 시
        reviewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"지원 예정입니다.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        // 메인화면으로 버튼 클릭 시
        ReturnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"메인화면으로 돌아갑니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void init(){
        if(reviewB == null){
            reviewB = (Button) findViewById(R.id.ReviewButton);
        }
        if(ReturnB == null){
            ReturnB = (Button) findViewById(R.id.ReturnButton);
        }
    }
}
