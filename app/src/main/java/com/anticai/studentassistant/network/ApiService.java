
package com.anticai.studentassistant.network;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // AUTH APIs

    @POST("api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);


    // STUDENT APIs

    @GET("api/student/me")
    Call<StudentResponse> getCurrentStudent();

    @GET("api/student/dashboard")
    Call<DashboardResponse> getDashboard();


    // AI CHAT API

    @POST("api/ai/chat")
    Call<AIChatResponse> chat(@Body AIChatRequest request);


    // STUDY SESSION APIs

    @POST("api/study-sessions/start")
    Call<StudySessionResponse> startStudySession(
            @Body StartStudySessionRequest request
    );

    @POST("api/study-sessions/{sessionId}/finish")
    Call<StudySessionResponse> finishStudySession(
            @Path("sessionId") long sessionId,
            @Body FinishStudySessionRequest request
    );

    @GET("api/study-sessions")
    Call<List<StudySessionResponse>> getStudySessions();


    // ONBOARDING APIs

    @GET("api/onboarding/status")
    Call<JsonObject> getOnboardingStatus(
            @Query("deviceId") String deviceId
    );

    @POST("api/onboarding/complete")
    Call<JsonObject> completeOnboarding(
            @Query("deviceId") String deviceId
    );

    @POST("api/onboarding/reset")
    Call<JsonObject> resetOnboarding(
            @Query("deviceId") String deviceId
    );


    // TASK APIs

    @GET("api/tasks")
    Call<List<TaskResponse>> getTasks();

    @GET("api/tasks/{id}")
    Call<TaskResponse> getTaskById(
            @Path("id") long id
    );

    @POST("api/tasks")
    Call<TaskResponse> createTask(
            @Body TaskRequest request
    );

    @PUT("api/tasks/{id}")
    Call<TaskResponse> updateTask(
            @Path("id") long id,
            @Body TaskRequest request
    );

    @PATCH("api/tasks/{id}/complete")
    Call<TaskResponse> completeTask(
            @Path("id") long id
    );

    @DELETE("api/tasks/{id}")
    Call<Void> deleteTask(
            @Path("id") long id
    );


    // SCHEDULE APIs

    @GET("api/schedules")
    Call<List<ScheduleResponse>> getSchedules();

    @GET("api/schedules/type/{type}")
    Call<List<ScheduleResponse>> getSchedulesByType(
            @Path("type") String type
    );

    @POST("api/schedules")
    Call<ScheduleResponse> createSchedule(
            @Body ScheduleRequest request
    );

    @PUT("api/schedules/{id}")
    Call<ScheduleResponse> updateSchedule(
            @Path("id") long id,
            @Body ScheduleRequest request
    );

    @DELETE("api/schedules/{id}")
    Call<Void> deleteSchedule(
            @Path("id") long id
    );


    // STUDY PLAN APIs

    @GET("api/study-plan")
    Call<List<StudyPlanResponse>> getStudyPlans();

    @GET("api/study-plan/active")
    Call<List<StudyPlanResponse>> getActiveStudyPlans();

    @GET("api/study-plan/{id}")
    Call<StudyPlanResponse> getStudyPlanById(
            @Path("id") long id
    );

    @POST("api/study-plan")
    Call<StudyPlanResponse> createStudyPlan(
            @Body StudyPlanRequest request
    );

    @POST("api/study-plan/generate")
    Call<StudyPlanResponse> generateStudyPlan(
            @Body StudyPlanRequest request
    );

    @PUT("api/study-plan/{id}")
    Call<StudyPlanResponse> updateStudyPlan(
            @Path("id") long id,
            @Body StudyPlanRequest request
    );

    @DELETE("api/study-plan/{id}")
    Call<Void> deleteStudyPlan(
            @Path("id") long id
    );
}
