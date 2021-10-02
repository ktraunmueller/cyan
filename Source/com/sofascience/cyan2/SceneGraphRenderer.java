/*
 * RenderContext.java
 *
 * Created on May 28, 2002, 9:57 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.net.URL;

/**
 * A SceneGraphRenderer runs in its own thread, drives the render loop, and 
 * handles mouse and keyboard interaction. Canvas3Ds can register
 * with the renderer and are notified after a new frame is available. 
 * Note that mouse and keyboard events must be passed through (from an applet or canvas).
 *
 * @author  Karl Traunmueller
 */
class SceneGraphRenderer implements Runnable {

    static final int    MOVING_AVG_LEN              = 12;
    static final float  DEFAULT_SCALE_MIN           = 0.001f;
    static final float  DEFAULT_SCALE_MAX           = 1000.0f;    
    static final float  DEFAULT_ROT_SCALE           = 1.0f / 8.0f;
    static final float  DEFAULT_MOVE_SCALE          = 1.0f / 10.0f;
    static final float  DEFAULT_SCALE_SCALE         = 1.0f / 60.0f;
    
    static final float  TRANSFORM_EPSILON           = 0.0015f;
    static final float  SUSPEND_EPSILON             = 0.001f;
    static final float  KEEP_ALIVE                  = 0.05f;
    static final float  EASE_OUT_KEEP_ALIVE         = 0.1f;
    
    static final float  MOUSE_SCALE                 = 1.0f;
    static final float  MOUSE_MAX                   = 1.2f;
    static final float  MOUSE_OFFSET                = 0.03f;
    static final float  EASE_OUT_TRANSLATION        = 0.6f;
    static final float  EASE_OUT_ROTATION           = 0.75f;        
    static final float  EASE_OUT_SCALE              = 0.5f;
    
    static final int    RENDERTHREAD_SLEEP          = 2;
    static final int    RENDERTHREAD_SLEEP_SLOW     = 20;
    static final int    TOOLTIP_DELAY               = 100;

    Scene               mScene;
    RenderPipeline      mRenderPipeline;    
    Dimension           mScreenDimension;
    private boolean     mResizeRequested;
    private Dimension   mNewDimension;
    Dimension           mPixelDimension;
    boolean             mAntialias;
    int                 mSupersampling;
    private Color       mColor;
    float[]             mZBuffer;
    float[]             mFloatOnes;
    byte[]              mIdBuffer;
    byte[]              mByteZeros;
    int[]               mIntWhite;
    Canvas3D            mCanvas;            
    private Thread      mRenderThread;
    private boolean     mStopRequested;
    boolean             mSuspended;
    float               mKeepAlive;
    View                mView;
    private boolean     mAllowMoveX, mAllowMoveY, mAllowMoveZ;
    private float       mdX, mdY, mdZ;
    private float       mXAbs, mYAbs, mZAbs;
    private MovingAvg   mdXAvg, mdYAvg, mdZAvg;
    private float       mMoveScale;    
    private boolean     mAllowRotX, mAllowRotY, mAllowRotZ;
    private boolean     mLimitRotX, mLimitRotY, mLimitRotZ;
    private MovingAvg   mRotXAvg, mRotYAvg, mRotZAvg;
    private float       mRotX, mRotY, mRotZ;
    private float       mRotXAbs, mRotXMin, mRotXMax;
    private float       mRotYAbs, mRotYMin, mRotYMax;
    private float       mRotZAbs, mRotZMin, mRotZMax;
    private float       mRotScale;
    private boolean     mAllowScale;    
    private boolean     mLimitScale;
    private MovingAvg   mScaleAvg;
    private float       mScale;
    private float       mScaleAbs;
    private float       mScaleScale;
    private float       mScaleMin, mScaleMax;
    private boolean     mAllowAA;
    private boolean     mAllowEaseOut, mAutoRotate, mAutoRotateLeft;
    private boolean     mResetTransform;
    private int         mMouseX, mMouseY;    
    boolean             mLocked;
    long                mToolTipTime;
    boolean             mToolTipActive;
    private boolean     mShiftDown;    
    private Texture     mBackground;
    int[]               mBackgroundPixels;
    private boolean     mRasterizerChangeRequested;
    private int         mNewRasterizer;
    private int         mRenderThreadSleep;
    private boolean     mEnableAutoSuspend;
    private boolean     mEnableAAExtern;
    private Vector      mPositions;
    private Position    mActivePosition;
    
    /**
     * Default constructor.
     */
    SceneGraphRenderer(Canvas3D client) {
        mCanvas = client;
        mRenderPipeline = new RenderPipeline(this);
        mScene = new Scene();
        mView = new View();
        mColor = new Color();
        
        mPixelDimension = new Dimension();
        mScreenDimension = new Dimension();
        mSupersampling = 2;
        
        mRenderThread = new Thread(this);
        mNewRasterizer = -1;
        
        mAllowScale = true;
        mScaleAvg = new MovingAvg(MOVING_AVG_LEN);
        mScale = 1.0f;
        mScaleScale = DEFAULT_SCALE_SCALE;
        mScaleMin = DEFAULT_SCALE_MIN;
        mScaleMax = DEFAULT_SCALE_MAX;
                     
        mAllowRotX = true;
        mAllowRotY = true;
        mAllowRotZ = true;

        mRotX = 0.005f;
        mRotXAvg = new MovingAvg(MOVING_AVG_LEN);
        mRotYAvg = new MovingAvg(MOVING_AVG_LEN);
        mRotZAvg = new MovingAvg(MOVING_AVG_LEN);
        
        mAllowMoveX = true;
        mAllowMoveY = true;
        mAllowMoveZ = true;                
        mdXAvg = new MovingAvg(MOVING_AVG_LEN);
        mdYAvg = new MovingAvg(MOVING_AVG_LEN);
        mdZAvg = new MovingAvg(MOVING_AVG_LEN);

        mScaleAbs = 1.0f;
        mRotScale = DEFAULT_ROT_SCALE;
        mMoveScale = DEFAULT_MOVE_SCALE;
        
        mRenderThreadSleep = RENDERTHREAD_SLEEP;
        mEnableAutoSuspend = true;
        mEnableAAExtern = true;
        
        mPositions = new Vector();
        
        System.out.println("---------------------------------------------");
        System.out.println(" This is Cyan 2.0");
        System.out.println(" Copyright (c) 2002-2003 Sofascience Software");
        System.out.println(" Visit us at http://www.sofascience.com");
        System.out.println("----------------------------------------------");
    }
     
