package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import view.MainView;
import view.ViewDelegate;
import drop.Server;

public class LoginView extends JFrame {
    private JLabel hintLabel;
    private JTextField usernameText;
    private JButton submitButton;
    private String username = "";

    public LoginView() {
        init();
    }

    private void init() {
        setTitle("name dialogue");
        setLayout(new GridLayout(3, 1));
        setBounds(200, 200, 300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        hintLabel = new JLabel("Please enter your username: ");
        usernameText = new JTextField(1);
        usernameText.setSize(200, 50);
        submitButton = new JButton("Confirm");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
        		username = usernameText.getText();
            	if (username.equals("")) {
            		JOptionPane.showMessageDialog(null, "請輸入使用者名稱","Warning",JOptionPane.WARNING_MESSAGE);
            	} else {
            		username = usernameText.getText();
            		setVisible(false);
            	}
            }
        });

        getContentPane().add(hintLabel);
        getContentPane().add(usernameText);
        getContentPane().add(submitButton);

        setVisible(true);
    }

    public String getName() {
        return username;
    }
}
