package com.konka.ipctest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.konka.ipctest.ipc.BinderReal;
import com.konka.ipctest.ipc.Book;
import com.konka.ipctest.ipc.BookService;
import com.konka.ipctest.ipc.IBookManager;

import java.util.List;
import java.util.Random;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private IBinder remoteAddService;

    private IBookManager bookManager;
    private Random random = new Random();
    private RecyclerView mRecycleView;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecycleView = findViewById(R.id.rv);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter();
        mRecycleView.setAdapter(bookAdapter);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                remoteAddService = service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, Server.class), serviceConnection, Context.BIND_AUTO_CREATE);


        ServiceConnection serviceConnection1 = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bookManager = BinderReal.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, BookService.class), serviceConnection1, BIND_AUTO_CREATE);
    }

    private String[] books = new String[]{"effective java", "设计模式", "算法4"};

    public void add(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();

                data.writeInt(4);
                data.writeInt(3);

                try {
                    int id = App.getInstance().getID();
                    Log.d("chj", "ID in main:" + id);
                    remoteAddService.transact(1, data, reply, 0);
                    Toast.makeText(this, "result :" + reply.readInt(), Toast.LENGTH_SHORT).show();
                    Log.d("chj", "id in main after server set:" + App.getInstance().getID());
                } catch (RemoteException e) {
                    e.printStackTrace();
                } finally {
                    data.recycle();
                    reply.recycle();
                }
                break;
            case R.id.btn2:
                bookManager.addBook(new Book(random.nextInt(Integer.MAX_VALUE), books[random.nextInt(books.length)], "hha"));
                break;
            case R.id.btn3:
                List<Book> bookList = bookManager.getBookList();
                if (bookList != null)
                    Toast.makeText(this, bookList.toString(), Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Book> all = realm.where(Book.class).findAll().sort("time");
        bookAdapter.setData(all);
        all.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Book>>() {
            @Override
            public void onChange(RealmResults<Book> books, OrderedCollectionChangeSet changeSet) {
//                bookAdapter.notifyDataSetChanged();
                books.sort("time");
                OrderedCollectionChangeSet.Range[] deletionRanges = changeSet.getDeletionRanges();
                for (OrderedCollectionChangeSet.Range r : deletionRanges) {
                    bookAdapter.notifyItemRangeRemoved(r.startIndex, r.length);
                    Log.d("chj", "delete:" + r.startIndex + ":length" + r.length);
                }
                int[] insertions = changeSet.getInsertions();
                for (Integer i : insertions) {
                    Log.d("chj", "insert:" + i + ":length" );
                    bookAdapter.notifyItemInserted(i);
                }
                OrderedCollectionChangeSet.Range[] changeRanges = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : changeRanges) {
                    bookAdapter.notifyItemRangeChanged(range.startIndex, range.length);
                    Log.d("chj", "changeRanges:" + range.startIndex + ":length" + range.length);
                }
            }

        });
    }
}
