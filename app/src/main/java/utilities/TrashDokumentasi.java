package utilities;

/**
 * Created by christian on 27/02/18.
 */

public class TrashDokumentasi {

                /*final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("service", ""+response.toString());
                                try {
                                    String nama = object.getString("first_name")+" "+object.getString("last_name");
                                    String id = object.getString("id");
                                    String foto = "https://graph.facebook.com/" + id + "/picture?type=large";
                                    String email = object.getString("email");
                                    String birthday = object.getString("birthday");
                                    Date date1 = new Date();
                                    try {
                                        date1=new SimpleDateFormat("MM/dd/yyyy").parse(birthday);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    requestLogin(nama, email, id, df2.format(date1), foto);
                                    Bundle facebookData = Utils.getFacebookData(object, prefUtil, accessToken);
                                } catch(JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();*/

    /*Intent i = new Intent(getActivity(), MainActivity.class);
								startActivityForResult(i, 5000);
								getActivity().overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
								getActivity().finish();



    private void getCache(){
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(optionalPendingResult.isDone()){
            GoogleSignInResult result = optionalPendingResult.get();
            handleSignInResult(result);
        }else{
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult result) {
                    if(result.getSignInAccount() != null)
                        handleSignInResult(result);
                }
            });
        }
    }

    if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                Bundle result=data.getExtras();
                //String a = bundle.getString("A");
            }
     }
    */

    /*private static final String ID = "ID";
    private static final String PROFILE = "PICTURE";
    private static final String NAME = "NAME";
    private static final String EMAIL = "EMAIL";
    private static final String GENDER = "GENDER";
    private static final String TOKEN = "TOKEN";
    private static final String LOGIN = "LOGIN";

    public static Bundle getGoogleData(Person person, PrefUtil prefUtil, GoogleSignInAccount account){
        Bundle bundle = new Bundle();
        try {
            if(person!=null){
                bundle.putString(ID, person.getId());
                bundle.putString(PROFILE, person.getImage().getUrl());
                bundle.putString(NAME, person.getDisplayName());
                bundle.putString(EMAIL, account.getEmail());
                bundle.putString(GENDER, person.getGender()==1?"Female":"Male");
                bundle.putString(TOKEN, account.getIdToken());
                prefUtil.saveGoogleUserInfo(person.getDisplayName(), account.getEmail(),
                        person.getGender()==1?"Female":"Male", person.getImage().getUrl(),
                        account.getIdToken(), person.getId());
            }else{
                bundle.putString(ID, account.getId());
                bundle.putString(PROFILE, account.getPhotoUrl().getPath());
                bundle.putString(NAME, account.getDisplayName());
                bundle.putString(EMAIL, account.getEmail());
                bundle.putString(GENDER, "");
                bundle.putString(TOKEN, account.getIdToken());
                prefUtil.saveGoogleUserInfo(account.getDisplayName(), account.getEmail(),
                        "", account.getPhotoUrl().getPath(),
                        account.getIdToken(), account.getId());
            }
            bundle.putString(LOGIN, "GOOGLE");
        }catch (Exception e) {
            //Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }
        return bundle;
    }

    public static Bundle getOtherData(PrefUtil prefUtil, String nama, String email, String id, String foto){
        Bundle bundle = new Bundle();
        try {
            bundle.putString(ID, id);
            bundle.putString(PROFILE, "");
            bundle.putString(NAME, nama);
            bundle.putString(EMAIL, email);
            bundle.putString(GENDER, "");
            bundle.putString(TOKEN, "");
            bundle.putString(LOGIN, "OTHER");
            prefUtil.saveOtherUserInfo(nama, email, id, "", "", PrefUtil.OTHER);
        }catch (Exception e) {
            //Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }
        return bundle;
    }

    public static Bundle getFacebookData(JSONObject object, PrefUtil prefUtil, AccessToken accessToken) {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                //Log.i("profile_pic", profile_pic + "");
                bundle.putString(PROFILE, profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString(ID, id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            bundle.putString(NAME, object.getString("first_name")+" "+object.getString("last_name"));
            if (object.has("email"))
                bundle.putString(EMAIL, object.getString("email"));
            if (object.has("gender"))
                bundle.putString(GENDER, object.getString("gender"));

            bundle.putString(TOKEN, accessToken.getToken());
            bundle.putString(LOGIN, "FB");
            prefUtil.saveFacebookUserInfo(object.getString("first_name"),
                    object.getString("last_name"),object.getString("email"),
                    object.getString("gender"), profile_pic.toString(),
                    accessToken.getToken(), accessToken.getUserId());
        } catch (Exception e) {
            //Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }
        return bundle;
    }

    public static Bundle getFacebookData(PrefUtil prefUtil, String nama, String email, String id) {
        Bundle bundle = new Bundle();
        try {
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                //Log.i("profile_pic", profile_pic + "");
                bundle.putString(PROFILE, profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString(ID, id);
            bundle.putString(NAME, nama);
            bundle.putString(EMAIL, email);
            bundle.putString(GENDER, "");

            bundle.putString(TOKEN, "");
            bundle.putString(LOGIN, "FB");
            prefUtil.saveFacebookUserInfo("",nama, email, "", profile_pic.toString(), "", id);
        } catch (Exception e) {
            //Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }
        return bundle;
    }*/



