package ui;

import entities.Map;
import entities.Robot;
import rpi.RPICommunicator;

import javax.swing.*;
import java.awt.*;
import java.net.ConnectException;

/**
 * For the control panel on the right of the map
 */
public class ControlPanel extends JPanel {
    private static int PADDING_HEIGHT = 10;
    private static int WIDTH = 150;
    private static int HEIGHT = 30;
    private static int X_COORDS = 10;

    private MapLayout mapLayout;
    private Robot robot;
    private Map map;
    private ButtonsController buttonsController;
    private JButton resetButton;
    private JButton clearButton;
    private JButton hamPathButton;
    private JButton connectButton;
    private JButton sendButton;
    private JButton receiveButton;

    private RPICommunicator rpiCommunicator;

    public ControlPanel(Map map, Robot robot, MapLayout mapLayout) {
        setLayout(null);
        setPreferredSize(new Dimension(200, 700));

        this.mapLayout = mapLayout;
        this.map = map;
        this.robot = robot;
        buttonsController = new ButtonsController(map, robot, mapLayout);

        resetButton = new JButton();
        clearButton = new JButton();
        hamPathButton = new JButton();
        connectButton = new JButton();
        sendButton = new JButton();
        receiveButton = new JButton();

        int count = 0;

        resetButton.setBounds(X_COORDS, HEIGHT * count + PADDING_HEIGHT * count++, WIDTH, HEIGHT);
        resetButton.setText("Reset arena");
        add(resetButton);

        clearButton.setBounds(X_COORDS, HEIGHT * count + PADDING_HEIGHT * count++, WIDTH, HEIGHT);
        clearButton.setText("Clear obstacles");
        add(clearButton);

        hamPathButton.setBounds(X_COORDS, HEIGHT * count + PADDING_HEIGHT * count++, WIDTH, HEIGHT);
        hamPathButton.setText("Hamiltonian Path");
        add(hamPathButton);

        connectButton.setBounds(X_COORDS, HEIGHT * count + PADDING_HEIGHT * count++, WIDTH, HEIGHT);
        connectButton.setText("Connect to RPI");
        add(connectButton);

        receiveButton.setBounds(X_COORDS, HEIGHT * count + PADDING_HEIGHT * count++, WIDTH, HEIGHT);
        receiveButton.setVisible(false);
        receiveButton.setText("Receive");
        add(receiveButton);

        sendButton.setBounds(X_COORDS, HEIGHT * count + PADDING_HEIGHT * count++, WIDTH, HEIGHT);
        sendButton.setVisible(false);
        sendButton.setText("Send Path to RPI");
        add(sendButton);


        resetButton.addActionListener(e -> {
            buttonsController.resetMap();
        });

        clearButton.addActionListener(e -> {
            buttonsController.clearMap();
        });


        hamPathButton.addActionListener(e -> new Thread(() -> {
            buttonsController.runHamPath();
        }).start());

        connectButton.addActionListener(e -> new Thread(() -> {
//            boolean connected = false;
            connectButton.setEnabled(false);
            if (connectButton.getText().equals("Connect to RPI")) {
                rpiCommunicator = RPICommunicator.getInstance();
//                while (!connected) connected = rpiCommunicator.connect();
                while (!rpiCommunicator.connect()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("Connected");

                buttonsController.receiveMsg(rpiCommunicator, this);
                buttonsController.sendMessages(rpiCommunicator);
                //Set true to see "Receive and Send button"
                sendButton.setVisible(false);
                receiveButton.setVisible(true);
                connectButton.setEnabled(true);
                connectButton.setText("Disconnect");
            } else {
                connectButton.setText("Connect to RPI");
                connectButton.setEnabled(true);
                rpiCommunicator.disconnect();
                sendButton.setVisible(false);
                receiveButton.setVisible(false);
            }
        }).start());

        sendButton.addActionListener(e -> new Thread(() -> buttonsController.sendMessages(rpiCommunicator)).start());

        receiveButton.addActionListener(e -> new Thread(() -> {
            System.out.println("Received button pressed");
            receiveButton.setEnabled(false);
            buttonsController.receiveMsg(rpiCommunicator, this);
            buttonsController.sendMessages(rpiCommunicator);
        }).start());
    }

    public JButton getReceiveButton() {
        return receiveButton;
    }
}
