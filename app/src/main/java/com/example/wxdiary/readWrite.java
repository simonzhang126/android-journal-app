package com.example.wxdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class readWrite extends AppCompatActivity implements View.OnClickListener{
private EditText etTitle,etReadWrite;
private Button btClear,btSave,bNext,bPre;
    MyReceiver myReceiver=new MyReceiver();
    private List<Note> noteList;
    Note curNote;
    private int curPos;

    private class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.serviceData")){
                int cmd = intent.getIntExtra("cmd", 0);
                if(cmd==4){
                    noteList= (List<Note>) intent.getSerializableExtra("data");
                    if(curPos!=-1){
                        setData();
                    }
                }else if(cmd==5){
                    curNote=null;
                    setData();
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_write);

        findv();
        bindButton();
        Intent it = this.getIntent();
        etTitle.setText(it.getStringExtra("title"));
        etReadWrite.setText(it.getStringExtra("content"));

        curPos=it.getIntExtra("pos",-1);
        if(curPos==-1){
            bNext.setVisibility(View.GONE);
            bPre.setVisibility(View.GONE);
        }
        registerReceiver(myReceiver,new IntentFilter("com.serviceData"));
        MyService.start(getApplicationContext(),4,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void findv() {
        etReadWrite = (EditText)findViewById(R.id.etReadWrite);
        etTitle = (EditText)findViewById(R.id.etTitle);
        bPre=findViewById(R.id.pre_btn);
        bNext=findViewById(R.id.next_btn);
        btClear = (Button)findViewById(R.id.btClear);
        btSave = (Button)findViewById(R.id.btSave);
        bPre.setOnClickListener(this);
    }

    private void bindButton() {
        btClear.setOnClickListener(this);
        btSave.setOnClickListener(this);
        bPre.setOnClickListener(this);
        bNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btClear) {
            etTitle.setText("");
            etReadWrite.setText("");
        }
        else  if (v.getId() == R.id.btSave) {
            String t=etTitle.getText().toString();
            String c=etReadWrite.getText().toString();
            if(TextUtils.isEmpty(t)||TextUtils.isEmpty(c)){
                Toast.makeText(getApplicationContext(), "请输入内容", Toast.LENGTH_LONG).show();

                return;
            }
            if(curNote==null){

                curNote=new Note();
                curNote.setDataId(System.currentTimeMillis()+"");
                curNote.setTitle(t);
                curNote.setContent(c);
                MyService.start(getApplicationContext(),5,curNote);
            }else {
                curNote.setTitle(t);
                curNote.setContent(c);
                MyService.start(getApplicationContext(),6,curNote);
            }


        }else if (v.getId() == R.id.pre_btn) {
            if(curPos>0){
                curPos--;
                setData();
            }


        }else if (v.getId() == R.id.next_btn) {
                if(curPos<noteList.size()-1){
                    curPos++;
                    setData();
                }

        }
    }

    private void setData() {
        if(curPos>=0&&curPos<noteList.size()){
            curNote=noteList.get(curPos);
            etTitle.setText(curNote.getTitle());
            etReadWrite.setText(curNote.getContent());

        }else {
            etTitle.setText("");
            etReadWrite.setText("");
        }

    }


}