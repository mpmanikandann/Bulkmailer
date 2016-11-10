package org.webluminous.mail.gui;

import org.apache.log4j.Logger;
import org.webluminous.mail.StringUtils;
import org.webluminous.mail.core.InputReader;
import org.webluminous.mail.core.Static;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.TransformerConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Manikandan on 11/5/2016.
 */
public class MainPanelGUI extends JPanel {

    private static final long serialVersionUID = 7019412141776293946L;
    private static final Logger LOGGER = Logger.getLogger(MainPanelGUI.class);
    private final JLabel csvlabel = new JLabel(Static.GUI_SELECT_THE_CSV_FILE);
    private final JTextField csvfiletext = new JTextField();
    private final JButton csvbutton = new JButton(Static.CHOOSE_BUTTON_TEXT);
    private final JTextField templatetext = new JTextField();
    private final JButton templatebutton = new JButton(Static.CHOOSE_BUTTON_TEXT);
    private final JFileChooser csvfilechooser = new JFileChooser();
    private final JLabel templatelabel = new JLabel(Static.GUI_EMAIL_TEMPLATE_LABEL);
    private final JFileChooser templatechooser = new JFileChooser();
    private final JPanel csvpanel = new JPanel(new FlowLayout());
    private final JPanel templatepanel = new JPanel(new FlowLayout());
    private JTable inputcsvtable;
    private final JPanel tablepanel = new JPanel();
    private final JButton sendemailbutton = new JButton("Send Bulk Emails");

    public MainPanelGUI() {
        setName(Static.GUI_TITLE);
        setVisible(true);
        setAutoscrolls(false);
        init();
    }

    /**
     * Init method where Components are created and added to the Jpanel
     */
    private void init() {
        //CSV File loading
        csvlabel.setSize(100, 20);
        csvfiletext.setPreferredSize(new Dimension(200, 20));
        csvbutton.setSize(100, 20);
        //csv file chooser
        csvfilechooser.setAcceptAllFileFilterUsed(false);
        csvfilechooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return ".csv";
            }
        });
        //template file chooser
        templatechooser.setAcceptAllFileFilterUsed(true);
        templatechooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".xsl") || f.getName().endsWith(".xslt");
            }

            @Override
            public String getDescription() {
                return "XSLT files only";
            }
        });
        csvbutton.addActionListener(button -> {
                    fileChooser(csvfilechooser);
                    if (csvfilechooser.getSelectedFile() != null) {
                        csvfiletext.setText(csvfilechooser.getSelectedFile().toString());
                    }
                }
        );
        //Template label,text,button
        templatelabel.setSize(100, 20);
        templatetext.setPreferredSize(new Dimension(200, 20));
        templatebutton.setSize(100, 20);
        templatebutton.addActionListener(tbutton -> {
            fileChooser(templatechooser);
            if (templatechooser.getSelectedFile() != null) {
                templatetext.setText(templatechooser.getSelectedFile().toString());
            }

        });

        sendemailbutton.addActionListener(mailbutton -> {
            if (StringUtils.isNotNull(csvfiletext.getText()) && StringUtils.isNotNull(templatetext.getText())) {
                InputReader reader = null;
                try {
                    reader = new InputReader(csvfiletext.getText(), templatetext.getText());
                } catch (FileNotFoundException | TransformerConfigurationException ex) {
                    LOGGER.error("Error Ocuured on the Sending mail", ex);
                    showErrorDialog(Static.ERR_ON_INPUT_FILE_VALIDATION, ex.getMessage());
                }
                setTable(reader);
            } else {
                showErrorDialog(Static.ERR_ON_INPUT_FILE_VALIDATION, Static.VALIDATION_MESSAGE);
            }
        });

        //Adding Componets to correspondingpanel
        csvpanel.add(csvlabel);
        csvpanel.add(csvfiletext);
        csvpanel.add(csvbutton);
        templatepanel.add(templatelabel);
        templatepanel.add(templatetext);
        templatepanel.add(templatebutton);
        templatepanel.add(sendemailbutton);
        //Adding Panel to Main panel
        this.add(csvpanel);
        this.add(templatepanel);
        this.add(tablepanel);
    }

    /**
     * Configuration for file chooser
     *
     * @param fileChooser File Chooser Object
     */
    private void fileChooser(JFileChooser fileChooser) {
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showOpenDialog(this);
    }

    /**
     * @param message Message to shown
     * @param title   Title of Error Message Dialog
     */
    private void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is used initalize the Jtable with rows and columns
     *
     * @param reader Passing Input Reader object to get the data values
     */
    private void setTable(InputReader reader) {
        DefaultTableModel tableModel = new DefaultTableModel(reader.getFilecontent().size(), reader.getHeader().split(Static.CSV_FILE_SEPERATOR).length);
        int i = 1;
        for (String content : reader.getFilecontent()) {
            tableModel.insertRow(i++, content.split(Static.CSV_FILE_SEPERATOR));
        }
        this.inputcsvtable = new JTable(tableModel);
        this.inputcsvtable.setAutoCreateRowSorter(true);
        this.inputcsvtable.setAutoCreateColumnsFromModel(true);
        tablepanel.add(this.inputcsvtable);
    }
}
