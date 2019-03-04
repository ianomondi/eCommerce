package com.example.ian.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ian.ecommerce.Model.Products;
import com.example.ian.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText txtShipmentName, txtShipmentPhone, txtShipmentAddress, txtShipmentCity;
    private Button confirmShipmentDetailsBtn;
    private String totalamount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);


        txtShipmentName = (EditText) findViewById(R.id.shipment_name);
        txtShipmentPhone = (EditText) findViewById(R.id.shipment_phone);
        txtShipmentAddress = (EditText) findViewById(R.id.shipment_address);
        txtShipmentCity = (EditText) findViewById(R.id.shipment_city);

        confirmShipmentDetailsBtn = (Button) findViewById(R.id.confirm_shipment_details_btn);

        totalamount = getIntent().getStringExtra("Total price");
        Toast.makeText(ConfirmFinalOrderActivity.this, "Total price = Ksh" + totalamount, Toast.LENGTH_LONG).show();

        confirmShipmentDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateOrder();
            }
        });

    }

    private void ValidateOrder() {

        if (TextUtils.isEmpty(txtShipmentName.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please Enter your name", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(txtShipmentAddress.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please Enter your phone numbner", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(txtShipmentAddress.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please Enter your address", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(txtShipmentAddress.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please Enter your City/Location", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder()
    {
        String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate =  new SimpleDateFormat("MMM, ddd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime =  new SimpleDateFormat("HHH, mm, ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        DatabaseReference OrdersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone());

        HashMap<String, Object> OdersMap = new HashMap<String, Object>();

        OdersMap.put("totalAmount", totalamount);
        OdersMap.put("address", txtShipmentAddress.getText().toString());
        OdersMap.put("phone", txtShipmentPhone.getText().toString());
        OdersMap.put("city", txtShipmentCity.getText().toString());
        OdersMap.put("date", saveCurrentDate);
        OdersMap.put("time", saveCurrentTime);
        OdersMap.put("name", txtShipmentName.getText().toString());
        OdersMap.put("state", "Not shipped");

        OrdersRef.updateChildren(OdersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User view")
                            .child(Prevalent.currentOnlineUsers.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ConfirmFinalOrderActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}
