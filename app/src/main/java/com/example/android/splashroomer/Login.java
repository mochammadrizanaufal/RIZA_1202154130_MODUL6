package com.example.android.splashroomer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.android.splashroomer.model.User;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();
    //declare variable
    EditText mEmail, mPassword;
    Button mSignIn, mSignUp;
    String email, password;
    //declare instance
    FirebaseAuth mAuth;
    DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize instance
        mAuth = FirebaseAuth.getInstance();
        //bind data
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.txt_pass);
        mSignIn = findViewById(R.id.btn_login);
        mSignUp = findViewById(R.id.btn_signup);

        databaseUser = FirebaseDatabase.getInstance().getReference(MainActivity.table_3);
        //set on click button signin
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mengambil isi field form
                getTextField();
                if (validateForm()){
                    //panggil method login
                    login(email, password);
                }else {
                    //toast pesan
                    toastMessage("Please fill the form field");
                }
            }
        });
        //set on click button signup
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mengambil isi field form
                getTextField();
                if (validateForm()){
                    signUp(email, password);
                }else {
                    toastMessage("Please fill the form field");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mengambil current user aktif
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //cek current user ada atau tidak
        if (currentUser != null){
            moveToMain();
        }
    }
    //method signup
    private void signUp(final String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //jika auth berhasil
                        if (task.isSuccessful()){
                            String id = mAuth.getCurrentUser().getUid();
                            String[] username = email.split("@");
                            User user = new User(id, username[0], email);
                            databaseUser.child(id).setValue(user);
                            moveToMain();
                        }else {
                            //toast pesan
                            toastMessage("Something went wrong");
                        }
                    }
                }
        );
    }
    //method login
    private void login(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            moveToMain();
                        }else {
                            //toast pesan
                            toastMessage("Account not yet registered");
                        }
                    }
                }
        );
    }

    private boolean validateForm(){
        //cek apakah field email atau password kosong
        if (email.isEmpty() || password.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    //method get text
    private void getTextField(){
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
    }
    //method intent to main activity
    private void moveToMain(){
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    //method toast pesan
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
