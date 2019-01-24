package com.example.project.tokoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
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

import utilities.PrefUtil;
import service.BaseApiService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import utilities.Link;
import utilities.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by christian on 29/01/18.
 */

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private CallbackManager callbackManager;
    private static final String TAG = "GoogleSignIn";
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private PrefUtil prefUtil;
    private LoginButton btnLoginFb;
    private BaseApiService mApiService;
    private ProgressDialog pDialog;
    private SignInButton btnLoginGoogle;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    @BindView(R.id.bLoginLogin) Button btnLogin;
    @BindView(R.id.btnLoginClearEmail) Button btnClearEmail;
    @BindView(R.id.input_layout_login_email) TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_layout_login_sandi) TextInputLayout inputLayoutPasw;
    @BindView(R.id.eLoginEmail) EditText eEmail;
    @BindView(R.id.eLoginSandi) EditText ePassword;
    @BindView(R.id.txtLoginSignUp) TextView txtSignUp;
    @BindView(R.id.txtLoginLupaSandi) TextView txtLupa;
    @BindView(R.id.txtLoginStatusVerifyEmail) TextView txtStatus;
    @BindView(R.id.linLoginGoogle) LinearLayout linGoogle;
    @BindView(R.id.linLoginFB) LinearLayout linFb;
    @BindView(R.id.lineLoginPhone) LinearLayout linPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        mApiService = Link.getAPIService();
        prefUtil = new PrefUtil(this);
        //logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("IDR"));
        btnLoginFb      = (LoginButton)findViewById(R.id.login_button_fb);
        mAuth = FirebaseAuth.getInstance();

        btnLoginGoogle  = (SignInButton)findViewById(R.id.login_button_google);
        btnLoginFb.setReadPermissions(Arrays.asList("email","public_profile","user_birthday"));

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnLoginFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
            }
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(Login.this)
                .enableAutoManage(Login.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } )
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        btnLoginGoogle.setVisibility(View.GONE);
    }

    @OnTextChanged(value = R.id.eLoginEmail, callback = OnTextChanged.Callback.TEXT_CHANGED)
    protected void txtChangePass(){
        btnClearEmail.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(value = R.id.eLoginSandi, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangePass(Editable editable){
        validatePasw(editable.length());
    }

    /*@OnTextChanged(value = R.id.eLoginEmail, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void txtChangeEmail(Editable editable){
        validateEmail(editable.length());
    }*/

    @OnClick(R.id.txtLoginSignUp)
    protected void signUp(){
        startActivityForResult(new Intent(Login.this, Register.class),3);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.txtLoginLupaSandi)
    protected void forgotPasw(){
        if(validateEmailorPhone(eEmail))
            sendPaswReset(eEmail.getText().toString());
    }

    @OnClick(R.id.btnLoginClearEmail)
    protected void clearEmail(){
        eEmail.setText("");
        btnClearEmail.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.linLoginFB)
    protected void loginFb(){
        btnLoginFb.performClick();
    }

    @OnClick(R.id.linLoginGoogle)
    protected void loginGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.lineLoginPhone)
    protected void loginPhone(){
        Intent i  = new Intent(Login.this, PhoneActivity.class);
        i.putExtra("status", "LOGIN");
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick(R.id.bLoginLogin)
    protected void loginOther(){
        if(validateEmailorPhone(eEmail) && validatePasw(ePassword.length())){
            if(eEmail.getText().toString().contains("@")){
                signInOther(eEmail.getText().toString(), ePassword.getText().toString());
            }else{//using phone

            }
        }
    }

    @Override
    public void onBackPressed() {
        navigateUp();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean validateEmailorPhone(EditText edittext) {
        boolean value;
        if (eEmail.getText().toString().isEmpty()
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(eEmail.getText().toString()).matches()){
            value=false;
            requestFocus(eEmail);
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
        } else if (edittext.length() > inputLayoutEmail.getCounterMaxLength()) {
            value = false;
            inputLayoutEmail.setError("Max character email length is " + inputLayoutEmail.getCounterMaxLength());
        }else{
            value=true;
            inputLayoutEmail.setError(null);
        }
        return value;
    }

    private boolean validatePasw(int length) {
        boolean value = true;
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
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
                                    uid = profile.getUid();
                                    name = profile.getDisplayName();
                                    image = profile.getPhotoUrl();
                                }
                            }
                            requestLogin(name, email, user.getUid(), null, image==null?"":image.toString(), PrefUtil.FB);
                        }
                    }
                });
    }

    private void signInOther(final String email, final String password) {//EMAIL MANUAL
        pDialog.setMessage("Login ...");
        showDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(!user.isEmailVerified()){
                                hideDialog();
                                Toast.makeText(getApplicationContext(), "this email has not been verified!\nPlease check your email!", Toast.LENGTH_SHORT).show();
                            }else{
                                requestLogin(user.getDisplayName(), user.getEmail(), user.getUid(),
                                        null, user.getPhotoUrl()==null?"":user.getPhotoUrl().getPath(),
                                        PrefUtil.OTHER);
                            }
                        } else {
                            hideDialog();
                            Toast.makeText(getApplicationContext(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                        if (!task.isSuccessful()) {
                            hideDialog();
                            Toast.makeText(getApplicationContext(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void requestLogin(String nama, String email, String id, String tgl, String foto,
                              final String account){
        String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        pDialog.setMessage("Login ...");
        showDialog();
        if(tgl==null || tgl.equals("")) tgl="1900-01-01";
        if(foto==null) foto = "";
        mApiService.loginRequest(nama==null?"":nama, email, id, tgl, foto, tanggalNow, device, account, "")
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

                                    if(account.equals(PrefUtil.FB)){
                                        prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.FB, birthday, phone, alamat, countToko);
                                    }else if(account.equals(PrefUtil.GL)){
                                        prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.GL, birthday, phone, alamat, countToko);
                                    }else{//OTHER
                                        prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.OTHER, birthday, phone, alamat, countToko);
                                    }
                                    Toast.makeText(Login.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                } else {
                                    if(account.equals(PrefUtil.FB)){
                                        LoginManager.getInstance().logOut();
                                        FirebaseAuth.getInstance().signOut();
                                    }else{//OTHER
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(Login.this, error_message, Toast.LENGTH_LONG).show();
                                }
                                hideDialog();
                            } catch (JSONException e) {
                                if(account.equals(PrefUtil.FB)){
                                    LoginManager.getInstance().logOut();
                                    FirebaseAuth.getInstance().signOut();
                                }else{//OTHER
                                    FirebaseAuth.getInstance().signOut();
                                }
                                e.printStackTrace();
                            } catch (IOException e) {
                                if(account.equals(PrefUtil.FB)){
                                    LoginManager.getInstance().logOut();
                                    FirebaseAuth.getInstance().signOut();
                                }else{//OTHER
                                    FirebaseAuth.getInstance().signOut();
                                }
                                e.printStackTrace();
                            }
                        } else {
                            //Log.i("debug", "onResponse: GA BERHASIL");
                            if(account.equals(PrefUtil.FB)){
                                LoginManager.getInstance().logOut();
                                FirebaseAuth.getInstance().signOut();
                            }else{//OTHER
                                FirebaseAuth.getInstance().signOut();
                            }
                            hideDialog();
                            Toast.makeText(Login.this, "GAGAL LOGIN", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                        if(account.equals(PrefUtil.FB)){
                            LoginManager.getInstance().logOut();
                            FirebaseAuth.getInstance().signOut();
                        }else{//OTHER
                            FirebaseAuth.getInstance().signOut();
                        }
                        hideDialog();
                        Toast.makeText(Login.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendPaswReset(String email){
        pDialog.setMessage("Loading ...");
        showDialog();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password reset has been sent in your email", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Login.this, "Failed to Password reset", Toast.LENGTH_LONG).show();
                        }
                        hideDialog();
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

    private void navigateUp() {
        final Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
            TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
        } else {
            NavUtils.navigateUpTo(this, upIntent);
        }
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
                .addOnCompleteListener(Login.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {
                        if (AuthResultTask.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            requestLogin(gsa.getDisplayName(), gsa.getEmail(), firebaseUser.getUid(), null,
                                    gsa.getPhotoUrl().getPath(), PrefUtil.GL);
                        }else {
                            FirebaseAuth.getInstance().signOut();
                            hideDialog();
                            Toast.makeText(Login.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}