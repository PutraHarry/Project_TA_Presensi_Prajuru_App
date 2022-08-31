package com.harry.presensiprajuru.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.activity.DetailKramaActivity;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.harry.presensiprajuru.model.DetailPresensi;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class DetailPresensiListAdapter extends RecyclerView.Adapter<DetailPresensiListAdapter.ViewHolder> {
    private Context context;
    private final ArrayList<DetailPresensi> detailPresensiArrayList;
    private int position;
    private final String KRAMA_KEY = "KRAMA_KEY";
    private final Gson gson = new Gson();

    public DetailPresensiListAdapter(Context context, ArrayList<DetailPresensi> detailPresensiArrayList) {
        this.context = context;
        this.detailPresensiArrayList = detailPresensiArrayList;
    }

    @NonNull
    @Override
    public DetailPresensiListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.detail_presensi_card_layout, parent, false);
        return new DetailPresensiListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailPresensiListAdapter.ViewHolder holder, int position) {
        holder.kramaName.setText(detailPresensiArrayList.get(holder.getAdapterPosition()).getCacahKramaMipil().getPenduduk().getNama());
        holder.kramaNic.setText(detailPresensiArrayList.get(holder.getAdapterPosition()).getCacahKramaMipil().getNomorCacahKramaMipil());


        if (detailPresensiArrayList.get(holder.getAdapterPosition()).getIsHadir() == 1) {
            holder.tanggalPengisian.setText(changeDateTimeFormatForCreatedAt(detailPresensiArrayList.get(holder.getAdapterPosition()).getUpdatedAt()));
            holder.presensiStatusChip.setText("Sudah Mengisi");
            holder.presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.greenSemiTransparent)));
        } else if (detailPresensiArrayList.get(holder.getAdapterPosition()).getIsHadir() == 0) {
            holder.presensiStatusChip.setText("Belum Mengisi");
            holder.presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.secondaryLightColorSemiTransparent)));
        }

        if (detailPresensiArrayList.get(holder.getAdapterPosition()).getCacahKramaMipil().getPenduduk().getFoto() != null) {
            holder.kramaImageLoadingContainer.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(context.getResources().getString(R.string.sikramat_endpoint) +
                            detailPresensiArrayList.get(holder.getAdapterPosition()).getCacahKramaMipil().getPenduduk().getFoto())
                    .fit().centerCrop()
                    .into(holder.kramaImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            holder.kramaImageLoadingContainer.setVisibility(View.GONE);
                            holder.kramaImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.kramaImageLoadingContainer.setVisibility(View.GONE);
                            Picasso.get().load(R.drawable.profile_placeholder).into(holder.kramaImage);
                        }
                    });
        }
        else {
            Picasso.get().load(R.drawable.profile_placeholder).into(holder.kramaImage);
            holder.kramaImage.setVisibility(View.VISIBLE);
        }

        holder.kramaDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacahKramaMipil cacahKramaMipil = detailPresensiArrayList.get(holder.getAdapterPosition()).getCacahKramaMipil();
                Intent cacahKramaMipilDetailIntent = new Intent(context, DetailKramaActivity.class);
                cacahKramaMipilDetailIntent.putExtra(KRAMA_KEY, cacahKramaMipil.getId());
                context.startActivity(cacahKramaMipilDetailIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return detailPresensiArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView kramaName, kramaNic, tanggalPengisian;
        private final Chip presensiStatusChip;
        private final Button kramaDetailButton;
        private final ImageView kramaImage;
        private final LinearLayout kramaImageLoadingContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kramaName = itemView.findViewById(R.id.presensi_detail_card_krama_nama_text);
            kramaNic = itemView.findViewById(R.id.presensi_detail_card_krama_nic_text);
            tanggalPengisian = itemView.findViewById(R.id.presensi_detail_card_date_text);
            kramaDetailButton = itemView.findViewById(R.id.presensi_detail_card_krama_detail_button);
            presensiStatusChip = itemView.findViewById(R.id.status_presensi_chip);
            kramaImage = itemView.findViewById(R.id.profile_image);
            kramaImageLoadingContainer = itemView.findViewById(R.id.profile_image_loading_container);
        }
    }

    public static String changeDateTimeFormatForCreatedAt (String time) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(time);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMMM-yyyy, HH:mm"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            time = dateFormatter.format(value);
        }
        catch (Exception e)
        {
            time = "00-00-0000 00:00";
        }
        Log.d("tanggal", time);
        return time;
    }
}