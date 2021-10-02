/*
 * Canvas3D.java
 *
 * Created on February 25, 2002, 5:43 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Point3f;
import java.applet.AudioClip;
import java.net.URL;
import java.awt.Toolkit;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.DirectColorModel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Event;
import java.util.Vector;

/**
 * A canvas for 3D scenes.
 *
 * Mouse interaction is handled by the Canvas3D.
 *
 * @author  Karl Traunmueller
 */ 

public class Canvas3D extends Canvas
                      implements MouseListener,
                                 MouseMotionListener,                                          
                                 KeyListener,
                                 ImageObserver,
                                 Runnable {

    static final int    PROGRESSBAR_WIDTH     = 100;
    static final int    PROGRESSBAR_HEIGHT    = 6;
    static final int    FRAMERATE_INTERVAL    = 500;
    static final int    TIMER_TICK            = 500;
    
    int                 mWidth, mHeight;
    int                 mProgressMax, mProgress;
    String              mStatusString;
    String              mTooltip;
    Font                mFont;
    SceneGraphRenderer  mRenderer;        
    Dimension           mOffscreenDimension;
    MemoryImageSource   mImageSource;    
    DirectColorModel    mColorModel;
    Image               mOffscreenImage;
    boolean             mFrameReady;
    int[]               mPixels;
    int[]               mSupersampledPixels;
    Dimension           mScreenDimension;    
    int                 mFramesPerInterval;
    long                mLastCheckPoint;
    float               mFrameRate;
    boolean             mEnableDisplayStats;
    Image               mLoadingImage;
    boolean             mShowLoadingScreen;
    int                 mLoadingScreenWidth, mLoadingScreenHeight;
    int                 mNrModels, mNrModelsLoaded;
    int                 mNrTextures, mNrTooltips, mNrImagesLoaded;
    boolean             mEnableTimer;
    Thread              mTimerThread;
    int                 mTick;
    java.awt.Color      mStatusColor;
    java.awt.Color      mBarColor;
    float               mScale;
    Point3f             mOrigin;
    float               mRotX, mRotY, mRotZ;
    Point               mMousePosition;
    int                 mTopPosition;
    int                 mBackPosition;
    int                 mPanelPosition;
    int                 mResetPosition;
    boolean             mMouseDown;
    Button              mAutoRotateLeftButton, mAutoRotateRightButton;
    Button              mRotateLeftButton, mRotateRightButton, mRotateUpButton, mRotateDownButton;
    Button              mPanLeftButton, mPanRightButton, mPanUpButton, mPanDownButton;
    Button              mZoomInButton, mZoomOutButton;
    Button              mAudioButton;
    Button              mActiveButton;
    Vector              mPositionButtons;
    Vector              mButtonLabels;
    AudioClip           mLoop;
    boolean             mShowUIButtons;
    
    /**
     *
     */
    public Canvas3D(int width, int height, int supersampling) {
        mFont = new Font("SansSerif", Font.PLAIN, 10);
        mRenderer = new SceneGraphRenderer(this);
        mRenderer.setSupersampling(supersampling);
        
        mWidth = width;
        mHeight = height;
        
        mColorModel = new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);
        
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);                
        
        mLastCheckPoint = System.currentTimeMillis();
        mShowLoadingScreen = true;
        
        mTimerThread = new Thread(this);
        
        mOrigin = new Point3f();
        mMousePosition = new Point();
        
        mPositionButtons = new Vector();
        mButtonLabels = new Vector();
    }

    public final void cleanUp() {
        stopRenderer();
        mRenderer.cleanUp();
        mImageSource = null;
        mOffscreenImage = null;
        mRenderer = null;
        mPixels = null;
        mSupersampledPixels = null;
        mLoadingImage = null;
    }
    
    public final void setLoadingScreen(URL loading) {        
        mLoadingImage = Toolkit.getDefaultToolkit().getImage(loading);
    }
    
    public final void setLoadingScreen(String loading) {
        URL url = null;
        try {
            url = new URL(loading);
        }
        catch (java.net.MalformedURLException ex) {
        }
        setLoadingScreen(url);
    }
    
    public final void setNrModels(int nrModels) {
        mNrModels = nrModels;
        mProgressMax = mNrModels + mNrTextures + mNrTooltips;
    }
    
    public final void setNrTextures(int nrTextures) {
        mNrTextures = nrTextures;
        mProgressMax = mNrModels + mNrTextures + mNrTooltips;
    }
    
    public final void setNrTooltips(int nrTooltips) {
        mNrTooltips = nrTooltips;
        mProgressMax = mNrModels + mNrTextures + mNrTooltips;
    }

    public final void setBackground(URL background) {
        mRenderer.setBackground(background);
    }
    
    public final void setBackground(String background) {
        URL url = null;
        try {
            url = new URL(background);
        }
        catch (java.net.MalformedURLException ex) {
        }
        setBackground(url);
    }
    
    public final void setPreferredRasterizer(int type) {
        mRenderer.setPreferredRasterizer(type);
    }

    public final void setScale(float scale) {        
        mScale = scale;
        Scene scene = mRenderer.getScene();        
        if (scene == null)
            return;        
        scene.setScale(scale);
    }
    
    public final void setOrigin(float x, float y, float z) {
        mOrigin.set(x, y, z);
        Scene scene = mRenderer.getScene();
        if (scene == null)
            return;
        scene.setOrigin(x, y, z);
        mRenderer.setPosAbs(x, y, z);
    }
    
    public final void setRotationOrigin(float rotX, float rotY, float rotZ) {
        mRotX = rotX;
        mRotY = rotY;
        mRotZ = rotZ;
    }
    
    public final void rotate(float rotX, float rotY, float rotZ) {
        Scene scene = mRenderer.getScene();
        if (scene == null)
            return;
        scene.rotate(rotX, rotY, rotZ);
        mRenderer.setRotAbs(rotX, rotY, rotZ);
    }

    public final void reset() {
        Scene scene = mRenderer.getScene();
        if (scene == null)
            return;
        scene.resetTransform();
        scene.setScale(mScale);
        scene.setOrigin(mOrigin.x, mOrigin.y, mOrigin.z);
        scene.rotate(mRotX, mRotY, mRotZ);
    }
    
    public final void enableMouseRotate(boolean rotX, boolean rotY, boolean rotZ) {
        mRenderer.enableMouseRotate(rotX, rotY, rotZ);
    }
    
    public final void enableMouseMove(boolean moveX, boolean moveY, boolean moveZ) {
        mRenderer.enableMouseMove(moveX, moveY, moveZ);
    }
    
    public final void enableDisplayStats(boolean enable) {
        mEnableDisplayStats = enable;
    }
    
    public final void enableZoom(boolean enable) {
        mRenderer.enableZoom(enable);
    }
    
    public final void limitZoom(boolean limit) {
        mRenderer.limitZoom(limit);
    }
    
    public final void setZoomRange(float min, float max) {
        mRenderer.setZoomRange(min, max);
    }
    
    public final void limitRotX(boolean limit, float min, float max) {
        mRenderer.limitRotX(limit, min, max);
    }        
    
    public final void limitRotY(boolean limit, float min, float max) {
        mRenderer.limitRotY(limit, min, max);
    }  
    
    public final void limitRotZ(boolean limit, float min, float max) {
        mRenderer.limitRotZ(limit, min, max);
    }  
    
    public final void setColor(String name, int r, int g, int b) {
        mRenderer.setColor(name, r, g, b);
    }
    
    public final void enableAutoRotate(boolean enable) {
        mRenderer.enableAutoRotate(enable, true);
    }
    
    public final void disableAutoRotate() {
        mRenderer.disableAutoRotate();
    }
    
    public final void enableAutoSuspend(boolean enable) {
        mRenderer.enableAutoSuspend(enable);
    }
    
    public final void addTooltip(URL tooltip, String mesh) {
        mRenderer.addTooltip(tooltip, mesh);
    }
    
    public final void addTooltip(String tooltip, String mesh) {
        URL url = null;
        try {
            url = new URL(tooltip);
        }
        catch (java.net.MalformedURLException ex) {
        }
        addTooltip(url, mesh);
    }
    
    public final void setStatusColor(java.awt.Color color) {
        mStatusColor = color;
    }
    
    public final void setBarColor(java.awt.Color color) {
        mBarColor = color;
    }
    
    public final void addAnimation(String name, int start, int end) {        
        mRenderer.getScene().addAnimation(name, start, end);
    }
    
    public final void playAnimation(String name) {
        mRenderer.getScene().playAnimation(name);
    }
    
    public final void setLoop(AudioClip loop) {
        mLoop = loop;
    }
    
    public final void loopOn() {
        if (mAudioButton != null)
            mAudioButton.setPressed(false);
        if (mLoop != null)
            mLoop.loop();
    }
    
    public final void loopOff() {
        if (mAudioButton != null)
            mAudioButton.setPressed(true);
        if (mLoop != null)
            mLoop.stop();
    }
    
    public final void addAutoRotateLeftButton(int left, int top, String normal, String pressed, Color mask) {
        mAutoRotateLeftButton = new Button(left, top, normal, pressed, mask);
    }

    public final void addAutoRotateRightButton(int left, int top, String normal, String pressed, Color mask) {
        mAutoRotateRightButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addRotateLeftButton(int left, int top, String normal, String pressed, Color mask) {
        mRotateLeftButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addRotateRightButton(int left, int top, String normal, String pressed, Color mask) {
        mRotateRightButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addRotateUpButton(int left, int top, String normal, String pressed, Color mask) {
        mRotateUpButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addRotateDownButton(int left, int top, String normal, String pressed, Color mask) {
        mRotateDownButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addPanLeftButton(int left, int top, String normal, String pressed, Color mask) {
        mPanLeftButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addPanRightButton(int left, int top, String normal, String pressed, Color mask) {
        mPanRightButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addPanUpButton(int left, int top, String normal, String pressed, Color mask) {
        mPanUpButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addPanDownButton(int left, int top, String normal, String pressed, Color mask) {
        mPanDownButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addZoomInButton(int left, int top, String normal, String pressed, Color mask) {
        mZoomInButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addZoomOutButton(int left, int top, String normal, String pressed, Color mask) {
        mZoomOutButton = new Button(left, top, normal, pressed, mask);
    }
    
    public final void addAudioButton(int left, int top, String normal, String pressed, Color mask) {
        mAudioButton = new Button(left, top, normal, pressed, mask);
        mAudioButton.setToggleButton(true);
    }
    
    public final void showUIButtons() {
        mShowUIButtons = true;
    }
    
    public final int addPositionButton(float x, float y, float z, float rotX, float rotY, float scale,
                                  int left, int top, String normal, String pressed, Color mask) {                              
        int pos = mRenderer.addPosition(x, y, z, rotX, rotY, scale);
        Button button = new Button(left, top, normal, pressed, mask);
        mPositionButtons.addElement(button);
        mRenderer.resumeRenderer();
        return pos;
    }
    
    public final void moveToPosition(int pos) {
        mRenderer.moveToPosition(pos);
    }
    
    public final void addButtonLabel(int left, int top, String normal, Color mask) {
        Button button = new Button(left, top, normal, normal, mask);
        mButtonLabels.addElement(button);
    }

    public final void resumeRenderer() {
        mRenderer.resumeRenderer();
    }
    
    /**
     *
     */
    public final void load(URL codebase, URL file) {
        startTimer();
        
        Loader3DS loader = new Loader3DS(this, codebase);
        try {
            loader.load(codebase, file, mRenderer.getScene());
        }
        catch (java.io.IOException ex) {
        }        
    }
    
    /**
     *
     */
    public final void loadZip(URL codebase, URL file) {
        startTimer();
        
        Loader3DS loader = new Loader3DS(this, codebase);
        try {
            loader.loadZip(codebase, file, mRenderer.getScene());
        }
        catch (java.io.IOException ex) {
        }        
    }        
    
    public final void loadZip(String codebase, String file) {
        URL cb = null;
        URL f = null;
        try {
            cb = new URL(codebase);
            f = new URL(file);
        }
        catch (java.net.MalformedURLException ex) {
        }
        loadZip(cb, f);
    }
    
    final void setRenderSpeedSlow() {
        mRenderer.setRenderSpeedSlow();
    }
    
    final void setRenderSpeedMax() {
        mRenderer.setRenderSpeedMax();
    }
    
    /**
     *
     */
    final void textureLoaded() {
        mNrImagesLoaded++;
        mTick = 0;
        mRenderer.resumeRenderer();
    }
    
    /**
     *
     */
    final void modelLoaded() {
        mNrModelsLoaded++;
        mTick = 0;
        Dimension d = getSize();
        mRenderer.resumeRenderer();
    }
        
    /**
     *
     */
    final void setTooltip(String tooltip) {
        mTooltip = tooltip;
    }
    
    /**
     *
     */
    final void showLoadingScreen(boolean show) {
        mShowLoadingScreen = show;
    }
    
    /**
     * Starts this canvas's renderer.
     */
    public synchronized void startRenderer() {
        if (mRenderer == null)
            return;        
        mShowLoadingScreen = false;
        mStatusString = null;
        mRenderer.startRenderer();
        //stopTimer();
    }
    
    /**
     * Stops this canvas's renderer.
     */
    public synchronized void stopRenderer() {
        if (mRenderer == null)
            return;
        mRenderer.stopRenderer();
        stopTimer();
    }
    
    // -------------------------------------------------------------------
    // SceneGraphRenderer access
    // -------------------------------------------------------------------
    
    /**
     * Retrieves the Canvas's scene graph renderer.
     */
    final SceneGraphRenderer getSceneGraphRenderer() {
        return mRenderer;
    }
    
    // -------------------------------------------------------------------
    // SceneGraphRendererClient interface
    // -------------------------------------------------------------------
    
    /**
     * Requests a repaint. The renderer waits until it's unlocked with a call to nextFrame().
     */
    final void frameReady() {
        mFrameReady = true;
        repaint();
    }
    
    /**
     * Returns the canvas's current dimension.
     */
    final Dimension getViewportDimension() {
        return getSize();
    }
    
    /**
     * Retrieves the clients pixel data array.
     */
    final int[] getPixels() {
        return mPixels;
    }
    
    /**
     * Retrieves the clients supersampled pixel data array.
     */
    final int[] getSupersampledPixels() {
        return mSupersampledPixels;
    }
        
    // -------------------------------------------------------------------
    // Canvas/Component interface
    // -------------------------------------------------------------------
    
    /**
     * Returns the preferred size.
     */
    public Dimension getPreferredSize() {
        return new Dimension(mWidth, mHeight);
    }
    
    /**
     * Updates the canvas.
     */
    public void update(Graphics g) {                
        Dimension d = getSize();                
        if (d.width == 0 || d.height == 0)
            return;
 
        if (mOffscreenImage == null || 
            d.width != mOffscreenDimension.width || 
            d.height != mOffscreenDimension.height) {

            mOffscreenDimension = d;

            mPixels = new int[d.width * d.height];            
            mImageSource = new MemoryImageSource(d.width, d.height, mPixels, 0, d.width);            
            mImageSource.setAnimated(true);
            mOffscreenImage = createImage(mImageSource);

            int supersampling = mRenderer.getSupersampling();
            mSupersampledPixels = new int[supersampling * supersampling * d.width * d.height];

            mRenderer.sizeChanged(d);
        }
        
        if (mShowLoadingScreen) {     
            int w = mLoadingImage != null ? mLoadingImage.getWidth(null) : 0;
            int h = mLoadingImage != null ? mLoadingImage.getHeight(null) : 0;
            if (w != d.width || h != d.height) {
                g.setColor(java.awt.Color.white);
                g.fillRect(0, 0, d.width, d.height);
            }
            
            if (mLoadingImage != null) {
                g.drawImage(mLoadingImage, (d.width - w) / 2, (d.height - h) / 2, this);
            }
        }                 
        
        if (mFrameReady) {
            drawButtons();
            mImageSource.newPixels(mPixels, mColorModel, 0, d.width);
            g.drawImage(mOffscreenImage, 0, 0, null);
        }            

        int progress = mNrModelsLoaded + mNrImagesLoaded;
        if (!mShowLoadingScreen && progress < mProgressMax) {
            if (progress > mProgressMax)
                progress = mProgressMax;
            if (mProgressMax == 0)
                mProgressMax = 1;
            
            int progressWidth = d.width - 60;            
            int tick = progress * progressWidth / mProgressMax + mTick;
            if (tick >= (progress + 1) * progressWidth / mProgressMax)
                tick = (progress + 1) * progressWidth / mProgressMax;
            if (tick >= progressWidth)
                tick = progressWidth;
            
            if (mStatusColor != null)
                g.setColor(mStatusColor);
            else
                g.setColor(java.awt.Color.black);
            
            if (mBarColor != null)
               g.setColor(mBarColor);
            g.fillRect(30, (d.height - PROGRESSBAR_HEIGHT) >> 1  , tick, PROGRESSBAR_HEIGHT);            
            g.setColor(mStatusColor);
            g.drawRect(30, (d.height - PROGRESSBAR_HEIGHT) >> 1, progressWidth, PROGRESSBAR_HEIGHT);

            /*
            String status = "Loading";
            for (int i = 0; i < mTick % 4; i++)
                status += ".";
            g.drawString(status, 30, d.height - 40);
             */
            //return;
        }
        
        long time = System.currentTimeMillis();                
        if (time - mLastCheckPoint > FRAMERATE_INTERVAL) {                    
            mFrameRate = (float)mFramesPerInterval * 1000.0f / (time - mLastCheckPoint);
            mLastCheckPoint = time;
            mFramesPerInterval = 0;                    
        }
        else {
            mFramesPerInterval++;
        }                 

        if (!mShowLoadingScreen && !mRenderer.mScene.mAllNormalsCalculated) {
            g.setColor(java.awt.Color.black);
            g.setFont(mFont);
            g.drawString("Smoothing model...", 10, d.height - 20);
        }
        
        if (mEnableDisplayStats) {
            g.setColor(java.awt.Color.black);
            g.setFont(mFont);
            g.drawString((int)(mFrameRate * 10.0f) / 10.0f + " frames/sec", 10, 20);                                
            if (mTooltip != null) {                    
                g.drawString(mTooltip, 200, 20);
            }
        }                                         
        
        mRenderer.nextFrame();
    }
    
    /**
     * Paints the canvas.
     */
    public void paint(Graphics g) {
        update(g);
    }

    void drawButtons() {
        if (mShowUIButtons) {
            if (mAutoRotateLeftButton != null)
                mAutoRotateLeftButton.draw(mPixels, mWidth);
            if (mAutoRotateRightButton != null)
                mAutoRotateRightButton.draw(mPixels, mWidth);
            
            if (mRotateLeftButton != null)
                mRotateLeftButton.draw(mPixels, mWidth);
            if (mRotateRightButton != null)
                mRotateRightButton.draw(mPixels, mWidth);
            if (mRotateUpButton != null)
                mRotateUpButton.draw(mPixels, mWidth);
            if (mRotateDownButton != null)
                mRotateDownButton.draw(mPixels, mWidth);

            if (mPanLeftButton != null)
                mPanLeftButton.draw(mPixels, mWidth);
            if (mPanRightButton != null)
                mPanRightButton.draw(mPixels, mWidth);
            if (mPanUpButton != null)
                mPanUpButton.draw(mPixels, mWidth);
            if (mPanDownButton != null)
                mPanDownButton.draw(mPixels, mWidth);

            if (mZoomInButton != null)
                mZoomInButton.draw(mPixels, mWidth);
            if (mZoomOutButton != null)
                mZoomOutButton.draw(mPixels, mWidth);

            if (mAudioButton != null)
                mAudioButton.draw(mPixels, mWidth);
        }
        
        for (int i = 0; i < mPositionButtons.size(); i++) {
            Button posButton = (Button)(mPositionButtons.elementAt(i));
            if (posButton != null)
                posButton.draw(mPixels, mWidth);
        }
        
        for (int i = 0; i < mButtonLabels.size(); i++) {
            Button label = (Button)(mButtonLabels.elementAt(i));
            if (label != null)
                label.draw(mPixels, mWidth);
        }
    }
    
    // -------------------------------------------------------------------
    // MouseListener/MouseMotionListener interface
    // -------------------------------------------------------------------
    
    public void mouseClicked(MouseEvent event) {
        mRenderer.mouseClicked(event);
    }
    
    public void mouseDragged(MouseEvent event) {        
        mRenderer.mouseDragged(event);
    }
        
    public void mouseEntered(MouseEvent event) {
        mRenderer.mouseEntered(event);
    }
    
    public void mouseExited(MouseEvent event) {
        mRenderer.mouseExited(event);
    }
    
    public void mouseMoved(MouseEvent event) {
        mRenderer.mouseMoved(event);
    }
    
    public void mousePressed(MouseEvent event) {
        Point p = event.getPoint();
    
        mMouseDown = true;
        mMousePosition.x = p.x;
        mMousePosition.y = p.y;

        handleMouseDown(p);
        mRenderer.mousePressed(event);
    }
    
    void handleMouseDown(Point p) {
        mActiveButton = null;
        if (mAutoRotateLeftButton != null && mAutoRotateLeftButton.hitTest(p)) {
            mActiveButton = mAutoRotateLeftButton;
            mRenderer.toggleAutoRotate(true);
        }
        else if (mAutoRotateRightButton != null && mAutoRotateRightButton.hitTest(p)) {
            mActiveButton = mAutoRotateRightButton;
            mRenderer.toggleAutoRotate(false);
        }
        else if (mRotateLeftButton != null && mRotateLeftButton.hitTest(p)) {
            mActiveButton = mRotateLeftButton;
            mRenderer.smoothRotX(0.1f);
        }
        else if (mRotateRightButton != null && mRotateRightButton.hitTest(p)) {
            mActiveButton = mRotateRightButton;
            mRenderer.smoothRotX(-0.1f);
        }
        else if (mRotateUpButton != null && mRotateUpButton.hitTest(p)) {
            mActiveButton = mRotateUpButton;
            mRenderer.smoothRotY(0.1f);
        }
        else if (mRotateDownButton != null && mRotateDownButton.hitTest(p)) {
            mActiveButton = mRotateDownButton;
            mRenderer.smoothRotY(-0.1f);
        }
        else if (mPanLeftButton != null && mPanLeftButton.hitTest(p)) {
            mActiveButton = mPanLeftButton;
            mRenderer.smoothMoveX(-0.1f);
        }
        else if (mPanRightButton != null && mPanRightButton.hitTest(p)) {
            mActiveButton = mPanRightButton;
            mRenderer.smoothMoveX(0.1f);
        }
        else if (mPanUpButton != null && mPanUpButton.hitTest(p)) {
            mActiveButton = mPanUpButton;
            mRenderer.smoothMoveY(-0.1f);
        }
        else if (mPanDownButton != null && mPanDownButton.hitTest(p)) {
            mActiveButton = mPanDownButton;
            mRenderer.smoothMoveY(0.1f);
        }
        else if (mZoomInButton != null && mZoomInButton.hitTest(p)) {
            mActiveButton = mZoomInButton;
            mRenderer.smoothZoom(1.05f);
        }
        else if (mZoomOutButton != null && mZoomOutButton.hitTest(p)) {
            mActiveButton = mZoomOutButton;
            mRenderer.smoothZoom(0.95f);
        }
        else if (mAudioButton != null && mAudioButton.hitTest(p)) {
            mMouseDown = false;
            mActiveButton = mAudioButton;              
        }
        else {
            for (int i = 0; i < mPositionButtons.size(); i++) {
                Button posButton = (Button)(mPositionButtons.elementAt(i));
                if (posButton != null && posButton.hitTest(p)) {
                    mActiveButton = posButton;
                    mRenderer.moveToPosition(i);
                    break;
                }
            }
        }
        
        if (mAutoRotateRightButton != null)
            mAutoRotateRightButton.setPressed(false);
        if (mAutoRotateLeftButton != null)
            mAutoRotateLeftButton.setPressed(false);
        
        if (mActiveButton != null && mActiveButton != mAudioButton) {
            if (mActiveButton == mAutoRotateLeftButton) {                
                mAutoRotateLeftButton.setPressed(mRenderer.getAutoRotate());
                mAutoRotateRightButton.setPressed(false);
            }
            else if (mActiveButton == mAutoRotateRightButton) {
                mAutoRotateRightButton.setPressed(mRenderer.getAutoRotate());
                mAutoRotateLeftButton.setPressed(false);
            }
            else {                
                mActiveButton.setPressed(true);            
            }
        }
    }
    
    public void mouseReleased(MouseEvent event) {
        mMouseDown = false;
        if (mActiveButton != null) {            
            if (mActiveButton == mAudioButton) {
                if (mAudioButton.isPressed()) {
                    loopOn();
                }
                else {
                    loopOff();
                }
            }
            else {                
                if (mActiveButton != mAutoRotateLeftButton && mActiveButton != mAutoRotateRightButton)
                    mActiveButton.setPressed(false);
            }
        }
        mRenderer.mouseReleased(event);
    }
    
    // -------------------------------------------------------------------
    // KeyListener interface
    // -------------------------------------------------------------------
    
    public void keyPressed(KeyEvent event) {
        mRenderer.keyPressed(event);
    }
    
    public void keyReleased(KeyEvent event) {
        mRenderer.keyReleased(event);
    }
    
    public void keyTyped(KeyEvent event) {        
        mRenderer.keyTyped(event);
    }

    public void setCursor(int cursor) {
        setCursor(Cursor.getPredefinedCursor(cursor));
    }

    // --------------------------------------------------------------
    // ImageObserver interface
    // --------------------------------------------------------------
    
    /**
     * Stores information about the image (asynchronous callback).
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {        
        if ((infoflags & ImageObserver.WIDTH) > 0 && width > 0)
            mLoadingScreenWidth = width;
        if ((infoflags & ImageObserver.HEIGHT) > 0 && height > 0)
            mLoadingScreenHeight = height;
        if (mLoadingScreenWidth > 0 && mLoadingScreenHeight > 0)
            repaint();
        return ((mLoadingScreenWidth > 0 && mLoadingScreenHeight > 0));
    }
    
    /**
     *
     */
    final void startTimer() {
        mTick = 0;
        mEnableTimer = true;
        mTimerThread.start();
        mRenderer.enableAA(false);
    }
    
    final void stopTimer() {
        mEnableTimer = false;
        mTick = 0;
        mRenderer.enableAA(true);
        mRenderer.resumeRenderer();
        //loopOn();
        //repaint();
    }
    
    /** 
     *
     */
    public void run() {
        while (mEnableTimer) {
            try {
                mTimerThread.sleep(TIMER_TICK);
            }
            catch (InterruptedException ex) {                    
            }
            mTick++;
            Dimension d = getSize();
            if (mShowLoadingScreen || mRenderer.mSuspended == true)
                repaint(0, d.height - 50, d.width, 50);
        }
    }
    
}
