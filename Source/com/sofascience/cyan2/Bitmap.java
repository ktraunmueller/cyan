/*
 * Bitmap.java
 *
 * Created on September 27, 2002, 1:27 PM
 */

package com.sofascience.cyan2;

import java.net.URL;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.Hashtable;

/**
 *
 * @author  Karl Traunmueller
 */
class Bitmap extends SceneGraphObject {

    static Hashtable    cache   = new Hashtable();
    
    int     mWidth, mHeight;
    int     mBitsPerPixel;
    int     mCompression;
    int[]   mPixels;
    
    Bitmap(URL url) {
        load(url);
    }
    
    Bitmap(String url) {
        URL file = null;
        try {
            file = new URL(url);
        }
        catch (java.net.MalformedURLException ex) {
        }
        load(file);
    }
    
    final void initFrom(Bitmap bm) {
        this.mWidth = bm.mWidth;
        this.mHeight = bm.mHeight;
        this.mBitsPerPixel = bm.mBitsPerPixel;
        this.mCompression = bm.mCompression;
        this.mPixels = bm.mPixels;
    }
    
    final int getWidth() {
        return mWidth;
    }
    
    final int getHeight() {
        return mHeight;
    }
    
    final int[] getPixels() {
        return mPixels;
    }
    
    private void load(URL url) {
        if (cache.containsKey(url.toString())) {
            Bitmap bm = (Bitmap)cache.get(url.toString());
            //System.out.println("Found bitmap " + url.toString() + " in cache");
            this.initFrom(bm);
            return;
        }
        
        if (url != null) {
            try {
                BufferedInputStream stream = new BufferedInputStream(url.openStream());
                readFileHeader(stream);
                readInfoHeader(stream);
                readPixelData(stream);
                stream.close();
            }
            catch (java.io.IOException ex) {
            }
        }
        
        cache.put(url.toString(), this);
    }
    
    private void readFileHeader(InputStream stream) throws java.io.IOException {
        if (stream == null)
            return;

        byte ident1 = (byte)readByte(stream);
        byte ident2 = (byte)readByte(stream);
        if (ident1 != 'B' || ident2 != 'M')
            return;
        
        int fileSize = readInt32(stream);
        int reserved = readInt32(stream);
        int bitmapDataOffset = readInt32(stream);
    }
    
    private void readInfoHeader(InputStream stream) throws java.io.IOException {
        if (stream == null)
            return;
        
        int infoSize = readInt32(stream);
        mWidth = readInt32(stream);
        mHeight = readInt32(stream);
        int planes = readInt16(stream);
        mBitsPerPixel = readInt16(stream);        
        int compression = readInt32(stream);
        int size = readInt32(stream);
        int xPixelsPerMeter = readInt32(stream);
        int yPixelsPerMeter = readInt32(stream);
        int colors = readInt32(stream);
        int colorsImportant = readInt32(stream);
    }
    
    private void readPixelData(InputStream stream) throws java.io.IOException {
        if (stream == null)
            return;
        if (mBitsPerPixel != 24)
            return;
        float fp = (mWidth * 3.0f / 4.0f);
        float ffloor = (float)Math.floor(fp);
        int pad = (int)((1.0f - (fp - ffloor)) * 4.0f);
        int nrPixels = mWidth * mHeight;
        mPixels = new int[nrPixels];
        for (int y = mHeight - 1; y >= 0; y--) {
            for (int x = 0; x < mWidth; x++) {
                mPixels[y * mWidth + x] = readRgb(stream);
            }
            if (pad == 1)
                readByte(stream);
            else if (pad == 2)
                readInt16(stream);
            else if (pad == 3)
                readRgb(stream);
        }
    }
    
    private int readByte(InputStream stream) throws java.io.IOException {
        if (stream == null)
            return -1;
        return stream.read();
    }
    
    private int readInt16(InputStream stream) throws java.io.IOException {
        if (stream == null)
            return -1;
        int byte1 = stream.read();
        int byte2 = stream.read();
        return ((byte2 << 8) | byte1);
    }
    
    private int readInt32(InputStream stream) throws java.io.IOException {
        if (stream == null)
            return -1;
        int word1 = readInt16(stream);
        int word2 = readInt16(stream);
        return ((word2 << 16) | word1);
    }
    
    private int readRgb(InputStream stream) throws java.io.IOException {
        if (stream == null)
            return -1;
        int r = readByte(stream);
        int g = readByte(stream);
        int b = readByte(stream);
        if (r == -1 || g == -1 || b == -1)
            return -1;
        return (0xFF000000 | (r << 16) | (g << 8) | b);
    }
}
