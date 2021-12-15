package com.example.breakbrickerdevolution;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.graphics.drawable.Drawable;

public class GameRules {

    //STATIC FIELDS
    private static int screenWidthX;
    private static int screenHeightY;

    //GAME OBJECTS
    private Paddle paddle;
    private Ball ball;
    private Brick[] bricks = new Brick[150];//MAX BRICKS 150

    //GENERAL PRIVATE MEMBER VARIABLES
    private Canvas canvas;
    private Paint paint;
    private Point size;
    private Thread gameThread = null;
    private volatile boolean active;
    private boolean paused = true;//Is game paused
    private long frameRate;
    private long frameTime;
    private int numBricks = 0;
    private int lives = 5;
    private int brickColumns = 8;
    private int brickRows = 3;
    private int score = 0;

    ////////////////////////////////////////////////////////////////
    //MAIN GAME METHODS

    //RESET GAME
    public void resetGame(){
        ball.reset(screenWidthX, screenHeightY);
        numBricks = 0;
        //Check the lives and reset
        if(lives == 0){
            lives = 5;
            score = 0;
        }
        //Loop through and set up bricks
        for (int column = 0; column < brickColumns; column++) {
            for (int row = 0; row < brickRows; row++) {
                bricks[numBricks] = new Brick(row, column, (screenWidthX/8), (screenHeightY/10));
                numBricks++;
            }
        }
    }

    //BALL-BRICK COLLISION
    public void collisionBallBrick(){
        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {
                if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                    bricks[i].setInvisible();
                    ball.reverseYVelocity();
                    score = score + 10;
                }
            }
        }
    }

    // BALL-PADDLE COLLISION
    public void collisionBallPaddle(){
        if (RectF.intersects(paddle.getRect(), ball.getRect())) {
            ball.setRandomXVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY(paddle.getRect().top - 2);
        }
    }

    // BALL-DEADZONE COLLISION
    public void ballHitsDeadZone(){
        if (ball.getRect().bottom > screenHeightY) {
            ball.reverseYVelocity();
            ball.clearObstacleY(screenHeightY - 2);
            lives--;
            if (lives == 0) {
                setPaused(true);
                resetGame();
            }
        }
    }

    // BALL-WALL COLLISION
    public void bounceOffLegalWalls(){
        // Ball Hits top
        if (ball.getRect().top < 0) {
            ball.reverseYVelocity();
            ball.clearObstacleY(12);
        }
        // Ball hits left wall
        if (ball.getRect().left < 0){
            ball.reverseXVelocity();
            ball.clearObstacleX(2);
        }
        // Ball hits right wall
        if (ball.getRect().right > screenWidthX - 10) {
            ball.reverseXVelocity();
            ball.clearObstacleX(screenWidthX - 22);
        }
    }

    // TODO:COMMENT
    public void checkMaxScore(){
        if (score >= numBricks * 10){
            paused = true;
            resetGame();
        }
    }

    // DRAWS BRICKS
    public void drawBricks(){
        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {
                drawOnCanvas(bricks[i].getRect(), paint);
            }
        }
    }
    // CHECKS IF USER WON OR LOST
    public void checkWinLoss(){
        // Win
        if (score == numBricks * 10) {
            setPaintTextSize(90);
            drawTextOnCanvas("YOU HAVE WON!", 10, screenHeightY / 2, paint);
        }
        // Loss
        if (lives <= 0) {
            setPaintTextSize(90);
            drawTextOnCanvas("YOU HAVE LOST!", 10, screenHeightY / 2, paint);
        }
    }

    ////////////////////////////////////////////////////////////////
    //HELPER METHODS

    //BALL
    // Creates Ball
    // @param x: x value for size of ball
    // @param y: y value for size of ball
    public void createBall(int x, int y){
        ball = new Ball(x, y);
    }

    // updates location of ball
    public void updateBall(){
        ball.update(frameRate);
    }

    // getter for ball
    // @return returns ball object from game
    public Ball getBall(){
        return ball;
    }

    //PADDLE
    // TODO: Comment createPaddle
    public void createPaddle(int x, int y){
        paddle = new Paddle(x, y);
    }

    // updates location of paddle
    public void updatePaddle(){
        paddle.update(getFrameRate());
    }

    // getter for paddle rectangle
    // @return returns paddle rectangle from game
    public RectF getPaddleRect(){
        return paddle.getRect();
    }

    public void Paddle(int direction){
        paddle.setMovementState(direction);
    }

    // getter for paddle object
    // @return returns paddle object
    public Paddle getPaddle() {
        return paddle;
    }

    // moves paddle object
    // @param direction: direction that paddle will move
    public void movePaddle(int direction){
        paddle.setMovementState(direction);
    }

    //PAINT
    // initializes paint object
    public void newPaint(){
        paint = new Paint();
    }
    // sets paint color
    // TODO: param comments for setPaintColor
    public void setPaintColorARGB(int a, int r, int g, int b){
        paint.setColor(Color.argb(a, r, g, b));
    }

    // setter for the paint text size
    // @param size: size of text that paints text size is set to
    public void setPaintTextSize(float size){
        paint.setTextSize(size);
    }

    // getter for paint object
    // @return returns the paint object
    public Paint getPaint(){
        return paint;
    }

    //CANVAS
    // locks canvas for drawing
    // TODO: param comment for lockCanvas
    public void lockCanvas(SurfaceHolder holder){
        canvas = holder.lockCanvas();
    }

    // draws a rectangle onto the canvas
    // @param rect: rectangle object to be drawn
    // @param paint: paint object for rectangle
    public void drawOnCanvas(RectF rect, Paint paint){
        canvas.drawRect(rect, paint);
    }

    // draws text onto screen
    // @param text: string value of text to be drawn
    // @param x: x value for start of text
    // @param y: y value for start of text
    // @param paint: paint object for drawing text
    public void drawTextOnCanvas(String text, int x, int y, Paint paint){
        canvas.drawText(text, x, y, paint);
    }

    // getter for the canvas object
    // @return returns the canvas object
    public Canvas getCanvas() {
        return canvas;
    }

    //PAUSED: for within the activity
    // true if game is paused and false otherwise
    // @return returns true or false based on paused state
    public boolean isPaused() {
        return paused;
    }

    // setter for paused variable
    // @param paused: what paused variable is set to
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    //GAME THREAD: for the activity itself
    public void newGameThread(Thread gameThread){
        this.gameThread = gameThread;
    }

    public void startGameThread(){
        gameThread.start();
    }

    public void joinGameThread() throws InterruptedException {
        gameThread.join();
    }

    //ACTIVE: For when the game is running or not
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    //FRAMERATE
    public long getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(long framerate) {
        this.frameRate = framerate;
    }

    //FRAME TIME
    public long getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }

    //SCREEN DIMENSIONS
    public static int getScreenWidthX() {
        return screenWidthX;
    }

    public static void setScreenWidthX(int screenWidthX) {
        GameRules.screenWidthX = screenWidthX;
    }

    public static int getScreenHeightY() {
        return screenHeightY;
    }

    public static void setScreenHeightY(int screenHeightY) {
        GameRules.screenHeightY = screenHeightY;
    }

    //POINT
    public void createPoint(){
        size = new Point();
    }

    public Point getPoint(){
        return size;
    }

    //SCORE
    public int getScore() {
        return score;
    }

    //GeT LIVES
    public int getLives() {
        return lives;
    }

}
