package pw.latematt.xiv;

import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Rederpz
 */

public class XIVTray {

    private ExternalFrame externalGUI;

    private PopupMenu menu;
    private TrayIcon icon;

    private MenuItem hideClient, hideTray, showExternal;

    public XIVTray() {
        if(!SystemTray.isSupported())
            return;

        this.icon = new TrayIcon(new BufferedImage(16, 16, 1), "XIV");

        this.menu = new PopupMenu();
        this.icon.setPopupMenu(menu);

        this.externalGUI = new ExternalFrame();

        this.showExternal = new MenuItem("Show External");
        this.showExternal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                externalGUI.load();
            }
        });

        this.hideClient = new MenuItem("Hide Client");
        this.hideClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                XIV.getInstance().getListenerManager().setEnabled(!XIV.getInstance().getListenerManager().isEnabled());
                icon.displayMessage("XIV", String.format("Client is now %s", XIV.getInstance().getListenerManager().isEnabled() ? "Showing" : "Hidden"), TrayIcon.MessageType.INFO);
                hideClient.setLabel(String.format("%s Client", XIV.getInstance().getListenerManager().isEnabled() ? "Hide" : "Show"));
            }
        });

        this.hideTray = new MenuItem("Hide Tray");
        this.hideTray.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                XIV.getInstance().getModManager().find(pw.latematt.xiv.mod.mods.misc.TrayIcon.class).setEnabled(false);
            }
        });

        this.showExternal.setEnabled(true);
        this.hideClient.setEnabled(true);
        this.hideTray.setEnabled(true);
        this.menu.add(this.showExternal);
        this.menu.add(this.hideClient);
        this.menu.add(this.hideTray);
    }

    public void load() throws AWTException {
        SystemTray.getSystemTray().add(icon);
    }

    public void unload() {
        this.externalGUI.unload();

        SystemTray.getSystemTray().remove(icon);
    }
}

class ExternalFrame extends JFrame {

    private final Color background = new Color(238, 238, 238);
    private final Font font = new Font("Verdana", Font.BOLD, 12);

    public ExternalFrame() {
        this.setTitle("XIV External GUI");
        this.setResizable(false);

        this.getContentPane().setLayout(new GridLayout(1, 2));

        this.setFont(font);
        this.setBackground(background);

        JTabbedPane pane = new JTabbedPane();

        pane.setBackground(background);
        pane.setFont(font);

        for(ModType type: ModType.values()) {
            JPanel panel = new JPanel(new GridLayout(0, 2));

            for(Mod mod: XIV.getInstance().getModManager().getContents()) {
                if(mod.getModType() == type) {
                    JCheckBox box = new JCheckBox(mod.getName());
                    box.setSelected(mod.isEnabled());
                    box.setFont(font);
                    box.setBackground(background);

                    box.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            mod.setEnabled(e.getStateChange() == 1);
                        }
                    });
                    panel.add(box, "Center");
                }
            }

            if((XIV.getInstance().getModManager().getContents().size() & 1) != 0) {
                JTextArea area = new JTextArea();
                area.setFocusable(false);
                area.setEditable(false);
                area.setBackground(background);
                area.setFont(font);

                panel.add(area, "Center");
            }

            pane.addTab(type.getName(), panel);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));

        JLabel label = new JLabel("Command Input: ");
        label.setFont(font);
        label.setBackground(background);

        panel.add(label, "Center");

        final JTextField field = new JTextField("");
        field.setEditable(true);
        field.setFocusable(true);
        field.setFont(font);
        field.setBackground(background);

        field.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent key) {
                if(key.getKeyCode() == KeyEvent.VK_ENTER) {
                    XIV.getInstance().getCommandManager().parseCommand(XIV.getInstance().getCommandManager().getPrefix() + field.getText());
                    field.setText("");
                }
            }
            @Override
            public void keyReleased(KeyEvent key) { }
            @Override
            public void keyTyped(KeyEvent key) { }
        });

        panel.add(field, "Center");

        pane.add("Commands", panel);

        this.getContentPane().add(pane, "Center");

        this.pack();
    }

    public void load() {
        this.setVisible(true);
    }

    public void unload() {
        this.setVisible(false);
    }
}