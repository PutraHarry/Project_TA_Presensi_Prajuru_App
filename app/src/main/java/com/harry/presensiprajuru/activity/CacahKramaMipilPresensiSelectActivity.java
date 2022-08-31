package com.harry.presensiprajuru.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.adapter.CacahKramaMipilAdapter;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitSikramat;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.harry.presensiprajuru.model.CacahKramaMipilAllGetResponse;
import com.harry.presensiprajuru.model.CacahKramaMipilGetResponse;
import com.harry.presensiprajuru.model.Tempekan;
import com.harry.presensiprajuru.model.TempekanGetResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CacahKramaMipilPresensiSelectActivity extends AppCompatActivity {

    private Button kramaDoneSelectButton;
    private ExtendedFloatingActionButton kramaSelectFab;

    private RecyclerView kramaList;
    private CacahKramaMipilAdapter cacahKramaMipilAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<CacahKramaMipil> cacahKramaMipilArrayList = new ArrayList<>();

    ActivityResultLauncher<Intent> startActivityIntent;

    private final Gson gson = new Gson();
    private final String KRAMA_SELECT_KEY = "KRAMA_SELECT_KEY";
    private final String TEMPEKAN_KEY = "TEMPEKAN_KEY";

    private Toolbar homeToolbar;

    private Button selectKramaByTempekanButton;

    private SharedPreferences loginPref;

    private SwipeRefreshLayout cacahKramaSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cacah_krama_mipil_presensi_select);

        loginPref = getSharedPreferences("login_pref", Context.MODE_PRIVATE);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //find semua layoutnya
        kramaDoneSelectButton = findViewById(R.id.cacah_krama_select_button);
        kramaSelectFab = findViewById(R.id.pilih_krama_fab);
        kramaList = findViewById(R.id.cacah_krama_list);
        selectKramaByTempekanButton = findViewById(R.id.cacah_krama_select_by_tempekan_button);
        cacahKramaSwipeLayout = findViewById(R.id.cacah_krama_select_swipe_layout);

        selectKramaByTempekanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectKramaByTempekanIntent = new Intent(getApplicationContext(), CacahKramaSelectByTempekanActivity.class);
                startActivityIntent.launch(selectKramaByTempekanIntent);
            }
        });

        if (getIntent().hasExtra(KRAMA_SELECT_KEY)) {
            /**
             * https://stackoverflow.com/questions/61686042/how-to-deal-with-cannot-be-cast-to-class-error-gson-java
             * https://stackoverflow.com/questions/43117731/what-is-type-typetoken
             */
            cacahKramaMipilArrayList.addAll(
                    gson.fromJson(
                            getIntent().getStringExtra(KRAMA_SELECT_KEY),
                            new TypeToken<ArrayList<CacahKramaMipil>>(){}.getType()
                    ));
        }

        /**
         *  nge create adapter untuk recycler view dari cacah krama mipil, argument pertama itu context,
         *  kedua itu array list cacah krama mipil, ketiga itu boolean yang di pake nenuin ini mode
         *  select krama atau mode liat daftar aja
         */
        cacahKramaMipilAdapter = new CacahKramaMipilAdapter(this, cacahKramaMipilArrayList, 2);
        linearLayoutManager = new LinearLayoutManager(this);
        kramaList.setLayoutManager(linearLayoutManager);
        kramaList.setAdapter(cacahKramaMipilAdapter);

        kramaSelectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * bikin intent untuk ngelaunch activity list cacah krama nya, yang di put extra itu
                 * nanti di pake activity list cacah kramanya untuk nentuin dia masuk mode select krama atau untuk mode daftar
                 */
                Intent cacahKramaSelectIntent = new Intent(getApplicationContext() , ListCacahKramaMipilActivity.class);
                cacahKramaSelectIntent.putExtra("SELECT_KRAMA", 1);
                startActivityIntent.launch(cacahKramaSelectIntent);
            }
        });

        kramaDoneSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * validasi untuk ngecek udh ada krama dipilh atau nggak,
                 * tinggal cek aja size array dari krama nya
                 */
                if (cacahKramaMipilArrayList.size() == 0) {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Tidak ada Krama dipilih.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent cacahKramaSelected = new Intent();
                    cacahKramaSelected.putExtra(KRAMA_SELECT_KEY, gson.toJson(cacahKramaMipilArrayList));
                    setResult(200, cacahKramaSelected);
                    finish();
                }
            }
        });

        startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 100) {
                            for (int i=0; i<cacahKramaMipilArrayList.size(); i++) {
                                CacahKramaMipil cacahKramaMipilCheck = gson.fromJson(result.getData().getStringExtra(KRAMA_SELECT_KEY), CacahKramaMipil.class);
                                if (cacahKramaMipilCheck.getNomorCacahKramaMipil().equals(cacahKramaMipilArrayList.get(i).getNomorCacahKramaMipil())) {
                                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                            "Krama sudah ada dalam daftar.", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            cacahKramaMipilArrayList.add(
                                    gson.fromJson(result.getData().getStringExtra(KRAMA_SELECT_KEY), CacahKramaMipil.class));
                            cacahKramaMipilAdapter.notifyDataSetChanged();
                            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                    "Krama berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
                        } else if (result.getResultCode() == 200) {
                            cacahKramaSwipeLayout.setRefreshing(true);
                            Tempekan tempekan = gson.fromJson(result.getData().getStringExtra(TEMPEKAN_KEY), Tempekan.class);
                            ApiRoute getData = RetrofitSikramat.buildRetrofit().create(ApiRoute.class);
                            Call<CacahKramaMipilAllGetResponse> cacahKramaMipilGetResponseCall = getData.getCacahMipilByTempekan(
                                    "Bearer " + loginPref.getString("token", "empty"),  String.valueOf(tempekan.getId())

                            );
                            cacahKramaMipilGetResponseCall.enqueue(new Callback<CacahKramaMipilAllGetResponse>() {
                                @Override
                                public void onResponse(Call<CacahKramaMipilAllGetResponse> call, Response<CacahKramaMipilAllGetResponse> response) {
                                    if (response.code() == 200 && response.body().getStatus().equals(true)) {
                                        cacahKramaMipilArrayList.addAll(response.body().getCacahKramaMipilList());
                                        kramaDoneSelectButton.callOnClick();
                                    }
                                    else {
                                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
                                    }
                                    cacahKramaSwipeLayout.setRefreshing(false);
                                }
                                @Override
                                public void onFailure(Call<CacahKramaMipilAllGetResponse> call, Throwable t) {
                                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else if (result.getResultCode() == 300) {
                            cacahKramaSwipeLayout.setRefreshing(true);
                            ApiRoute getData = RetrofitSikramat.buildRetrofit().create(ApiRoute.class);
                            Call<CacahKramaMipilAllGetResponse> cacahKramaMipilGetResponseCall = getData.getCacahMipilAll(
                                    "Bearer " + loginPref.getString("token", "empty")

                            );
                            cacahKramaMipilGetResponseCall.enqueue(new Callback<CacahKramaMipilAllGetResponse>() {
                                @Override
                                public void onResponse(Call<CacahKramaMipilAllGetResponse> call, Response<CacahKramaMipilAllGetResponse> response) {
                                    if (response.code() == 200 && response.body().getStatus().equals(true)) {
                                        cacahKramaMipilArrayList.addAll(response.body().getCacahKramaMipilList());
                                        kramaDoneSelectButton.callOnClick();
                                    }
                                    else {
                                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
                                    }
                                    cacahKramaSwipeLayout.setRefreshing(false);
                                }
                                @Override
                                public void onFailure(Call<CacahKramaMipilAllGetResponse> call, Throwable t) {
                                    cacahKramaSwipeLayout.setRefreshing(false);
                                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    }
}
