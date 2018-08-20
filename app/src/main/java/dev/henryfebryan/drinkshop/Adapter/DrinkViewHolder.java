package dev.henryfebryan.drinkshop.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import dev.henryfebryan.drinkshop.Interface.IItemClickListener;
import dev.henryfebryan.drinkshop.R;

public class DrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView img_product;
    TextView txt_drink_name, txt_price;

    IItemClickListener itemClickListener;

    Button btn_add_to_cart;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DrinkViewHolder(View itemView) {
        super(itemView);

        img_product = (ImageView) itemView.findViewById(R.id.image_product);
        txt_drink_name = (TextView) itemView.findViewById(R.id.txt_drink_name);
        txt_price = (TextView) itemView.findViewById(R.id.txt_price);

        btn_add_to_cart = (Button) itemView.findViewById(R.id.btn_add_cart);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v);
    }
}
