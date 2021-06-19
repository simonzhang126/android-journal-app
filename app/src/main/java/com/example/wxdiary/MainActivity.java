package com.example.wxdiary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
private TextView tvNotive;
private EditText etPassword;
private Button btLogin,btCheck,btManage;
private String TAG = "wxwxwx";
private boolean loginFlag = false;
    MyReceiver myReceiver=new MyReceiver();
    private List<Note> noteList;

private class MyReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.serviceData")){
                int cmd = intent.getIntExtra("cmd", 0);
                if(cmd==1){
                    noteList= (List<Note>) intent.getSerializableExtra("data");
                    Log.i(TAG, "onReceive: "+noteList);
                     createListDialog(noteList);
                }else if(cmd==2){
                    noteList= (List<Note>) intent.getSerializableExtra("data");
                    createMangerDialog();
                }else if(cmd==3){
                    int num= (int) intent.getSerializableExtra("data");
                    if (num>0) {
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                    }
                }



            }
    }
}

    private void createMangerDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        Intent it = new Intent();
        it.setClass(MainActivity.this,readWrite.class);

        ab.setTitle("日记列表");
        CharSequence[] data=new String[noteList.size()];
        int i=0;
        for(Note note:noteList){
            data[i]=note.getTitle();
            i++;
        }
        ListView listView=new ListView(this);


        MyAdapter adapter=new MyAdapter();

        listView.setAdapter(adapter);
        ab.setView(listView);




        ab.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                it.putExtra("title","");
//                it.putExtra("content","");
//                it.putExtra("pos",-1);
//                startActivity(it);
                ArrayList<Note> data=new ArrayList<>();
                for(Integer inter:adapter.getMap().keySet()){
                    data.add(noteList.get(inter));

                }
                MyService.start(getApplicationContext(),3,  data);
                dialog.dismiss();
            }
        });
        ab.create().show();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findv();
        bindButton();
        btCheck.setVisibility(View.GONE);
        btManage.setVisibility(View.GONE);
        IntentFilter filter=new IntentFilter("com.serviceData");
        registerReceiver(myReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void bindButton() {
        btLogin.setOnClickListener(this);
        btCheck.setOnClickListener(this);
        btManage.setOnClickListener(this);
    }

    private void findv() {
        tvNotive = (TextView)findViewById(R.id.tvNotice);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btLogin = (Button)findViewById(R.id.btLogin);
        btCheck = (Button)findViewById(R.id.btCheck);
        btManage = (Button)findViewById(R.id.btManage);
    }

    @Override
    public void onClick(View v) {
        if (loginFlag==false) {
            if (etPassword.getText().toString().equals("1234")) {
                tvNotive.setText("Welcome!");
                loginFlag = true;
                etPassword.setVisibility(View.GONE);
                btLogin.setVisibility(View.GONE);
                btCheck.setVisibility(View.VISIBLE);
                btManage.setVisibility(View.VISIBLE);
            }
        }else{
            if (v.getId() == R.id.btCheck) {
//                try {
//                    readData();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                createListDialog();
                MyService.start(getApplicationContext(),1);

            }
            if (v.getId() == R.id.btManage) {
                MyService.start(getApplicationContext(),2);
            }
        }
  }






    private void createListDialog(List<Note> noteList) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        Intent it = new Intent();
        it.setClass(MainActivity.this,readWrite.class);

        ab.setTitle("Please choose");
        CharSequence[] data=new String[noteList.size()];
        int i=0;
        for(Note note:noteList){
            data[i]=note.getTitle();
            i++;
        }



        ab.setItems(data
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                it.putExtra("title",noteList.get(which).getTitle());
                it.putExtra("content",noteList.get(which).getContent());
                it.putExtra("pos",which);
                startActivity(it);
            }
        });
        ab.setPositiveButton("add new", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                it.putExtra("title","");
                it.putExtra("content","");
                it.putExtra("pos",-1);
                startActivity(it);
            }
        });
        ab.create().show();
    }

    private void createMultiChioceDialog() {
    }



  class  MyAdapter extends   BaseAdapter {
        private Map<Integer,Boolean> map=new HashMap<>();

        public Map<Integer, Boolean> getMap() {
            return map;
        }

        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public Object getItem(int i) {
            return noteList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return noteList.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyViewHoler myViewHoler;
            if(view==null){
                view=new CheckBox(viewGroup.getContext());
                myViewHoler=new MyViewHoler();
                myViewHoler.checkBox= (CheckBox) view;
                view.setTag(myViewHoler);
            }else {
                myViewHoler= (MyViewHoler) view.getTag();
            }
            myViewHoler.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        map.put(i,true);
                    }    else {
                        map.put(i,false);
                    }
                }
            });
            Boolean aBoolean = map.get(i);
            if(aBoolean!=null){
                myViewHoler.checkBox.setChecked(aBoolean);
            }else {
                myViewHoler.checkBox.setChecked(false);
            }

            myViewHoler.checkBox.setText(noteList.get(i).getTitle());




            return view;
        }


        class MyViewHoler{
            CheckBox checkBox;

        }
    };
}