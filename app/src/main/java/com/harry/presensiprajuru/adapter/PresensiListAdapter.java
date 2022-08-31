package com.harry.presensiprajuru.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.activity.CreateKegiatanActivity;
import com.harry.presensiprajuru.activity.DetailPresensiActivity;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.Kegiatan;
import com.harry.presensiprajuru.model.Presensi;
import com.harry.presensiprajuru.model.PresensiDetailResponse;
import com.harry.presensiprajuru.model.PresensiGetResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresensiListAdapter extends RecyclerView.Adapter<PresensiListAdapter.ViewHolder> {
    private Context context;
    private final ArrayList<Presensi> presensiArrayList;
    private int position;
    private final String PRESENSI_KEY = "PRESENSI_KEY";
    private final Gson gson = new Gson();
    private ProgressDialog dialog;

    public PresensiListAdapter(Context context, ArrayList<Presensi> presensiArrayList) {
        this.context = context;
        this.presensiArrayList = presensiArrayList;
        dialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public PresensiListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.presensi_card_layout, parent, false);
        return new PresensiListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PresensiListAdapter.ViewHolder holder, int position) {
        holder.presensiName.setText(presensiArrayList.get(holder.getAdapterPosition()).getNamaPresensi());
        holder.presensiDesc.setText(presensiArrayList.get(holder.getAdapterPosition()).getKeterangan());
        holder.presensiDateOpen.setText(presensiArrayList.get(holder.getAdapterPosition()).getTglOpen());
        holder.presensiDateClose.setText(presensiArrayList.get(holder.getAdapterPosition()).getTglClose());

        if (presensiArrayList.get(holder.getAdapterPosition()).getIsOpen() == 0) {
            holder.presensiStatusChip.setText("Presensi Tertutup");
            holder.presensiOpenCloseButton.setText("Buka Presensi");
            holder.presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.secondaryLightColorSemiTransparent)));
        } else {
            holder.presensiStatusChip.setText("Presensi Terbuka");
            holder.presensiOpenCloseButton.setText("Tutup Presensi");
            holder.presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.greenSemiTransparent)));
        }

        holder.presensiOpenCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("Mohon tunggu...");
                dialog.show();
                holder.presensiOpenCloseButton.setEnabled(false);
                ApiRoute getData = RetrofitClient.buildRetrofit().create(ApiRoute.class);
                Call<PresensiDetailResponse> presensiDetailResponseCall = getData.openClosePresensi(presensiArrayList.get(holder.getAdapterPosition()).getId());
                presensiDetailResponseCall.enqueue(new Callback<PresensiDetailResponse>() {
                    @Override
                    public void onResponse(Call<PresensiDetailResponse> call, Response<PresensiDetailResponse> response) {
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                        holder.presensiOpenCloseButton.setEnabled(true);
                        if (response.code() == 200 && response.body().getStatus().equals(true)) {
                            if (response.body().getPresensi().getIsOpen() == 0) {
                                Snackbar.make(view, "Berhasil menutup presensi", Snackbar.LENGTH_SHORT).show();
                                holder.presensiStatusChip.setText("Presensi Tertutup");
                                holder.presensiOpenCloseButton.setText("Buka Presensi");
                                holder.presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.secondaryLightColorSemiTransparent)));
                            } else {
                                Snackbar.make(view, "Berhasil membuka presensi", Snackbar.LENGTH_SHORT).show();
                                holder.presensiStatusChip.setText("Presensi Terbuka");
                                holder.presensiOpenCloseButton.setText("Tutup Presensi");
                                holder.presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.greenSemiTransparent)));
                            }
                        }
                        else {
                            if (presensiArrayList.get(holder.getAdapterPosition()).getIsOpen() == 0) {
                                Snackbar.make(view, "Gagal membuka presensi", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "Gagal menutup presensi", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PresensiDetailResponse> call, Throwable t) {
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                        if (presensiArrayList.get(holder.getAdapterPosition()).getIsOpen() == 0) {
                            Snackbar.make(view, "Gagal membuka presensi", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(view, "Gagal menutup presensi", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        holder.presensiDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Presensi presensi = presensiArrayList.get(holder.getAdapterPosition());
                Intent presensiDetail = new Intent(context, DetailPresensiActivity.class);
                presensiDetail.putExtra(PRESENSI_KEY, gson.toJson(presensi));
                context.startActivity(presensiDetail);
            }
        });
    }


    @Override
    public int getItemCount() {
        return presensiArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView presensiName, presensiDesc, presensiDateOpen, presensiDateClose;
        private final Button presensiDetailButton, presensiOpenCloseButton;
        private final Chip presensiStatusChip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            presensiName = itemView.findViewById(R.id.presensi_card_title_text);
            presensiDesc = itemView.findViewById(R.id.presensi_card_desc_text);
            presensiDateOpen = itemView.findViewById(R.id.presensi_card_open_date_text);
            presensiDateClose = itemView.findViewById(R.id.presensi_card_close_date_text);
            presensiDetailButton = itemView.findViewById(R.id.presensi_card_detail_button);
            presensiStatusChip = itemView.findViewById(R.id.status_presensi_chip);
            presensiOpenCloseButton = itemView.findViewById(R.id.presensi_card_openclose_button);
        }
    }
}