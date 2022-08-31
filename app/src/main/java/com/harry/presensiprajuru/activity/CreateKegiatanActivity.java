package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.AuthResponse;
import com.harry.presensiprajuru.model.Kegiatan;
import com.harry.presensiprajuru.model.KegiatanDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateKegiatanActivity extends AppCompatActivity {

    private TextInputEditText kegiatanNameField, kegiatanKeteranganField;
    private Button kegiatanSubmitButton;
    private ProgressDialog dialog;

    private Gson gson = new Gson();
    private final String KEGIATAN_KEY = "KEGIATAN_KEY";
    private Kegiatan kegiatanEdit;
    private Toolbar  homeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kegiatan);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        dialog = new ProgressDialog(this);
        dialog.setMessage("Mohon tunggu...");

        kegiatanEdit = gson.fromJson(getIntent().getStringExtra(KEGIATAN_KEY), Kegiatan.class);

        kegiatanNameField = findViewById(R.id.kegiatan_new_name_field);
        kegiatanKeteranganField = findViewById(R.id.kegiatan_new_keterangan_field);
        kegiatanSubmitButton = findViewById(R.id.kegiatan_submit_button);

        if (kegiatanEdit != null) {
            kegiatanNameField.setText(kegiatanEdit.getNamaKegiatan());
            kegiatanKeteranganField.setText(kegiatanEdit.getKeterangan());
            kegiatanSubmitButton.setText("Edit Kegiatan");
            homeToolbar.setTitle("Detail Kegiatan");
        }

        kegiatanSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kegiatanEdit != null) {
                    if (kegiatanNameField.getText().toString().length() == 0 || kegiatanKeteranganField.getText().toString().length() == 0) {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Terdapat data yang kosong. Silahkan periksa kembali", Snackbar.LENGTH_SHORT).show();
                    } else {
                        updateKegiatan();
                    }
                } else {
                    if (kegiatanNameField.getText().toString().length() == 0 || kegiatanKeteranganField.getText().toString().length() == 0) {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Terdapat data yang kosong. Silahkan periksa kembali.", Snackbar.LENGTH_SHORT).show();
                    } else {
                        createKegitan();
                    }
                }
            }
        });
    }


    public void createKegitan() {
        dialog.show();
        ApiRoute apiRoute = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<KegiatanDetailResponse> kegiatanDetailResponseCall = apiRoute.createKegiatan(
                kegiatanNameField.getText().toString(),
                kegiatanKeteranganField.getText().toString()
        );

        kegiatanDetailResponseCall.enqueue(new Callback<KegiatanDetailResponse>() {
            @Override
            public void onResponse(Call<KegiatanDetailResponse> call, Response<KegiatanDetailResponse> response) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }

                if (response.code() == 200 && response.body().getStatusCode() == 200
                        && response.body().getMessage().equals("data kegiatan sukses")) {
                    Toast.makeText(getApplicationContext(), "Berhasil menambahkan kegiatan!", Toast.LENGTH_SHORT).show();
                    finish();

                } else if (response.code() == 200 && response.body().getStatusCode() == 200
                        && response.body().getMessage().equals("data kegiatan exist")) {
                    Toast.makeText(getApplicationContext(), "Data Kegiatan sudah terdaftar!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal menambahkan kegiatan. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KegiatanDetailResponse> call, Throwable t) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal menambahkan kegiatan. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void updateKegiatan() {
        dialog.show();
        ApiRoute apiRoute = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<KegiatanDetailResponse> kegiatanDetailResponseCall = apiRoute.editKegiatan(
                kegiatanEdit.getId(),
                kegiatanNameField.getText().toString(),
                kegiatanKeteranganField.getText().toString()
        );

        kegiatanDetailResponseCall.enqueue(new Callback<KegiatanDetailResponse>() {
            @Override
            public void onResponse(Call<KegiatanDetailResponse> call, Response<KegiatanDetailResponse> response) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }

                if (response.code() == 200 && response.body().getStatusCode() == 200
                        && response.body().getMessage().equals("data kegiatan sukses")) {
                    Toast.makeText(getApplicationContext(), "Berhasil memperbaharui kegiatan!", Toast.LENGTH_SHORT).show();
                    finish();

                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal memperbaharui data kegiatan. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KegiatanDetailResponse> call, Throwable t) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal memperbaharui data kegiatan. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}