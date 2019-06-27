package com.meng.picTools.libAndHelper.javaBean.allPics;

import com.google.gson.annotations.SerializedName;

public class BookmarkCount {
    @SerializedName("public")
    public Public _public = new Public();
    @SerializedName("private")
    public Private _private = new Private();
}
