package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.AuthResponse;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailField, passwordField;
    private Button loginButton;
    private SharedPreferences loginPref;
    private ProgressDialog dialog;
    String roleArr[] = {"prajuru_banjar", "kelihan_adat", "penyarikan_banjar", "patengen_banjar", "pangliman_banjar"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Mohon tunggu...");

        emailField = findViewById(R.id.login_email_field);
        passwordField = findViewById(R.id.login_password_field);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });

        loginPref = getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        if (loginPref.getInt("status", 0) == 1) {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);
            finish();
        }
    }

    public void submitData() {
        dialog.show();
        ApiRoute apiRoute = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<AuthResponse> authResponseCall = apiRoute.loginUser(
                emailField.getText().toString(),
                passwordField.getText().toString()
        );

        authResponseCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }

                if (response.code() == 200 && response.body().getStatusCode() == 200
                        && response.body().getMessage().equals("Login berhasil")
                        && Arrays.asList(roleArr).contains(response.body().getData())) {

                    SharedPreferences.Editor loginPrefEditor = loginPref.edit();
                    loginPrefEditor.putInt("status", 1);
                    loginPrefEditor.putString("token", response.body().getToken());
                    loginPrefEditor.apply();

                    Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(home);
                    finish();

                } else if (response.code() == 200 && response.body().getStatusCode() == 403
                        && response.body().getMessage().equals("email not verified")) {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Email belum terverifikasi. Silahkan melakukan verifikasi email melalui SIKRAMAT.", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal melakukan login. Silahkan coba lagi nanti atau periksa email dan password.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal melakukan login. Silahkan coba lagi nanti atau periksa email dan password.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}