package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.adapter.CacahKramaMipilAdapter;
import com.harry.presensiprajuru.adapter.TempekanListAdapter;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitSikramat;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.harry.presensiprajuru.model.CacahKramaMipilGetResponse;
import com.harry.presensiprajuru.model.Tempekan;
import com.harry.presensiprajuru.model.TempekanGetResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CacahKramaSelectByTempekanActivity extends AppCompatActivity {

    RecyclerView tempekanList;
    ArrayList<Tempekan> tempekanArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    TempekanListAdapter tempekanListAdapter;
    SwipeRefreshLayout tempekanContainer;

    private SharedPreferences loginPref;

    private Toolbar homeToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cacah_krama_select_by_tempekan);
        loginPref = getSharedPreferences("login_pref", Context.MODE_PRIVATE);

        homeToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tempekanContainer = findViewById(R.id.tempekan_container);

        tempekanList = findViewById(R.id.tempekan_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        tempekanListAdapter = new TempekanListAdapter(this, tempekanArrayList);
        tempekanList.setLayoutManager(linearLayoutManager);
        tempekanList.setAdapter(tempekanListAdapter);


        getData(loginPref.getString("token", "empty"));
    }

    public void getData(String token) {
        tempekanContainer.setRefreshing(true);
        ApiRoute getData = RetrofitSikramat.buildRetrofit().create(ApiRoute.class);
        Call<TempekanGetResponse> tempekanGetResponseCall = getData.getTempekanWithCacahCount(
                "Bearer " + token
        );
        tempekanGetResponseCall.enqueue(new Callback<TempekanGetResponse>() {
            @Override
            public void onResponse(Call<TempekanGetResponse> call, Response<TempekanGetResponse> response) {
                if (response.code() == 200 && response.body().getStatus().equals(true)) {
                    tempekanArrayList.clear();
                    tempekanArrayList.addAll(response.body().getData());
                    tempekanListAdapter.notifyDataSetChanged();
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
                }
                tempekanContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<TempekanGetResponse> call, Throwable t) {
                tempekanContainer.setRefreshing(false);
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}