package algo;

import entities.Cell;
import entities.Map;
import entities.Robot;
import enums.Direction;
import enums.MovementType;
import enums.ArenaDetails;
import moves.*;
import ui.ControlPanel;
import ui.MapLayout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static enums.Constant.MAX_X_VAL;
import static enums.Constant.MAX_Y_VAL;
import static enums.Constant.MIN_XY_VAL;

public class AStarTestRun {

    public AStarTestRun() {

    }

    public ArrayList<ArrayList<String>> runASearch(boolean exhaustive,ArrayList<Cell> obstacleList){
       if(exhaustive) return _runAExSearch(obstacleList);
       else return _runASearch(obstacleList);
    }


    //Exhaustive runASearch
    private ArrayList<ArrayList<String>> _runAExSearch(ArrayList<Cell> obstacleList) {
        Map m = new Map();
        Robot r = new Robot(1, 1, Direction.NORTH);
        ArrayList<ArrayList<String>> combineArrayList;
        ArrayList<ArrayList<String>> finalCombineArrayList = null;


        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                m.getMap()[i][j] = new Cell(i, j);
            }
        }


        for (Cell c : obstacleList) {
            m.getMap()[c.getRow()][c.getCol()].setObstacle(true);
            m.getMap()[c.getRow()][c.getCol()].setObstacleDirection(c.getObstacleDirection());
            m.setBoundary(c.getRow(), c.getCol(), c.getObstacleDirection());
            m.setTargetCell(m.getMap()[c.getRow()][c.getCol()]);
        }


        //Add targets in the map to a target array list
        ArrayList<Cell> t = new ArrayList<Cell>();
        for (int i = 0; i < m.getMap().length; i++) {
            for (int j = 0; j < m.getMap()[i].length; j++) {
                if (m.getMap()[i][j].getTarget()) {
                    t.add(m.getMap()[i][j]);
                }
            }
        }

        //Sort target list such that it is the order of visiting the targets
        //CHECKED CORRECT the target list is generated correctly
        t = NearestNeighbour.calculateDist(t, r);

        //Deep copy target cells so that tarList can reference the directions
        ArrayList<Cell> targetTemp = new ArrayList<>();
        for (Cell c : t) {
            Cell tempCell = new Cell(c.getRow(), c.getCol());
            tempCell.setHeadDirection(c.getHeadDirection());
            targetTemp.add(tempCell);
        }
        //Order of obstacle for shortest path
        ArrayList<Cell> shortestPathObstArr = new ArrayList<>();
        int shortestPathArrSize = 99; //Max path length

        for (ArrayList<Cell> tarList : Permute.permute(t)) {
            boolean exceeded=false;
            r.setxPos(1);
            r.setyPos(1);
            r.setDirection(Direction.NORTH);
            //Create arrays to store the target's row, column, and direction head values.
            //This will be used to determine which targets to visit, starting at the start position
            int[] tarHeadRArr = new int[tarList.size()];
            int[] tarHeadCArr = new int[tarList.size()];
            Direction[] tarHeadDirArr = new Direction[tarList.size()];

            //Adding into the arrays created.
            //CHECKED CORRECT the target row, col and directions are generated correctly
            for (int i = 0; i < tarList.size(); i++) {
                tarHeadRArr[i] = tarList.get(i).getRow();
                tarHeadCArr[i] = tarList.get(i).getCol();
                //tarList cells somehow changes the head direction to NULL after the first iteration
                //now it references from targetTemp to get the head directions
                for (Cell target : targetTemp) {
                    if (target.getRow() == tarHeadRArr[i] && target.getCol() == tarHeadCArr[i]) {
                        tarHeadDirArr[i] = target.getHeadDirection();
                        break;
                    }
                }
            }

            System.out.println("tarHeadDirArr: " + Arrays.toString(tarHeadDirArr));

            //String path = "";
            ArrayList<String> path = new ArrayList<>();
            ArrayList<String> correction;
            int countForDirection = 0;
            Direction startingDirection = Direction.NORTH;
            //Loop through the target head array and for each time, do A star setting new target
            for (int i = 0; i < tarHeadRArr.length; i++) {

                AstarTesting astar = new AstarTesting(m, r, tarHeadRArr[i], tarHeadCArr[i], tarHeadDirArr[i]);
                boolean searchResultBool = astar.aStarSearch();
                ArrayList<String> sol = astar.tracePath(shortestPathArrSize);

                //can merge the two paths here
                path.addAll(sol);
                //add turning on the spot
                //find last position
                if (countForDirection == 0) {
                    startingDirection = Direction.NORTH;
                } else {
                    startingDirection = tarHeadDirArr[i - 1];
                }
                countForDirection++;
                Direction lastDirection = findLastDirection(sol, startingDirection);
                correction = generateCorrection(generatePolarDirection(tarHeadDirArr[i]), lastDirection);
                if (!searchResultBool) {
                    path.addAll(correction);
                }
                path.add("I");
                path.add("V");
                System.out.println(path);

                r.setxPos(tarHeadCArr[i]);
                r.setyPos(tarHeadRArr[i]);
                r.setDirection(tarHeadDirArr[i]);
                for (int o = 0; o < MAX_Y_VAL; o++) {
                    for (int u = 0; u < MAX_X_VAL; u++) {
                        m.getMap()[o][u].resetCell();
                    }
                }

                for (Cell c : obstacleList) {
                    m.getMap()[c.getRow()][c.getCol()].setObstacle(true);
                    m.getMap()[c.getRow()][c.getCol()].setObstacleDirection(c.getObstacleDirection());
                    m.setBoundary(c.getRow(), c.getCol(), c.getObstacleDirection());
                }
                if(path.size()>=shortestPathArrSize) {exceeded=true; break;}

            }
            if(exceeded) continue;
            ArrayList<String> obsDirectionsList = new ArrayList<String>();
            for (int i = 0; i < tarHeadDirArr.length; i++) {
                if (tarHeadDirArr[i] == Direction.NORTH) {
                    obsDirectionsList.add("north");
                } else if (tarHeadDirArr[i] == Direction.SOUTH) {
                    obsDirectionsList.add("south");
                } else if (tarHeadDirArr[i] == Direction.EAST) {
                    obsDirectionsList.add("east");
                } else if (tarHeadDirArr[i] == Direction.WEST) {
                    obsDirectionsList.add("west");
                }
            }

            combineArrayList = new ArrayList<>();
            combineArrayList.add(path);
            combineArrayList.add(obsDirectionsList);

            //If there is a path with less movement required, compared to the previous "shortest" path
            if (combineArrayList.get(0).size() < shortestPathArrSize) {
                System.out.println("New shortest path found: " + combineArrayList + "\n");
                shortestPathArrSize = combineArrayList.get(0).size();
                shortestPathObstArr = new ArrayList<>();
                for (Cell x : tarList) {
                    shortestPathObstArr.add(x.getObst());
                }
                finalCombineArrayList = new ArrayList<>();
                finalCombineArrayList.add(path);
                finalCombineArrayList.add(obsDirectionsList);
            }
        }
        NearestNeighbour.setObstArr(shortestPathObstArr);
        System.out.println("\n\nFinal Shortest Path: "+finalCombineArrayList.get(0));
        return finalCombineArrayList;
    }

