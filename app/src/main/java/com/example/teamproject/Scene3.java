package com.example.teamproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Scene3 extends AppCompatActivity {

    private static final String  TAG = "Scene3";
    TextView barcodetv;
    public static String barcode_st = "";
    ImageView barcode;
    String url = "http://192.168.0.20:8000/";
    ContentValues info = new ContentValues();
    String storenum = "";
    String bar ="";
    TextView waitingView;
    Button btnDelay, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene3);

        initControls();
//        if(Scene3.barcode_st == "") {
            Intent getin = getIntent();
            storenum = getIntent().getStringExtra("num");
            info.put("waiting", storenum);
            Scene3.NetworkTask networkTask = new Scene3.NetworkTask(url, info);
            networkTask.execute();
//        }
//        else if (Scene3.barcode_st != "") {
//            Intent intent = new Intent(getApplicationContext(),Scene3.class);
//            startActivity(intent);
//        }

        btnDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_delay();           // 팝업창 띄우기
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_cancel();          // 팝업창 띄우기
            }
        });

        // 카운트 띄우기
        final Chronometer chronometer = (Chronometer) findViewById(R.id.timeView);
        chronometer.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"onRestart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG,"onSaveInstanceState");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG,"onRestoreInstanceState");
    }

    // 뒤로가기 버튼 클릭시
    public void onBackPressed() {
        show_back();                   // 팝업창 띄우기
    }

    private void initControls() {
        if (barcodetv == null){
            barcodetv = (TextView) findViewById(R.id.barcodetv);
        }
        if (barcode == null){
            barcode = (ImageView) findViewById(R.id.barcode);
        }
        if (waitingView == null){
            waitingView = (TextView) findViewById(R.id.waitingView);
        }
        if (btnDelay ==null){
            btnDelay = (Button) findViewById(R.id.btnDelay);
        }
        if(btnCancel == null){
            btnCancel = (Button) findViewById(R.id.btnCancel);
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String result;          // 요청 결과를 저장할 변수
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request3(url,values);

            return result;
        }

        protected  void onPostExecute(String s) {
            super.onPostExecute(s);
//            doInBackground 로 부터 리턴된 값이 매개변수로 넘어오므로 s를 추력.
//            tv.setText(s);
            bar = s.substring(0,10);
            String text = s.substring(12);
            barcodetv.setText(bar);
            barcode_st = bar;
            waitingView.setText(text);
            Bitmap barcodes = createBarcode(barcode_st);
            barcode.setImageBitmap(barcodes);
            barcode.invalidate();
        }

        public Bitmap createBarcode(String code) {
            Bitmap bitmap = null;
            MultiFormatWriter gen = new MultiFormatWriter();
            try{
                final int WIDTH = 840;
                final int HEIGHT = 320;
                BitMatrix bytemap = gen.encode(code, BarcodeFormat.CODE_128, WIDTH,HEIGHT);
                bitmap = Bitmap.createBitmap(WIDTH,HEIGHT,Bitmap.Config.ARGB_8888);
                for(int i = 0 ; i < WIDTH ; ++i){
                    for(int j = 0 ; j < HEIGHT ; ++j){
                        bitmap.setPixel(i,j,bytemap.get(i,j)? Color.BLACK : Color.WHITE);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask2(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String result;          // 요청 결과를 저장할 변수
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request3(url,values);

            return result;
        }

        protected  void onPostExecute(String s) {
            super.onPostExecute(s);
//            doInBackground 로 부터 리턴된 값이 매개변수로 넘어오므로 s를 추력.
//            tv.setText(s);
//            barcodetv.setText(bar);
//            waitingView.setText(text);
        }
    }

    public void show_delay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("정말 양보하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "양보하였습니다.", Toast.LENGTH_LONG).show();
                        initControls();
                        info.clear();
                        info.put("waiting",storenum);
                        info.put("zzzzzzzz",bar);
                        System.out.println(info.toString());
                        Scene3.NetworkTask2 networkTask2 = new Scene3.NetworkTask2(url, info);
                        networkTask2.execute();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getApplicationContext(),".",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    public void show_cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("정말 취소하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_LONG).show();
                        initControls();
                        info.clear();
                        info.put("account/cancel", bar);
                        System.out.println(info.toString());
                        Scene3.NetworkTask2 networkTask2 = new Scene3.NetworkTask2(url, info);
                        networkTask2.execute();

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getApplicationContext(), "취소하지 않았습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    public void show_back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Caution");
        builder.setMessage("뒤로가기 하시면 줄서기가 취소됩니다\n그래도 진행하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getApplicationContext(), ".", Toast.LENGTH_LONG).show();
                        initControls();
                        info.clear();
                        info.put("account/cancel", bar);
                        System.out.println(info.toString());
                        Scene3.NetworkTask2 networkTask2 = new Scene3.NetworkTask2(url, info);
                        networkTask2.execute();

                        // 액티비티 종료
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getApplicationContext(), "취소하지 않았습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
}
