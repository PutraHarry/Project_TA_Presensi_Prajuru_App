package com.harry.presensiprajuru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailPresensiResponse {
    @SerializedName("statusCode")
    @Expose
    public Integer statusCode;
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("presensi")
    @Expose
    public Presensi presensi;
    @SerializedName("presensi_detail")
    @Expose
    public List<DetailPresensi> detailPresensiList = null;
    @SerializedName("message")
    @Expose
    public String message;
}
