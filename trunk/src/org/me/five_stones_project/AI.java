package org.me.five_stones_project;

import java.util.ArrayList;

/**
 *
 * @author Tangl
 */

class AI{
    private Matrix matrix;
    private MainActivity main;
    private Coordinate first,last;

    public AI(Matrix matrix,MainActivity main){
        this.matrix=matrix;
        this.main=main;
        first=new Coordinate(-1,-1);
        last=new Coordinate(-1,-1);
    }

    public Coordinate nextStep(int[][] board,Coordinate last_step){
        ArrayList<Coordinate> coords;
        int[][] board_part;
        int[][] mint1=null,mint2=null;
        int[] desc=null;
        int x=0,y=0;
        int start=0,end=0;
        int width_before,width_after,height_before,height_after;

        if(main.getLevel()==0){//easy level
            start=0;
            end=2;
        }
        else if(main.getLevel()==1){//medium level
            start=2;
            end=4;
        }
        else if(main.getLevel()==2){//hard level
            start=4;
            end=13;
        }
        for(int m=start;m<end;++m){
            switch(m){//set the mint from the Mints class hang on the level
                case 0:{
                    mint1=Mints.fours;
                    desc=Mints.fours_descriptor;
                    mint2=null;
                }break;
                case 1:{
                    mint1=Mints.threes_twos;
                    desc=Mints.threes_twos_descriptor;
                    mint2=null;
                }break;
                case 2:{
                    mint1=Mints._fours;
                    desc=Mints._fours_descriptor;
                    mint2=null;
                }break;
                case 3:{
                    mint1=Mints._threes_twos;
                    desc=Mints._threes_twos_descriptor;
                    mint2=null;
                }break;
                case 4:{
                    mint1=Mints._fours;
                    desc=Mints._fours_descriptor;
                    mint2=null;
                }break;
                case 5:{
                    mint1=Mints.hard_o_threes;
                    mint2=Mints.hard_o_threes;
                    desc=Mints.hard_o_threes_descriptor;
                }break;
                case 6:{
                    mint1=Mints.hard_o_threes;
                    mint2=Mints.hard_o_twos;
                    desc=Mints.hard_o_threes_descriptor;
                }break;                
                case 7:{
                    mint1=Mints.hard_x_threes;
                    mint2=Mints.hard_x_threes;
                    desc=Mints.hard_x_threes_descriptor;
                }break;
                case 8:{
                    mint1=Mints.hard_x_threes;
                    mint2=Mints.hard_x_twos;
                    desc=Mints.hard_x_threes_descriptor;
                }break;
                case 9:{
                    mint1=Mints.threes;
                    desc=Mints.threes_descriptor;
                    mint2=null;
                }break;
                case 10:{
                    mint1=Mints.hard_o_twos;
                    mint2=Mints.hard_o_twos;
                    desc=Mints.hard_o_twos_descriptor;
                }break;
                case 11:{
                    mint1=Mints.hard_x_twos;
                    mint2=Mints.hard_x_twos;
                    desc=Mints.hard_x_twos_descriptor;
                }break;
                case 12:{
                    mint1=Mints.twos;
                    desc=Mints.twos_descriptor;
                    mint2=null;
                }break;
                default:break;
            }
            for(int l=0;l<mint1.length;++l){
                //first set the part of the board must be check
                //this is different, and hang on the mint
                //after it call the matrix.searchMatch method, whcih return a
                //list of the potentianal coordinates
                if(desc[l]==2){
                    if(first.getX()>1)
                        width_before=2;
                    else width_before=1;
                    if(first.getY()>1)
                        height_before=2;
                    else height_before=1;
                    if(last.getX()>=board.length-2)//---------------
                        width_after=1;
                    else width_after=2;
                    if(last.getY()>=board[0].length-2)//--------------
                        height_after=1;
                    else height_after=2;
                    x=width_before+width_after+last.getX()-first.getX()+1;
                    y=height_before+height_after+last.getY()-first.getY()+1;
                    board_part=new int[x][y];
                    for(int i=0;i<x;++i)
                        for(int j=0;j<y;++j)
                            board_part[i][j]=board[first.getX()-width_before+i]
                                                  [first.getY()-height_before+j];

                    coords=matrix.searchMatch(board_part,mint1[l],mint2);
                    x=first.getX()-width_before;
                    y=first.getY()-height_before;
                }
                else{                    
                    if(last_step.getX()<mint1[l].length){
                        width_before=last_step.getX();x=0;
                    }
                    else{
                        width_before=mint1[l].length-1;
                        x=last_step.getX()-width_before;
                    }
                    if((board.length-last_step.getX())<mint1[l].length)
                        width_after=board.length-last_step.getX()-1;
                    else
                        width_after=mint1[l].length-1;
                    if(last_step.getY()<mint1[l].length){
                        height_before=last_step.getY();y=0;
                    }
                    else{
                        height_before=mint1[l].length-1;
                        y=last_step.getY()-height_before;
                    }
                    if((board[0].length-last_step.getY())<mint1[l].length)
                        height_after=board[0].length-last_step.getY()-1;
                    else
                        height_after=mint1[l].length-1;
                    board_part=new int[width_before+width_after+1][height_before+height_after+1];
                    for(int i=0;i<board_part.length;++i)
                        for(int j=0;j<board_part[0].length;++j){
                            board_part[i][j]=board[last_step.getX()-width_before+i]
                                                  [last_step.getY()-height_before+j];
                        }

                    coords=matrix.searchMatch(board_part,mint1[l],mint2);
                }                
                if(coords!=null){
                    if(mint2!=null)
                        //if there are two mints, it try to search a second match
                        //in the first neighbourhood
                        for(int c=0;c<coords.size();++c){
                            Coordinate coord=new Coordinate(coords.get(c));
                            coord.setXY(coord.getX()+x,coord.getY()+y);
                            if(matrix.searchSecondMatch(board,mint2,coord)){
                                coords.clear();
                                coords.add(coord);
                                return searchWithNeighbours(board,coords,0,0,desc[l]);
                            }
                        }
                    else
                        return searchWithNeighbours(board,coords,x,y,desc[l]);
                }
            }
        }
        return searchRandom(board,last_step);
    }

