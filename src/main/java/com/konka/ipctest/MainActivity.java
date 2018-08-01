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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.konka.ipctest.ipc.BinderReal;
import com.konka.ipctest.ipc.Book;
import com.konka.ipctest.ipc.BookService;
import com.konka.ipctest.ipc.IBookManager;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private IBinder remoteAddService;

    private IBookManager bookManager;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
