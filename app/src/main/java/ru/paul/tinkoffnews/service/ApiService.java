package ru.paul.tinkoffnews.service;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.paul.tinkoffnews.Constants;
import ru.paul.tinkoffnews.models.ResponseAPI;

public interface ApiService {

    @GET("news")
    Single<ResponseAPI> getPosts(@Query("first") Integer first, @Query("last") Integer last);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

}
