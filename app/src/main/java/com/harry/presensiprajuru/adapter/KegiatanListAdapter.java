package com.harry.presensiprajuru.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.activity.CreateKegiatanActivity;
import com.harry.presensiprajuru.model.Kegiatan;

import java.util.ArrayList;

public class KegiatanListAdapter extends RecyclerView.Adapter<KegiatanListAdapter.ViewHolder> {
    private Context context;
    //0 for list, 1 for select
    private Boolean selectKegiatanMode;
    private final ArrayList<Kegiatan> kegiatanArrayList;
    private int position;
    private final String KEGIATAN_KEY = "KEGIATAN_KEY";
    private final Gson gson = new Gson();

    public KegiatanListAdapter(Context context, ArrayList<Kegiatan> kegiatanArrayList, Boolean selectKegiatanMode) {
        this.context = context;
        this.kegiatanArrayList = kegiatanArrayList;
        this.selectKegiatanMode = selectKegiatanMode;
    }

    @NonNull
    @Override
    public KegiatanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.kegiatan_card_layout, parent, false);
        return new KegiatanListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KegiatanListAdapter.ViewHolder holder, int position) {
        holder.kegiatanNamaText.setText(kegiatanArrayList.get(holder.getAdapterPosition()).getNamaKegiatan());
        holder.kegiatanDeskripsiText.setText(kegiatanArrayList.get(holder.getAdapterPosition()).getKeterangan());

        if (selectKegiatanMode) {
            holder.kegiatanDetailButton.setText("Pilih Kegiatan");
            holder.kegiatanDetailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Kegiatan kegiatan = kegiatanArrayList.get(holder.getAdapterPosition());
                    Intent kegiatanSelected = new Intent();
                    kegiatanSelected.putExtra(KEGIATAN_KEY, gson.toJson(kegiatan));
                    ((Activity) context).setResult(100, kegiatanSelected);
                    ((Activity) context).finish();
                }
            });
        } else {
            holder.kegiatanDetailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Kegiatan kegiatan = kegiatanArrayList.get(holder.getAdapterPosition());
                    Intent kegiatanDetail = new Intent(context, CreateKegiatanActivity.class);
                    kegiatanDetail.putExtra(KEGIATAN_KEY, gson.toJson(kegiatan));
                    context.startActivity(kegiatanDetail);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return kegiatanArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView kegiatanNamaText, kegiatanDeskripsiText;
        private final Button kegiatanDetailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kegiatanNamaText = itemView.findViewById(R.id.kegiatan_nama_card_text);
            kegiatanDeskripsiText = itemView.findViewById(R.id.kegiatan_deskripsi_card_text);
            kegiatanDetailButton = itemView.findViewById(R.id.kegiatan_detail_card_button);
        }
    }
}
