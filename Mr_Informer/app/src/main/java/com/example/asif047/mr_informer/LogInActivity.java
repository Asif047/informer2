package com.example.asif047.mr_informer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText emailET;
    private EditText passwordET;

    private Button logInBtn;
    private Button registerBtn;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);



        //new starts
        if(!isConnected(LogInActivity.this))buildDialog(LogInActivity.this).show();
        else
        {

        }


        //new ends


        emailET= (EditText) findViewById(R.id.edit_text_email);
        passwordET= (EditText) findViewById(R.id.edit_text_password);

        logInBtn= (Button) findViewById(R.id.button_log_in);
        registerBtn= (Button) findViewById(R.id.button_register);

        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            //profile activity here



            finish();
            startActivity(new Intent(getApplicationContext(),InformActivity.class));
        }



        logInBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);


    }


    private void userLogin()
    {
        String email=emailET.getText().toString().trim();
        String password=passwordET.getText().toString().trim();


        if(TextUtils.isEmpty(email))
        {

            //email is empty
            Toast.makeText(this,"Please enter the email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            //password is empty
            Toast.makeText(this,"Please enter the password",Toast.LENGTH_SHORT).show();
            return;
        }



        progressDialog.setMessage("Log In user....");
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful())
                        {
                            //start the profile activity

                            finish();
                            startActivity(new Intent(getApplicationContext(),InformActivity.class));
                        }

                        else
                        {
                            Toast.makeText(LogInActivity.this,"Your email or password doesn't match",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onClick(View view) {

        if(view==logInBtn)
        {
            userLogin();
        }

        if(view==registerBtn)
        {

            finish();
            startActivity(new Intent(this,RegisterActivity.class));
        }

    }




    //new starts


    public boolean isConnected(Context context)
    {
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo=cm.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnectedOrConnecting())
        {
            android.net.NetworkInfo wifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile!=null && mobile.isConnectedOrConnecting())||(wifi!=null && wifi.isConnectedOrConnecting()))
                return true;
            else
                return false;

        }
        else
            return false;
    }



    public AlertDialog.Builder buildDialog(Context c)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have mobile data or wifi to access this.Press ok to exit");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        return builder;
    }
    //new ends


    //back button operation starts

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    //back button operation ends




}
