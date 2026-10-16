package com.anticai.studentassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.anticai.studentassistant.models.StudyPlan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudyPlanStorage {

    private static final String PREFS = "anticai_saved_plans";
    private static final String KEY_PLANS = "plans";

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    public static synchronized List<StudyPlan> getPlans(Context context) {
        List<StudyPlan> plans = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(
                    prefs(context).getString(KEY_PLANS, "[]")
            );

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                plans.add(StudyPlan.fromJson(object));
            }
        } catch (Exception ignored) {
        }

        // Upcoming dates first; completed plans go to the bottom.
        Collections.sort(plans, new Comparator<StudyPlan>() {
            @Override
            public int compare(StudyPlan a, StudyPlan b) {
                if (a.completed != b.completed) {
                    return a.completed ? 1 : -1;
                }
                return a.date.compareTo(b.date);
            }
        });

        return plans;
    }

    public static synchronized void savePlan(
            Context context,
            StudyPlan newPlan
    ) {
        List<StudyPlan> plans = getPlans(context);

        // Replace the plan if it already exists.
        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).id.equals(newPlan.id)) {
                plans.set(i, newPlan);
                saveAll(context, plans);
                return;
            }
        }

        plans.add(newPlan);
        saveAll(context, plans);
    }

    public static synchronized void markCompleted(
            Context context,
            String planId
    ) {
        List<StudyPlan> plans = getPlans(context);

        for (StudyPlan plan : plans) {
            if (plan.id.equals(planId)) {
                plan.completed = true;
                break;
            }
        }

        saveAll(context, plans);
    }

    private static void saveAll(
            Context context,
            List<StudyPlan> plans
    ) {
        JSONArray array = new JSONArray();

        try {
            for (StudyPlan plan : plans) {
                array.put(plan.toJson());
            }
        } catch (Exception ignored) {
        }

        prefs(context).edit()
                .putString(KEY_PLANS, array.toString())
                .apply();
    }
}