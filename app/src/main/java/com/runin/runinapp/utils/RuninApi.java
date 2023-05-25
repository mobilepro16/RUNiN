package com.runin.runinapp.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements calls to the remote API
 * Created by Samuel Kobelkowsky on 10/17/17.
 */
public class RuninApi {
    // The classname for Debug purposes.
    private static final String TAG = RuninApi.class.getSimpleName();

    private static final String API_URL = "https://runin.azurewebsites.net/api/";
    private static final String API_USERNAME = "noreply@runinapp.com";
    private static final String API_PASSWORD = "f&d5vbc4325ucsWeer.";

    private final Context context;

    // Used for network operations
//    private final transient RequestQueue queue;

    // Number of retries when an error is received. Setting it too high might cause the user to close the app.
    private int retries = 2;

    public RuninApi(Context context) {
        this.context = context;
//        this.queue = Volley.newRequestQueue(context);
    }

    /**
     * Registers a user in the API
     *
     * @param user The user to be registered
     */
    public void registerUser(RuninApiUser user) {
//        JSONObject params = new JSONObject();
//
//        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.i(TAG, "onResponse: " + response.toString());
//            }
//        };
//
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error != null && error.networkResponse != null) {
//                    Log.e(TAG, String.format("Network error (registerUser): %s %d %s", error.getLocalizedMessage(), error.networkResponse.statusCode, error.toString()));
//                }
//                else {
//                    Log.e(TAG, "No answer from server");
//                }
//            }
//        };
//
//        try {
//            params.put("Email", user.getEmail());
//            params.put("Firstname", user.getFirstName());
//            params.put("Lastname", user.getLastName());
//            params.put("DeviceUniqueId", InstanceID.getInstance(context).getId());
//            params.put("Platform", 1);
//            params.put("Gender", user.getGender());
//            params.put("FacebookID", user.getFacebookId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL + "users", params, listener, errorListener) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                try {
//                    Map<String, String> map = new HashMap<>();
//                    String key = "Authorization";
//                    String encodedString = Base64.encodeToString(String.format("%s:%s", API_USERNAME, API_PASSWORD).getBytes(), Base64.NO_WRAP);
//                    String value = String.format("Basic %s", encodedString);
//                    map.put(key, value);
//                    return map;
//                } catch (Exception e) {
//                    Log.e(TAG, "Authentication Header Failure", e);
//                }
//                return super.getHeaders();
//            }
//        };
//
//        request.setTag("apiRegister");
//
//        queue.add(request);
    }

    /**
     * Downloads a lists of segments for all the tracks
     *
     * @param result The list of segments
     */
    public void fetchSegments(final NetworkJSONArrayResultInterface result) {

//        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                if (result != null) {
//                    result.onSuccess(response);
//                }
//            }
//        };
//
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (retries > 0) {
//                    Log.e(TAG, String.format("Retrying: %d tries left", retries - 1));
//                    fetchSegments(result);
//                    retries--;
//                }
//                else {
//                    int statusCode = -1;
//                    if (error != null && error.networkResponse != null) {
//                        Log.e(TAG, String.format("Network error (fetchSegments): %s %d %s", error.getLocalizedMessage(), error.networkResponse.statusCode, error.toString()));
//                        statusCode = error.networkResponse.statusCode;
//                    }
//                    else {
//                        Log.e(TAG, "No answer from server");
//                    }
//
//                    if (result != null) {
//                        result.onFailure(statusCode);
//                    }
//                }
//            }
//        };
//
//        JsonArrayRequest request = new JsonArrayRequest(API_URL + "pistas", listener, errorListener) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                try {
//                    Map<String, String> map = new HashMap<>();
//                    String key = "Authorization";
//                    String encodedString = Base64.encodeToString(String.format("%s:%s", API_USERNAME, API_PASSWORD).getBytes(), Base64.NO_WRAP);
//                    String value = String.format("Basic %s", encodedString);
//                    map.put(key, value);
//                    return map;
//                } catch (Exception e) {
//                    Log.e(TAG, "Authentication Header Failure", e);
//                }
//                return super.getHeaders();
//            }
//        };
//
//        request.setTag("fetchSegments");
//
//        queue.add(request);
    }

    // endregion

    // region interfaces
    public interface NetworkJSONArrayResultInterface {
        void onSuccess(JSONArray response);

        void onFailure(int statusCode);
    }

    public interface ResultResponseInterface {
        void onNetworkConnectSuccess();

        void onNetworkConnectFailure(int statusCode);
    }
    // endregion
}
