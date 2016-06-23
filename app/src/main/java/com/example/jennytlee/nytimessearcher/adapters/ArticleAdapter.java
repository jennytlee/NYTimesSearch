package com.example.jennytlee.nytimessearcher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jennytlee.nytimessearcher.R;
import com.example.jennytlee.nytimessearcher.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jennytlee on 6/21/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    ArrayList<Article> articles;
    Context mContext;
    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvHeadline) TextView tvHeadline;
        @BindView(R.id.ivThumbnail) ImageView ivThumbnail;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

    public ArticleAdapter(Context context, ArrayList<Article> articleList) {
        articles = articleList;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder viewHolder, int position) {
        Article article = articles.get(position);

        viewHolder.tvHeadline.setText(article.getHeadline());

        String thumbnailT = article.getThumbnail();

        if (!TextUtils.isEmpty(thumbnailT)) {
            Picasso.with(getContext()).load(article.thumbnail).resize(600, 600).centerCrop().into(viewHolder.ivThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void clearData() {
        articles.clear(); //clear list
        this.notifyDataSetChanged(); //let your adapter know about the changes and reload view.
    }

    public void addItem(Article article) {
        articles.add(article);
        this.notifyDataSetChanged();
    }

    /*   @Override
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

        double positionHeight = getPositionRatio(position);
        viewholder.ivThumbnail.setHeightRatio(positionHeight);
        viewholder.tvHeadline.setText(topArticle.getHeadline());

        String thumbnailT = topArticle.getThumbnail();
        if (!TextUtils.isEmpty(thumbnailT)) {
            Picasso.with(getContext()).load(topArticle.thumbnail).resize(600, 600).centerCrop().into(viewholder.ivThumbnail);
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
    */

}
