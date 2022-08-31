package com.harry.presensiprajuru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacahKramaMipil {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nomor_cacah_krama_mipil")
    @Expose
    private String nomorCacahKramaMipil;
    @SerializedName("banjar_dinas_id")
    @Expose
    private Integer banjarDinasId;
    @SerializedName("banjar_adat_id")
    @Expose
    private Integer banjarAdatId;
    @SerializedName("tempekan_id")
    @Expose
    private Integer tempekanId;
    @SerializedName("penduduk_id")
    @Expose
    private Integer pendudukId;
    @SerializedName("jenis_kependudukan")
    @Expose
    private String jenisKependudukan;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("tanggal_nonaktif")
    @Expose
    private Object tanggalNonaktif;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("banjar_adat")
    @Expose
    private BanjarAdat banjarAdat;
    @SerializedName("penduduk")
    @Expose
    private Penduduk penduduk;
    @SerializedName("tempekan")
    @Expose
    private Tempekan tempekan;
}
