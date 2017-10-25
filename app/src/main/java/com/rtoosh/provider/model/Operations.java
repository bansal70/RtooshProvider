package com.rtoosh.provider.model;

/*
 * Created by rishav on 10/23/2017.
 */

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Operations {
    private static final String TAG = Operations.class.getSimpleName();
    public static JSONObject jsonService = new JSONObject();

    public static String makeJsonRegisterID(String id, String id_type, String issue_date, List<String> workType) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("id_number", id);
            jsonObject.put("id_type", id_type);
            jsonObject.put("issue_date", issue_date);
            jsonObject.put("workType", workType);

            Log.e(TAG, "JSON ID: "+ jsonObject.toString());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String makeJsonRegisterOrder(int persons, int services, String price) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("max_persons", persons);
            jsonObject.put("max_services", services);
            jsonObject.put("min_order", price);

            Log.e(TAG, "JSON Order: "+ jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

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

    public static String makeJSONRegisterInfo(String surname, String bio) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("surname", surname);
            jsonObject.put("bio", bio);

            Log.e(TAG, "JSON Info: "+ jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
