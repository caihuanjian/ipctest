package com.konka.ipctest.ipc;

import android.os.IInterface;

import java.util.List;

/**
 * Created by HwanJ.Choi on 2018-7-5.
 */
public interface IBookManager extends IInterface {

    void addBook(Book book);

    List<Book> getBookList();
}
