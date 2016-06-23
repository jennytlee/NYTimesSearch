package com.example.jennytlee.nytimessearcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

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

        setupViews();

        // load top articles data on start
        loadArticles(0);

    }


    // set up initial start page
    public void setupViews() {
        topArticles = new ArrayList<>();
        topArticleAdapter = new TopArticleAdapter(this, topArticles);
        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        linearLayoutManager = new LinearLayoutManager(this);

        // endless scroll listener for top articles
        gvTopArticles.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
                return true;
            }
        });

        // click listener for top articles
        gvTopArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                TopArticle topArticle = topArticles.get(position);
                // pass in the article to intent
                i.putExtra("article", topArticle);
                // launch the activity
                startActivity(i);
            }
        });

        // initialize adapter for top articles
        gvTopArticles.setAdapter(topArticleAdapter);
    }


    // Load top articles on start up of the app
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

    // Search articles and replace the top articles / previous search results
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

                    // in case of replacing top articles, replace adapter
                    if (!topArticleAdapter.isEmpty()) {
                        topArticleAdapter.clear();
                    }
                    gvTopArticles.setAdapter(articleAdapter);
                    // in case adapter is not empty, clear adapter (reset search)
                    if (!articleAdapter.isEmpty()) {
                        articleAdapter.clear();
                    }

                    articleAdapter.addAll(Article.fromJSONArray(articleJsonResults));

                    // endless scroll listener for searched articles
                    gvTopArticles.setOnScrollListener(new EndlessScrollListener() {
                        @Override
                        public boolean onLoadMore(int page, int totalItemsCount) {
                            customLoadMoreSearchResults(page, query);
                            return true;
                        }
                    });

                    // click listener for searched articles
                    gvTopArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // create an intent to display
                            Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                            // get the article to display
                            Article article = articles.get(position);
                            // pass in the article to intent
                            i.putExtra("article", article);
                            // launch the activity
                            startActivity(i);
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

    // Append more data into the top articles adapter
    public void customLoadMoreDataFromApi(int offset) {
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

    // using the toolbar to search
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
