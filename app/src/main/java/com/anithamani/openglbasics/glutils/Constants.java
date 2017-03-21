package com.anithamani.openglbasics.glutils;

/**
 * Constant values
 */

public class Constants {

    public static final int NO_OF_COORD_IN_VERTEX = 3;
    public static final int NO_OF_COORD_IN_COLOR = 4;

    public static final int VERTEX_STRIDE = Constants.NO_OF_COORD_IN_VERTEX * 4; // 4 bytes per vertex
    public static final int COLOR_STRIDE = Constants.NO_OF_COORD_IN_COLOR * 4; // 4 bytes per vertex

    public static final byte X_AXIS = 0;
    public static final byte Y_AXIS = 1;
    public static final byte Z_AXIS = 2;
}
