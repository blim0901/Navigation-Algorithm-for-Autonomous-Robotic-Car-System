package moves;

import entities.Cell;
import entities.Map;
import enums.CellType;
import enums.Direction;
import enums.Constant;
import enums.MovementType;

public class ReverseTurningMove {
    private int row;
    private int col;
    private boolean obstacle;
    private Direction obstacleDirection;
    private Direction headDirection;
    private Boolean target;
    private boolean visited;
    private int cost;

    public ReverseTurningMove(){
        this.cost = 7;
    }

    public int[] makeMove(Cell currentCell, MovementType movementType){
        int yPos = currentCell.getRow();
        int xPos = currentCell.getCol();
        int newYPos = yPos;
        int newXPos = xPos;
        if (movementType == MovementType.REVERSERIGHT) {
            if (currentCell.getHeadDirection() == Direction.NORTH) {
                newXPos = xPos + 2;
                newYPos = yPos - 3;
            } else if (currentCell.getHeadDirection() == Direction.SOUTH) {
                newXPos = xPos - 2;
                newYPos = yPos + 3;
            } else if (currentCell.getHeadDirection() == Direction.EAST) {
                newXPos = xPos - 3;
                newYPos = yPos - 2;
            } else if (currentCell.getHeadDirection() == Direction.WEST) {
                newXPos = xPos + 3;
                newYPos = yPos + 2;
            }
        }
        else {
            if (currentCell.getHeadDirection() == Direction.NORTH) {
                newXPos = xPos - 2;
                newYPos = yPos - 3;
            } else if (currentCell.getHeadDirection() == Direction.SOUTH) {
                newXPos = xPos + 2;
                newYPos = yPos + 3;
            } else if (currentCell.getHeadDirection() == Direction.EAST) {
                newXPos = xPos - 3;
                newYPos = yPos + 2;
            } else if (currentCell.getHeadDirection() == Direction.WEST) {
                newXPos = xPos + 3;
                newYPos = yPos - 2;
            }
        }

        int[] coordinatesArray = {newYPos, newXPos};
        return coordinatesArray;
    }

    public Direction getDirection(Cell currentCell, MovementType movementType){
        Direction direction = currentCell.getHeadDirection();
        Direction newDirection = Direction.NULL;
        if (movementType == MovementType.REVERSERIGHT) {
            if (direction == Direction.NORTH) {
                newDirection = Direction.WEST;
            } else if (direction == Direction.SOUTH) {
                newDirection = Direction.EAST;
            } else if (direction == Direction.EAST) {
                newDirection = Direction.NORTH;
            } else if (direction == Direction.WEST) {
                newDirection = Direction.SOUTH;
            }
        }
        else {
            if (direction == Direction.NORTH) {
                newDirection = Direction.EAST;
            } else if (direction == Direction.SOUTH) {
                newDirection = Direction.WEST;
            } else if (direction == Direction.EAST) {
                newDirection = Direction.SOUTH;
            } else if (direction == Direction.WEST) {
                newDirection = Direction.NORTH;
            }
        }
        return newDirection;
    }

    public int getCost() {
        return cost;
    }
}