//Original - No exhaustive search
    private ArrayList<ArrayList<String>> _runASearch(ArrayList<Cell> obstacleList) {
        Map m = new Map();
        Robot r = new Robot(1, 1, Direction.NORTH);

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                m.getMap()[i][j] = new Cell(i, j);
            }
        }


        for (Cell c : obstacleList) {
            m.getMap()[c.getRow()][c.getCol()].setObstacle(true);
            m.getMap()[c.getRow()][c.getCol()].setObstacleDirection(c.getObstacleDirection());
            m.setBoundary(c.getRow(), c.getCol(), c.getObstacleDirection());
            m.setTargetCell(m.getMap()[c.getRow()][c.getCol()]);
        }


        //Add targets in the map to a target array list
        ArrayList<Cell> tarList = new ArrayList<Cell>();
        for (int i = 0; i < m.getMap().length; i++) {
            for (int j = 0; j < m.getMap()[i].length; j++) {
                if (m.getMap()[i][j].getTarget()) {
                    tarList.add(m.getMap()[i][j]);
                }
            }
        }

        //Sort target list such that it is the order of visiting the targets
        //CHECKED CORRECT the target list is generated correctly
        tarList = NearestNeighbour.calculateDist(tarList, r);
        //Create arrays to store the target's row, column, and direction head values.
        //This will be used to determine which targets to visit, starting at the start position
        int[] tarHeadRArr = new int[tarList.size()];
        int[] tarHeadCArr = new int[tarList.size()];
        Direction[] tarHeadDirArr = new Direction[tarList.size()];

        //Adding into the arrays created.
        //CHECKED CORRECT the target row, col and directions are generated correctly
        for (int i = 0; i < tarList.size(); i++) {
            tarHeadRArr[i] = tarList.get(i).getRow();
            tarHeadCArr[i] = tarList.get(i).getCol();
            tarHeadDirArr[i] = tarList.get(i).getHeadDirection();
        }


        //String path = "";
        ArrayList<String> path = new ArrayList<String>();
        ArrayList<String> correction;
        int countForDirection = 0;
        Direction startingDirection = Direction.NORTH;
        //Loop through the target head array and for each time, do A star setting new target
        for (int i = 0; i < tarHeadRArr.length; i++) {

            AstarTesting astar = new AstarTesting(m, r, tarHeadRArr[i], tarHeadCArr[i], tarHeadDirArr[i]);
            boolean searchResultBool = astar.aStarSearch();
            //not pathing. supposed to be tracePath
            ArrayList<String> sol = astar.tracePath();
            //System.out.println(sol);

            //path = path + "," + sol;
            //can merge the two paths here
            path.addAll(sol);
            //add turning on the spot
            //find last position
            if (countForDirection == 0) {
                startingDirection = Direction.NORTH;
            } else {
                startingDirection = tarHeadDirArr[i - 1];
            }
            countForDirection++;
            Direction lastDirection = findLastDirection(sol, startingDirection);
            correction = generateCorrection(generatePolarDirection(tarHeadDirArr[i]), lastDirection);
            if (!searchResultBool) {
                path.addAll(correction);
            }
            path.add("I");
            path.add("V");
            System.out.println(path);

            r.setxPos(tarHeadCArr[i]);
            r.setyPos(tarHeadRArr[i]);
            r.setDirection(tarHeadDirArr[i]);
            for (int o = 0; o < MAX_Y_VAL; o++) {
                for (int u = 0; u < MAX_X_VAL; u++) {
                    m.getMap()[o][u].resetCell();
                }
            }

            for (Cell c : obstacleList) {
                m.getMap()[c.getRow()][c.getCol()].setObstacle(true);
                m.getMap()[c.getRow()][c.getCol()].setObstacleDirection(c.getObstacleDirection());
                m.setBoundary(c.getRow(), c.getCol(), c.getObstacleDirection());
                //m.setTargetCell(m.getMap()[c.getRow()][c.getCol()]);
            }

        }

        ArrayList<String> obsDirectionsList = new ArrayList<String>();
        for (int i = 0; i < tarHeadDirArr.length; i++) {
            if (tarHeadDirArr[i] == Direction.NORTH) {
                obsDirectionsList.add("north");
            } else if (tarHeadDirArr[i] == Direction.SOUTH) {
                obsDirectionsList.add("south");
            } else if (tarHeadDirArr[i] == Direction.EAST) {
                obsDirectionsList.add("east");
            } else if (tarHeadDirArr[i] == Direction.WEST) {
                obsDirectionsList.add("west");
            }
        }

        ArrayList<ArrayList<String>> combineArrayList = new ArrayList<ArrayList<String>>();
        combineArrayList.add(path);
        combineArrayList.add(obsDirectionsList);

        return combineArrayList;
    }


    //Original
    public ArrayList<ArrayList<String>> generateCoordinates(ArrayList<String> path, ArrayList<String> obsDirections) {
        ArrayList<ArrayList<String>> finalArrayList = new ArrayList<ArrayList<String>>();
        ArrayList<Integer> xCoorArrayTemp = new ArrayList<Integer>();
        ArrayList<Integer> yCoorArrayTemp = new ArrayList<Integer>();
        ArrayList<String> directionArray = new ArrayList<String>();
        xCoorArrayTemp.add(1);
        yCoorArrayTemp.add(1);
        directionArray.add("north");
        int counter = 0;
        for (int i = 0; i < path.size(); i++) {
            switch (path.get(i)) {
                case "W" -> {
                    if (directionArray.get(i) == "north") {
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 1);
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        directionArray.add("north");
                    } else if (directionArray.get(i) == "south") {
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 1);
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        directionArray.add("south");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("west");
                    }
                }
                case "S" -> {
                    if (directionArray.get(i) == "north") {
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 1);
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        directionArray.add("north");
                    } else if (directionArray.get(i) == "south") {
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 1);
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        directionArray.add("south");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("west");
                    }
                }
                case "E" -> {
                    if (directionArray.get(i) == "north") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 2);
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "south") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 2);
                        directionArray.add("west");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 3);
                        directionArray.add("south");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 3);
                        directionArray.add("north");
                    }
                }
                case "Q" -> {
                    if (directionArray.get(i) == "north") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 2);
                        directionArray.add("west");
                    } else if (directionArray.get(i) == "south") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 2);
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 3);
                        directionArray.add("north");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 3);
                        directionArray.add("south");
                    }
                }
                case "C" -> {
                    if (directionArray.get(i) == "north") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 3);
                        directionArray.add("west");
                    } else if (directionArray.get(i) == "south") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 3);
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 2);
                        directionArray.add("north");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 2);
                        directionArray.add("south");
                    }
                }
                case "Z" -> {
                    if (directionArray.get(i) == "north") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 3);
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "south") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 2);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 3);
                        directionArray.add("west");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 2);
                        directionArray.add("south");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 3);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 2);
                        directionArray.add("north");
                    }
                }
                case "D" -> {
                    if (directionArray.get(i) == "north") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "south") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("west");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 1);
                        directionArray.add("south");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 1);
                        directionArray.add("north");
                    }
                }
                case "A" -> {
                    if (directionArray.get(i) == "north") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) - 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("west");
                    } else if (directionArray.get(i) == "south") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i) + 1);
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                        directionArray.add("east");
                    } else if (directionArray.get(i) == "east") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) + 1);
                        directionArray.add("north");
                    } else if (directionArray.get(i) == "west") {
                        xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                        yCoorArrayTemp.add(yCoorArrayTemp.get(i) - 1);
                        directionArray.add("south");
                    }
                }
                case "V" -> {
                    yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                    xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                    directionArray.add(obsDirections.get(counter));
                    counter++;
                }
                case "P" -> {
                    yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                    xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                    directionArray.add(directionArray.get(i));
                }
                case "O" -> {
                    yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                    xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                    directionArray.add(directionArray.get(i));
                }
                case "N" -> {
                    yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                    xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                    directionArray.add(directionArray.get(i));
                }
                case "I" -> {
                    yCoorArrayTemp.add(yCoorArrayTemp.get(i));
                    xCoorArrayTemp.add(xCoorArrayTemp.get(i));
                    directionArray.add(directionArray.get(i));
                }
            }
        }

        ArrayList<String> xCoorArray = new ArrayList<String>();
        ArrayList<String> yCoorArray = new ArrayList<String>();
        for (Integer n : xCoorArrayTemp) {
            xCoorArray.add(String.valueOf(n));
        }
        for (Integer m : yCoorArrayTemp) {
            yCoorArray.add(String.valueOf(m));
        }

        finalArrayList.add(xCoorArray);
        finalArrayList.add(yCoorArray);
        finalArrayList.add(directionArray);

        return finalArrayList;
    }


    public ArrayList<Direction> generateDirections(ArrayList<String> directionString) {
        ArrayList<Direction> directionArray = new ArrayList<Direction>();
        for (int i = 0; i < directionString.size(); i++) {
            if (directionString.get(i) == "north") {
                directionArray.add(Direction.NORTH);
            } else if (directionString.get(i) == "south") {
                directionArray.add(Direction.SOUTH);
            } else if (directionString.get(i) == "east") {
                directionArray.add(Direction.EAST);
            } else if (directionString.get(i) == "west") {
                directionArray.add(Direction.WEST);
            }
        }
        return directionArray;
    }

    public ArrayList<String> generateCorrection(Direction obstacleDirection, Direction currentDirection) {
        ArrayList<String> returnArray = new ArrayList<String>();
        /*
        if (obstacleDirection == Direction.NORTH && currentDirection == Direction.SOUTH) {
            returnArray.add("none");
            return returnArray;
        }
        else if (obstacleDirection == Direction.SOUTH && currentDirection == Direction.NORTH) {
            returnArray.add("none");
            return returnArray;
        }
        else if (obstacleDirection == Direction.EAST && currentDirection == Direction.WEST) {
            returnArray.add("none");
            return returnArray;
        }
        else if (obstacleDirection == Direction.WEST && currentDirection == Direction.EAST) {
            returnArray.add("none");
            return returnArray;
        }

         */
        //--------------------------------------------------------------------------------
        if (obstacleDirection == Direction.NORTH && currentDirection == Direction.NORTH) {
            returnArray.add("P");
            returnArray.add("P");
            return returnArray;
        } else if (obstacleDirection == Direction.NORTH && currentDirection == Direction.EAST) {
            returnArray.add("O");
            return returnArray;
        } else if (obstacleDirection == Direction.NORTH && currentDirection == Direction.WEST) {
            returnArray.add("P");
            return returnArray;
        } else if (obstacleDirection == Direction.SOUTH && currentDirection == Direction.SOUTH) {
            returnArray.add("P");
            returnArray.add("P");
            return returnArray;
        } else if (obstacleDirection == Direction.SOUTH && currentDirection == Direction.EAST) {
            returnArray.add("P");
            return returnArray;
        } else if (obstacleDirection == Direction.SOUTH && currentDirection == Direction.WEST) {
            returnArray.add("O");
            return returnArray;
        } else if (obstacleDirection == Direction.EAST && currentDirection == Direction.NORTH) {
            returnArray.add("P");
            return returnArray;
        } else if (obstacleDirection == Direction.EAST && currentDirection == Direction.SOUTH) {
            returnArray.add("O");
            return returnArray;
        } else if (obstacleDirection == Direction.EAST && currentDirection == Direction.EAST) {
            returnArray.add("P");
            returnArray.add("P");
            return returnArray;
        } else if (obstacleDirection == Direction.WEST && currentDirection == Direction.NORTH) {
            returnArray.add("O");
            return returnArray;
        } else if (obstacleDirection == Direction.WEST && currentDirection == Direction.SOUTH) {
            returnArray.add("P");
            return returnArray;
        } else if (obstacleDirection == Direction.WEST && currentDirection == Direction.WEST) {
            returnArray.add("P");
            returnArray.add("P");
            return returnArray;
        }
        returnArray.add("N");
        return returnArray;
    }

    public Direction findLastDirection(ArrayList<String> directionsArray, Direction startDirection) {
        Direction returnDirection = startDirection;
        for (int i = 0; i < directionsArray.size(); i++) {
            if (returnDirection == Direction.NORTH) {
                if (directionsArray.get(i) == "W") {
                    returnDirection = Direction.NORTH;
                } else if (directionsArray.get(i) == "S") {
                    returnDirection = Direction.NORTH;
                } else if (directionsArray.get(i) == "E") {
                    returnDirection = Direction.EAST;
                } else if (directionsArray.get(i) == "Q") {
                    returnDirection = Direction.WEST;
                } else if (directionsArray.get(i) == "C") {
                    returnDirection = Direction.WEST;
                } else if (directionsArray.get(i) == "Z") {
                    returnDirection = Direction.EAST;
                } else if (directionsArray.get(i) == "D") {
                    returnDirection = Direction.EAST;
                } else if (directionsArray.get(i) == "A") {
                    returnDirection = Direction.WEST;
                }
            } else if (returnDirection == Direction.SOUTH) {
                if (directionsArray.get(i) == "W") {
                    returnDirection = Direction.SOUTH;
                } else if (directionsArray.get(i) == "S") {
                    returnDirection = Direction.SOUTH;
                } else if (directionsArray.get(i) == "E") {
                    returnDirection = Direction.WEST;
                } else if (directionsArray.get(i) == "Q") {
                    returnDirection = Direction.EAST;
                } else if (directionsArray.get(i) == "C") {
                    returnDirection = Direction.EAST;
                } else if (directionsArray.get(i) == "Z") {
                    returnDirection = Direction.WEST;
                } else if (directionsArray.get(i) == "D") {
                    returnDirection = Direction.WEST;
                } else if (directionsArray.get(i) == "A") {
                    returnDirection = Direction.EAST;
                }
            } else if (returnDirection == Direction.EAST) {
                if (directionsArray.get(i) == "W") {
                    returnDirection = Direction.EAST;
                } else if (directionsArray.get(i) == "S") {
                    returnDirection = Direction.EAST;
                } else if (directionsArray.get(i) == "E") {
                    returnDirection = Direction.SOUTH;
                } else if (directionsArray.get(i) == "Q") {
                    returnDirection = Direction.NORTH;
                } else if (directionsArray.get(i) == "C") {
                    returnDirection = Direction.NORTH;
                } else if (directionsArray.get(i) == "Z") {
                    returnDirection = Direction.SOUTH;
                } else if (directionsArray.get(i) == "D") {
                    returnDirection = Direction.SOUTH;
                } else if (directionsArray.get(i) == "A") {
                    returnDirection = Direction.NORTH;
                }
            } else if (returnDirection == Direction.WEST) {
                if (directionsArray.get(i) == "W") {
                    returnDirection = Direction.WEST;
                } else if (directionsArray.get(i) == "S") {
                    returnDirection = Direction.WEST;
                } else if (directionsArray.get(i) == "E") {
                    returnDirection = Direction.NORTH;
                } else if (directionsArray.get(i) == "Q") {
                    returnDirection = Direction.SOUTH;
                } else if (directionsArray.get(i) == "C") {
                    returnDirection = Direction.SOUTH;
                } else if (directionsArray.get(i) == "Z") {
                    returnDirection = Direction.NORTH;
                } else if (directionsArray.get(i) == "D") {
                    returnDirection = Direction.NORTH;
                } else if (directionsArray.get(i) == "A") {
                    returnDirection = Direction.SOUTH;
                }
            }
        }
        return returnDirection;
    }


    public Direction generatePolarDirection(Direction inputDirection) {
        if (inputDirection == Direction.NORTH) {
            return Direction.SOUTH;
        }
        if (inputDirection == Direction.SOUTH) {
            return Direction.NORTH;
        }
        if (inputDirection == Direction.EAST) {
            return Direction.WEST;
        }
        if (inputDirection == Direction.WEST) {
            return Direction.EAST;
        }
        return Direction.NULL;
    }


}
