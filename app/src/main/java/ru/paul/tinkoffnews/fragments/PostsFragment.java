package ru.paul.tinkoffnews.fragments;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.paul.tinkoffnews.Constants;
import ru.paul.tinkoffnews.OnLoadMoreListener;
import ru.paul.tinkoffnews.R;
import ru.paul.tinkoffnews.adapters.PostAdapter;
import ru.paul.tinkoffnews.models.Post;
import ru.paul.tinkoffnews.models.ResponseAPI;
import ru.paul.tinkoffnews.service.ApiService;

public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";

    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Post> posts = new ArrayList<>();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Integer first = 0;
    Integer last = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.posts_fragment, null);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(recyclerView);
        recyclerView.setAdapter(postAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Gson gson =new GsonBuilder().create();

        ApiService apiService = ApiService.retrofit.create(ApiService.class);

        DisposableSingleObserver<ResponseAPI> observer = new DisposableSingleObserver<ResponseAPI>() {
            @Override
            public void onSuccess(ResponseAPI responseAPI) {
                posts.addAll(responseAPI.getPayload());
                postAdapter.setLoading();
                postAdapter.setPosts(posts);
                postAdapter.notifyDataSetChanged();
                Log.i("response", "response");
                compositeDisposable.clear();
            }

            @Override
            public void onError(Throwable e) {
                Log.i("err", "err");
            }
        };

        setListener(apiService, observer);

        compositeDisposable.add(apiService.getPosts(first, last)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer));



    }

    private void setListener(ApiService apiService, DisposableSingleObserver<ResponseAPI> observer) {
        postAdapter.setOnLoadMoreListener(() -> {
            Log.i("load", "load");
            first += 20;
            last += 20;
            compositeDisposable.add(apiService.getPosts(first, last)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ResponseAPI>() {
                        @Override
                        public void onSuccess(ResponseAPI responseAPI) {
                            posts.addAll(responseAPI.getPayload());
                            postAdapter.setLoading();
                            postAdapter.setPosts(posts);
                            postAdapter.notifyDataSetChanged();
                            Log.i("responseRefresh", "responseRefresh");
                            compositeDisposable.clear();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("errRefresh", "errRefresh");
                        }
                    }));

        });

    }
}
