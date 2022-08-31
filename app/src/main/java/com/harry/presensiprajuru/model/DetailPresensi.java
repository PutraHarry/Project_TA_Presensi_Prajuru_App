package com.harry.presensiprajuru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DetailPresensi {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("presensi_id")
    @Expose
    public Integer presensiId;
    @SerializedName("is_hadir")
    @Expose
    public Integer isHadir;
    @SerializedName("nomor_cacah_krama_mipil")
    @Expose
    public String nomorCacahKramaMipil;
    @SerializedName("uid_kartu")
    @Expose
    public String uidKartu;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    public Object deletedAt;
    @SerializedName("cacah_krama_mipil")
    @Expose
    public CacahKramaMipil cacahKramaMipil;
}
