package adapter.master;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.tokoapp.R;

import java.util.List;

import model.master.MerchantListModel;

public class AdpMerchant extends ArrayAdapter<MerchantListModel> {

    private List<MerchantListModel> columnslist;
    private LayoutInflater vi;
    private int Resource;
    private Context context;
    private ViewHolder holder;
    private AlertDialog alert;
    private Spanned span;

    public AdpMerchant(Context context, int resource, List<MerchantListModel> objects) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View v	=	convertView;
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.ImgDelete	=	 (ImageView)v.findViewById(R.id.ImgColMerchantDelete);
            holder.imgEdit	=	 (ImageView)v.findViewById(R.id.ImgColMerchantEdit);
            holder.imgVerify	=	 (ImageView)v.findViewById(R.id.imgColMerchantVerify);
            holder.TvNamaToko = 	 (TextView)v.findViewById(R.id.TvColMerchantNama);
            holder.Tvverificated =  (TextView)v.findViewById(R.id.TvColMerchantVerified);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }

        holder.ImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder msMaintance = new AlertDialog.Builder(getContext());
                msMaintance.setCancelable(false);
                msMaintance.setMessage("Yakin akan dihapus? ");
                msMaintance.setNegativeButton("Ya", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*columnslist.remove(position);
                        notifyDataSetChanged();
                        columnslist.get(position).setStat("D");*/
                    }
                });

                msMaintance.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                    }
                });
                alert	=msMaintance.create();
                alert.show();
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.TvNamaToko.setText(columnslist.get(position).getNama());
        if(columnslist.get(position).getVerificated().intValue() == 0){
            span= Html.fromHtml("<i>Has Not Been Verified</i>");
            holder.Tvverificated.setText(span);
            holder.Tvverificated.setTextColor(Color.RED);
            holder.imgVerify.setImageResource(R.drawable.notverified);
        }else{
            span= Html.fromHtml("<i>Already Verified</i>");
            holder.Tvverificated.setText(span);
            holder.Tvverificated.setTextColor(Color.GREEN);
            holder.imgVerify.setImageResource(R.drawable.verified);
        }

        return v;
    }

    static class ViewHolder{
        private ImageView ImgDelete;
        private ImageView imgEdit;
        private ImageView imgVerify;
        private TextView TvNamaToko;
        private TextView Tvverificated;
    }
}
