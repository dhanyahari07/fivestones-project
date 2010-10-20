package org.me.five_stones_project;

/**
 *
 * @author Tangl
 */

public class Coordinate {
    private int x=0;
    private int y=0;

    public Coordinate(){

    }

    public Coordinate(int _x, int _y){
        x=_x;
        y=_y;
    }

    public Coordinate(Coordinate coord){
        x=coord.getX();
        y=coord.getY();
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setXY(int _x, int _y){
        x=_x;
        y=_y;
    }

    public void setX(int _x){
        x=_x;
    }

    public void setY(int _y){
        y=_y;
    }
}
