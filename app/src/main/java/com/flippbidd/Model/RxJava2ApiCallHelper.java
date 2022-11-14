package com.flippbidd.Model;


import io.reactivex.Observable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public final class RxJava2ApiCallHelper {

    private RxJava2ApiCallHelper() {
    }

    public static <T> Disposable call(Observable<T> observable, final RxJava2ApiCallback<T> rxJava2ApiCallback) {

        if (observable == null) {
            throw new IllegalArgumentException("Observable must not be null.");
        }

        if (rxJava2ApiCallback == null) {
            throw new IllegalArgumentException("Callback must not be null.");
        }

        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(@NonNull T t) throws Exception {
                        rxJava2ApiCallback.onSuccess(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (throwable != null) {
//                            rxJava2ApiCallback.onFailed(new Exception("Refresh in progress…"));
                            rxJava2ApiCallback.onFailed(throwable);
                        } else {
                            rxJava2ApiCallback.onFailed(new Exception("Something went wrong"));
//                            rxJava2ApiCallback.onFailed(new Exception("Refresh in progress…"));
                        }
                    }
                });
    }
}
