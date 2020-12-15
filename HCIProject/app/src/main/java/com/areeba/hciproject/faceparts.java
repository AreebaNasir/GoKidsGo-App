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

public class faceparts extends AppCompatActivity {
    TextToSpeech tts;
    FloatingActionButton back,set,music;
    TextView eye,ear,nose,lips;
    ImageView barrow1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faceparts);
        back=findViewById(R.id.back);

        barrow1 = findViewById(R.id.barrow1);
        eye=findViewById(R.id.eye);
        ear=findViewById(R.id.ear);
        nose=findViewById(R.id.nose);
        lips=findViewById(R.id.lips);


        barrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(faceparts.this,myself.class);
                startActivity(i1);
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(faceparts.this, nursery_GK.class);
                startActivity(i1);
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
                        eye.setEnabled(true);
                        ear.setEnabled(true);
                        nose.setEnabled(true);
                        lips.setEnabled(true);
                    }
                } else{
                    Log.e("TTS","Initialization failed!!!");
                }

            }
        });

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakEye();
            }
        });
        ear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakEar();
            }
        });
        nose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakNose();
            }
        });
        lips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakLips();
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

    private void speakEye()
    {
        String txt1=eye.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakEar()
    {
        String txt=ear.getText().toString();
        tts.speak(txt,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakNose()
    {
        String txt1=nose.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakLips()
    {
        String txt=lips.getText().toString();
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