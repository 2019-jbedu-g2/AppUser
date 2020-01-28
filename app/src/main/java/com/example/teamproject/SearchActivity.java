package com.example.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ImageButton SearchB, MyLocationB;
    private TextView SearchTV;
    private List<String> list;
    private ListView listView;
    private SearchAdapter adapter;
    private ArrayList<String> arraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        list = new ArrayList<String>();
        settingList();
        arraylist = new ArrayList<String>();
        arraylist.addAll(list);
        adapter = new SearchAdapter(list, this);
        listView.setAdapter(adapter);
        SearchTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = SearchTV.getText().toString().trim();
                search(text);
            }
        });
        list.clear();
        SearchB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view){
                    list.clear();
                    SearchMap();
                }
        });

        SearchTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                list.clear();
                switch(i){
                    case EditorInfo.IME_ACTION_SEARCH:
                        SearchMap();
                            break;
                        default:
                            SearchMap();
                            break;
                }
                return false;
            }
        });
        MyLocationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("SearchLocate","MyLocate");
                            startActivity(intent);
                        }
                });
        }
    });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String ClickedItem = adapter.getItem(i).toString();
                SearchTV.setText(ClickedItem);
                view.clearFocus();
                list.clear();
            }
        });
    }
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof TextView){
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())){
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
    public void SearchMap(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(SearchTV.getText().toString().equals("")||SearchTV.getText().equals(null)) {
                    Toast.makeText(getApplicationContext(), "검색창이 비어있습니다.", Toast.LENGTH_SHORT).show();
                } else{
                    String Locate = SearchTV.getText().toString().trim();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("SearchLocate",Locate);
                    startActivity(intent);
                }
            }
        });
    }
    public void init() {
        if(SearchB == null){
            SearchB = (ImageButton) findViewById(R.id.SearchButton);
        }
        if(MyLocationB == null) {
            MyLocationB = (ImageButton) findViewById(R.id.MyLocationButton);
        }
        if(SearchTV == null){
            SearchTV = (TextView) findViewById(R.id.SearchInput);
        }
        if(listView == null){
            listView = (ListView) findViewById(R.id.listView);
        }
    }
    public void search(String charText){
        list.clear();
        if(charText.length() == 0){
            list.addAll(arraylist);
        }else{
            for(int i = 0; i < arraylist.size(); i++){
                if(arraylist.get(i).toLowerCase().contains(charText)){
                    list.add(arraylist.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void settingList(){
        list.add("건대");
        list.add("건대입구");
        list.add("건국대학교");
        list.add("홍대");
        list.add("홍대입구");
        list.add("홍익대학교");
        list.add("hongik");
        list.add("konkuk");

    }

}
