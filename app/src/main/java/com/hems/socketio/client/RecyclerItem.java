package com.hems.socketio.client;

public class RecyclerItem {
    int image;
    String title;

    public RecyclerItem(int image, String title){
        this.image=image;
        this.title=title;
    }

    private int getImage(){
        return this.image;
    }

    private String getTitle(){
        return this.title;
    }


}
