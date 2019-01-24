package adapter.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.project.tokoapp.R;

import java.util.ArrayList;

import model.list.ListKecamatan;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Chris on 27/03/2018.
 */

public class AdpKecamatan extends RecyclerView.Adapter<AdpKecamatan.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<ListKecamatan> mArrayList;
    private ArrayList<ListKecamatan> mFilteredList;

    public AdpKecamatan(Context contextku, ArrayList<ListKecamatan> arrayList) {
        context = contextku;
        mArrayList = arrayList;
        mFilteredList = arrayList;
    }

    @Override
    public AdpKecamatan.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_kecamatan, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdpKecamatan.ViewHolder viewHolder, int i) {
        viewHolder.tv_kode.setText(mFilteredList.get(i).getKodeKec());
        viewHolder.tv_nama.setText(mFilteredList.get(i).getNamaKec());
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mArrayList;
                } else {
                    ArrayList<ListKecamatan> filteredList = new ArrayList<>();
                    for (ListKecamatan entity : mArrayList) {
                        if (entity.getKodeKec().toLowerCase().contains(charString) ||
                                entity.getNamaKec().toLowerCase().contains(charString) ) {
                            filteredList.add(entity);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ListKecamatan>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_kode,tv_nama;

        public ViewHolder(View view) {
            super(view);
            tv_kode = (TextView)view.findViewById(R.id.txt_view_kecamatan_kode);
            tv_nama = (TextView)view.findViewById(R.id.txt_view_kecamatan_nama);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("kodeKec", tv_kode.getText().toString());
            intent.putExtra("namaKec", tv_nama.getText().toString());
            ((Activity)context).setResult(RESULT_OK, intent);
            ((Activity)context).finish();
        }
    }
}
