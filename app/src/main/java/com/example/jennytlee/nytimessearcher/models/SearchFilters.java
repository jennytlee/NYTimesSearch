package com.example.jennytlee.nytimessearcher.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jennytlee on 6/24/16.
 */
public class SearchFilters implements Parcelable {

    String beginDate;
    String sort;
    String newsDesks;

    public SearchFilters() {
        beginDate = "";
        sort = "oldest";
        newsDesks = "";
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getSort() {
        if (sort == null) {
            return "oldest";
        }
        return sort;
    }

    public String getNewsDesks() {
        return newsDesks;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setNewsDesks(String newsDesks) {
        this.newsDesks =  String.format("news_desk:(%s)", newsDesks);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(beginDate);
        out.writeString(sort);
        out.writeString(newsDesks);
    }

    private SearchFilters(Parcel in) {
        beginDate = in.readString();
        sort = in.readString();
        newsDesks = in.readString();
    }

    public static final Parcelable.Creator<SearchFilters> CREATOR
            = new Parcelable.Creator<SearchFilters>() {

        @Override
        public SearchFilters createFromParcel(Parcel in) {
            return new SearchFilters(in);
        }

        @Override
        public SearchFilters[] newArray(int size) {
            return new SearchFilters[size];
        }
    };

}
