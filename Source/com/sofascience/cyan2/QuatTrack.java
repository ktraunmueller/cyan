/*
 * QuatTrack.java
 *
 * Created on September 27, 2002, 12:09 PM
 */

package com.sofascience.cyan2;

import java.util.Vector;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class QuatTrack extends Track {
    
    /** 
     * Creates a new instance of QuatTrack.
     */
    QuatTrack(Object target) {
        super(target);
    }
    
    /**
     *
     */
    String getType() {
        return "";//"QUATTRACK";
    }
    
    /**
     *
     */
    void setup() {
        QuatKey pp = null;
        QuatKey pc = null;
        QuatKey pn = null;
        QuatKey pl = null;
        Quat4fEx q = new Quat4fEx();

        for (pp = null, pc = (QuatKey)mKeys; pc != null; pp = pc, pc = (QuatKey)(pc.mNext)) {
            q.setAxisAngle(pc.mAxis, pc.mAngle);
            if (pp != null)
                pc.mQ.mul(q, pp.mQ);
            else
                pc.mQ.set(q);
        }

        pc = (QuatKey)mKeys;
        if (pc == null)
            return;
        if (pc.mNext == null) {
            pc.mDs.set(pc.mQ);
            pc.mDd.set(pc.mQ);
            return;
        }

        if ((mFlags & SMOOTH) > 0) {
            for (pl = (QuatKey)mKeys; pl.mNext.mNext != null; pl = (QuatKey)(pl.mNext));
            QuatKey.setup(pl, (QuatKey)(pl.mNext), pc, null, (QuatKey)(pc.mNext));
        }
        else {
            QuatKey.setup(null, null, pc, null, (QuatKey)(pc.mNext));
        }
        
        for (;;) {
            pp = pc;
            pc = (QuatKey)(pc.mNext);
            pn = (QuatKey)(pc.mNext);
            if (pn == null)
                break;
            QuatKey.setup(pp, null, pc, null, pn);
        }

        if ((mFlags & SMOOTH) > 0)
            QuatKey.setup(pp, null, pc, (QuatKey)(mKeys), (QuatKey)(mKeys.mNext));
        else
            QuatKey.setup(pp, null, pc, null, null);
    }
    
    /**
     *
     */
    boolean eval(int time) {
        Quat4fEx target = (Quat4fEx)mTarget;
        QuatKey k = null;        

        if (mKeys == null) {
            target.set(0.0f, 0.0f, 0.0f, 1.0f);
            return false;
        }
        if (mKeys.mNext == null) {
            target.set(((QuatKey)mKeys).mQ);
            return false;
        }

        for (k = (QuatKey)mKeys; k.mNext != null; k = (QuatKey)(k.mNext)) {
            if ((time >= k.mTcb.mFrame) && (time < k.mNext.mTcb.mFrame))
                break;
        }
        
        int nt = time;
        if (k.mNext == null) {
            if ((mFlags & REPEAT) > 0) {
                nt = time % k.mTcb.mFrame;
                for (k = (QuatKey)mKeys; k.mNext != null; k = (QuatKey)(k.mNext)) {
                    if ((nt >= k.mTcb.mFrame) && (nt < k.mNext.mTcb.mFrame))
                        break;
                } 
            }
            else {
                target.set(k.mQ);
                return true;
            }
        }
        else {
            nt = time;
        }
        
        float u = nt - k.mTcb.mFrame;
        u /= (k.mNext.mTcb.mFrame - k.mTcb.mFrame);

        target.squad(k.mQ, k.mDd, ((QuatKey)(k.mNext)).mDs, ((QuatKey)(k.mNext)).mQ, u );
        return true;
    }
    
}
