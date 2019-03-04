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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ian.ecommerce.Model.Cart;
import com.example.ian.ecommerce.Prevalent.Prevalent;
import com.example.ian.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button nextBtn;
    private TextView txtTotalPrice, txtMsg;

    private ImageView deleteIcon,editIcon;

    private int overalPrice= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalPrice =(TextView) findViewById(R.id.total_price);
        txtMsg =(TextView) findViewById(R.id.msg1);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTotalPrice.setText("Total Price = Ksh."+ String.valueOf(overalPrice));

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total price", String.valueOf(overalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();

        txtTotalPrice.setText("Total Price = Ksh." + String.valueOf(overalPrice));

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef.child("User view").child(Prevalent.currentOnlineUsers.getPhone())
                        .child("Products"), Cart.class)
                .build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText("Price =" + model.getPrice());
                        holder.txtProductQuantity.setText("Quantity =" +model.getQuantity());

                        int priceOfOneProductType= ((Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity()));
                        overalPrice = overalPrice + priceOfOneProductType;

                        holder.editIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CartActivity.this, PoductDetailsActivity.class);
                                intent.putExtra("pid",model.getPid());
                                startActivity(intent);
                            }
                        });

                        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cartRef.child("User view")
                                        .child(Prevalent.currentOnlineUsers.getPhone())
                                        .child("Products")
                                        .child(model.getPid())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(CartActivity.this, "Product removed successfully", Toast.LENGTH_SHORT ).show();

                                                    Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        });


              /*          holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                          "Edit",
                                          "Remove"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            Intent intent = new Intent(CartActivity.this, PoductDetailsActivity.class);
                                            intent.putExtra("pid",model.getPid());
                                            startActivity(intent);
                                        }

                                        if (which ==1){
                                            cartRef.child("User view")
                                                    .child(Prevalent.currentOnlineUsers.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                Toast.makeText(CartActivity.this, "Product removed successfully", Toast.LENGTH_SHORT ).show();

                                                                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });*/



                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout, viewGroup, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
            recyclerView.setAdapter(adapter);
            adapter.startListening();

    }


    private void CheckOrderState()
    {
        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String shipmentState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shipmentState.equals("Shipped"))
                    {
                        txtTotalPrice.setText("Dear" + userName + "\n your order has been Dispatched");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg.setText("Congratulations , your final order has bee placed successfully.Your order will be delivered to your location.Thank you for staying with lexers");
                        txtMsg.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.INVISIBLE);

                        Toast.makeText(CartActivity.this, "You will be able to make another oder once you receive our current order", Toast.LENGTH_SHORT).show();



                    }
                    else if(shipmentState.equals("Not shipped"))
                    {
                        txtTotalPrice.setText("Shipping state= Not shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.INVISIBLE);

                        Toast.makeText(CartActivity.this, "You will be able to mak another oder once you receive our current order", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
