package com.example.dailymart.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dailymart.AdaptorClass.ProductAdaptor;
import com.example.dailymart.ModelClass.ProductModel;
import com.example.dailymart.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {
LinearLayout tvcfNetworkStatus;
    RecyclerView rvProductList;
    List<ProductModel> productModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_categories, container, false);
        Toast.makeText(getActivity(),"Categories",Toast.LENGTH_SHORT).show();
        tvcfNetworkStatus = view.findViewById(R.id.tvcfNetworkStatus);

        // check network on load
        checkNetworkStatus();
        rvProductList=view.findViewById(R.id.rvProductList);

        productModel=new ArrayList<>();

        productModel.add(new ProductModel(R.drawable.namkeen,"5% Off","Namkeen","Rs 30/-","Free Delivary"));

        productModel.add(new ProductModel(R.drawable.biscuit,"7% Off","Biscuit","Rs 10/-","Free Delivary"));

        productModel.add(new ProductModel(R.drawable.halkefulke,"3% Off","Halke Fulke","Rs 30/-","Free Delivary"));

        productModel.add(new ProductModel(R.drawable.khajur,"5% Off","Khajur","Rs 70/-","Free Delivary"));

        productModel.add(new ProductModel(R.drawable.sonpapli,"8% Off","Sonpapdi","Rs 40/-","Free Delivary"));

        productModel.add(new ProductModel(R.drawable.sweety,"5% Off","Sweet","Rs 30/-","Free Delivary"));

        ProductAdaptor productAdaptor=new ProductAdaptor(getActivity(),productModel);
        rvProductList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvProductList.setAdapter(productAdaptor);
        return view;
    }
    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            tvcfNetworkStatus.setVisibility(View.GONE); // hide banner
        } else {
            tvcfNetworkStatus.setVisibility(View.VISIBLE); // show banner
        }
    }
}