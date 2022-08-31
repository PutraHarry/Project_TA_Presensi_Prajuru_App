package com.harry.presensiprajuru.adapter;

import android.app.Activity;
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
import com.harry.presensiprajuru.activity.DetailPresensiActivity;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.harry.presensiprajuru.model.Presensi;
import com.harry.presensiprajuru.model.PresensiDetailResponse;
import com.harry.presensiprajuru.model.Tempekan;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TempekanListAdapter  extends RecyclerView.Adapter<TempekanListAdapter.ViewHolder> {
    private Context context;
    private final ArrayList<Tempekan> tempekanArrayList;
    private int position;
    private final String TEMPEKAN_KEY = "TEMPEKAN_KEY";
    private final Gson gson = new Gson();

    public TempekanListAdapter(Context context, ArrayList<Tempekan> tempekanArrayList) {
        this.context = context;
        this.tempekanArrayList = tempekanArrayList;
    }

    @NonNull
    @Override
    public TempekanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tempekan_card_list, parent, false);
        return new TempekanListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempekanListAdapter.ViewHolder holder, int position) {
        holder.tempekanName.setText(tempekanArrayList.get(holder.getAdapterPosition()).getNamaTempekan());
        holder.jumlahKrama.setText(String.format("Jumlah Krama: %s", tempekanArrayList.get(holder.getAdapterPosition()).getJumlahKrama()));

        holder.tempekanSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tempekan tempekan = tempekanArrayList.get(holder.getAdapterPosition());
                Intent tempekanSelected = new Intent();
                tempekanSelected.putExtra(TEMPEKAN_KEY, gson.toJson(tempekan));
                ((Activity) context).setResult(200, tempekanSelected);
                ((Activity) context).finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return tempekanArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView tempekanName, jumlahKrama;
        final Button tempekanSelectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tempekanName = itemView.findViewById(R.id.tempekan_name_text);
            jumlahKrama = itemView.findViewById(R.id.tempekan_jumlah_krama_text);
            tempekanSelectButton = itemView.findViewById(R.id.tempekan_select_button);
        }
    }
}