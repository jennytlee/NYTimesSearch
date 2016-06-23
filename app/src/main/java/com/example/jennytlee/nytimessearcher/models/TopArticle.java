package com.example.jennytlee.nytimessearcher.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jennytlee on 6/21/16.
 */

public class TopArticle implements Parcelable {

    public String webUrl;
    public String headline;
    public String thumbnail;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public TopArticle(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("url");
            this.headline = jsonObject.getString("title");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                this.thumbnail = multimedia.getJSONObject(multimedia.length()-1).getString("url");
            } else {
                this.thumbnail = "http://i.imgur.com/50NrOnW.png";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<TopArticle> fromJSONArray(JSONArray array) {
        ArrayList<TopArticle> results = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new TopArticle(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(webUrl);
        out.writeString(headline);
        out.writeString(thumbnail);
    }

    private TopArticle(Parcel in) {
        webUrl = in.readString();
        headline = in.readString();
        thumbnail = in.readString();
    }

    public static final Parcelable.Creator<TopArticle> CREATOR
            = new Parcelable.Creator<TopArticle>() {

        @Override
        public TopArticle createFromParcel(Parcel in) {
            return new TopArticle(in);
        }

        @Override
        public TopArticle[] newArray(int size) {
            return new TopArticle[size];
        }
    };


}
