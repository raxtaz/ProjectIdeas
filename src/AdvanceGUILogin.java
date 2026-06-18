import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class AdvanceGUILogin
{
private static int attempts = 0;
private static final int MAX_ATTEMPTS = 3;
private static final int LOCKOUT_DURATION_MINUTES = 5;
private static LocalDateTime lockoutTime = null;
private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

public static void main(String[] args)
{
	SwingUtilities.invokeLater(() ->
	{
		// Initialize user database with hashed passwords
		HashMap<String, User> userDatabase = initializeUsers();

		// Create and configure the main window
		JFrame frame = createMainFrame();

		// Create the login panel
		JPanel mainPanel = createLoginPanel(frame, userDatabase);
		frame.add(mainPanel);

		frame.setVisible(true);
	});
}

/**
 * Initialize user database with example users
 */
private static HashMap<String, User> initializeUsers()
{
	HashMap<String, User> users = new HashMap<>();
	users.put("admin", new User("admin", hashPassword("password123"), "admin@company.com", true));
	users.put("user99", new User("user99", hashPassword("helloWorld"), "user99@company.com", false));
	users.put("john.doe", new User("john.doe", hashPassword("SecurePass2024!"), "john.doe@company.com", false));
	return users;
}

/**
 * Create and configure the main JFrame
 */
private static JFrame createMainFrame()
{
	JFrame frame = new JFrame("Advanced Security Login System");
	frame.setSize(500, 700);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLocationRelativeTo(null);
	frame.setResizable(false);

	// Add window listener for session cleanup
	frame.addWindowListener(new WindowAdapter()
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			System.out.println("[SESSION] Window closed at " + getCurrentTimestamp());
			System.exit(0);
		}
	});

	return frame;
}

/**
 * Create the main login panel with all components
 */
