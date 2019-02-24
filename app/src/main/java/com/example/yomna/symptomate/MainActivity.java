package com.example.yomna.symptomate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.database.DataSetObserver;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "ChatActivity";
    ChatArrayAdapter chatArrayAdapter;
    EditText chatText;
    ListView chatList;
    Button sendbtn, recbtn, hist_btn, near_btn;
    Button begin;
    String  Authorization="";
    String Recieved="";
    Context mContext=this;
    TextToSpeech t1;
    int contentview;
    private int MY_PERMISSIONS_RECORD_AUDIO = 1001;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    boolean flag = false, speechFlag = true;
    //boolean typing=true;
    LocationManager locationManager;
    String provider;
    boolean flag_name = true;
    public static String my_name="", my_symp="", my_diag="", my_lat, my_long, t_date="";
    public static double lat_d = 18.496320, long_d = 73.804450;
    public static String android_id;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startingv2);

        checkLocationPermission();

        //provider = locationManager.getBestProvider(new Criteria(), false);

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //Toast.makeText(mContext, android_id, Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(android_id);

        //Toast.makeText(mContext, Locale.getDefault().toString(), Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
        }

        Calendar calendar = Calendar.getInstance();

        int _year = calendar.get(Calendar.YEAR);
        int _month = calendar.get(Calendar.MONTH)+1;
        int _day = calendar.get(Calendar.DAY_OF_MONTH);

        String day, mon, yea;

        if(_day/10 == 0)
            day = "0" + _day;
        else
            day = _day + "";

        if(_month/10 == 0)
            mon = "0" + _month;
        else
            mon = _month + "";

        yea = _year + "";

        t_date = day + "-" + mon + "-" + yea;

        contentview=R.layout.startingv2;
        begin = findViewById(R.id.begin);
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
                }
                else {
                    if(flag) {
                        setContentView(R.layout.activity_main);
                        contentview=R.layout.activity_main;
                        int v=R.layout.activity_main;
                        chatList = findViewById(R.id.chatlist);
                        chatText = findViewById(R.id.msgtyped);
                        changeview();
                        chatArrayAdapter = new ChatArrayAdapter(mContext, new ArrayList<ChatMessage>(),chatList);
                        chatList.setAdapter(chatArrayAdapter);
                        chatArrayAdapter.add(new ChatMessage(1,"typing..."));
                        //  chatArrayAdapter.getItem(0).setIsMine(1);
                        new Welcome(mContext).execute();
                    }
                    else {
                        Toast.makeText(mContext, "Check your internet connection and try again!!!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    flag = true;
                }
            }
        });

        t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                //if (speechFlag)
                    //promptSpeechInput();

            }

            @Override
            public void onError(String s) {

            }
        });
    }

    public void addToDatabse() {

        Random rand = new Random();

        double n = rand.nextInt(10) + 1;
        n = n/150.0;
        //n = 0;

        my_lat = Double.toString((double) (18.496320+n));
        my_long = Double.toString((double) (73.804450+n));

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference newMovie = mDatabase.push();

                newMovie.child("Name").setValue(my_name);
                newMovie.child("Date").setValue(t_date);
                newMovie.child("Symptoms").setValue(my_symp);
                newMovie.child("Diagnosis").setValue(my_diag);
                newMovie.child("Latitude").setValue(my_lat);
                newMovie.child("Longitude").setValue(my_long);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()/*"hin"*/);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hin");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "mr_");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0).length() != 0) {
                        if (result.get(0).equals("mail"))
                            result.set(0, "male");
                        if (result.get(0).equals("yes go on"))
                            result.set(0, "Yes, go on");

                        chatText.setText(result.get(0));
                        sendMessage();
                    }
                    else {
                        Toast.makeText(mContext, "No input!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }

    }

    public void sendMessage() {
        String message = chatText.getText().toString();
        ChatMessage chatMessage = new ChatMessage(0, message);

        if (flag_name) {
            my_name = message;
            flag_name = false;
        }

        chatArrayAdapter.add(chatMessage);

        JSONObject jsn=new JSONObject();
        try {
            jsn.put("message",message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Chat(mContext).execute(Authorization,jsn.toString());
        chatText.setText("");
        chatArrayAdapter.add(new ChatMessage(1,"typing..."));
        chatList.setSelection(chatArrayAdapter.getCount() - 1);
    }

    @Override
    protected void onDestroy() {
        // prevent memory leaks when activity is destroyed
        super.onDestroy();
        t1.shutdown();

    }

    public void changeview(){

        recbtn = findViewById(R.id.rec_btn);
        recbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
                }
                else {
                    promptSpeechInput();
                }
            }
        });

        sendbtn = findViewById(R.id.send_btn);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendMessage();
            }
        });

        hist_btn = findViewById(R.id.history_btn);
        hist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, History.class));
            }
        });

        near_btn = findViewById(R.id.nearby_btn);
        near_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Nearby.class));
            }
        });


    }
    public void ServerWelcome(JSONObject res){
        try {
            //Toast.makeText(mContext, res.toString(), Toast.LENGTH_SHORT).show();
            Recieved=(res.getString("message"));
            Authorization=res.getString("uuid");
            chatArrayAdapter.remove((ChatMessage) chatArrayAdapter.chatList.getItemAtPosition(chatArrayAdapter.getCount()-1));
            chatArrayAdapter.notifyDataSetChanged();

            ChatMessage chatMessage = new ChatMessage(1, Recieved);
            chatArrayAdapter.add(chatMessage);
            chatList.setSelection(chatArrayAdapter.getCount() - 1);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

            String toSpeak = Recieved;
           // Toast.makeText(mContext, Recieved, Toast.LENGTH_SHORT).show();
            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);


        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    //  this method recieves the server response
    public void ServerChat(JSONObject res){



        try {
            Recieved=(res.getString("message"));
            //Log.d("nfndskjfnjkdsn   ",Recieved);
           chatArrayAdapter.remove((ChatMessage) chatArrayAdapter.chatList.getItemAtPosition(chatArrayAdapter.getCount()-1));
           chatArrayAdapter.notifyDataSetChanged();

            ChatMessage chatMessage = new ChatMessage(1, Recieved);
            chatArrayAdapter.add(chatMessage);
            chatList.setSelection(chatArrayAdapter.getCount() - 1);

            //Toast.makeText(mContext, chatArrayAdapter.viewType+"", Toast.LENGTH_SHORT).show();

            if(chatArrayAdapter.viewType == 0) {
                speechFlag = false;
                my_diag = Recieved;
                my_diag = my_diag.replace("conditions", "");
                addToDatabse();
            }
            else {
                my_symp += "\n==>" + Recieved;
            }

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID1");

            String toSpeak = Recieved;
            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        t1.shutdown();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Allow location Access")
                        .setMessage("We need your location to give better suggestions.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

}
