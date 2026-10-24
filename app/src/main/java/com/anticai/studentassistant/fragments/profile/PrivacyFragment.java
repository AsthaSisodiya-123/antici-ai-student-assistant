
package com.anticai.studentassistant.fragments.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.RetrofitClient;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyFragment extends Fragment {

    private TextView tvPrivacyContent;

    private final Map<String, String> sections =
            new LinkedHashMap<>();

    private final String[] sectionOrder = {
            "Personal Information",
            "Tasks",
            "Study Plans",
            "Study Sessions"
    };

    public PrivacyFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_privacy,
                container,
                false
        );

        View btnBack = rootView.findViewById(R.id.btnPrivacyBack);

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        if (!(rootView instanceof ViewGroup)) {
            return rootView;
        }

        View child = ((ViewGroup) rootView).getChildAt(0);

        if (!(child instanceof ViewGroup)) {
            return rootView;
        }

        ViewGroup contentContainer = (ViewGroup) child;

        tvPrivacyContent = new TextView(requireContext());
        tvPrivacyContent.setTextColor(Color.rgb(180, 190, 210));
        tvPrivacyContent.setTextSize(14);
        tvPrivacyContent.setLineSpacing(6, 1f);
        tvPrivacyContent.setPadding(24, 24, 24, 32);

        contentContainer.addView(tvPrivacyContent);

        sections.clear();

        for (String section : sectionOrder) {
            sections.put(section, "Loading...");
        }

        renderPrivacyData();
        loadPrivacyData();

        return rootView;
    }

    private void loadPrivacyData() {
        if (!isAdded()) {
            return;
        }

        loadSection(
                "Personal Information",
                RetrofitClient.getApiService(requireContext())
                        .getPrivacyProfile()
        );

        loadSection(
                "Tasks",
                RetrofitClient.getApiService(requireContext())
                        .getPrivacyTasks()
        );

        loadSection(
                "Study Plans",
                RetrofitClient.getApiService(requireContext())
                        .getPrivacyStudyPlans()
        );

        loadSection(
                "Study Sessions",
                RetrofitClient.getApiService(requireContext())
                        .getPrivacyStudySessions()
        );
    }

    private void loadSection(
            String title,
            Call<JsonElement> call) {

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(
                    @NonNull Call<JsonElement> call,
                    @NonNull Response<JsonElement> response) {

                if (!isAdded() || tvPrivacyContent == null) {
                    return;
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && !response.body().isJsonNull()) {

                    String json = new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(response.body());

                    sections.put(title, json);

                } else {
                    sections.put(
                            title,
                            "Unable to load this section. HTTP "
                                    + response.code()
                    );
                }

                renderPrivacyData();
            }

            @Override
            public void onFailure(
                    @NonNull Call<JsonElement> call,
                    @NonNull Throwable t) {

                if (!isAdded() || tvPrivacyContent == null) {
                    return;
                }

                sections.put(
                        title,
                        "Unable to connect to the server. "
                                + "Check your connection and try again."
                );

                renderPrivacyData();
            }
        });
    }

    private void renderPrivacyData() {
        if (tvPrivacyContent == null) {
            return;
        }

        StringBuilder content = new StringBuilder();

        content.append("YOUR PERSONAL DATA\n\n");
        content.append(
                "This information is retrieved from your AnticiAI account.\n\n"
        );

        for (String title : sectionOrder) {
            content.append(title)
                    .append("\n");

            content.append("------------------------------")
                    .append("\n");

            content.append(
                    sections.getOrDefault(title, "Not loaded yet.")
            );

            content.append("\n\n");
        }

        content.append("ABOUT YOUR PRIVACY\n\n");
        content.append(
                "Your profile and study information help AnticiAI "
                        + "provide student features. Keep your login "
                        + "credentials private, and sign out when using "
                        + "a shared device.\n\n"
        );

        content.append(
                "If any section cannot be loaded, check your internet "
                        + "connection and sign-in status."
        );

        tvPrivacyContent.setText(content.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tvPrivacyContent = null;
    }
}
