package com.rtoosh.provider.model;

/*
 * Created by rishav on 10/23/2017.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import timber.log.Timber;

public class Operations {

    public static JSONObject makeJsonRegisterService(String service, String serviceName, String description,
                                                 String price, String duration) {
        JSONObject jsonObject = new JSONObject();
        try {
           // JSONArray jsonArray = new JSONArray();
            JSONObject jObj = new JSONObject();
            jObj.put("service_name", serviceName);
            jObj.put("description", description);
            jObj.put("price", price);
            jObj.put("duration", duration);
            //jsonArray.put(jObj);
            jsonObject.put(service, jObj);
          //  jsonService.put("service", jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static String updateProfileParams(String query) {
        String params = Config.UPDATE_PROFILE_URL + query;
        Timber.e("update_profile params-- "+params);
        return params;
    }

    public static HashMap<String, String> acceptRequestParams(String request_id, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("request_id", request_id);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> declineRequestParams(String request_id, String reason, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("request_id", request_id);
        hashMap.put("reason", reason);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> serviceStartedParams(String service_id, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("service_id", service_id);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> serviceCompletedParams(String service_id, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("service_id", service_id);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> additionalServiceParams(String order_id, String number_of_services,
                                                                  String total_paid_amount, String note, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order_id", order_id);
        hashMap.put("number_of_services", number_of_services);
        hashMap.put("total_paid_amount", total_paid_amount);
        hashMap.put("note", note);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> feedbackParams(String provider_id, String user_id, float rating,
                                                         String comment, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("provider_id", provider_id);
        hashMap.put("user_id", user_id);
        hashMap.put("rating", String.valueOf(rating));
        hashMap.put("comment", comment);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> updateLocationParams(String user_id, double lat, double lng, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("lat", String.valueOf(lat));
        hashMap.put("lng", String.valueOf(lng));
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> updateWalletParams(String user_id, String name, String iBan,  String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("name", name);
        hashMap.put("iban_no", iBan);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> walletParams(String user_id, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> historyParams(String provider_id, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("provider_id", provider_id);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> vacationParams(String user_id, String type, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("type", type);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> updateInfoParams(String user_id, String register_service,
                                                           String register_order, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("register_service", register_service);
        hashMap.put("register_order", register_order);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> updateWorkParams(String user_id, String work_online,
                                                           String work_schedule, String lang) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("work_online", work_online);
        hashMap.put("work_schedule", work_schedule);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> ratingsParams(String provider_id, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("provider_id", provider_id);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> updateScheduleParams(String user_id, String hours, String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", user_id);
        hashMap.put("register_schedule_hours", hours);
        hashMap.put("lang", lang);

        return hashMap;
    }

    public static HashMap<String, String> updateDocParams(String user_id, String issue_date, String id_type,
                                                          String id_number ,String lang) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", user_id);
        hashMap.put("id_number", id_number);
        hashMap.put("issue_date", issue_date);
        hashMap.put("id_type", id_type);
        hashMap.put("lang", lang);

        return hashMap;
    }

}
