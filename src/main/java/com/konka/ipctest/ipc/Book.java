package com.konka.ipctest.ipc;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HwanJ.Choi on 2018-7-5.
 */
public class Book extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;
    private String name;
    private String author;

    private long time;

    public Book() {

    }

    public Book(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
        time = System.currentTimeMillis();
    }

    protected Book(Parcel in) {
        id = in.readInt();
        name = in.readString();
        author = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(author);
    }

    @Override
    public String toString() {
        return "[id:" + id + ",name:" + name + ",author:" + author + "]";
    }

    public int getId() {
        return id;
    }
}
