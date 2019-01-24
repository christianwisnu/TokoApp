package com.example.project.tokoapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utilities.CircleTransform;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;

/**
 * Created by christian on 27/02/18.
 */

public class UbahProfile extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 0012;
    private static final int REQUEST_CODE_GALLERY = 0013;
    private PrefUtil prefUtil;
    private BaseApiService mApiService, mUploadService;
    private SharedPreferences shared;
    private String id, nama, login, email, gender, profile, telp, alamat, birthday;
    private String hasilImage, hasilKelamin, hasilTglLahir, hasilFoto;
    private ProgressDialog pDialog;
    private Calendar dateAndTime = Calendar.getInstance();
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private String [] items = {"Camera","Gallery"};
    private Uri selectedImage;

    @BindView(R.id.btn_updateprofile_save)Button btnSave;
    @BindView(R.id.img_updateprofile_back)ImageView imgBack;
    @BindView(R.id.img_updateprofile_foto)ImageView imgFoto;
    @BindView(R.id.img_updateprofile_kalendar)ImageView imgKalendar;
    @BindView(R.id.il_updateprofile_nama)TextInputLayout ilNama;
    @BindView(R.id.il_updateprofile_email)TextInputLayout ilEmail;
    @BindView(R.id.il_updateprofile_telp)TextInputLayout ilTelp;
    @BindView(R.id.il_updateprofile_alamat)TextInputLayout ilAlamat;
    @BindView(R.id.il_updateprofile_tgl)TextInputLayout ilTgl;
    @BindView(R.id.rd_updateprofile_grupkelamin)RadioGroup rgGrup;
    @BindView(R.id.rd_updateprofile_laki)RadioButton rbPria;
    @BindView(R.id.rd_updateprofile_wanita)RadioButton rbWanita;
    @BindView(R.id.e_updateprofile_nama)EditText edNama;
    @BindView(R.id.e_updateprofile_email)EditText edEmail;
    @BindView(R.id.e_updateprofile_telp)EditText edTelp;
    @BindView(R.id.e_updateprofile_alamat)EditText edAlamat;
    @BindView(R.id.e_updateprofile_tgl)EditText edTgl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_user);
        ButterKnife.bind(this);
        mApiService = Link.getAPIService();
        mUploadService = Link.getImageService();
        prefUtil = new PrefUtil(this);
        hasilFoto="N";
        try{
            shared  = prefUtil.getUserInfo();
            id      = shared.getString(PrefUtil.ID, null);
            nama    = shared.getString(PrefUtil.NAME, null);
            login   = shared.getString(PrefUtil.LOGIN, null);
            email   = shared.getString(PrefUtil.EMAIL, null);
            gender  = shared.getString(PrefUtil.GENDER, null);
            profile = shared.getString(PrefUtil.PROFILE, null);
            birthday= shared.getString(PrefUtil.BIRTHDAY, null);
            alamat  = shared.getString(PrefUtil.ALAMAT, null);
            telp    = shared.getString(PrefUtil.TELP, null);
        }catch (Exception e){e.getMessage();}
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        edNama.setText(nama==null || nama.equals("") || nama.equals("null")?"-":nama);
        edEmail.setText(email==null || email.equals("") || email.equals("null")?"-":email);
        edTelp.setText(telp==null || telp.equals("") || telp.equals("null")?"-":telp);
        edAlamat.setText(alamat==null || alamat.equals("") || alamat.equals("null")?"-":alamat);
        try{
            if(birthday!=null && !birthday.equals("")){
                Date a =  (Date)df2.parse(birthday);
                edTgl.setText(sdf1.format(a));
            }else{
                Calendar cal = Calendar.getInstance();
                cal.set(1900,0,1);
                edTgl.setText(sdf1.format(cal.getTime()));
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }

        if(gender.equals("P")){
            rbPria.setSelected(true);
        }else if(gender.equals("W")){
            rbWanita.setSelected(true);
        }else{
            rbWanita.setSelected(false);
            rbPria.setSelected(false);
        }

        if(profile!=null && !profile.equals("")){
            Glide.with(this).load(profile)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgFoto);
       }

       if(login.equals(PrefUtil.FB) || login.equals(PrefUtil.GL) || login.equals(PrefUtil.OTHER)){
           edNama.setEnabled(false);
           edEmail.setEnabled(false);
           edTelp.setEnabled(true);
       }else{
           edNama.setEnabled(false);
           edEmail.setEnabled(true);
           edTelp.setEnabled(false);
       }
    }

    @OnClick(R.id.btn_updateprofile_save)
    protected void btnUpdate(){
        int selectedId = rgGrup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        hasilKelamin=radioButton.getText().toString();

        new AlertDialog.Builder(this)
                .setMessage("Simpan Profile Baru?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        if(hasilFoto.equals("Y")){
                            uploadImage(selectedImage, hasilImage, id, edTelp.getText().toString(), hasilTglLahir,
                                    edAlamat.getText().toString(), hasilFoto.equals("Y")?Link.BASE_URL_IMAGE+hasilImage:profile,
                                    hasilKelamin.equals("Pria")?"P":"W");
                        }else{
                            updateProfile( id, edTelp.getText().toString(), hasilTglLahir,
                                    edAlamat.getText().toString(), hasilFoto.equals("Y")?Link.BASE_URL_IMAGE+hasilImage:profile,
                                    hasilKelamin.equals("Pria")?"P":"W", edNama.getText().toString(),
                                    edEmail.getText().toString());
                        }
                    }
                }).create().show();
    }

    @OnClick(R.id.img_updateprofile_back)
    protected void back(){
        finish();
    }

    @OnClick(R.id.img_updateprofile_foto)
    protected void foto(){
        openImage();
    }

    @OnClick(R.id.rd_updateprofile_laki)
    protected void laki(){

    }

    @OnClick(R.id.rd_updateprofile_wanita)
    protected void cewek(){

    }

    @OnClick(R.id.img_updateprofile_kalendar)
    protected void tglLahir(){
        new DatePickerDialog(UbahProfile.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener dFrom =new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // TODO Auto-generated method stub
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);
            updatelabelFrom();
        }
    };

    private void updatelabelFrom(){
        edTgl.setText(sdf1.format(dateAndTime.getTime()));
        hasilTglLahir=sdf2.format(dateAndTime.getTime());
    }

    private void openImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Camera")){
                    //dialog.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }else if(items[i].equals("Gallery")){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, REQUEST_CODE_GALLERY);
                }
            }
        });
        builder.show();
    }

    private void updateProfile(String id, String telp, String birthday, String alamat, String foto,
                              String gender, String nama, String email){
        pDialog.setMessage("Update Profile ...");
        showDialog();
        String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        if(birthday==null) birthday="1900-01-01";
        if(foto==null) foto = "";

        mApiService.editProfile(id, telp, birthday, alamat, gender, foto, tanggalNow, device, nama, email)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    hideDialog();
                                    prefUtil.clear();
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
                                    if(login.equals(PrefUtil.FB)){
                                        prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.FB, birthday, phone, alamat, countToko);
                                    }else if(login.equals(PrefUtil.GL)){
                                        prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.GL, birthday, phone, alamat, countToko);
                                    }else if(login.equals(PrefUtil.PHONE)){
                                        prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.PHONE, birthday, phone, alamat, countToko);
                                    }else{//OTHER
                                        prefUtil.saveUserInfo(nama, email, uId, gender, foto, PrefUtil.OTHER, birthday, phone, alamat, countToko);
                                    }
                                    Intent intent = new Intent();
                                    intent.putExtra("id", uId);
                                    intent.putExtra("alamat", alamat);
                                    intent.putExtra("telp", phone);
                                    intent.putExtra("birthday", birthday);
                                    intent.putExtra("gender", gender);
                                    intent.putExtra("foto", foto);
                                    intent.putExtra("email", email);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    Toast.makeText(UbahProfile.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                                } else {
                                    hideDialog();
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(UbahProfile.this, error_message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                hideDialog();
                                e.printStackTrace();
                            } catch (IOException e) {
                                hideDialog();
                                e.printStackTrace();
                            }
                        } else {
                            //Log.i("debug", "onResponse: GA BERHASIL");
                            hideDialog();
                            Toast.makeText(UbahProfile.this, "GAGAL UPDATE", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                        hideDialog();
                        Toast.makeText(UbahProfile.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
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

    private String getNameImage() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd-HHmmss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {
            try {
                hasilFoto="Y";
                hasilImage	= getNameImage().toString()+".jpg";
                selectedImage = data.getData();

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                       hasilImage);
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Glide.with(this).load(selectedImage)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgFoto);
                //uploadImage(selectedImage, hasilImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                hasilFoto="Y";
                hasilImage	= getNameImage().toString()+".jpg";
                selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = this.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                if (cursor == null) return;
                cursor.moveToFirst();
                cursor.close();
                Glide.with(this).load(selectedImage)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgFoto);
            } catch (Exception e) {
                hideDialog();
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri selectedImage, String desc, final String id, final String telp, String birthday,
                             final String alamat, final String foto, final String gender) {
        pDialog.setMessage("Uploading Image to Server...");
        showDialog();
        File file = new File(getRealPathFromURI(selectedImage));
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), file);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), desc);
        mUploadService.uploadImage(requestFile, descBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("value").equals("false")){
                            //Toast.makeText(UbahProfile.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                            updateProfile(id, telp, hasilTglLahir, alamat,
                                foto, gender, edNama.getText().toString(), edEmail.getText().toString());
                        }else{
                            hideDialog();
                            Toast.makeText(UbahProfile.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException e) {
                        hideDialog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        hideDialog();
                        e.printStackTrace();
                    }
                }else{
                    hideDialog();
                    Toast.makeText(UbahProfile.this, "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
                Toast.makeText(UbahProfile.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
