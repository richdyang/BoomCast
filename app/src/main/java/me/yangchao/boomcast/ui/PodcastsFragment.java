package me.yangchao.boomcast.ui;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.subjects.PublishSubject;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.util.DateUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class PodcastsFragment extends Fragment {

    // data
    private List<Podcast> podcasts = new ArrayList<>();

    // ui widgets
    @BindView(R.id.podcasts_recyclerview) RecyclerView podcastsRecyclerView;

    // event subject
    PublishSubject<Long> podcastClicked = PublishSubject.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_podcasts, container, false);

        ButterKnife.bind(this, view);

        // recycler view
        podcastsRecyclerView.setAdapter(new PodcastsRecyclerAdapter());
        podcastsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        refresh();
    }

    public void refresh() {
        podcasts = Podcast.listAll(Podcast.class);

        if(podcastsRecyclerView != null) {
            podcastsRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    class PodcastsRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;

        public PodcastsRecyclerAdapter() {}

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (context == null) context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_podcasts_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Podcast podcast = PodcastsFragment.this.podcasts.get(position);

            // podcast title, cover, date
            holder.podcastTitle.setText(podcast.getTitle());
            holder.podcastPublishedAt.setText(DateUtil.formatDate(podcast.getPublishedDate()));
            // podcast cover
            Glide.with(context)
                    .load(Uri.parse(podcast.getImageUrl()))
                    .placeholder(R.drawable.placeholder)
                    .override(180, 240)
                    .into(holder.podcastImage);

            // click item event handler
            holder.itemView.setOnClickListener(v -> {
                podcastClicked.onNext(podcast.getId());
            });
        }

        @Override
        public int getItemCount() {
            return podcasts.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.podcast_image) ImageView podcastImage;
        @BindView(R.id.podcast_title) TextView podcastTitle;
        @BindView(R.id.podcast_published_at) TextView podcastPublishedAt;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}