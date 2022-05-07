package ui;

import algo.AStarTestRun;
import algo.HamPath;
import algo.NearestNeighbour;
import entities.Cell;
import entities.Map;
import entities.Robot;
import enums.Constant;
import enums.Direction;
import rpi.RPICommunicator;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import static enums.Constant.delayMessageTimer;

/**
 * Functions for the button
 */
public class ButtonsController {
    private MapLayout mapLayout;
    private Map map;
    private Robot robot;
    private HamPath hamPath;

    public ButtonsController(Map map, Robot robot, MapLayout mapLayout) {
        this.map = map;
        this.robot = robot;
        this.mapLayout = mapLayout;
        hamPath = new HamPath(map, robot, mapLayout);
    }

    public void resetMap() {
        _map(false);
    }

    public void clearMap() {
        _map(true);
    }

    private void _map(Boolean clear) {
        Cell[][] cellsArray = map.getMap();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Cell cell = cellsArray[i][j];
                if (cell.isVisited()) {
                    cell.setVisited(false);
                    cell.setCost(0);
                    cell.setTotalCost(999);
                }
                if (clear) {
                    cell.resetCell();
                    map.setObstacles(null);
                }
            }
        }
        robot.setxPos(1);
        robot.setyPos(1);
        robot.setDirection(Direction.NORTH);

        if (clear) mapLayout.clearCellText();
        hamPath.setDirectionArray(null);
        hamPath.setxValArray(null);
        hamPath.setyValArray(null);
        mapLayout.updateCellColors();

    }

    public void runHamPath() {
        try {
            hamPath.run(map.getObstacles(), true);
        } catch (NullPointerException e) {
            System.out.println("Add an obstacle first");
        }
    }

    public void sendMessages(RPICommunicator rpiCommunicator) {
        try {
            _sendToRPI(rpiCommunicator);
            _sendToAndroid(rpiCommunicator);
            hamPath.animate();
        } catch (NullPointerException e) {
            System.out.println("Add an obstacle first");
        }
    }

    private void _sendToRPI(RPICommunicator rpiCommunicator) {
        ArrayList<String> pathArr = hamPath.run(map.getObstacles(), false);
        String path = String.join(", ", pathArr);
        String message = "ALG|CAR|SP_" + path;
        rpiCommunicator.sendMessage(message);
    }

    private void _sendToAndroid(RPICommunicator rpiCommunicator) {
        try {
            Thread.sleep(delayMessageTimer);
            //Send order of obstacle visit
            String message = "ALG|AND|OBST_" + _obstMessage(NearestNeighbour.getObstArr());
            rpiCommunicator.sendMessage(message);
            System.out.println("Sent \""+message+"\"successfully");

            //Send Row message
            Thread.sleep(delayMessageTimer);
            String message2 = " ALG|AND|ROW_" + _RowColMessage(hamPath.getyValArray(), true);
            rpiCommunicator.sendMessage(message2);
            System.out.println("Sent \""+message2+"\" successfully");

            //Send COL message
            Thread.sleep(delayMessageTimer);
            String message3 = "ALG|AND|COL_" + _RowColMessage(hamPath.getxValArray(), false);
            rpiCommunicator.sendMessage(message3);
            System.out.println("Sent \""+message3+"\" successfully");

            //Send DIRECTION message
            Thread.sleep(delayMessageTimer);
            String directionArr = String.join(", ", hamPath.getDirectionArray().toString());
            String message4 = "ALG|AND|DIR_" + directionArr;
            rpiCommunicator.sendMessage(message4);
            System.out.println("Sent \""+message4+"\" successfully");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String _obstMessage(ArrayList<Cell> obstacleLists) {
        String temp = "";
        for (Cell c : obstacleLists) {
            temp += c.toString() + ",";
        }

        temp = temp.substring(0, temp.lastIndexOf(","));
        return temp;
    }

    //Send Robot's head position
    private String _RowColMessage(ArrayList<Integer> arrayList, boolean row) {
        String temp = "";
        int count = 0;
        //If sending row message
        if (row) {
            for (int i : arrayList) {
                temp = temp + (i) + ",";
            }
        }
        //If sending col message
        else {
            for (int i : arrayList) {
                temp = temp + (i) + ",";
            }
        }
        temp = temp.substring(0, temp.lastIndexOf(","));
        return temp;
    }

    public void receiveMsg(RPICommunicator rpiCommunicator, ControlPanel controlPanel) {
        while (true) {
            try {
                String receivedMsg = rpiCommunicator.readMessage();
                System.out.println("Received message: " + receivedMsg);
                //String format: RPI|ALG|MESSAGE
                //Format: RPI|ALG|X1Y1D1,X2Y2D2,X3Y3D3
                //Example: RPI|ALG|0011N,1122S,2233W
                try {
                    if (receivedMsg.length() == 0) {
                        throw new StringIndexOutOfBoundsException();
                    }
                    clearMap();
                    ArrayList<Cell> obstacleList = new ArrayList<>();

                    String[] obstacles = receivedMsg.split(",");

                    for (String s : obstacles) {
                        //First 2 character is col value
                        int col = Integer.parseInt(s.charAt(0) + String.valueOf(s.charAt(1)));
                        //Followed by 2 character is row value
                        int row = Integer.parseInt((s.charAt(2)) + String.valueOf(s.charAt(3)));
                        Direction direction = null;
                        //Last char is Direction initial
                        switch (String.valueOf(s.charAt(4))) {
                            case "N" -> {
                                direction = Direction.NORTH;
                                mapLayout.getGrid()[row + 1][col + 1].setText("\u2191");
                            }
                            case "E" -> {
                                direction = Direction.EAST;
                                mapLayout.getGrid()[row + 1][col + 1].setText("\u2192");
                            }
                            case "S" -> {
                                direction = Direction.SOUTH;
                                mapLayout.getGrid()[row + 1][col + 1].setText("\u2193");
                            }
                            case "W" -> {
                                direction = Direction.WEST;
                                mapLayout.getGrid()[row + 1][col + 1].setText("\u2190");
                            }
                            default -> System.out.println("Direction is NULL");
                        }
                        map.getMap()[row][col].setObstacle(true);
                        map.getMap()[row][col].setObstacleDirection(direction);
                        obstacleList.add(map.getMap()[row][col]);
                        map.setObstacles(obstacleList);
                        System.out.println("Obstacle added. R: " + row + " Col: " + col);
                    }
                } catch (StringIndexOutOfBoundsException e) { //If wrong message received
                    Thread.sleep(Constant.sleepTimer * 2);
                    continue;
                }
                mapLayout.updateCellColors();
                break;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        controlPanel.getReceiveButton().setEnabled(true);
    }


}