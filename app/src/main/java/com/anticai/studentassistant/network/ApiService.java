
package com.anticai.studentassistant.network;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/register")
    Call<LoginResponse> register(
            @Body RegisterRequest request
    );

    @POST("api/auth/login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );

    @GET("api/student/me")
    Call<StudentResponse> getCurrentStudent();

    @GET("api/student/dashboard")
    Call<DashboardResponse> getDashboard();

    @GET("api/study-plan")
    Call<StudyPlanResponse> getStudyPlan();

    @GET("api/study-plan/active")
    Call<List<JsonObject>> getActiveStudyPlans();

    @POST("api/ai/chat")
    Call<AIChatResponse> chat(
            @Body AIChatRequest request
    );

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

    @POST("api/study-plan/generate")
    Call<ResponseBody> generateStudyPlan(
            @Body JsonObject request
    );

}
