package com.example.jennytlee.nytimessearcher.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.etsy.android.grid.util.DynamicHeightTextView;
import com.example.jennytlee.nytimessearcher.R;
import com.example.jennytlee.nytimessearcher.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jennytlee on 6/21/16.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {

    Random mRandom;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
        mRandom = new Random();
    }

    public static class ViewHolder {
        @BindView(R.id.tvHeadline) DynamicHeightTextView tvHeadline;
        @BindView(R.id.ivThumbnail) DynamicHeightImageView ivThumbnail;

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

        double positionHeight = getPositionRatio(position);
        viewholder.ivThumbnail.setHeightRatio(positionHeight);
        viewholder.tvHeadline.setText(article.getHeadline());

        String thumbnailT = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnailT)) {
            Picasso.with(getContext()).load(article.thumbnail).fit().centerCrop().into(viewholder.ivThumbnail);
        }

        return convertView;

    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() * 0.7) + 1.0; // height will be 1.0 - 1.5 the width
    }

}
