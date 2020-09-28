package com.elamblakatt.dict_eng_malayalam.Meaning;



import android.os.Parcel;
import android.os.Parcelable;


public class WordDetail implements Parcelable {
    private int id;
    private String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(String antonyms) {
        this.antonyms = antonyms;
    }

    public String getWord_type() {
        return word_type;
    }

    public void setWord_type(String word_type) {
        this.word_type = word_type;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }


    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    private String synonyms;
    private String antonyms;
    private String word_type;
    private String meaning;

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    private String example;

    public WordDetail() {
    }


    public WordDetail(String word, String synomyns, String antonnyms, String word_type, String meaning) {

        this.word=word;
        this.synonyms=synomyns;
        this.antonyms=antonnyms;
        this.word_type=word_type;
        this.meaning=meaning;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {



        dest.writeString(blankIfNull(word));
        dest.writeString(blankIfNull(synonyms));
        dest.writeString(blankIfNull(antonyms));
        dest.writeString(blankIfNull(word_type));
        dest.writeString(blankIfNull(meaning));

    }

    public static final Creator<WordDetail> CREATOR = new Creator<WordDetail>() {
        @Override
        public WordDetail[] newArray(int paramInt) {
            return new WordDetail[paramInt];
        }

        @Override
        public WordDetail createFromParcel(Parcel in) {
            return new WordDetail(in);
        }
    };

    private WordDetail(Parcel in) {

        word= in.readString();
        synonyms= in.readString();

        antonyms= in.readString();
        word_type= in.readString();
        meaning= in.readString();
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
