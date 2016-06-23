package com.example.jennytlee.nytimessearcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jennytlee.nytimessearcher.models.Article;
import com.example.jennytlee.nytimessearcher.models.TopArticle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.wvArticle) WebView wvArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // set webview inside app
        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if (getIntent().getParcelableExtra("article") instanceof TopArticle) {
            TopArticle topArticle = getIntent().getParcelableExtra("article");
            wvArticle.loadUrl(topArticle.getWebUrl());
        } else {
            Article article = getIntent().getParcelableExtra("article");
            wvArticle.loadUrl(article.getWebUrl());
        }



    }

}
