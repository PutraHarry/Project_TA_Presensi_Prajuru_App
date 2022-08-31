package com.harry.presensiprajuru.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.activity.DetailKramaActivity;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CacahKramaMipilAdapter extends RecyclerView.Adapter<CacahKramaMipilAdapter.ViewHolder> {
    private Context context;
    private final ArrayList<CacahKramaMipil> cacahKramaMipilArrayList;
    private int position;
    private final String KRAMA_KEY = "KRAMA_KEY";
    private final String KRAMA_SELECT_KEY = "KRAMA_SELECT_KEY";
    private final Gson gson = new Gson();
    private int selectKramaMode;
    /**
     * 0= view daftar krama
     * 1= milih rama
     * 2= daftar krama untuk create presensi baru
     */

    public CacahKramaMipilAdapter(Context context, ArrayList<CacahKramaMipil> cacahKramaMipilArrayList, int selectKramaMode) {
        this.context = context;
        this.cacahKramaMipilArrayList = cacahKramaMipilArrayList;
        this.selectKramaMode = selectKramaMode;
    }

    @NonNull
    @Override
    public CacahKramaMipilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cacah_krama_mipil_card_layout, parent, false);
        return new CacahKramaMipilAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CacahKramaMipilAdapter.ViewHolder holder, int position) {
        holder.kramaName.setText(cacahKramaMipilArrayList.get(holder.getAdapterPosition()).getPenduduk().getNama());
        holder.kramaNic.setText(cacahKramaMipilArrayList.get(holder.getAdapterPosition()).getNomorCacahKramaMipil());

        if (cacahKramaMipilArrayList.get(holder.getAdapterPosition()).getPenduduk().getFoto() != null) {
            holder.kramaImageLoadingContainer.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(context.getResources().getString(R.string.sikramat_endpoint) +
                            cacahKramaMipilArrayList.get(holder.getAdapterPosition()).getPenduduk().getFoto())
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


        holder.kramaSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacahKramaMipil cacahKramaMipil = cacahKramaMipilArrayList.get(holder.getAdapterPosition());
                Intent cacahKramaSelected = new Intent();
                cacahKramaSelected.putExtra(KRAMA_SELECT_KEY, gson.toJson(cacahKramaMipil));
                ((Activity) context).setResult(100, cacahKramaSelected);
                ((Activity) context).finish();
            }
        });

        holder.detailKramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacahKramaMipil cacahKramaMipil = cacahKramaMipilArrayList.get(holder.getAdapterPosition());
                Intent cacahKramaMipilDetailIntent = new Intent(context, DetailKramaActivity.class);
                cacahKramaMipilDetailIntent.putExtra(KRAMA_KEY, cacahKramaMipil.getId());
                context.startActivity(cacahKramaMipilDetailIntent);
            }
        });

        holder.deleteKramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cacahKramaMipilArrayList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), cacahKramaMipilArrayList.size());
            }
        });
    }


    @Override
    public int getItemCount() {
        return cacahKramaMipilArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView kramaName, kramaNic;
        private final Button kramaSelectButton, detailKramaButton, deleteKramaButton;
        private final ImageView kramaImage;
        private final LinearLayout kramaImageLoadingContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kramaName = itemView.findViewById(R.id.krama_card_name_text);
            kramaNic = itemView.findViewById(R.id.krama_card_nic_text);
            kramaSelectButton = itemView.findViewById(R.id.krama_card_select_button);
            detailKramaButton = itemView.findViewById(R.id.krama_card_detail_button);
            deleteKramaButton = itemView.findViewById(R.id.krama_card_delete_button);
            kramaImage = itemView.findViewById(R.id.profile_image);
            kramaImageLoadingContainer = itemView.findViewById(R.id.profile_image_loading_container);
            if (selectKramaMode == 0) {
                kramaSelectButton.setVisibility(View.GONE);
                deleteKramaButton.setVisibility(View.GONE);
            } else if (selectKramaMode == 1) {
                kramaSelectButton.setVisibility(View.VISIBLE);
                deleteKramaButton.setVisibility(View.GONE);
            } else if (selectKramaMode == 2) {
                kramaSelectButton.setVisibility(View.GONE);
                deleteKramaButton.setVisibility(View.VISIBLE);
            }
        }
    }
}