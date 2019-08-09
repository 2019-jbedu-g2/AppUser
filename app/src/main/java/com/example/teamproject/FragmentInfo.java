package com.example.teamproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentInfo extends Fragment {
    View view;
    private TextView tv_outPut;
    String url =  "http://192.168.0.8:8000/";
    ContentValues info = new ContentValues();
    String Storenum="";

    public FragmentInfo() {
    }
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof Scene2){
            Storenum = ((Scene2) getActivity()).getData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.info_fragment,container,false);
        // AsyncTask를 통해 HttpURLConnection 수행
        info.put("store",Storenum);
        NetworkTask networkTask = new NetworkTask(url,info);
        tv_outPut = (TextView) view.findViewById(R.id.tv_outPut);
      //  NetworkTask networkTask = new NetworkTask(url, null);
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
            String page = "["+s+"]";
            String text = "";
            try {
                JSONArray jArray = new JSONArray(page);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject1 = (JSONObject) jArray.get(0);
                    String Num = (String) jObject1.get("storenum");
                    String Name = (String) jObject1.get("storename");
                    String TEL = (String) jObject1.get("category");
                    String Intro = (String) jObject1.get("intro");
                    String Inform = (String) jObject1.get("inform");
                    String Waiting = Integer.toString((int) jObject1.get("waitingcount"));
                    Inform = Inform.replace("/", "\n");

                   text = "대기팀 : " + Waiting + "\n\n" + "StoreName : " + Name + "\n\n" + "CATEGORY : " + TEL + "\n\n" + "가게 소개\n " +
                            Intro + "\n\n" + "가게정보\n" + Inform;
                }
            }catch (JSONException e) {
                    e.printStackTrace();
                }
            tv_outPut.setText(text);
        }
    }
}
