package com.example.breakbrickerdevolution;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Platform {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our platform will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speed that the paddle will move
    private float platformSpeed;

    // Which ways can the platform move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    //TODO


    // Is the platform moving and in which direction
    private int platformMoving = STOPPED;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    // @param screenX: x value of length of screen
    // @param screen y: y value of length of screen
    public Platform(int screenX, int screenY){
        // 130 pixels wide and 20 pixels high
        length = 200;
        height = 50;

        // Start platform in roughly the screen centre
        x = screenX / 2;
        //y = screenY - 20;
        y = screenY - 100;

        rect = new RectF(x, y, x + length, y + height);


        // How fast is the platform in pixels per second
        platformSpeed = 349;
    }

    // This is a getter method to make the rectangle that
    // defines our platform available in BreakoutView class
    // @return returns a rectF rectangle to use as the platform
    public RectF getRect(){
        return rect;
    }

    // This method will be used to change/set if the platform is going left, right or nowhere
    // @param state: int value to set direction of platform
    public void setMovementState(int state){
        platformMoving = state;
    }

    public boolean inBoundsLeft(){
        if(x > 0)
            return true;
        else
            return false;
    }

    public boolean inBoundsRight(int screenX){
        if(x < (screenX-length))
            return true;
        else
            return false;
    }

    // This update method will be called from update in BreakoutView
    // It determines if the platform needs to move and changes the coordinates
    // contained in rect if necessary
    // @param fps: TODO: comment this
    public void update(long fps){
        if(platformMoving == LEFT){
            x = x - platformSpeed / fps;
        }

        if(platformMoving == RIGHT){
            x = x + platformSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

}
