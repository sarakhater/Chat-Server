package view;

import controller.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ServerFrame extends JFrame implements ActionListener {

    // Constants
    private static final Dimension TITLE_IMAGE_DIMENSION = new Dimension(100, 80);
    private static final Dimension INFO_IMAGES_DIMENSION = new Dimension(40, 40);

    private static final Font TITLE_FONT = new Font("Cooper Black", Font.BOLD, 50);
    private static final Font INFO_FONT = new Font(Font.DIALOG, Font.BOLD, 20);
    private static final Font MESSAGE_FONT = new Font(Font.DIALOG, Font.BOLD, 15);
    private static final Font BUTTONS_FONT = new Font(Font.DIALOG, Font.BOLD, 15);

    private static final String TITLE = "Chat Server";

    // Panels
    private JPanel panel_start = new JPanel(new BorderLayout());
    
    private JPanel panel_title = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
    
    private JPanel panel_actions = new JPanel();
    private JPanel panel_buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
    private JPanel panel_info = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));

    private JPanel panel_total = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JPanel panel_online = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JPanel panel_offline = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    
    private JPanel panel_statistics = new JPanel(new BorderLayout());

    private JPanel panel_message = new JPanel(new BorderLayout());

    // Labels
    private JLabel label_titleImage = new JLabel();
    private JLabel label_title = new JLabel(TITLE);

    private JLabel label_totalImage = new JLabel();
    private JLabel label_totalText = new JLabel("Total: ");
    private JLabel label_totalValue = new JLabel();

    private JLabel label_onlineImage = new JLabel();
    private JLabel label_onlineText = new JLabel("Online: ");
    private JLabel label_onlineValue = new JLabel();

    private JLabel label_offlineImage = new JLabel();
    private JLabel label_offlineText = new JLabel("Offline: ");
    private JLabel label_offlineValue = new JLabel();

    // Text Area
    private JTextArea textArea_message = new JTextArea();

    // Scroll Panes
    private JScrollPane scrollPane_message = new JScrollPane(textArea_message);

    // Buttons
    private JButton button_send = new JButton("<html> <p align=\"center\">Send<br/>Message </p></html>");
    private JButton button_start_stop = new JButton("Start Server");
    private JButton button_Advertise = new JButton("Send Advertisement");

    // FX
    private JFXPanel fxPanel = new JFXPanel();

    Controller controller;

    private boolean started;
    private boolean shiftPressed;

    // Constructors
    public ServerFrame(Controller controller) {
        super(TITLE);
        this.controller = controller;
        initComponents();
    }

    // Initialize Components
    private void initComponents() {
        // Buttons Font
        button_start_stop.setFont(BUTTONS_FONT);
        button_Advertise.setFont(BUTTONS_FONT);
        button_send.setFont(BUTTONS_FONT);

        // Buttons Size
        button_start_stop.setPreferredSize(new Dimension(200, 35));
        button_Advertise.setPreferredSize(new Dimension(200, 35));
        button_send.setPreferredSize(new Dimension(120, 75));

        // Buttons Listeners
        button_start_stop.addActionListener(this);
        button_Advertise.addActionListener(this);
        button_send.addActionListener(this);

        button_Advertise.setEnabled(false);
        button_send.setEnabled(false);
        textArea_message.setEnabled(false);

        // Message Text Area
        textArea_message.setWrapStyleWord(true);
        textArea_message.setLineWrap(true);
        textArea_message.setFont(MESSAGE_FONT);
        textArea_message.addKeyListener(new KeyHandler());
        textArea_message.getDocument().addDocumentListener(new DocumentHandler());

        // Title Panel
        label_titleImage.setIcon(getImageIcon("images\\logo.png", TITLE_IMAGE_DIMENSION));
        label_title.setFont(TITLE_FONT);
        panel_title.add(label_titleImage);
        panel_title.add(label_title);

        // Total Panel
        label_totalImage.setIcon(getImageIcon("images\\total.png", INFO_IMAGES_DIMENSION));
        label_totalText.setFont(INFO_FONT);
        label_totalValue.setFont(INFO_FONT);
        panel_total.add(label_totalImage);
        panel_total.add(label_totalText);
        panel_total.add(label_totalValue);
        
        // Online Panel
        label_onlineImage.setIcon(getImageIcon("images\\online.png", INFO_IMAGES_DIMENSION));
        label_onlineText.setFont(INFO_FONT);
        label_onlineValue.setFont(INFO_FONT);
        panel_online.add(label_onlineImage);
        panel_online.add(label_onlineText);
        panel_online.add(label_onlineValue);

        // Offline Panel
        label_offlineImage.setIcon(getImageIcon("images\\offline.png", INFO_IMAGES_DIMENSION));
        label_offlineText.setFont(INFO_FONT);
        label_offlineValue.setFont(INFO_FONT);
        panel_offline.add(label_offlineImage);
        panel_offline.add(label_offlineText);
        panel_offline.add(label_offlineValue);

        panel_buttons.add(button_start_stop);
        panel_buttons.add(button_Advertise);

        // Info Panel
        panel_info.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        panel_info.add(panel_total);
        panel_info.add(panel_offline);
        panel_info.add(panel_online);
        
        // Actions Panel
        panel_actions.setLayout(new BoxLayout(panel_actions, BoxLayout.PAGE_AXIS));
        panel_actions.add(panel_buttons, BorderLayout.PAGE_END);
        panel_actions.add(panel_info, BorderLayout.PAGE_END);

        // Start Panel
        panel_start.add(panel_title);
        panel_start.add(panel_actions, BorderLayout.PAGE_END);

        // Message Scroll Pane
        scrollPane_message.setPreferredSize(new Dimension(300, 80));

        // Message Panel
        panel_message.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel_message.add(scrollPane_message);
        panel_message.add(button_send, BorderLayout.LINE_END);
        
        // Statistics panel
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                initFX();
            }
        });
        JPanel panel_statistics = new JPanel(new FlowLayout());
        panel_statistics.setBackground(Color.WHITE);
        panel_statistics.add(fxPanel);
        
        this.addWindowListener(new WindowHandler());

        this.add(panel_start, BorderLayout.PAGE_START);
        this.add(panel_statistics);
        this.add(panel_message, BorderLayout.PAGE_END);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(new Dimension(550, 600));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (screenSize.getWidth() - getWidth()) / 2, (int) (screenSize.getHeight() - getHeight()) / 2);
    }

    // Get Image Icon
    private ImageIcon getImageIcon(String path, Dimension dimension) {
        try {
            return new ImageIcon(ImageIO.read(new File(path)).getScaledInstance(
                    (int) dimension.getWidth(), (int) dimension.getHeight(), Image.SCALE_SMOOTH));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Update Info
    public void updateInfo(int total, int online) {
        if (online < 0) {
            online = 0;
        }
        final int onlineNumber = online;
        final int offlineNumber = total - online;

        label_totalValue.setText(total + "");
        label_onlineValue.setText(online + "");
        label_offlineValue.setText(total - online + "");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Scene scene = new Scene(new Group());
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                pieChartData.add(new PieChart.Data("Offline", offlineNumber));
                pieChartData.add(new PieChart.Data("", 0));
                pieChartData.add(new PieChart.Data("Online", onlineNumber));

                PieChart chart = new PieChart(pieChartData);
                chart.setLegendVisible(false);
                chart.setAnimated(true);
                chart.setPrefSize(275, 275);

                ((Group) scene.getRoot()).getChildren().add(chart);
                fxPanel.setScene(scene);
            }
        });
    }

    // Set Started
    public void setStarted(boolean started) {
        this.started = started;
        if (started) {
            button_start_stop.setText("Stop Server");
            button_Advertise.setEnabled(true);
            textArea_message.setEnabled(true);
            button_send.setEnabled(!textArea_message.getText().trim().equals(""));
            textArea_message.requestFocus();
        } else {
            button_start_stop.setText("Start Server");
            button_Advertise.setEnabled(false);
            textArea_message.setEnabled(false);
            button_send.setEnabled(false);
        }
    }

    // Send Message
    private void sendMessage() {
        controller.sendSystemMessage(textArea_message.getText());
        textArea_message.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == button_start_stop) {
            if (started) {
                try {
                    controller.stop();
                } catch (RemoteException | NotBoundException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    controller.start();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (event.getSource() == button_Advertise) {
            controller.sendAdvertisement("Advertisement");
        } else if (event.getSource() == button_send) {
            sendMessage();
        }
    }

    // Key Handler
    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getSource() == textArea_message) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (shiftPressed) {
                        textArea_message.append("\n");
                    } else if (!textArea_message.getText().trim().equals("")) {
                        sendMessage();
                        event.consume();
                    }
                } else if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = true;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
                shiftPressed = false;
            }
        }

    }

    // Document Handler
    private class DocumentHandler implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent event) {
            if (event.getDocument() == textArea_message.getDocument()) {
                button_send.setEnabled(started && !textArea_message.getText().trim().equals(""));
            }
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            if (event.getDocument() == textArea_message.getDocument()) {
                button_send.setEnabled(!textArea_message.getText().trim().equals(""));
            }
        }

        @Override
        public void changedUpdate(DocumentEvent event) {
        }

    }

    // Window Handler
    private class WindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            String title = "Exit Confirmation";
            String message = "Are you sure you want to exit ?";
            int response = JOptionPane.showConfirmDialog(ServerFrame.this, message, title, JOptionPane.YES_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                if (started) {
                    try {
                        controller.stop();
                    } catch (RemoteException | NotBoundException ex) {
                        ex.printStackTrace();
                    }
                }
                dispose();
                System.exit(0);
            }
        }
    }

}