    final void cleanUp() {
        mZBuffer = null;
        mFloatOnes = null;
        mIdBuffer = null;
        mByteZeros = null;
        mIntWhite = null;
        mScene = null;
        mRenderPipeline = null;
        mBackgroundPixels = null;
    }
    
    final void enableMouseRotate(boolean rotX, boolean rotY, boolean rotZ) {
        mAllowRotX = rotX;
        mAllowRotY = rotY;
        mAllowRotZ = rotZ;
    }
    
    final void limitRotX(boolean limit, float min, float max) {
        mLimitRotX = limit;
        mRotXMin = min;
        mRotXMax = max;
    }        
    
    final void limitRotY(boolean limit, float min, float max) {
        mLimitRotY = limit;
        mRotYMin = min;
        mRotYMax = max;
    }  
    
    final void limitRotZ(boolean limit, float min, float max) {
        mLimitRotZ = limit;
        mRotZMin = min;
        mRotZMax = max;
    }  
    
    final void enableMouseMove(boolean moveX, boolean moveY, boolean moveZ) {
        mAllowMoveX = moveX;
        mAllowMoveY = moveY;
        mAllowMoveZ = moveZ;
    }
    
    public final void enableZoom(boolean enable) {
        mAllowScale = enable;
    }
    
    final void limitZoom(boolean limit) {
        mLimitScale = limit;
    }
    
    final void setZoomRange(float min, float max) {
        mScaleMin = min;
        mScaleMax = max;
    }
    
    final void setBackground(URL background) {
        mBackground = new Texture(background);
        mBackgroundPixels = mBackground.getPixels();        
    }
    
    final void enableAutoRotate(boolean autorotate, boolean left) {
        mAutoRotate = autorotate;
        mAllowEaseOut = !autorotate;
        mRotX = autorotate ? (left ? 0.01f : -0.01f) : 0.2f;
        mRotY = autorotate ? 0.003f : 0.0f;
    }
    
    final void disableAutoRotate() {
        mAutoRotate = false;
        mAllowEaseOut = true;
        //mRotX = 0.0f;
        //mRotY = 0.0f;
    }
    
    final void toggleAutoRotate(boolean left) {
        if (mAutoRotate)
            disableAutoRotate();
        else
            enableAutoRotate(true, left);
    }
    
    final boolean getAutoRotate() {
        return mAutoRotate;
    }
    
    final void enableAutoSuspend(boolean enable) {
        mEnableAutoSuspend = enable;
    }
    
    final void enableAA(boolean enable) {
        mEnableAAExtern = enable;
    }
    
    final void addTooltip(URL tooltip, String mesh) {
        if (mScene == null)
            return;        
        Texture tt = new Texture(tooltip);
        mCanvas.textureLoaded();
        mScene.addTooltip(mesh, tt);
    }
    
    final void setColor(String name, int r, int g, int b) {
        if (mScene == null)
            return;        
        Material mat = mScene.getMaterialByName(name);
        if (mat != null) {
            mat.mDiffuse.set(r / 255.0f, g / 255.0f, b / 255.0f, 0.0f);
        }
    }

    final void setRenderSpeedSlow() {
        mRenderThreadSleep = RENDERTHREAD_SLEEP_SLOW;
    }
    
    final void setRenderSpeedMax() {
        mRenderThreadSleep = RENDERTHREAD_SLEEP;
    }

    final void smoothRotX(float dX) {
        mRotXAvg.input(dX);
        mRotX = Math.max(Math.abs(mRotXAvg.value()), TRANSFORM_EPSILON) * (dX > 0.0f ? 1.0f : -1.0f);
    }
    
    final void smoothRotY(float dY) {
        mRotYAvg.input(dY);
        mRotY = Math.max(Math.abs(mRotYAvg.value()), TRANSFORM_EPSILON) * (dY > 0.0f ? 1.0f : -1.0f);
    }
    
    final void smoothRotZ(float dZ) {
        mRotZAvg.input(dZ);
        mRotZ = Math.max(Math.abs(mRotZAvg.value()), TRANSFORM_EPSILON) * (dZ > 0.0f ? 1.0f : -1.0f);
    }
    
    final void smoothZoom(float dS) {
        mScaleAvg.input(dS - 1.0f);
        mScale = 1.0f + mScaleAvg.value();
    }
    
    final void smoothMoveX(float dX) {
        mdXAvg.input(dX);
        mdX = Math.max(Math.abs(mdXAvg.value()), TRANSFORM_EPSILON) * (dX > 0.0f ? 1.0f : -1.0f);
    }
    
    final void smoothMoveY(float dY) {
        mdYAvg.input(dY);
        mdY = Math.max(Math.abs(mdYAvg.value()), TRANSFORM_EPSILON) * (dY > 0.0f ? 1.0f : -1.0f);
    }
    
    final void smoothMoveZ(float dZ) {
        mdZAvg.input(dZ);
        mdZ = Math.max(Math.abs(mdZAvg.value()), TRANSFORM_EPSILON) * (dZ > 0.0f ? 1.0f : -1.0f);
    }
    
    final void setRotAbs(float rotX, float rotY, float rotZ) {
        mRotXAbs = rotZ;
        mRotYAbs = rotY;
    }
    
    final void setPosAbs(float x, float y, float z) {
        mXAbs = x;
        mYAbs = y;
        mZAbs = z;
    }
    
    final int addPosition(float x, float y, float z, float rotX, float rotY, float scale) {
        Position pos = new Position(x, y, z, rotX, rotY, scale);
        mPositions.addElement(pos);
        return mPositions.indexOf(pos);
    }
    
    final void moveToPosition(int pos) {
        if (mScene == null)
            return;
        mActivePosition = (Position)(mPositions.elementAt(pos));
        if (mActivePosition == null)
            return;
        TransformNode sceneGraph = mScene.getSceneGraph();
        mActivePosition.prepareDeltas(mXAbs, mYAbs, mZAbs, mRotXAbs, mRotYAbs, sceneGraph.getScale());
        resumeRenderer();
    }
    
