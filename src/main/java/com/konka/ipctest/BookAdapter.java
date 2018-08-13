package com.konka.ipctest;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konka.ipctest.ipc.Book;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HwanJ.Choi on 2018-8-10.
 */
public class BookAdapter extends RecyclerView.Adapter {

    private RealmResults<Book> mDatas;

    public void setData(RealmResults<Book> list) {
        if (list == null)
            return;
        mDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setTextSize(16);
        textView.setTextColor(Color.BLUE);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        return new BookHodler(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        Book book = mDatas.get(position);
        textView.setText(book.toString());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        book.deleteFromRealm();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class BookHodler extends RecyclerView.ViewHolder {

        public BookHodler(View itemView) {
            super(itemView);

        }
    }
}
