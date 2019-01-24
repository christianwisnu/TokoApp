package fragment.master;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.example.project.tokoapp.R;
import com.example.project.tokoapp.StoreActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapter.master.AdpMerchant;
import model.master.MerchantListModel;
import utilities.AppController;
import utilities.Link;
import utilities.PrefUtil;

public class FrgMerchantUser extends AppCompatActivity{

    private ListView lsvupload;
    private ArrayList<MerchantListModel> columnlist= new ArrayList<MerchantListModel>();
    private TextView tvstatus;
    private ProgressBar prbstatus;
    private AdpMerchant adapter;
    private ImageButton imbAdd;
    private ImageView imgBack;
    private PrefUtil prefUtil;
    private SharedPreferences shared;
    private String id;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frg_merchant);
        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            id      = shared.getString(PrefUtil.ID, null);
        }catch (Exception e){e.getMessage();}
        imgBack = (ImageView) findViewById(R.id.img_frgmerchant_back);
        imbAdd = (ImageButton)findViewById(R.id.bListMerchantAdd);
        lsvupload	= (ListView)findViewById(R.id.LsvListMerchant);
        tvstatus	= (TextView)findViewById(R.id.TvStatusListMerchant);
        prbstatus	= (ProgressBar)findViewById(R.id.PrbStatusListMerchant);
        adapter		= new AdpMerchant(FrgMerchantUser.this, R.layout.col_list_merchant, columnlist);
        lsvupload.setAdapter(adapter);
        getDataUpload(Link.BASE_URL_API+"getMerchantByUser.php?id="+id);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(FrgMerchantUser.this, StoreActivity.class);
                i.putExtra("Status", 1);
                i.putExtra("Tittle", "Tambah Data");
                //i.putExtra("ID", tbMobil.getLastIdQuotaAdded());
                startActivity(i);//forResult
            }
        });

        lsvupload.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent i = new Intent(FrgMerchantUser.this, InfoData.class);
                i.putExtra("idBerita", columnlist.get(position).getIdBerita());
                i.putExtra("namaTempat", columnlist.get(position).getNamaTempat());
                i.putExtra("judul", columnlist.get(position).getJudul());
                i.putExtra("isi", columnlist.get(position).getIsi());
                i.putExtra("alamat", columnlist.get(position).getAlamat());
                i.putExtra("latt", columnlist.get(position).getLatt());
                i.putExtra("longt", columnlist.get(position).getLongt());
                i.putExtra("gambar", columnlist.get(position).getPathGbr());
                i.putExtra("idKategori", columnlist.get(position).getIdKategori());
                i.putExtra("kategori", columnlist.get(position).getKategori());
                i.putExtra("idSubKategori", columnlist.get(position).getIdSubKategori());
                i.putExtra("subKategori", columnlist.get(position).getNamaSubKategori());
                i.putExtra("rating", columnlist.get(position).getRating());
                i.putExtra("countUser", columnlist.get(position).getCountUser());
                i.putExtra("jamBuka", columnlist.get(position).getJamBuka());
                i.putExtra("jamTutup", columnlist.get(position).getJamTutup());
                i.putExtra("harga", columnlist.get(position).getHarga());
                i.putExtra("tglEvent", columnlist.get(position).getTglEvent());
                i.putExtra("login", login);
                i.putExtra("idUser", idUser);
                i.putExtra("profile", profile);
                startActivity(i);*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getDataUpload(String Url){
        tvstatus.setVisibility(View.GONE);
        prbstatus.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            Log.i("Status", String.valueOf(sucses));
                            if (sucses==1){
                                tvstatus.setVisibility(View.GONE);
                                prbstatus.setVisibility(View.GONE);
                                adapter.clear();
                                JSONArray JsonArray = response.getJSONArray("uploade");
                                for (int i = 0; i < JsonArray.length(); i++) {
                                    JSONObject object = JsonArray.getJSONObject(i);
                                    MerchantListModel colums 	= new MerchantListModel();
                                    colums.setId(object.getString("id"));
                                    colums.setNama(object.getString("nama"));
                                    colums.setPathFoto(object.getString("foto"));
                                    colums.setVerificated(object.getInt("verify"));
                                    colums.setRating(new BigDecimal(object.getDouble("rating")));
                                    colums.setCountUserRating(object.getInt("countUser"));
                                    columnlist.add(colums);
                                }
                            }else{
                                tvstatus.setVisibility(View.VISIBLE);
                                tvstatus.setText("Tidak Ada Toko");
                                prbstatus.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        lsvupload.invalidate();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check Koneksi Internet Anda");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("AuthFailureError");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof ServerError) {
                    //TODO
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check ServerError");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof NetworkError) {
                    //TODO
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check NetworkError");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof ParseError) {
                    //TODO
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check ParseError");
                    prbstatus.setVisibility(View.GONE);
                }
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().invalidate(Url, true);
        AppController.getInstance().addToRequestQueue(jsonget);
    }
}
