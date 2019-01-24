package com.example.project.tokoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import list.ListKabKotaView;
import list.ListKecamatanView;
import list.ListProvinsiView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;
import utilities.Anim;
import utilities.AppController;
import utilities.CircleTransform;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by christian on 22/03/18.
 */

public class StoreActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 0012;
    private static final int REQUEST_CODE_GALLERY = 0013;
    private BaseApiService mApiService, mUploadService;
    private SharedPreferences shared;
    private ProgressDialog pDialog;
    private PrefUtil prefUtil;
    private String[] items = {"Camera", "Gallery"};
    private Uri selectedImage;
    private String hasilImage, hasilFoto="N", alamatMap, id;
    private int hourZero = 0;
    private int minuteZero = 0;
    private int hourLast = 23;
    private int minuteLast = 59;
    private int RESULT_PROV = 2;
    private int RESULT_KAB = 3;
    private int RESULT_KEC = 4;
    private int RESULT_MAP = 5;
    private String kodeProv, namaProv, kodeKabKota, namaKabKota, kodeKec, namaKec, tipe;
    private enum TransMode{NEW, NEW_MODIFIED, OLD, OLD_MODIFIED};
    private TransMode transMode;
    private LocationManager locationManager ;
    private boolean GpsStatus ;
    private double latitude, longtitude;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BindView(R.id.imgExpandKontak)
    ImageView imgColExpKontak;
    @BindView(R.id.linTokoLayoutKontak)
    LinearLayout linKontak;
    @BindView(R.id.imgExpandLokasi)
    ImageView imgColExpLokasi;
    @BindView(R.id.linTokoLayoutLokasi)
    LinearLayout linLokasi;
    @BindView(R.id.imgExpandJadwal)
    ImageView imgColExpJadwal;
    @BindView(R.id.linTokoLayoutJadwal)
    LinearLayout linJadwal;
    @BindView(R.id.imgExpandSosial)
    ImageView imgColExpSosial;
    @BindView(R.id.linTokoLayoutSosial)
    LinearLayout linSosial;
    @BindView(R.id.btn_tokolayout_save)
    Button btnSave;
    @BindView(R.id.img_tokolayout_back)
    ImageView imgBack;
    @BindView(R.id.img_tokolayout_foto)
    ImageView imgFoto;
    @BindView(R.id.inputlayout_toko_id)
    TextInputLayout tiIDToko;
    @BindView(R.id.eTokoLayoutID)
    EditText edIDToko;
    @BindView(R.id.inputlayout_toko_nama)
    TextInputLayout tiNamaToko;
    @BindView(R.id.eTokoLayoutNama)
    EditText edNamaToko;
    @BindView(R.id.inputlayout_toko_slogan)
    TextInputLayout tiSloganToko;
    @BindView(R.id.eTokoLayoutSlogan)
    EditText edSloganToko;
    @BindView(R.id.inputlayout_toko_deskripsi)
    TextInputLayout tiDeskripsiToko;
    @BindView(R.id.eTokoLayoutDeskripsi)
    EditText edDeskripsiToko;
    @BindView(R.id.inputlayout_toko_note)
    TextInputLayout tiNote;
    @BindView(R.id.eTokoLayoutNote)
    EditText edNote;
    @BindView(R.id.inputlayout_toko_provinsi)
    TextInputLayout tiProv;
    @BindView(R.id.eTokoLayoutProvinsi)
    EditText edProv;
    @BindView(R.id.txtTokoLayoutPilihProv)
    TextView txtPilihProv;
    @BindView(R.id.inputlayout_toko_kab)
    TextInputLayout tiKabKota;
    @BindView(R.id.eTokoLayoutKab)
    EditText edKabKota;
    @BindView(R.id.txtTokoLayoutPilihKab)
    TextView txtPilihKabKota;
    @BindView(R.id.inputlayout_toko_kec)
    TextInputLayout tiKec;
    @BindView(R.id.eTokoLayoutKec)
    EditText edKec;
    @BindView(R.id.txtTokoLayoutPilihKec)
    TextView txtPilihKec;
    @BindView(R.id.inputlayout_toko_alamat)
    TextInputLayout tiAlamatToko;
    @BindView(R.id.eTokoLayoutAlamat)
    EditText edAlamat;
    @BindView(R.id.imgTokoLayoutPilihLokasiMap)
    ImageView imgPilihMaps;
    @BindView(R.id.inputlayout_toko_alamat_text)
    TextInputLayout tiAlamatTokoText;
    @BindView(R.id.eTokoLayoutAlamatText)
    EditText edAlamatText;
    @BindView(R.id.inputlayout_toko_kodepos)
    TextInputLayout tiKodePos;
    @BindView(R.id.eTokoLayoutKodepos)
    EditText edKodePos;
    @BindView(R.id.inputlayout_toko_emailtoko)
    TextInputLayout tiEmail;
    @BindView(R.id.eTokoLayoutEmailtoko)
    EditText edEmail;
    @BindView(R.id.inputlayout_toko_telp)
    TextInputLayout tiTelp;
    @BindView(R.id.eTokoLayoutTelp)
    EditText edTelp;
    @BindView(R.id.inputlayout_toko_telp2)
    TextInputLayout tiTelp2;
    @BindView(R.id.eTokoLayoutTelp2)
    EditText edTelp2;
    @BindView(R.id.inputlayout_toko_wa)
    TextInputLayout tiWa;
    @BindView(R.id.eTokoLayoutWa)
    EditText edWa;
    @BindView(R.id.inputlayout_toko_fax)
    TextInputLayout tiFax;
    @BindView(R.id.eTokoLayoutFax)
    EditText edFax;
    @BindView(R.id.inputlayout_toko_jam_buka)
    TextInputLayout tiJamBuka;
    @BindView(R.id.eTokoLayoutJamBuka)
    EditText edJamBuka;
    @BindView(R.id.imgTokoLayoutPilihJamBuka)
    ImageView imgJamBuka;
    @BindView(R.id.inputlayout_toko_jam_tutup)
    TextInputLayout tiJamTutup;
    @BindView(R.id.eTokoLayoutJamTutup)
    EditText edJamTutup;
    @BindView(R.id.imgTokoLayoutPilihJamTutup)
    ImageView imgJamTutup;
    @BindView(R.id.btnProsesTerapkan)
    Button btnTerapkanJam;

    @BindView(R.id.inputlayout_toko_facebook)
    TextInputLayout tiFb;
    @BindView(R.id.eTokoLayoutFacebook)
    EditText edFb;
    @BindView(R.id.inputlayout_toko_twitter)
    TextInputLayout tiTwitter;
    @BindView(R.id.eTokoLayoutTwitter)
    EditText edTwitter;
    @BindView(R.id.inputlayout_toko_instagram)
    TextInputLayout tiIG;
    @BindView(R.id.eTokoLayoutInstagram)
    EditText edIG;

    @BindView(R.id.inputlayout_toko_jam_buka_mon)
    TextInputLayout tiJamBukaMon;
    @BindView(R.id.eTokoLayoutJamBukaMon)
    EditText edJamBukaMon;
    @BindView(R.id.imgTokoLayoutPilihJamBukaMon)
    ImageView imgJamBukaMon;
    @BindView(R.id.inputlayout_toko_jam_tutup_mon)
    TextInputLayout tiJamTutupMon;
    @BindView(R.id.eTokoLayoutJamTutupMon)
    EditText edJamTutupMon;
    @BindView(R.id.imgTokoLayoutPilihJamTutupMon)
    ImageView imgJamTutupMon;
    @BindView(R.id.ckLiburMon)
    CheckBox ckLiburMon;

    @BindView(R.id.inputlayout_toko_jam_buka_tue)
    TextInputLayout tiJamBukaTue;
    @BindView(R.id.eTokoLayoutJamBukaTue)
    EditText edJamBukaTue;
    @BindView(R.id.imgTokoLayoutPilihJamBukaTue)
    ImageView imgJamBukaTue;
    @BindView(R.id.inputlayout_toko_jam_tutup_tue)
    TextInputLayout tiJamTutupTue;
    @BindView(R.id.eTokoLayoutJamTutupTue)
    EditText edJamTutupTue;
    @BindView(R.id.imgTokoLayoutPilihJamTutupTue)
    ImageView imgJamTutupTue;
    @BindView(R.id.ckLiburTue)
    CheckBox ckLiburTue;

    @BindView(R.id.inputlayout_toko_jam_buka_wed)
    TextInputLayout tiJamBukaWed;
    @BindView(R.id.eTokoLayoutJamBukaWed)
    EditText edJamBukaWed;
    @BindView(R.id.imgTokoLayoutPilihJamBukaWed)
    ImageView imgJamBukaWed;
    @BindView(R.id.inputlayout_toko_jam_tutup_Wed)
    TextInputLayout tiJamTutupWed;
    @BindView(R.id.eTokoLayoutJamTutupWed)
    EditText edJamTutupWed;
    @BindView(R.id.imgTokoLayoutPilihJamTutupWed)
    ImageView imgJamTutupWed;
    @BindView(R.id.ckLiburWed)
    CheckBox ckLiburWed;

    @BindView(R.id.inputlayout_toko_jam_buka_thu)
    TextInputLayout tiJamBukaThu;
    @BindView(R.id.eTokoLayoutJamBukaThu)
    EditText edJamBukaThu;
    @BindView(R.id.imgTokoLayoutPilihJamBukaThu)
    ImageView imgJamBukaThu;
    @BindView(R.id.inputlayout_toko_jam_tutup_Thu)
    TextInputLayout tiJamTutupThu;
    @BindView(R.id.eTokoLayoutJamTutupThu)
    EditText edJamTutupThu;
    @BindView(R.id.imgTokoLayoutPilihJamTutupThu)
    ImageView imgJamTutupThu;
    @BindView(R.id.ckLiburThu)
    CheckBox ckLiburThu;

    @BindView(R.id.inputlayout_toko_jam_buka_fri)
    TextInputLayout tiJamBukaFri;
    @BindView(R.id.eTokoLayoutJamBukaFri)
    EditText edJamBukaFri;
    @BindView(R.id.imgTokoLayoutPilihJamBukaFri)
    ImageView imgJamBukaFri;
    @BindView(R.id.inputlayout_toko_jam_tutup_fri)
    TextInputLayout tiJamTutupFri;
    @BindView(R.id.eTokoLayoutJamTutupFri)
    EditText edJamTutupFri;
    @BindView(R.id.imgTokoLayoutPilihJamTutupFri)
    ImageView imgJamTutupFri;
    @BindView(R.id.ckLiburFri)
    CheckBox ckLiburFri;

    @BindView(R.id.inputlayout_toko_jam_buka_sat)
    TextInputLayout tiJamBukaSat;
    @BindView(R.id.eTokoLayoutJamBukaSat)
    EditText edJamBukaSat;
    @BindView(R.id.imgTokoLayoutPilihJamBukaSat)
    ImageView imgJamBukaSat;
    @BindView(R.id.inputlayout_toko_jam_tutup_sat)
    TextInputLayout tiJamTutupSat;
    @BindView(R.id.eTokoLayoutJamTutupSat)
    EditText edJamTutupSat;
    @BindView(R.id.imgTokoLayoutPilihJamTutupSat)
    ImageView imgJamTutupSat;
    @BindView(R.id.ckLiburSat)
    CheckBox ckLiburSat;

    @BindView(R.id.inputlayout_toko_jam_buka_sun)
    TextInputLayout tiJamBukaSun;
    @BindView(R.id.eTokoLayoutJamBukaSun)
    EditText edJamBukaSun;
    @BindView(R.id.imgTokoLayoutPilihJamBukaSun)
    ImageView imgJamBukaSun;
    @BindView(R.id.inputlayout_toko_jam_tutup_sun)
    TextInputLayout tiJamTutupSun;
    @BindView(R.id.eTokoLayoutJamTutupSun)
    EditText edJamTutupSun;
    @BindView(R.id.imgTokoLayoutPilihJamTutupSun)
    ImageView imgJamTutupSun;
    @BindView(R.id.ckLiburSun)
    CheckBox ckLiburSun;
    @BindView(R.id.spTokoLayoutJenisLapak)
    Spinner spMoving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toko_layout);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                tipe = i.getString("tipe");//ADD or UPDATE or DELETE
            } catch (Exception e) {}
        }
        if(tipe.equals("ADD")){
            this.transMode = TransMode.NEW;
        }else{//EDIT
            this.transMode = TransMode.OLD;
        }
        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            id      = shared.getString(PrefUtil.ID, null);
        }catch (Exception e){e.getMessage();}
        ButterKnife.bind(this);
        mApiService = Link.getAPIService();

        hasilFoto = "N";
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        linKontak.setVisibility(View.GONE);
        linJadwal.setVisibility(View.GONE);
        linLokasi.setVisibility(View.GONE);
        linSosial.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_tokolayout_back)
    protected void back() {
        if(this.transMode==TransMode.NEW_MODIFIED || this.transMode==TransMode.OLD_MODIFIED){
            new AlertDialog.Builder(this)
                    .setMessage("Data belum disimpan.\nYakin akan keluar?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).create().show();
        }else{
            finish();
        }
    }

    @OnClick(R.id.img_tokolayout_foto)
    protected void pilihFoto() {
        openImage();
    }

    @OnTextChanged(value = R.id.eTokoLayoutID, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeIDToko(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
        validateIDToko();
    }

    private boolean validateIDToko() {
        boolean value;
        if (edIDToko.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edIDToko);
            tiIDToko.setError(getString(R.string.err_msg_id_toko));
        } else {
            value = true;
            tiIDToko.setError(null);
        }
        return value;
    }

    @OnTextChanged(value = R.id.eTokoLayoutNama, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeNamaToko(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
        validateNamaToko();
    }

    private boolean validateNamaToko() {
        boolean value;
        if (edNamaToko.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edNamaToko);
            tiNamaToko.setError(getString(R.string.err_msg_nama_toko));
        } else {
            value = true;
            tiNamaToko.setError(null);
        }
        return value;
    }

    @OnTextChanged(value = R.id.eTokoLayoutSlogan, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeSlogan(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutDeskripsi, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeDeskripsi(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutNote, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeNote(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    public void expcol_lokasi(View v) {
        if (linLokasi.isShown()) {
            Anim.slide_up(this, linLokasi);
            linLokasi.setVisibility(View.GONE);
            imgColExpLokasi.setBackgroundResource(R.drawable.snv_expand);
        } else {
            Anim.slide_down(this, linLokasi);
            linLokasi.setVisibility(View.VISIBLE);
            imgColExpLokasi.setBackgroundResource(R.drawable.snv_collapse);
        }
    }


    @OnTextChanged(value = R.id.eTokoLayoutProvinsi, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeProv(Editable editable) {
        validateProvinsi();
    }

    private boolean validateProvinsi() {
        boolean value;
        if (edProv.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edProv);
            tiProv.setError(getString(R.string.err_msg_prov));
        } else {
            value = true;
            edProv.setError(null);
        }
        return value;
    }

    @OnClick(R.id.txtTokoLayoutPilihProv)
    protected void pilihProv() {
        Intent i = new Intent(StoreActivity.this, ListProvinsiView.class);
        startActivityForResult(i,RESULT_PROV);
    }

    /*@OnTextChanged(value = R.id.eTokoLayoutKab, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeKab(Editable editable) {
        validateKab();
    }

    private boolean validateKab() {
        boolean value;
        if (edKabKota.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edKabKota);
            tiKabKota.setError(getString(R.string.err_msg_kab));
        } else {
            value = true;
            edKabKota.setError(null);
        }
        return value;
    }*/

    @OnClick(R.id.txtTokoLayoutPilihKab)
    protected void pilihKabKota() {
        if(kodeProv==null || kodeProv.trim().equals("")){
            Toast.makeText(StoreActivity.this, "Provinsi harap diisi!", Toast.LENGTH_LONG).show();
        }else{
            Intent i = new Intent(StoreActivity.this, ListKabKotaView.class);
            i.putExtra("kodeProv",kodeProv);
            startActivityForResult(i,RESULT_KAB);
        }
    }

    /*@OnTextChanged(value = R.id.eTokoLayoutKec, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeKec(Editable editable) {
        validateKec();
    }

    private boolean validateKec() {
        boolean value;
        if (edKec.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edKec);
            tiKec.setError(getString(R.string.err_msg_kec));
        } else {
            value = true;
            edKec.setError(null);
        }
        return value;
    }*/

    @OnClick(R.id.txtTokoLayoutPilihKec)
    protected void pilihKec() {
        if(kodeKabKota==null || kodeKabKota.trim().equals("")){
            Toast.makeText(StoreActivity.this, "Kabupaten/Kota harap diisi!", Toast.LENGTH_LONG).show();
        }else{
            Intent i = new Intent(StoreActivity.this, ListKecamatanView.class);
            i.putExtra("kodeKabKota",kodeKabKota);
            startActivityForResult(i,RESULT_KEC);
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutAlamat, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeAlamat(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
        validateAlamatMap();
    }

    private boolean validateAlamatMap() {
        boolean value;
        if (edAlamat.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edAlamat);
            tiAlamatToko.setError(getString(R.string.err_msg_alamat));
        } else {
            value = true;
            edAlamat.setError(null);
        }
        return value;
    }

    private boolean validateAlamatManual() {
        boolean value;
        if (edAlamatText.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edAlamatText);
            tiAlamatTokoText.setError(getString(R.string.err_msg_alamat));
        } else {
            value = true;
            edAlamatText.setError(null);
        }
        return value;
    }

    private boolean validateJamBukaTutup() {
        boolean value;
        if (edJamBukaMon.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edJamBukaMon);
            tiJamBukaMon.setError(getString(R.string.err_msg_jam));
        } else if (edJamBukaTue.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edJamBukaTue);
            tiJamBukaTue.setError(getString(R.string.err_msg_jam));
        } else if (edJamBukaWed.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edJamBukaWed);
            tiJamBukaWed.setError(getString(R.string.err_msg_jam));
        } else if (edJamBukaThu.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edJamBukaThu);
            tiJamBukaThu.setError(getString(R.string.err_msg_jam));
        } else if (edJamBukaFri.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edJamBukaFri);
            tiJamBukaFri.setError(getString(R.string.err_msg_jam));
        } else if (edJamBukaSat.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edJamBukaSat);
            tiJamBukaSat.setError(getString(R.string.err_msg_jam));
        } else if (edJamBukaSun.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edJamBukaSun);
            tiJamBukaSun.setError(getString(R.string.err_msg_jam));
        } else {
            value = true;
        }
        return value;
    }

    private boolean validateFoto() {
        boolean value;
        if (hasilFoto.equals("N")) {
            value = false;
            Toast.makeText(StoreActivity.this, "Foto toko belum ada!", Toast.LENGTH_LONG).show();
        } else {
            value = true;
        }
        return value;
    }

    @OnClick(R.id.imgTokoLayoutPilihLokasiMap)
    protected void pilihMap() {
        CheckGpsStatus() ;
        if(GpsStatus == true){
            Intent i = new Intent(getApplication(), MapsActivity.class);
            startActivityForResult(i, RESULT_MAP);
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Perhatian")
                    .setMessage("GPS belum diaktifkan\nAktifkan dahulu!")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create().show();
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutKodepos, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeKodePos(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    public void expcol_kontak(View v) {
        if (linKontak.isShown()) {
            Anim.slide_up(this, linKontak);
            linKontak.setVisibility(View.GONE);
            imgColExpKontak.setBackgroundResource(R.drawable.snv_expand);
        } else {
            Anim.slide_down(this, linKontak);
            linKontak.setVisibility(View.VISIBLE);
            imgColExpKontak.setBackgroundResource(R.drawable.snv_collapse);
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutEmailtoko, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeEmail(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutWa, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeWa(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
        validatePhone();
    }

    @OnTextChanged(value = R.id.eTokoLayoutTelp, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeTelp1(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
        validatePhone();
    }

    private boolean validatePhone() {
        boolean value;
        if (edWa.getText().toString().isEmpty() && edTelp.getText().toString().isEmpty()) {
            value = false;
            requestFocus(edWa);
            tiWa.setError(getString(R.string.err_msg_wa_phone1));
        } else {
            value = true;
            edWa.setError(null);
        }
        return value;
    }

    @OnTextChanged(value = R.id.eTokoLayoutTelp2, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeTelp2(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutFax, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeFax(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    public void expcol_sosial(View v) {
        if (linSosial.isShown()) {
            Anim.slide_up(this, linSosial);
            linSosial.setVisibility(View.GONE);
            imgColExpSosial.setBackgroundResource(R.drawable.snv_expand);
        } else {
            Anim.slide_down(this, linSosial);
            linSosial.setVisibility(View.VISIBLE);
            imgColExpSosial.setBackgroundResource(R.drawable.snv_collapse);
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutFacebook, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeFb(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutTwitter, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeTwitter(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnTextChanged(value = R.id.eTokoLayoutInstagram, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeIg(Editable editable) {
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    public void expcol_jadwal(View v) {
        if (linJadwal.isShown()) {
            Anim.slide_up(this, linJadwal);
            linJadwal.setVisibility(View.GONE);
            imgColExpJadwal.setBackgroundResource(R.drawable.snv_expand);
        } else {
            Anim.slide_down(this, linJadwal);
            linJadwal.setVisibility(View.VISIBLE);
            imgColExpJadwal.setBackgroundResource(R.drawable.snv_collapse);
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBuka)
    protected void jamBuka() {
        pilihJam(edJamBuka);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutup)
    protected void jamTutup() {
        pilihJam(edJamTutup);
    }

    @OnCheckedChanged(R.id.ckTokoLayout24jam)
    protected void cek24jam(boolean check) {
        if (check) {
            String sHrs, sMins;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBuka);
            edJamTutup.setText(hourLast + ":" + minuteLast);
            imgJamBuka.setVisibility(View.GONE);
            imgJamTutup.setVisibility(View.GONE);
        } else {
            edJamBuka.setText(null);
            edJamTutup.setText(null);
            imgJamBuka.setVisibility(View.VISIBLE);
            imgJamTutup.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.btnProsesTerapkan)
    protected void prosesTerapkan() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(StoreActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(StoreActivity.this);
        }
        if (edJamBuka.getText().toString().isEmpty() || edJamTutup.getText().toString().isEmpty()) {
            builder.setTitle("Perhatian")
                    .setMessage("Jam buka/tutup toko tidak boleh kosong.")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            edJamBukaSun.setText(edJamBuka.getText().toString());
            edJamBukaMon.setText(edJamBuka.getText().toString());
            edJamBukaTue.setText(edJamBuka.getText().toString());
            edJamBukaWed.setText(edJamBuka.getText().toString());
            edJamBukaThu.setText(edJamBuka.getText().toString());
            edJamBukaFri.setText(edJamBuka.getText().toString());
            edJamBukaSat.setText(edJamBuka.getText().toString());
            edJamTutupSun.setText(edJamTutup.getText().toString());
            edJamTutupMon.setText(edJamTutup.getText().toString());
            edJamTutupTue.setText(edJamTutup.getText().toString());
            edJamTutupWed.setText(edJamTutup.getText().toString());
            edJamTutupThu.setText(edJamTutup.getText().toString());
            edJamTutupFri.setText(edJamTutup.getText().toString());
            edJamTutupSat.setText(edJamTutup.getText().toString());
            if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
                this.transMode = TransMode.NEW_MODIFIED;
            }else{
                this.transMode = TransMode.OLD_MODIFIED;
            }
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBukaMon)
    protected void pilJamBukaMon() {
        pilihJam(edJamBukaMon);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutupMon)
    protected void pilJamTutupMon() {
        pilihJam(edJamTutupMon);
    }

    @OnCheckedChanged(R.id.ckLiburMon)
    protected void offMon(boolean check) {
        if (check) {
            String sMins, sHrs;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBukaMon);
            edJamTutupMon.setText(hourLast + ":" + minuteLast);
            imgJamBukaMon.setVisibility(View.GONE);
            imgJamTutupMon.setVisibility(View.GONE);
        } else {
            edJamBukaMon.setText(null);
            edJamTutupMon.setText(null);
            imgJamBukaMon.setVisibility(View.VISIBLE);
            imgJamTutupMon.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBukaTue)
    protected void pilJamBukaTue() {
        pilihJam(edJamBukaTue);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutupTue)
    protected void pilJamTutupTue() {
        pilihJam(edJamTutupTue);
    }

    @OnCheckedChanged(R.id.ckLiburTue)
    protected void offTue(boolean check) {
        if (check) {
            String sMins, sHrs;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBukaTue);
            edJamTutupTue.setText(hourLast + ":" + minuteLast);
            imgJamBukaTue.setVisibility(View.GONE);
            imgJamTutupTue.setVisibility(View.GONE);
        } else {
            edJamBukaTue.setText(null);
            edJamTutupTue.setText(null);
            imgJamBukaTue.setVisibility(View.VISIBLE);
            imgJamTutupTue.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBukaWed)
    protected void pilJamBukaWed() {
        pilihJam(edJamBukaWed);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutupWed)
    protected void pilJamTutupWed() {
        pilihJam(edJamTutupWed);
    }

    @OnCheckedChanged(R.id.ckLiburWed)
    protected void offWed(boolean check) {
        if (check) {
            String sMins, sHrs;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBukaWed);
            edJamTutupWed.setText(hourLast + ":" + minuteLast);
            imgJamBukaWed.setVisibility(View.GONE);
            imgJamTutupWed.setVisibility(View.GONE);
        } else {
            edJamBukaWed.setText(null);
            edJamTutupWed.setText(null);
            imgJamBukaWed.setVisibility(View.VISIBLE);
            imgJamTutupWed.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBukaThu)
    protected void pilJamBukaThu() {
        pilihJam(edJamBukaThu);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutupThu)
    protected void pilJamTutupThu() {
        pilihJam(edJamTutupThu);
    }

    @OnCheckedChanged(R.id.ckLiburThu)
    protected void offThu(boolean check) {
        if (check) {
            String sMins, sHrs;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBukaThu);
            edJamTutupThu.setText(hourLast + ":" + minuteLast);
            imgJamBukaThu.setVisibility(View.GONE);
            imgJamTutupThu.setVisibility(View.GONE);
        } else {
            edJamBukaThu.setText(null);
            edJamTutupThu.setText(null);
            imgJamBukaThu.setVisibility(View.VISIBLE);
            imgJamTutupThu.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBukaFri)
    protected void pilJamBukaFri() {
        pilihJam(edJamBukaFri);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutupFri)
    protected void pilJamTutupFri() {
        pilihJam(edJamTutupFri);
    }

    @OnCheckedChanged(R.id.ckLiburFri)
    protected void offFri(boolean check) {
        if (check) {
            String sMins, sHrs;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBukaFri);
            edJamTutupFri.setText(hourLast + ":" + minuteLast);
            imgJamBukaFri.setVisibility(View.GONE);
            imgJamTutupFri.setVisibility(View.GONE);
        } else {
            edJamBukaFri.setText(null);
            edJamTutupFri.setText(null);
            imgJamBukaFri.setVisibility(View.VISIBLE);
            imgJamTutupFri.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBukaSat)
    protected void pilJamBukaSat() {
        pilihJam(edJamBukaSat);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutupSat)
    protected void pilJamTutupSat() {
        pilihJam(edJamTutupMon);
    }

    @OnCheckedChanged(R.id.ckLiburSat)
    protected void offSat(boolean check) {
        if (check) {
            String sMins, sHrs;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBukaSat);
            edJamTutupSat.setText(hourLast + ":" + minuteLast);
            imgJamBukaSat.setVisibility(View.GONE);
            imgJamTutupSat.setVisibility(View.GONE);
        } else {
            edJamBukaSat.setText(null);
            edJamTutupSat.setText(null);
            imgJamBukaSat.setVisibility(View.VISIBLE);
            imgJamTutupSat.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.imgTokoLayoutPilihJamBukaSun)
    protected void pilJamBukaSun() {
        pilihJam(edJamBukaSun);
    }

    @OnClick(R.id.imgTokoLayoutPilihJamTutupSun)
    protected void pilJamTutupSun() {
        pilihJam(edJamTutupSun);
    }

    @OnCheckedChanged(R.id.ckLiburSun)
    protected void offSun(boolean check) {
        if (check) {
            String sMins, sHrs;
            if (minuteZero < 10) {
                sMins = "0" + minuteZero;
            } else {
                sMins = String.valueOf(minuteZero);
            }
            if (hourZero < 10) {
                sHrs = "0" + hourZero;
            } else {
                sHrs = String.valueOf(hourZero);
            }
            setSelectedTime(sHrs, sMins, edJamBukaSun);
            edJamTutupSun.setText(hourLast + ":" + minuteLast);
            imgJamBukaSun.setVisibility(View.GONE);
            imgJamTutupSun.setVisibility(View.GONE);
        } else {
            edJamBukaSun.setText(null);
            edJamTutupSun.setText(null);
            imgJamBukaSun.setVisibility(View.VISIBLE);
            imgJamTutupSun.setVisibility(View.VISIBLE);
        }
        if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
            this.transMode = TransMode.NEW_MODIFIED;
        }else{
            this.transMode = TransMode.OLD_MODIFIED;
        }
    }

    @OnClick(R.id.btn_tokolayout_save)
    protected void saveData() {
        mUploadService = Link.getImageMerchantService(edIDToko.getText().toString());
        uploadImage();
        /*if(validateAlamatManual() && validatePhone() && validateAlamatMap() && validateProvinsi() &&
                validateNamaToko() && validateJamBukaTutup()){
            //PROSES SIMPAN DAHULU KARENA BERHUBUNGAN DGN CREATE FOLDER BERDASARKAN ID
            //proses_simpan();

        }*/
    }

    private void proses_simpan(){
        pDialog.setMessage("Proses Simpan Data ...");
        showDialog();
        String spJenisLapak = spMoving.getSelectedItem().toString();
        Integer lapak=0;
        if(spJenisLapak.equals("Standby")){
            lapak=1;
        }else{
            lapak=0;
        }
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        /*StringRequest register = new StringRequest(Request.Method.POST, Link.BASE_URL_API + "registerMerchant.php", new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d("Respone", response.toString());
                try {
                    JSONObject jsonrespon = new JSONObject(response);
                    int Sucsess = jsonrespon.getInt("value");
                    if (Sucsess > 0) {
                        if(tipe.equals("ADD")){
                            Toast.makeText(StoreActivity.this, jsonrespon.getString("message"), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(StoreActivity.this, jsonrespon.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        uploadImage();
                    } else {
                        hideDialog();
                        Toast.makeText(StoreActivity.this, jsonrespon.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    hideDialog();
                    Toast.makeText(StoreActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(StoreActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("IDToko", edIDToko.getText().toString());
                params.put("i_idcust", idCust);
                params.put("i_idsewastatus", statusSewa.equals("Man Only")?"1":
                        statusSewa.equals("Women Only")?"2":"3");
                params.put("vc_namakost", eNamaKos.getText().toString());
                params.put("vc_alamat", eAlamat.getText().toString());
                params.put("vc_gambar", simage1);
                params.put("vc_gambar2", simage2);
                params.put("vc_gambar3", simage3);
                params.put("vc_gambar4", simage4);
                params.put("vc_gambar5", simage5);
                params.put("i_lebar", eLebar.getText().toString());
                params.put("i_panjang", ePanjang.getText().toString());
                params.put("c_statuslistrik", statusListrik.equals("Include")?"Y":"N");
                params.put("i_jmlkamar", eTotalKamar.getText().toString());
                params.put("t_fasilitas", daftarKodeFasilitas);
                params.put("d_latitude", String.valueOf(latitude));
                params.put("d_longtitude", String.valueOf(longtitude));
                params.put("n_harga", eharga.getText().toString());
                return params;
            }
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(register);*/
        /*mApiService.registerMerchant(edIDToko.getText().toString(), edNamaToko.getText().toString(), edSloganToko.getText().toString(), edDeskripsiToko.getText().toString(),
                edNote.getText().toString(), kodeProv, kodeKabKota, kodeKec, edAlamatText.getText().toString(),
                latitude, longtitude,
                edKodePos.getText().toString(), lapak, edEmail.getText().toString(), edWa.getText().toString(),
                edTelp.getText().toString(), edTelp2.getText().toString(), edFax.getText().toString(),
                edFb.getText().toString(), edTwitter.getText().toString(), edIG.getText().toString(),
                edJamBukaMon.getText().toString(), edJamTutupMon.getText().toString(), ckLiburMon.isChecked()?1:0,
                edJamBukaTue.getText().toString(), edJamTutupTue.getText().toString(), ckLiburTue.isChecked()?1:0,
                edJamBukaWed.getText().toString(), edJamTutupWed.getText().toString(), ckLiburWed.isChecked()?1:0,
                edJamBukaThu.getText().toString(), edJamTutupThu.getText().toString(), ckLiburThu.isChecked()?1:0,
                edJamBukaFri.getText().toString(), edJamTutupFri.getText().toString(), ckLiburFri.isChecked()?1:0,
                edJamBukaSat.getText().toString(), edJamTutupSat.getText().toString(), ckLiburSat.isChecked()?1:0,
                edJamBukaSun.getText().toString(), edJamTutupSun.getText().toString(), ckLiburSun.isChecked()?1:0,
                "**********", id, tanggalNow, tipe, edIDToko.getText().toString().trim()+"-"+hasilImage)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    if(tipe.equals("ADD")){
                                        Toast.makeText(StoreActivity.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(StoreActivity.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                    uploadImage();
                                }else{
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(StoreActivity.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            }catch (JSONException e) {
                                hideDialog();
                                Toast.makeText(StoreActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }catch (IOException e) {
                                hideDialog();
                                Toast.makeText(StoreActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(StoreActivity.this, "Gagal Simpan Data", Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(StoreActivity.this, "Gagal Simpan Data", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                });*/
    }

    private void openImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    //dialog.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } else if (items[i].equals("Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, REQUEST_CODE_GALLERY);
                }
            }
        });
        builder.show();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void pilihJam(final EditText edittext) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(StoreActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String sHrs, sMins;
                if (selectedMinute < 10) {
                    sMins = "0" + selectedMinute;
                } else {
                    sMins = String.valueOf(selectedMinute);
                }
                if (selectedHour < 10) {
                    sHrs = "0" + selectedHour;
                } else {
                    sHrs = String.valueOf(selectedHour);
                }
                setSelectedTime(sHrs, sMins, edittext);
                if(transMode == TransMode.NEW || transMode == TransMode.NEW_MODIFIED){
                    transMode = TransMode.NEW_MODIFIED;
                }else{
                    transMode = TransMode.OLD_MODIFIED;
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Pilih Jam");
        mTimePicker.show();
    }

    public void setSelectedTime(String hourOfDay, String minute, EditText edText) {
        edText.setText(hourOfDay + ":" + minute);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(this.transMode == TransMode.OLD_MODIFIED || this.transMode == TransMode.NEW_MODIFIED){
                new AlertDialog.Builder(this)
                        .setMessage("Data belum disimpan\nYakin akan keluar?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        }).create().show();
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void uploadImage() {
        pDialog.setMessage("Uploading Image to Server...");
        showDialog();
        File file = new File(getRealPathFromURI(selectedImage));
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), file);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), edIDToko.getText().toString().trim()+"-"+hasilImage);
        RequestBody folderBody = RequestBody.create(MediaType.parse("text/plain"), edIDToko.getText().toString().trim());
        mUploadService.uploadImageToko(requestFile, descBody, folderBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("value").equals("false")){
                            Toast.makeText(StoreActivity.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                                //proses_simpan();
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }else{
                            hideDialog();
                            Toast.makeText(StoreActivity.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException e) {
                        hideDialog();
                        e.printStackTrace();
                    }catch (IOException e) {
                        hideDialog();
                        e.printStackTrace();
                    }
                }else{
                    hideDialog();
                    Toast.makeText(StoreActivity.this, "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
                Toast.makeText(StoreActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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

    public void CheckGpsStatus(){
        locationManager = (LocationManager)StoreActivity.this.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {
            try {
                hasilFoto = "Y";
                hasilImage = getNameImage().toString() + ".jpg";
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
                if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
                    this.transMode = TransMode.NEW_MODIFIED;
                }else{
                    this.transMode = TransMode.OLD_MODIFIED;
                }
                //uploadImage(selectedImage, hasilImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                hasilFoto = "Y";
                hasilImage = getNameImage().toString() + ".jpg";
                selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Utils.GetCycleImage("file:///"+picturePath, imgFoto, this);
                String fileNameSegments[] = picturePath.split("/");
                Bitmap myImg = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                myImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                /*String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor == null) return;
                cursor.moveToFirst();
                cursor.close();
                Glide.with(this).load(selectedImage)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgFoto);*/
                if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
                    this.transMode = TransMode.NEW_MODIFIED;
                }else{
                    this.transMode = TransMode.OLD_MODIFIED;
                }
            } catch (Exception e) {
                hideDialog();
                e.printStackTrace();
            }
        }else if(requestCode == RESULT_PROV) {
            if(resultCode == RESULT_OK) {
                kodeProv = data.getStringExtra("kodeProv");
                namaProv = data.getStringExtra("namaProv");
                edProv.setText(namaProv);
                edKabKota.setText(null);
                txtPilihKabKota.setVisibility(View.VISIBLE);
                edKec.setText(null);
                txtPilihKec.setVisibility(View.GONE);
                if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
                    this.transMode = TransMode.NEW_MODIFIED;
                }else{
                    this.transMode = TransMode.OLD_MODIFIED;
                }
            }
        }else if(requestCode == RESULT_KAB) {
            if(resultCode == RESULT_OK) {
                kodeKabKota = data.getStringExtra("kodeKabKota");
                namaKabKota = data.getStringExtra("namaKabKota");
                edKabKota.setText(namaKabKota);
                edKec.setText(null);
                txtPilihKec.setVisibility(View.VISIBLE);
                if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
                    this.transMode = TransMode.NEW_MODIFIED;
                }else{
                    this.transMode = TransMode.OLD_MODIFIED;
                }
            }
        }else if(requestCode == RESULT_KEC) {
            if(resultCode == RESULT_OK) {
                kodeKec = data.getStringExtra("kodeKec");
                namaKec = data.getStringExtra("namaKec");
                edKec.setText(namaKec);
                if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
                    this.transMode = TransMode.NEW_MODIFIED;
                }else{
                    this.transMode = TransMode.OLD_MODIFIED;
                }
            }
        }else if (requestCode == RESULT_MAP) {
            if(resultCode == RESULT_OK) {
                alamatMap = data.getStringExtra("alamat");
                latitude = data.getDoubleExtra("latitude",0);
                longtitude = data.getDoubleExtra("longtitude",0);
                edAlamat.setText(alamatMap);
                edKodePos.setText(data.getStringExtra("postalCode"));
                if(this.transMode == TransMode.NEW || this.transMode == TransMode.NEW_MODIFIED){
                    this.transMode = TransMode.NEW_MODIFIED;
                }else{
                    this.transMode = TransMode.OLD_MODIFIED;
                }
            }
        }
    }

    private String getNameImage() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd-HHmmss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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