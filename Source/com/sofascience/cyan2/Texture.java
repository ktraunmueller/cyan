/*
 * Texture.java
 *
 * Created on February 23, 2002, 3:14 PM
 */

package com.sofascience.cyan2;

import java.net.URL;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

/**
 * A texture image
 *
 * @author  Karl Traunmueller
 */
class Texture implements ImageObserver {

    String          mFilename;                              
    Image           mImg;
    PixelGrabber    mPixelGrabber;
    int             mWidth, mHeight;
    int[]           mPixels;
    
    /**
     * Default constructor.
     */
    Texture() {
    }
    
    /**
     * Creates a texture from the given filename.
     */     
    Texture(String file) {
        mFilename = file;
        mImg = Toolkit.getDefaultToolkit().getImage(file);
        mWidth = mImg.getWidth(this);
        mHeight = mImg.getHeight(this);
        getPixels();
    }
    
    Texture(URL url) {
        mFilename = url.getFile();
        mImg = Toolkit.getDefaultToolkit().getImage(url);
        mWidth = mImg.getWidth(this);
        mHeight = mImg.getHeight(this);
        getPixels();
    }
    
    // --------------------------------------------------------------
    // image access 
    // --------------------------------------------------------------
    
    /**
     * Retrieves the texture image.
     */
    final Image getImage() {
        return mImg;
    }
    
    /**
     * Retrieves the texture's width.
     */
    final int getWidth() {
        mWidth = mImg.getWidth(this);
        return mWidth;
    }
    
    /**
     * Retrieves the texture's height.
     */
    final int getHeight() {
        mHeight = mImg.getHeight(this);
        return mHeight;
    }
    
    /**
     * Returns true when the width and height of the texture image are known.
     */
    boolean imageSizeKnown() {
        return (mWidth > 0 && mHeight > 0);
    }
    
    /**
     * Retrieves the texture's pixel data array.
     */
    final int[] getPixels() {
        if (mPixelGrabber == null) {
            mPixelGrabber = new PixelGrabber(mImg, 0, 0, mWidth, mHeight, true);
            try {
                mPixelGrabber.grabPixels();
            }
            catch (InterruptedException ex) {
            }
        }
        if (mPixelGrabber != null)
            return (int[])(mPixelGrabber.getPixels());
        return null;
    }
    
    // --------------------------------------------------------------
    // ImageObserver interface
    // --------------------------------------------------------------
    
    /**
     * Stores information about the image (asynchronous callback).
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & ImageObserver.WIDTH) > 0)
            mWidth = width;
        if ((infoflags & ImageObserver.HEIGHT) > 0)
            mHeight = height;
        
        return (!imageSizeKnown());
    }
    
}
