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

import model.list.ListKabKota;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Chris on 27/03/2018.
 */

public class AdpKabKota extends RecyclerView.Adapter<AdpKabKota.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<ListKabKota> mArrayList;
    private ArrayList<ListKabKota> mFilteredList;

    public AdpKabKota(Context contextku, ArrayList<ListKabKota> arrayList) {
        context = contextku;
        mArrayList = arrayList;
        mFilteredList = arrayList;
    }

    @Override
    public AdpKabKota.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_kabkota, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdpKabKota.ViewHolder viewHolder, int i) {
        viewHolder.tv_kode.setText(mFilteredList.get(i).getKodeKabKota());
        viewHolder.tv_nama.setText(mFilteredList.get(i).getNamaKabKota());
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
                    ArrayList<ListKabKota> filteredList = new ArrayList<>();
                    for (ListKabKota entity : mArrayList) {
                        if (entity.getKodeKabKota().toLowerCase().contains(charString) ||
                                entity.getNamaKabKota().toLowerCase().contains(charString) ) {
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
                mFilteredList = (ArrayList<ListKabKota>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_kode,tv_nama;

        public ViewHolder(View view) {
            super(view);
            tv_kode = (TextView)view.findViewById(R.id.txt_view_kabkota_kode);
            tv_nama = (TextView)view.findViewById(R.id.txt_view_kabkota_nama);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("kodeKabKota", tv_kode.getText().toString());
            intent.putExtra("namaKabKota", tv_nama.getText().toString());
            ((Activity)context).setResult(RESULT_OK, intent);
            ((Activity)context).finish();
        }
    }
}
