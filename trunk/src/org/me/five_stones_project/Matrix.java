package org.me.five_stones_project;

import java.util.ArrayList;

/**
 *
 * @author Tangl
 */

class Matrix{
    private Coordinate first,last;
    private int angle=-1,type;
    private boolean five=false;

    public Matrix(){
        first=new Coordinate();
        last=new Coordinate();
    }

    public boolean searchFives(int[][] board,Coordinate last_step,int type){
        int[][] board_part;
        int width_before,width_after,height_before,height_after;
        if(last_step.getX()<5)
            width_before=last_step.getX();
        else
            width_before=4;
        if((board.length-last_step.getX())<5)
            width_after=board.length-last_step.getX()-1;
        else
            width_after=4;
        if(last_step.getY()<5)
            height_before=last_step.getY();
        else
            height_before=4;
        if((board[0].length-last_step.getY())<5)
            height_after=board[0].length-last_step.getY()-1;
        else
            height_after=4;
        board_part=new int[width_before+width_after+1][height_before+height_after+1];
        for(int i=0;i<board_part.length;++i)
            for(int j=0;j<board_part[0].length;++j){
                board_part[i][j]=board[last_step.getX()-width_before+i]
                                      [last_step.getY()-height_before+j];
            }
        five=true;
        if(searchMatch(board_part,Mints.fives[type-1],null)!=null){
            this.type=type;
            first.setXY(first.getX()+last_step.getX()-width_before,
                    first.getY()+last_step.getY()-height_before);
            switch(angle){
                case 0: last.setXY(first.getX()+4,first.getY());break;
                case 1: last.setXY(first.getX()+4,first.getY()+4);break;
                case 2: last.setXY(first.getX(),first.getY()+4);break;
                case 3: last.setXY(first.getX()+4,first.getY()-4);break;
                default: break;
            }
            five=false;
            return true;
        }
        five=false;
        return false;
    }

    public ArrayList<Coordinate> searchMatch(int[][] board_part,int[] mint1,int[][] mint2){
        ArrayList<Coordinate> ret=new ArrayList<Coordinate>();
        outer: for(int _angle=0;_angle<8;++_angle){
            int[][] rotated=rotateVector(mint1,_angle);
            for(int i=0;i<board_part.length-rotated.length+1;++i)
                for(int j=0;j<board_part[0].length-rotated[0].length+1;++j){
                    ret.clear();
                    inner: for(int w=0;w<rotated.length;++w){
                        for(int h=0;h<rotated[0].length;++h){
                            if(board_part[i+w][j+h]!=rotated[w][h]
                                && rotated[w][h]!=-1 && board_part[i+w][j+h]+5!=rotated[w][h])
                                break inner;//not match
                            if(rotated[w][h]==0)//if match
                                ret.add(new Coordinate(i+w,j+h));
                        }
                        if(w<rotated.length-1)
                            continue inner;
                        if(five){
                            if(ret.size()==0)
                                ret.add(new Coordinate());
                            angle=_angle;
                            if(angle==3)
                                first.setXY(i,j+4);
                            else
                                first.setXY(i,j);
                            //visszatérés
                            return ret;
                        }
                        else{
                            angle=_angle;
                            return ret;
                        }
                    }
                }
        }
        angle=-1;
        return null;
    }

