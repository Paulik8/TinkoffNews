package ru.paul.tinkoffnews.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.paul.tinkoffnews.R;
import ru.paul.tinkoffnews.models.ResponseDetail;
import ru.paul.tinkoffnews.service.ApiService;

public class DetailFragment extends Fragment {

    public static String TAG = "DetailFragment";
    TextView content;
    ApiService apiService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_fragment, container, false);
        content = v.findViewById(R.id.content_detail);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int id = 0;

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getInt("id");
        }

        apiService = ApiService.retrofit.create(ApiService.class);

        compositeDisposable.add(apiService.getDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseDetail>() {

                    @Override
                    public void onSuccess(ResponseDetail responseDetail) {
                        content.setText(Html.fromHtml(responseDetail.getPayload().getContent()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("errorDetail", "err");
                    }
                }));

    }

}
