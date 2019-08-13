package com.example.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class Scene2 extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private Button btnPre, btnRow;
    String storenum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene2);

        initControls();

        storenum = getIntent().getStringExtra("num");   // MainActivity로부터 매장번호를 받아 변수에 저장.

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Add Fragment
        adapter.AddFragment(new FragmentInfo(),"정보");
        adapter.AddFragment(new FragmentMenu(),"메뉴");
        adapter.AddFragment(new FragmentReview(),"리뷰");

        //adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Scene3.class);
                intent.putExtra("num",storenum);
                startActivity(intent);
            }
        });
    }
    private void initControls(){
        if(tabLayout == null){
            tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        }
        if(appBarLayout == null){
            appBarLayout = (AppBarLayout) findViewById(R.id.appbarid);
        }
        if(viewPager == null){
            viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        }
        if(btnPre == null) {
            btnPre = (Button) findViewById(R.id.btnPre);
        }
        if(btnRow == null) {
            btnRow = (Button) findViewById(R.id.btnRow);
        }
    }

    public String getData(){
        return storenum;
    }
}
