package com.runin.runinapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.runin.runinapp.utils.RuninApi;
import com.runin.runinapp.utils.RuninApiUser;
import com.runin.runinapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Documentation
 * Created by thoma on 22/11/2016.
 */
public class FacebookRuninActivity extends AppCompatActivity {
    private static final String TAG = FacebookRuninActivity.class.getSimpleName();

    private TextView fb_text;

    // Facebook classes
    private ProfilePictureView profile_pic;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.face_activity);

        TextView screen_title = findViewById(R.id.screen_title);
        LoginButton login_button = findViewById(R.id.login_button);

        fb_text = findViewById(R.id.fb_text);
        profile_pic = findViewById(R.id.picture);

        screen_title.setTypeface(Utils.getFontBebasNeue(this));

        if (AccessToken.getCurrentAccessToken() != null) {
            RequestData();
            profile_pic.setVisibility(View.VISIBLE);
            fb_text.setVisibility(View.GONE);
        }

        callbackManager = CallbackManager.Factory.create();

        //getKeyHash();

        login_button.setReadPermissions("email");
        login_button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    profile_pic.setVisibility(View.GONE);
                    fb_text.setVisibility(View.VISIBLE);
                }
            }
        });
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "Facebook login success");
                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                    profile_pic.setVisibility(View.VISIBLE);
                    fb_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook login canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "Facebook login error", exception);
            }
        });


    }

    /*
    private void getKeyHash() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
    */

    private void RequestData() {
        Log.w(TAG, "Requesting data");

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                System.out.println("Json data :" + json);
                try {
                    if (json != null) {
                        Log.i(TAG, String.format("Name: %s %s, Email: %s, Profile link: %s", json.getString("first_name"), json.getString("last_name"), json.getString("email"), json.getString("link")));
                        profile_pic.setProfileId(json.getString("id"));

                        RuninApiUser user = new RuninApiUser();
                        user.setEmail(json.getString("email"));
                        user.setFirstName(json.getString("first_name"));
                        user.setLastName(json.getString("last_name"));
                        user.setFacebookId(json.getString("id"));
                        user.setGender(json.getString("gender").equals("male") ? 0 : 1);

                        RuninApi runinApi = new RuninApi(FacebookRuninActivity.this);
                        runinApi.registerUser(user);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,link,email,picture,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}