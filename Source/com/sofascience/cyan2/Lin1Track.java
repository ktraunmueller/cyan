/*
 * Lin1Track.java
 *
 * Created on September 27, 2002, 12:58 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class Lin1Track extends Track {
    
    /** Creates a new instance of Lin1Track */
    Lin1Track(Object target) {
        super(target);
    }
    
    /**
     *
     */
    String getType() {
        return "";//"LIN1TRACK";
    }
        
    /**
     *
     */
    void setup() {
        Lin1Key pp = null;
        Lin1Key pc = null;
        Lin1Key pn = null;
        Lin1Key pl = null;

        pc = (Lin1Key)mKeys;
        if (pc == null)
            return;
        if (pc.mNext == null) {
            pc.mDs.mFloatVal = 0;
            pc.mDd.mFloatVal = 0;
            return;
        }

        if ((mFlags & SMOOTH) > 0) {
            for (pl = (Lin1Key)mKeys; pl.mNext.mNext != null; pl = (Lin1Key)(pl.mNext));
            Lin1Key.setup(pl, (Lin1Key)(pl.mNext), pc, null, (Lin1Key)(pc.mNext));
        }
        else {
            Lin1Key.setup(null, null, pc, null, (Lin1Key)(pc.mNext));
        }
        
        for (;;) {
            pp = pc;
            pc = (Lin1Key)(pc.mNext);
            pn = (Lin1Key)(pc.mNext);
            if (pn == null)
              break;
            Lin1Key.setup(pp, null, pc, null, pn);
        }

        if ((mFlags & SMOOTH) > 0)
            Lin1Key.setup(pp, null, pc, (Lin1Key)mKeys, (Lin1Key)(mKeys.mNext));
        else
            Lin1Key.setup(pp, null, pc, null, null);
    }
    
    /**
     *
     */
    boolean eval(int time) {
        FloatValue val = (FloatValue)mTarget;
        if (mKeys == null) {
            val.mFloatVal = 0.0f;
            return false;
        }
        if (mKeys.mNext == null) {
            val.mFloatVal = ((Lin1Key)mKeys).mValue.mFloatVal;
            return false;
        }

        Key k = null;
        int nt;
        for (k = mKeys; k.mNext != null; k = k.mNext) {
            if ((time >= k.mTcb.mFrame) && (time < k.mNext.mTcb.mFrame))
                break;
        }
        if (k.mNext == null) {
            if ((mFlags & REPEAT) > 0) {
                nt = time % k.mTcb.mFrame;
                for (k = mKeys; k.mNext != null; k = k.mNext) {
                    if ((nt >= k.mTcb.mFrame) && (nt < k.mNext.mTcb.mFrame))
                        break;
                }
            }
            else {
                val.mFloatVal = ((Lin1Key)k).mValue.mFloatVal;
                return true;
            }
        }
        else {
            nt = time;
        }
        float u = nt - k.mTcb.mFrame;
        u /= (k.mNext.mTcb.mFrame - k.mTcb.mFrame);

        val.mFloatVal = Cubic.cubic(((Lin1Key)k).mValue.mFloatVal, ((Lin1Key)k).mDd.mFloatVal,
                                    ((Lin1Key)k.mNext).mDs.mFloatVal, ((Lin1Key)k.mNext).mValue.mFloatVal, u);
        return true;
    }
    
}
