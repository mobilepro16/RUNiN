package com.runin.runinapp.utils;

/**
 * Please document this class
 * Created by Usuario on 29/09/2016.
 */

public class RecyclerItemData {

    private final String title;
    private final int imageUrl;
    private final boolean showArrow;

    public RecyclerItemData(String title) {
        this.title = title;
        this.imageUrl = com.runin.runinapp.R.mipmap.boton_musica;
        this.showArrow = true;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public boolean getShowArrow() {
        return showArrow;
    }

}
