package com.fastnews.fastnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.TextView;

import com.fastnews.fastnews.actions.FavoriteAction;
import com.fastnews.fastnews.actions.RenderNewsAction;
import com.fastnews.fastnews.models.ListNewsModel;
import com.fastnews.fastnews.models.NewsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucask on 08/05/17.
 */

public class ListFavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private List<NewsModel> favorites = new ArrayList<NewsModel>();
    private List<NewsModel> news = new ArrayList<NewsModel>();

    private RenderNewsAction renderNewsAction = new RenderNewsAction() {
        @Override
        public void renderNewsDay() {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Fast News - Favorite News");
        setContentView(R.layout.activity_list_favorites);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_favorites);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ListNewsModel favoriteModels = (ListNewsModel) getIntent().getSerializableExtra("favorites");
        favorites = favoriteModels.models;

        ListNewsModel newsModels = (ListNewsModel) getIntent().getSerializableExtra("news");
        news = newsModels.models;

        Intent intent = getIntent();
        intent.putExtra("news", newsModels);
        intent.putExtra("favorites", favoriteModels);
        setResult(ActivityActions.FAVORITES.ordinal(), intent);

        renderNews();
    }

    private void renderNews(){
        RecyclerView.Adapter newsAdapter = new NewsAdapter(favorites, renderNewsAction, new FavoriteAction() {
            @Override
            public void addToFavorites(NewsModel newsModel) {
                favorites.remove(newsModel);
                news.add(newsModel);
                verificaListaDeFavoritos();
            }
        });
        recyclerView.setAdapter(newsAdapter);

        verificaListaDeFavoritos();
    }

    private void verificaListaDeFavoritos() {
        TextView emptyFavorites = (TextView) findViewById(R.id.emptyFavorites);
        if(favorites.size() != 0)
            emptyFavorites.setText("");
        else
            emptyFavorites.setText("Lista de favoritos está vazia.");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_favorites, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
