package com.elamblakatt.dict_eng_malayalam.notes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jasmine Nitin on 9/15/2017.
 */

public class Note implements Parcelable
{
    private String title;
    private String content;
    private Long creation;
    private Long lastModification;
    private String createdDate;
    private int id;
    private boolean expanded;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getTitle());
        parcel.writeString(getContent());
        parcel.writeString(getCreatedDate());
        parcel.writeInt(getId());
       // parcel.writeLong(getCreation());
        //parcel.writeLong(getLastModification());

    }
    /*
     * Parcelable interface must also have a static field called CREATOR, which is an object implementing the
     * Parcelable.Creator interface. Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Creator<Note> CREATOR = new Creator<Note>() {

        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }


        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public Note()
    {

    }
    private Note(Parcel in) {
        setTitle(in.readString());
        setContent(in.readString());
        setCreatedDate(in.readString());
        setId(in.readInt());

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreation() {
        return creation;
    }

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    public Long getLastModification() {
        return lastModification;
    }

    public void setLastModification(Long lastModification) {
        this.lastModification = lastModification;
    }

}