    // -------------------------------------------------------------------
    // render pipeline access
    // -------------------------------------------------------------------
    
    final RenderPipeline getRenderPipeline() {
        return mRenderPipeline;
    }
    
    /**
     *
     */
    final void setPreferredRasterizer(int type) {
        mRenderPipeline.setPreferredRasterizer(type);
    }
    
    // -------------------------------------------------------------------
    // client access
    // -------------------------------------------------------------------
    
    /**
     * Retrieves the renderer's current client.
     */ 
    final Canvas3D getClient() {
        return mCanvas;
    }

    // -------------------------------------------------------------------
    // pixel data access
    // -------------------------------------------------------------------
    
    /**
     * Retrieves the renderer's client pixel data array.
     */
    final int[] getPixels() {
        if (mCanvas == null)
            return null;
        return mAntialias ? mCanvas.getSupersampledPixels() : mCanvas.getPixels();
    }

    /**
     * Returns the pixel buffers dimension.
     */
    final Dimension getPixelDimension() {
        return mPixelDimension;
    }
    
    /**
     * Returns whether antialiasing is currently on or off.
     */
    final boolean getAntialias() {
        return mAntialias;
    }
    
    // -------------------------------------------------------------------
    // scene access
    // -------------------------------------------------------------------
    
    /**
     * Returns the renderer's scene.
     */
    synchronized Scene getScene() {
        return mScene;
    }
            
    // -------------------------------------------------------------------
    // view access
    // -------------------------------------------------------------------
    
    /** 
     * Retrieves the current View.
     */
    final View getView() {
        return mView;        
    }
        
    /**
     * Sets the current View.
     */
    final void setView(View view) {
        mView = view;
    }

    final void sizeChanged(Dimension d) {
        if (mScreenDimension.height != d.height ||
            mScreenDimension.width != d.width) {
               
            mScreenDimension.setSize(d);
            if (mAntialias)
                mPixelDimension.setSize(d.width * mSupersampling, d.height * mSupersampling);
            else
                mPixelDimension.setSize(d);
            
            mZBuffer = new float[mSupersampling * mSupersampling * d.width * d.height];
            mIdBuffer = new byte[mSupersampling * mSupersampling * d.width * d.height];
            
            mFloatOnes = new float[mSupersampling * d.width];
            mByteZeros = new byte[mSupersampling * d.width];
            for (int i = mFloatOnes.length - 1; i >= 0; i--) {
                mFloatOnes[i] = 1.0f;
                mByteZeros[i] = 0;
            }
            mIntWhite = new int[d.width];
            for (int i = d.width - 1; i >= 0; i--) {
                mIntWhite[i] = 0xFFFFFFFF;
            }
            
            mView.setViewportDimension(d);
            mRenderPipeline.setProjectionMatrix(mView.getProjectionMatrix());
        }
    }
    
    /**
     *
     */
    final void resize() {
        mResizeRequested = false;
        mScreenDimension.setSize(mNewDimension);
        if (mAntialias)
            mPixelDimension.setSize(mNewDimension.width * mSupersampling, 
                                    mNewDimension.height * mSupersampling);
        else
            mPixelDimension.setSize(mNewDimension);

        mZBuffer = new float[mSupersampling * mSupersampling * mNewDimension.width * mNewDimension.height];
        mIdBuffer = new byte[mSupersampling * mSupersampling * mNewDimension.width * mNewDimension.height];

        mView.setViewportDimension(mNewDimension);
        mRenderPipeline.setProjectionMatrix(mView.getProjectionMatrix());
    }
    
    /**
     * Returnst the supersampling factor.
     */
    final int getSupersampling() {
        return mSupersampling;
    }
    
    /**
     * Set supersampling factor.
     */
    final void setSupersampling(int supersampling) {
        mSupersampling = supersampling;
    }
    
    // -------------------------------------------------------------------
    // data array access
    // -------------------------------------------------------------------    
    
    /**
     * Returns the z-buffer array.
     */
    final float[] getZBuffer() {
        return mZBuffer;
    }
    
    /**
     * Returns the id-buffer array.
     */
    final byte[] getIdBuffer() {
        return mIdBuffer;
    }
        
    // -------------------------------------------------------------------
    // renderer operations
    // -------------------------------------------------------------------
    
    /**
     * Starts the render loop.
     */
    final void startRenderer() {     
        if (mScene == null) {
            System.out.println("Cyan.SceneGraphRenderer: no scene set, cannot start renderer");
            return;
        }
        
        if (mScene.getNrLights() == 0) {
            //System.out.println("Cyan.SceneGraphRenderer: inserting default light into scene");
            Light light = new Light("DefaultLight");
            light.mSpot.set(0.0f, -0.2f, 1.0f);
            light.mSpot.normalize();
            mScene.addLight(light);
        }
        
        System.out.println("Cyan.SceneGraphRenderer: starting renderer");
        mStopRequested = false;
        mRenderThread.start();                        
    }
    
    /**
     * Asks the render loop to stop (will stop after current frame is rendered).
     */
    final void stopRenderer() {
        mAllowEaseOut = true;
        System.out.println("Cyan.SceneGraphRenderer: stopping renderer");
        mStopRequested = true;
    }

    /**
     * Suspends the render thread.
     */
    final void suspendRenderer() {        
        if (mRenderThread == null || mSuspended || !mEnableAutoSuspend)
            return;
                
        synchronized (mRenderThread) {
            //System.out.println("Cyan.SceneGraphRenderer: suspending renderer");
            mSuspended = true;
            try {
                mRenderThread.wait();
            }
            catch (InterruptedException ex) {
            }         
        }
    }
    
    /**
     * Resume the render thread.
     */
    final void resumeRenderer() {   
        if (mSuspended == false || mRenderThread == null)
            return;
             
        synchronized (mRenderThread) {
            //System.out.println("Cyan.SceneGraphRenderer: resuming renderer");
            mKeepAlive = KEEP_ALIVE;
            mSuspended = false;
            mRenderThread.notify();            
        }
    }
        
