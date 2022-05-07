package ui;

import java.awt.*;

public class CellColor {
    public Color cellColor(String type) {
        switch (type) {
            case "normal":
                return Color.white;
            case "robot":
                return Color.blue;
            case "start":
                return Color.cyan;
            case "end":
                return Color.yellow;
            case "head":
                return Color.red;
            case "visited":
                return Color.green;
            case "obstacle":
                return Color.ORANGE;
            default:
                return Color.black;
        }
    }
}
