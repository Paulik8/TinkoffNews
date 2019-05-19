package ru.paul.tinkoffnews.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.paul.tinkoffnews.OnLoadMoreListener;
import ru.paul.tinkoffnews.OnOpenContentListener;
import ru.paul.tinkoffnews.R;
import ru.paul.tinkoffnews.models.Post;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Post> posts;
    private int totalItemCount, lastVisibleItem;
    private OnOpenContentListener onOpenContentListener;
    private boolean loading;
    private int visibleThreshold = 2;
    private OnLoadMoreListener onLoadMoreListener;

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public PostAdapter(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.OnLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return posts.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;
        if (i == VIEW_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.posts_item, viewGroup, false);
            vh = new PostViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar, viewGroup, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof PostViewHolder) {
            ((PostViewHolder)viewHolder).bind(posts.get(i), onOpenContentListener);
            ((PostViewHolder)viewHolder).text.setText(reduceText((posts.get(i).getText())));
            ((PostViewHolder)viewHolder).date.setText(convertTime(posts.get(i).getPublicationDate().getMilliseconds()));
        } else {
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        if (posts == null)
            return 0;
        else
            return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        CardView cardView;
        TextView date;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_post);
            cardView = itemView.findViewById(R.id.card_post);
            date = itemView.findViewById(R.id.date_post);
        }

        void bind(Post post, final OnOpenContentListener onOpenContentListener) {
            itemView.setOnClickListener((v) ->
                    onOpenContentListener.show(post.getId()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private String reduceText(String text) {
        String separator = "...";
        if (text.length() > 70) {
            return text.subSequence(0, 70) + separator;
        }
        return text;
    }

    private String convertTime(Long milliseconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.CANADA);
        return format.format(calendar.getTime());
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setClick(OnOpenContentListener onClickListener) {
        onOpenContentListener = onClickListener;
    }

    public void setLoading() {
        this.loading = false;
    }
}
