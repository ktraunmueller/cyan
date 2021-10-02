/*
 * GaliNuva.java
 *
 * Created on October 25, 2002, 2:03 PM
 */

import java.applet.Applet;
import java.net.URL;

import java.awt.Point;
import com.sofascience.cyan2.*;

/**
 *
 * @author  karl
 */
public final class GaliNuva extends Applet {
    
    protected Canvas3D mCanvas;
    
    /** 
     *
     */
    public void init() { 
        String sWidth = getParameter("width");
        String sHeight = getParameter("height");
        String sSupersampling = getParameter("supersampling");
        int width = java.lang.Integer.parseInt(sWidth);
        int height = java.lang.Integer.parseInt(sHeight);
        int supersampling = 2;
        if (sSupersampling != null)
            supersampling = java.lang.Integer.parseInt(sSupersampling);        
        
        // create canvas
        mCanvas = new Canvas3D(width, height, supersampling);        
        
        // set progress params        
        mCanvas.setNrModels(1);
        mCanvas.setNrTextures(0);
        mCanvas.setNrTooltips(0);
        mCanvas.setStatusColor(java.awt.Color.black);
        mCanvas.setBarColor(java.awt.Color.lightGray);        
        mCanvas.setScale(0.004f);
        mCanvas.setOrigin(0.0f, -0.4f, -10.0f);               
        mCanvas.rotate(0.0f, 0.2f, -0.9f);
        mCanvas.enableAutoRotate(false);
        
        addKeyListener(mCanvas);
        addMouseListener(mCanvas);        
        addMouseMotionListener(mCanvas);        
        
        setLayout(new java.awt.BorderLayout());       
        add(mCanvas, "North");     
       
        setVisible(true);
    }        
    
    public void start() {                                 
        String sLoading = getParameter("loadingScreen");                               
        
        URL codebase = getCodeBase();
        
        // prepare loading screen
        if (sLoading != null) {            
            URL loading = null;
            try {
                loading = new URL(codebase.getProtocol(), codebase.getHost(), codebase.getPort(), codebase.getFile() + sLoading);
            }
            catch (java.net.MalformedURLException ex) {
            }
            mCanvas.setLoadingScreen(loading);
        }                 
        
        String sModel = getParameter("model");        
        String sBg = getParameter("background");
               
        if (sBg != null) {
            URL bg = null;
            try {
                bg = new URL(codebase.getProtocol(), codebase.getHost(), codebase.getPort(),  codebase.getFile() + sBg);
            }
            catch (java.net.MalformedURLException ex) {
            }
            mCanvas.setBackground(bg);
        }
        
        URL GaliNuva = null;
        try {
            GaliNuva = new URL(codebase.getProtocol(), codebase.getHost(), codebase.getPort(),  codebase.getFile() + sModel);
        }
        catch (java.net.MalformedURLException ex) {
        }
        
        if (sModel.substring(sModel.length() - 4).compareTo(".zip") == 0)
            mCanvas.loadZip(codebase, GaliNuva);
        else
            mCanvas.load(codebase, GaliNuva);
        
        mCanvas.enableDisplayStats(false);
    }
    
    public void stop() {
        mCanvas.stopRenderer();
    }
    
    public void destroy() {
        stop();
    }    
    
    public void update(java.awt.Graphics g) {
        mCanvas.update(g);
    }
    
    public void paint(java.awt.Graphics g) {
        mCanvas.paint(g);
    }
        
}
