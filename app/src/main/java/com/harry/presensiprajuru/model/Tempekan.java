package com.harry.presensiprajuru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tempekan {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("banjar_adat_id")
    @Expose
    private Integer banjarAdatId;
    @SerializedName("kode_tempekan")
    @Expose
    private String kodeTempekan;
    @SerializedName("nama_tempekan")
    @Expose
    private String namaTempekan;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("jumlah_krama")
    @Expose
    private Integer jumlahKrama;
}
