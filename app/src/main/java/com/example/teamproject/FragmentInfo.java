package com.example.teamproject;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentInfo extends Fragment {
    View view;                      // View
    private TextView tv_outPut;   // Fragment내의 정보 출력창
    String url =  URLSetting.getURL();  // 서버접속 URL
    ContentValues info = new ContentValues();
    String Storenum="";             // 매장번호

    public FragmentInfo() {
    }
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof Scene2){
            Storenum = ((Scene2) getActivity()).getData();      // Scene2 로부터 매장번호를 넘겨 받는다.
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.info_fragment, container, false);
        }
        if (tv_outPut == null){
            tv_outPut = (TextView) view.findViewById(R.id.tv_outPut);
        }
        // AsyncTask를 통해 HttpURLConnection 수행
        info.put("store",Storenum);     // 주소 파라미터 넣음
        NetworkTask networkTask = new NetworkTask(url,info);    //  매장번호를 통해 서버로부터 매장 정보를 받아온다.
        networkTask.execute();
        return view;
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues pageParsed1;

        public NetworkTask(String url, ContentValues pageParsed1) {

            this.url = url;
            this.pageParsed1 = pageParsed1;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String result;          // 요청 결과를 저장할 변수
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url,pageParsed1);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력

            super.onPostExecute(s);
            StringBuffer sb1 = new StringBuffer();
            sb1.append("[").append(s).append("]");
            StringBuffer SB = new StringBuffer();
            try {
                JSONArray jArray = new JSONArray(sb1.toString());       // Json 파싱
                JSONObject jObject1 = (JSONObject) jArray.get(0);
                String Num = (String) jObject1.get("storenum");
                String Name = (String) jObject1.get("storename");
                String TEL = (String) jObject1.get("category");
                String Intro = (String) jObject1.get("intro");
                String Inform = (String) jObject1.get("inform");
                String Waiting = Integer.toString((int) jObject1.get("waitingcount"));
                Inform = Inform.replace("/", "\n");     // 넘겨받은 데이터에서 / 를 줄바꿈으로 바꿈.

                SB.append("대기팀 : ").append(Waiting).append("\n\n가게이름 : ").append(Name).append("\n\n분류 : ").append(TEL).append("\n\n 가게 소개\n").append(Intro).append("\n\n가게정보\n").append(Inform);
            }catch (JSONException e) {
                    e.printStackTrace();
                }
            tv_outPut.setText(SB.toString());
        }
    }
}
