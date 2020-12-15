package com.areeba.hciproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Class extends AppCompatActivity {
    ImageView poohstand,rays, cloud,myprofile;
    Button nur,prep;
    TextView appname1,appname2,appname3,sen;
    FirebaseDatabase database;
    DatabaseReference reference;
    Contacts currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        appname1 = findViewById(R.id.appname1);
        appname2 = findViewById(R.id.appname2);
        appname3 = findViewById(R.id.appname3);

        nur = findViewById(R.id.nursery);
        prep = findViewById(R.id.prep);

        rays = findViewById(R.id.sunrays);
        sen = findViewById(R.id.sen2);
        cloud = findViewById(R.id.redcloud);
        poohstand = findViewById(R.id.standingpooh);
        myprofile = findViewById(R.id.myprofile);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("contacts");

        //Now lets add listener to get any new contact added to the firebase
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Show the logged in user in the search view:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String id = user.getUid();

                Contacts c = dataSnapshot.getValue(Contacts.class);
                String idd = dataSnapshot.getKey();
                Log.d(c.getFirstname(), "onChildAdded: ********************");
                Log.d(id, "onChildAdded: ********************");
                Log.d(idd, "onChildAdded: ********************");
                if(id.equals(idd)) {
                    currentuser = c;
                    Picasso.get().load(currentuser.getDp()).into(myprofile);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(Class.this,NurserySubjects.class);
                startActivity(i1);
            }
        });

        prep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(Class.this,PrepSubjects.class);
                startActivity(i2);
            }
        });

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Class.this,MyProfile.class);
                i.putExtra("info",currentuser);
                startActivity(i);
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