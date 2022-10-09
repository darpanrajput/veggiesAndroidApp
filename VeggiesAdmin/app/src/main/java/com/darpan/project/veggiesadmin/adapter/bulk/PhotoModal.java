package com.darpan.project.veggiesadmin.adapter.bulk;

import android.net.Uri;

public class PhotoModal {
    private Uri uri;

    public PhotoModal(Uri uri) {
        this.uri = uri;
    }

    public PhotoModal() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