    /**
     * Clears the z-buffer and image buffer.
     */
    final void clearBuffers() {        
        if (mZBuffer == null || mIdBuffer == null || 
            mFloatOnes == null || mByteZeros == null || mIntWhite == null ||
            mCanvas.getPixels() == null)
            return;
                
        int lines = mSupersampling * mScreenDimension.height;
        int scanline = mSupersampling * mScreenDimension.width;
        while (lines-- > 0) {
            System.arraycopy(mFloatOnes, 0, mZBuffer, lines * scanline, scanline);
            System.arraycopy(mByteZeros, 0, mIdBuffer, lines * scanline, scanline);
        }

        if (mAntialias == false) {
            int[] pixels = mCanvas.getPixels();
            if (mBackgroundPixels != null) {
                System.arraycopy(mBackgroundPixels, 0, pixels, 0, pixels.length);
            }
            else {                
                lines = mScreenDimension.height;
                scanline = mScreenDimension.width;
                while (lines-- > 0) {
                    System.arraycopy(mIntWhite, 0, pixels, lines * scanline, scanline);
                }
            }
        }
        else {
            int[] pixels = mCanvas.getSupersampledPixels();
            if (mBackgroundPixels != null) {                
                if (mSupersampling == 2) {
                    int interlace = mScreenDimension.width << 1;
                    for (int y = mScreenDimension.height - 1; y >= 0; y--) {
                        int pixelOffset = y * mScreenDimension.width;
                        int supersampledPixelOffset = pixelOffset << 2; // * 4
                        for (int x = mScreenDimension.width - 1; x >= 0; x--) {                    
                            int backgroundPixel = mBackgroundPixels[pixelOffset];
                            pixels[supersampledPixelOffset] = backgroundPixel;
                            pixels[supersampledPixelOffset + 1] = backgroundPixel;
                            pixels[supersampledPixelOffset + interlace] = backgroundPixel;
                            pixels[supersampledPixelOffset + interlace + 1] = backgroundPixel;
                            pixelOffset++;
                            supersampledPixelOffset += 2;
                        }            
                    }
                }
                else if (mSupersampling == 3) {
                    int interlace = mScreenDimension.width * 3;
                    for (int y = mScreenDimension.height - 1; y >= 0; y--) {
                        int pixelOffset = y * mScreenDimension.width;
                        int supersampledPixelOffset = pixelOffset * 9;
                        for (int x = mScreenDimension.width - 1; x >= 0; x--) {                    
                            int backgroundPixel = mBackgroundPixels[pixelOffset];
                            pixels[supersampledPixelOffset] = backgroundPixel;
                            pixels[supersampledPixelOffset + 1] = backgroundPixel;
                            pixels[supersampledPixelOffset + 2] = backgroundPixel;
                            pixels[supersampledPixelOffset + interlace] = backgroundPixel;
                            pixels[supersampledPixelOffset + interlace + 1] = backgroundPixel;
                            pixels[supersampledPixelOffset + interlace + 2] = backgroundPixel;
                            pixels[supersampledPixelOffset + (interlace << 1)] = backgroundPixel;
                            pixels[supersampledPixelOffset + (interlace << 1) + 1] = backgroundPixel;
                            pixels[supersampledPixelOffset + (interlace << 1) + 2] = backgroundPixel;
                            pixelOffset++;
                            supersampledPixelOffset += 3;
                        }            
                    }
                }
            }
            else
            {
                while (lines-- > 0) {
                    System.arraycopy(mIntWhite, 0, pixels, lines * scanline, scanline);
                }
            }
        }
    }
        
    /**
     * Allows the (waiting) renderer to render the next frame.
     */
    void nextFrame() {
        mLocked = false;
    }

    /**
     * Render loop.
     */
    public void run() 
    {
        if (mView == null || mCanvas == null || mScene == null)
            return;               
        
        mRenderPipeline.setProjectionMatrix(mView.getProjectionMatrix());
        mAllowAA = true;
        mScene.eval(0);
        int time = 0;
        
        while (!mStopRequested) {
            
            if (mRasterizerChangeRequested) {
                mRenderPipeline.setPreferredRasterizer(mNewRasterizer);
                mRasterizerChangeRequested = false;
                mAllowAA = (mNewRasterizer != TriangleRasterizer.WIREFRAME);
            }
           
            if (mCanvas.mMouseDown) {
                mCanvas.handleMouseDown(mCanvas.mMousePosition);
            }
            
            applyTransforms();
            checkAA();
            clearBuffers();

            mRenderPipeline.setViewingTransform(mView.getViewingTransform());
            boolean animationsActive = mScene.render(this);    
            mScene.renderTransparentFaces(this);
            mAllowAA &= !animationsActive;
            
            if (mAllowAA && mAntialias && mEnableAAExtern)
                antialiasImage();
            
            checkToolTip(hitTest());
            
            mLocked = true;
            mCanvas.frameReady();
            while (mLocked && !mStopRequested) {
                try {
                    mRenderThread.sleep(mRenderThreadSleep);
                }
                catch (InterruptedException ex) {                    
                }
            }

            checkSuspendRenderer();
        }
    }
    
