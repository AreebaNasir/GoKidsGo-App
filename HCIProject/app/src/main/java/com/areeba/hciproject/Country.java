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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class Country extends AppCompatActivity {

    TextToSpeech tts;
    TextView t1,t2, t3,t4;
    LinearLayout country1ll,country2ll,country3ll,country4ll;
    FloatingActionButton country_back,country_settings,country_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        country1ll=findViewById(R.id.country1ll);
        country2ll=findViewById(R.id.country2ll);
        country3ll=findViewById(R.id.country3ll);
        country4ll=findViewById(R.id.country4ll);

        t1=findViewById(R.id.cname);
        t2=findViewById(R.id.flag);
        t3=findViewById(R.id.provinces);
        t4=findViewById(R.id.capital);

        country_back=findViewById(R.id.country_back);
        country_settings=findViewById(R.id.country_settings);
        country_music=findViewById(R.id.country_music);

        country_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        t1.setEnabled(true);
                        t2.setEnabled(true);
                        t3.setEnabled(true);
                        t4.setEnabled(true);
                    }
                } else{
                    Log.e("TTS","Initialization failed!!!");
                }

            }
        });

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakT1();
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakT2();
            }
        });
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakT3();
            }
        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakT4();
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



    private void speakT1()
    {
        String txt1=t1.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakT2()
    {
        String txt=t2.getText().toString();
        tts.speak(txt,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakT3()
    {
        String txt1=t3.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakT4()
    {
        String txt=t4.getText().toString();
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