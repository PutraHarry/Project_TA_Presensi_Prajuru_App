package com.harry.presensiprajuru.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KramaProfileGetResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("penduduk")
    @Expose
    private Penduduk penduduk;
    @SerializedName("cacahKramaMipil")
    @Expose
    private CacahKramaMipil cacahKramaMipil;
    @SerializedName("message")
    @Expose
    private String message;
}
