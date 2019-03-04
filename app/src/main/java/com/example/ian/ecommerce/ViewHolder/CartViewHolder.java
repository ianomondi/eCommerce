package com.example.ian.ecommerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ian.ecommerce.Interface.ItemClickListener;
import com.example.ian.ecommerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductQuantity, txtProductPrice;
    private ItemClickListener itemClickListener;
    public ImageView deleteIcon,editIcon;


    public CartViewHolder(@NonNull View itemView) {

        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        deleteIcon = (ImageView) itemView.findViewById(R.id.delete_icon);
        editIcon = (ImageView) itemView.findViewById(R.id.edit_icon);
    }

    @Override
    public void onClick(View v)
    {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
    public void setItemClickListener(ItemClickListener itemClickListener){

        this.itemClickListener= itemClickListener;
    }

}

