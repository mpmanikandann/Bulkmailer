package org.webluminous.mail.gui;

import org.webluminous.mail.StringUtils;
import org.webluminous.mail.core.InputReader;
import org.webluminous.mail.core.Static;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Manikandan on 11/5/2016.
 */
public class MainPanel extends JPanel {

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

  public MainPanel() {
    setName(Static.GUI_TITLE);
    setVisible(true);
    setAutoscrolls(false);
    init();
  }

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

    csvbutton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fileChooser(csvfilechooser);
        if (csvfilechooser.getSelectedFile() != null) {
          csvfiletext.setText(csvfilechooser.getSelectedFile().toString());
        }
      }
    });
    //Template label,text,button
    templatelabel.setSize(100, 20);
    templatetext.setPreferredSize(new Dimension(200, 20));
    templatebutton.setSize(100, 20);
    templatebutton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fileChooser(templatechooser);
        if (templatechooser.getSelectedFile() != null) {
          templatetext.setText(templatechooser.getSelectedFile().toString());
        }
      }
    });

    sendemailbutton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (StringUtils.isNotNull(csvfiletext.getText()) && StringUtils.isNotNull(templatetext.getText())) {
          InputReader reader = new InputReader(csvfiletext.getText(), templatetext.getText());
          setTable(reader);
        } else {
          showErrorDialog(Static.ERR_ON_INPUT_FILE_VALIDATION, Static.VALIDATION_MESSAGE);
        }
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
   * @param title Title of Error Message Dialog
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
    DefaultTableModel tableModel = new DefaultTableModel(reader.getFilecontent().size(),reader.getHeader().split(Static.CSV_FILE_SEPERATOR).length);
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