    /**
     * Performs any pending rotations, translations, and scales.
     */ 
    final void applyTransforms() {
        if (mScene == null)
            return;
        
        TransformNode sceneGraph = mScene.getSceneGraph();
        TransformNode sceneTransform = mScene.getSceneTransform();
        float scale = 50.0f * (float)Math.sqrt(1.0f / sceneGraph.getScale()) * sceneGraph.getScale();

        if (mActivePosition != null) {
            sceneGraph.rotZ(mActivePosition.dRotX);
            mRotXAbs += mActivePosition.dRotX;            
            if (mRotXAbs > Math.PI)
                mRotXAbs -= 2 * Math.PI ;
            if (mRotXAbs < -Math.PI)
                mRotXAbs += 2 * Math.PI;
            sceneTransform.rotX(mActivePosition.dRotY);
            mRotYAbs += mActivePosition.dRotY;
            if (mRotYAbs > Math.PI)
                mRotYAbs -= 2 * Math.PI;
            if (mRotYAbs < -Math.PI)
                mRotYAbs += 2 * Math.PI;
            sceneGraph.moveX(mActivePosition.dX);
            mXAbs += mActivePosition.dX;
            sceneTransform.moveY(mActivePosition.dY);
            mYAbs += mActivePosition.dY; 
            sceneGraph.scaleAbs(mActivePosition.currentScale);
            mScaleAbs = mActivePosition.currentScale;
            if (!mActivePosition.step()) 
                mActivePosition = null;          
        }
        else {
            if (mAllowRotX && mRotX != 0.0f) {
                //if (!mLimitRotX || !(mRotXAbs + mRotX < mRotXMin || mRotXAbs + mRotX > mRotXMax)) {
                    sceneGraph.rotZ(-mRotX);
                    mRotXAbs -= mRotX;
                    if (mRotXAbs > Math.PI)
                        mRotXAbs -= 2 * Math.PI;
                    if (mRotXAbs < -Math.PI)
                        mRotXAbs += 2 * Math.PI;
                //}
            }
            if (mAllowRotY && mRotY != 0.0f) {
                //if (!mLimitRotY || !(mRotYAbs + mRotY < mRotYMin || mRotYAbs + mRotY > mRotYMax)) {
                    sceneTransform.rotX(mRotY);
                    mRotYAbs += mRotY;
                    if (mRotYAbs > Math.PI)
                        mRotYAbs -= 2 * Math.PI;
                    if (mRotYAbs < -Math.PI)
                        mRotYAbs += 2 * Math.PI;
                //}
            }
            /*
            if (mAllowRotZ && mRotZ != 0.0f) {
                //if (!mLimitRotZ || !(mRotZAbs + mRotZ < mRotZMin || mRotZAbs + mRotZ > mRotZMax)) {
                    sceneGraph.rotY(mRotZ);
                    mRotZAbs += mRotZ;
                    if (mRotZAbs < pihalf)
                        mRotZAbs += pihalf;
                    if (mRotZAbs > pihalf)
                        mRotZAbs -= pihalf;
                //}
            }
             */

            if (mAllowMoveX && mdX != 0.0f) {            
                sceneGraph.moveX(-mdX * scale);
                mXAbs -= mdX * scale;
            }
            if (mAllowMoveY && mdY != 0.0f) {
                sceneTransform.moveY(mdY * scale);
                mYAbs += mdY * scale;
            }
            //if (mAllowMoveZ && mdZ != 0.0f) {
            //    sceneGraph.moveZ(mdZ * scale);
            //    mZAbs += mdZ * scale;
            //}

            if (mAllowScale && mScale != 1.0f) {
                if (!mLimitScale || !(mScaleAbs * mScale < mScaleMin || mScaleAbs * mScale > mScaleMax)) {
                    sceneGraph.scale(mScale);
                    mScaleAbs *= mScale;
                }
            }
        }
        
        if (mResetTransform) {
            sceneGraph.resetTransform();
            sceneTransform.resetTransform();
            mResetTransform = false;
        }
    }
    
    final void antialiasOn(boolean on) {
        if (on) {
            mAntialias = true;
            mPixelDimension.setSize(mScreenDimension.width * mSupersampling, mScreenDimension.height * mSupersampling);
        }
        else {
            mAntialias = false;
            mPixelDimension.setSize(mScreenDimension.width, mScreenDimension.height);
        }
    }
    
    /**
     * Performs an 'ease out' on all transformations.
     */
    final void checkAA() {         
        if (mActivePosition != null) {
            antialiasOn(false);
            return;
        }
        
        mRotXAvg.input(0.0f);
        mRotYAvg.input(0.0f);
        mRotZAvg.input(0.0f);
        mdXAvg.input(0.0f);
        mdYAvg.input(0.0f);
        mdZAvg.input(0.0f);
        mScaleAvg.input(0.0f);
        
        if (mAllowEaseOut) {
            mRotX *= EASE_OUT_ROTATION;
            mRotY *= EASE_OUT_ROTATION;
            mRotZ *= EASE_OUT_ROTATION;
            mdX *= EASE_OUT_TRANSLATION;
            mdY *= EASE_OUT_TRANSLATION;
            mdZ *= EASE_OUT_TRANSLATION;
            mScale = 1.0f + (mScale - 1.0f) * EASE_OUT_SCALE;
            mKeepAlive *= EASE_OUT_KEEP_ALIVE;
        }
        
        if ((float)Math.abs(mRotX)  < TRANSFORM_EPSILON &&
            (float)Math.abs(mRotY)  < TRANSFORM_EPSILON &&
            (float)Math.abs(mRotZ)  < TRANSFORM_EPSILON &&
            (float)Math.abs(mdX)    < TRANSFORM_EPSILON &&
            (float)Math.abs(mdY)    < TRANSFORM_EPSILON &&
            (float)Math.abs(mdZ)    < TRANSFORM_EPSILON &&
            (float)Math.abs(1.0f - mScale) < TRANSFORM_EPSILON &&
            mKeepAlive              < TRANSFORM_EPSILON &&
            mAllowAA) {
            antialiasOn(true);
        }
        else {
            antialiasOn(false);
        }                
    }
    
    /**
     * Suspends the render thread if image is static.
     */
    final void checkSuspendRenderer() {
        if (!mAllowAA)
            return;
        
        if (mKeepAlive              > SUSPEND_EPSILON ||
            (float)Math.abs(mRotX)  > SUSPEND_EPSILON ||
            (float)Math.abs(mRotY)  > SUSPEND_EPSILON ||
            (float)Math.abs(mRotZ)  > SUSPEND_EPSILON ||
            (float)Math.abs(mdX)    > SUSPEND_EPSILON ||
            (float)Math.abs(mdY)    > SUSPEND_EPSILON ||
            (float)Math.abs(mdZ)    > SUSPEND_EPSILON ||
            (float)Math.abs(1.0f - mScale) > SUSPEND_EPSILON) 
            return;
                
        suspendRenderer();
    }
    
