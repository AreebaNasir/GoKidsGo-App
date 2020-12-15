package com.areeba.hciproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class myself extends AppCompatActivity {
    TextToSpeech tts;
    TextView name,fname,mname,sib;
    FloatingActionButton back,set,music;
    ImageView farrow1,barrow1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);
        back=findViewById(R.id.back);

        farrow1 = findViewById(R.id.farrow1);
        barrow1 = findViewById(R.id.barrow1);
        name=findViewById(R.id.name);
        fname=findViewById(R.id.fname);
        mname=findViewById(R.id.mname);
        sib=findViewById(R.id.siblings);


        farrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(myself.this,faceparts.class);
                startActivity(i1);
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(myself.this, nursery_GK.class);
                startActivity(i1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        barrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(myself.this,colors.class);
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });


        //*****************************************************************************//
        //For text to speech
        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS)
                {
                    int res=tts.setLanguage(Locale.US);
                    if(res==TextToSpeech.LANG_MISSING_DATA || res==TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS","Language not supported");
                    }
                    else
                    {
                        name.setEnabled(true);
                        fname.setEnabled(true);
                        mname.setEnabled(true);
                        sib.setEnabled(true);
                    }
                } else{
                    Log.e("TTS","Initialization failed!!!");
                }

            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakName();
            }
        });
        fname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakFname();
            }
        });
        mname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakMname();
            }
        });
        sib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakSibling();
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

    private void speakName()
    {
        String txt1=name.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakFname()
    {
        String txt=fname.getText().toString();
        tts.speak(txt,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakMname()
    {
        String txt1=mname.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakSibling()
    {
        String txt=sib.getText().toString();
        tts.speak(txt,TextToSpeech.QUEUE_FLUSH,null);
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
        if(tts!=null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();

        //UNBIND music service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

    }
}