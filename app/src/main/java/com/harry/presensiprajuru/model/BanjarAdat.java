package com.harry.presensiprajuru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BanjarAdat {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("desa_adat_id")
    @Expose
    private Integer desaAdatId;
    @SerializedName("kode_banjar_adat")
    @Expose
    private String kodeBanjarAdat;
    @SerializedName("nama_banjar_adat")
    @Expose
    private String namaBanjarAdat;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
}
