/*
 * Color.java
 *
 * Created on June 6, 2002, 11:42 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
public class Color {
    
    /** color components */
    static final int ALPHA_CH   = 3;
    static final int RED_CH     = 2;
    static final int GREEN_CH   = 1;
    static final int BLUE_CH    = 0;
    
    /** predefined colors */
    static final int BLACK = 0xFF000000;
    static final int WHITE = 0xFFFFFFFF;
    static final int GREY  = 0xFF808080;    
    static final int RED   = 0xFFFF0000;
    static final int GREEN = 0xFF00FF00;
    static final int BLUE  = 0xFF0000FF;
    
    
    /** color in ARGB format */
    int mColor;
    int mRed, mGreen, mBlue;
    
    /** 
     * Constructs a white color.
     */
    public Color() {
        mColor = 0xFFFFFFFF;
        mRed = 255;
        mGreen = 255;
        mBlue = 255;
    }

    /**
     * Constructs a new color from the given color.
     */
    public Color(Color color) {
        mColor = color.mColor;
        mRed = color.mRed;
        mGreen = color.mGreen;
        mBlue = color.mBlue;
    }
    
    /**
     * Creates a color from the given color.
     */
    public Color(int color) {
        mColor = color;
        mRed = getComponent(2);
        mGreen = getComponent(1);
        mBlue = getComponent(0);
    }

    /**
     * Creates a color from the given RGB values.
     */
    public Color(int red, int green, int blue) {
        setRGB(red, green, blue);
    }
    
    /**
     * Sets the RGB values for this color.
     */
    void setRGB(int red, int green, int blue) {
        mRed = red;
        mGreen = green;
        mBlue = blue;
        mColor = 0xFF000000 | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }
    
    /**
     * Retrieves the ARGB 4-byte.
     */
    final int getColor() {
        return mColor;
    }
    
    /**
     * Sets this color's color to color's color :)
     */
    final void set(Color color) {
        mColor = color.mColor;
        mRed = color.mRed;
        mGreen = color.mGreen;
        mBlue = color.mBlue;
    }

    /**
     * Sets this color's color to the given color :)
     */
    final void set(int color) {
        mColor = color;
        mRed = getComponent(2);
        mGreen = getComponent(1);
        mBlue = getComponent(0);
    }
    
    /**
     * Returns one of three color components (RED, GREEN, BLUE).
     */
    final int getComponent(int component) {
        int shift = component << 3;
        int mask = 0xFF << shift;
        return (mColor & mask) >> shift;
    }

    /**
     *
     */
    static int getColorComponent(int color, int component) {
        int shift = component << 3;
        int mask = 0xFF << shift;
        return (color & mask) >> shift;
    }
    
    /**
     *
     */
    final void modulate(float s, int red, int green, int blue) {
        int _alpha  = getComponent(Color.ALPHA_CH);
        int _red    = getComponent(Color.RED_CH);
        int _green  = getComponent(Color.GREEN_CH);
        int _blue   = getComponent(Color.BLUE_CH);
        
        mRed   = (int)(((_red + red) >> 1) * s);
        mRed   = mRed > 255 ? 255 : mRed;
        mGreen = (int)(((_green + green) >> 1)* s);
        mGreen = mGreen > 255 ? 255 : mGreen;
        mBlue  = (int)(((_blue + blue) >> 1) * s);
        mBlue  = mBlue > 255 ? 255 : mBlue;
        
        mColor = (_alpha & 0xFF) << 24 | (mRed & 0xFF) << 16 | (mGreen & 0xFF) << 8 | (mBlue & 0xFF);
    }
    
    /**
     *
     */
    final void add(int c) {
        int _alpha  = getComponent(Color.ALPHA_CH);
        
        int alpha   = Color.getColorComponent(c, Color.ALPHA_CH);
        int red     = Color.getColorComponent(c, Color.RED_CH);
        int green   = Color.getColorComponent(c, Color.GREEN_CH);
        int blue    = Color.getColorComponent(c, Color.BLUE_CH);
        
        _alpha = ((_alpha + alpha) >> 1);
        _alpha = _alpha > 255 ? 255 : _alpha;
        mRed   = ((mRed + red) >> 1);
        mRed   = mRed > 255 ? 255 : mRed;
        mGreen = ((mGreen + green) >> 1);
        mGreen = mGreen > 255 ? 255 : mGreen;
        mBlue  = ((mBlue + blue) >> 1);
        mBlue  = mBlue > 255 ? 255 : mBlue;
        
        mColor = (_alpha & 0xFF) << 24 | (mRed & 0xFF) << 16 | (mGreen & 0xFF) << 8 | (mBlue & 0xFF);
    }
}
