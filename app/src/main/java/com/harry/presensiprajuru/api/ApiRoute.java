package com.harry.presensiprajuru.api;

import com.harry.presensiprajuru.model.AuthResponse;
import com.harry.presensiprajuru.model.CacahKramaMipilAllGetResponse;
import com.harry.presensiprajuru.model.CacahKramaMipilDetailResponse;
import com.harry.presensiprajuru.model.CacahKramaMipilGetResponse;
import com.harry.presensiprajuru.model.DetailPresensiResponse;
import com.harry.presensiprajuru.model.EspResponse;
import com.harry.presensiprajuru.model.KegiatanDetailResponse;
import com.harry.presensiprajuru.model.KegiatanGetResponse;
import com.harry.presensiprajuru.model.KramaProfileGetResponse;
import com.harry.presensiprajuru.model.PresensiDetailResponse;
import com.harry.presensiprajuru.model.PresensiGetResponse;
import com.harry.presensiprajuru.model.TempekanGetResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRoute {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("krama/login")
    Call<AuthResponse> loginUser(
            @Field("email") String username,
            @Field("password") String password
    );

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("krama/get-data-krama")
    Call<KramaProfileGetResponse> getProfileKrama(
            @Field("token") String token
    );

    @Headers({"Accept: application/json"})
    @GET("kegiatan/get")
    Call<KegiatanGetResponse> getKegiatan();


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("kegiatan/create")
    Call<KegiatanDetailResponse> createKegiatan(
            @Field("nama_kegiatan") String namaKegiatan,
            @Field("keterangan") String keterangan
    );

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("kegiatan/edit")
    Call<KegiatanDetailResponse> editKegiatan(
            @Field("id_kegiatan") Integer idKegiatan,
            @Field("nama_kegiatan") String namaKegiatan,
            @Field("keterangan") String keterangan
    );


    @Headers({"Accept: application/json"})
    @GET("presensi/get-open")
    Call<PresensiGetResponse> getPresensiOpen();


    @Headers({"Accept: application/json"})
    @GET("presensi/open-presensi")
    Call<PresensiDetailResponse> openClosePresensi(
            @Query("presensi") Integer idPresensi
    );


    @Headers({"Accept: application/json"})
    @GET("presensi/get")
    Call<PresensiGetResponse> getPresensi();

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("presensi/detail")
    Call<DetailPresensiResponse> getDetailPresnesi(
            @Field("token") String token,
            @Field("presensi") Integer idPresensi
    );


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("presensi/create")
    Call<PresensiDetailResponse> createPresensi(
            @Field("kegiatan") Integer idKegiatan,
            @Field("nama_presensi") String namaPresensi,
            @Field("kode_presensi") String kodePresensi,
            @Field("keterangan") String keterangan,
            @Field("tgl_open") String tglOpen,
            @Field("tgl_close") String tglClose,
            @Field("cacah_krama_mipil_list") String cacahKramaList
    );


    // rotue integrasi ke sikramat

    @Headers({"Accept: application/json"})
    @GET("admin/banjar-adat/cacah-krama/get-mipil")
    Call<CacahKramaMipilGetResponse> getCacahKramaMipil(
            @Header("Authorization") String authHeader,
            @Query("nama") String namaKrama
    );

    @Headers({"Accept: application/json"})
    @GET("admin/banjar-adat/cacah-krama/get-mipil")
    Call<CacahKramaMipilGetResponse> getCacahKramaMipilNextPage(
            @Header("Authorization") String authHeader,
            @Query("page") int page,
            @Query("nama") String namaKrama
    );

    @Headers({"Accept: application/json"})
    @GET("admin/cacah-krama/get-mipil-detail/{id}")
    Call<CacahKramaMipilDetailResponse> getCacahKramaMipilDetail(
            @Header("Authorization") String authHeader,
            @Path("id") Integer id
    );


    @Headers({"Accept: application/json"})
    @GET("admin/banjar-adat/cacah-krama/get-tempekan-list-with-krama")
    Call<TempekanGetResponse> getTempekanWithCacahCount(
            @Header("Authorization") String authHeader
    );

    @Headers({"Accept: application/json"})
    @GET("admin/banjar-adat/cacah-krama/get-mipil-by-tempekan")
    Call<CacahKramaMipilAllGetResponse> getCacahMipilByTempekan(
            @Header("Authorization") String authHeader,
            @Query("tempekan") String tempekan
    );

    @Headers({"Accept: application/json"})
    @GET("admin/banjar-adat/cacah-krama/get-all-mipil")
    Call<CacahKramaMipilAllGetResponse> getCacahMipilAll(
            @Header("Authorization") String authHeader
    );


    // esp

    @Headers({"Accept: application/json"})
    @GET("test/")
    Call<EspResponse> espTest();

    @Headers({"Accept: application/json"})
    @POST("/write/")
    @FormUrlEncoded
    Call<EspResponse> tulisData (
            @Field("data-krama") String oke
    );
}