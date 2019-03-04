package com.example.ian.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ian.ecommerce.Model.Products;
import com.example.ian.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {

    private Button searchBtn;
    private EditText inputProductname;
    private RecyclerView seachList;
    private String SearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBtn =  findViewById(R.id.search_product_name_button);
        inputProductname = findViewById(R.id.search_product_name);
        seachList = findViewById(R.id.search_list);
        seachList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));


        SearchInput =  inputProductname.getText().toString();

        onStart();

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(SearchInput), Products.class)
                .build();

       FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
               holder.txtProductName.setText(model.getPname());
               holder.txtProductPrice.setText("Price =" + model.getPrice() + "$");
               holder.txtProductDescription.setText(model.getDescription());
               Picasso.get().load(model.getImage()).into(holder.productImage);

               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(SearchActivity.this, PoductDetailsActivity.class);
                       intent.putExtra("pid", model.getPid());
                       startActivity(intent);
                   }
               });

           }

           @NonNull
           @Override
           public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
               View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item_layout, viewGroup, false);
               ProductViewHolder holder = new ProductViewHolder(view);
               return holder;
           }
       };

        seachList.setAdapter(adapter);
        adapter.startListening();
    }
}
