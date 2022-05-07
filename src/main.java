import entities.Cell;
import entities.Map;
import entities.Robot;
import enums.ArenaDetails;
import enums.CellType;
import enums.Constant;
import enums.Direction;
import ui.ControlPanel;
import ui.MapLayout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import algo.AstarTesting;
import algo.AStarTestRun;

import static enums.Constant.MAX_X_VAL;
import static enums.Constant.MAX_Y_VAL;

public class main extends JFrame {
    private MapLayout mapLayout;
    private ControlPanel controlPanel;
    private Map map;
    private Robot robot;

    public static void main(String[] args) throws InterruptedException {
        main gui = new main();
        gui.setVisible(true);
    }

    public main() throws InterruptedException {
        map = new Map();
        robot = new Robot(1,1, Direction.NORTH);
        initGUI();

//
//        JLabel[][] grid;
//        grid = new JLabel[MAX_Y_VAL + 1][MAX_X_VAL + 1];
//        int xCount = 0;
//        int yCount = 19;
//        for (int i = MAX_X_VAL; i >= 0; i--) {
//            for (int j = 0; j <= MAX_Y_VAL; j++) {
//                grid[i][j] = new JLabel("", SwingConstants.CENTER);
//                //Grid labels
//                //If row & col = 0, no text
//                /*
//                if (i == 0 && j == 0) grid[0][0].setText("");
//                else if (i == 0) grid[0][j].setText(String.valueOf(yCount++));
//                else if (j == 0) grid[i][0].setText(String.valueOf(xCount--));
//
//                 */
//
//                //Create grid lines
//                if (i != 0 && j != 0) {
//                    grid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
//                }
//                grid[i][j].setOpaque(true);
//                this.add(grid[i][j]);
//            }
//        }
//
//        Cell cell1 = map.getMap()[ArenaDetails.OBS1Y + 1][ArenaDetails.OBS1X + 1];
//        cell1.setObstacleDirection(ArenaDetails.OBS1DIR);
//        cell1.setObstacle(true);
//        mapLayout.getGrid()[ArenaDetails.OBS1Y + 1][ArenaDetails.OBS1X + 1].setBackground(cell1.cellColor(CellType.OBSTACLE));
//        mapLayout.getGrid()[ArenaDetails.OBS1Y + 1][ArenaDetails.OBS1X + 1].setText("\u2193");
//
//
//
//        Cell cell2 = map.getMap()[ArenaDetails.OBS2Y + 1][ArenaDetails.OBS2X + 1];
//        JLabel gridCell2 = grid[ArenaDetails.OBS2Y + 1][ArenaDetails.OBS2X + 1];
//        cell2.setObstacleDirection(ArenaDetails.OBS2DIR);
//        cell2.setObstacle(true);
//        gridCell2.setBackground(cell2.cellColor(CellType.OBSTACLE));
//        gridCell2.setText("\u2191");
//
//
//        Cell cell3 = map.getMap()[ArenaDetails.OBS3Y + 1][ArenaDetails.OBS3X + 1];
//        JLabel gridCell3 = grid[ArenaDetails.OBS3Y + 1][ArenaDetails.OBS3X + 1];
//        cell3.setObstacleDirection(ArenaDetails.OBS3DIR);
//        cell3.setObstacle(true);
//        gridCell3.setBackground(cell3.cellColor(CellType.OBSTACLE));
//        gridCell3.setText("\u2191");
//
//
//        Cell cell4 = map.getMap()[ArenaDetails.OBS4Y + 1][ArenaDetails.OBS4X + 1];
//        JLabel gridCell4 = grid[ArenaDetails.OBS4Y + 1][ArenaDetails.OBS4X + 1];
//        cell4.setObstacleDirection(ArenaDetails.OBS4DIR);
//        cell4.setObstacle(true);
//        gridCell4.setBackground(cell4.cellColor(CellType.OBSTACLE));
//        gridCell4.setText("\u2191");
//
//        Cell cell5 = map.getMap()[ArenaDetails.OBS5Y + 1][ArenaDetails.OBS5X + 1];
//        JLabel gridCell5 = grid[ArenaDetails.OBS5Y + 1][ArenaDetails.OBS5X + 1];
//        cell5.setObstacleDirection(ArenaDetails.OBS5DIR);
//        cell5.setObstacle(true);
//        gridCell5.setBackground(cell5.cellColor(CellType.OBSTACLE));
//        gridCell5.setText("\u2193");
//
//        ArrayList<Cell> test = new ArrayList<>();
//        test.add(cell1);
//        test.add(cell2);
//        test.add(cell3);
//        test.add(cell4);
//        test.add(cell5);
//
//
//
//        mapLayout.updateCellColors();
//
//        //AstarTesting.main();
//        AStarTestRun testRun = new AStarTestRun();
//        ArrayList<ArrayList<String>> moveCommands = testRun.runASearch(test);
//        System.out.println(moveCommands.get(0));
//        //Generate xValArray and yVal Array
//        ArrayList<ArrayList<String>> coordinatesDirectionArray = testRun.generateCoordinates(moveCommands.get(0), moveCommands.get(1));
//        //Generate Directions Array
//        ArrayList<Integer> xValArray = new ArrayList<Integer>();
//        for(int i = 0; i < coordinatesDirectionArray.get(0).size(); i++) {
//            xValArray.add(Integer.parseInt(coordinatesDirectionArray.get(0).get(i)));
//        }
//
//        ArrayList<Integer> yValArray = new ArrayList<Integer>();
//        for(int i = 0; i < coordinatesDirectionArray.get(1).size(); i++) {
//            yValArray.add(Integer.parseInt(coordinatesDirectionArray.get(1).get(i)));
//        }
//
//        ArrayList<Direction> directionArray = testRun.generateDirections(coordinatesDirectionArray.get(2));
//        //System.out.println(xValArray);
//        //System.out.println(yValArray);
//        //System.out.println(directionArray);
//
//
//
//        simulateMovement(xValArray, yValArray, directionArray);



    }

    private void initGUI(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 650);
        setLayout(new FlowLayout());
        setTitle("MDP simulator");
        mapLayout = new MapLayout(map,robot);
        add(mapLayout);
        controlPanel = new ControlPanel(map,robot,mapLayout);
        add(controlPanel);
        pack();
        setVisible(true);
        mapLayout.updateCellColors();
    }

    /*
    //Preserved original version
    private void simulateMovement(int[] xValArray, int[] yValArray, Direction[] directionArray) throws InterruptedException {
        for(int i= 0; i<=xValArray.length;i++){
            //Direction direction=Direction.RIGHT;
            TimeUnit.SECONDS.sleep(1);
            map.setVisited(robot.getxPos(),robot.getyPos(),robot.getDirection());
            robot.setxPos(xValArray[i]);
            robot.setyPos(yValArray[i]);
            robot.setDirection(directionArray[i]);
            mapLayout.updateCellColors();
        }
    }
    */
    //Testing version
    private void simulateMovement(ArrayList<Integer> xValArray, ArrayList<Integer> yValArray, ArrayList<Direction> directionArray) throws InterruptedException {
        for(int i= 0; i<xValArray.size();i++){
            //Direction direction=Direction.RIGHT;
            TimeUnit.MILLISECONDS.sleep(500);
            map.setVisited(robot.getyPos(),robot.getxPos(),robot.getDirection());
            robot.setyPos(yValArray.get(i));
            robot.setxPos(xValArray.get(i));
            robot.setDirection(directionArray.get(i));
            mapLayout.updateCellColors();
        }
    }




}
