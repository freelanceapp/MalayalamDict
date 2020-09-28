package com.elamblakatt.dict_eng_malayalam.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Word1 implements Parcelable {
    private int id;
    private String word;
    private String pronouce;
    private String english_meaning;
    private String hindi_meaning;
    private String wordType;
    boolean favorite;

    public Word1() {
    }

    public String getEnglish_meaning() {
        return english_meaning;
    }

    public void setEnglish_meaning(String english_meaning) {
        this.english_meaning = english_meaning;
    }

    public String getHindi_meaning() {
        return hindi_meaning;
    }

    public void setHindi_meaning(String hindi_meaning) {
        this.hindi_meaning = hindi_meaning;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public String getPronouce() {
        return pronouce;
    }

    public void setPronouce(String pronouce) {
        this.pronouce = pronouce;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Word1(int id, String english_meaning, String hindi_meaning, String wordType, String pronouce, String word, boolean favorite) {
        this.id = id;
        this.english_meaning = english_meaning;
        this.hindi_meaning = hindi_meaning;
        this.favorite = favorite;
        this.wordType = wordType;
        this.pronouce = pronouce;
        this.word=word;

    }
    public Word1(int id, String english_meaning, String hindi_meaning, boolean favorite) {
        this.id = id;
        this.english_meaning = english_meaning;
        this.hindi_meaning = hindi_meaning;
        this.favorite = favorite;


    }

    public Word1(String english, String hindi_meaning, boolean favorite) {
        this.english_meaning = english;
        this.hindi_meaning = hindi_meaning;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(zeroIfNull(id));
        dest.writeString(blankIfNull(english_meaning));
        dest.writeString(blankIfNull(hindi_meaning));
        dest.writeString(blankIfNull(wordType));
        dest.writeString(blankIfNull(word));
        dest.writeString(blankIfNull(pronouce));
        dest.writeInt(favorite ? 1 : 0);
    }

    public static final Creator<Word1> CREATOR = new Creator<Word1>() {
        @Override
        public Word1[] newArray(int paramInt) {
            return new Word1[paramInt];
        }

        @Override
        public Word1 createFromParcel(Parcel in) {
            return new Word1(in);
        }
    };

    private Word1(Parcel in) {
        id= in.readInt();
        english_meaning= in.readString();
        hindi_meaning= in.readString();

        wordType= in.readString();
        word= in.readString();
        pronouce= in.readString();
        favorite = in.readInt() == 1 ? true : false;
    }

    public static String blankIfNull(String toCheck) {
        return ifNull(toCheck, "");
    }

    public static String ifNull(String toCheck, String ifNull) {
        return toCheck == null ? ifNull : toCheck;
    }

    public static Integer zeroIfNull(Integer toCheck) {
        return ifNull(toCheck, 0);
    }
    public static Integer ifNull(Integer toCheck, Integer ifNull) {
        return toCheck == null ? ifNull : toCheck;
    }
}
