package com.example.project.tokoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utilities.CircleTransform;
import utilities.Link;
import utilities.PrefUtil;
import service.BaseApiService;

/**
 * Created by christian on 27/02/18.
 */

public class Profil extends AppCompatActivity {

    private PrefUtil prefUtil;
    private BaseApiService mApiService;
    private SharedPreferences shared;
    private String id, nama, login, email, gender, profile, telp, alamat, birthday;
    private ProgressDialog pDialog;
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");

    @BindView(R.id.btn_ubah_profile)Button btnUbah;
    @BindView(R.id.img_profile_foto)ImageView imgFoto;
    @BindView(R.id.img_profile_back)ImageView imgBack;
    @BindView(R.id.txt_profile_iduser)TextView txtId;
    @BindView(R.id.txt_profile_namauser)TextView txtNama;
    @BindView(R.id.txt_profile_emailuser)TextView txtEmail;
    @BindView(R.id.txt_profile_telpuser)TextView txtTelp;
    @BindView(R.id.txt_profile_alamatuser)TextView txtAlamat;
    @BindView(R.id.txt_profile_birthdayuser)TextView txtBirthday;
    @BindView(R.id.txt_profile_genderuser)TextView txtGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);
        ButterKnife.bind(this);
        mApiService = Link.getAPIService();
        prefUtil = new PrefUtil(this);
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
        txtId.setText(id);
        txtNama.setText(nama==null || nama.equals("") || nama.equals("null")?"-":nama);
        txtEmail.setText(email==null || email.equals("") || email.equals("null")?"-":email);
        txtTelp.setText(telp==null || telp.equals("") || telp.equals("null")?"-":telp);
        txtAlamat.setText(alamat==null || alamat.equals("") || alamat.equals("null")?"-":alamat);
        try{
            if(birthday!=null && !birthday.equals("") && !birthday.equals("1900-01-01")){
                Date a =  (Date)df2.parse(birthday);
                txtBirthday.setText(sdf1.format(a));
            }else{
                txtBirthday.setText("-");
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
        txtGender.setText(gender.equals("P")?"Pria":gender.equals("W")?"Wanita":"-");
        if(profile!=null && !profile.equals("")){
            Glide.with(this).load(profile)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgFoto);
        }
    }

    @OnClick(R.id.btn_ubah_profile)
    protected void btnUbah(){
        startActivityForResult(new Intent(Profil.this, UbahProfile.class),2);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick(R.id.img_profile_back)
    protected void back(){
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                alamat = data.getStringExtra("alamat");
                telp = data.getStringExtra("telp");
                birthday = data.getStringExtra("birthday");
                gender = data.getStringExtra("gender");
                profile = data.getStringExtra("foto");
                id = data.getStringExtra("id");
                email = data.getStringExtra("email");
                txtId.setText(id);
                txtNama.setText(nama==null || nama.equals("") || nama.equals("null")?"-":nama);
                txtEmail.setText(email==null || email.equals("") || email.equals("null")?"-":email);
                txtTelp.setText(telp==null || telp.equals("") || telp.equals("null")?"-":telp);
                txtAlamat.setText(alamat==null || alamat.equals("") || alamat.equals("null")?"-":alamat);
                txtBirthday.setText(birthday==null || birthday.equals("") || birthday.equals("1900-01-01") || birthday.equals("null")?"-":birthday);
                txtGender.setText(gender==null || gender.equals("") || gender.equals("null")?"-":gender.equals("P")?"Pria":"Wanita");
                if(profile!=null && !profile.equals("")){
                    Glide.with(this).load(profile)
                            .crossFade()
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(this))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgFoto);
                }
            }
        }
    }
}
