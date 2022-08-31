package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.adapter.KegiatanListAdapter;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.Kegiatan;
import com.harry.presensiprajuru.model.KegiatanGetResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListKegiatanActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton tambahKegiatanFab;
    RecyclerView kegiatanList;
    ArrayList<Kegiatan> kegiatanArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    KegiatanListAdapter kegiatanListAdapter;
    SwipeRefreshLayout kegiatanContainer;
    NestedScrollView kegiatanScrollView;

    private Boolean selectKegiatanMode = false;
    private Toolbar homeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kegiatan);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tambahKegiatanFab = findViewById(R.id.tambah_kegiatan_fab);
        kegiatanList = findViewById(R.id.kegiatan_recycler);
        kegiatanContainer = findViewById(R.id.kegiatan_container);
        kegiatanScrollView = findViewById(R.id.kegiatan_nested_scroll);

        kegiatanScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // the delay of the extension of the FAB is set for 12 items
                if (scrollY > oldScrollY + 12 && tambahKegiatanFab.isExtended()) {
                    tambahKegiatanFab.shrink();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !tambahKegiatanFab.isExtended()) {
                    tambahKegiatanFab.extend();
                }

                // if the nestedScrollView is at the first item of the list then the
                // extended floating action should be in extended state
                if (scrollY == 0) {
                    tambahKegiatanFab.extend();
                }
            }
        });

        if ( getIntent().getIntExtra("SELECT_KEGIATAN", -1) == 1) {
            selectKegiatanMode = true;
            tambahKegiatanFab.setVisibility(View.GONE);
        }

        linearLayoutManager = new LinearLayoutManager(this);
        kegiatanListAdapter = new KegiatanListAdapter(this, kegiatanArrayList, selectKegiatanMode);
        kegiatanList.setLayoutManager(linearLayoutManager);
        kegiatanList.setAdapter(kegiatanListAdapter);

        kegiatanContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        tambahKegiatanFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kegiatanBaruIntent = new Intent(getApplicationContext(), CreateKegiatanActivity.class);
                startActivity(kegiatanBaruIntent);
            }
        });

        getData();
    }

    public void getData() {
        kegiatanContainer.setRefreshing(true);
        ApiRoute getData = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<KegiatanGetResponse> kegiatanGetResponseCall = getData.getKegiatan();
        kegiatanGetResponseCall.enqueue(new Callback<KegiatanGetResponse>() {
            @Override
            public void onResponse(Call<KegiatanGetResponse> call, Response<KegiatanGetResponse> response) {
                Log.d("duar", String.valueOf(response.code()));
                if (response.code() == 200 && response.body().getStatus().equals(true)) {
                    kegiatanArrayList.clear();
                    kegiatanArrayList.addAll(response.body().getKegiatanList());
                    kegiatanListAdapter.notifyDataSetChanged();
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                           "Gagal memuat data Kegiatan", Snackbar.LENGTH_SHORT).show();
                }
                kegiatanContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<KegiatanGetResponse> call, Throwable t) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal memuat data Kegiatan", Snackbar.LENGTH_SHORT).show();
                kegiatanContainer.setRefreshing(false);
            }
        });
    }
}