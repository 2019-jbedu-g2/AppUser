package com.example.teamproject;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RequestHttpURLConnection {

    public String request1(String _url, ContentValues _params){

        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        // 보낼 데이터가 없으면 파라미터를 비운다.
        if (_params == null) {
            sbParams.append("");

            // 보낼 데이터가 있으면 파라미터를 채운다.
        }else {
            // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            // 파라미터 키와 값.
            String key;
            String value;

            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                // 파라미터가 두개 이상일때, 파라미터 사이에 /를 붙인다.
                if (isAnd)
                    sbParams.append("/");

                sbParams.append(key).append("/").append(value);

                // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }

         // HttpURLConnection을 통해 web의 데이터를 가져온다.
        try{
            _url = _url + sbParams.toString();
            Log.d(TAG,_url);
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

//           [2-1]. urlConn 설정.
            urlConn.setReadTimeout(1000);   // 읽어 들일 시 연결 시간, 서버를 보호하기위한 설정
            urlConn.setConnectTimeout(1000);  // 서버 접속시 연결 시간
            urlConn.setRequestMethod("GET"); // URL 요청에 대한 메소드 설정 : GET
            urlConn.setDoInput(true);
//            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Accept", "application/json");
            urlConn.setRequestProperty("Context_Type", "application/json");
//            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
//            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

//            [2-2]. parameter 전달 및 데이터 읽어오기.
//            String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
//            Log.d(TAG,url.toString());
//            OutputStream os = urlConn.getOutputStream();
//            os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
//            os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
//            os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";
            String pageParsed1 = "";
            String pageParsed2 = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null){
                page += line;
            }
            page = "["+page+"]";
            JSONArray jArray = new JSONArray(page);
            for(int i=0;i<jArray.length();i++) {
                JSONObject jObject1 = (JSONObject)jArray.get(0);
                String Num = (String) jObject1.get("storenum");
                String Name = (String) jObject1.get("storename");
                String TEL = (String) jObject1.get("category");
                String Intro = (String) jObject1.get("intro");
                String Inform = (String) jObject1.get("inform");
                String Waiting = (String) jObject1.get("latencytime");
//                JSONObject jObject2 = (JSONObject)jArray.get(i);
//                String M1 = (String) jObject1.optString("M1");
//                String P1 = (String) jObject1.optString("P1");
//                String M2 = (String) jObject1.optString("M2");
//                String P2 = (String) jObject1.optString("P2");
//                String M3 = (String) jObject1.optString("M3");
//                String P3 = (String) jObject1.optString("P3");
//                String M4 = (String) jObject1.optString("M4");
//                String P4 = (String) jObject1.optString("P4");
//                String M5 = (String) jObject1.optString("M5");
//                String P5 = (String) jObject1.optString("P5");
//                String M6 = (String) jObject1.optString("M6");
//                String P6 = (String) jObject1.optString("P6");
                Inform = Inform.replace("/","\n");

                pageParsed1 = "대기팀 : " + Waiting + "\n\n" + "StoreName : " + Name + "\n\n" + "CATEGORY : " + TEL + "\n\n" + "가게 소개\n " +
                        Intro + "\n\n" + "가게정보\n" + Inform;
//                pageParsed2 = M1 + "\t\t\t\t" + P1 + "\n\n" + M2 + "\t\t\t\t" + P2 + "\n\n" +
//                              M3 + "\t\t\t\t" + P3 + "\n\n" + M4 + "\t\t\t\t" + P4 + "\n\n" +
//                              M5 + "\t\t\t\t" + P5 + "\n\n" + M6 + "\t\t\t\t" + P6;
            }

            return pageParsed1;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }

    public String request2(String _url, ContentValues _params) {
        HttpURLConnection urlConn = null;
        StringBuffer sbParams = new StringBuffer();
        if (_params == null) {
            sbParams.append("");
        }else {
            boolean isAnd = false;
            String key;
            String value;
            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();
                if (isAnd)
                    sbParams.append("/");

                sbParams.append(key).append("/").append(value);
                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }
        try{
            _url = _url + sbParams.toString();
            Log.d(TAG,_url);
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(1000);
            urlConn.setConnectTimeout(1000);
            urlConn.setRequestMethod("GET");
            urlConn.setDoInput(true);
            urlConn.setRequestProperty("Accept", "application/json");
            urlConn.setRequestProperty("Context_Type", "application/json");

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            String line;
            String page = "";
            String pageParsed1 = "";
            String pageParsed2 = "";

            while ((line = reader2.readLine()) != null){
                page += line;
            }
            page = "["+page+"]";
            JSONArray jArray2 = new JSONArray(page);
            for(int i=0;i<jArray2.length();i++) {
//                JSONObject jObject2 = (JSONObject)jArray.get(i);
//                String Name = (String) jObject2.get("Name");
//                String TEL = (String) jObject2.get("TEL");
//                String Open = (String) jObject2.get("Open");
//                String Close = (String) jObject2.get("Close");
                JSONObject jObject2 = (JSONObject)jArray2.get(i);
                String M = (String) jObject2.optString("menu");


//                String P1 = (String) jObject2.optString("P1");
//                String M2 = (String) jObject2.optString("M2");
//                String P2 = (String) jObject2.optString("P2");
//                String M3 = (String) jObject2.optString("M3");
//                String P3 = (String) jObject2.optString("P3");
//                String M4 = (String) jObject2.optString("M4");
//                String P4 = (String) jObject2.optString("P4");
//                String M5 = (String) jObject2.optString("M5");
//                String P5 = (String) jObject2.optString("P5");
//                String M6 = (String) jObject2.optString("M6");
//                String P6 = (String) jObject2.optString("P6");

//                pageParsed1 = Name + "\n\n" + "TEL : " + TEL + "\n\n" + "Open  " +
//                     Open + " / " + "Close  " + Close + "\n\n" + "예상 대기시간 : " + Waiting;
                pageParsed2 = M.replace("/","\n\n");
            }

            return pageParsed2;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }

    public String request3(String _url, ContentValues _params) {
        HttpURLConnection urlConn = null;
        StringBuffer sbParams = new StringBuffer();
        if (_params == null) {
            sbParams.append("");
        }else {
            boolean isAnd = false;
            String key;
            String value;
            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                if(!(parameter.getKey() == "zzzzzzzz")) {
                    key = parameter.getKey();
                } else
                    key="";
                value = parameter.getValue().toString();
                if (isAnd)
                    sbParams.append("");

                sbParams.append(key).append("/").append(value);
                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }
        try{
            _url = _url + sbParams.toString();
            Log.d(TAG,_url);
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(1000);
            urlConn.setConnectTimeout(1000);
            urlConn.setRequestMethod("GET");
            urlConn.setDoInput(true);
            urlConn.setRequestProperty("Accept", "application/json");
            urlConn.setRequestProperty("Context_Type", "application/json");

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            String line;
            String page = "";

            while ((line = reader2.readLine()) != null){
                page += line;
            }
            return page;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }
}


