
package com.anticai.studentassistant.fragments.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpSupportFragment extends Fragment {

    private Button btnContactSupport;

    public HelpSupportFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_help_support,
                container,
                false
        );

        TextView btnBack = view.findViewById(R.id.btnHelpBack);
        btnContactSupport = view.findViewById(R.id.btnContactSupport);

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        btnContactSupport.setOnClickListener(v -> showSupportForm());

        return view;
    }

    private void showSupportForm() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);

        layout.setPadding(padding, padding / 2, padding, 0);

        EditText subjectInput = new EditText(requireContext());
        subjectInput.setHint("Subject");
        subjectInput.setSingleLine(true);
        layout.addView(subjectInput);

        EditText messageInput = new EditText(requireContext());
        messageInput.setHint("Describe your issue");
        messageInput.setMinLines(4);
        messageInput.setGravity(
                android.view.Gravity.TOP | android.view.Gravity.START
        );
        layout.addView(messageInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Contact Support")
                .setMessage("Tell us how we can help.")
                .setView(layout)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .setPositiveButton("Submit", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String subject = subjectInput.getText()
                                    .toString().trim();

                            String message = messageInput.getText()
                                    .toString().trim();

                            if (subject.isEmpty()) {
                                subjectInput.setError(
                                        "Please enter a subject"
                                );
                                return;
                            }

                            if (message.isEmpty()) {
                                messageInput.setError(
                                        "Please describe your issue"
                                );
                                return;
                            }

                            if (subject.length() > 150) {
                                subjectInput.setError(
                                        "Maximum 150 characters"
                                );
                                return;
                            }

                            if (message.length() > 3000) {
                                messageInput.setError(
                                        "Maximum 3000 characters"
                                );
                                return;
                            }

                            dialog.dismiss();
                            submitSupportRequest(subject, message);
                        })
        );

        dialog.show();
    }

    private void submitSupportRequest(
            String subject,
            String message) {

        if (!isAdded()) return;

        btnContactSupport.setEnabled(false);
        btnContactSupport.setText("Submitting...");

        JsonObject request = new JsonObject();
        request.addProperty("subject", subject);
        request.addProperty("message", message);
        request.addProperty("category", "GENERAL");

        RetrofitClient.getApiService(requireContext())
                .submitSupportRequest(request)
                .enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response) {

                        if (!isAdded()) return;

                        btnContactSupport.setEnabled(true);
                        btnContactSupport.setText("Contact Support");

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Support request submitted successfully.",
                                    Toast.LENGTH_LONG
                            ).show();
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Submission failed. Please try again.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        btnContactSupport.setEnabled(true);
                        btnContactSupport.setText("Contact Support");

                        Toast.makeText(
                                requireContext(),
                                "Could not connect to the server. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
