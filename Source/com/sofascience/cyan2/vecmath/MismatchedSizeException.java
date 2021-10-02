package com.sofascience.cyan2.vecmath;

/**
 * Indicates that an operation cannot be completed properly
 * because of a mismatch in the sizes of object attributes.
 *
 * @version specification 1.1, implementation $Revision: 1.2 $, $Date: 2002/10/19 19:08:53 $
 * @author Kenji hiranabe
 */
public class MismatchedSizeException extends RuntimeException {
    /**
      * Creates the exception object with default values.
      */
    public MismatchedSizeException() {
    }

    /**
      * Creates the exception object that outputs a message.
      * @param str the message string to output
      */
    public MismatchedSizeException(String str) {
        super(str);
    }
}
