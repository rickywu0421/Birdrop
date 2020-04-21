package view;

import view.MainView;

public class ViewDelegate {
    private static MainView mainView;

    public static void setMainView(MainView main) {
        mainView = main;
    }

    public static MainView getMainView() {
        return mainView;
    }
}