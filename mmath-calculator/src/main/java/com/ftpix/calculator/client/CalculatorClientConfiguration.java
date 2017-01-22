package com.ftpix.calculator.client;

import com.ftpix.utils.GsonUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gz on 25-Sep-16.
 */

@Configuration
@PropertySource("classpath:config.properties")
public class CalculatorClientConfiguration {

    @Value("${calculator.url}")
    private  String calculatorUrl;

    @Bean
    CalculatorClient calculatorClient(){
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(calculatorUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                .client(okHttpClient)
                .build();

        return retrofit.create(CalculatorClient.class);
    }
}
