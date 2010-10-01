/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.five_stones_project;

/**
 *
 * @author Tangl
 */

//this stores the mints that necessery to the game
class Mints{
    /////////////////////////////////EASY//////////////////////////////////////
    public static final int[][] threes_twos={{5,0,2,2,2,5},{0,1,1,1,0},
                                        {5,0,2,2,2,1},{0,2,2,0},{0,1,1,1,2},
                                        {0,1,1,0},{0,2,2,1},{0,1,1,2}};
    public static final int[] threes_twos_descriptor={2,1,2,2,1,1,2,1};
    public static final int[][] fours={{0,2,2,2,2},{0,1,1,1,1}};
    public final static int[] fours_descriptor={2,1};
    ///////////////////////////////MEDIUM//////////////////////////////////////
    public static final int[][] _threes_twos={{5,0,2,2,2,5},{5,2,0,2,2,5},
                                        {0,1,1,1,0},{5,1,0,1,1,5},{5,0,2,2,2,1},
                                        {0,2,2,0,2,1},{0,2,0,2,2,1},{5,2,2,0,0},
                                        {5,2,0,2,5,1},{0,2,0,2,0},{5,2,0,0,2,5},
                                        {0,1,1,1,2},{0,1,1,0,1,2},{0,1,0,1,1,2},
                                        {5,1,1,0,0},{0,1,0,1,0},{5,1,0,0,1,5},
                                        {0,2,2,1},{0,2,0,2,1},{0,1,1,2},{0,1,0,1,2}};
    public static final int[] _threes_twos_descriptor=
                                        {2,2,1,1,2,2,2,2,2,2,2,1,1,1,1,1,1,2,2,1,1};
    public static final int[][] _fours={{0,2,2,2,2},{2,0,2,2,2},{2,2,0,2,2},
                                        {0,1,1,1,1},{1,0,1,1,1},{1,1,0,1,1}};
    public final static int[] _fours_descriptor={2,2,2,1,1,1};
    /////////////////////////////////HARD//////////////////////////////////////
    public static final int[][] threes={{5,0,2,2,2,5},{5,2,0,2,2,5},
                                        {0,1,1,1,0},{5,1,0,1,1,5},{5,0,2,2,2,1},
                                        {0,2,2,0,2,1},{0,2,0,2,2,1},{0,2,2,0},
                                        {5,2,0,2,5,1},{0,2,0,2,0},{0,1,1,1,2},
                                        {0,1,1,0,1,2},{0,1,0,1,1,2}};
    public static final int [] threes_descriptor={2,2,1,1,2,2,2,2,2,2,1,1,1};
    public static final int[][] twos={{0,1,1,0},{0,1,0,1,0},{0,2,2,1},{0,2,0,2,1},
                                      {5,2,0,0,2,5},{0,1,1,2},{0,1,0,1,2},{5,1,0,0,1,5}};
    public static final int[] twos_descriptor={1,1,2,2,2,1,1,1};
    public static final int[][] hard_x_threes={{0,5,1,1,1},{0,1,1,1,0},{0,1,0,1,1,0},
                                               {1,0,1,0,1},{0,5,1,1,1,2}};
    public static final int[] hard_x_threes_descriptor={1,1,1,1,1};
    public static final int[][] hard_x_twos={{5,1,1,0,0},{5,1,0,1,5}};
    public static final int[] hard_x_twos_descriptor={1,1};
    public static final int[][] hard_o_threes={{0,5,2,2,2},{0,2,2,2,0},{0,2,0,2,2,0},
                                               {2,0,2,0,2},{0,5,2,2,2,1}};
    public static final int[] hard_o_threes_descriptor={2,2,2,2,2};
    public static final int[][] hard_o_twos={{5,2,2,0,0},{5,2,0,2,5}};
    public static final int[] hard_o_twos_descriptor={2,2};
    /////////////////////////////////FIVES/////////////////////////////////////
    public static final int[][] fives={{1,1,1,1,1},{2,2,2,2,2}};
}
