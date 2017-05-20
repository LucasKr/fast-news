package com.fastnews.fastnews.actions;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fastnews.fastnews.R;
import com.fastnews.fastnews.models.NewsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucask on 20/05/17.
 */

public class Actions {

    public static final String REQUEST_NEWS = "REQUEST_NEWS";

    public static boolean isActionToRequestNews(String action) {
        return "carregar.economia".equals(action)
                || "carregar.esportes".equals(action)
                || "carrergar.tecnologia".equals(action);
    }


    public static void chatBotTalking(List<NewsModel> noticias, String action, RequestQueue queue, RenderNewsAction render) {
        if(!isActionToRequestNews(action))
            return;


        StringRequest stringRequest;
        String url = "";

        Log.d("ACTION", action);

        if("carregar.economia".equals(action)) {
            url = "https://newsapi.org/v1/articles?source=the-economist&sortBy=top&apiKey=13461edad0894f3c88aaed55661947fb";
        } else if("carregar.esportes".equals(action)) {
            url = "https://newsapi.org/v1/articles?source=espn&sortBy=top&apiKey=13461edad0894f3c88aaed55661947fb";
        } else { //if("carrergar.tecnologia".equals(action);
            url = "https://newsapi.org/v1/articles?source=hacker-news&sortBy=top&apiKey=13461edad0894f3c88aaed55661947fb";
        }

        stringRequest = listNewsFromAPI(url, REQUEST_NEWS, noticias, render);
        queue.add(stringRequest);

    }

    public static StringRequest listNewsFromAPI(String url, String REQUEST_TAG, final List<NewsModel> news, final RenderNewsAction render) {

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
                    if(render != null)
                        render.renderNewsDay();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        },  new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                news.clear();
            }

        });

        stringRequest.setTag(REQUEST_TAG);

        return stringRequest;
    }

}