private static JPanel createLoginPanel(JFrame frame, HashMap<String, User> userDatabase)
{
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	mainPanel.setBackground(new Color(240, 245, 250));
	mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

	// Header
	JPanel headerPanel = createHeaderPanel();
	mainPanel.add(headerPanel);
	mainPanel.add(Box.createVerticalStrut(20));

	// Login form panel
	JPanel formPanel = new JPanel();
	formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
	formPanel.setOpaque(false);

	// Username field
	JLabel userLabel = new JLabel("Username or Email");
	userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
	userLabel.setForeground(new Color(50, 50, 50));
	formPanel.add(userLabel);
	formPanel.add(Box.createVerticalStrut(5));

	JTextField userText = createStyledTextField("Enter username or email");
	formPanel.add(userText);
	formPanel.add(Box.createVerticalStrut(15));

	// Password field
	JLabel passwordLabel = new JLabel("Password");
	passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
	passwordLabel.setForeground(new Color(50, 50, 50));
	formPanel.add(passwordLabel);
	formPanel.add(Box.createVerticalStrut(5));

	JPasswordField passwordText = new JPasswordField(20);
	stylePasswordField(passwordText);
	formPanel.add(passwordText);
	formPanel.add(Box.createVerticalStrut(10));

	// Caps Lock indicator
	JLabel capsLockLabel = new JLabel("Caps Lock is ON");
	capsLockLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
	capsLockLabel.setForeground(new Color(255, 140, 0));
	capsLockLabel.setVisible(false);
	formPanel.add(capsLockLabel);

	passwordText.addKeyListener(new KeyAdapter()
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			capsLockLabel.setVisible(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
		}
	});
	formPanel.add(Box.createVerticalStrut(15));

	// Remember me checkbox
	JCheckBox rememberMeCheckBox = new JCheckBox("Remember this device");
	rememberMeCheckBox.setOpaque(false);
	rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	formPanel.add(rememberMeCheckBox);
	formPanel.add(Box.createVerticalStrut(20));

	// Login button
	JButton loginButton = createStyledButton("Login", new Color(25, 118, 210));

	// Status label
	JLabel statusLabel = new JLabel(" ");
	statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	statusLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

	// Attempt counter
	JLabel attemptLabel = new JLabel();
	attemptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
	attemptLabel.setForeground(new Color(150, 150, 150));
	updateAttemptLabel(attemptLabel);

	formPanel.add(loginButton);
	formPanel.add(Box.createVerticalStrut(10));
	formPanel.add(statusLabel);
	formPanel.add(Box.createVerticalStrut(5));
	formPanel.add(attemptLabel);

	mainPanel.add(formPanel);
	mainPanel.add(Box.createVerticalStrut(20));

	// Bottom panel with links
	JPanel bottomPanel = createBottomPanel();
	mainPanel.add(bottomPanel);

	// Login button action
	loginButton.addActionListener(e ->
	{
		if(isLockedOut())
		{
			statusLabel.setText("Account locked. Try again in " +
					getMinutesUntilUnlock() + " minutes.");
			statusLabel.setForeground(new Color(211, 47, 47));
			return;
		}

		String username = userText.getText().trim();
		String password = new String(passwordText.getPassword());

		// Input validation
		String validationError = validateInputs(username, password);
		if(validationError != null)
		{
			statusLabel.setText(validationError);
			statusLabel.setForeground(new Color(211, 47, 47));
			return;
		}

		// Authenticate user
		User user = authenticateUser(userDatabase, username, password);

		if(user != null)
		{
			attempts = 0;
			statusLabel.setText("✓ Login successful! Loading dashboard...");
			statusLabel.setForeground(new Color(56, 142, 60));

			loginButton.setEnabled(false);
			userText.setEnabled(false);
			passwordText.setEnabled(false);
			rememberMeCheckBox.setEnabled(false);

			// Simulate dashboard loading
			javax.swing.Timer timer = new javax.swing.Timer(1500, event ->
			{
				openDashboard(frame, user);
			});
			timer.setRepeats(false);
			timer.start();

			logActivity("LOGIN_SUCCESS", username);
		}
		else
		{
			attempts++;
			int attemptsLeft = MAX_ATTEMPTS - attempts;

			if(attemptsLeft > 0)
			{
				statusLabel.setText("✗ Invalid credentials. " + attemptsLeft + " attempt(s) left.");
				statusLabel.setForeground(new Color(211, 47, 47));
			}
			else
			{
				lockoutTime = LocalDateTime.now();
				statusLabel.setText("✗ Max attempts reached. Account locked for 5 minutes.");
				statusLabel.setForeground(new Color(211, 47, 47));

				loginButton.setEnabled(false);
				userText.setEnabled(false);
				passwordText.setEnabled(false);
				rememberMeCheckBox.setEnabled(false);
			}

			updateAttemptLabel(attemptLabel);
			passwordText.setText("");
			logActivity("LOGIN_FAILURE", username);
		}
	});

	// Clear status on input
	KeyAdapter clearStatusAdapter = new KeyAdapter()
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			statusLabel.setText(" ");
		}
	};
	userText.addKeyListener(clearStatusAdapter);
	passwordText.addKeyListener(clearStatusAdapter);

	return mainPanel;
}

/**
 * Create header panel with title and security indicator
 */
private static JPanel createHeaderPanel()
{
	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setOpaque(false);

	JLabel titleLabel = new JLabel("Secure Login");
	titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
	titleLabel.setForeground(new Color(25, 118, 210));
	titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	panel.add(titleLabel);

	JLabel subtitleLabel = new JLabel("Military-grade encryption | Two-Factor Authentication");
	subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
	subtitleLabel.setForeground(new Color(120, 120, 120));
	subtitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	panel.add(subtitleLabel);

	return panel;
}

/**
 * Create bottom panel with links
 */
private static JPanel createBottomPanel()
{
	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	panel.setOpaque(false);

	JLabel forgotPassword = createClickableLabel("Forgot Password?");
	JLabel signup = createClickableLabel("Sign Up");

	forgotPassword.addMouseListener(new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			showDialog("Password Recovery", "Password reset link sent to your email.");
		}
	});

	signup.addMouseListener(new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			showDialog("Sign Up", "Redirecting to registration page...");
		}
	});

	panel.add(forgotPassword);
	panel.add(Box.createHorizontalGlue());
	panel.add(signup);

	return panel;
}

