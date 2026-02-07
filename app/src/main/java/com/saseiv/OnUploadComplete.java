package com.saseiv;

public interface OnUploadComplete {
    void onSuccess(String url);
    void onError();
}
