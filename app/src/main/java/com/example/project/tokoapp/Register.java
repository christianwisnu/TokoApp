package com.example.project.tokoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import utilities.PrefUtil;
import service.BaseApiService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import utilities.Link;
import utilities.PatternEditableBuilder;
import utilities.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by christian on 05/01/18.
 */

public class Register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "GoogleRegister";
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 9001;
    private PrefUtil prefUtil;
    private ProgressDialog pDialog;
    private LoginButton btnRegisterFb;
    private SignInButton btnRegisterGoogle;
    private TextView txtSyarat;
    private BaseApiService mApiService;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth;

    @BindView(R.id.bRegisterDaftar)Button btnRegister;
    @BindView(R.id.btnRegisterClearEmail)Button btnClearEmail;
    @BindView(R.id.input_layout_register_email)TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_layout_register_sandi)TextInputLayout inputLayoutPasw;
    @BindView(R.id.input_layout_register_sandi2)TextInputLayout inputLayoutPasw2;
    @BindView(R.id.input_layout_register_nama)TextInputLayout inputLayoutNama;
    @BindView(R.id.eRegisterEmail)EditText eEmail;
    @BindView(R.id.eRegisterSandi)EditText ePassword;
    @BindView(R.id.eRegisterSandi2)EditText ePassword2;
    @BindView(R.id.eRegisterNama)EditText eNama;
    @BindView(R.id.lineRegisterGoogle)LinearLayout linGoogle;
    @BindView(R.id.lineRegisterFB)LinearLayout linFb;
    @BindView(R.id.lineRegisterPhone)LinearLayout linPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.register);
        ButterKnife.bind(this);
        mApiService         = Link.getAPIService();
        mAuth = FirebaseAuth.getInstance();
        prefUtil            = new PrefUtil(this);
        btnRegisterFb       = (LoginButton) findViewById(R.id.register_button_fb);
        btnRegisterGoogle   = (SignInButton)findViewById(R.id.register_button_google);
        txtSyarat           = (TextView) findViewById(R.id.txtRegisterSyarat);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        txtSyarat.setText("Saya telah membaca dan memahami syarat penggunaan dan kebijakan privasi TokoApp");

        btnRegisterFb.setReadPermissions(Arrays.asList("email","public_profile","user_birthday"));

        btnRegisterFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                pDialog.setMessage("Preparing...");
                showDialog();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), R.string.cancel_login_fb, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplication(), R.string.error_login_fb, Toast.LENGTH_LONG).show();
                Utils.deleteAccessToken(prefUtil);
            }
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(Register.this)
                .enableAutoManage(Register.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } )
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        btnRegisterGoogle.setVisibility(View.GONE);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("syarat penggunaan"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(getApplication(), "Clicked username: " + text,
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                .addPattern(Pattern.compile("kebijakan privasi"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(getApplication(), "Clicked username: " + text,
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                .into(txtSyarat);

    }

    @OnClick(R.id.btnRegisterClearEmail)
    protected void regClearEmail(){
        eEmail.setText("");
        btnClearEmail.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.lineRegisterFB)
    protected void loginFbku(){
        btnRegisterFb.performClick();
    }

    @OnClick(R.id.lineRegisterGoogle)
    protected void loginGoogleku(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.lineRegisterPhone)
    protected void loginPhoneku(){
        Intent i  = new Intent(Register.this, PhoneActivity.class);
        i.putExtra("status", "REGISTER");
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick(R.id.bRegisterDaftar)
    protected void register(){
        if(validateEmailorPhone(eEmail) && validateNama(eNama.length()) &&
                validatePasw(ePassword.length()) && validatePasw2(ePassword2.length())){
            registerManual(eNama.getText().toString(), eEmail.getText().toString(), ePassword.getText().toString(), PrefUtil.OTHER);
        }
    }

    @OnTextChanged(value = R.id.eRegisterEmail, callback = OnTextChanged.Callback.TEXT_CHANGED)
    protected void txtChangePass(){
        btnClearEmail.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(value = R.id.eRegisterEmail, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeEmail(Editable editable){
        validateEmailorPhone(eEmail);
    }

    @OnTextChanged(value = R.id.eRegisterSandi, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeSandi(Editable editable){
        validatePasw(editable.length());
    }

    @OnTextChanged(value = R.id.eRegisterSandi2, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeSandi2(Editable editable){
        validatePasw2(editable.length());
    }

    @OnTextChanged(value = R.id.eRegisterNama, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeNama(Editable editable){
        validateNama(editable.length());
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideDialog();
                            LoginManager.getInstance().logOut();
                            FirebaseAuth.getInstance().signOut();
                        }else{
                            FirebaseUser user = task.getResult().getUser();
                            String uid="", name="", email="";
                            Uri image=null;
                            email = user.getEmail();
                            for(UserInfo profile : user.getProviderData()) {
                                if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                                    image = profile.getPhotoUrl();
                                    uid = profile.getUid();
                                    name = profile.getDisplayName();
                                }
                            }
                            requestRegister(name, email, user.getUid(), null, image==null?"":image.toString(), PrefUtil.FB);
                        }
                    }
                });
    }

    private void registerManual(final String nama, final String email, final String pasw, final String account){
        pDialog.setMessage("Registering ...");
        showDialog();
        mAuth.createUserWithEmailAndPassword(email, pasw)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            hideDialog();
                            Toast.makeText(Register.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            if(account.equals(PrefUtil.FB)){
                                LoginManager.getInstance().logOut();
                                FirebaseAuth.getInstance().signOut();
                            }else{//OTHER
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else {
                            FirebaseUser user = task.getResult().getUser();
                            requestRegister(nama, email, user.getUid(), null, null, PrefUtil.OTHER);
                        }
                    }
                });
    }

    private void requestRegister(String nama, String email, String id, String tgl, String foto,
                                 final String account){
        pDialog.setMessage("Registering ...");
        showDialog();
        String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        if(tgl==null || tgl.equals("")) tgl="1900-01-01";
        if(foto==null) foto = "";
        mApiService.registerRequest(nama, email, id, tgl, foto, tanggalNow, device, account, "")
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
                                    if(account.equals(PrefUtil.OTHER)){
                                        sendVerificationEmail();
                                    }else{
                                        hideDialog();
                                        if(account.equals(PrefUtil.FB)){
                                            prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.FB, birthday, phone, alamat, countToko);
                                        }else if(account.equals(PrefUtil.GL)){
                                            prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.GL, birthday, phone, alamat, countToko);
                                        }//OTHER tidak save krn msuk ke login activity
                                        Toast.makeText(Register.this, "BERHASIL REGISTRASI", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Register.this, MainActivity.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }
                                } else {
                                    if(account.equals(PrefUtil.FB)){
                                        LoginManager.getInstance().logOut();
                                        FirebaseAuth.getInstance().signOut();
                                    }else{//OTHER
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(Register.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            } catch (JSONException e) {
                                if(account.equals(PrefUtil.FB)){
                                    LoginManager.getInstance().logOut();
                                    FirebaseAuth.getInstance().signOut();
                                }else{//OTHER
                                    FirebaseAuth.getInstance().signOut();
                                }
                                hideDialog();
                                e.printStackTrace();
                            } catch (IOException e) {
                                if(account.equals(PrefUtil.FB)){
                                    LoginManager.getInstance().logOut();
                                    FirebaseAuth.getInstance().signOut();
                                }else{//OTHER
                                    FirebaseAuth.getInstance().signOut();
                                }
                                hideDialog();
                                e.printStackTrace();
                            }
                        } else {
                            if(account.equals(PrefUtil.FB)){
                                LoginManager.getInstance().logOut();
                                FirebaseAuth.getInstance().signOut();
                            }else{//OTHER
                                FirebaseAuth.getInstance().signOut();
                            }
                            hideDialog();
                            Toast.makeText(Register.this, "REGISTRASI GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if(account.equals(PrefUtil.FB)){
                            LoginManager.getInstance().logOut();
                            FirebaseAuth.getInstance().signOut();
                        }else{//OTHER
                            FirebaseAuth.getInstance().signOut();
                        }
                        hideDialog();
                        Toast.makeText(Register.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationEmail(){
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideDialog();
                        if (task.isSuccessful()) {
                            new AlertDialog.Builder(Register.this).setTitle("Notification").setMessage("Verification email sent to " + user.getEmail()+".\n Please check your email!").setIcon(R.drawable.tick).setNeutralButton("Close", null).show();
                            startActivity(new Intent(Register.this, Login.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void navigateUp() {
        final Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
            TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
        } else {
            NavUtils.navigateUpTo(this, upIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateUp();
        finish();
    }

    private boolean validateEmailorPhone(EditText edittext) {
        boolean value;
        if (eEmail.getText().toString().isEmpty()
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(eEmail.getText().toString()).matches()){
            value=false;
            requestFocus(eEmail);
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
        } else if (edittext.length() > inputLayoutEmail.getCounterMaxLength()) {
            value=false;
            inputLayoutEmail.setError("Max character email length is " + inputLayoutEmail.getCounterMaxLength());
        }else {
            value=true;
            inputLayoutEmail.setError(null);
        }
        return value;
    }

    private boolean validatePasw(int length) {
        boolean value=true;
        int minValue = 6;
        if (ePassword.getText().toString().trim().isEmpty()) {
            value=false;
            requestFocus(ePassword);
            inputLayoutPasw.setError(getString(R.string.err_msg_sandi));
        } else if (length > inputLayoutPasw.getCounterMaxLength()) {
            value=false;
            inputLayoutPasw.setError("Max character password length is " + inputLayoutPasw.getCounterMaxLength());
        } else if (length < minValue) {
            value=false;
            inputLayoutPasw.setError("Min character password length is 6" );
        } else{
            value=true;
            inputLayoutPasw.setError(null);}
        return value;
    }

    private boolean validatePasw2(int length) {
        boolean value=true;
        int minValue = 6;
        if (ePassword2.getText().toString().trim().isEmpty()) {
            value=false;
            requestFocus(ePassword2);
            inputLayoutPasw2.setError(getString(R.string.err_msg_sandi));
        } else if (length > inputLayoutPasw2.getCounterMaxLength()) {
            value=false;
            inputLayoutPasw2.setError("Max character password length is " + inputLayoutPasw2.getCounterMaxLength());
        } else if (length < minValue) {
            value=false;
            inputLayoutPasw2.setError("Min character password length is 6" );
        } else if(!ePassword2.getText().toString().equals(ePassword.getText().toString())){
            value=false;
            inputLayoutPasw2.setError("Confirm Password is wrong");
        }else{
            value=true;
            inputLayoutPasw2.setError(null);
        }
        return value;
    }

    private boolean validateNama(int length) {
        boolean value=true;
        if (eNama.getText().toString().trim().isEmpty()) {
            value=false;
            requestFocus(eNama);
            inputLayoutNama.setError(getString(R.string.err_msg_nama));
        } else if (length > inputLayoutNama.getCounterMaxLength()) {
            value=false;
            inputLayoutNama.setError("Max character name length is " + inputLayoutNama.getCounterMaxLength());
        }else {
            value=true;
            inputLayoutNama.setError(null);
        }
        return value;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            if(resultCode == RESULT_OK) {
                pDialog.setMessage("Preparing ...");
                showDialog();
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()){
                    GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
                    FirebaseUserAuth(googleSignInAccount);
                }
            }
        }
    }

    private void FirebaseUserAuth(final GoogleSignInAccount gsa) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(gsa.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(Register.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {
                        if (AuthResultTask.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            requestRegister(gsa.getDisplayName(), gsa.getEmail(),firebaseUser.getUid(), null,
                                    gsa.getPhotoUrl()==null?"":gsa.getPhotoUrl().getPath(),
                                    PrefUtil.GL);
                        }else {
                            FirebaseAuth.getInstance().signOut();
                            hideDialog();
                            Toast.makeText(Register.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
