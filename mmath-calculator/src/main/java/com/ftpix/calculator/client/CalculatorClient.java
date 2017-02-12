package com.ftpix.calculator.client;

import com.ftpix.mmath.model.MmathFighter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by gz on 25-Sep-16.
 */
public interface CalculatorClient {

    @GET("/better-than/{fighter}/count")
    Call<Long> betterThanCount(@Path("fighter") String fighterId);

    @GET("/weaker-than/{fighter}/count")
    Call<Long> weakerThanCount(@Path("fighter") String fighterId);


    @GET("/better-than/{fighter1}/{fighter2}")
    Call<List<MmathFighter>> betterThan(@Path("fighter1") String fighter1Id, @Path("fighter2") String fighter2Id);

}
