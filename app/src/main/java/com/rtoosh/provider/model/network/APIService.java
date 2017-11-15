package com.rtoosh.provider.model.network;

/*
 * Created by rishav on 10/26/2017.
 */

import com.rtoosh.provider.model.POJO.LoginResponse;
import com.rtoosh.provider.model.POJO.OtpResponse;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.POJO.RegisterResponse;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.POJO.register.RegisterServiceResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIService {
    @POST
    Call<ResponseBody> response(@Url String string);

    @POST("apis/sentOtp")
    Call<LoginResponse> phoneResponse(@Query("country_code") String code,
                                      @Query("phone") String phone,
                                      @Query("deviceToken") String deviceToken,
                                      @Query("devicetype") String deviceType,
                                      @Query("lang") String lang);

    @POST("customers/verifyOtp")
    Call<OtpResponse> phoneResponse(@Query("phone") String phone,
                                    @Query("otp") String otp,
                                    @Query("lang") String lang);

    @POST("apis/Register")
    Call<RegisterResponse> registerResponse(@Query("full_name") String full_name,
                                            @Query("email") String email,
                                            @Query("country_code") String code,
                                            @Query("phone") String phone,
                                            @Query("password") String password,
                                            @Query("user_type") String user_type,
                                            @Query("user_role") String user_role,
                                            @Query("deviceToken") String deviceToken,
                                            @Query("deviceType") String deviceType,
                                            @Query("lang") String lang);

    @POST("apis/servicelist")
    Call<RegisterServiceResponse> registerServiceResponse(@Query("lang") String lang);

    @Multipart
    @POST("apis/copyidimage")
    Call<AbstractApiResponse> uploadIdResponse(@Query("user_id") String user_id,
                                                   @Query("lang") String lang,
                                                   @Part MultipartBody.Part id_image);

    @Multipart
    @POST("apis/Register")
    Call<AbstractApiResponse> registerInfoResponse(@Query("user_id") String user_id,
                                                   @Query("register_id") String register_id,
                                                   @Query("register_order") String register_order,
                                                   @Query("register_schedule_hours") String register_schedule_hours,
                                                   @Query("register_service") String register_service,
                                                   @Query("register_info") String register_info,
                                                   @Query("deviceToken") String deviceToken,
                                                   @Query("deviceType") String deviceType,
                                                   @Query("lang") String lang,
                                                   @Part MultipartBody.Part coverFile,
                                                   @Part MultipartBody.Part[] workFiles);

    @POST("apis/Register")
    Call<AbstractApiResponse> registerInfoResponse(@Query("user_id") String user_id,
                                                   @Query("register_id") String register_id,
                                                   @Query("register_order") String register_order,
                                                   @Query("register_schedule_hours") String register_schedule_hours,
                                                   @Query("register_service") String register_service,
                                                   @Query("register_info") String register_info,
                                                   @Query("deviceToken") String deviceToken,
                                                   @Query("deviceType") String deviceType,
                                                   @Query("lang") String lang);

    @POST("apis/changeStatus")
    Call<AbstractApiResponse> statusResponse(@Query("user_id") String user_id,
                                    @Query("status") String status,
                                    @Query("lang") String lang);

    @POST("apis/addProblem")
    Call<AbstractApiResponse> reportResponse(@Query("user_id") String user_id,
                                             @Query("name") String title,
                                             @Query("description") String description,
                                             @Query("lang") String lang);

    @POST("apis/contactUs")
    Call<AbstractApiResponse> contactResponse(@Query("user_id") String user_id,
                                             @Query("description") String description,
                                             @Query("lang") String lang);

    @POST("apis/getUser")
    Call<ProfileResponse> profileResponse(@Query("user_id") String user_id,
                                          @Query("lang") String lang);

    @Multipart
    @POST("apis/updateProfile")
    Call<AbstractApiResponse> updateProfileResponse(@Query("id") String user_id,
                                                   @Query("surname") String surname,
                                                   @Query("bio") String bio,
                                                   @Query("lang") String lang,
                                                   @Part MultipartBody.Part cover_image,
                                                   @Part MultipartBody.Part profilePic);

    @POST("apis/updateProfile")
    Call<AbstractApiResponse> updateProfileResponse(@Query("id") String user_id,
                                                   @Query("surname") String surname,
                                                   @Query("bio") String bio,
                                                   @Query("lang") String lang);

    @Multipart
    @POST("apis/updateProfile")
    Call<AbstractApiResponse> updateProfileResponse(@Query("id") String user_id,
                                                   @Query("lang") String lang,
                                                   @QueryMap HashMap<String, String> mapRemove,
                                                   @Part MultipartBody.Part[] workFiles);

    @POST("apis/updateProfile")
    Call<AbstractApiResponse> updateProfileResponse(@Query("id") String user_id,
                                                    @Query("lang") String lang,
                                                    @QueryMap HashMap<String, String> mapRemove);

    @POST("apis/updatePassword")
    Call<AbstractApiResponse> updatePasswordResponse(@Query("user_id") String user_id,
                                                    @Query("oldPass") String oldPass,
                                                    @Query("newPass") String newPass,
                                                    @Query("lang") String lang);

    @POST("apis/declinereq")
    Call<AbstractApiResponse> declineRequestResponse(@QueryMap HashMap<String, String> mapParams);

    @POST("apis/acceptreq")
    Call<RequestDetailsResponse> acceptRequestResponse(@QueryMap HashMap<String, String> mapParams);

    @POST("apis/startservice")
    Call<AbstractApiResponse> serviceStartedResponse(@QueryMap HashMap<String, String> mapParams);

    @POST("apis/completeservice")
    Call<AbstractApiResponse> serviceCompletedResponse(@QueryMap HashMap<String, String> mapParams);

    @POST("apis/addionalser")
    Call<AbstractApiResponse> additionalServiceResponse(@QueryMap HashMap<String, String> mapParams);

    @POST("apis/addFeedback")
    Call<AbstractApiResponse> feedbackResponse(@QueryMap HashMap<String, String> mapParams);

}
