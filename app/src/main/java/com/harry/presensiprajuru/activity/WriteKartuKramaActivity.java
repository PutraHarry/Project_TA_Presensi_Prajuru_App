package com.harry.presensiprajuru.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.harry.presensiprajuru.model.EspResponse;
import com.squareup.picasso.Picasso;

import net.posick.mdns.Lookup;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteKartuKramaActivity extends AppCompatActivity {

    private Gson gson = new Gson();
    private final String KRAMA_KEY = "KRAMA_KEY";
    private CacahKramaMipil cacahKramaMipil;

    private ImageView kramaImage;
    private LinearLayout kramaImageLoadingLayout;
    private TextView kramaName, kramaNic, ipAlatText;
    private Button kramaWriteKartu;

    private TextInputEditText ipNodeMcuField;

    private Toolbar homeToolbar;

    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_kartu_krama);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cacahKramaMipil = gson.fromJson(getIntent().getStringExtra(KRAMA_KEY), CacahKramaMipil.class);

        new GetIp().execute();

        kramaImage = findViewById(R.id.profile_image);
        kramaImageLoadingLayout = findViewById(R.id.profile_image_loading_container);
        kramaName = findViewById(R.id.detail_krama_nama_text);
        kramaNic = findViewById(R.id.detail_krama_nic_text);

        ipAlatText = findViewById(R.id.krama_write_ip_text);

        ipNodeMcuField = findViewById(R.id.write_kartu_ip_field);
        ipNodeMcuField.setVisibility(View.GONE);

        kramaWriteKartu = findViewById(R.id.krama_write_button);
        kramaWriteKartu.setVisibility(View.GONE);
//        kramaWriteKartu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                testNodeMcu(ipNodeMcuField.getText().toString());
//            }
//        });

        kramaName.setText(cacahKramaMipil.getPenduduk().getNama());
        kramaNic.setText(cacahKramaMipil.getNomorCacahKramaMipil());

        if (cacahKramaMipil.getPenduduk().getFoto() != null) {
            Picasso.get()
                    .load(getResources().getString(R.string.sikramat_endpoint) +
                            cacahKramaMipil.getPenduduk().getFoto())
                    .into(kramaImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            kramaImageLoadingLayout.setVisibility(View.GONE);
                            kramaImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(R.drawable.logo).into(kramaImage);
                            kramaImage.setVisibility(View.VISIBLE);
                            kramaImageLoadingLayout.setVisibility(View.GONE);
                        }
                    });
        }
        else {
            Picasso.get().load(R.drawable.logo).into(kramaImage);
            kramaImage.setVisibility(View.VISIBLE);
            kramaImageLoadingLayout.setVisibility(View.GONE);
        }
    }


    public retrofit2.Retrofit buildRetro (String ip) {
        String baseURL = ip;
        retrofit2.Retrofit retrofit = null;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    public void tulisData(String ip) {
        kramaWriteKartu.setVisibility(View.GONE);
        ApiRoute submitData = buildRetro("http://" + ip).create(ApiRoute.class);
        Call<EspResponse> testResponseCall = submitData.tulisData(
                "#" + cacahKramaMipil.getNomorCacahKramaMipil() +
                        "#" + cacahKramaMipil.getPenduduk().getNik() +
                        "#" + cacahKramaMipil.getPenduduk().getNama() +
                        "#" + cacahKramaMipil.getPenduduk().getJenisKelamin() +
                        "#" + cacahKramaMipil.getPenduduk().getTempatLahir() +
                        "#" + cacahKramaMipil.getPenduduk().getTanggalLahir() +
                        "#" + cacahKramaMipil.getPenduduk().getAlamat()
        );
        testResponseCall.enqueue(new Callback<EspResponse>() {
            @Override
            public void onResponse(Call<EspResponse> call, Response<EspResponse> response) {
                if (response.code() == 200 && response.body().getName().equals("sukses write")){
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Sukses menulis kartu", Snackbar.LENGTH_LONG).show();
                }  else if (response.code() == 200 && response.body().getName().equals("no kartu")) {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Tidak ada kartu ditemukan", Snackbar.LENGTH_LONG).show();
                }
                kramaWriteKartu.setVisibility(View.VISIBLE);
                kramaWriteKartu.setText("Tulis Kartu");
//                kramaWriteKartu.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        testNodeMcu(ip);
//                    }
//                });
            }

            @Override
            public void onFailure(Call<EspResponse> call, Throwable t) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal melakukan koneksi", Snackbar.LENGTH_LONG).show();
                kramaWriteKartu.setVisibility(View.VISIBLE);
            }
        });
    }

    public void testNodeMcu(String ip) {
        kramaWriteKartu.setVisibility(View.GONE);
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Mohon tunggu", Snackbar.LENGTH_LONG).show();
        ApiRoute submitData = buildRetro("http://" + ip).create(ApiRoute.class);
        Call<EspResponse> testResponseCall = submitData.espTest();
        testResponseCall.enqueue(new Callback<EspResponse>() {
            @Override
            public void onResponse(Call<EspResponse> call, Response<EspResponse> response) {
                if (response.code() == 200 ){
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Sukses terkoneksi dengan NodeMCU", Snackbar.LENGTH_LONG).show();
                    kramaWriteKartu.setText("Tulis Kartu");
                    kramaWriteKartu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tulisData(ip);
                        }
                    });
                }  else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "gagal", Snackbar.LENGTH_LONG).show();
                }
                kramaWriteKartu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<EspResponse> call, Throwable t) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "gagal terkoneksi", Snackbar.LENGTH_LONG).show();
                kramaWriteKartu.setVisibility(View.VISIBLE);
            }
        });
    }

    class GetIp extends AsyncTask<String, Void, ArrayList<Record>> {
        // https://stackoverflow.com/questions/64713877/how-to-resolve-ipv4-and-ipv6-from-local-using-mdns-in-android
        protected ArrayList<Record> doInBackground(String... urls) {
            try {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiManager.MulticastLock multicastLoc = wifiManager.createMulticastLock("mDnsLock");
                multicastLoc.setReferenceCounted(true);
                multicastLoc.acquire();
                ArrayList<Record> records = new ArrayList<>();
                records.addAll(Arrays.asList(new Lookup("esp8266", Type.A, DClass.IN).lookupRecords()));
                Log.d("masuk sini", String.valueOf(records.size()));
                multicastLoc.release();
                return records;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("masuk sini", "masuk sini 2");
                return null;
            }
        }
        protected void onPostExecute(ArrayList<Record> record) {
            Log.d("masuk sini", String.valueOf(record.size()));
            for (Record value : record) {
                if (value.getType() == Type.A) {
                    ((ARecord) value).getAddress().getHostAddress();
                    ip = ((ARecord) value).getAddress().getHostAddress();
                    Log.d("masuk sini", ip);
                    ipAlatText.setText("IP: " + ip);
                    kramaWriteKartu.setVisibility(View.VISIBLE);
                    kramaWriteKartu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tulisData(ip);
                        }
                    });
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Berhasil terkoneksi dengan Alat", Snackbar.LENGTH_LONG).show();
                } else {
                    Log.d("masuk sini", "duar");
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Gagal melakukan koneksi dengan Alat", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}