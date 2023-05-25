package com.runin.runinapp.settings;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.runin.runinapp.App;
import com.runin.runinapp.BaseActivity;
import com.runin.runinapp.R;
import com.runin.runinapp.SelectIndoorOutdoorActivity;
import com.runin.runinapp.api.APIService;
import com.runin.runinapp.api.UserRequest;
import com.runin.runinapp.data.User;
import com.runin.runinapp.utils.ExtensionsKt;
import com.runin.runinapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

/**
 * Save and edit runner profile
 * Created by Cesar on 09/08/2016
 */
public class ProfileActivity extends BaseActivity {

    private static final String FILENAME = "profile.png";
    private static final String TEMP_FILENAME = "tmp.png";
    private static final int MY_PERMISSION_CAMERA_REQUEST_ID = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int SELECT_PHOTO = 2;
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.editTextName)
    EditText nameEditText;

    @BindView(R.id.editTextWeight)
    EditText weightEditText;

    @BindView(R.id.editTextHeight)
    EditText heightEditText;

    @BindView(R.id.editTextBirth)
    EditText editTextBirth;

    @BindView(R.id.spinnerGender)
    Spinner spinnerGender;

//    @BindView(R.id.spinnerUnits)
//    Spinner spinnerUnits;

    @BindView(R.id.profile_photo)
    ImageView profilePhoto;

    @BindView(R.id.editTextHeightFeets)
    EditText editTextHeightFeet;

    @BindView(R.id.editTextHeightInches)
    EditText editTextHeightInches;

    @BindView(R.id.heightFeetsLinearLayout)
    LinearLayout heightFeetLayout;

    @BindView(R.id.heightCmLayout)
    RelativeLayout heightCmLayout;

    @BindView(R.id.units_weight)
    TextView unitsWeight;

    @BindView(R.id.buttonSave)
    Button buttonSave;

    private int profilePictureSize = 0;
    private boolean isEdit;

    @Inject
    User userProfile;

    @Inject
    SharedPreferences sharedPreferences;

    private User newUserProfile;
    @Inject
    APIService apiService;

    /**
     * A filter that will only allow alphanumeric characters in the text edits
     *
     * @return A filter
     */
    private static InputFilter getEditTextFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {

                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                    {
                        sb.append(c);
                    } else {
                        keepOriginal = false;
                    }
                }
                if (keepOriginal) {
                    return null;
                } else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
                Matcher ms = ps.matcher(String.valueOf(c));
                return ms.matches();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create the view
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        newUserProfile = userProfile.clone();
        // Nice font on button
        buttonSave.setTypeface(Utils.getFontBebasNeue(this));

        // Hide keyboard on click of the date dialog
        editTextBirth.setInputType(InputType.TYPE_NULL);

        // Add filters that only allow alphanumeric characters in TextEdit
        nameEditText.setFilters(new InputFilter[]{getEditTextFilter()});

        // Determines the size of the user image
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        profilePictureSize = Utils.dp2px(82, this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_view_item);
        // Specify the layout to use when the list of choices appears
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerGender.setAdapter(adapterGender);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                newUserProfile.setGender(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

//        ArrayAdapter<CharSequence> adapterUnits = ArrayAdapter.createFromResource(this, R.array.units, R.layout.spinner_view_item);
//        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerUnits.setAdapter(adapterUnits);
//        spinnerUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                newUserProfile.setMeasuringSystem(i);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        // For Imperial system units. TODO: Make it work
        heightFeetLayout.setVisibility(View.GONE);
        heightCmLayout.setVisibility(View.VISIBLE);

        // Edit Profile case
        if (getIntent() != null && getIntent().getExtras() != null) {
            isEdit = getIntent().getExtras().getBoolean(App.extrasEdit);
        }

        // Populate the fields with the user profile
        nameEditText.setText(userProfile.getName());
        if (userProfile.getWeight() > 0) {
            weightEditText.setText(userProfile.getWeightString());
        }
        if (userProfile.getHeight() > 0) {
            heightEditText.setText(userProfile.getHeightString());
        }
        editTextBirth.setText(userProfile.getBirthDateInView());
        spinnerGender.setSelection(userProfile.getGender());

        if(!TextUtils.isEmpty(userProfile.getProfilePicture())) {
            Picasso.get().load(userProfile.getProfilePicture())
                    .placeholder(R.drawable.ic_pic_camara)
                    .into(profilePhoto);
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void selectBirthDate(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR) - 18;
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    newUserProfile.setBirthDate(calendar);
                    editTextBirth.setText(newUserProfile.getBirthDateInView());
                }, mYear, mMonth, mDay) {

        };
        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.show();
    }

    private Disposable disposable;

    public void onClickSaveProfile(View view) {
        String name = nameEditText.getText().toString();
        String weight = weightEditText.getText().toString();
        String height = heightEditText.getText().toString();
        String birth = editTextBirth.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(height) || TextUtils.isEmpty(birth)) {
            Toast.makeText(this, "Please enter required field", Toast.LENGTH_LONG).show();
            return;
        }

        newUserProfile.setName(name);
        newUserProfile.setWeight(Float.parseFloat(weight));
        newUserProfile.setHeight(Float.parseFloat(height));

        // Obtain a String resource ID with the error message (if any)
        int validate_resource = validateData();

        // Show the validation message
        if (validate_resource != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(validate_resource).setTitle(R.string.title_profile_validate);

            // Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();
        } else {
            showProgress();
            UserRequest userRequest = new UserRequest();
            userRequest.setName(newUserProfile.getName());
            userRequest.setGender(newUserProfile.getGender() + "");
            userRequest.setHeight(newUserProfile.getHeightString());
            userRequest.setWeight(newUserProfile.getWeightString());
            userRequest.setBirthDate(newUserProfile.getBirthDate());


            Observable<User> infoObservable;
            if (!isEdit) {
                ExtensionsKt.putBoolean(sharedPreferences, App.sharedPreferencesPropertyProfileSaved, true);
                infoObservable = apiService.registerUser(userRequest);
            } else {
                infoObservable = apiService.updateUser(userRequest);
            }

            if (newUserProfile.getTempPhotoFile() == null) {
                disposable = infoObservable
                        .subscribeOn(Schedulers.io())
                        .doFinally(this::hideProgress)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(callBack, throwable -> getHandleNetworkError());

            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), newUserProfile.getTempPhotoFile());
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", newUserProfile.getTempPhotoFile().getName(), requestFile);
//                RequestBody fileName = RequestBody.create(MediaType.parse("multipart/form-data"), newUserProfile.getTempPhotoFile().getName());
                Observable<User> avatarObservable = apiService.updateAvatar(body);

                disposable = Observable.zip(infoObservable.subscribeOn(Schedulers.io()), avatarObservable.subscribeOn(Schedulers.io()),
                        (infoUser, avatarUser) -> {
//                                FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()
                            Timber.d(infoUser.toString() + " " + avatarUser.toString());
                            return infoUser;
                        })
                        .doFinally(this::hideProgress)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(callBack, throwable -> getHandleNetworkError()
                        );
            }

        }
    }

    private Consumer<User> callBack = user -> {
        ExtensionsKt.saveUser(sharedPreferences, userProfile, user);
        Intent selectPlaceIntent = new Intent(this, SelectIndoorOutdoorActivity.class);
        startActivity(selectPlaceIntent);
        finish();
    };

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
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

    /**
     * Take photo, called from activity_save_profile.xml
     *
     * @param view The view asking for the photo to be taken.
     */
    @SuppressWarnings("UnusedParameters")
    public void takePhoto(View view) {
        CharSequence options[] = new CharSequence[]{getString(R.string.choose_picture), getString(R.string.take_picture)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.profile_picture));
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    pickFromGallery();
                    break;
                case 1:
                    // After Android 6.0 we need to check app permission.
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // We assign an activity wide request id
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA_REQUEST_ID);
                        } else {
                            // Older versions of Android
                            dispatchTakePictureIntent();
                        }
                    }
                    // Permission already granted
                    else {
                        dispatchTakePictureIntent();
                    }
                    break;
            }
            dialog.dismiss();
        });
        builder.show();
    }

    /**
     * Call back of permission request
     *
     * @param requestCode  The ID of the permission request we (randomly) assigned
     * @param permissions  The permission requested
     * @param grantResults Whether the permission was granted or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_CAMERA_REQUEST_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(ProfileActivity.this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * The user chose to select an image from the phone library. After selection, it will be available in onActivityResult.
     */
    private void pickFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoPickerIntent.setType("image/*");

        // Ensure that there's a camera activity to handle the intent
        if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }

    /**
     * The user chose to take a profile picture. This method is called after permissions have been checked. The picture will be made available in the
     * onActivityResult method.
     */
    private File profilePictureTempFile;

    private void dispatchTakePictureIntent() {
        Timber.i("Entering dispatchTakePictureIntent");

        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                // We defined a "provider" in AndroidManifest.xml that helps with the permissions needed when accessing external (Shared between apps) storage.
                // That provider generates an Uri that will reference the file that the camera creates inside this app package. It also tells the path where
                // those files can be put (defined in res/xml/file_paths.xml). In this case that folder is /my_images

                // Define a temporary file where we'll store the picture
                profilePictureTempFile = new File(getExternalFilesDir("my_images"), TEMP_FILENAME);

                // Obtain the URI that will point to that file
                newUserProfile.setTempPhotoFile(profilePictureTempFile);

                // Start the camera and tell it where to store the file
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newUserProfile.getTempPhotoFile());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        } catch (Exception ex) {
            Timber.e(ex, "Cannot take picture");
        }
    }

    /**
     * Process the result of the intents that return something, in this case is the camera or the image selection.
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        returned data from intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.i("Entering onActivityResult with requestCode: " + requestCode + " and result code: " + resultCode);

        // The image where the profile picture will be stored
        Bitmap bitmap = null;

        // Camera image might need to be rotated
        Matrix rotationMatrix = new Matrix();

        // User took a picture. The file was defined in dispatchTakePictureIntent() and saved to profilePictureTempFile
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                // Determine the amount of degrees that the image will be needed to rotate. Usually 270º
                rotationMatrix.postRotate(Utils.getImageRotationAngle(profilePictureTempFile));
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        FileProvider.getUriForFile(this, "com.runin.runinapp.file_provider", newUserProfile.getTempPhotoFile()));
            } catch (Exception ex) {
                Timber.e(ex, "Take photo failed");
            }
        }
        // User selected a picture from the library
        else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                // The selected picture URI
                Uri selectedImageUri = data.getData();

                if (selectedImageUri == null) {
                    throw new Exception("Cannot obtain image from file.");
                }

                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);

                // No need to rotate the image
                rotationMatrix.postRotate(0);

            } catch (Exception ex) {
                Timber.e(ex, "Selected photo failed");
            }
        }
        // Other intent was called
        else {
            Timber.e("Unexpected activity result");
            return;
        }

        if (bitmap != null) {
            // Resize the image to something manageable and crops in the shape of a circle
            Bitmap resized_image = Utils.getResizedAndCircleBitmap(bitmap, profilePictureSize, profilePictureSize);

            // Perform the actual rotation of the image if needed
            Bitmap rotated_image = Bitmap.createBitmap(resized_image, 0, 0, resized_image.getWidth(), resized_image.getHeight(), rotationMatrix, true);

            // Get the bytes of the image to be saved in the user profile
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            rotated_image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            // Update the profile photo in the layout
            profilePhoto.setImageBitmap(rotated_image);

            // Save to a file pointed by profilePictureURI
            File outputFile = new File(getExternalFilesDir("my_images"), FILENAME);
            newUserProfile.setTempPhotoFile(outputFile);
            try {
                OutputStream outputStream = new FileOutputStream(outputFile);
                byteArrayOutputStream.writeTo(outputStream);
                Timber.i("Picture will be available in: %s", newUserProfile.getTempPhotoFile());
            } catch (IOException e) {
                Timber.e(e, "Cannot save file");
            }
        }
    }

    private int validateData() {
        String name = newUserProfile.getName();
        int selectedAge = newUserProfile.getAge();
        float height = newUserProfile.getHeight();
        float weight = newUserProfile.getWeight();

        if (name.isEmpty() || name.length() <= 1 || name.length() > 40) {
            return R.string.enter_valid_name;
        }

//        if (lastname.isEmpty() || lastname.length() <= 1 || lastname.length() > 40) {
//            return R.string.enter_valid_lastname;
//        }

        if (selectedAge < 13 || selectedAge > 100) {
            return R.string.enter_valid_age;
        }

        if (weight < 40 || weight > 180) {
            return R.string.should_validweight;
        }

        if (height < 90 || height > 230) {
            return R.string.should_validheight;
        }

        return 0;
    }
}
