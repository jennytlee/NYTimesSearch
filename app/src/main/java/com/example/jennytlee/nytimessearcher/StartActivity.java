package com.example.jennytlee.nytimessearcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jennytlee.nytimessearcher.adapters.ArticleAdapter;
import com.example.jennytlee.nytimessearcher.adapters.TopArticleAdapter;
import com.example.jennytlee.nytimessearcher.dialogs.SearchFilterDialogFragment;
import com.example.jennytlee.nytimessearcher.models.Article;
import com.example.jennytlee.nytimessearcher.models.SearchFilters;
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

public class StartActivity extends AppCompatActivity implements SearchFilterDialogFragment.OnFilterSearchListener{

    @BindView(R.id.gvTopArticles)
    RecyclerView gvTopArticles;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    MenuItem miActionProgressItem;

    ArrayList<TopArticle> topArticles;
    TopArticleAdapter topArticleAdapter;
    StaggeredGridLayoutManager layoutManager;

    ArrayList<Article> articles;
    ArticleAdapter articleAdapter;

    SearchFilters mfilters;
    String savedQuery;

    public static final String API_KEY = "0be89842a4dc4f99ba0d5aa314659d4d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mfilters = new SearchFilters();
        setupViews();
        loadArticles(0);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // load top articles data on start
                loadArticles(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    // set up initial start page
    public void setupViews() {
        topArticles = new ArrayList<>();
        topArticleAdapter = new TopArticleAdapter(this, topArticles);
        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        // endless scroll listener for top articles
        gvTopArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });

        // click listener for top articles
        topArticleAdapter.setOnItemClickListener(new TopArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
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
        gvTopArticles.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
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
                topArticleAdapter.clearData();
                try {
                    JSONArray results;
                    if (response != null) {
                        results = response.getJSONArray("results");
                        ArrayList<TopArticle> articles = TopArticle.fromJSONArray(results);
                        topArticleAdapter.clearData();

                        for (TopArticle article : articles) {
                            topArticleAdapter.addItem(article);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // Search articles and replace the top articles / previous search results
    private void searchArticles(int page, final String query, SearchFilters filter) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", page);
        params.put("q", query);

        if (filter != null) {
            String date = filter.getBeginDate();
            String sort = filter.getSort();
            String newsDesks = filter.getNewsDesks();

            params.put("sort", sort);

            if (!newsDesks.equals("news_desk:()")) {
                params.put("fq", filter);
            }

            if (!date.equals("")) {
                params.put("begin_date", date);
            }

        }

        gvTopArticles.setAdapter(articleAdapter);

        showProgressBar();
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                hideProgressBar();

                try {
                    JSONArray results;

                    if (response != null) {
                        results = response.getJSONObject("response").getJSONArray("docs");
                        ArrayList<Article> articles = Article.fromJSONArray(results);

                        // in case adapter is not empty, clear adapter (reset search)
                        if (articleAdapter.getItemCount() != 0) {
                            articleAdapter.clearData();
                        }

                        for (Article article : articles) {
                            articleAdapter.addItem(article);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // endless scroll listener for searched articles
                gvTopArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        customLoadMoreSearchResults(page, query);
                    }
                });

                // click listener for searched articles
                articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
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

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideProgressBar();
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
                            topArticleAdapter.addItem(article);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
                            articleAdapter.addItem(article);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

                savedQuery = query;

                topArticleAdapter.clearData();
                articleAdapter.clearData();

                // perform query here
                searchView.clearFocus();

                searchArticles(0, query, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter) {
            showFiltersDialog();
        }

        return super.onOptionsItemSelected(item);
    }


    public void showFiltersDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SearchFilterDialogFragment filtersDialogFragment =
                SearchFilterDialogFragment.newInstance(mfilters);
        filtersDialogFragment.show(fm, "fragment_search_filter");
    }

    public void onUpdateFilters(SearchFilters filters) {
        // 1. Access the updated filters here and store them in member variable
        // 2. Initiate a fresh search with these filters updated and same query value

        articleAdapter.clearData();
        searchArticles(0, savedQuery, filters);

    }
}
