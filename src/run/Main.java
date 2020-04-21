package run;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.SwingUtilities;

import view.LoginView;
import view.MainView;
import view.ViewDelegate;
import drop.Server;
import dns.NeighborRegistration;
import dns.NeighborDiscovery;

public class Main {
    private static String name;

    public static void main(String[] args) {
        new Server();

        LoginView loginView = new LoginView();
        while ((name = loginView.getName()).equals(""))
            ;   

        // registration
        try {
            new NeighborRegistration(InetAddress.getLocalHost(), name);
        } catch(UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
            
        // discovery
        try {
            new NeighborDiscovery(InetAddress.getLocalHost());
        } catch(UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        MainView mainView = new MainView(name);
        ViewDelegate.setMainView(mainView);
    }
}