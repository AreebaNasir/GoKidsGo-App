package com.areeba.hciproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Nursery_English extends AppCompatActivity {
    Button A,B,C,D,E,F;
    FloatingActionButton back,set,music;
    ImageView cloud, pooh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery__english);

        back = findViewById(R.id.back);
        set = findViewById(R.id.settings);
        music = findViewById(R.id.music);

        A = findViewById(R.id.one);
        B = findViewById(R.id.two);
        C = findViewById(R.id.three);

        cloud = findViewById(R.id.redcloud);
        pooh = findViewById(R.id.pp);

        //One screen
        A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 =  new Intent(Nursery_English.this,A.class);
                startActivity(i1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        //Two screen
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 =  new Intent(Nursery_English.this,B.class);
                startActivity(i1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        //Three screen
        C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 =  new Intent(Nursery_English.this,C.class);
                startActivity(i1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        //Back to subject screen
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 =  new Intent(Nursery_English.this,NurserySubjects.class);
                startActivity(i1);
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        //*****************************************************************************//
        //BIND Music Service
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);
        //*****************************************************************************//
    }

    //Bind/Unbind music service
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Detect idle screen
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //UNBIND music service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

    }
}