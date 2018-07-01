package com.example.kolawole.ijournal;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout profile_layout;
    private ImageView profile_img;
    private TextView profile_name;
    private TextView profile_email;
    private Button sign_in_btn;
    private SignInButton sign_in_google;

    private static int REQUEST_CODE= 8001;
    private GoogleSignInClient gsc;
    GoogleSignInAccount gsa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_layout= findViewById(R.id.profile_layout);
        profile_img= findViewById(R.id.profile_img);
        profile_name= findViewById(R.id.profile_name);
        profile_email= findViewById(R.id.profile_email);
        sign_in_google= findViewById(R.id.sign_in_google);
        sign_in_btn= findViewById(R.id.sign_in_btn);


        sign_in_google.setSize(SignInButton.SIZE_STANDARD);
        sign_in_google.setOnClickListener(this);
        sign_in_btn.setOnClickListener(this);

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gsa = GoogleSignIn.getLastSignedInAccount(this);
        if (gsa != null) {
            journalSignIn();
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sign_in_google:
                if (checkInternetConnection()){
                    googleSignIn();
                }
                else{
                    Toast.makeText(this, "turn on data connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.sign_in_btn:
                journalSignIn();
                break;
        }

    }

    private void handleResult(Task<GoogleSignInAccount> completedTask){

        try{
            gsa= completedTask.getResult(ApiException.class);
            profile_name.setText(gsa.getDisplayName());
            profile_email.setText(gsa.getEmail());
            //TODO extract the image
            Glide.with(this).load(gsa.getPhotoUrl()).into(profile_img);
            updateUI(true);
        }

        catch (ApiException e){
            updateUI(false);
            e.printStackTrace();
        }

    }

    private void updateUI(boolean isLogin){

        if (isLogin){
            profile_layout.setVisibility(VISIBLE);
            sign_in_google.setVisibility(GONE);
        }

        else{
            profile_layout.setVisibility(GONE);
            sign_in_google.setVisibility(VISIBLE);
        }

    }

    private void  googleSignIn(){
        Intent intent= gsc.getSignInIntent();
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, requestCode, data);
        if (requestCode==REQUEST_CODE){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(task);
        }

    }

    private boolean checkInternetConnection(){
        boolean isConnected=false;
        //TODO put this in try/catch block because of that warning
        try {
            ConnectivityManager cMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            assert cMgr != null;
            NetworkInfo netInfo= cMgr.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                isConnected = true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return isConnected;
    }

    private void journalSignIn(){
        finish();
        Intent intent=new Intent(MainActivity.this, JournalEntries.class);
        startActivity(intent);
    }
}
