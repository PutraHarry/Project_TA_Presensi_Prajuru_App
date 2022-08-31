package com.harry.presensiprajuru.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Kegiatan {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("nama_kegiatan")
    @Expose
    public String namaKegiatan;
    @SerializedName("keterangan")
    @Expose
    public String keterangan;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("deleted_at")
    @Expose
    public Object deletedAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
}
