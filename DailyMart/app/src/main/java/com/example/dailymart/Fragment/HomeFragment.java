package com.example.dailymart.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailymart.Common.NetworkChangeListner;
import com.example.dailymart.R;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment{
    LinearLayout tvNetworkStatus;
SearchView searchView;
ImageView ivMic;
private static final int REQUEST_CODE_SPEECH_INPUT = 1;
TextToSpeech textToSpeech;
    GridLayout gridLayout1;
    String[] offer={"10% OFF","13% OFF","15% OFF","5% OFF","20% OFF"};
    String[] name={"Joy Lemon FaceWash","Omkar Papad","Colgate","Head & Shoulder","Taj Mahal Rice"};
    String[] prize={"Rs.129/-","Rs.50/-","Rs.60/-","Rs.149/-","Rs.99/-"};
    String[] delivary={"Free Delivary","Delivary Rs.29/-","Delivary Rs.17/-","Free Delivary","Free Delivary"};
    int[] image={R.drawable.facewash,R.drawable.papad,R.drawable.tothpaste,R.drawable.shampoo,R.drawable.tmrice};

GridLayout gridLayout2;
    String[] productOffer={"10% OFF","13% OFF","15% OFF","5% OFF","20% OFF","10% OFF","13% OFF","15% OFF","5% OFF","20% OFF"};
    String[] productName={"Halke-Fulke","Khajur","Coffee","Sonpapdi","Namkeen","Biscuit","Sugar","Fortune oil","Masale","Pen"};
    String[] productPrize={"Rs.129/-","Rs.50/-","Rs.60/-","Rs.149/-","Rs.99/-",
            "Rs.79/-","Rs.40/-","Rs.560/-","Rs.49/-","Rs.99/-"};
    String[] productDelivary={"Free Delivary","Delivary Rs.29/-","Delivary Rs.17/-","Free Delivary","Free Delivary",
            "Free Delivary","Delivary Rs.29/-","Delivary Rs.17/-","Free Delivary","Free Delivary"};
    int[] productImage={R.drawable.halkefulke,R.drawable.khajur,R.drawable.tea,R.drawable.sonpapli,R.drawable.namkeen,
            R.drawable.biscuit,R.drawable.sugar,R.drawable.foil,R.drawable.masala,R.drawable.stationary};


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        Toast.makeText(getActivity(),"Home Fragment",Toast.LENGTH_SHORT).show();
        searchView=view.findViewById(R.id.searchView);
        ivMic=view.findViewById(R.id.ivMic);
        tvNetworkStatus = view.findViewById(R.id.tvNetworkStatus);

        // check network on load
        checkNetworkStatus();

        ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak Now");
                try {
                    startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Home Fragment", Toast.LENGTH_SHORT).show();
                }

                }
        });

        gridLayout1=view.findViewById(R.id.glHomeFragment);
        LayoutInflater inflater1=LayoutInflater.from(getActivity());


        for (int i=0;i<name.length;i++)
        {
            View view1=inflater1.inflate(R.layout.custom_list,null);
            ImageView ivCustomImage=view1.findViewById(R.id.ivCustomImage);
            TextView tvCustomOffer=view1.findViewById(R.id.tvCustomOffer);
            TextView tvCustomName=view1.findViewById(R.id.tvCustomName);
            TextView tvCustomPrize=view1.findViewById(R.id.tvCustomPrize);
            TextView tvCustomDelivary=view1.findViewById(R.id.tvCustomRate);

            ivCustomImage.setImageResource(image[i]);
            tvCustomOffer.setText(offer[i]);
            tvCustomName.setText(name[i]);
            tvCustomPrize.setText(prize[i]);
            tvCustomDelivary.setText(delivary[i]);

            gridLayout1.addView(view1);

        }

        gridLayout2=view.findViewById(R.id.glHomeFragment1);
        LayoutInflater inflater2=LayoutInflater.from(getActivity());


        for (int i=0;i<productName.length;i++)
        {
            View view2=inflater2.inflate(R.layout.custom_product_list,null);
            ImageView ivCustomImage2=view2.findViewById(R.id.ivCustomImage);
            TextView tvCustomOffer2=view2.findViewById(R.id.tvCustomOffer);
            TextView tvCustomName2=view2.findViewById(R.id.tvCustomName);
            TextView tvCustomPrize2=view2.findViewById(R.id.tvCustomPrize);
            TextView tvCustomDelivary2=view2.findViewById(R.id.tvCustomRate);

            ivCustomImage2.setImageResource(productImage[i]);
            tvCustomOffer2.setText(productOffer[i]);
            tvCustomName2.setText(productName[i]);
            tvCustomPrize2.setText(productPrize[i]);
            tvCustomDelivary2.setText(productDelivary[i]);

            gridLayout2.addView(view2);

        }


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1)
        {
            if (resultCode==RESULT_OK && data!=null)
            {
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                searchView.setQuery(result.get(0),true);
                textToSpeech=new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        textToSpeech.setLanguage(Locale.ENGLISH);

                        textToSpeech.speak(result.get(0),TextToSpeech.QUEUE_FLUSH,null,null);


                    }
                });
            }
        }
    }
    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            tvNetworkStatus.setVisibility(View.GONE); // hide banner
        } else {
            tvNetworkStatus.setVisibility(View.VISIBLE); // show banner
        }
    }

}
