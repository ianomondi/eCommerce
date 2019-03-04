package com.example.ian.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.SnackBar;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = (Button) findViewById(R.id.register_btn);

        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);

        loadingProgress = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            createAccount();

            }

            private void createAccount() {
                String username = InputName.getText().toString();
                String phone = InputPhoneNumber.getText().toString();
                String password = InputPassword.getText().toString();

                //Toast.makeText(getApplicationContext(),phone + " -- " + password+" -- " + username, Toast.LENGTH_LONG).show();

                if (TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this, "Please fill the username field", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phone)){
                    Toast.makeText(RegisterActivity.this, "Please fill the phone field", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Please fill the password field", Toast.LENGTH_SHORT).show();
                }
                else {
                        loadingProgress.setTitle("Creating Account");
                        loadingProgress.setMessage("Please wait as we are setting you up");
                        loadingProgress.setCanceledOnTouchOutside(false);
                        loadingProgress.show();


                        validatePhoneNumber(username, phone, password);
                }
            }

            private void validatePhoneNumber(final String username, final String phone, final String password) {
                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference();

                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!(dataSnapshot.child("Users").child(phone).exists())){
                            HashMap<String, Object> UserDataMap = new HashMap<>();
                            UserDataMap.put("username", username);
                            UserDataMap.put("phone", phone);
                            UserDataMap.put("password", password);

                            RootRef.child("Users").child(phone).updateChildren(UserDataMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Account Created Successfully, Proceed to login", Toast.LENGTH_SHORT).show();
                                                loadingProgress.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });


                        }else{
                            Toast.makeText(RegisterActivity.this, "This Phone number" + phone + "has already been registered", Toast.LENGTH_SHORT).show();
                            loadingProgress.dismiss();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        });
    }
}
