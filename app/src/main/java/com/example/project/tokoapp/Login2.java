package com.example.project.tokoapp;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by christian on 01/02/18.
 */

public class Login2 extends AppCompatActivity {

    /*@BindView(R.id.field_phone_number)
    EditText fieldPhoneNumber;
    @BindView(R.id.field_verification_code)
    EditText fieldVerificationCode;
    @BindView(R.id.button_start_verification)
    Button buttonStartVerification;
    @BindView(R.id.button_verify_phone)
    Button buttonVerifyPhone;
    @BindView(R.id.button_resend)
    Button buttonResend;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    private static final String TAG = "PhoneAuthActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tes);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w("", "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    fieldPhoneNumber.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Log.d("", "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            //startActivity(new Intent(PhoneAuthActivity.this, HomeActivity.class).putExtra("phone", user.getPhoneNumber()));
                            //finish();
                            Toast.makeText(Login2.this, "Sukses",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                fieldVerificationCode.setError("Invalid code.");
                                Toast.makeText(Login2.this, "Invalid Code.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void getOtp(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
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

    @OnClick(R.id.button_start_verification)
    public void clickStartVerification(){
        getOtp(fieldPhoneNumber.getText().toString());
    }

    @OnClick(R.id.button_verify_phone)
    public void clickVerifyPhone(){
        String code = fieldVerificationCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            fieldVerificationCode.setError("Cannot be empty.");
            return;
        }
        verifyPhoneNumberWithCode(mVerificationId, code);
    }

    @OnClick(R.id.button_resend)
    public void clickResend(){
        resendVerificationCode(fieldPhoneNumber.getText().toString(), mResendToken);
    }*/
}

