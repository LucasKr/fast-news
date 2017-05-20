package com.fastnews.fastnews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.fastnews.fastnews.actions.FavoriteAction;
import com.fastnews.fastnews.actions.RenderNewsAction;
import com.fastnews.fastnews.models.ListNewsModel;
import com.fastnews.fastnews.models.NewsModel;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.util.ArrayList;
import java.util.Arrays;
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

    public void sendMail(View view) {
        Log.d("ListFavoriteActivity","Enviando e-mail...");
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(new String[] {GmailScopes.MAIL_GOOGLE_COM}))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString("accountName", null));

        Gmail mService = new Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("AndroidGmailAPIexample")
                .build();

        try {
            GmailUtils.sendMessage(mService, "ME", GmailUtils.createEmail("lucas.kr1996@gmail.com", "lucas.kr1996@gmail.com", "Noticias favoritadas", "Lista de noticias aqui. lista com .toString() "));
        } catch (Exception e) {
            Log.e("ListFavoriteActivity", "Erro ao enviar email", e);
        }

        Log.d("ListFavoriteActivity","E-mail enviado...");
    }




}
