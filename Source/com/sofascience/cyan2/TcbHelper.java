/*
 * TcbHelper.java
 *
 * Created on October 15, 2002, 11:51 AM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  karl
 */
class TcbHelper {
    
    float np, nn, ksm, ksp, kdm, kdp;
    
    /** 
     *
     */
    TcbHelper() {
    }
    
    /**
     *
     */
    void init(TcbSpline p, TcbSpline pc, TcbSpline c, TcbSpline nc, TcbSpline n) {
        float tm, cm, cp, bm, bp, tmcm, tmcp, cc;
        float dt, fp, fn;

        if (pc == null)
            pc = c;
        if (nc == null)
            nc = c;

        fp = fn = 1.0f;
        if (p != null && n != null) {
            dt = 0.5f * (pc.mFrame - p.mFrame + n.mFrame - nc.mFrame);
            fp = (pc.mFrame - p.mFrame) / dt;
            fn = (n.mFrame - nc.mFrame) / dt;
            cc = Math.abs(c.mContinuity);
            fp = fp + cc - cc * fp;
            fn = fn + cc - cc * fn;
        }

        cm = 1.0f - c.mContinuity;
        tm = 0.5f * (1.0f - c.mTension);
        cp = 2.0f - cm;
        bm = 1.0f - c.mBias;
        bp = 2.0f - bm;      
        tmcm = tm * cm;
        tmcp = tm * cp;
        ksm = tmcm * bp * fp;
        ksp = tmcp * bm * fp;
        kdm = tmcp * bp * fn;
        kdp = tmcm * bm * fn;
    }
}
