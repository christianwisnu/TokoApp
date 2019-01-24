package list;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.project.tokoapp.R;

import java.util.ArrayList;
import java.util.Arrays;

import adapter.list.AdpKabKota;
import model.list.ListKabKota;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;
import utilities.JSONResponse;
import utilities.Link;

/**
 * Created by Chris on 25/03/2018.
 */

public class ListKabKotaView extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<ListKabKota> mArrayList;
    private ArrayList<ListKabKota> mFilteredList;
    private AdpKabKota mAdapter;
    private ProgressDialog progress;
    private BaseApiService mApiService;
    private String kodeProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_kab_kota);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                kodeProv = i.getString("kodeProv");
            } catch (Exception e) {}
        }
        mApiService         = Link.getAPIService();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarKabKota);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar Kab/Kota");
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();
        initViews();
        loadJSON();
    }

    private void initViews(){
        mRecyclerView = (RecyclerView)findViewById(R.id.card_recycler_view_kabkota);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.custom_divider));
        mRecyclerView.addItemDecoration(divider);
    }

    private void loadJSON(){
        mApiService.getKabKota(kodeProv).enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                mArrayList = new ArrayList<>(Arrays.asList(jsonResponse.getKabkota()));
                mAdapter = new AdpKabKota(ListKabKotaView
                        .this, mArrayList);
                //assert mRecyclerView != null;
                mRecyclerView.setAdapter(mAdapter);
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(ListKabKotaView.this, "Jaringan Error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
