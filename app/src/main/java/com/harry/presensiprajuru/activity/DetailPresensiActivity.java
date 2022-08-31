package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.adapter.DetailPresensiListAdapter;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.DetailPresensi;
import com.harry.presensiprajuru.model.DetailPresensiResponse;
import com.harry.presensiprajuru.model.Kegiatan;
import com.harry.presensiprajuru.model.Presensi;
import com.harry.presensiprajuru.model.PresensiDetailResponse;
import com.harry.presensiprajuru.model.PresensiGetResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPresensiActivity extends AppCompatActivity {

    private TextView presensiTitleText, presensiDescText, presensiDateOpenText,
            presensiDateCloseText, presensiKegiatanName, presensiKegiatanDesc, presensiCodeText;

    private Button presensiOpenCloseButton;
    private Chip presensiStatusChip;

    private RecyclerView presensiDetailList;
    private SwipeRefreshLayout presensiContainer;

    private LinearLayoutManager linearLayoutManager;
    private DetailPresensiListAdapter detailPresensiListAdapter;
    private ArrayList<DetailPresensi> detailPresensiArrayList = new ArrayList<>();

    private Presensi presensi;
    private Gson gson = new Gson();

    private final String PRESENSI_KEY = "PRESENSI_KEY";
    private SharedPreferences loginPref;

    private ProgressDialog dialog;
    private Toolbar homeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_presensi);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loginPref = getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);

        presensi = gson.fromJson(getIntent().getStringExtra(PRESENSI_KEY), Presensi.class);

        presensiTitleText = findViewById(R.id.presensi_detail_title_text);
        presensiDescText = findViewById(R.id.presensi_detail_desc_text);
        presensiDateOpenText = findViewById(R.id.presensi_detail_open_date_text);
        presensiDateCloseText = findViewById(R.id.presensi_detail_close_date_text);
        presensiKegiatanName = findViewById(R.id.presensi_detail_kegiatan_name_text);
        presensiKegiatanDesc = findViewById(R.id.presensi_detail_kegiatan_desc_text);
        presensiStatusChip = findViewById(R.id.status_presensi_chip);
        presensiOpenCloseButton = findViewById(R.id.presensi_card_openclose_button);
        presensiCodeText = findViewById(R.id.presensi_code_text);

        presensiOpenCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openClosePresensi(presensi.getId());
            }
        });

        presensiDetailList = findViewById(R.id.detail_presensi_list);
        presensiContainer = findViewById(R.id.presensi_container);

        linearLayoutManager = new LinearLayoutManager(this);
        detailPresensiListAdapter = new DetailPresensiListAdapter(this, detailPresensiArrayList);
        presensiDetailList.setLayoutManager(linearLayoutManager);
        presensiDetailList.setAdapter(detailPresensiListAdapter);

        presensiContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData( loginPref.getString("token", "empty"), presensi.getId() );
            }
        });

        getData(loginPref.getString("token", "empty"), presensi.getId());
    }

    public void getData(String token, int presensiId) {
        presensiContainer.setRefreshing(true);
        ApiRoute getData = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<DetailPresensiResponse> presensiGetResponseCall = getData.getDetailPresnesi(token, presensiId);
        presensiGetResponseCall.enqueue(new Callback<DetailPresensiResponse>() {
            @Override
            public void onResponse(Call<DetailPresensiResponse> call, Response<DetailPresensiResponse> response) {
                if (response.code() == 200 && response.body().getStatus().equals(true)) {
                    detailPresensiArrayList.clear();
                    if (response.body().getDetailPresensiList() != null) {
                        detailPresensiArrayList.addAll(response.body().getDetailPresensiList());
                        detailPresensiListAdapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Gagal memuat data detail Presensi", Snackbar.LENGTH_SHORT).show();
                    }

                    presensiTitleText.setText(response.body().getPresensi().getNamaPresensi());
                    presensiDescText.setText(response.body().getPresensi().getKeterangan());
                    presensiDateOpenText.setText(response.body().getPresensi().getTglOpen());
                    presensiDateCloseText.setText(response.body().getPresensi().getTglClose());
                    presensiKegiatanName.setText(response.body().getPresensi().getKegiatan().getNamaKegiatan());
                    presensiKegiatanDesc.setText(response.body().getPresensi().getKegiatan().getKeterangan());
                    presensiCodeText.setText(response.body().getPresensi().getKodePresensi().toString());

                    if (response.body().getPresensi().getIsOpen() == 0) {
                        presensiStatusChip.setText("Presensi Tertutup");
                        presensiOpenCloseButton.setText("Buka Presensi");
                        presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(
                                ContextCompat.getColor(getApplicationContext(), R.color.secondaryLightColorSemiTransparent)));
                    } else {
                        presensiStatusChip.setText("Presensi Terbuka");
                        presensiOpenCloseButton.setText("Tutup Presensi");
                        presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(
                                ContextCompat.getColor(getApplicationContext(), R.color.greenSemiTransparent)));
                    }
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal memuat data Presensi", Snackbar.LENGTH_SHORT).show();
                }
                presensiContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<DetailPresensiResponse> call, Throwable t) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal memuat data Presensi", Snackbar.LENGTH_SHORT).show();
                presensiContainer.setRefreshing(false);
            }
        });
    }

    public void openClosePresensi(Integer idPresensi) {
        dialog.setMessage("Mohon tunggu...");
        dialog.show();
        presensiOpenCloseButton.setEnabled(false);
        ApiRoute getData = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<PresensiDetailResponse> presensiDetailResponseCall = getData.openClosePresensi(idPresensi);
        presensiDetailResponseCall.enqueue(new Callback<PresensiDetailResponse>() {
            @Override
            public void onResponse(Call<PresensiDetailResponse> call, Response<PresensiDetailResponse> response) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                presensiOpenCloseButton.setEnabled(true);
                if (response.code() == 200 && response.body().getStatus().equals(true)) {
                    if (response.body().getPresensi().getIsOpen() == 0) {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Berhasil menutup presensi", Snackbar.LENGTH_SHORT).show();
                        presensiStatusChip.setText("Presensi Tertutup");
                        presensiOpenCloseButton.setText("Buka Presensi");
                        presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(
                                ContextCompat.getColor(getApplicationContext(), R.color.secondaryLightColorSemiTransparent)));
                    } else {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Berhasil membuka presensi", Snackbar.LENGTH_SHORT).show();
                        presensiStatusChip.setText("Presensi Terbuka");
                        presensiOpenCloseButton.setText("Tutup Presensi");
                        presensiStatusChip.setChipBackgroundColor(ColorStateList.valueOf(
                                ContextCompat.getColor(getApplicationContext(), R.color.greenSemiTransparent)));
                    }
                }
                else {
                    if (presensi.getIsOpen() == 0) {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Gagal membuka presensi", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Gagal menutup presensi", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PresensiDetailResponse> call, Throwable t) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                if (presensi.getIsOpen() == 0) {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal membuka presensi", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal menutup presensi", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}