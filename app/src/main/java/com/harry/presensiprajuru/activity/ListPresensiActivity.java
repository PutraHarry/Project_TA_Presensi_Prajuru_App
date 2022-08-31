package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.adapter.KegiatanListAdapter;
import com.harry.presensiprajuru.adapter.PresensiListAdapter;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.Kegiatan;
import com.harry.presensiprajuru.model.Presensi;
import com.harry.presensiprajuru.model.PresensiGetResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPresensiActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton tambahPresensiFab;
    RecyclerView presensiList;
    ArrayList<Presensi> presensiArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    PresensiListAdapter presensiListAdapter;

    SwipeRefreshLayout presensiContainer;
    NestedScrollView presensiScrollView;

    private Toolbar homeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_presensi);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tambahPresensiFab = findViewById(R.id.tambah_presensi_fab);
        presensiList = findViewById(R.id.presensi_recycler);
        presensiContainer = findViewById(R.id.presensi_container);
        presensiScrollView = findViewById(R.id.presensi_nested_scroll);

        presensiScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // the delay of the extension of the FAB is set for 12 items
                if (scrollY > oldScrollY + 12 && tambahPresensiFab.isExtended()) {
                    tambahPresensiFab.shrink();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !tambahPresensiFab.isExtended()) {
                    tambahPresensiFab.extend();
                }

                // if the nestedScrollView is at the first item of the list then the
                // extended floating action should be in extended state
                if (scrollY == 0) {
                    tambahPresensiFab.extend();
                }
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        presensiListAdapter = new PresensiListAdapter(this, presensiArrayList);
        presensiList.setLayoutManager(linearLayoutManager);
        presensiList.setAdapter(presensiListAdapter);

        presensiContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        tambahPresensiFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kegiatanBaruIntent = new Intent(getApplicationContext(), CreatePresensiActivity.class);
                startActivity(kegiatanBaruIntent);
            }
        });

        getData();
    }

    public void getData() {
        presensiContainer.setRefreshing(true);
        ApiRoute getData = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<PresensiGetResponse> presensiGetResponseCall = getData.getPresensi();
        presensiGetResponseCall.enqueue(new Callback<PresensiGetResponse>() {
            @Override
            public void onResponse(Call<PresensiGetResponse> call, Response<PresensiGetResponse> response) {
                if (response.code() == 200 && response.body().getStatus().equals(true)) {
                    presensiArrayList.clear();
                    presensiArrayList.addAll(response.body().getPresensiList());
                    presensiListAdapter.notifyDataSetChanged();
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal memuat data Presensi", Snackbar.LENGTH_SHORT).show();
                }
                presensiContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<PresensiGetResponse> call, Throwable t) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal memuat data Presensi", Snackbar.LENGTH_SHORT).show();
                presensiContainer.setRefreshing(false);
            }
        });
    }
}