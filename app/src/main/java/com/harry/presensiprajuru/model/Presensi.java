package com.harry.presensiprajuru.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Presensi {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("kegiatan_id")
    @Expose
    public Integer kegiatanId;
    @SerializedName("nama_presensi")
    @Expose
    public String namaPresensi;
    @SerializedName("keterangan")
    @Expose
    public String keterangan;
    @SerializedName("kode_presensi")
    @Expose
    public Object kodePresensi;
    @SerializedName("tgl_open")
    @Expose
    public String tglOpen;
    @SerializedName("tgl_close")
    @Expose
    public String tglClose;
    @SerializedName("is_open")
    @Expose
    public Integer isOpen;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("deleted_at")
    @Expose
    public Object deletedAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("presensi_detail")
    @Expose
    public List<DetailPresensi> presensiDetail = null;
    @SerializedName("kegiatan")
    @Expose
    public Kegiatan kegiatan;
}