    /*public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }*/

    /*private void saveRegisterManual(){
        pDialog.setMessage("Registering ...");
        showDialog();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        requestNomorId(year, month, phoneId);
    }

    public void requestNomorId(final int tahun, final int bulan, final String phone){
        mApiService.cekNomor(tahun, bulan)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    String no = jsonRESULTS.getJSONObject("nomor").getString("iNomor");
                                    int nomor = Integer.parseInt(no)+1;
                                    String nomorId = String.valueOf(tahun).substring(2,4)+
                                            new DecimalFormat("00").format(bulan)+new DecimalFormat("0000").format(nomor);
                                    requestRegister("", "", nomorId, "", "", PrefUtil.PHONE, phone);
                                } else {
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(PhoneActivity.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            } catch (JSONException e) {
                                hideDialog();
                                e.printStackTrace();
                            } catch (IOException e) {
                                hideDialog();
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("debug", "onResponse: GA BERHASIL");
                            hideDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                    }
                });
    }*/

    /*if(val.contains("@")){

        }else{
            int max=13;
            if (eEmail.getText().toString().isEmpty()
                    || val.matches(mobilePattern)){
                value=false;
                requestFocus(eEmail);
                inputLayoutEmail.setError(getString(R.string.err_msg_phone));
            } else if (edittext.length() > max) {
                value = false;
                inputLayoutEmail.setError("Max phone number length is 13" );
            } else if(!val.matches(mobilePattern)) {
                value = false;
                inputLayoutEmail.setError("Please enter valid 10 digit phone number" );
            } else{
                value=true;
                inputLayoutEmail.setError(null);
            }
        }*/
    /*
    DOKUMENTASI LINK
    https://farizdotid.com/tutorial-login-dan-register-menggunakan-retrofit2-dan-api-server/
https://farizdotid.com/cara-membuat-api-login-dan-regitrasi-dengan-php/
https://www.codepolitan.com/cara-membuat-login-facebook-di-android-59b7ab667a57d
https://www.codeproject.com/Articles/1113631/Adding-Facebook-Login-to-Android-App

https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
http://androidbash.com/firebase-classic-email-login-facebook-login-android/
https://github.com/oemilk/firebase/tree/master/app/src/main/java/com/sh/firebase/authentication
https://github.com/Ginowine/android-firebase-authentication/blob/master/app/src/main/java/com/tutorial/authentication/MainActivity.java
http://javasampleapproach.com/android/firebase-authentication-sign-up-sign-in-sign-out-verify-email-android
https://android.jlelse.eu/android-firebase-authentication-with-google-signin-3f878d9b7553
http://javasampleapproach.com/android/firebase-authentication-send-reset-password-email-forgot-password-android*/
}
