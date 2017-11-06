package com.rtoosh.provider.model;

/*
 * Created by rishav on 10/23/2017.
 */

import org.json.JSONException;
import org.json.JSONObject;

public class Operations {
    private static final String TAG = Operations.class.getSimpleName();

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
}
