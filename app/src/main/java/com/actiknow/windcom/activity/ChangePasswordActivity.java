package com.actiknow.windcom.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.actiknow.windcom.R;
import com.actiknow.windcom.utils.AppConfigTags;
import com.actiknow.windcom.utils.AppConfigURL;
import com.actiknow.windcom.utils.AppDetailsPref;
import com.actiknow.windcom.utils.Constants;
import com.actiknow.windcom.utils.NetworkConnection;
import com.actiknow.windcom.utils.SetTypeFace;
import com.actiknow.windcom.utils.TypefaceSpan;
import com.actiknow.windcom.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class ChangePasswordActivity extends AppCompatActivity {
    CoordinatorLayout clMain;
    ProgressDialog progressDialog;
    AppDetailsPref appDetailsPref;
    EditText etOldPassword;
    EditText etNewPassword;
    EditText etConfirmPassword;
    ImageView ivSave;
    RelativeLayout rlBack;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_change_password);
        initView ();
        initData ();
        initListener ();
    }
    
    private void initData () {
        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        Utils.setTypefaceToAllViews (this, clMain);
        appDetailsPref = AppDetailsPref.getInstance ();
    }
    
    private void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        etNewPassword = (EditText) findViewById (R.id.etNewPassword);
        etOldPassword = (EditText) findViewById (R.id.etOldPassword);
        etConfirmPassword = (EditText) findViewById (R.id.etConfirmPassword);
        ivSave = (ImageView) findViewById (R.id.ivSave);
        rlBack=(RelativeLayout)findViewById(R.id.rlBack);
    }
    
    private void initListener () {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    
        etNewPassword.addTextChangedListener (new TextWatcher() {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etNewPassword.setError (null);
                }
            }
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged (Editable s) {
            }
        });
        etOldPassword.addTextChangedListener (new TextWatcher() {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etOldPassword.setError (null);
                }
            }
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged (Editable s) {
            }
        });
        etConfirmPassword.addTextChangedListener (new TextWatcher() {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etConfirmPassword.setError (null);
                }
            }
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged (Editable s) {
            }
        });
        ivSave.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Utils.hideSoftKeyboard (ChangePasswordActivity.this);
                SpannableString s1 = new SpannableString(getResources ().getString (R.string.please_enter_password));
                s1.setSpan (new TypefaceSpan(ChangePasswordActivity.this, Constants.font_name), 0, s1.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s2 = new SpannableString(getResources ().getString (R.string.password_not_matched));
                s1.setSpan (new TypefaceSpan (ChangePasswordActivity.this, Constants.font_name), 0, s1.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (etOldPassword.getText ().toString ().trim ().length () == 0) {
                    etOldPassword.setError (s1);
                }
                if (etNewPassword.getText ().toString ().trim ().length () == 0) {
                    etNewPassword.setError (s1);
                }
                if (etConfirmPassword.getText ().toString ().trim ().length () == 0) {
                    etConfirmPassword.setError (s1);
                }
    
                if (etConfirmPassword.getText ().toString ().trim ().equalsIgnoreCase (etNewPassword.getText ().toString ().trim ()) &&
                        etOldPassword.getText ().toString ().trim ().length () > 0 &&
                        etNewPassword.getText ().toString ().trim ().length () > 0 &&
                        etConfirmPassword.getText ().toString ().trim ().length () > 0) {
                    sendPasswordChangeToServer (etOldPassword.getText ().toString ().trim (), etNewPassword.getText ().toString ().trim ());
                } else {
                    etConfirmPassword.setError (s2);
                }
            }
        });
    }
    
    private void sendPasswordChangeToServer (final String oldPassword, final String newPassword) {
        if (NetworkConnection.isNetworkAvailable (ChangePasswordActivity.this)) {
            Utils.showProgressDialog(ChangePasswordActivity.this, progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.CHANGE_PASSWORD, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.CHANGE_PASSWORD,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        new MaterialDialog.Builder (ChangePasswordActivity.this)
                                                .content (message)
                                                .positiveText ("OK")
                                                .typeface (SetTypeFace.getTypeface (ChangePasswordActivity.this), SetTypeFace.getTypeface (ChangePasswordActivity.this))
                                                .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                    @Override
                                                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss ();
                                                        finish ();
                                                    }
                                                })
                                                .show ();
                                    } else {
                                        Utils.showSnackBar (ChangePasswordActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (ChangePasswordActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (ChangePasswordActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (ChangePasswordActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put (AppConfigTags.OLD_PASSWORD, oldPassword);
                    params.put (AppConfigTags.NEW_PASSWORD, newPassword);
                    
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.LOGIN_KEY, appDetailsPref.getStringPref(ChangePasswordActivity.this, AppDetailsPref.LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
            Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }
    
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
}