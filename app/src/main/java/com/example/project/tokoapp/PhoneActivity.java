package com.example.project.tokoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;

/**
 * Created by christian on 08/03/18.
 */

public class PhoneActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "PhoneAuthActivity";
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private FirebaseAuth mAuth;
    private PrefUtil prefUtil;
    private String status;

    private boolean mVerificationInProgress = false;
    private String mVerificationId, phoneId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private ViewGroup mSignedInViews;

    private TextView mStatusText;
    private TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;
    private EditText mFullName;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mSignOutButton;

    private ProgressDialog pDialog;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_layout);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                status = i.getString("status");
            } catch (Exception e) {}
        }
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        mPhoneNumberViews = (ViewGroup) findViewById(R.id.phone_auth_fields);
        mSignedInViews = (ViewGroup) findViewById(R.id.signed_in_buttons);

        mStatusText = (TextView) findViewById(R.id.status);
        mDetailText = (TextView) findViewById(R.id.detail);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);
        mFullName = (EditText) findViewById(R.id.field_nama);

        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (Button) findViewById(R.id.button_resend);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mApiService         = Link.getAPIService();
        mAuth = FirebaseAuth.getInstance();
        prefUtil            = new PrefUtil(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        if(status.equals("LOGIN")){
            mFullName.setVisibility(View.GONE);
        }else{
            mFullName.setVisibility(View.VISIBLE);
        }
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                mVerificationInProgress = false;
                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }
                updateUI(STATE_VERIFY_FAILED);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                updateUI(STATE_CODE_SENT);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            if(status.equals("LOGIN")){
                                requestLogin(PrefUtil.PHONE, phoneId, user.getUid());
                            }else{//register
                                requestRegister(mFullName.getText().toString(), "", user.getUid(), "", "", PrefUtil.PHONE, phoneId);
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                            updateUI(STATE_SIGNIN_FAILED);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField, mFullName);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField, mFullName);
                disableViews(mStartButton);
                mDetailText.setText("Code Sent");
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField, mFullName);
                mDetailText.setText("Verification failed");
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField, mFullName);
                mDetailText.setText("Verification succeeded");

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText("(instant validation)");
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                mDetailText.setText("Sign-in failed");
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

        if (user == null) {
            // Signed out
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);

            mStatusText.setText("Signed Out");
        } else {
            // Signed in
            mPhoneNumberViews.setVisibility(View.GONE);
            mSignedInViews.setVisibility(View.VISIBLE);

            enableViews(mPhoneNumberField, mVerificationField, mFullName);
            mPhoneNumberField.setText(null);
            mVerificationField.setText(null);
            mFullName.setText(null);

            mStatusText.setText("Signed In");
            mDetailText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private boolean validateName() {
        String name = mFullName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mFullName.setError("Invalid Name");
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                if(status.equals("REGISTER")){
                    if (!validateName()) {
                        return;
                    }
                }
                phoneId = mPhoneNumberField.getText().toString();
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void requestLogin(final String account, String phone, String id){
        String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        pDialog.setMessage("Login ...");
        showDialog();
        mApiService.loginRequest("", "", id, "", "", tanggalNow, device, account, phone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            //Log.i("debug", "onResponse: BERHASIL");
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    String nama = jsonRESULTS.getJSONObject("user").getString("cFullName")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cFullName");
                                    String email = jsonRESULTS.getJSONObject("user").getString("cEmail")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cEmail");
                                    String uId = jsonRESULTS.getJSONObject("user").getString("cUserId")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cUserId");
                                    String foto = jsonRESULTS.getJSONObject("user").getString("cPhoto")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cPhoto");
                                    String gender = jsonRESULTS.getJSONObject("user").getString("cGender")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cGender");
                                    String birthday = jsonRESULTS.getJSONObject("user").getString("dBirthDate")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("dBirthDate");
                                    String phone = jsonRESULTS.getJSONObject("user").getString("cPhoneId")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cPhoneId");
                                    String alamat = jsonRESULTS.getJSONObject("user").getString("vcAlamat")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("vcAlamat");
                                    Integer countToko = jsonRESULTS.getJSONObject("user").getInt("countMerchant");
                                    prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.PHONE
                                            , birthday, phone, alamat, countToko);
                                    Toast.makeText(PhoneActivity.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(PhoneActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                } else {
                                    FirebaseAuth.getInstance().signOut();
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(PhoneActivity.this, error_message, Toast.LENGTH_LONG).show();
                                }
                                hideDialog();
                            } catch (JSONException e) {
                                FirebaseAuth.getInstance().signOut();
                                e.printStackTrace();
                                hideDialog();
                            } catch (IOException e) {
                                FirebaseAuth.getInstance().signOut();
                                e.printStackTrace();
                                hideDialog();
                            }
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            hideDialog();
                            Toast.makeText(PhoneActivity.this, "GAGAL LOGIN", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        FirebaseAuth.getInstance().signOut();
                        hideDialog();
                        Toast.makeText(PhoneActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void requestRegister(String nama, String email, String id, String tgl, String foto,
                                 final String account, final String phone){
        String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        if(tgl==null || tgl.equals("")) tgl="1900-01-01";
        if(foto==null) foto = "";
        mApiService.registerRequest(nama, email, id, tgl, "", tanggalNow, device, account, phone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    String nama = jsonRESULTS.getJSONObject("user").getString("cFullName")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cFullName");
                                    String email = jsonRESULTS.getJSONObject("user").getString("cEmail")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cEmail");
                                    String uId = jsonRESULTS.getJSONObject("user").getString("cUserId")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cUserId");
                                    String foto = jsonRESULTS.getJSONObject("user").getString("cPhoto")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cPhoto");
                                    String gender = jsonRESULTS.getJSONObject("user").getString("cGender")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cGender");
                                    String birthday = jsonRESULTS.getJSONObject("user").getString("dBirthDate")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("dBirthDate");
                                    String phone = jsonRESULTS.getJSONObject("user").getString("cPhoneId")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("cPhoneId");
                                    String alamat = jsonRESULTS.getJSONObject("user").getString("vcAlamat")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("vcAlamat");
                                    Integer countToko = jsonRESULTS.getJSONObject("user").getInt("countMerchant");
                                    hideDialog();
                                    prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.PHONE
                                            , birthday, phone, alamat, countToko);
                                    Toast.makeText(PhoneActivity.this, "BERHASIL REGISTRASI", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(PhoneActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                } else {
                                    FirebaseAuth.getInstance().signOut();
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(PhoneActivity.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            } catch (JSONException e) {
                                FirebaseAuth.getInstance().signOut();
                                hideDialog();
                                e.printStackTrace();
                            } catch (IOException e) {
                                FirebaseAuth.getInstance().signOut();
                                hideDialog();
                                e.printStackTrace();
                            }
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            hideDialog();
                            Toast.makeText(PhoneActivity.this, "REGISTRASI GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        FirebaseAuth.getInstance().signOut();
                        hideDialog();
                        Toast.makeText(PhoneActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