    public boolean searchSecondMatch(int[][] board,int[][] mint,Coordinate coord){
        int _angle=angle+1;
        int[][] rotated=null;
        int x_before,x_after,y_before,y_after;

        for(int l=0;l<mint.length;++l){
            if((coord.getX()-mint[l].length+1)<0)
                x_before=coord.getX();
            else
                x_before=mint[l].length-1;
            if((coord.getX()+mint[l].length-1)>=board.length)
                x_after=board.length-coord.getX()-1;
            else
                x_after=mint[l].length-1;
            if((coord.getY()-mint[l].length+1)<0)
                y_before=coord.getY();
            else
                y_before=mint[l].length-1;
            if((coord.getY()+mint[l].length-1)>=board[0].length)
                y_after=board[0].length-coord.getY()-1;
            else
                y_after=mint[l].length-1;
            while(true){
                if(_angle>=8)
                    _angle=0;
                if(_angle==angle)
                    break;
                if(_angle==angle+4 || _angle==angle-4)
                    ++_angle;
                rotated=rotateVector(mint[l],_angle);
                switch(_angle){
                    case 0 | 4:{//OK
                        for(int i=0;i<=x_before+x_after+1-rotated.length;++i){
                            for(int j=0;j<rotated.length;++j){
                                if(rotated[j][0]!=board[coord.getX()-x_before+i+j]
                                                    [coord.getY()]
                                        && rotated[j][0]!=board[coord.getX()-x_before+i+j]
                                                    [coord.getY()]+5)
                                    break;
                                if(j==rotated.length-1)
                                    return true;
                            }
                        }
                    }break;
                    case 1 | 5:{
                        int b,a;
                        if(x_before<y_before) b=x_before;
                        else b=y_before;
                        if(x_after<y_after) a=x_after;
                        else a=y_after;
                        for(int i=0;i<=b+a+1-rotated.length;++i){
                            for(int j=0;j<rotated.length;++j){
                                if(rotated[j][j]!=board[coord.getX()-b+i+j]
                                                    [coord.getY()-b+i+j]
                                        && rotated[j][j]!=board[coord.getX()-b+i+j]
                                                    [coord.getY()-b+i+j]+5)
                                    break;
                                if(j==rotated.length-1)
                                    return true;
                            }
                        }
                    }break;
                    case 2 | 6:{//OK
                        for(int i=0;i<=y_before+y_after+1-rotated.length;++i){
                            for(int j=0;j<rotated.length;++j){
                                if(rotated[0][j]!=board[coord.getX()]
                                                    [coord.getY()-y_before+i+j]
                                        && rotated[0][j]!=board[coord.getX()]
                                                    [coord.getY()-y_before+i+j]+5)
                                    break;
                                if(j==rotated[0].length-1)
                                    return true;
                            }
                        }
                    }break;
                    case 3 | 7:{
                        int b,a;
                        if(x_before<y_after) b=x_before;
                        else b=y_after;
                        if(x_after<y_before) a=x_after;
                        else a=y_before;
                        for(int i=0;i<=b+a+1-rotated.length;++i){
                            for(int j=0;j<rotated.length;++j){
                                if(rotated[j][rotated[0].length-1-j]!=
                                    board[coord.getX()-b+i+j][coord.getY()+b-i-j]
                                    && rotated[j][rotated[0].length-1-j]!=
                                    board[coord.getX()-b+i+j][coord.getY()+b-i-j]+5)
                                    break;
                                if(j==rotated.length-1)
                                    return true;
                            }
                        }
                    }break;
                    default: break;
                }
                ++_angle;
            }
        }
        return false;
    }

    public int[][] rotateVector(int[] mint,int angle){
        int[][] return_mx=null;
        switch(angle){
            case 0:{//return the original matrix
                return_mx=new int[mint.length][1];
                for(int i=0;i<mint.length;++i)
                    return_mx[i][0]=mint[i];
            }break;
            case 1:{//rotate the matrix with 45°, etc
                return_mx=new int[mint.length][mint.length];
                for(int i=0;i<mint.length;++i)
                    for(int j=0;j<mint.length;++j)
                        return_mx[i][j]=-1;
                for(int i=0;i<mint.length;++i){
                    return_mx[i][i]=mint[i];
                }
            }break;
            case 2:{
                return_mx=new int[1][mint.length];
                for(int i=0;i<mint.length;++i)
                    return_mx[0][i]=mint[i];
            }break;
            case 3:{
                return_mx=new int[mint.length][mint.length];
                for(int i=0;i<mint.length;++i)
                    for(int j=0;j<mint.length;++j)
                        return_mx[i][j]=-1;
                for(int i=0;i<mint.length;++i){
                    return_mx[i][mint.length-1-i]=mint[mint.length-1-i];
                }
            }break;
            case 4:{
                return_mx=new int[mint.length][1];
                for(int i=0;i<mint.length;++i)
                    return_mx[i][0]=mint[mint.length-1-i];
            }break;
            case 5:{
                return_mx=new int[mint.length][mint.length];
                for(int i=0;i<mint.length;++i)
                    for(int j=0;j<mint.length;++j)
                        return_mx[i][j]=-1;
                for(int i=0;i<mint.length;++i){
                    return_mx[i][i]=mint[mint.length-1-i];
                }
            }break;
            case 6:{
                return_mx=new int[1][mint.length];
                for(int i=0;i<mint.length;++i)
                    return_mx[0][i]=mint[mint.length-1-i];
            }break;
            case 7:{
                return_mx=new int[mint.length][mint.length];
                for(int i=0;i<mint.length;++i)
                    for(int j=0;j<mint.length;++j)
                        return_mx[i][j]=-1;
                for(int i=0;i<mint.length;++i){
                    return_mx[i][mint.length-1-i]=mint[i];
                }
            }break;
            default: break;
        }
        return return_mx;
    }

    public Coordinate getFirst(){
        return first;
    }

    public Coordinate getLast(){
        return last;
    }

    public int getType(){
        return type;
    }

    public void update(int x,int y){
        first.setXY(first.getX()+x,first.getY()+y);
        last.setXY(last.getX()+x,last.getY()+y);
    }
}
