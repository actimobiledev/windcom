package com.actiknow.windcom.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.actiknow.windcom.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
    //  int type = 3;

    TextView tvQuestionTypeQuestionOnly;

    EditText etQuestionType1;
    TextView tvQuestionTypeInput;

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
    List<Questions> questionsList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   checkPermissions();
        initView();
        initData();
        initListener();
        getQuestionList();
        checkPermissions();

    }

    private void createDynamicallyView() {
        Log.e("size","fsdfsdf"+questionsList.size());

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int type = 0; type < questionsList.size(); type++) {
            Questions questions=questionsList.get(type);
            int questionType=questions.getQuestion_type();
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
                                doCheck(cbOption);
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
                    tvQuestionTypeUploadImage=(TextView)addView6.findViewById(R.id.tvQuestionType5);
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
        clMain=(CoordinatorLayout)findViewById(R.id.clMain);

    }

    private void initData() {

        //type 1- edit text
        //type 2- radio button
        //type 3- check box
        //type 4- image
        //type 5- upload image

    }

    private void initListener() {




        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    protected void doCheck(CheckBox check) {
        try {
            Toast.makeText(this, check.getText(), Toast.LENGTH_SHORT).show();
           /* for(int i=0;i<llQuestionOption.getChildCount();i++)
            {
                CheckBox checkbox=(CheckBox)llQuestionOption.getChildAt(i);
                checkbox.setChecked(false);
            }*/

            if (check.isChecked()) {
                check.setChecked(true);
                checkItemList.add(check.getText().toString());
            } else {
                check.setChecked(false);
                checkItemList.add(check.getText().toString());
            }

            String strCheck2 = check.getText().toString();
            Log.e("strCheck2", strCheck2);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                ivImageUpload.setImageBitmap (thumbnail);

                //    imagesPathList.add (Utils.bitmapToBase64 (thumbnail));


            }
        }
    }

    public void getQuestionList () {
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.GET_SURVEY_DETAIL, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.GET_SURVEY_DETAIL,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    questionsList.clear ();
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! is_error) {
                                        JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.QUESTIONS);
                                        for (int i = 0; i < jsonArray.length (); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject (i);

                                            questionsList.add (i, new Questions (
                                                    jsonObject.getInt (AppConfigTags.QUESTIONNAIRE_ID),
                                                    jsonObject.getInt (AppConfigTags.QUESTION_ID),
                                                    jsonObject.getString (AppConfigTags.QUESTION_TEXT),
                                                    jsonObject.getString (AppConfigTags.QUESTION_OPTION),
                                                    jsonObject.getInt (AppConfigTags.QUESTION_TYPE),
                                                    jsonObject.getInt (AppConfigTags.QUESTION_STYLE)
                                            ));
                                        }
                                        createDynamicallyView();

                                     
                                    } else {
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                }
                            } else {
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                          
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                        
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String (response.data), true);
                            }
                            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {

                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    AppDetailsPref appDetailsPref = AppDetailsPref.getInstance ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                //    params.put (AppConfigTags.HEADER_EMPLOYEE_LOGIN_KEY, appDetailsPref.getStringPref (MainActivity.this, AppDetailsPref.EMPLOYEE_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 5);
        } else {
         
            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }



    public void checkPermissions () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission (CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission (WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {CAMERA, INTERNET, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale (permission);
                    if (! showRationale) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (MainActivity.this);
                        builder.setMessage ("Permission are required please enable them on the App Setting page")
                                .setCancelable (false)
                                .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                    public void onClick (DialogInterface dialog, int id) {
                                        dialog.dismiss ();
                                        Intent intent = new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts ("package", getPackageName (), null));
                                        startActivity (intent);
                                    }
                                });
                        android.support.v7.app.AlertDialog alert = builder.create ();
                        alert.show ();
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (CAMERA.equals (permission)) {
//                        Utils.showToast (this, "Camera Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    } else if (WRITE_EXTERNAL_STORAGE.equals (permission)) {
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
