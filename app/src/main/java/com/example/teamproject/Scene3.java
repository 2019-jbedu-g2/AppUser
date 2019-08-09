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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

public class Scene3 extends AppCompatActivity {

    private static final String  TAG = "Scene3";
    TextView barcodetv;
    public static String barcode_st = "";
    private WebSocketClient wsc;
    ImageView barcode;
    String url = "http://192.168.0.8:8000/";
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
            Scene3.NetworkTask networkTask = new Scene3.NetworkTask(url, info,"barcode");
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
    // 소켓 연결 메소드
    public void SocketConnect(String Storenum, String Barcode){
        StringBuffer URL = new StringBuffer();
        URL.append("ws://192.168.0.8:8000/queue/");
        URL.append(Storenum);
        URL.append("/");
        URL.append(Barcode);
        String url = URL.toString();
        try{
            Draft d = new Draft_6455();
            wsc = new WebSocketClient(new URI(url),d) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wsc.send("");
                        }
                    });
                }

                @Override
                public void onMessage(String message) {

                    try {
                        JSONObject text = new JSONObject(message);
                        final String number = text.getString("message");
                        if(number.equals("확인이 불가합니다.")){
                            Intent intent = new Intent(getApplicationContext(),ConfirmActivity.class);
                            wsc.close();
                            startActivity(intent);
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    waitingView.setText("현재 대기 인원 :" + number + " 명" );
                                }
                            });

                    }
                }catch (JSONException E){
                        E.getStackTrace();
                    }
                }

                @Override
                public void onClose(final int code, final String reason, boolean remote) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }

                @Override
                public void onError(final Exception ex) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ex.printStackTrace();
                            Toast.makeText(getApplicationContext(),"서버와의 통신에 문제가 발생 하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };
        } catch(URISyntaxException E){
            E.printStackTrace();
            Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
        }
        wsc.connect();
    }


    // http 네트워크 함수
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private String CalledType;

        public NetworkTask(String url, ContentValues values, String CalledType) {

            this.url = url;
            this.values = values;
            this.CalledType = CalledType;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String result;          // 요청 결과를 저장할 변수
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url,values);

            return result;
        }

        protected  void onPostExecute(String s) {
            super.onPostExecute(s);
            if(CalledType.equals("delay")) {
                wsc.send("");
            }else if( CalledType.equals("cancel")){
                wsc.send("");
                wsc.close();
            }else if(CalledType.equals("barcode")){
                //            doInBackground 로 부터 리턴된 값이 매개변수로 넘어오므로 s를 추력.
//            tv.setText(s);
                if(s.length()<10){}else {
                    bar = s.substring(0, 10);
                    String text = s.substring(12);
                    barcodetv.setText(bar);
                    barcode_st = bar;
                    waitingView.setText(text);
                    Bitmap barcodes = createBarcode(barcode_st);
                    barcode.setImageBitmap(barcodes);
                    barcode.invalidate();
                    SocketConnect(storenum, bar);
                }
            }
            }
        }
        // 바코드 생성
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
                        StringBuffer sb = new StringBuffer();
                        sb.append(storenum).append("/").append(bar);
                        info.put("waiting",sb.toString());
                        Scene3.NetworkTask networkTask = new Scene3.NetworkTask(url, info,"delay");
                        networkTask.execute();
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
                        Scene3.NetworkTask networkTask = new Scene3.NetworkTask(url, info,"cancel");
                        networkTask.execute();
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
                        initControls();
                        info.clear();
                        info.put("account/cancel", bar);
                        System.out.println(info.toString());
                        Scene3.NetworkTask networkTask = new Scene3.NetworkTask(url, info,"cancel");
                        networkTask.execute();
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
