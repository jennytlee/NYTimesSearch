package com.example.jennytlee.nytimessearcher;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.etsy.android.grid.StaggeredGridView;
import com.example.jennytlee.nytimessearcher.adapters.ArticleAdapter;
import com.example.jennytlee.nytimessearcher.adapters.TopArticleAdapter;
import com.example.jennytlee.nytimessearcher.models.Article;
import com.example.jennytlee.nytimessearcher.models.TopArticle;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.gvTopArticles) StaggeredGridView gvTopArticles;

    ArrayList<TopArticle> topArticles;
    TopArticleAdapter topArticleAdapter;
    LinearLayoutManager linearLayoutManager;

    ArrayList<Article> articles;
    ArticleAdapter articleAdapter;



    public static final String API_KEY = "0be89842a4dc4f99ba0d5aa314659d4d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        topArticles = new ArrayList<>();
        topArticleAdapter = new TopArticleAdapter(this, topArticles);
        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        linearLayoutManager = new LinearLayoutManager(this);

        gvTopArticles.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
                return true;
            }
        });

        // initialize adapter for top articles
        gvTopArticles.setAdapter(topArticleAdapter);

        // load top articles data
        loadArticles(0);

    }

    private void loadArticles(int page) {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/topstories/v2/home.json";
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", page);


        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results;
                    if (response != null) {
                        results = response.getJSONArray("results");
                        ArrayList<TopArticle> articles = TopArticle.fromJSONArray(results);
                        topArticleAdapter.clear();

                        for (TopArticle article : articles) {
                            topArticleAdapter.add(article);
                        }
                        topArticleAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    // Search articles
    private void searchArticles(int page, final String query) {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", page);
        params.put("q", query);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");

                    if (!topArticleAdapter.isEmpty()) {

                        topArticleAdapter.clear();
                    }
                    gvTopArticles.setAdapter(articleAdapter);
                    articleAdapter.addAll(Article.fromJSONArray(articleJsonResults));

                    gvTopArticles.setOnScrollListener(new EndlessScrollListener() {
                        @Override
                        public boolean onLoadMore(int page, int totalItemsCount) {
                            customLoadMoreSearchResults(page, query);

                            return true;
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/topstories/v2/home.json";
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", offset);


        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results;
                    if (response != null) {
                        results = response.getJSONArray("results");
                        ArrayList<TopArticle> articles = TopArticle.fromJSONArray(results);

                        for (TopArticle article : articles) {
                            topArticleAdapter.add(article);
                        }
                        topArticleAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    // Append more data into the search adapter
    public void customLoadMoreSearchResults(int offset, String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", offset);
        params.put("q", query);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray articleJsonResults;
                    if (response != null) {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        ArrayList<Article> articles = Article.fromJSONArray(articleJsonResults);

                        for (Article article : articles) {
                            articleAdapter.add(article);
                        }
                        articleAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchArticles(0, query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }



}
