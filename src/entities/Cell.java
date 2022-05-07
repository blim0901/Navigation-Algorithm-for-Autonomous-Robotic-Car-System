package entities;

import enums.CellType;
import enums.Constant;
import enums.Direction;

import java.awt.*;

import static enums.Constant.MAX_X_VAL;
import static enums.Constant.MAX_Y_VAL;

public class Cell implements Comparable<Cell>{
    private int row;
    private int col;
    private boolean obstacle;
    private Direction obstacleDirection;
    private Direction headDirection;
    private Boolean target=false;
    private boolean visited;
    private double cost;
    private double totalCost;  //fCost
    private double heuristicCost; //hCost
    private Cell parent;
    private Boolean haveParent;
    private Cell obst;


    public Cell(int row,int col){
        this.row=row;
        this.col=col;
        this.obstacle=false;
        this.obstacleDirection=Direction.NULL;
        this.headDirection = Direction.NULL;
        this.heuristicCost = Constant.FLT_MAX;
        this.visited=false;
        this.haveParent=false;
        this.cost = Constant.FLT_MAX;
        this.totalCost = Constant.FLT_MAX;
        this.parent=null;
    }

    public Cell getObst() {
        return obst;
    }

    public void setObst(Cell obst) {
        this.obst = obst;
    }

    public Direction getHeadDirection() {
        return headDirection;
    }

    public void setHeadDirection(Direction headDirection) {
        this.headDirection = headDirection;
    }


    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


    public Boolean getTarget() {
        return target;
    }

    public void setTarget(Boolean target) {
        this.target = target;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public Direction getObstacleDirection() {
        return obstacleDirection;
    }

    public void setObstacleDirection(Direction obstacleDirection) {
        this.obstacleDirection = obstacleDirection;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getHeuristicCost() {
        return heuristicCost;
    }

    public void setHeuristicCost(double heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    public static Color cellColor(CellType type) {
        switch (type) {
            case NORMAL:
                return Color.white;
            case ROBOT:
                return Color.blue;
            case START:
                return Color.cyan;
            case END:
                return Color.yellow;
            case ROBOTHEAD:
                return Color.red;
            case VISITED:
                return Color.green;
            case OBSTACLE:
                return Color.ORANGE;
            default:
                return Color.black;
        }
    }

    public Cell getParent() {
        return parent;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public Boolean haveParent() {
        return haveParent;
    }

    public void setHaveParent(Boolean parent) {
        this.haveParent = parent;
    }

    public void resetCell() {
        this.obstacle = false;
        this.obstacleDirection=Direction.NULL;
        this.headDirection = Direction.NULL;
        this.heuristicCost = 0;
        this.cost = 0;
        this.totalCost = 999;
        this.visited=false;
        this.haveParent=false;
        this.parent=null;
    }

    @Override
    public String toString(){
        int temp = getCol();
        String col = temp<10 ? "0"+(temp) : String.valueOf(temp);
        temp = getRow();
        String row = temp<10 ? "0"+(temp) : String.valueOf(temp);
        String dir="";
        switch (getObstacleDirection()){
            case NORTH -> dir = "N";
            case SOUTH -> dir = "S";
            case EAST ->  dir = "E";
            case WEST ->  dir = "W";
        }
        return col + row + dir;
    }

    @Override
    public int compareTo(Cell o){
        if(this.cost>o.getCost()) return 1;
        else if (this.cost < o.getCost()) return -1;
        return 0;
    }
}