    /**
     * Postfilters the image data if antialiasing is currently enabled.
     */
    final void antialiasImage() {
        int[] supersampledPixels = mCanvas.getSupersampledPixels();
        int[] pixels = mCanvas.getPixels();
                        
        if (mSupersampling == 2) {
            int interlace = mScreenDimension.width << 1;
            for (int y = mScreenDimension.height - 1; y >= 0; y--) {
                int pixelOffset = y * mScreenDimension.width;
                int supersampledPixelOffset = pixelOffset << 2; // * 4
                for (int x = mScreenDimension.width - 1; x >= 0; x--) {                    
                    int backgroundPixel = mBackgroundPixels != null ? mBackgroundPixels[pixelOffset] : 0xFFFFFFFF;
                    int c1 = supersampledPixels[supersampledPixelOffset];
                    //c1 = (mZBuffer[supersampledPixelOffset] == 1.0f ? backgroundPixel : c1);
                    int c2 = supersampledPixels[supersampledPixelOffset + 1];
                    //c2 = (mZBuffer[supersampledPixelOffset + 1] == 1.0f ? backgroundPixel : c2);
                    int c3 = supersampledPixels[supersampledPixelOffset + interlace];
                    //c3 = (mZBuffer[supersampledPixelOffset + interlace] == 1.0f ? backgroundPixel : c3);
                    int c4 = supersampledPixels[supersampledPixelOffset + interlace + 1];
                    //c4 = (mZBuffer[supersampledPixelOffset + interlace + 1] == 1.0f ? backgroundPixel : c4);
                    int red   = ((c1 & 0x00FF0000) >> 16) + ((c2 & 0x00FF0000) >> 16) + ((c3 & 0x00FF0000) >> 16) + ((c4 & 0x00FF0000) >> 16);
                    red   >>= 2;
                    red   = Math.min(red, 255);
                    int green = ((c1 & 0x0000FF00) >>  8) + ((c2 & 0x0000FF00) >>  8) + ((c3 & 0x0000FF00) >>  8) + ((c4 & 0x0000FF00) >>  8);
                    green >>= 2;
                    green = Math.min(green, 255);
                    int blue  = (c1 & 0x000000FF)       + (c2 & 0x000000FF)       + (c3 & 0x000000FF)       + (c4 & 0x000000FF);                    
                    blue  >>= 2;
                    blue  = Math.min(blue, 255);                    
                    
                    int color = 0xFF000000 | (red << 16) | (green << 8) | blue;
                    pixels[pixelOffset] = color;
                    
                    pixelOffset++;
                    supersampledPixelOffset += 2;
                }            
            }        
        }
        else if (mSupersampling == 3) {
            int interlace = mScreenDimension.width * 3;
            for (int y = mScreenDimension.height - 1; y >= 0; y--) {
                int pixelOffset = y * mScreenDimension.width;
                int supersampledPixelOffset = pixelOffset * 9;
                for (int x = mScreenDimension.width - 1; x >= 0; x--) {
                    int backgroundPixel = mBackgroundPixels != null ? mBackgroundPixels[pixelOffset] : 0xFFFFFFFF;
                    int c1 = supersampledPixels[supersampledPixelOffset];
                    c1 = (mZBuffer[supersampledPixelOffset] == 1.0f ? backgroundPixel : c1);
                    int c2 = supersampledPixels[supersampledPixelOffset + 1];
                    c2 = (mZBuffer[supersampledPixelOffset + 1] == 1.0f ? backgroundPixel : c2);
                    int c3 = supersampledPixels[supersampledPixelOffset + 2];
                    c3 = (mZBuffer[supersampledPixelOffset + 2] == 1.0f ? backgroundPixel : c3);
                    int c4 = supersampledPixels[supersampledPixelOffset + interlace];
                    c4 = (mZBuffer[supersampledPixelOffset + interlace] == 1.0f ? backgroundPixel : c4);
                    int c5 = supersampledPixels[supersampledPixelOffset + interlace + 1];
                    c5 = (mZBuffer[supersampledPixelOffset + interlace + 1] == 1.0f ? backgroundPixel : c5);
                    int c6 = supersampledPixels[supersampledPixelOffset + interlace + 2];
                    c6  = (mZBuffer[supersampledPixelOffset + interlace + 2] == 1.0f ? backgroundPixel : c6);
                    int c7 = supersampledPixels[supersampledPixelOffset + (interlace << 1)];
                    c7  = (mZBuffer[supersampledPixelOffset + (interlace << 1)] == 1.0f ? backgroundPixel : c7);
                    int c8 = supersampledPixels[supersampledPixelOffset + (interlace << 1) + 1];
                    c8  = (mZBuffer[supersampledPixelOffset + (interlace << 1) + 1] == 1.0f ? backgroundPixel : c8);
                    int c9 = supersampledPixels[supersampledPixelOffset + (interlace << 1) + 2];
                    c9  = (mZBuffer[supersampledPixelOffset + (interlace << 1) + 2] == 1.0f ? backgroundPixel : c9);

                    int red   = ((c1 & 0x00FF0000) >> 16) + ((c2 & 0x00FF0000) >> 16) + ((c3 & 0x00FF0000) >> 16) + 
                                ((c4 & 0x00FF0000) >> 16) + ((c5 & 0x00FF0000) >> 16) + ((c6 & 0x00FF0000) >> 16) + 
                                ((c7 & 0x00FF0000) >> 16) + ((c8 & 0x00FF0000) >> 16) + ((c9 & 0x00FF0000) >> 16);
                    red   /= 9;
                    red   = Math.min(red, 255);
                    int green = ((c1 & 0x0000FF00) >>  8) + ((c2 & 0x0000FF00) >>  8) + ((c3 & 0x0000FF00) >>  8) + 
                                ((c4 & 0x0000FF00) >>  8) + ((c5 & 0x0000FF00) >>  8) + ((c6 & 0x0000FF00) >>  8) + 
                                ((c7 & 0x0000FF00) >>  8) + ((c8 & 0x0000FF00) >>  8) + ((c9 & 0x0000FF00) >>  8);
                    green /= 9;
                    green = Math.min(green, 255);
                    int blue  = (c1 & 0x000000FF) + (c2 & 0x000000FF) + (c3 & 0x000000FF) + 
                                (c4 & 0x000000FF) + (c5 & 0x000000FF) + (c6 & 0x000000FF) + 
                                (c7 & 0x000000FF) + (c8 & 0x000000FF) + (c9 & 0x000000FF);
                    blue  /= 9;
                    blue  = Math.min(blue, 255);
                    
                    int color = 0xFF000000 | (red << 16) | (green << 8) | blue;
                    pixels[pixelOffset] = color;
                    
                    pixelOffset++;
                    supersampledPixelOffset += 3;
                }            
            }
        }
        else {
        }
    }
    
