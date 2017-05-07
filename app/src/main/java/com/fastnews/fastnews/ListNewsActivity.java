package com.fastnews.fastnews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastnews.fastnews.actions.RenderNewsAction;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_news);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        queue = Volley.newRequestQueue(this);

        String url = "https://newsapi.org/v1/articles?source=bbc-sport&sortBy=top&apiKey=13461edad0894f3c88aaed55661947fb";

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray articles = jsonObject.getJSONArray("articles");
                    for(int i = 0; i < articles.length(); i ++) {
                        JSONObject article = articles.getJSONObject(i);
                        NewsModel newsModel = new NewsModel();
                        newsModel.setAuthor(article.getString("author"));
                        newsModel.setTitle(article.getString("title"));
                        newsModel.setDescription(article.getString("description"));
                        newsModel.setUrl(article.getString("url"));
                        newsModel.setUrlToImage(article.getString("urlToImage"));
                        newsModel.setPublishedAt(article.getString("publishedAt"));
                        news.add(newsModel);
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                renderNews();

                TextView emptyData = (TextView) findViewById(R.id.emptyData);
                if(news.size() != 0)
                    emptyData.setText("");
                else
                    emptyData.setText("Noticias não foram encontradas... Tente novamente mais tarde.");
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView emptyData = (TextView) findViewById(R.id.emptyData);
                emptyData.setText("Erro ao carregar seu feed de noticias!");
            }
        });

        stringRequest.setTag(REQUEST_BBC_SPORTS);

        queue.add(stringRequest);
    }

    private void renderNews(){
        RecyclerView.Adapter newsAdapter = new NewsAdapter(news, new RenderNewsAction() {
            @Override
            public void renderNewsDay(int position) {
                favorites.add(news.get(position));
                news.remove(position);
                renderNews();
            }
        });
        recyclerView.setAdapter(newsAdapter);
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
