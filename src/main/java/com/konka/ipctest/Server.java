package com.konka.ipctest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by HwanJ.Choi on 2018-7-5.
 */
public class Server extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServerBinder();
    }


    static class ServerBinder extends Binder {
        private static final int ADD_CODE = 0X0001;

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case ADD_CODE:
                    Log.d("chj", "thread：" + Thread.currentThread().getName());
                    int num1 = data.readInt();
                    int num2 = data.readInt();
                    if (reply != null) {
                        reply.writeInt(add(num1, num2));
                    }
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private int add(int a, int b) {
            int id1 = App.getInstance().getID();
            Log.d("chj", "id in server before set:" + id1);
            App.getInstance().setID(20);
            int id = App.getInstance().getID();
            Log.d("chj", "id in server after set:" + id);
            return a + b;
        }
    }
}
