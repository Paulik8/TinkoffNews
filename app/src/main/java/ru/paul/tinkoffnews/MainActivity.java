package ru.paul.tinkoffnews;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.paul.tinkoffnews.fragments.DetailFragment;
import ru.paul.tinkoffnews.fragments.PostsFragment;
import ru.paul.tinkoffnews.models.Post;

public class MainActivity extends AppCompatActivity implements OnOpenContentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PostsFragment postsFragment = new PostsFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment, postsFragment, PostsFragment.TAG)
                .commit();
    }

    @Override
    public void show(Integer id) {

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment, detailFragment, DetailFragment.TAG)
                .addToBackStack(null)
                .commit();
    }
}
