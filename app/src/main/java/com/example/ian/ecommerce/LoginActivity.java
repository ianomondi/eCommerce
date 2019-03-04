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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ian.ecommerce.Model.Users;
import com.example.ian.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.drawable.CheckBoxDrawable;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText InputPhoneNumber,InputPassword;
    private ProgressDialog loadingProgress;
    private TextView adminLink, notAdminLink;

    private String parentDbName = "Users";

    private CheckBox chkRememberMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_btn);

        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        loadingProgress = new ProgressDialog(this);

        chkRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);

        Paper.init(this);

        adminLink = (TextView) findViewById(R.id.admin_link);
        notAdminLink = (TextView) findViewById(R.id.not_admin_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }

            private void LoginUser() {

                String phone = InputPhoneNumber.getText().toString();
                String password = InputPassword.getText().toString();

                 if (TextUtils.isEmpty(phone)){
                    Toast.makeText(LoginActivity.this, "Please fill the phone field", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Please fill the password field", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingProgress.setTitle("Logging Account");
                    loadingProgress.setMessage("Please wait as we are setting you up");
                    loadingProgress.setCanceledOnTouchOutside(false);
                    loadingProgress.show();


                    GrantAccess(phone, password);
                }
            }

            private void GrantAccess(final String phone, final String password) {
                if (chkRememberMe.isChecked()){
                    Paper.book().write(Prevalent.userPhoneKey, phone);
                    Paper.book().write(Prevalent.userPasswordKey, password);
                }

                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference();

                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(parentDbName).child(phone).exists()){
                            Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                            if (userData.getPhone().equals(phone)){
                               if (userData.getPassword().equals(password)){
                                   if (parentDbName.equals("Admins")){
                                       Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                       loadingProgress.dismiss();

                                       Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                       startActivity(intent);
                                   }else if (parentDbName.equals("Users")){
                                           Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                           loadingProgress.dismiss();

                                           Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                           Prevalent.currentOnlineUsers= userData;
                                           startActivity(intent);
                                       }
                                   }
                               else
                               {
                                   loadingProgress.dismiss();
                                   Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                               }
                               }
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Account with this" + phone + "does not exist", Toast.LENGTH_SHORT).show();
                                loadingProgress.dismiss();
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Admin Login");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }
}
