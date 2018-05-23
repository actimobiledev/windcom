package com.actiknow.windcom.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.windcom.Manifest;
import com.actiknow.windcom.R;
import com.actiknow.windcom.model.Questions;
import com.actiknow.windcom.utils.AppConfigTags;
import com.actiknow.windcom.utils.AppConfigURL;
import com.actiknow.windcom.utils.AppDetailsPref;
import com.actiknow.windcom.utils.Constants;
import com.actiknow.windcom.utils.NetworkConnection;
import com.actiknow.windcom.utils.SetTypeFace;
import com.actiknow.windcom.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    LinearLayout llMain;
    ImageView ivSave;
    CoordinatorLayout clMain;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    Bundle savedInstanceState;
    AppDetailsPref appDetailsPref;

    TextView tvQuestionTypeQuestionOnly;
    EditText etQuestionType1;
    TextView tvQuestionTypeInput;
    ProgressDialog progressDialog;
    TextView tvQuestionTypeRadio;
    RadioGroup rgMain;

    TextView tvQuestionTypeCheck;
    LinearLayout llQuestionOption;

    TextView tvQuestionTypeImage;
    ImageView ivImage;

    TextView tvQuestionTypeUploadImage;
    TextView tvUploadImage;
    ImageView ivImageUpload;


    List<String> checkItemList = new ArrayList<>();
    List<Questions> questionsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
        isLogin();
        initDrawer();
        getQuestionList();
        checkPermissions();

    }

    private void isLogin() {
        if (appDetailsPref.getStringPref(MainActivity.this, AppDetailsPref.LOGIN_KEY).length() == 0) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


    private void createDynamicallyView() {
        // Log.e("size","fsdfsdf"+questionsList.size());
        //Question Type (1 => Question Only, 2 => Input Type, 3 => Radio Button, 4 => Checkboxes, 5 => Image Display, 6 => Image Upload), Question Style (0 => Normal, 1 => Bold, 2 => Italic)",

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int type = 0; type < questionsList.size(); type++) {
            Questions questions = questionsList.get(type);
            int questionType = questions.getQuestion_type();
            switch (questionType) {

                case 1:
                    final View addView1 = layoutInflater.inflate(R.layout.question_type_question_only, null);
                    tvQuestionTypeQuestionOnly = (TextView) addView1.findViewById(R.id.tvQuestionTypeQuestionOnly);
                    tvQuestionTypeQuestionOnly.setText(questions.getQuestion_text());
                    llMain.addView(addView1);
                    break;

                case 2:
                    final View addView2 = layoutInflater.inflate(R.layout.question_type_edit_text, null);
                    etQuestionType1 = (EditText) addView2.findViewById(R.id.etQuestionType1);
                    tvQuestionTypeInput = (TextView) addView2.findViewById(R.id.tvQuestionType1);
                    tvQuestionTypeInput.setText(questions.getQuestion_text());
                    llMain.addView(addView2);
                    break;

                case 3:
                    final View addView3 = layoutInflater.inflate(R.layout.question_type_radio_button, null);
                    tvQuestionTypeRadio = (TextView) addView3.findViewById(R.id.tvQuestionType2);
                    rgMain = (RadioGroup) addView3.findViewById(R.id.rgMain);
                    tvQuestionTypeRadio.setText(questions.getQuestion_text());
                    List<String> radioOptionList = Arrays.asList(questions.getQuestion_options().replaceAll("\\s", "").split(";"));


                    for (int i = 0; i < radioOptionList.size(); i++) {
                        RadioButton rdbtn = new RadioButton(this);
                        rdbtn.setId(i);
                        rdbtn.setText(radioOptionList.get(i));
                        rgMain.addView(rdbtn);

                        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                // checkedId is the RadioButton selected
                                RadioButton rb = (RadioButton) findViewById(checkedId);
                                Log.e("You Selected ", rb.getText().toString());
                                //Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    llMain.addView(addView3);
                    break;


                case 4:
                    final View addView4 = layoutInflater.inflate(R.layout.question_type_check_box, null);
                    tvQuestionTypeCheck = (TextView) addView4.findViewById(R.id.tvQuestionType3);
                    llQuestionOption = (LinearLayout) addView4.findViewById(R.id.llQuestionOption);
                    tvQuestionTypeCheck.setText(questions.getQuestion_text());
                    List<String> checkBoxOptionList = Arrays.asList(questions.getQuestion_options().replaceAll("\\s", "").split(";"));

                    for (int i = 0; i < checkBoxOptionList.size(); i++) {
                        final CheckBox cbOption = new CheckBox(this);
                        cbOption.setId(i);
                        cbOption.setText(checkBoxOptionList.get(i));
                        llQuestionOption.addView(cbOption);

                        cbOption.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View arg0) {
                                if (cbOption.isChecked()) {
                                    cbOption.setChecked(true);
                                    checkItemList.add(cbOption.getText().toString());
                                    Log.d("add", checkItemList.toString());
                                } else {
                                    cbOption.setChecked(false);
                                    checkItemList.remove(cbOption.getText().toString());
                                    Log.d("remove", checkItemList.toString());
                                }
                            }
                        });
                    }
                    llMain.addView(addView4);

                    break;


                case 5:
                    final View addView5 = layoutInflater.inflate(R.layout.question_type_image, null);
                    tvQuestionTypeImage = (TextView) addView5.findViewById(R.id.tvQuestionType4);
                    ivImage = (ImageView) addView5.findViewById(R.id.ivImage);
                    tvQuestionTypeImage.setText(questions.getQuestion_text());
                    llMain.addView(addView5);
                    break;

                case 6:
                    final View addView6 = layoutInflater.inflate(R.layout.question_type_upload_image, null);
                    tvQuestionTypeUploadImage = (TextView) addView6.findViewById(R.id.tvQuestionType5);
                    tvUploadImage = (TextView) addView6.findViewById(R.id.tvUploadImage);
                    ivImageUpload = (ImageView) addView6.findViewById(R.id.ivImageUpload);
                    tvQuestionTypeUploadImage.setText(questions.getQuestion_text());
                    tvUploadImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectImage();
                        }
                    });
                    llMain.addView(addView6);
                    break;
            }
        }
    }

    private void initView() {
        llMain = (LinearLayout) findViewById(R.id.llMain);
        ivSave = (ImageView) findViewById(R.id.ivSave);
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
    }

    private void initData() {
        progressDialog = new ProgressDialog(this);
        appDetailsPref = AppDetailsPref.getInstance();
        Utils.setTypefaceToAllViews(this, clMain);
    }

    private void initListener() {
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void getQuestionList() {
        if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.GET_SURVEY_DETAIL, true);
            Utils.showProgressDialog(MainActivity.this, progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            StringRequest strRequest = new StringRequest(Request.Method.GET, AppConfigURL.GET_SURVEY_DETAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    questionsList.clear();
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!is_error) {
                                        JSONArray jsonArray = jsonObj.getJSONArray(AppConfigTags.QUESTIONS);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                                            questionsList.add(i, new Questions(
                                                    jsonObject.getInt(AppConfigTags.QUESTIONNAIRE_ID),
                                                    jsonObject.getInt(AppConfigTags.QUESTION_ID),
                                                    jsonObject.getString(AppConfigTags.QUESTION_TEXT),
                                                    jsonObject.getString(AppConfigTags.QUESTION_OPTION),
                                                    jsonObject.getInt(AppConfigTags.QUESTION_TYPE),
                                                    jsonObject.getInt(AppConfigTags.QUESTION_STYLE)
                                            ));
                                        }
                                        createDynamicallyView();
                                    } else {
                                        Utils.showSnackBar(MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                }
                            } else {
                                Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss();
                        }

                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    AppDetailsPref appDetailsPref = AppDetailsPref.getInstance();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    //    params.put (AppConfigTags.HEADER_EMPLOYEE_LOGIN_KEY, appDetailsPref.getStringPref (MainActivity.this, AppDetailsPref.EMPLOYEE_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest, 5);
        } else {

            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }

    private void initDrawer() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                if (uri != null) {
                    Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.colorPrimary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_white_1000);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withTypeface(SetTypeFace.getTypeface(MainActivity.this))
                .withPaddingBelowHeader(false)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesVisible(true)
                .withDividerBelowHeader(true)
                .withTextColor(getResources().getColor(R.color.primary_text))
                .withOnlyMainProfileImageVisible(false)
                .withDividerBelowHeader(true)
                .withHeaderBackground(R.color.text_color_grey_light2)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem();
        profileDrawerItem.withName(appDetailsPref.getStringPref(MainActivity.this, AppDetailsPref.RESPONDANT_NAME));
        profileDrawerItem.withEmail(appDetailsPref.getStringPref(MainActivity.this, AppDetailsPref.RESPONDANT_EMAIL));


     /*   if (appDetailsPref.getStringPref (MainActivity.this, AppDetailsPref.EMPLOYEE_IMAGE).length () > 0) {
            profileDrawerItem.withIcon (appDetailsPref.getStringPref (MainActivity.this, AppDetailsPref.EMPLOYEE_IMAGE));
        } else {
            if (appDetailsPref.getStringPref (MainActivity.this, AppDetailsPref.EMPLOYEE_GENDER).equalsIgnoreCase ("F")) {
                profileDrawerItem.withIcon (R.drawable.ic_profile_female);
            } else {
                profileDrawerItem.withIcon (R.drawable.ic_profile_male);
            }
        }*/
        headerResult.addProfiles(profileDrawerItem);

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {

                            case 6:
                                Intent intent6 = new Intent(MainActivity.this, ChangePasswordActivity.class);
                                startActivity(intent6);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 7:
                                showLogOutDialog();
                                break;
                        }
                        return false;
                    }
                });


        drawerBuilder.addDrawerItems(
                new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                new PrimaryDrawerItem().withName("Change Password").withIcon(FontAwesome.Icon.faw_asterisk).withIdentifier(6).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                new PrimaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(7).withSelectable(false).withTypeface(SetTypeFace.getTypeface(MainActivity.this))
        );


        result = drawerBuilder.build();
    }

    private void selectImage() {
        final CharSequence[] options = {"From Camera", "From Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("From Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = Utils.compressBitmap(BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions), MainActivity.this);


                    ivImageUpload.setImageBitmap(bitmap);

                    String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = Utils.compressBitmap(BitmapFactory.decodeFile(picturePath), MainActivity.this);
                ivImageUpload.setImageBitmap(thumbnail);

                //    imagesPathList.add (Utils.bitmapToBase64 (thumbnail));


            }
        }
    }


    private void showLogOutDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .contentColor(getResources().getColor(R.color.primary_text))
                .positiveColor(getResources().getColor(R.color.primary_text))
                .negativeColor(getResources().getColor(R.color.primary_text))
                .content("Do you wish to Sign Out?")
                .positiveText("Yes")
                .negativeText("No")
                .typeface(SetTypeFace.getTypeface(MainActivity.this), SetTypeFace.getTypeface(MainActivity.this))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        appDetailsPref.putStringPref(MainActivity.this, AppDetailsPref.RESPONDANT_NAME, "");
                        appDetailsPref.putStringPref(MainActivity.this, AppDetailsPref.LOGIN_KEY, "");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }).build();
        dialog.show();
    }


    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA, INTERNET, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Permission are required please enable them on the App Setting page")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        startActivity(intent);
                                    }
                                });
                        android.support.v7.app.AlertDialog alert = builder.create();
                        alert.show();
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (CAMERA.equals(permission)) {
//                        Utils.showToast (this, "Camera Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    } else if (WRITE_EXTERNAL_STORAGE.equals(permission)) {
//                        Utils.showToast (this, "Write Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    }
                }
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

}
