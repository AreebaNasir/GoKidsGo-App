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

public class Vegetables extends AppCompatActivity {
    TextToSpeech tts;
    TextView carrot,eggplant, onion,potato;
    LinearLayout veg1ll,veg2ll,veg3ll,veg4ll;
    FloatingActionButton vegs_back,vegs_settings,vegs_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetables);

        veg1ll=findViewById(R.id.veg1ll);
        veg2ll=findViewById(R.id.veg2ll);
        veg3ll=findViewById(R.id.veg3ll);
        veg4ll=findViewById(R.id.veg4ll);
        carrot=findViewById(R.id.carrot);
        eggplant=findViewById(R.id.ep);
        onion=findViewById(R.id.onion);
        potato=findViewById(R.id.potato);

        vegs_back=findViewById(R.id.vegs_back);
        vegs_settings=findViewById(R.id.vegs_settings);
        vegs_music=findViewById(R.id.vegs_music);

        vegs_back.setOnClickListener(new View.OnClickListener() {
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
                        carrot.setEnabled(true);
                        onion.setEnabled(true);
                        eggplant.setEnabled(true);
                        potato.setEnabled(true);
                    }
                } else{
                    Log.e("TTS","Initialization failed!!!");
                }

            }
        });

        carrot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakCarrot();
            }
        });
        onion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOnion();
            }
        });
        eggplant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakEP();
            }
        });
        potato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakPotato();
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


    private void speakCarrot()
    {
        String txt1=carrot.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakEP()
    {
        String txt=eggplant.getText().toString();
        tts.speak(txt,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOnion()
    {
        String txt1=onion.getText().toString();
        tts.speak(txt1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakPotato()
    {
        String txt=potato.getText().toString();
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