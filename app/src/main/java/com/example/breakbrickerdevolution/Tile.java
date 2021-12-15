package com.example.breakbrickerdevolution;

import android.graphics.RectF;

public class Tile {

    private RectF rect;
    private boolean isVisible;

    public Tile(int row, int column, int width, int height){

        isVisible = true;

        int indent = 1;
        rect = new RectF(column * width + indent,
                row * height + indent,
                column * width + width - indent,
                row * height + height - indent);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}
