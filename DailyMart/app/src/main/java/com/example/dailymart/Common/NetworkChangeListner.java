package com.example.dailymart.Common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.dailymart.NoInternetActivity;
import com.example.dailymart.R;

public class NetworkChangeListner extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        if(!NetworkDetails.isConnectedToInternet(context))
//        {
            //AlertDialog.Builder ad=new AlertDialog.Builder(context);
            //View view= LayoutInflater.from(context).inflate(R.layout.check_internet_connection2,null);
            //ad.setView(view);

//            AlertDialog alertDialog=ad.create();
//            alertDialog.show();
//            alertDialog.setCanceledOnTouchOutside(false);
//            ad.setCancelable(false);
//
//            AppCompatButton btnTryAgain=view.findViewById(R.id.btnTryAgain);
//
//            btnTryAgain.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {

//                    alertDialog.dismiss();
////                    onReceive(context,intent);
//                }
//            });
//
//
//        }
//        else {
//            Toast.makeText(context,"Your Internet is Connected",Toast.LENGTH_SHORT).show();
//        }

//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (!NetworkDetails.isConnectedToInternet(context)) {
//               // Start the no internet activity
//               Intent noInternetIntent = new Intent(context, NoInternetActivity.class);
//                noInternetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required for BroadcastReceiver
//                context.startActivity(noInternetIntent);
//           } else {
//               Toast.makeText(context, "Your Internet is Connected", Toast.LENGTH_SHORT).show();
//            }
//        }


@Override
public void onReceive(Context context, Intent intent) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    if (isConnected) {
        Toast.makeText(context, "Internet Connected", Toast.LENGTH_SHORT).show();
    }
}


}



