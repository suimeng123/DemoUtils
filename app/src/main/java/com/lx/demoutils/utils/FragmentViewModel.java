package com.lx.demoutils.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * com.lx.demoutils.utils
 * DemoUtils
 * Created by lixiao2
 * 2018/9/30.
 */

public class FragmentViewModel extends ViewModel {

    public MutableLiveData<String> liveData;

    public void setData(String str) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        liveData.setValue(str);
    }

    public LiveData<String> getData() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        liveData = null;
    }
}
