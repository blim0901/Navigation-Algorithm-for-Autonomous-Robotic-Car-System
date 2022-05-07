package algo;

import entities.Cell;
import entities.Map;
import entities.Robot;
import enums.Direction;
import ui.MapLayout;

import java.util.ArrayList;

import static enums.Constant.exhaustiveSearch;
import static enums.Constant.sleepTimer;

public class HamPath {
    private Map map;
    private Robot robot;
    private MapLayout mapLayout;
    private ArrayList<Direction> directionArray = null;
    private ArrayList<Integer> xValArray = null;
    private ArrayList<Integer> yValArray = null;


    public HamPath(Map m, Robot r, MapLayout mapLayout) {
        this.map = m;
        this.robot = r;
        this.mapLayout = mapLayout;


    }

    public ArrayList<Direction> getDirectionArray() {
        return directionArray;
    }

    public ArrayList<Integer> getxValArray() {
        return xValArray;
    }

    public ArrayList<Integer> getyValArray() {
        return yValArray;
    }

    public void setDirectionArray(ArrayList<Direction> directionArray) {
        this.directionArray = directionArray;
    }

    public void setxValArray(ArrayList<Integer> xValArray) {
        this.xValArray = xValArray;
    }

    public void setyValArray(ArrayList<Integer> yValArray) {
        this.yValArray = yValArray;
    }

    //Preserve original
    public ArrayList<String> run(ArrayList<Cell> obstacles, boolean simulation) {
        AStarTestRun testRun = new AStarTestRun();
        xValArray = new ArrayList<>();
        yValArray = new ArrayList<>();
        directionArray = new ArrayList<>();
        ArrayList<ArrayList<String>> moveCommands = testRun.runASearch(exhaustiveSearch,obstacles);
        //Generate xValArray and yVal Array
        ArrayList<ArrayList<String>> coordinatesDirectionArray = testRun.generateCoordinates(moveCommands.get(0), moveCommands.get(1));
        //Generate Directions Array
        for (int i = 0; i < coordinatesDirectionArray.get(0).size(); i++) {
            xValArray.add(Integer.parseInt(coordinatesDirectionArray.get(0).get(i)));
        }

        for (int i = 0; i < coordinatesDirectionArray.get(1).size(); i++) {
            yValArray.add(Integer.parseInt(coordinatesDirectionArray.get(1).get(i)));
        }

        directionArray = testRun.generateDirections(coordinatesDirectionArray.get(2));

        if (simulation) simulateMovement(xValArray, yValArray, directionArray);
        System.out.println("Send to Android -");
        System.out.println("Col Val: " + getxValArray());
        System.out.println("Row val: " + getyValArray());
        System.out.println("Direction Array: " + getDirectionArray());
        System.out.println("Obstacle list: "+ NearestNeighbour.getObstArr());

        return moveCommands.get(0);
    }

    public void simulateMovement(ArrayList<Integer> xValArray, ArrayList<Integer> yValArray, ArrayList<Direction> directionArray) {
        for (int i = 0; i < xValArray.size(); i++) {
            //map.setVisited(robot.getyPos(),robot.getxPos(),robot.getDirection());
            robot.setyPos(yValArray.get(i));
            robot.setxPos(xValArray.get(i));
            robot.setDirection(directionArray.get(i));
            mapLayout.updateCellColors();
            try {
                Thread.sleep(sleepTimer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void animate() {
        simulateMovement(getxValArray(), getyValArray(), getDirectionArray());
        try {
            Thread.sleep(sleepTimer * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}