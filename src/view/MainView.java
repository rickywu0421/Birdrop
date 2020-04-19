package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

import dns.NeighborRegistration;
import dns.NeighborDiscovery;
import dns.UserInfo;
import drop.Client;
import speedtest.SpeedTest;

public class MainView extends JFrame {
    private JButton findOthersButton, messengerButton, speedtestButton;
    private JLabel usernameLabel, initLabel, speedtestLabel;
    private Font usernameFont, buttonFont, speedtestFont;
    private List<UserInfo> neighbors;
    private List<JButton> userButtonList;

    private final int maxNeighbors = 5;

    public MainView(String username) {
        init(username);
    }

    private void init(String username) {
        setTitle("Windrop");
        setLayout(null);
        setBounds(50, 50, 1280, 765);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        usernameFont = new Font("cursive", Font.PLAIN, 20);
        buttonFont = new Font("cursive", Font.PLAIN, 25);
        speedtestFont = new Font("cursive", Font.PLAIN, 50);

        usernameLabel = new JLabel("hello, " + username);
        usernameLabel.setBounds(820, 0, 200, 60);
        usernameLabel.setFont(usernameFont);

        speedtestLabel = new JLabel("0  Mbps");
        speedtestLabel.setBounds(400, 300, 600, 100);
        speedtestLabel.setFont(speedtestFont);
        speedtestLabel.setBorder(BorderFactory.createLineBorder(Color.black, 5));
        speedtestLabel.setVisible(false);

        userButtonList = new ArrayList<JButton>();

        final int[][] bounds = {{500, 60, 250, 200},
                                {800, 60, 250, 200},
                                {650, 280, 250, 200},
                                {500, 500, 250, 200},
                                {800, 500, 250, 200}};

        for (int i = 0; i < maxNeighbors; i++) {
            JButton userButton = new JButton();
            userButton.setBounds(bounds[i][0], bounds[i][1], bounds[i][2], bounds[i][3]);
            userButton.setFont(buttonFont);
            userButton.setVisible(false);
            userButtonList.add(userButton);
        }

        findOthersButton = new JButton("find others");
        findOthersButton.setBounds(0, 0, 255, 255);
        findOthersButton.setFont(buttonFont);
        findOthersButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                speedtestLabel.setVisible(false);
                neighbors = NeighborDiscovery.getNeighbors();

                for (int i = 0; i < neighbors.size(); i++) {
                    JButton userButton = userButtonList.get(i);
                    UserInfo userInfo = neighbors.get(i);
                    
                    userButton.setText("<html>" + userInfo.getName() + "<br />" + userInfo.getHostAddr() + "</html>");
                    userButton.setVisible(true);

                    userButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent event) {
                            File file = null;
                            file = popSenderFileChooser();
                        
                            if (file != null){
                                new Client().handleSend(file, userInfo.getHostAddr());
                            }
                        }
                    });
                }
            } 
        });
        
        messengerButton = new JButton("messenger");
        messengerButton.setBounds(0, 255, 255, 255);
        messengerButton.setFont(buttonFont);
        messengerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

            } 
        });
        
        speedtestButton = new JButton("speedtest");
        speedtestButton.setBounds(0, 510, 255, 255);
        speedtestButton.setFont(buttonFont);
        speedtestButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for (JButton userButton : userButtonList) {
                            userButton.setVisible(false);
                        }

                        speedtestLabel.setVisible(true);
                        SpeedTest speedTest = new SpeedTest();
                        while (!speedTest.getIsFinished()) {
                            speedtestLabel.setText(speedTest.getSpeed() + "  Mbps");
                        }
                    }
                }).start();
            } 
        });

        getContentPane().add(usernameLabel);
        getContentPane().add(speedtestLabel);
        getContentPane().add(findOthersButton);
        getContentPane().add(messengerButton);
        getContentPane().add(speedtestButton);
        for (JButton userButton : userButtonList) {
            getContentPane().add(userButton);
        }
        setVisible(true);
    }

    public File popSenderFileChooser() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showOpenDialog(null);

        if (ret == JFileChooser.APPROVE_OPTION) {    
            file = fileChooser.getSelectedFile();
        }

        return file;
    }

    public File popReceiverFileChooser() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int ret = fileChooser.showOpenDialog(null);

        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        return file;
    }

    public boolean popOptionDialog(String sender, String filename) {
        String title = "confirmation";
        String text = sender + " send " + filename + " to you. Do you accept?";
        int result = JOptionPane.showConfirmDialog(
            null, title, text, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            return true;
        }

        return false;
    }
}