package com.sofascience.cyan2.vecmath;

import java.io.Serializable;

/**
  * A 3 element point that is represented by signed integer x,y,z coordinates.
  * @since Java 3D 1.2
  * @version specification 1.2, implementation $Revision: 1.2 $, $Date: 2002/10/19 19:08:53 $
  * @author Kenji hiranabe
  */
public class Point3i extends Tuple3i implements Serializable {
/*
 * $Log: Point3i.java,v $
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
# Revision 1.2  1999/11/25  10:29:35  hiranabe
# Java3D 1.2 integer point
#
# Revision 1.2  1999/11/25  10:29:35  hiranabe
# Java3D 1.2 integer point
#
 */
    /**
      * Constructs and initializes a Point3i from the specified xyz coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      */
    public Point3i(int x, int y, int z) {
	super(x, y, z);
    }

    /**
      * Constructs and initializes a Point3i from the specified array.
      * @param t the array of length 3 containing xyz in order
      */
    public Point3i(int t[]) {
	super(t);
    }

    /**
      * Constructs and initializes a Point3i from the specified Point3i.
      * @param t1 the Point3i containing the initialization x y z data
      */
    public Point3i(Point3i t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Point3i to (0,0,0).
      */
    public Point3i() {
	// super(); called implicitly.
    }
}
