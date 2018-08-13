package com.konka.ipctest.ipc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HwanJ.Choi on 2018-7-5.
 * 远程binder实体
 */
public class BinderReal extends Binder implements IBookManager {

    private static final String DESCRIPTION = "IBookManager";
    static final int ID_METHOD_ADD = Binder.FIRST_CALL_TRANSACTION;
    static final int ID_METHOD_GETLIST = Binder.FIRST_CALL_TRANSACTION + 1;
    private static CopyOnWriteArrayList<Book> sBooks = new CopyOnWriteArrayList<>();

    public BinderReal() {
        /**
         * Convenience method for associating a specific interface with the Binder.
         * After calling, queryLocalInterface() will be implemented for you
         * to return the given owner IInterface when the corresponding
         * descriptor is requested.
         */
        this.attachInterface(this, DESCRIPTION);
    }

    @Override
    public void addBook(Book book) {

        if (book != null) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(book);
            realm.commitTransaction();
            Log.d("addbook:", book.toString());
        }
    }

    @Override
    public List<Book> getBookList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Book> books = realm.where(Book.class).findAll();
        return books;
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case Binder.INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTION);
                return true;
            case ID_METHOD_ADD:
                data.enforceInterface(DESCRIPTION);//进行某种校验，客户端代码必须调用data.writeInterfaceToken(DESCRIPTION);
                int check = data.readInt();
                Book arg;
                if (check == 0) {
                    arg = null;
                } else {
                    arg = Book.CREATOR.createFromParcel(data);
                }
                addBook(arg);
                if (reply != null) {
                    reply.writeNoException();
                }
                return true;
            case ID_METHOD_GETLIST:
                data.enforceInterface(DESCRIPTION);
                if (reply != null) {
                    reply.writeNoException();
                    reply.writeTypedList(getBookList());
                }
                return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    public static IBookManager asInterface(IBinder obj) {
        if (obj == null)
            return null;
        /*
            试着查找本地的binder，找不到说明跨进程，需要返回一个binder代理对象
            obj需调用attachInterface建立与IBookManager的关联
         */
        IInterface iInterface = obj.queryLocalInterface(DESCRIPTION);
        if (iInterface != null && iInterface instanceof IBookManager) {
            return (IBookManager) iInterface;
        } else {
            return new BinderProxy(obj);
        }
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    private static class BinderProxy implements IBookManager {

        private IBinder mRemote;

        BinderProxy(IBinder binder) {
            mRemote = binder;
        }

        /**
         * 这里的代码还是在客户端线程执行
         */
        @Override
        public void addBook(Book book) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInterfaceToken(DESCRIPTION);
            try {
                if (book == null) {
                    data.writeInt(0);
                } else {
                    data.writeInt(1);
                    book.writeToParcel(data, 0);
                }
                mRemote.transact(ID_METHOD_ADD, data, reply, Binder.FLAG_ONEWAY);//远程调用
                reply.readException();
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                data.recycle();
                reply.recycle();
            }

        }

        @Override
        public List<Book> getBookList() {
            Parcel reply = Parcel.obtain();
            Parcel data = Parcel.obtain();
            List<Book> result = null;
            data.writeInterfaceToken(DESCRIPTION);
            try {
                mRemote.transact(ID_METHOD_GETLIST, data, reply, 0);//阻塞直到结果返回
                reply.readException();//因为远程调用往reply写result前,调用了reply.writeNoException();所以这里读结果前要先readException()
                result = reply.createTypedArrayList(Book.CREATOR);
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return result;
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }
}
