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

public class FragmentMenu extends Fragment {
    View view;                  // view
    private TextView menu;      // Fragment 내의 메뉴란
    String url =  URLSetting.getURL();   // 서버 URL
    ContentValues info = new ContentValues();
    String Storenum="";     // 매장번호

    public FragmentMenu() {
    }
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof Scene2){
            Storenum = ((Scene2) getActivity()).getData();   // Scene2 로부터 매장번호를 넘겨 받는다.
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.menu_fragment,container,false);
        }
        if(menu == null){
            menu = (TextView) view.findViewById(R.id.menu);
        }
        info.put("store",Storenum);
        NetworkTask networkTask = new NetworkTask(url,info);  // 매장 정보를 통해 서버로부터 메뉴를 전달 받는다.
        networkTask.execute();
        return view;
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues pageParsed2;

        public NetworkTask(String url, ContentValues pageParsed2) {

            this.url = url;
            this.pageParsed2 = pageParsed2;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String result;          // 요청 결과를 저장할 변수
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, pageParsed2);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력
            super.onPostExecute(s);
            StringBuffer sb = new StringBuffer();
            sb.append("[").append(s).append("]");
            String text = "";
            try{
            JSONArray jArray2 = new JSONArray(sb.toString());
            for(int i=0;i<jArray2.length();i++) {           //json 파싱
                JSONObject jObject2 = (JSONObject)jArray2.get(i);
                String M = (String) jObject2.optString("menu");

                text = M.replace("/","\n\n");
            }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            menu.setText(text);
        }
    }
}
