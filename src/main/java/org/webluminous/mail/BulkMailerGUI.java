package org.webluminous.mail;

import org.webluminous.mail.core.Static;
import org.webluminous.mail.gui.MainPanelGUI;

import javax.swing.*;
import java.awt.*;

/**
 * This Class is the main class where execution begins UI Invoked based on the input's provide
 * process will be started
 *
 * @author Manikandan on 11/5/2016.
 * @version 1.0
 *          `
 */
public class BulkMailerGUI {
    private final JFrame mainframe = new JFrame();

    public BulkMailerGUI() {
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainframe.setTitle(Static.GUI_TITLE);
        mainframe.setVisible(true);
        mainframe.setLayout(new BorderLayout());
        mainframe.setSize(800, 500);
    }

    public JFrame getMainframe() {
        return mainframe;
    }

    public static void main(String[] args) {
        BulkMailerGUI email = new BulkMailerGUI();
        MainPanelGUI panel = new MainPanelGUI();
        email.getMainframe().add(panel);
    }

}
