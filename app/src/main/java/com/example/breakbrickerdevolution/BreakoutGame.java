package com.example.breakbrickerdevolution;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BreakoutGame extends Activity {

    BreakoutView breakoutView;
    GameRules game = new GameRules();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize gameView and set it as the view
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    class BreakoutView extends SurfaceView implements Runnable {

        SurfaceHolder holder;

        public BreakoutView(Context context) {
            super(context);
            holder = getHolder();
            game.newPaint();
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();

            display.getSize(size);
            game.setScreenWidthX(size.x);
            game.setScreenHeightY(size.y);
            game.createPaddle(game.getScreenWidthX(), game.getScreenHeightY());
            game.createBall(game.getScreenWidthX(), game.getScreenHeightY());
            game.resetGame();
        }

        @Override
        public void run() {
            while (game.isActive() == true) {
                long startFrameTime = System.currentTimeMillis();
                // Update the frame
                if (game.isPaused() == false)
                    update();
                draw();
                game.setFrameTime(System.currentTimeMillis() - startFrameTime);
                if (game.getFrameTime() >= 1)
                    game.setFrameRate(1000 / game.getFrameTime());
            }

        }

        public void update() {
            // Move the paddle if required
            game.updatePaddle();
            game.updateBall();

            // Check for ball colliding with a brick
            game.collisionBallBrick();
            game.collisionBallPaddle();
            game.ballHitsDeadZone();
            game.bounceOffLegalWalls();
            game.checkMaxScore();
        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (holder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                game.lockCanvas(holder);
                // Draw the background color
                drawBackground();
                // Choose the brush color for drawing
                game.setPaintColorARGB(255, 255, 255, 255);
                // Draw the paddle
                game.drawOnCanvas(game.getPaddleRect(), game.getPaint());
                // Draw the ball
                game.drawOnCanvas(game.getBall().getRect(), game.getPaint());
                // Change the brush color for drawing
                game.setPaintColorARGB(255, 249, 129, 0);
                // Draw the bricks if visible
                game.drawBricks();
                // Choose the brush color for drawing
                game.setPaintColorARGB(255, 255, 255, 255);
                // Draw the score
                game.setPaintTextSize(40);
                game.drawTextOnCanvas("Score: " + game.getScore() + "   Lives: " + game.getLives(), 10, 50, game.getPaint());
                game.checkWinLoss();
                // Draw everything to the screen
                holder.unlockCanvasAndPost(game.getCanvas());
            }
        }

        public void drawBackground(){
            Drawable d = getResources().getDrawable(R.drawable.purple_background, null);
            d.setBounds(0, 0, game.getScreenWidthX(), game.getScreenHeightY());
            d.draw(game.getCanvas());
        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            game.setActive(false);
            try {
                game.joinGameThread();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            game.setActive(true);
            game.newGameThread(new Thread(this));
            game.startGameThread();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    game.setPaused(false);
                    if (motionEvent.getX() > game.getScreenWidthX() / 2) {
                        game.movePaddle(game.getPaddle().RIGHT);
                    }
                    else
                    {
                        game.movePaddle(game.getPaddle().LEFT);
                    }

                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    game.movePaddle(game.getPaddle().STOPPED);
                    break;
            }

            return true;
        }

    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        breakoutView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        breakoutView.pause();
    }

}
// This is the end of the BreakoutGame class