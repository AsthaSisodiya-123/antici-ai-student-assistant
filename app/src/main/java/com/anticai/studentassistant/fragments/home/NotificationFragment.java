
package com.anticai.studentassistant.fragments.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.NotificationResponse;
import com.anticai.studentassistant.network.RetrofitClient;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private LinearLayout notificationContainer;
    private TextView unreadCount;
    private TextView statusText;

    private ApiService apiService;

    private int backgroundColor;
    private int cardColor;
    private int primaryText;
    private int secondaryText;
    private int accentColor;
    private int borderColor;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        loadThemeColors();

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(backgroundColor);
        scrollView.setClipToPadding(false);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(24), dp(22), dp(30));

        scrollView.addView(root);

        TextView brand = createText(
                "ANTICIAI", 12, accentColor, true);
        root.addView(brand);

        TextView title = createText(
                "Notifications.", 29, primaryText, true);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(10);
        root.addView(title, titleParams);

        TextView subtitle = createText(
                "Stay ahead of tasks, classes, and study goals.",
                14, secondaryText, false);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(5);
        root.addView(subtitle, subtitleParams);

        LinearLayout summaryCard = new LinearLayout(requireContext());
        summaryCard.setOrientation(LinearLayout.VERTICAL);
        summaryCard.setPadding(dp(17), dp(16), dp(17), dp(16));
        summaryCard.setBackground(makeBackground(cardColor, 18));

        LinearLayout.LayoutParams summaryParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        summaryParams.topMargin = dp(24);
        root.addView(summaryCard, summaryParams);

        TextView summaryLabel = createText(
                "YOUR INBOX", 11, secondaryText, true);
        summaryCard.addView(summaryLabel);

        unreadCount = createText(
                "Unread notifications: —", 17, primaryText, true);

        LinearLayout.LayoutParams countParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        countParams.topMargin = dp(8);
        summaryCard.addView(unreadCount, countParams);

        TextView sectionTitle = createText(
                "RECENT NOTIFICATIONS", 12, secondaryText, true);

        LinearLayout.LayoutParams sectionParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        sectionParams.topMargin = dp(28);
        sectionParams.bottomMargin = dp(12);
        root.addView(sectionTitle, sectionParams);

        notificationContainer = new LinearLayout(requireContext());
        notificationContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(notificationContainer);

        statusText = createText(
                "Loading notifications...", 14, secondaryText, false);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(28), 0, dp(28));
        root.addView(statusText);

        apiService = RetrofitClient.getApiService(requireContext());

        loadNotifications();
        loadUnreadCount();

        return scrollView;
    }

    private void loadThemeColors() {
        android.content.res.Resources res =
                requireContext().getResources();

        backgroundColor = res.getColor(R.color.app_background);
        cardColor = res.getColor(R.color.surface_color);
        primaryText = res.getColor(R.color.text_primary);
        secondaryText = res.getColor(R.color.text_secondary);
        accentColor = res.getColor(R.color.accent_blue);
        borderColor = res.getColor(R.color.border_color);
    }

    private void loadNotifications() {
        if (!isAdded()) return;

        apiService.getNotifications().enqueue(
                new Callback<List<NotificationResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<List<NotificationResponse>> call,
                            @NonNull Response<List<NotificationResponse>> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {

                            notificationContainer.removeAllViews();

                            List<NotificationResponse> notifications =
                                    response.body();

                            if (notifications.isEmpty()) {
                                statusText.setVisibility(View.VISIBLE);
                                statusText.setText(
                                        "You're all caught up!\nNo notifications yet.");
                                return;
                            }

                            statusText.setVisibility(View.GONE);

                            for (NotificationResponse notification
                                    : notifications) {
                                addNotificationCard(notification);
                            }

                        } else {
                            showError(
                                    "Couldn't load notifications. "
                                            + "Please check your login and backend.");
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<NotificationResponse>> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        showError(
                                "Connection failed. Check that the backend "
                                        + "is running and reachable.");
                    }
                });
    }

    private void loadUnreadCount() {
        if (!isAdded()) return;

        apiService.getUnreadNotificationCount().enqueue(
                new Callback<Long>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Long> call,
                            @NonNull Response<Long> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {
                            unreadCount.setText(
                                    "Unread notifications: " + response.body());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Long> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            unreadCount.setText(
                                    "Unread notifications: unavailable");
                        }
                    }
                });
    }

    private void addNotificationCard(NotificationResponse notification) {

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackground(makeBackground(cardColor, 16));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(13);
        notificationContainer.addView(card, cardParams);

        String category = notification.getCategory() == null
                ? "GENERAL"
                : notification.getCategory().toUpperCase(Locale.ROOT);

        TextView categoryText = createText(
                category.replace('_', ' '), 11, accentColor, true);
        card.addView(categoryText);

        TextView notificationTitle = createText(
                notification.getTitle() == null
                        ? "Notification" : notification.getTitle(),
                17, primaryText, true);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(9);
        card.addView(notificationTitle, titleParams);

        TextView message = createText(
                notification.getMessage() == null
                        ? "" : notification.getMessage(),
                14, secondaryText, false);

        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.topMargin = dp(7);
        card.addView(message, messageParams);

        TextView readStatus = createText(
                notification.isRead() ? "READ" : "UNREAD",
                11,
                notification.isRead() ? secondaryText : accentColor,
                true);

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        statusParams.topMargin = dp(12);
        card.addView(readStatus, statusParams);

        LinearLayout actions = new LinearLayout(requireContext());
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        actionsParams.topMargin = dp(10);
        card.addView(actions, actionsParams);

        if (!notification.isRead()) {
            TextView markRead = createActionText("Mark as read");

            markRead.setOnClickListener(v ->
                    markNotificationRead(notification.getId()));

            actions.addView(markRead);
        }

        TextView delete = createActionText("Delete");

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteParams.leftMargin = dp(20);
        actions.addView(delete, deleteParams);

        delete.setOnClickListener(v ->
                deleteNotification(notification.getId()));
    }

    private void markNotificationRead(Long id) {
        if (id == null || !isAdded()) return;

        apiService.markNotificationAsRead(id).enqueue(
                new Callback<NotificationResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<NotificationResponse> call,
                            @NonNull Response<NotificationResponse> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Marked as read",
                                    Toast.LENGTH_SHORT).show();

                            loadNotifications();
                            loadUnreadCount();

                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Could not mark notification as read",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<NotificationResponse> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Connection error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteNotification(Long id) {
        if (id == null || !isAdded()) return;

        apiService.deleteNotification(id).enqueue(
                new Callback<Void>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Void> call,
                            @NonNull Response<Void> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Notification deleted",
                                    Toast.LENGTH_SHORT).show();

                            loadNotifications();
                            loadUnreadCount();

                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Could not delete notification",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Void> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Connection error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showError(String message) {
        notificationContainer.removeAllViews();
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(message);
    }

    private TextView createText(
            String text,
            int size,
            int color,
            boolean bold) {

        TextView view = new TextView(requireContext());
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private TextView createActionText(String text) {
        TextView view = createText(text, 13, accentColor, true);
        view.setPadding(dp(5), dp(8), dp(5), dp(8));
        view.setClickable(true);
        view.setFocusable(true);
        return view;
    }

    private GradientDrawable makeBackground(
            int color,
            int radiusDp) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        drawable.setStroke(dp(1), borderColor);
        return drawable;
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}
