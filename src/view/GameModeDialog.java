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

import main.Constants;
import main.GameMode;

@SuppressWarnings("serial")
public class GameModeDialog extends JDialog {

	private JPanel panel;
	private ButtonGroup group;
	private JRadioButton local, network;
	private JPanel gameModeOptions;
	private JPanel hostnamePanel, portPanel, usernamePanel;
	private JLabel hostnameLabel;
	private JLabel portLabel;
	private JLabel usernameLabel;
	private JTextField hostnameField;
	private JTextField portField;
	private JTextField usernameField;
	private JButton submit;

	private GameMode gameMode = null;
	private InetSocketAddress address = null;
	private String username = null;
	private boolean okClicked = false;

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
				usernameField.setEnabled(false);
				hostnameField.setBackground(Color.LIGHT_GRAY);
				portField.setBackground(Color.LIGHT_GRAY);
				usernameField.setBackground(Color.LIGHT_GRAY);
			}
		});
		network = new JRadioButton("Network");
		network.setSelected(true);
		network.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hostnameField.setEnabled(true);
				portField.setEnabled(true);
				usernameField.setEnabled(true);
				hostnameField.setBackground(Color.WHITE);
				portField.setBackground(Color.WHITE);
				usernameField.setBackground(Color.WHITE);
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
		hostnameLabel = new JLabel("Host ");
		hostnameField = new JTextField("", 20);
		hostnamePanel.add(hostnameLabel);
		hostnamePanel.add(Box.createRigidArea(new Dimension(60, 0)));
		hostnamePanel.add(hostnameField);
		panel.add(hostnamePanel);

		portPanel = new JPanel();
		portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
		portLabel = new JLabel("Port ");
		portField = new JTextField(Integer.toString(Constants.SERVER_PORT), 5);
		portPanel.add(portLabel);
		portPanel.add(Box.createRigidArea(new Dimension(62, 0)));
		portPanel.add(portField);
		panel.add(portPanel);
		
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
		usernameLabel = new JLabel("Username ");
		usernameField = new JTextField("", 5);
		usernamePanel.add(usernameLabel);
		usernamePanel.add(Box.createRigidArea(new Dimension(21, 0)));
		usernamePanel.add(usernameField);

		panel.add(usernamePanel);
		
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
						JOptionPane.showMessageDialog(null, "Invalid port number.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					username = usernameField.getText();
					int usernameLength = username.length();
					if (usernameLength < Constants.USERNAME_MIN_CHARS || usernameLength > Constants.USERNAME_MAX_CHARS) {
						JOptionPane.showMessageDialog(null,
								"Username must be between " + Constants.USERNAME_MIN_CHARS + " and " + Constants.USERNAME_MAX_CHARS + " characters long.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				okClicked = true;
				finish();
			}
		});
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(submit);

		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(panel, BorderLayout.CENTER);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.pack();
		this.setLocationRelativeTo(null);
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

	public String getUsername() {
		return username;
	}

	public boolean isOkClicked() {
		return okClicked;
	}
}
