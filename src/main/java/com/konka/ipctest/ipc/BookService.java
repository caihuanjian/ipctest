package com.konka.ipctest.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by HwanJ.Choi on 2018-7-5.
 */
public class BookService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BinderReal();
    }
}
