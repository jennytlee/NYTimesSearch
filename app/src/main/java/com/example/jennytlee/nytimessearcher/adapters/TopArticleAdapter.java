package com.example.jennytlee.nytimessearcher.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jennytlee.nytimessearcher.R;
import com.example.jennytlee.nytimessearcher.models.TopArticle;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jennytlee on 6/21/16.
 */
public class TopArticleAdapter extends ArrayAdapter<TopArticle> {

    public TopArticleAdapter(Context context, List<TopArticle> topArticles) {
        super(context, android.R.layout.simple_list_item_1, topArticles);
    }

    public static class ViewHolder {
        @BindView(R.id.tvTopHeadline) TextView tvHeadline;
        @BindView(R.id.ivTopThumbnail) ImageView ivThumbnail;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopArticle topArticle = getItem(position);
        ViewHolder viewholder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article, parent, false);
            viewholder = new ViewHolder(convertView);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.tvHeadline.setText(topArticle.getHeadline());

        String thumbnailT = topArticle.getThumbnail();
        if (!TextUtils.isEmpty(thumbnailT)) {
            Picasso.with(getContext()).load(topArticle.thumbnail).fit().into(viewholder.ivThumbnail);
        }



        return convertView;

    }
}
