package com.example.companyfind.gus;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SoapApi {
    @Headers({
            "Content-Type: text/xml; charset=utf-8",
            "SOAPAction: \"http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/StatusSesji\""
    })
    @POST("UslugaBIRzewnPubl.svc")
    Call<SoapResponse> statusSesji(@Body SoapRequest request);

    @Headers({
            "Content-Type: application/soap+xml; charset=utf-8"
    })
    @POST("UslugaBIRzewnPubl.svc")
    Call<ResponseBody> zaloguj(@Body RequestBody soapEnvelope);

    @Headers({
            "Content-Type: application/soap+xml; charset=utf-8"
    })
    @POST("UslugaBIRzewnPubl.svc")
    Call<ResponseBody> daneSzukajPodmioty(@Body RequestBody soapEnvelope);
}
