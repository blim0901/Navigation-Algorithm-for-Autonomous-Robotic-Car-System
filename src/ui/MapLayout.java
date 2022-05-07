package ui;

import entities.Cell;
import entities.Map;
import entities.Robot;
import enums.CellType;
import enums.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import static enums.Constant.MAX_X_VAL;
import static enums.Constant.MAX_Y_VAL;

/**
 * Map GUI related stuff
 */
public class MapLayout extends JPanel {
    /**
     * Grid = gui array
     */
    private JLabel[][] grid;
    private Map map;
    private Robot robot;

    /**
     * Constructor that initialises the map
     */
    public MapLayout(Map map, Robot robot) {
        super(new GridLayout(MAX_X_VAL + 1, MAX_Y_VAL + 1));
        this.map = map;
        this.robot = robot;
        this.setPreferredSize(new Dimension(700, 700));
        createMap();
        clickListener();
    }

    /**
     * Create empty layout of the map
     */
    private void createMap() {
        grid = new JLabel[MAX_Y_VAL + 1][MAX_X_VAL + 1];
        int yCount = 0;
        int xCount = 19;
        for (int i = MAX_Y_VAL; i >= 0; i--) {
            for (int j = 0; j <= MAX_X_VAL; j++) {
                grid[i][j] = new JLabel("", SwingConstants.CENTER);
                //Grid labels
                //If row & col = 0, no text
                if (i == 0 && j == 0) grid[0][0].setText("");
                else if (i == 0) grid[0][j].setText(String.valueOf(yCount++));
                else if (j == 0) grid[i][0].setText(String.valueOf(xCount--));


                //Create grid lines
                if (i != 0 && j != 0) {
                    grid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }
                grid[i][j].setOpaque(true);
                this.add(grid[i][j]);
            }
        }
    }


    public JLabel[][] getGrid() {
        return this.grid;
    }

    /**
     * Add colors to the grid
     */
    public void updateCellColors() {
        Cell[][] cellsArray = map.getMap();
        int yPos = robot.getyPos();
        int xPos = robot.getxPos();
        Color color;

        //Starting area 4x4
        color = Cell.cellColor(CellType.START);
        //Continue after starting area
        for (int i = 0; i < MAX_Y_VAL; i++) {
            for (int j = 0; j < MAX_X_VAL; j++) {
                Cell cell = cellsArray[i][j];
                if(i<=3 && j<=3) {
                    color = cell.cellColor(CellType.START);
                }
                else if (cell.isObstacle()) {
                    color = cell.cellColor(CellType.OBSTACLE);

                } else if (cell.isVisited()) {
                    color = cell.cellColor(CellType.VISITED);
                } else {
                    color = cell.cellColor(CellType.NORMAL);
                }
                grid[i+1][j+1].setBackground(color);
            }
        }
        //Robot body
        color = Cell.cellColor(CellType.ROBOT);
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                grid[yPos + i][xPos + j].setBackground(color);
            }
        }

        //Robot head
        color = Cell.cellColor(CellType.ROBOTHEAD);
        switch (robot.getDirection()) {
            case NORTH -> grid[yPos + 2][xPos + 1].setBackground(color);
            case SOUTH -> grid[yPos][xPos + 1].setBackground(color);
            case WEST -> grid[yPos + 1][xPos].setBackground(color);
            case EAST -> grid[yPos + 1][xPos + 2].setBackground(color);
        }

    }

    /**
     * Listen to the click on the grid
     */
    private void clickListener() {
        for (int i = 20; i >= 0; i--) {
            for (int j = 0; j <= 20; j++) {
                grid[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Object source = e.getSource();
                        for (int i = 20; i >= 0; i--) {
                            for (int j = 0; j <= 20; j++) {
                                //Find the grid where the MouseEvent occurs
                                try {
                                    if (grid[i][j] == source) editObstacle(i, j);
                                } catch (ArrayIndexOutOfBoundsException x) {
                                    System.out.println("Out of bounds");
                                }
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
            }
        }
    }

    /**
     * Edit obstacle on click
     *
     * @param row row of the cell
     * @param col col of the cell
     */
    public void editObstacle(int row, int col) {
        Cell cell = map.getMap()[row - 1][col - 1];
        JLabel gridCell = grid[row][col];
        if (cell.isObstacle()) {
            map.getObstacles().remove(cell);
            Direction cellDirection = cell.getObstacleDirection();
            //Arrow follows clockwise direction

            //Direction-less obstacle -> Up obstacle
            if (cellDirection == Direction.NORTH) {
                cell.setObstacleDirection(Direction.EAST);
                gridCell.setText("\u2192");
                map.getObstacles().add(cell);
            }
            //Right obstacle -> Down obstacle
            else if (cellDirection == Direction.EAST) {
                cell.setObstacleDirection(Direction.SOUTH);
                gridCell.setText("\u2193");
                map.getObstacles().add(cell);
            }
            //Down obstacle -> Left obstacle
            else if (cellDirection == Direction.SOUTH) {
                cell.setObstacleDirection(Direction.WEST);
                gridCell.setText("\u2190");
                map.getObstacles().add(cell);

            }
            //Remove obstacle
            else if (cellDirection == Direction.WEST) {
                cell.setObstacle(false);
                gridCell.setBackground(cell.cellColor(CellType.NORMAL));
                gridCell.setText("");
            }
            //If currently no obstacle
        } else {
            cell.setObstacle(true);
            cell.setObstacleDirection(Direction.NORTH);
            gridCell.setText("\u2191");
            gridCell.setBackground(cell.cellColor(CellType.OBSTACLE));
            if(map.getObstacles()==null||map.getObstacles().size()==0) {
                map.setObstacles(new ArrayList<>());
            }
                 map.getObstacles().add(cell);

        }
    }

    public void clearCellText() {
        for (int i = 1; i <= MAX_X_VAL; i++) {
            for (int j = 1; j <= MAX_Y_VAL; j++) {
                grid[i][j].setText("");
            }
        }
    }


}

