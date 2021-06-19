package com.example.wxdiary;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyService extends IntentService {
    private static final String TAG = "MyService";
    public MyService() {
        super("");
    }

    /**
     * @param name
     * @deprecated
     */
    public MyService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int cmd = intent.getIntExtra("cmd", 0);
        switch (cmd){
            case 1:
                List<Note> all = LitePal.findAll(Note.class);
                Log.i(TAG, "onHandleIntent: "+all.size());
                sendBrodcard(1, (Serializable) all);


                break;
            case 2:
              all = LitePal.findAll(Note.class);
                sendBrodcard(2, (Serializable) all);
                break;
            case 3:
                  Serializable[] data= (Serializable[]) intent.getSerializableExtra("data");
                  ArrayList<Note> notes= (ArrayList<Note>) data[0];
                  for(Note note:notes){
                      LitePal.deleteAll(Note.class,"dataid = ?",note.getDataId());
                  }
                sendBrodcard(3,notes.size());
                break;
            case 4:
                all = LitePal.findAll(Note.class);
                sendBrodcard(4, (Serializable) all);
                break;
            case 5:
               data= (Serializable[]) intent.getSerializableExtra("data");
               Note note= (Note) data[0];
               note.setCreateDate(System.currentTimeMillis());
                sendBrodcard(5, (Serializable)  note.save());
                start(getApplicationContext(),4);
                break;
            case 6:
                data= (Serializable[]) intent.getSerializableExtra("data");
                 note= (Note) data[0];
                ContentValues contentValues=new ContentValues();
                contentValues.put("title",note.getTitle());
                contentValues.put("content",note.getContent());
                contentValues.put("updatedate",System.currentTimeMillis());
                LitePal.updateAll(Note.class,contentValues,"dataid = ?",note.getDataId());
                sendBrodcard(6, null);
                start(getApplicationContext(),4);
                break;

        }

    }

    public static void start(Context context,int cmd, Serializable ...obj){
        Intent intent=new Intent(context,MyService.class);
        intent.putExtra("cmd",cmd);
        intent.putExtra("data",obj);
        context.startService(intent);



    }



    public void sendBrodcard(int cmd,Serializable data){
        Intent intent=new Intent("com.serviceData");
        intent.putExtra("cmd",cmd);
        intent.putExtra("data",data);
        sendBroadcast(intent);

    }


}
