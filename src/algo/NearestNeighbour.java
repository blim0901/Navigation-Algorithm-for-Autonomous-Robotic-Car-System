package algo;

import entities.Cell;
import entities.Robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NearestNeighbour {
    private static ArrayList<Cell> obstArr;

    public static ArrayList<Cell> calculateDist(ArrayList<Cell> t, Robot robot) {
        obstArr = new ArrayList<>();
        int xRobotPos = 1;
        int yRobotPos = 1;
        while(t.size()>0){
            for (Cell c : t) {
                int xTargetPos = c.getRow();
                int yTargetPos = c.getCol();
                c.setCost(Math.abs(xRobotPos - xTargetPos) + Math.abs(yRobotPos - yTargetPos));
            }
            Collections.sort(t);
            Cell temp = t.get(0);
            obstArr.add(temp);
            t.remove(0);

            xRobotPos=temp.getRow();
            yRobotPos=temp.getCol();
        }
        return obstArr;
    }
//Original
//    public static ArrayList<Cell> calculateDist(ArrayList<Cell> t, Robot robot) {
//        obstArr = new ArrayList<>();
//        int xRobotPos = robot.getxPos();
//        int yRobotPos = robot.getyPos();
//        for (Cell c : t) {
//            int xTargetPos = c.getRow();
//            int yTargetPos = c.getCol();
//            c.setCost(Math.abs(xRobotPos - xTargetPos) + Math.abs(yRobotPos - yTargetPos));
//        }
//        Collections.sort(t);
//        for(Cell c:t) obstArr.add(c.getObst());
//        return t;
//    }


    public static void setObstArr(ArrayList<Cell> obstArr) {
        NearestNeighbour.obstArr = obstArr;
    }

    public static ArrayList<Cell> getObstArr() {
        return obstArr;
    }
}
