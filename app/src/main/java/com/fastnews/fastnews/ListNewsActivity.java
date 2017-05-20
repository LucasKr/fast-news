package com.fastnews.fastnews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastnews.fastnews.actions.Actions;
import com.fastnews.fastnews.actions.FavoriteAction;
import com.fastnews.fastnews.actions.RenderNewsAction;
import com.fastnews.fastnews.models.ListNewsModel;
import com.fastnews.fastnews.models.NewsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListNewsActivity extends AppCompatActivity {

    private static final String REQUEST_BBC_SPORTS = "REQUEST_BBC_SPORTS";
    private RecyclerView recyclerView;
    private RequestQueue queue;

    private List<NewsModel> news = new ArrayList<NewsModel>();
    private List<NewsModel> favorites = new ArrayList<NewsModel>();

    private RenderNewsAction renderNewsAction = new RenderNewsAction() {
        @Override
        public void renderNewsDay() {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Fast News - Daily News");
        setContentView(R.layout.activity_list_news);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        queue = Volley.newRequestQueue(this);

        String url = "https://newsapi.org/v1/articles?source=bbc-sport&sortBy=top&apiKey=13461edad0894f3c88aaed55661947fb";

        StringRequest stringRequest  = Actions.listNewsFromAPI(url, Actions.REQUEST_NEWS, news, new RenderNewsAction() {
            @Override
            public void renderNewsDay() {
                renderNews();
            }
        });
        queue.add(stringRequest);
    }

    private void renderNews(){
        RecyclerView.Adapter newsAdapter = new NewsAdapter(news, renderNewsAction, new FavoriteAction() {
            @Override
            public void addToFavorites(NewsModel newsModel) {
                news.remove(newsModel);
                favorites.add(newsModel);
            }
        });
        recyclerView.setAdapter(newsAdapter);


        TextView emptyData = (TextView) findViewById(R.id.emptyData);
        if(news.size() != 0)
            emptyData.setText("");
        else
            emptyData.setText("Noticias não foram encontradas... Tente novamente mais tarde.");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_news, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.verFavoritos) {
            Intent intent = new Intent(this, ListFavoriteActivity.class);
            ListNewsModel listNewsModelFav = new ListNewsModel(favorites);
            ListNewsModel listNewsModel = new ListNewsModel(news);
            intent.putExtra("favorites", listNewsModelFav);
            intent.putExtra("news", listNewsModel);
            startActivityForResult(intent,  ActivityActions.FAVORITES.ordinal());
            return true;
        }
        else if (item.getItemId() == R.id.verChat) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivityForResult(intent,  ActivityActions.CHAT.ordinal());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(ActivityActions.FAVORITES.ordinal() == resultCode) {
            ListNewsModel listnewsModel = (ListNewsModel) data.getSerializableExtra("news");
            news = listnewsModel.models;

            ListNewsModel listFavoritesModel = (ListNewsModel) data.getSerializableExtra("favorites");
            favorites = listFavoritesModel.models;

            renderNews();
        }

        if(ActivityActions.CHAT.ordinal() == resultCode) {
            ListNewsModel listnewsModel = (ListNewsModel) data.getSerializableExtra("news");
            news = listnewsModel.models;

            renderNews();
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(REQUEST_BBC_SPORTS);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}
