package ru.paul.tinkoffnews.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.paul.tinkoffnews.Constants;
import ru.paul.tinkoffnews.OnOpenContentListener;
import ru.paul.tinkoffnews.R;
import ru.paul.tinkoffnews.adapters.PostAdapter;
import ru.paul.tinkoffnews.models.Post;
import ru.paul.tinkoffnews.models.ResponseAPI;
import ru.paul.tinkoffnews.service.ApiService;

public class PostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "PostsFragment";

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    PostAdapter postAdapter;
    List<Post> posts = new ArrayList<>();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SharedPreferences sharedPreferences;
    Integer first = Constants.first;
    Integer last = Constants.last;
    ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.posts_fragment, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        swipeRefreshLayout = v.findViewById(R.id.refresh_posts);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        apiService = ApiService.retrofit.create(ApiService.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(recyclerView);
        recyclerView.setAdapter(postAdapter);

        DisposableSingleObserver<ResponseAPI> observer = new DisposableSingleObserver<ResponseAPI>() {
            @Override
            public void onSuccess(ResponseAPI responseAPI) {

                changePosts(responseAPI);
            }

            @Override
            public void onError(Throwable e) {
                Log.i("err", "err");
            }
        };

        setListener(observer);
        swipeRefreshLayout.setOnRefreshListener(this);

        postAdapter.setClick((OnOpenContentListener) getActivity());

        compositeDisposable.add(apiService.getPosts(first, last)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer));

    }

    private void setListener(DisposableSingleObserver<ResponseAPI> observer) {
        postAdapter.setOnLoadMoreListener(() -> {
            first += Constants.last;
            last += Constants.last;
            posts.add(null);
            postAdapter.notifyItemInserted(posts.size() - 1);
            compositeDisposable.add(apiService.getPosts(first, last)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ResponseAPI>() {
                        @Override
                        public void onSuccess(ResponseAPI responseAPI) {
                            posts.remove(posts.size() - 1);
                            postAdapter.notifyItemRemoved(posts.size());

                            changePosts(responseAPI);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("errRefresh", "errRefresh");
                        }
                    }));

        });

    }

    @Override
    public void onRefresh() {
        first = Constants.first;
        last = Constants.last;
        onRefreshPosts();
    }


    private void onRefreshPosts() {
        compositeDisposable.add(apiService.getPosts(first, last)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseAPI>() {
                    @Override
                    public void onSuccess(ResponseAPI responseAPI) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (posts != null && posts.size() > 0) {
                            posts.clear();

                            changePosts(responseAPI);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));

    }

    private void changePosts(ResponseAPI responseAPI) {
        posts.addAll(responseAPI.getPayload());
        postAdapter.setLoading();
        postAdapter.setPosts(posts);
        postAdapter.notifyDataSetChanged();
        saveOrUpdateData(posts);
        compositeDisposable.clear();
    }

    private void saveOrUpdateData(List<Post> savePosts) {

        if (getActivity() != null) {
            sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(savePosts);
            editor.putString("posts", json);
            editor.apply();
        }
    }

    private List<Post> getPostsFromPref() {
        if (getActivity() != null) {
            sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("posts", "");
            return gson.fromJson(json, new TypeToken<List<Post>>(){}.getType());
        }
        return null;
    }
}
