package com.example.ian.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;

public class AdminNewOrdersActivity extends AppCompatActivity {
    RecyclerView orderList;
    DatabaseReference orderFef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderList = findViewById(R.id.orders_list);

        orderFef = FirebaseDatabase.getInstance().getReference().child("Orders");

        orderList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<AdminOrders> options=
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderFef, AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolde> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolde>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolde holder, final int position, @NonNull final AdminOrders model)
                    {
                        holder.username.setText("Name " + model.getName());
                        holder.userPhoneNumber.setText("Phone Number " + model.getPhone());
                        holder.userShipmentAddress.setText("Address " + model.getAddress() + " " + model.getCity());
                        holder.userDateTime.setText("Ordered at: " + model.getDate()+ " "  + model.getTime());
                        holder.userTotalPrice.setText("Total Amount = Ksh " + model.getTotalAmount());

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have these orders been shipped");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which==0){
                                            String uID = getRef(position).getKey();

                                            RemoveOrder(uID);
                                        }
                                        else {
                                            finish();
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolde onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_layout, viewGroup,false);
                        return new AdminOrdersViewHolde(view);
                    }
                };

        orderList.setAdapter(adapter);
        adapter.startListening();

    }



    public static class AdminOrdersViewHolde extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView username, userPhoneNumber, userShipmentAddress,userDateTime,userTotalPrice;
        private Button showOrdersBtn;
        public AdminOrdersViewHolde(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.order_username);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userShipmentAddress = itemView.findViewById(R.id.order_address_city);
            userDateTime = itemView.findViewById(R.id.order_date_ime);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);

            showOrdersBtn = itemView.findViewById(R.id.show_orders_btn);
        }

        @Override
        public void onClick(View v) {

        }


    }
    private void RemoveOrder(String uID)
    {
        orderFef.child(uID).removeValue();
    }
}
