package com.sofascience.cyan2.vecmath;

import java.io.Serializable;

/**
  * A 4 element point that is represented by signed integer x,y,z and w coordinates.
  * @since Java 3D 1.2
  * @version specification 1.2, implementation $Revision: 1.2 $, $Date: 2002/10/19 19:08:53 $
  * @author Kenji hiranabe
  */
public class Point4i extends Tuple4i implements Serializable {
/*
 * $Log: Point4i.java,v $
 * Revision 1.2  2002/10/19 19:08:53  cvs
 * no message
 *
 * Revision 1.1.1.1  2002/10/12 23:36:13  root
 * Moved to srcserver
 *
 * Revision 1.1.1.1  2002/06/22 18:40:40  karl
 * re-added cyan code
 *
 * Revision 1.1  2002/06/21 16:01:07  karl
 * *** empty log message ***
 *
# Revision 1.1  1999/11/25  10:29:35  hiranabe
# Initial revision
#
# Revision 1.1  1999/11/25  10:29:35  hiranabe
# Initial revision
#
 */
    /**
      * Constructs and initializes a Point4i from the specified xyzw coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      * @param w the w coordinate
      */
    public Point4i(int x, int y, int z, int w) {
	super(x, y, z, w);
    }

    /**
      * Constructs and initializes a Point4i from the specified array.
      * @param t the array of length 4 containing xyzw in order
      */
    public Point4i(int t[]) {
	super(t);
    }

    /**
      * Constructs and initializes a Point4i from the specified Point4i.
      * @param t1 the Point4i containing the initialization x y z w data
      */
    public Point4i(Point4i t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Point4i to (0,0,0,0).
      */
    public Point4i() {
	// super(); called implicitly.
    }
}
