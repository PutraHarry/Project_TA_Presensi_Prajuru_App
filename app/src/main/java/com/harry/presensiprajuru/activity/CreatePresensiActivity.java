package com.harry.presensiprajuru.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.harry.presensiprajuru.R;
import com.harry.presensiprajuru.api.ApiRoute;
import com.harry.presensiprajuru.api.RetrofitClient;
import com.harry.presensiprajuru.model.CacahKramaMipil;
import com.harry.presensiprajuru.model.Kegiatan;
import com.harry.presensiprajuru.model.KegiatanDetailResponse;
import com.harry.presensiprajuru.model.PresensiDetailResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePresensiActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> startActivityIntent;
    private final String KEGIATAN_KEY = "KEGIATAN_KEY";
    private final String KRAMA_SELECT_KEY = "KRAMA_SELECT_KEY";

    private Button kegiatanSelectButton, presensiSubmitButton, kramaSelectButton;
    private TextInputEditText presensiKeterangan, presensiDateOpen,
            presensiDateClose, presensiName, presensiKode, presensiTimeOpen, presensiTimeClose;
    private TextView kegiatanName, kegiatanDesc, kramaSelectedText;
    private MaterialCardView kegiatanCard;

    private Kegiatan kegiatanPresensi;

    private Gson gson = new Gson();

    private ArrayList<CacahKramaMipil> cacahKramaMipilArrayList = new ArrayList<>();

    private ProgressDialog dialog;
    private Toolbar homeToolbar;

    @TimeFormat
    private int clockFormat;
    private SimpleDateFormat timeFormatter;

    MaterialTimePicker.Builder timePickerBuilder;
    MaterialTimePicker presensiOpenTimePicker, presensiCloseTimePicker;

    private Date presensiOpenTime, presensiCloseTime;

    private Boolean checkKrama = false, checkKegiatan = false, checkPresensiNama = false,
            checkWaktuPresensi = false, checkKodePresensi = false, checkPresensiKeterangan = false;

    SimpleDateFormat dateDiffFormat;
    Date dateOpen, dateClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_presensi);

        homeToolbar = findViewById(R.id.home_toolbar);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        dialog = new ProgressDialog(this);
        dialog.setMessage("Mohon tunggu...");

        dateDiffFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        kegiatanSelectButton = findViewById(R.id.presensi_new_kegiatan_select_button);
        kegiatanCard = findViewById(R.id.presensi_new_kegiatan_card);
        kegiatanName = findViewById(R.id.presensi_new_kegiatan_nama_text);
        kegiatanDesc = findViewById(R.id.presensi_new_kegiatan_deskripsi_text);

        presensiSubmitButton = findViewById(R.id.presensi_new_simpan_button);
        presensiKeterangan = findViewById(R.id.presensi_new_keterangan_field);
        presensiDateOpen = findViewById(R.id.presensi_new_tanggal_open_field);
        presensiDateClose = findViewById(R.id.presensi_new_tanggal_close_field);
        presensiTimeOpen = findViewById(R.id.presensi_new_time_open_field);
        presensiTimeClose = findViewById(R.id.presensi_new_time_close_field);
        presensiName = findViewById(R.id.presensi_new_name_field);
        presensiKode = findViewById(R.id.presensi_new_code_field);

        presensiName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (presensiName.getText().toString().length() > 0) {
                    checkPresensiNama = true;
                }
                else {
                    checkPresensiNama = false;
                }
            }
        });

        presensiKode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (presensiKode.getText().toString().length() > 0) {
                    checkKodePresensi = true;
                }
                else {
                    checkPresensiNama = false;
                }
            }
        });

        presensiKeterangan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (presensiKeterangan.getText().toString().length() > 0) {
                    checkPresensiKeterangan = true;
                }
                else {
                    checkPresensiKeterangan = false;
                }
            }
        });

        kramaSelectedText = findViewById(R.id.krama_selected_text);
        kramaSelectButton = findViewById(R.id.presensi_new_krama_select);
        kramaSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kramaSelectIntent = new Intent(getApplicationContext() , CacahKramaMipilPresensiSelectActivity.class);
                if (!cacahKramaMipilArrayList.isEmpty()) {
                    kramaSelectIntent.putExtra(KRAMA_SELECT_KEY, gson.toJson(cacahKramaMipilArrayList));
                }
                startActivityIntent.launch(kramaSelectIntent);
            }
        });

        kegiatanSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent presensiKegiatanSelect = new Intent(getApplicationContext() , ListKegiatanActivity.class);
                presensiKegiatanSelect.putExtra("SELECT_KEGIATAN", 1);
                startActivityIntent.launch(presensiKegiatanSelect);
            }
        });

        startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 100) {
                            kegiatanPresensi = gson.fromJson(result.getData().getStringExtra(KEGIATAN_KEY), Kegiatan.class);
                            kegiatanName.setText(kegiatanPresensi.getNamaKegiatan());
                            kegiatanDesc.setText(kegiatanPresensi.getKeterangan());
                            kegiatanCard.setVisibility(View.VISIBLE);
                            kegiatanSelectButton.setText("Ganti Kegiatan");
                            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                    "Berhasil memilih kegiatan.", Snackbar.LENGTH_SHORT).show();
                            checkKegiatan = true;
                        } else if (result.getResultCode() == 200) {
                            cacahKramaMipilArrayList.clear();
                            cacahKramaMipilArrayList.addAll(gson.fromJson(
                                    result.getData().getStringExtra(KRAMA_SELECT_KEY),
                                    new TypeToken<ArrayList<CacahKramaMipil>>(){}.getType()
                            ));
                            if (cacahKramaMipilArrayList.size() != 0) {
                                checkKrama = true;
                            } else {
                                checkKrama = false;
                            }
                            kramaSelectButton.setText("Lihat Krama yang Dipilih");
                            kramaSelectedText.setText(String.valueOf(cacahKramaMipilArrayList.size()) + " Krama dipilih");
                        }
                    }
                });

        MaterialDatePicker.Builder<Long> datePickerPresensiOpenBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerPresensiOpenBuilder.setTitleText("Pilih tanggal buka Presensi");
        final MaterialDatePicker<Long> datePickerPresensiOpen = datePickerPresensiOpenBuilder.build();
        datePickerPresensiOpen.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selectedDate) {
                TimeZone timeZoneUTC = TimeZone.getDefault();
                int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                SimpleDateFormat simpleFormat = new SimpleDateFormat("EEE, dd-MM-yyyy", Locale.US);
                Date date = new Date(selectedDate + offsetFromUTC);
                presensiDateOpen.setText(simpleFormat.format(date));
            }
        });

        presensiDateOpen.setShowSoftInputOnFocus(false);
        presensiDateOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(datePickerPresensiOpen.isVisible())) {
                    datePickerPresensiOpen.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                }
            }
        });

        presensiDateOpen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    if (!(datePickerPresensiOpen.isVisible())) {
                        datePickerPresensiOpen.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                }
            }
        });


        MaterialDatePicker.Builder<Long> datePickerPresensiCloseBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerPresensiOpenBuilder.setTitleText("Pilih tanggal tutup Presensi");
        final MaterialDatePicker<Long> datePickerPresensiClose = datePickerPresensiCloseBuilder.build();
        datePickerPresensiClose.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selectedDate) {
                TimeZone timeZoneUTC = TimeZone.getDefault();
                int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                SimpleDateFormat simpleFormat = new SimpleDateFormat("EEE, dd-MM-yyyy", Locale.US);
                Date date = new Date(selectedDate + offsetFromUTC);
                presensiDateClose.setText(simpleFormat.format(date));
            }
        });

        presensiDateClose.setShowSoftInputOnFocus(false);
        presensiDateClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(datePickerPresensiClose.isVisible())) {
                    datePickerPresensiClose.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                }
            }
        });

        presensiDateClose.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    if (!(datePickerPresensiClose.isVisible())) {
                        datePickerPresensiClose.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                }
            }
        });

        // untuk nge format waktunya biar jadi 14:50 misalnya
        clockFormat = TimeFormat.CLOCK_24H;
        Calendar cal = Calendar.getInstance();
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        timePickerBuilder = new MaterialTimePicker.Builder();
        timePickerBuilder.setTimeFormat(clockFormat);
        timePickerBuilder.setTitleText("Pilih waktu");

        presensiOpenTimePicker = timePickerBuilder.build();
        presensiOpenTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.set(Calendar.HOUR_OF_DAY, presensiOpenTimePicker.getHour());
                cal.set(Calendar.MINUTE,presensiOpenTimePicker.getMinute());
                cal.set(Calendar.SECOND,0);
                presensiOpenTime = cal.getTime();
                presensiTimeOpen.setText(timeFormatter.format(presensiOpenTime));
            }
        });

        presensiTimeOpen.setShowSoftInputOnFocus(false);
        presensiTimeOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(presensiOpenTimePicker.isVisible())) {
                    presensiOpenTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER_1");
                }
            }
        });

        presensiTimeOpen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    if (!(presensiOpenTimePicker.isVisible())) {
                        presensiOpenTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER_1");
                    }
                }
            }
        });

        presensiCloseTimePicker = timePickerBuilder.build();
        presensiCloseTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.set(Calendar.HOUR_OF_DAY, presensiCloseTimePicker.getHour());
                cal.set(Calendar.MINUTE,presensiCloseTimePicker.getMinute());
                cal.set(Calendar.SECOND,0);
                presensiCloseTime = cal.getTime();
                presensiTimeClose.setText(timeFormatter.format(presensiCloseTime));
            }
        });

        presensiTimeClose.setShowSoftInputOnFocus(false);
        presensiTimeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(presensiCloseTimePicker.isVisible())) {
                    presensiCloseTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER_1");
                }
            }
        });

        presensiTimeClose.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    if (!(presensiCloseTimePicker.isVisible())) {
                        presensiCloseTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER_1");
                    }
                }
            }
        });

        presensiSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presensiDateOpen.getText().toString().length() == 0 || presensiTimeOpen.getText().toString().length() == 0
                        || presensiDateClose.getText().toString().length() == 0 || presensiTimeClose.getText().toString().length() == 0) {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Tanggal buka dan tutup presensi tidak valid", Snackbar.LENGTH_SHORT).show();
                } else {
                    try {
                        dateOpen = dateDiffFormat.parse(changeDateFormatForForm(presensiDateOpen.getText().toString()) + " " + presensiTimeOpen.getText().toString());
                        dateClose = dateDiffFormat.parse(changeDateFormatForForm(presensiDateClose.getText().toString()) + " " + presensiTimeClose.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long dateDifferent = dateClose.getTime() - dateOpen.getTime();
                    if (dateDifferent < 0) {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Tanggal buka dan tutup presensi tidak valid", Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (checkKegiatan && checkKrama && checkPresensiNama && checkKodePresensi && checkPresensiKeterangan) {
                            submitData();
                        } else {
                            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                    "Terdapat data yang kosong. Silahkan periksa kembali.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }



    public void submitData() {
        dialog.show();
        ApiRoute apiRoute = RetrofitClient.buildRetrofit().create(ApiRoute.class);
        Call<PresensiDetailResponse> presensiDetailResponseCall = apiRoute.createPresensi(
                kegiatanPresensi.getId(),
                presensiName.getText().toString(),
                presensiKode.getText().toString(),
                presensiKeterangan.getText().toString(),
                changeDateFormatForForm(presensiDateOpen.getText().toString()) + " " + presensiTimeOpen.getText().toString(),
                changeDateFormatForForm(presensiDateClose.getText().toString()) + " " + presensiTimeClose.getText().toString(),
                gson.toJson(cacahKramaMipilArrayList)
        );

        presensiDetailResponseCall.enqueue(new Callback<PresensiDetailResponse>() {
            @Override
            public void onResponse(Call<PresensiDetailResponse> call, Response<PresensiDetailResponse> response) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }

                if (response.code() == 200 && response.body().getStatusCode() == 200
                        && response.body().getMessage().equals("data presensi sukses")) {
                    Toast.makeText(getApplicationContext(), "Berhasil menambahkan Presensi!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                            "Gagal menambahkan data Presensi. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PresensiDetailResponse> call, Throwable t) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Gagal menambahkan data Presensi. Silahkan coba lagi nanti.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }



    public static String changeDateFormatForForm(String time) {
        String inputPattern = "EEE, dd-MM-yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat input = new SimpleDateFormat(inputPattern);
        SimpleDateFormat output = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = input.parse(time);
            str = output.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}