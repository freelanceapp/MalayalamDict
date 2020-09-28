package com.elamblakatt.dict_eng_malayalam.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Word implements Parcelable {
    private int eng_id=0;

    public int getMl_id() {
        return ml_id;
    }

    public void setMl_id(int ml_id) {
        this.ml_id = ml_id;
    }

    private int ml_id=0;
    private String word;
    private String pronouce;
    private String english_meaning;
    private String hindi_meaning;
    private String wordType;
    boolean favorite;

    public Word() {
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

    public Word(int eng_id, String english_meaning, String hindi_meaning,String wordType, String pronouce,String word, boolean favorite) {
        this.eng_id = eng_id;
        this.english_meaning = english_meaning;
        this.hindi_meaning = hindi_meaning;
        this.favorite = favorite;
        this.wordType = wordType;
        this.pronouce = pronouce;
        this.word=word;

    }
    public Word(int eng_id, String english_meaning, boolean favorite) {
        this.eng_id = eng_id;
        this.english_meaning = english_meaning;
      //  this.hindi_meaning = hindi_meaning;
        this.favorite = favorite;


    }
    public Word(int mal_id, String english_meaning) {
        this.ml_id = mal_id;
        this.english_meaning = english_meaning;


    }

    public Word(int eng_id,int mal_id, String english_meaning) {
        this.eng_id = eng_id;
        this.ml_id = mal_id;
        this.english_meaning = english_meaning;
   }
    public Word(int eng_id, String english_meaning,String hindi_meaning, boolean favorite) {
        this.eng_id = eng_id;
        this.english_meaning = english_meaning;
          this.hindi_meaning = hindi_meaning;
        this.favorite = favorite;


    }


    public Word(String english, String hindi_meaning, boolean favorite) {
        this.english_meaning = english;
        this.hindi_meaning = hindi_meaning;
        this.favorite = favorite;
    }

    public int getEngId() {
        return eng_id;
    }

    public void setEngId(int id) {
        this.eng_id = id;
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
        dest.writeInt(zeroIfNull(eng_id));
        dest.writeInt(zeroIfNull(ml_id));
        dest.writeString(blankIfNull(english_meaning));
        dest.writeString(blankIfNull(hindi_meaning));
        dest.writeString(blankIfNull(wordType));
        dest.writeString(blankIfNull(word));
        dest.writeString(blankIfNull(pronouce));
        dest.writeInt(favorite ? 1 : 0);
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word[] newArray(int paramInt) {
            return new Word[paramInt];
        }

        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }
    };

    private Word(Parcel in) {
        eng_id= in.readInt();
        ml_id=in.readInt();
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