/**
 * Create a styled text field
 */
private static JTextField createStyledTextField(String placeholder)
{
	JTextField field = new JTextField(20);
	field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
	field.setPreferredSize(new Dimension(0, 40));
	field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

	// Store placeholder state
	boolean[] isPlaceholder = {true};

	// Set initial placeholder text
	field.setText(placeholder);
	field.setForeground(new Color(150, 150, 150));

	field.addFocusListener(new FocusAdapter()
	{
		@Override
		public void focusGained(FocusEvent e)
		{
			// Clear placeholder on focus
			if(isPlaceholder[0])
			{
				field.setText("");
				field.setForeground(new Color(0, 0, 0));
				isPlaceholder[0] = false;
			}
			field.setBorder(new LineBorder(new Color(25, 118, 210), 2, true));
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			// Restore placeholder if field is empty
			if(field.getText().isEmpty())
			{
				field.setText(placeholder);
				field.setForeground(new Color(150, 150, 150));
				isPlaceholder[0] = true;
			}
			field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		}
	});

	return field;
}

/**
 * Style password field
 */
private static void stylePasswordField(JPasswordField field)
{
	field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
	field.setPreferredSize(new Dimension(0, 40));
	field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

	field.addFocusListener(new FocusAdapter()
	{
		@Override
		public void focusGained(FocusEvent e)
		{
			field.setBorder(new LineBorder(new Color(25, 118, 210), 2, true));
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		}
	});
}

/**
 * Create a styled button
 */
private static JButton createStyledButton(String text, Color color)
{
	JButton button = new JButton(text);
	button.setFont(new Font("Segoe UI", Font.BOLD, 13));
	button.setForeground(Color.WHITE);
	button.setBackground(color);
	button.setBorder(new EmptyBorder(10, 20, 10, 20));
	button.setOpaque(true);
	button.setFocusPainted(false);
	button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	button.setPreferredSize(new Dimension(0, 45));
	button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

	button.addMouseListener(new MouseAdapter()
	{
		@Override
		public void mouseEntered(MouseEvent e)
		{
			button.setBackground(color.darker());
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			button.setBackground(color);
		}
	});

	return button;
}

/**
 * Create a clickable label (for links)
 */
private static JLabel createClickableLabel(String text)
{
	JLabel label = new JLabel(text);
	label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	label.setForeground(new Color(25, 118, 210));
	label.setCursor(new Cursor(Cursor.HAND_CURSOR));

	label.addMouseListener(new MouseAdapter()
	{
		@Override
		public void mouseEntered(MouseEvent e)
		{
			label.setForeground(new Color(1, 87, 155));
			label.setText("<html><u>" + text + "</u></html>");
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			label.setForeground(new Color(25, 118, 210));
			label.setText(text);
		}
	});

	return label;
}

/**
 * Validate user inputs
 */
private static String validateInputs(String username, String password)
{
	if(username.isEmpty())
	{
		return "Username cannot be empty.";
	}
	if(username.length() < 3)
	{
		return "Username must be at least 3 characters.";
	}
	if(password.isEmpty())
	{
		return "Password cannot be empty.";
	}
	if(password.length() < 6)
	{
		return "Password must be at least 6 characters.";
	}
	return null;
}

/**
 * Authenticate user against database
 */
private static User authenticateUser(HashMap<String, User> db, String username, String password)
{
	User user = db.get(username);

	// Also check if email is used instead of username
	if(user == null)
	{
		for(User u : db.values())
		{
			if(u.email().equalsIgnoreCase(username))
			{
				user = u;
				break;
			}
		}
	}

	if(user != null && user.passwordHash().equals(hashPassword(password)))
	{
		return user;
	}

	return null;
}

/**
 * Hash password using SHA-256
 */
