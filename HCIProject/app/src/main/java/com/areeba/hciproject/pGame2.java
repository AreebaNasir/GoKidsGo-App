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
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class pGame2 extends AppCompatActivity {

    FloatingActionButton pg2back,pg2settings,pg2music;
    Button pa,pb,pc,pd,pe,pf,pg,ph,pi,pj;
    EditText g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_game2);

        pa = findViewById(R.id.pa);
        pb = findViewById(R.id.pb);
        pc = findViewById(R.id.pc);
        pd = findViewById(R.id.pd);
        pe = findViewById(R.id.pe);
        pf = findViewById(R.id.pf);
        pg = findViewById(R.id.pg);
        ph = findViewById(R.id.ph);
        pi = findViewById(R.id.pi);
        pj = findViewById(R.id.pj);
        pg2back = findViewById(R.id.pg2back);
        pg2settings = findViewById(R.id.pg2settings);
        pg2music = findViewById(R.id.pg2music);

        g= findViewById(R.id.g);

        pg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g.setText("G");
                Intent i=new Intent(pGame2.this,Win.class);
                startActivity(i);
                finish();
            }
        });

        pg2back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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