    /**
     * Returns the shape under the mouse cursor, or null.
     */
    final Mesh hitTest() {
        if (mScene == null || mIdBuffer == null)
            return null;
                
        int mouseX = mMouseX;
        int mouseY = mMouseY;
        if (mAntialias) {
            mouseY *= mSupersampling;
            mouseX *= mSupersampling;
        }

        int idOffset = mouseY * mPixelDimension.width + mouseX;
        if (idOffset < 0 || idOffset > mIdBuffer.length)
            return null;

        int id = mIdBuffer[idOffset];
        return mScene.getMeshById(id);
    }
    
    /**
     * Checks if the mouse is over a hotspot and if the tooltip delay is exceeded.
     * If so, it displays the corresponding tool tip.
     */
    final void checkToolTip(Mesh mesh) {
        if (mesh == null)
            return;
        
        //mCanvas.setTooltip(mesh.getName());
        long time = System.currentTimeMillis();
        
        if (time - mToolTipTime > TOOLTIP_DELAY) { 
            String tooltipName = mesh.getName(); 
            if (tooltipName != null) {                
                Texture tooltip = mScene.getTooltip(tooltipName);
                
                /*
                if (tooltip != null) {                    
                    System.out.println(tooltipName + " has a tooltip");
                }
                else {
                    System.out.println(tooltipName + " doesn't have a tooltip");
                }
                 */

                if (tooltip != null) {
                    mToolTipActive = true;                    
                    resumeRenderer();
                    mKeepAlive = TRANSFORM_EPSILON;
                    
                    int[] pixels = mCanvas.getPixels();                        
                    int[] toolPixels = tooltip.getPixels();
                    Dimension d = mCanvas.getViewportDimension();

                    int toolHeight = tooltip.getHeight();
                    int toolWidth = tooltip.getWidth();

                    int toolX = mMouseX + 20;
                    int toolY = mMouseY + 20;
                    if (toolX + toolWidth > d.width)
                        toolX -= (toolWidth + 40);
                    if (toolY + toolHeight > d.height)
                        toolY -= (toolHeight + 40);

                    int offset = toolY * d.width + toolX;

                    for (int y = 0; y < toolHeight; y++) {
                        int toolOffset = y * toolWidth;
                        System.arraycopy(toolPixels, toolOffset, pixels, offset, toolWidth);
                        offset += d.width;
                    }
                }
            }
            else {
                if (mToolTipActive) {
                    resumeRenderer();
                }
                mToolTipActive = false;
            }
        }
    }
    
    // -------------------------------------------------------------------
    // mouse events
    // -------------------------------------------------------------------
    