private static String hashPassword(String password)
{
	try
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(password.getBytes());
		StringBuilder hexString = new StringBuilder();

		for(byte b : hash)
		{
			String hex = Integer.toHexString(0xff & b);
			if(hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}
	catch(Exception e)
	{
		throw new RuntimeException("Error hashing password", e);
	}
}

/**
 * Check if account is locked
 */
private static boolean isLockedOut()
{
	if(lockoutTime == null) return false;

	LocalDateTime now = LocalDateTime.now();
	long minutesElapsed = java.time.temporal.ChronoUnit.MINUTES.between(lockoutTime, now);

	return minutesElapsed < LOCKOUT_DURATION_MINUTES;
}

/**
 * Get minutes until unlock
 */
private static int getMinutesUntilUnlock()
{
	if(lockoutTime == null) return 0;

	LocalDateTime now = LocalDateTime.now();
	long minutesElapsed = java.time.temporal.ChronoUnit.MINUTES.between(lockoutTime, now);
	int remaining = (int) (LOCKOUT_DURATION_MINUTES - minutesElapsed);

	return Math.max(0, remaining);
}

/**
 * Update attempt counter display
 */
private static void updateAttemptLabel(JLabel label)
{
	int remaining = MAX_ATTEMPTS - attempts;
	if(remaining > 0)
	{
		label.setText("Attempts remaining: " + remaining);
		label.setForeground(new Color(200, 150, 0));
	}
}

/**
 * Log security events
 */
private static void logActivity(String eventType, String username)
{
	String timestamp = getCurrentTimestamp();
	System.out.println("[" + eventType + "] User: " + username + " | Time: " + timestamp);
}

/**
 * Get current timestamp
 */
private static String getCurrentTimestamp()
{
	return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
}

/**
 * Show simple dialog
 */
private static void showDialog(String title, String message)
{
	JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
}

/**
 * Open user dashboard after successful login
 */
private static void openDashboard(JFrame loginFrame, User user)
{
	loginFrame.dispose();

	JFrame dashboardFrame = new JFrame("Dashboard - " + user.username());
	dashboardFrame.setSize(700, 500);
	dashboardFrame.setLocationRelativeTo(null);
	dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JPanel panel = new JPanel();
	panel.setBackground(new Color(240, 245, 250));
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(new EmptyBorder(40, 40, 40, 40));

	JLabel welcomeLabel = new JLabel("Welcome, " + user.username() + "!");
	welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
	welcomeLabel.setForeground(new Color(25, 118, 210));
	panel.add(welcomeLabel);

	panel.add(Box.createVerticalStrut(20));

	JTextArea infoArea = new JTextArea();
	infoArea.setEditable(false);
	infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	infoArea.setBackground(new Color(255, 255, 255));
	infoArea.setBorder(new LineBorder(new Color(200, 200, 200)));
	infoArea.setText(
			"User Information:\n\n" +
					"Username: " + user.username() + "\n" +
					"Email: " + user.email() + "\n" +
					"Admin: " + (user.isAdmin() ? "Yes" : "No") + "\n" +
					"Login Time: " + getCurrentTimestamp() + "\n" +
					"Session ID: " + generateSessionId() + "\n\n" +
					"Security Status: ✓ All systems secure"
	);
	panel.add(infoArea);

	panel.add(Box.createVerticalStrut(20));

	JButton logoutButton = createStyledButton("Logout", new Color(25, 118, 210));
	logoutButton.addActionListener(e ->
	{
		logActivity("LOGOUT", user.username());
		dashboardFrame.dispose();
		main(new String[]{});
	});
	panel.add(logoutButton);

	dashboardFrame.add(panel);
	dashboardFrame.setVisible(true);
}

/**
 * Generate a unique session ID
 */
private static String generateSessionId()
{
	SecureRandom random = new SecureRandom();
	byte[] bytes = new byte[16];
	random.nextBytes(bytes);
	StringBuilder sb = new StringBuilder();
	for(byte b : bytes)
	{
		sb.append(String.format("%02x", b));
	}
	return sb.toString();
}

/**
 * User class to store user information
 */
record User(String username, String passwordHash, String email, boolean isAdmin)
{


}
}