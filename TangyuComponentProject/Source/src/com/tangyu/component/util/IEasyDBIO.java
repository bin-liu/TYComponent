package com.tangyu.component.util;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

public interface IEasyDBIO {

    public static EasyGetContentValue<?> GetCtnValue = null;

    public static EasyGetData<?> GetData = null;

    public abstract class EasyGetContentValue<T> {
        public abstract ContentValues getContentValues(T t);
    }

    public abstract class EasyGetData<T extends Object> {
        public abstract T getData(Cursor c);

        public abstract List<T> getDatas(Cursor c);
    }
}
