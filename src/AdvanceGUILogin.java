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
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

public class AdvanceGUILogin
{
	// UI constants
	private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
	private static final Color BG_COLOR = new Color(240, 245, 250);
	private static final Color MUTED_COLOR = new Color(120, 120, 120);
	private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	private static final Border NORMAL_BORDER = new LineBorder(new Color(200, 200, 200), 1, true);
	private static final Border FOCUS_BORDER = new LineBorder(PRIMARY_COLOR, 2, true);

	// Password hashing - reuse MessageDigest via ThreadLocal to avoid repeated instantiation
	private static final ThreadLocal<MessageDigest> DIGEST = ThreadLocal.withInitial(() ->
	{
		try
		{
			return MessageDigest.getInstance("SHA-256");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	});

	// Email lookup index (lowercase keys)
	private static final HashMap<String, User> emailIndex = new HashMap<>();

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

		// populate email index for fast email lookups
		emailIndex.clear();
		for(User u : users.values())
		{
			emailIndex.put(u.email().toLowerCase(), u);
		}

		return users;
	}

	/**
	 * Create and configure the main JFrame
	 */
	private static JFrame createMainFrame()
	{
		// Set a modern look and feel (Nimbus) if available
		try
		{
			for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch(Exception e)
		{
			// ignore and continue with default L&F
		}

		JFrame frame = new JFrame("Secure Login");
		frame.setSize(520, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		// set app icon
		frame.setIconImage(createAppIconImage());

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
		JPanel mainPanel = new GradientPanel();
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

		RoundedPanel card = new RoundedPanel(12, new Color(255, 255, 255));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.setBorder(new EmptyBorder(20, 20, 20, 20));
		card.add(formPanel);
		mainPanel.add(card);
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
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		ImageIcon icon = new ImageIcon(createAppIconImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH));
		JLabel iconLabel = new JLabel(icon);
		iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
		panel.add(iconLabel);

		JPanel texts = new JPanel();
		texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
		texts.setOpaque(false);

		JLabel titleLabel = new JLabel("Secure Login");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
		titleLabel.setForeground(new Color(25, 118, 210));
		texts.add(titleLabel);

		JLabel subtitleLabel = new JLabel("Secure access protected by strong encryption");
		subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
		subtitleLabel.setForeground(new Color(120, 120, 120));
		texts.add(subtitleLabel);

		panel.add(texts);

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
		field.setFont(UI_FONT);
		field.setBorder(NORMAL_BORDER);
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
				if(isPlaceholder[0])
				{
					field.setText("");
					field.setForeground(Color.BLACK);
					isPlaceholder[0] = false;
				}
				field.setBorder(FOCUS_BORDER);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if(field.getText().isEmpty())
				{
					field.setText(placeholder);
					field.setForeground(new Color(150, 150, 150));
					isPlaceholder[0] = true;
				}
				field.setBorder(NORMAL_BORDER);
			}
		});

		return field;
	}

	/**
	 * Style password field
	 */
	private static void stylePasswordField(JPasswordField field)
	{
		field.setFont(UI_FONT);
		field.setBorder(NORMAL_BORDER);
		field.setPreferredSize(new Dimension(0, 40));
		field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		field.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				field.setBorder(FOCUS_BORDER);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				field.setBorder(NORMAL_BORDER);
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
		// direct username match
		User user = db.get(username);

		// if not username, try email lookup via index
		if(user == null && username.contains("@"))
		{
			user = emailIndex.get(username.toLowerCase());
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
			MessageDigest md = DIGEST.get();
			md.reset();
			byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder(hash.length * 2);
			for(byte b : hash)
			{
				hex.append(String.format("%02x", b));
			}
			return hex.toString();
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
	 * Utility: create app icon image
	 */
	private static Image createAppIconImage()
	{
		BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(25, 118, 210));
		g.fillOval(0, 0, 64, 64);
		g.setColor(Color.WHITE);
		// simple lock shape
		g.fillRect(26, 26, 12, 14);
		g.fillOval(24, 18, 16, 16);
		g.dispose();
		return img;
	}

	/**
	 * Gradient background panel
	 */
	static class GradientPanel extends JPanel
	{
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			Color c1 = new Color(245, 248, 251);
			Color c2 = new Color(230, 240, 250);
			Paint old = g2.getPaint();
			g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
			g2.fillRect(0, 0, w, h);
			g2.setPaint(old);
		}
	}

	/**
	 * Rounded white card panel
	 */
	static class RoundedPanel extends JPanel
	{
		private final int radius;
		private final Color backgroundColor;

		RoundedPanel(int radius, Color bg)
		{
			this.radius = radius;
			this.backgroundColor = bg;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			// shadow
			g2.setColor(new Color(0, 0, 0, 20));
			g2.fillRoundRect(5, 5, w - 10, h - 10, radius + 6, radius + 6);
			// background
			g2.setColor(backgroundColor);
			g2.fillRoundRect(0, 0, w - 10, h - 10, radius, radius);
			g2.dispose();
			super.paintComponent(g);
		}
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