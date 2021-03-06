package com.example.breakbrickerdevolution;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class BreakoutGame extends Activity {

    Context mainActivity;
    BreakoutView breakoutView;
    ArrayList<Bitmap> bMaps = new ArrayList<Bitmap>();
    GameRules game = new GameRules(BreakoutGame.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize gameView and set it as the view
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
        createAssets();

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
            game.createPlatform(game.getScreenWidthX(), game.getScreenHeightY());
            game.createBall(game.getScreenWidthX(), game.getScreenHeightY());
            game.resetGame();
        }

        @Override
        public void run() {
            while (game.isActive() == true) {
                long startFrameTime = System.currentTimeMillis();
                if (game.isPaused() == false)// Update the frame
                    update();
                draw();
                game.setFrameTime(System.currentTimeMillis() - startFrameTime);
                if (game.getFrameTime() >= 1)
                    game.setFrameRate(1000 / game.getFrameTime());
            }
        }

        public void update() {
            // Move the paddle if required
            game.updatePlatform();
            game.updateBall();

            // Check for ball colliding with a tile
            game.collisionBallTile();
            game.collisionBallPlatform();
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
                game.drawOnCanvas(game.getPlatformRect(), game.getPaint(), bMaps.get(0));
                // Draw the ball
                game.drawOnCanvas(game.getBall().getRect(), game.getPaint(), null);
                // Change the brush color for drawing
                game.setPaintColorARGB(255, 249, 129, 0);
                // Draw the tiles if visible
                game.drawTiles(bMaps.get(1));
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
                    if (motionEvent.getX() > game.getScreenWidthX() / 2)
                        game.movePlatform(game.getPlatform().RIGHT);
                    else
                        game.movePlatform(game.getPlatform().LEFT);
                    break;
                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    game.movePlatform(game.getPlatform().STOPPED);
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

    public void createAssets(){
        //https://jamiecross.itch.io/breakout-brick-breaker-game-tile-set-free
        bMaps.add(BitmapFactory.decodeResource(getResources(),R.drawable.platform));
        bMaps.add(BitmapFactory.decodeResource(getResources(),R.drawable.brick));
        bMaps.add(BitmapFactory.decodeResource(getResources(),R.drawable.ball));

    }

}
// This is the end of the BreakoutGame class