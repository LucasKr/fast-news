package com.fastnews.fastnews;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fastnews.fastnews.actions.RenderNewsAction;
import com.fastnews.fastnews.models.NewsModel;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    private List<NewsModel> news;
    protected  RenderNewsAction renderNewsAction;

    public NewsAdapter(List<NewsModel> newsModelList, RenderNewsAction renderNewsAction) {
        this.news = newsModelList;
        this.renderNewsAction = renderNewsAction;
    }

    public class NewsHolder extends RecyclerView.ViewHolder {
        View news;

        public NewsHolder(View view) {
            super(view);
            news = view;
        }

    }

    @Override
    public NewsAdapter.NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_list, parent, false);
        return new NewsHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewsAdapter.NewsHolder holder, final int position) {
        NewsModel newsModel = news.get(position);

        ImageButton addFavorite = (ImageButton) holder.news.findViewById(R.id.addFavorito);
        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderNewsAction.renderNewsDay(position);
            }
        });

        TextView title = (TextView) holder.news.findViewById(R.id.title);
        TextView descricao = (TextView) holder.news.findViewById(R.id.description);
        TextView author = (TextView) holder.news.findViewById(R.id.author);

        title.setText(newsModel.getTitle());
        descricao.setText(newsModel.getDescription());
        author.setText(newsModel.getAuthor());
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}
