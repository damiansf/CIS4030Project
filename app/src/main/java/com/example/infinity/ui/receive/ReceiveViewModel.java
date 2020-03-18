package com.example.infinity.ui.receive;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReceiveViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ReceiveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("QuickLink Receive Holder");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
