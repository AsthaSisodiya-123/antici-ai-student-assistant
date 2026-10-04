package com.example.whatsapp.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.opengl.ETC1.encodeImage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.ContactActivity;
import com.example.whatsapp.LoginActivity;
import com.example.whatsapp.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class Chat_Fragment extends Fragment {

    SearchView searchView;
    ImageView ivMic;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    TextToSpeech textToSpeech;
    GridLayout gridLayout;
    String[] name = {"Myself","Mumma","Papa","Dadaji","Dadiji","Swaraj","Viddu","Mamiji","Mamaji","Naniji",
            "Aarti","Gauri","Janhavi","Diksha","Ishwari","Kalyani","Dhanu","Sanskruti"};

    int[] image = {R.drawable.myself,R.drawable.mumma,R.drawable.papa,R.drawable.dadaji,R.drawable.dadiji,R.drawable.swaraj,
            R.drawable.viddu,R.drawable.mamiji,R.drawable.mamaji,
            R.drawable.naniji,R.drawable.aarti,R.drawable.gauri,R.drawable.janhavi,R.drawable.diksha,R.drawable.ishwari,
            R.drawable.kalyani,R.drawable.dhanu,R.drawable.sanskruti};

    String[] number = {"8329431239","9359319818","9284370982","976767136","5555555555",
            "6666666666","9067988397","8888008353","7709599439","9860780043",
            "8080048546","9579827540","9623519549","8010343580","9309734390",
            "9322690297","7385332710","9766756029"};
    ArrayList<View> contactViews = new ArrayList<>();
    ArrayList<String> contactNames = new ArrayList<>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat_, container, false);

        searchView=view.findViewById(R.id.searchView);
        ivMic=view.findViewById(R.id.ivMic);

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

        gridLayout=view.findViewById(R.id.glHome);
        LayoutInflater inflater1=LayoutInflater.from(getActivity());

        for (int i = 0; i < name.length; i++) {
            int index = i;
            View view1 = inflater1.inflate(R.layout.contact_list, null);
            ShapeableImageView ivCustomImage = view1.findViewById(R.id.ivContactListImage);
            TextView tvCustomName = view1.findViewById(R.id.tvContactListName);
            LinearLayout llContactList = view1.findViewById(R.id.llContactList);

            ivCustomImage.setImageResource(image[i]);
            tvCustomName.setText(name[i]);

            gridLayout.addView(view1);
            contactViews.add(view1);
            contactNames.add(name[i].toLowerCase());

            ivCustomImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BitmapDrawable drawable = (BitmapDrawable) ivCustomImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    String base64 = encodeImage(bitmap);
                    String contactName = tvCustomName.getText().toString();

                    preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                    editor = preferences.edit();
                    editor.putString("profileImage", base64);
                    editor.putString("profileName", contactName);
                    editor.apply();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.homeFrameLayout, new ProfilePicFragment())
                            .commit();


                    ProfilePicFragment profilePicFragment=new ProfilePicFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, profilePicFragment).commit();

                }
            });
            // Click to open contact activity with number
            llContactList.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                intent.putExtra("profileName", name[index]);
                intent.putExtra("profileNumber", number[index]);
                startActivity(intent);

                preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                editor = preferences.edit();
                editor.putString("profileName", name[index]);
                editor.putString("profileNumber", number[index]);
                editor.apply();
            });
        }




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        return view;
    }

    private void filterContacts(String query) {
        String lowerQuery = query.toLowerCase().trim();
        for (int i = 0; i < contactViews.size(); i++) {
            contactViews.get(i).setVisibility(contactNames.get(i).contains(lowerQuery) ? View.VISIBLE : View.GONE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
    }
}

