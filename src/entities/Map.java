package entities;

import enums.ArenaDetails;
import enums.Direction;
import enums.Constant;

import java.util.ArrayList;

public class Map {
    private Cell[][] map;
    public int robotXval;
    public int robotYval;
    private ArrayList<Cell> obstacles;

    /**
     * Initialise a new map entity
     */
    public Map() {
        this.map = new Cell[Constant.MAX_Y_VAL][Constant.MAX_X_VAL];

        for (int i = 0; i < Constant.MAX_Y_VAL; i++) {
            for (int j = 0; j < Constant.MAX_X_VAL; j++) {
                this.map[i][j] = new Cell(i, j);
            }
        }
    }

    public Cell[][] getMap() {
        return map;
    }

    public void setMap(Cell[][] map) {
        this.map = map;
    }

    public Cell setTargetCell(Cell obstacleCell){
        Cell[][] cellArray = getMap();
        Cell cell;
        int yPos = obstacleCell.getRow();
        int xPos = obstacleCell.getCol();

        Direction obstacleDirection = obstacleCell.getObstacleDirection();
        switch (obstacleDirection){
            case NORTH -> {
                cell = cellArray[yPos+3][xPos];
                cell.setHeadDirection(Direction.SOUTH);
            }
            case EAST -> {
                cell = cellArray[yPos][xPos+3];
                cell.setHeadDirection(Direction.WEST);
            }
            case SOUTH -> {
                cell = cellArray[yPos-3][xPos];
                cell.setHeadDirection(Direction.NORTH);
            }
            default -> {
                cell = cellArray[yPos][xPos-3];
                cell.setHeadDirection(Direction.EAST);
            }
        }
        cell.setTarget(true);
        cell.setObst(obstacleCell);
        return cell;
    }

    public ArrayList<Cell> getObstacles() {
        return obstacles;
    }

    public void setObstacles(ArrayList<Cell> obstacles) {
        this.obstacles = obstacles;
    }

    public void setVisited(int i, int j, Direction direction) {
        //for(int z=0;z<3;z++){
        //switch (direction){
        //case UP -> map[i][j+z].setVisited(true);
        //case DOWN -> map[i][j-z].setVisited(true);
        //case RIGHT -> map[i+z][j].setVisited(true);
        //case LEFT -> map[i-z][j].setVisited(true);
        map[i][j].setVisited(true);
        //   }
        //   }

    }

    public void setBoundary(int yVal, int xVal, Direction direction) {

        if((yVal+1<20) && (xVal+1<20)){
            //Top
            map[yVal + 1][xVal].setObstacle(true);
            map[yVal + 1][xVal].setObstacleDirection(direction);

            //Top right
            map[yVal + 1][xVal + 1].setObstacle(true);
            map[yVal + 1][xVal + 1].setObstacleDirection(direction);

            if(xVal-1>=0){
                //Top left
                map[yVal + 1][xVal - 1].setObstacle(true);
                map[yVal + 1][xVal - 1].setObstacleDirection(direction);
            }
        }

        if(xVal+1<20){
            //Right
            map[yVal][xVal + 1].setObstacle(true);
            map[yVal][xVal + 1].setObstacleDirection(direction);
            if(yVal-1>=0){
                //Bottom right
                map[yVal - 1][xVal + 1].setObstacle(true);
                map[yVal - 1][xVal + 1].setObstacleDirection(direction);
            }
        }

        if(yVal-1>=0){
            //Bottom
            map[yVal - 1][xVal].setObstacle(true);
            map[yVal - 1][xVal].setObstacleDirection(direction);
        }


        if(yVal-1>=0 && xVal-1>=0){ //Bottom left
            map[yVal - 1][xVal - 1].setObstacle(true);
            map[yVal - 1][xVal - 1].setObstacleDirection(direction);
        }

        if(xVal-1>=0){ //Left
            map[yVal][xVal - 1].setObstacle(true);
            map[yVal][xVal - 1].setObstacleDirection(direction);
        }

        //--Setting second layer of boundary-----------------------------------------------------------------
        /*
        //For 5 and 1
        if((yVal+2<20) && (xVal+2<20)){
            //For 5
            map[yVal + 2][xVal + 2].setObstacle(true);
            map[yVal + 2][xVal + 2].setObstacleDirection(direction);

            if(xVal-2>=0){
                //For 1
                map[yVal + 2][xVal - 2].setObstacle(true);
                map[yVal + 2][xVal - 2].setObstacleDirection(direction);
            }
        }
        //For 13 and 9
        if((yVal-2>=0) && (xVal+2<20)){
            //For 9
            map[yVal - 2][xVal + 2].setObstacle(true);
            map[yVal - 2][xVal + 2].setObstacleDirection(direction);

            if(xVal-2>=0){
                //For 13
                map[yVal - 2][xVal - 2].setObstacle(true);
                map[yVal - 2][xVal - 2].setObstacleDirection(direction);
            }
        }
        //For rights
        if(xVal+2<20){
            //For 7
            map[yVal][xVal + 2].setObstacle(true);
            map[yVal][xVal + 2].setObstacleDirection(direction);
            //For 6
            if(yVal+1<20){
                map[yVal + 1][xVal + 2].setObstacle(true);
                map[yVal + 1][xVal + 2].setObstacleDirection(direction);
            }
            //For 8
            if(yVal-1>=0){
                map[yVal - 1][xVal + 2].setObstacle(true);
                map[yVal - 1][xVal + 2].setObstacleDirection(direction);
            }
        }
        //For lefts
        if(xVal-2>=0){
            //For 15
            map[yVal][xVal - 2].setObstacle(true);
            map[yVal][xVal - 2].setObstacleDirection(direction);
            //For 16
            if(yVal+1<20){
                map[yVal + 1][xVal - 2].setObstacle(true);
                map[yVal + 1][xVal - 2].setObstacleDirection(direction);
            }
            //For 14
            if(yVal-1>=0){
                map[yVal - 1][xVal - 2].setObstacle(true);
                map[yVal - 1][xVal - 2].setObstacleDirection(direction);
            }
        }
        //For tops
        if(yVal+2<20){
            //For 3
            map[yVal + 2][xVal].setObstacle(true);
            map[yVal + 2][xVal].setObstacleDirection(direction);
            //For 4
            if(xVal+1<20){
                map[yVal + 2][xVal  + 1].setObstacle(true);
                map[yVal + 2][xVal  + 1].setObstacleDirection(direction);
            }
            //For 2
            if(xVal-1>=0){
                map[yVal  + 2][xVal - 1].setObstacle(true);
                map[yVal  + 2][xVal - 1].setObstacleDirection(direction);
            }
        }
        //For bottoms
        if(yVal-2>=0){
            //For 11
            map[yVal - 2][xVal].setObstacle(true);
            map[yVal - 2][xVal].setObstacleDirection(direction);
            //For 10
            if(xVal+1<20){
                map[yVal - 2][xVal  + 1].setObstacle(true);
                map[yVal - 2][xVal  + 1].setObstacleDirection(direction);
            }
            //For 12
            if(xVal-1>=0){
                map[yVal  - 2][xVal - 1].setObstacle(true);
                map[yVal  - 2][xVal - 1].setObstacleDirection(direction);
            }
        }
        */

    }

    public void setStartPoint(int startXval, int startYval) {
        this.robotXval = startXval;
        this.robotYval = startYval;
    }

}
