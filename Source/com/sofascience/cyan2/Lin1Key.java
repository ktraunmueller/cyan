/*
 * Lin1Key.java
 *
 * Created on September 27, 2002, 12:58 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class Lin1Key extends Key {
    
    FloatValue mValue;
    FloatValue mDd;
    FloatValue mDs;

    /** Creates a new instance of Lin1Key */
    Lin1Key() {
        mValue = new FloatValue();
        mDd = new FloatValue();
        mDs = new FloatValue();
    }
    
    static void setup(Lin1Key p, Lin1Key cp, Lin1Key c, Lin1Key cn, Lin1Key n) {
        if (cp == null)
            cp = c;
        if (cn == null)
            cn = c;
        if (p == null && n == null) {
            c.mDs.mFloatVal = 0.0f;
            c.mDd.mFloatVal = 0.0f;
            return;
        }

        TcbHelper helper = new TcbHelper();
        float np, nn;
        
        if (n != null && p != null) {
            helper.init(p.mTcb, cp.mTcb, c.mTcb, cn.mTcb, n.mTcb);
            np = c.mValue.mFloatVal - p.mValue.mFloatVal; 
            nn = n.mValue.mFloatVal - c.mValue.mFloatVal; 

            c.mDs.mFloatVal = helper.ksm * np + helper.ksp * nn;
            c.mDd.mFloatVal = helper.kdm * np + helper.kdp * nn;
        }
        else {
            if (p != null) {
                np = c.mValue.mFloatVal - p.mValue.mFloatVal;
                c.mDs.mFloatVal = np;
                c.mDd.mFloatVal = np;
            }
            if (n != null) {
                nn = n.mValue.mFloatVal - c.mValue.mFloatVal; 
                c.mDs.mFloatVal = nn;
                c.mDd.mFloatVal = nn;
            }
        }
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- LIN1KEY");
        mTcb.dump(level + 1);
        indent(level + 1); System.out.println("value: " + mValue);
        indent(level + 1); System.out.println("dd: " + mDd);
        indent(level + 1); System.out.println("ds: " + mDs);
    }
     */
    
}
