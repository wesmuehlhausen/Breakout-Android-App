package com.example.breakbrickerdevolution;

import android.graphics.RectF;

public class Paddle {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our paddle will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speed that the paddle will move
    private float paddleSpeed;

    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the paddle moving and in which direction
    private int paddleMoving = STOPPED;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    // @param screenX: x value of length of screen
    // @param screen y: y value of length of screen
    public Paddle(int screenX, int screenY){
        // 130 pixels wide and 20 pixels high
        length = 130;
        height = 20;

        // Start paddle in roughly the screen centre
        x = screenX / 2;
        //y = screenY - 20;
        y = screenY / 2;

        rect = new RectF(x, y, x + length, y + height);

        // How fast is the paddle in pixels per second
        paddleSpeed = 350;
    }

    // This is a getter method to make the rectangle that
    // defines our paddle available in BreakoutView class
    // @return returns a rectF rectangle to use as the paddle
    public RectF getRect(){
        return rect;
    }

    // This method will be used to change/set if the paddle is going left, right or nowhere
    // @param state: int value to set direction of paddle
    public void setMovementState(int state){
        paddleMoving = state;
    }

    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary
    // @param fps: TODO: comment this
    public void update(long fps){
        if(paddleMoving == LEFT){
            x = x - paddleSpeed / fps;
        }

        if(paddleMoving == RIGHT){
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

}
