package com.example.dailymart.AdaptorClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailymart.ModelClass.ProductModel;
import com.example.dailymart.R;

import java.util.List;

public class ProductAdaptor extends RecyclerView.Adapter<ProductAdaptor.ViewHolder> {
    private Context context;
    List<ProductModel> productModelList;

    public ProductAdaptor(Context context, List<ProductModel> productModelList) {
        this.context = context;
        this.productModelList = productModelList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProductImage;
        TextView tvProductOffer,tvProductName,tvProductPrize,tvProductDelivaryRate;
        public ViewHolder(@NonNull View view) {
            super(view);
            ivProductImage=view.findViewById(R.id.ivCustomImage);
            tvProductOffer=view.findViewById(R.id.tvCustomOffer);
            tvProductName=view.findViewById(R.id.tvCustomName);
            tvProductPrize=view.findViewById(R.id.tvCustomPrize);
            tvProductDelivaryRate=view.findViewById(R.id.tvCustomRate);
        }
    }


    @NonNull
    @Override
    public ProductAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_product_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdaptor.ViewHolder viewHolder, int position) {

        ProductModel productModel=productModelList.get(position);
        viewHolder.ivProductImage.setImageResource(productModel.getProductImage());
        viewHolder.tvProductOffer.setText(productModel.getProductOffer());
        viewHolder.tvProductName.setText(productModel.getProductName());
        viewHolder.tvProductPrize.setText(productModel.getProductPrize());
        viewHolder.tvProductDelivaryRate.setText(productModel.getProductDelivaryRate());

    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }



}