    void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
        mouseEvent.consume();
    }
    
    void mouseDragged(java.awt.event.MouseEvent mouseEvent) {                
        mKeepAlive = KEEP_ALIVE;
        mToolTipTime = System.currentTimeMillis();
        
        float rotX = 0.0f, rotY = 0.0f, rotZ = 0.0f, dX = 0.0f, dY = 0.0f, dZ = 0.0f, scale = 0.0f;
        if ((mouseEvent.getModifiers() & (MouseEvent.BUTTON1_MASK | MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) > 0) {           
            if ((mouseEvent.getModifiers() & MouseEvent.BUTTON1_MASK) > 0) {
                if (mShiftDown || (mouseEvent.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) > 0) {
                    dX = (-(mMouseX - mouseEvent.getX()) * mMoveScale);
                    dY = (-(mMouseY - mouseEvent.getY()) * mMoveScale);
                }
                else {
                    rotX = ((mMouseX - mouseEvent.getX()) * mRotScale);
                    rotY = ((mMouseY - mouseEvent.getY()) * mRotScale);
                }
            }            
            else if ((mouseEvent.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) > 0) {
                scale = ((mMouseY - mouseEvent.getY()) * mScaleScale);
            }
        }
        else {
            if (mouseEvent.isShiftDown()) {
                dX = (-(mMouseX - mouseEvent.getX()) * mMoveScale);
                dY = (-(mMouseY - mouseEvent.getY()) * mMoveScale);
            }
            else if (mouseEvent.isControlDown()) {
                scale = ((mMouseY - mouseEvent.getY()) * mScaleScale);
            }
            else {
                rotX = ((mMouseX - mouseEvent.getX()) * mRotScale);
                rotY = ((mMouseY - mouseEvent.getY()) * mRotScale);
            }
        }
        
        float r = 0.0f;
        float s = 0.0f;
        if (rotX != 0.0f) {
            r = Math.min(Math.abs(rotX), MOUSE_MAX) + MOUSE_OFFSET;
            s = (1.0f - (float)Math.cos((double)r)) * (rotX > 0 ? MOUSE_SCALE : -MOUSE_SCALE);
            mRotXAvg.input(s);       
            mRotX = mRotXAvg.value();
        }         
        if (rotY != 0.0f) {
            r = Math.min(Math.abs(rotY), MOUSE_MAX) + MOUSE_OFFSET;
            s = (1.0f - (float)Math.cos((double)r)) * (rotY > 0 ? MOUSE_SCALE : -MOUSE_SCALE);
            mRotYAvg.input(s);       
            mRotY = mRotYAvg.value();
        }        
        if (rotZ != 0.0f) {
            r = Math.min(Math.abs(rotZ), MOUSE_MAX) + MOUSE_OFFSET;
            s = (1.0f - (float)Math.cos((double)r)) * (rotZ > 0 ? MOUSE_SCALE : -MOUSE_SCALE);
            mRotZAvg.input(s);
            mRotZ = mRotZAvg.value();
        }
        if (dX != 0.0f) {        
            r = Math.min(Math.abs(dX), MOUSE_MAX);
            s = (1.0f - (float)Math.cos((double)r)) * (dX > 0 ? MOUSE_SCALE : -MOUSE_SCALE);
            mdXAvg.input(s);
            mdX = mdXAvg.value();
        }
        if (dY != 0.0f) {
            r = Math.min(Math.abs(dY), MOUSE_MAX);
            s = (1.0f - (float)Math.cos((double)r)) * (dY > 0 ? MOUSE_SCALE : -MOUSE_SCALE);
            mdYAvg.input(s);
            mdY = mdYAvg.value();
        }
        if (dZ != 0.0f) {
            r = Math.min(Math.abs(dZ), MOUSE_MAX);
            s = (1.0f - (float)Math.cos((double)r)) * (dZ > 0 ? MOUSE_SCALE : -MOUSE_SCALE);
            mdZAvg.input(s);
            mdZ = mdZAvg.value();
        }        

        mScaleAvg.input(scale);      
        mScale = 1.0f + mScaleAvg.value();
        
        mMouseX = mouseEvent.getX();
        mMouseY = mouseEvent.getY();
        mouseEvent.consume();
        
        resumeRenderer();
    }
    
    void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
        mouseEvent.consume();
    }
    
    void mouseExited(java.awt.event.MouseEvent mouseEvent) {
        mouseEvent.consume();
    }
    
    void mouseMoved(java.awt.event.MouseEvent mouseEvent) {                
        if (mSuspended) {
            checkToolTip(hitTest());
            //checkTrigger(shape);
        }
    
        mMouseX = mouseEvent.getX();
        mMouseY = mouseEvent.getY();
        mouseEvent.consume();
    }
        
    void mousePressed(java.awt.event.MouseEvent mouseEvent) {                        
        //mAllowAA = false;
        mAllowEaseOut = true;
        //mKeepAlive = KEEP_ALIVE;
        
        mMouseX = mouseEvent.getX();
        mMouseY = mouseEvent.getY();
        mouseEvent.consume();
        
        resumeRenderer();
    }
    
    void mouseReleased(java.awt.event.MouseEvent mouseEvent) {                
        if (!mAutoRotate) {
            mAllowAA = true;
            mAllowEaseOut = true;
        }
    
        //mKeepAlive = KEEP_ALIVE;
        
        mMouseX = mouseEvent.getX();
        mMouseY = mouseEvent.getY();
        mouseEvent.consume();
        
    }
    
    // -------------------------------------------------------------------
    // keyboard events
    // -------------------------------------------------------------------
    
    void keyPressed(KeyEvent keyEvent) {
        if (mScene == null)
            return;
        TransformNode sceneGraph = mScene.getSceneGraph();
        float scale = 1.0f / sceneGraph.getScale();
        
        switch (keyEvent.getKeyCode()) {
            /*
            case KeyEvent.VK_DOWN:
                mKeepAlive = KEEP_ALIVE;
                mdYAvg.input(scale);
                break;
            case KeyEvent.VK_UP:
                mKeepAlive = KEEP_ALIVE;
                mdYAvg.input(-scale);
                break;
            case KeyEvent.VK_LEFT:
                mKeepAlive = KEEP_ALIVE;
                mdXAvg.input(scale);
                break;
            case KeyEvent.VK_RIGHT:
                mKeepAlive = KEEP_ALIVE;
                mdXAvg.input(-scale);
                break;
            case KeyEvent.VK_Z:
                mKeepAlive = KEEP_ALIVE;
                mdZAvg.input(scale);
                break;
            case KeyEvent.VK_X:
                mKeepAlive = KEEP_ALIVE;
                mdZAvg.input(-scale);
                break;
            case KeyEvent.VK_C:
                mKeepAlive = KEEP_ALIVE;
                mScaleAvg.input(1.05f);
                break;
            case KeyEvent.VK_V:
                mKeepAlive = KEEP_ALIVE;
                mScaleAvg.input(0.95f);
                break;
            case KeyEvent.VK_R:
                mKeepAlive = KEEP_ALIVE;
                mResetTransform = true;
                mScaleAvg.flush();
                mdXAvg.flush();
                mdYAvg.flush();
                mdZAvg.flush();
                mRotXAvg.flush();
                mRotYAvg.flush();
                mRotZAvg.flush();
                break;
            case KeyEvent.VK_SHIFT:
                mKeepAlive = KEEP_ALIVE;
                mShiftDown = true;
                break;                            
             */
            
            case KeyEvent.VK_W:
                mKeepAlive = KEEP_ALIVE;
                mRasterizerChangeRequested = true;
                mNewRasterizer = TriangleRasterizer.WIREFRAME;
                break;
            case KeyEvent.VK_F:
                mKeepAlive = KEEP_ALIVE;
                mRasterizerChangeRequested = true;
                mNewRasterizer = TriangleRasterizer.FLAT;
                break;
            case KeyEvent.VK_G:
                mKeepAlive = KEEP_ALIVE;
                mRasterizerChangeRequested = true;
                mNewRasterizer = TriangleRasterizer.GOURAUD;
                break;
            case KeyEvent.VK_T:
                mKeepAlive = KEEP_ALIVE;
                mRasterizerChangeRequested = true;
                mNewRasterizer = TriangleRasterizer.TEXTURED;
                break;
        }
        
        mRotX = mRotXAvg.value();
        mRotY = mRotYAvg.value();
        mRotZ = mRotZAvg.value();
        mdX = mdXAvg.value();
        mdY = mdYAvg.value();
        mdZ = mdZAvg.value();
        mScale = 1.0f + mScaleAvg.value();
        
        keyEvent.consume();
        resumeRenderer();
    }
    
    void keyReleased(java.awt.event.KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                mShiftDown = false;
                break;
        }
        keyEvent.consume();
    }
    
    void keyTyped(java.awt.event.KeyEvent keyEvent) {
    }
    
    // -------------------------------------------------------------------
    // ImageConsumer interface
    // -------------------------------------------------------------------
    
    void imageComplete(int param) {
    }
    
    void setColorModel(java.awt.image.ColorModel colorModel) {
    }
    
    void setDimensions(int param, int param1) {
    }
    
    void setHints(int param) {
    }
    
    void setPixels(int param, int param1, int param2, int param3, java.awt.image.ColorModel colorModel, byte[] values, int param6, int param7) {
    }
    
    void setPixels(int param, int param1, int param2, int param3, java.awt.image.ColorModel colorModel, int[] values, int param6, int param7) {
    }
    
    void setProperties(java.util.Hashtable hashtable) {
    }           
    
}


