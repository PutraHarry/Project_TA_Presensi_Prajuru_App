package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.api.RetrofitSikramat;
import com.harry.presensiprajuru.model.CacahKramaMipilDetailResponse;
import com.harry.presensiprajuru.model.EspResponse;
import com.harry.presensiprajuru.model.KramaProfileGetResponse;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailKramaActivity extends AppCompatActivity {

    private ImageView kramaImage;
    private LinearLayout kramaImageLoadingLayout;
    private TextView kramaName, kramaNic, kramaNik, kramaTempekan;
    private Button kramaWriteKartu;

    private SharedPreferences loginPref;

    private SwipeRefreshLayout kramaContainer;

    private final String KRAMA_KEY = "KRAMA_KEY";

    private Gson gson = new Gson();

    private Toolbar homeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_krama);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loginPref = getSharedPreferences("login_pref", Context.MODE_PRIVATE);

        kramaContainer = findViewById(R.id.main_container);

        kramaImage = findViewById(R.id.profile_image);
        kramaImageLoadingLayout = findViewById(R.id.profile_image_loading_container);
        kramaName = findViewById(R.id.detail_krama_nama_text);
        kramaNic = findViewById(R.id.detail_krama_nic_text);
        kramaNik = findViewById(R.id.detail_krama_nik_text);
        kramaTempekan = findViewById(R.id.detail_krama_tempekan_text);
        kramaWriteKartu = findViewById(R.id.detail_krama_write_kartu_button);

        kramaContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(getIntent().getIntExtra(KRAMA_KEY, -1), loginPref.getString("token", "empty"));
            }
        });

        if (getIntent().hasExtra("DETAIL_PROFILE")) {
            kramaWriteKartu.setVisibility(View.GONE);
            homeToolbar.setTitle("Detail Profile");
        }

        getData(getIntent().getIntExtra(KRAMA_KEY, -1), loginPref.getString("token", "empty"));
    }

    public void getData(Integer id, String token) {
        kramaContainer.setRefreshing(true);
        ApiRoute apiRoute = RetrofitSikramat.buildRetrofit().create(ApiRoute.class);
        Call<CacahKramaMipilDetailResponse> cacahKramaMipilDetailResponseCall = apiRoute.getCacahKramaMipilDetail("Bearer " + token, id);
        cacahKramaMipilDetailResponseCall.enqueue(new Callback<CacahKramaMipilDetailResponse>() {
            @Override
            public void onResponse(Call<CacahKramaMipilDetailResponse> call, Response<CacahKramaMipilDetailResponse> response) {
                kramaContainer.setRefreshing(false);
                if (response.code() == 200 && response.body().getStatusCode() == 200
                        && response.body().getMessage().equals("data cacah krama mipil sukses")) {

                    kramaName.setText(response.body().getCacahKramaMipil().getPenduduk().getNama());
                    kramaNic.setText(response.body().getCacahKramaMipil().getNomorCacahKramaMipil());
                    kramaTempekan.setText(response.body().getCacahKramaMipil().getTempekan().getNamaTempekan());
                    kramaNik.setText(response.body().getCacahKramaMipil().getPenduduk().getNik());

                    kramaWriteKartu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent kramaWriteIntent = new Intent(getApplicationContext(), WriteKartuKramaActivity.class);
                            kramaWriteIntent.putExtra(KRAMA_KEY, gson.toJson(response.body().getCacahKramaMipil()));
                            startActivity(kramaWriteIntent);
                        }
                    });

                    if (response.body().getCacahKramaMipil().getPenduduk().getFoto() != null) {
                        Picasso.get()
                                .load(getResources().getString(R.string.sikramat_endpoint) +
                                        response.body().getCacahKramaMipil().getPenduduk().getFoto())
                                .into(kramaImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        kramaImageLoadingLayout.setVisibility(View.GONE);
                                        kramaImage.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(R.drawable.profile_placeholder).into(kramaImage);
                                        kramaImage.setVisibility(View.VISIBLE);
                                        kramaImageLoadingLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                    else {
                        Picasso.get().load(R.drawable.profile_placeholder).into(kramaImage);
                        kramaImage.setVisibility(View.VISIBLE);
                        kramaImageLoadingLayout.setVisibility(View.GONE);
                    }

                } else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal memuat data Krama. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CacahKramaMipilDetailResponse> call, Throwable t) {
                kramaContainer.setRefreshing(false);
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal memuat data Krama. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}