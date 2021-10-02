/*
 * Button.java
 *
 * Created on January 23, 2003, 8:14 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl
 */
class Button {
    
    private int     mLeft, mTop, mWidth, mHeight;
    private Texture mJpgNormal, mJpgPressed;
    private Bitmap  mBmpNormal, mBmpPressed;
    private Color   mMask;
    private boolean mUseMask;
    private int[]   mNormalPixels;
    private int[]   mPressedPixels;
    private boolean mIsPressed;
    private boolean mIsToggleButton;
    
    /** 
     *
     */
    Button(int left, int top, java.net.URL normal, java.net.URL pressed, Color mask) {
        mLeft = left;
        mTop = top;
        if (normal.getFile().endsWith(".bmp")) {
            mBmpNormal = new Bitmap(normal);
            mNormalPixels = mBmpNormal.getPixels();
            mWidth = mBmpNormal.getWidth();
            mHeight = mBmpNormal.getHeight();
            mUseMask = true;
        }
        else {
            mJpgNormal = new Texture(normal);
            mNormalPixels = mJpgNormal.getPixels();
            mWidth = mJpgNormal.getWidth();
            mHeight = mJpgNormal.getHeight();
        }
        if (pressed.getFile().endsWith(".bmp")) {
            mBmpPressed = new Bitmap(pressed);
            mPressedPixels = mBmpPressed.getPixels();
            mWidth = mBmpPressed.getWidth();
            mHeight = mBmpPressed.getHeight();
            mUseMask = true;
        }
        else {
            mJpgPressed = new Texture(pressed);
            mPressedPixels = mJpgPressed.getPixels();
            mWidth = mJpgPressed.getWidth();
            mHeight = mJpgPressed.getHeight();
        }        
        mMask = mask; 
    }
    
    Button(int left, int top, String normal, String pressed, Color mask) {
        mLeft = left;
        mTop = top;
        if (normal.endsWith(".bmp")) {
            mBmpNormal = new Bitmap(normal);
            mNormalPixels = mBmpNormal.getPixels();
            mWidth = mBmpNormal.getWidth();
            mHeight = mBmpNormal.getHeight();
            mUseMask = true;
        }
        else {
            mJpgNormal = new Texture(normal);
            mNormalPixels = mJpgNormal.getPixels();
            mWidth = mJpgNormal.getWidth();
            mHeight = mJpgNormal.getHeight();
        }
        if (pressed.endsWith(".bmp")) {
            mBmpPressed = new Bitmap(pressed);
            mPressedPixels = mBmpPressed.getPixels();
            mWidth = mBmpPressed.getWidth();
            mHeight = mBmpPressed.getHeight();
            mUseMask = true;
        }
        else {
            mJpgPressed = new Texture(pressed);
            mPressedPixels = mJpgPressed.getPixels();
            mWidth = mJpgPressed.getWidth();
            mHeight = mJpgPressed.getHeight();
        }
        mMask = mask; 
    }
    
    final boolean hitTest(java.awt.Point p) {
        if (p.x < mLeft || p.x >= mLeft + mWidth || p.y < mTop || p.y >= mTop + mHeight)
            return false;

        if (mNormalPixels == null || !mUseMask)
            return true;
        
        int x = p.x - mLeft;
        int y = p.y - mTop;
        int index = (y * mWidth) + x;
        if (index < 0 || index > mNormalPixels.length)
            return false;
        return (mNormalPixels[index] & 0x00FFFFFF) != ((mMask.mRed << 16) | (mMask.mGreen << 8) | (mMask.mBlue));
    }
    
    final void setToggleButton(boolean toggle) {
        mIsToggleButton = toggle;
    }
    
    final void setPressed(boolean pressed) {
        mIsPressed = pressed;
    }
    
    final boolean isPressed() {
        return mIsPressed;
    }
    
    final void draw(int[] imagePixels, int imageScanline) {
        if (imagePixels == null)
            return;
        
        int[] pixels = mIsPressed ? mPressedPixels : mNormalPixels;
        if (pixels == null)
            return;

        int imageOffset = mTop * imageScanline + mLeft;
        int offset = 0;
        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {                
                int pixel = pixels[offset];
                if (!mUseMask || (mUseMask && pixel != mMask.mColor)) {                    
                    int pixelBlue = (pixel & 0x00FF0000) >> 16;
                    int pixelGreen = (pixel & 0x0000FF00) >> 8;
                    int pixelRed = (pixel & 0x000000FF);
                    int imagePixel = imagePixels[imageOffset];               
                    int imagePixelRed = (imagePixel & 0x00FF0000) >> 16;
                    int imagePixelGreen = (imagePixel & 0x0000FF00) >> 8;
                    int imagePixelBlue = (imagePixel & 0x000000FF);
                    int color = 0xFF000000 | 
                                (((pixelRed + imagePixelRed) >> 1) << 16) |
                                (((pixelGreen + imagePixelGreen) >> 1) << 8) |
                                ((pixelBlue + imagePixelBlue) >> 1);
                    imagePixels[imageOffset] = color;
                }
                offset++;
                imageOffset++;
            }
            imageOffset += (imageScanline - mWidth);
        }
    }
}

