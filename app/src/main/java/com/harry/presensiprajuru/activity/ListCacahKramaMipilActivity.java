package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.adapter.CacahKramaMipilAdapter;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitSikramat;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.harry.presensiprajuru.model.CacahKramaMipilGetResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCacahKramaMipilActivity extends AppCompatActivity {

    RecyclerView kramaMipilList;
    ArrayList<CacahKramaMipil> cacahKramaMipilArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    CacahKramaMipilAdapter cacahKramaMipilAdapter;
    SwipeRefreshLayout kramaContainer;
    NestedScrollView kramaNestedScroll;

    private SharedPreferences loginPref;

    private Toolbar homeToolbar;
    TextView kramaAllDataLoadedTextView;
    int currentPage;
    int nextPage;
    int lastPage;

    TextInputEditText searchKramaField;
    TextView noDataFoundText;

    private int selectKramaMode = 0;

    private Button selectAllCacahKramaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cacah_krama_mipil);

        loginPref = getSharedPreferences("login_pref", Context.MODE_PRIVATE);

        selectAllCacahKramaButton = findViewById(R.id.cacah_krama_select_all_button);

        homeToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (getIntent().hasExtra("SELECT_KRAMA")) {
            selectKramaMode = 1;
            selectAllCacahKramaButton.setVisibility(View.VISIBLE);
        }

        kramaContainer = findViewById(R.id.cacah_krama_container);
        kramaNestedScroll = findViewById(R.id.cacah_krama_nested_scroll);
        kramaAllDataLoadedTextView = findViewById(R.id.all_data_loaded_cacah_krama_text);
        noDataFoundText = findViewById(R.id.no_data_found_krama_text);

        selectAllCacahKramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(300);
                finish();
            }
        });


        kramaMipilList = findViewById(R.id.cacah_krama_recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        cacahKramaMipilAdapter = new CacahKramaMipilAdapter(this, cacahKramaMipilArrayList, selectKramaMode);
        kramaMipilList.setLayoutManager(linearLayoutManager);
        kramaMipilList.setAdapter(cacahKramaMipilAdapter);

        searchKramaField = findViewById(R.id.cacah_krama_search_field);
        searchKramaField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    getData(loginPref.getString("token", "empty"), searchKramaField.getText().toString());
                    return true;
                }
                return false;
            }
        });

        kramaContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(loginPref.getString("token", "empty"), searchKramaField.getText().toString());
            }
        });

        getData(loginPref.getString("token", "empty"), searchKramaField.getText().toString());
    }

    public void getData(String token, String nama) {
        kramaContainer.setRefreshing(true);
        CloseKeyboard closeKeyboard = new CloseKeyboard();
        closeKeyboard.CloseKeyboard(getCurrentFocus(), getApplicationContext());
        ApiRoute getData = RetrofitSikramat.buildRetrofit().create(ApiRoute.class);
        Call<CacahKramaMipilGetResponse> cacahKramaMipilGetResponseCall = getData.getCacahKramaMipil(
                "Bearer " + token,
                nama
        );
        cacahKramaMipilGetResponseCall.enqueue(new Callback<CacahKramaMipilGetResponse>() {
            @Override
            public void onResponse(Call<CacahKramaMipilGetResponse> call, Response<CacahKramaMipilGetResponse> response) {
                if (response.code() == 200 && response.body().getStatus().equals(true)) {
                    noDataFoundText.setVisibility(View.GONE);
                    cacahKramaMipilArrayList.clear();
                    cacahKramaMipilArrayList.addAll(response.body().getCacahKramaMipilPaginate().getCacahKramaMipilList());
                    cacahKramaMipilAdapter.notifyDataSetChanged();

                    selectAllCacahKramaButton.setText(String.format(
                            "Pilih semua krama (%d)",
                            response.body().getCacahKramaMipilPaginate().getTotal())
                    );
                    currentPage = response.body().getCacahKramaMipilPaginate().getCurrentPage();
                    lastPage = response.body().getCacahKramaMipilPaginate().getLastPage();
                    if (currentPage != lastPage) {
                        nextPage = currentPage+1;
                        kramaNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                            @Override
                            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                if (!kramaNestedScroll.canScrollVertically(1)) {
                                    getNextData(token, nextPage, nama);
                                }
                            }
                        });
                    } else if (cacahKramaMipilArrayList.isEmpty()) {
                        noDataFoundText.setVisibility(View.VISIBLE);
                        kramaAllDataLoadedTextView.setVisibility(View.GONE);
                    }
                    else {
                        noDataFoundText.setVisibility(View.GONE);
                        kramaAllDataLoadedTextView.setVisibility(View.VISIBLE);
                        kramaNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                            @Override
                            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                            }
                        });
                    }
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
                }
                kramaContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<CacahKramaMipilGetResponse> call, Throwable t) {
                kramaContainer.setRefreshing(false);
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void getNextData(String token, int page, String nama) {
        kramaContainer.setRefreshing(true);
        ApiRoute getData = RetrofitSikramat.buildRetrofit().create(ApiRoute.class);
        Call<CacahKramaMipilGetResponse> cacahKramaMipilGetResponseCall = getData.getCacahKramaMipilNextPage(
                "Bearer " + token,
                page,
                nama
        );
        cacahKramaMipilGetResponseCall.enqueue(new Callback<CacahKramaMipilGetResponse>() {
            @Override
            public void onResponse(Call<CacahKramaMipilGetResponse> call, Response<CacahKramaMipilGetResponse> response) {
                if (response.code() == 200 && response.body().getStatus().equals(true)) {
                    cacahKramaMipilArrayList.addAll(response.body().getCacahKramaMipilPaginate().getCacahKramaMipilList());
                    currentPage = response.body().getCacahKramaMipilPaginate().getCurrentPage();
                    cacahKramaMipilAdapter.notifyItemInserted(cacahKramaMipilArrayList.size()-1);
                    if (currentPage != lastPage) {
                        nextPage++;
                    }
                    else {
                        kramaAllDataLoadedTextView.setVisibility(View.VISIBLE);
                        kramaNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                            @Override
                            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                            }
                        });
                    }
                    kramaContainer.setRefreshing(false);
                }
                else {
                    kramaContainer.setRefreshing(false);
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CacahKramaMipilGetResponse> call, Throwable t) {
                kramaContainer.setRefreshing(false);
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal memuat data", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public class CloseKeyboard {
        public void CloseKeyboard(View view, Context context) {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}