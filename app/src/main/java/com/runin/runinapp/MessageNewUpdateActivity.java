package com.runin.runinapp;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.runin.runinapp.data.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

public class MessageNewUpdateActivity extends AppCompatActivity {

    @Inject
    User userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_new_update);

//        if (profilePictureURI != null) {
//            Log.d("tabuenopues", "Si jala");
//            Log.d("tabuenopues", userProfile.getPhotoUri().toString());
////            byte[] data = getBytes(userProfile.getPhotoUri());
//            InputStream iStream = null;
//            try {
//                iStream = getContentResolver().openInputStream(userProfile.getPhotoUri());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            byte[] inputData = new byte[0];
//            try {
//                inputData = getBytes(iStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            file = new ParseFile("avatar.png", inputData);
//            file.saveInBackground(new SaveCallback() {
//                public void done(ParseException e) {
//                    if (e == null) {
//                        // Hooray! Let them use the app now.
//                        Log.d("tabuenopues", "Saved in background");
//                        LinearLayout confirmUpdate = (LinearLayout )findViewById(R.id.confirmUpdate);
//                        confirmUpdate.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Log.d("tabuenopues", "Click en confirmar");
//                                ViewDialog alert = new ViewDialog();
//                                alert.showDialog(MessageNewUpdateActivity.this);
//                            }
//                        });
//
//                    } else {
//                        // Sign up didn't succeed. Look at the ParseException
//                        // to figure out what went wrong
//                        Log.d("tabuenopues", String.valueOf(e));
//                    }
//                }
//            });
//            Log.d("tabuenopues", "After Save in Background");
//
//        }
        //Hasta aqui

        //Set text
        String runin = getResources().getString(R.string.newUpdateMessage1);
        String newUpdateMessage1 = "<font color='#000000' fontFamily='@font/open_sans_bold'>" + runin + " </font>";
        String newUpdateMessage2 = getResources().getString(R.string.newUpdateMessage2);
        final TextView textViewToChange = findViewById(R.id.newUpdate);
        textViewToChange.setText(Html.fromHtml(newUpdateMessage1 + newUpdateMessage2));

    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public class ViewDialog {

        public void showDialog(MessageNewUpdateActivity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_update);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


            final LinearLayout createNewUser = (LinearLayout) dialog.findViewById(R.id.createNewUser);

//            LinearLayout createNewUser = (LinearLayout )findViewById(R.id.createNewUser);
            createNewUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("tabuenopues", "Click ok");


                    final EditText signUser = (EditText) dialog.findViewById(R.id.newUsername);
                    final EditText signPass = (EditText) dialog.findViewById(R.id.newPassword);

//                    username = signUser.getText().toString().toLowerCase();
//                    password = signPass.getText().toString();

                    createUser();
                    dialog.dismiss();
                }
            });
//
//            FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
//            mDialogOk.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();
//                    dialog.cancel();
//                }
//            });

            dialog.show();
        }
    }

    private void createUser() {

//        ParseUser user = new ParseUser();
//        Log.d("tabuenopues", username);
//        user.setUsername(username);
//        Log.d("tabuenopues", password);
//        user.setPassword(password);
//        user.setEmail(username);
//        user.put("firstName", firstName);
//        user.put("lastName", lastName);
//        user.put("weight", weight);
//        user.put("height", height);
//        user.put("birthdate", birthdate);
//        user.put("gender", gender);
//        user.put("avatar", file);

        Log.d("tabuenopues", "create User");

        // other fields can be set just like with ParseObject
//                    user.put("phone", "650-253-0000");

//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    // Hooray! Let them use the app now.
//                    Log.d("tabuenopues", "Success");
//
//                    // The file where the user preferences is stored.
//                    String preferencesFile = MyApplication.sharedPreferencesFile;
//                    SharedPreferences sharedPref = MessageNewUpdateActivity.this.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
//
//                    // Obtain the saved preferences
//                    boolean isTutorialVisited = sharedPref.getBoolean(MyApplication.sharedPreferencesPropertyTutorialVisited, false);
//                    final boolean isSavedProfileData = sharedPref.getBoolean(MyApplication.sharedPreferencesPropertyProfileSaved, false);
//                    final String typeOfRunner = sharedPref.getString(MyApplication.sharedPreferencesPropertyTypeOfRunner, null);
//
//                    Intent continue_intent = new Intent(MessageNewUpdateActivity.this,SplashScreenActivity.class);
//                    startActivity(continue_intent);
//                    if (!isSavedProfileData) {
//                        Intent saveProfileIntent = new Intent(MessageNewUpdateActivity.this, ProfileActivity.class);
//                        MessageNewUpdateActivity.this.startActivity(saveProfileIntent);
//                        MessageNewUpdateActivity.this.finish();
//                    }
//                    else if (typeOfRunner == null) {
//                        Intent placeToRunIntent = new Intent(MessageNewUpdateActivity.this, SelectIndoorOutdoorActivity.class);
//                        MessageNewUpdateActivity.this.startActivity(placeToRunIntent);
//                        MessageNewUpdateActivity.this.finish();
//                    }
//                    else if (typeOfRunner.equals(MyApplication.runnerTypeOutdoor)) {
//                        Intent planOutdoorIntent = new Intent(MessageNewUpdateActivity.this, OutdoorSelectPlanActivity.class);
//                        MessageNewUpdateActivity.this.startActivity(planOutdoorIntent);
//                        MessageNewUpdateActivity.this.finish();
//                    }
//                    else {
//                        Intent mainIntent = new Intent(MessageNewUpdateActivity.this, OutdoorDashboardActivity.class);
//                        mainIntent.putExtra(MyApplication.extrasScreenName, MyApplication.screenNameQuick);
//                        MessageNewUpdateActivity.this.startActivity(mainIntent);
//                        MessageNewUpdateActivity.this.finish();
//                    }
//                } else {
//                    // Sign up didn't succeed. Look at the ParseException
//                    // to figure out what went wrong
//                    Log.d("tabuenopues", String.valueOf(e));
//                }
//            }
//        });
    }


}