    public Coordinate searchWithNeighbours(int[][] board,
            ArrayList<Coordinate> coords,int x,int y,int type){
        //it decide, which coordinate is the best. The best is, that has the most
        //"o" neighbours, that can grows the chance to the win
        int new_weight=0,old_weight;
        if(type==1)
            old_weight=10;
        else
            old_weight=0;
        Coordinate ret=null;
        for(int i=0;i<coords.size();++i){
            for(int w=-1;w<2;++w)
                for(int h=-1;h<2;++h){
                    if(board[coords.get(i).getX()+x+w][coords.get(i).getY()+y+h]
                            ==gamertypes.o.ordinal())
                        ++new_weight;
                }
            if(old_weight<=new_weight && type==2){
                ret=new Coordinate(coords.get(i).getX()+x,coords.get(i).getY()+y);
                old_weight=new_weight;
            }
            else if(old_weight>=new_weight && type==1){
                ret=new Coordinate(coords.get(i).getX()+x,coords.get(i).getY()+y);
                old_weight=new_weight;
            }
            new_weight=0;
        }
        return ret;
    }

    public Coordinate searchRandom(int[][] board,Coordinate last_step){
        //random searxh int the neighbour of the last step
        for(int i=-1;i<2;i+=2)
            for(int j=-1;j<2;j+=2)
                if(board[last_step.getX()+i][last_step.getY()+j]==0)
                    return new Coordinate(last_step.getX()+i,last_step.getY()+j);
        for(int i=-1;i<2;i+=2)
            if(board[last_step.getX()+i][last_step.getY()]==0)
                return new Coordinate(last_step.getX()+i,last_step.getY());
         for(int i=-1;i<2;i+=2)
            if(board[last_step.getX()][last_step.getY()+i]==0)
                return new Coordinate(last_step.getX(),last_step.getY()+i);
        //if there are no potential good coordinate, check the entery board for
        //an empty cell
        for(int i=0;i<board.length;++i)
            for(int j=0;j<board[0].length;++j)
                if(board[i][j]==0)
                    return new Coordinate(i,j);
        return null;
    }

    public void update(Coordinate last_step,int inc_x,int inc_y){
        //update the first and last coordinate, this restrict the area, where
        //are previously placed signs
        if(last_step==null){
            first=new Coordinate(-1,-1);
            last=new Coordinate(-1,-1);
        }
        else if(last.getX()==-1){
            first.setXY(last_step.getX(),last_step.getY());
            last.setXY(last_step.getX(),last_step.getY());
        }
        else{
            first.setXY(first.getX()+inc_x,first.getY()+inc_y);
            last.setXY(last.getX()+inc_x,last.getY()+inc_y);
            if(last_step.getX()<first.getX())
                first.setX(last_step.getX());
            if(last_step.getY()<first.getY())
                first.setY(last_step.getY());
            if(last_step.getX()>last.getX())
                last.setX(last_step.getX());
            if(last_step.getY()>last.getY())
                last.setY(last_step.getY());
        }
    }
}
