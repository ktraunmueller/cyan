/*
 * Lin3Track.java
 *
 * Created on September 27, 2002, 11:47 AM
 */

package com.sofascience.cyan2;

import java.util.Vector;
import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class Lin3Track extends Track {
    
    Vector3f mResetValue;
    
    /** Creates a new instance of Lin3Track */
    Lin3Track(Tuple3f target) {
        super(target);
        mResetValue = new Vector3f(0.0f, 0.0f, 0.0f);
    }
    
    /**
     *
     */
    void setResetValue(float x, float y, float z) {
        mResetValue.set(x, y, z);
    }
    
    /**
     *
     */
    String getType() {
        return "";//"LINTRACK";
    }
    
    /**
     *
     */
    void setup() {
        Lin3Key pp = null;
        Lin3Key pc = null;
        Lin3Key pn = null;
        Lin3Key pl = null;

        pc = (Lin3Key)mKeys;
        if (pc == null)
            return;

        if (pc.mNext == null) {
            pc.mDs.set(0.0f, 0.0f, 0.0f);
            pc.mDd.set(0.0f, 0.0f, 0.0f);
            return;
        }

        if ((mFlags & SMOOTH) > 0) {
            for (pl = (Lin3Key)mKeys; pl.mNext.mNext != null; pl = (Lin3Key)(pl.mNext));
            Lin3Key.setup(pl, (Lin3Key)(pl.mNext), pc, null, (Lin3Key)(pc.mNext));
        }
        else {
            Lin3Key.setup(null, null, pc, null, (Lin3Key)(pc.mNext));
        }
        
        for (;;) {
            pp = pc;
            pc = (Lin3Key)(pc.mNext);
            pn = (Lin3Key)(pc.mNext);
            if (pn == null)
              break;
            Lin3Key.setup(pp, null, pc, null, pn);
        }

        if ((mFlags & SMOOTH) > 0)
            Lin3Key.setup(pp, null, pc, (Lin3Key)mKeys, (Lin3Key)(mKeys.mNext));
        else
            Lin3Key.setup(pp, null, pc, null, null);
    }
    
    /**
     *
     */
    boolean eval(int time) {
        Tuple3f target = (Tuple3f)mTarget;
        if (mKeys == null) {
            target.set(mResetValue);
            return false;
        }
        if (mKeys.mNext == null) {
            target.set(((Lin3Key)mKeys).mValue);
            return false;
        }

        Key k = null;
        for (k = mKeys; k.mNext != null; k = k.mNext) {
            if ((time >= k.mTcb.mFrame) && (time < k.mNext.mTcb.mFrame))
                break;
        }
        
        int nt;
        if (k.mNext == null) {
            if ((mFlags & REPEAT) > 0) {
                nt = time % k.mTcb.mFrame;
                for (k = mKeys; k.mNext != null; k = k.mNext) {
                    if ((nt >= k.mTcb.mFrame) && (nt < k.mNext.mTcb.mFrame))
                        break;
                }
            }
            else {
                target.set(((Lin3Key)k).mValue);
                return true;
            }
        }
        else {
            nt = time;
        }
        float u = nt - k.mTcb.mFrame;
        u /= (k.mNext.mTcb.mFrame - k.mTcb.mFrame);

        Cubic.vectorCubic(target, ((Lin3Key)k).mValue, ((Lin3Key)k).mDd, ((Lin3Key)k.mNext).mDs,
                          ((Lin3Key)k.mNext).mValue, u);
        return true;
    }
    
}
