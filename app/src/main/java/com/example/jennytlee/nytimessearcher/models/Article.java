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
public class Article implements Parcelable {

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

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(1);
                this.thumbnail = String.format("http://www.nytimes.com/%s", multimediaJson.getString("url"));
            } else {
                this.thumbnail = "http://i.imgur.com/50NrOnW.png";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new Article(array.getJSONObject(x)));
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

    private Article(Parcel in) {
        webUrl = in.readString();
        headline = in.readString();
        thumbnail = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR
            = new Parcelable.Creator<Article>() {

        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
