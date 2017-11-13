package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import main.GameMode;

@SuppressWarnings("serial")
public class GameModeDialog extends JDialog {

	private JPanel panel;
	private ButtonGroup group;
	private JRadioButton local, network;
	private JPanel gameModeOptions;
	private JPanel hostnamePanel, portPanel;
	private JLabel hostnameLabel;
	private JLabel portLabel;
	private JTextField hostnameField;
	private JTextField portField;
	private JButton submit;

	private GameMode gameMode;
	private InetSocketAddress address;

	public GameModeDialog() {
		super();
		this.setTitle("Choose game mode");
		this.setLayout(new BorderLayout());

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		group = new ButtonGroup();
		local = new JRadioButton("Local");
		local.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hostnameField.setEnabled(false);
				portField.setEnabled(false);
				hostnameField.setBackground(Color.LIGHT_GRAY);
				portField.setBackground(Color.LIGHT_GRAY);
			}
		});
		network = new JRadioButton("Network");
		network.setSelected(true);
		network.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hostnameField.setEnabled(true);
				portField.setEnabled(true);
				hostnameField.setBackground(Color.WHITE);
				portField.setBackground(Color.WHITE);
			}
		});
		group.add(local);
		group.add(network);

		gameModeOptions = new JPanel();
		gameModeOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
		gameModeOptions.setLayout(new BoxLayout(gameModeOptions, BoxLayout.X_AXIS));
		gameModeOptions.add(local);
		gameModeOptions.add(Box.createRigidArea(new Dimension(10, 0)));
		gameModeOptions.add(network);
		panel.add(gameModeOptions);

		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(new JSeparator());
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		hostnamePanel = new JPanel();
		hostnamePanel.setLayout(new BoxLayout(hostnamePanel, BoxLayout.X_AXIS));
		hostnameLabel = new JLabel("Host");
		hostnameField = new JTextField("", 20);
		hostnamePanel.add(hostnameLabel);
		hostnamePanel.add(Box.createRigidArea(new Dimension(40, 0)));
		hostnamePanel.add(hostnameField);
		panel.add(hostnamePanel);

		portPanel = new JPanel();
		portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
		portLabel = new JLabel("Port");
		portField = new JTextField("", 5);
		portPanel.add(portLabel);
		portPanel.add(Box.createRigidArea(new Dimension(42, 0)));
		portPanel.add(portField);
		panel.add(portPanel);

		submit = new JButton("Go!");
		submit.setAlignmentX(Component.CENTER_ALIGNMENT);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameMode = local.isSelected() ? GameMode.LOCAL : GameMode.NETWORK;
				if (gameMode == GameMode.NETWORK) {
					int port;
					try {
						port = Integer.parseInt(portField.getText());
						address = new InetSocketAddress(hostnameField.getText(), port);
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(new JFrame(), "Invalid port number.");
						return;
					}
				}
				finish();
			}
		});
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(submit);

		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(panel, BorderLayout.CENTER);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);
	}

	private void finish() {
		this.setVisible(false);
		this.dispose();
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public InetSocketAddress getAddress() {
		return address;
	}
}
