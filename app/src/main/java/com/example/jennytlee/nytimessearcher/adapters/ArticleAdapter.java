package com.example.jennytlee.nytimessearcher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jennytlee.nytimessearcher.R;
import com.example.jennytlee.nytimessearcher.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jennytlee on 6/21/16.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    public static class ViewHolder {
        @BindView(R.id.tvHeadline) TextView tvHeadline;
        @BindView(R.id.ivThumbnail) ImageView ivThumbnail;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        ViewHolder viewholder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article_result, parent, false);
            viewholder = new ViewHolder(convertView);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.tvHeadline.setText(article.headline);
        Picasso.with(getContext()).load(article.thumbnail).fit().into(viewholder.ivThumbnail);

        return convertView;

    }
}
