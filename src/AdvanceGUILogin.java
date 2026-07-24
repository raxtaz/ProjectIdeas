import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.util.Map;
import java.util.LinkedHashMap;

public class AdvanceGUILogin
{
	// Dark Theme - Research & Analysis Wing Portal
	private static final Color PRIMARY_COLOR = new Color(230, 126, 34); // Orange
	private static final Color PRIMARY_LIGHT = new Color(242, 142, 43);
	private static final Color SECONDARY_COLOR = new Color(155, 89, 182); // Purple
	private static final Color BG_DARK = new Color(25, 35, 50); // Dark background
	private static final Color CARD_COLOR = new Color(45, 55, 70); // Dark card
	private static final Color MUTED_COLOR = new Color(150, 150, 150);
	private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
	private static final Color ERROR_COLOR = new Color(231, 76, 60);
	private static final Color WARN_COLOR = new Color(241, 196, 15);
	private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
	private static final Color TEXT_SECONDARY = new Color(189, 195, 199);
	private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
	private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
	private static final Border NORMAL_BORDER = new ModernBorder(new Color(80, 100, 120), 1);
	private static final Border FOCUS_BORDER = new ModernBorder(PRIMARY_COLOR, 2);

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
		}

		JFrame frame = new JFrame("Research & Analysis Wing - Secure Access");
		frame.setSize(700, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setIconImage(createAppIconImage());

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.out.println("[SESSION] Portal closed at " + getCurrentTimestamp());
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
		JPanel mainPanel = new GradientPanel(BG_DARK, new Color(35, 50, 70));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

		// Header with enhanced styling
		JPanel headerPanel = createHeaderPanel();
		mainPanel.add(headerPanel);
		mainPanel.add(Box.createVerticalStrut(30));

		// Lockout Timer Label (will be shown when locked)
		JLabel timerLabel = new JLabel(" ");
		timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		timerLabel.setForeground(ERROR_COLOR);
		timerLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		timerLabel.setVisible(false);

		// Timer for lockout countdown
		final javax.swing.Timer[] lockoutTimer = new javax.swing.Timer[1];
		final int[] secondsRemaining = {0};

		// Form panel
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setOpaque(false);

		// Username field
		JLabel userLabel = new JLabel("Username or Email");
		userLabel.setFont(LABEL_FONT);
		userLabel.setForeground(TEXT_PRIMARY);
		formPanel.add(userLabel);
		formPanel.add(Box.createVerticalStrut(8));

		JTextField userText = createStyledTextField("Enter your username or email", 22);
		formPanel.add(userText);
		formPanel.add(Box.createVerticalStrut(18));

		// Password field
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setFont(LABEL_FONT);
		passwordLabel.setForeground(TEXT_PRIMARY);
		formPanel.add(passwordLabel);
		formPanel.add(Box.createVerticalStrut(8));

		JPasswordField passwordText = new JPasswordField(20);
		stylePasswordField(passwordText);
		formPanel.add(passwordText);
		formPanel.add(Box.createVerticalStrut(12));

		// Caps Lock indicator
		JLabel capsLockLabel = new JLabel("⚠ Caps Lock is ON");
		capsLockLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
		capsLockLabel.setForeground(WARN_COLOR);
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
		JCheckBox rememberMeCheckBox = new JCheckBox("Keep me signed in");
		rememberMeCheckBox.setOpaque(false);
		rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		rememberMeCheckBox.setForeground(TEXT_SECONDARY);
		formPanel.add(rememberMeCheckBox);
		formPanel.add(Box.createVerticalStrut(25));

		// Login button
		JButton loginButton = createStyledButton("🔐 ACCESS PORTAL", PRIMARY_COLOR, PRIMARY_LIGHT);

		// Status label
		JLabel statusLabel = new JLabel(" ");
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		statusLabel.setForeground(TEXT_SECONDARY);
		statusLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

		// Attempt counter
		JLabel attemptLabel = new JLabel();
		attemptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		attemptLabel.setForeground(MUTED_COLOR);
		updateAttemptLabel(attemptLabel);

		formPanel.add(loginButton);
		formPanel.add(Box.createVerticalStrut(12));
		formPanel.add(statusLabel);
		formPanel.add(Box.createVerticalStrut(8));
		formPanel.add(attemptLabel);

		RoundedPanel card = new RoundedPanel(16, CARD_COLOR, 8);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.setBorder(new EmptyBorder(25, 25, 25, 25));
		card.add(formPanel);
		mainPanel.add(card);
		mainPanel.add(Box.createVerticalStrut(15));
		mainPanel.add(timerLabel);
		mainPanel.add(Box.createVerticalStrut(10));

		// Bottom panel with links
		JPanel bottomPanel = createBottomPanel();
		mainPanel.add(bottomPanel);

		// Login button action
		loginButton.addActionListener(e ->
		{
			if(isLockedOut())
			{
				int minutesLeft = getMinutesUntilUnlock();
				int secondsLeft = getSecondsUntilUnlock();
				statusLabel.setText("🔒 Account locked. Try again in " + minutesLeft + "m " + secondsLeft + "s");
				statusLabel.setForeground(ERROR_COLOR);
				timerLabel.setVisible(true);

				// Start countdown timer
				if(lockoutTimer[0] == null)
				{
					secondsRemaining[0] = minutesLeft * 60 + secondsLeft;
					lockoutTimer[0] = new javax.swing.Timer(1000, evt ->
					{
						secondsRemaining[0]--;
						int mins = secondsRemaining[0] / 60;
						int secs = secondsRemaining[0] % 60;
						timerLabel.setText(String.format("⏱️ Lockout Timer: %02d:%02d remaining", mins, secs));
						if(secondsRemaining[0] <= 0)
						{
							lockoutTimer[0].stop();
							lockoutTimer[0] = null;
							timerLabel.setVisible(false);
							attempts = 0;
							statusLabel.setText(" ");
							loginButton.setEnabled(true);
							userText.setEnabled(true);
							passwordText.setEnabled(true);
							rememberMeCheckBox.setEnabled(true);
						}
					});
					lockoutTimer[0].start();
				}
				return;
			}

			String username = userText.getText().trim();
			String password = new String(passwordText.getPassword());

			String validationError = validateInputs(username, password);
			if(validationError != null)
			{
				statusLabel.setText("✗ " + validationError);
				statusLabel.setForeground(ERROR_COLOR);
				return;
			}

			User user = authenticateUser(userDatabase, username, password);

			if(user != null)
			{
				attempts = 0;
				statusLabel.setText("✓ Access granted! Loading weaponry inventory...");
				statusLabel.setForeground(SUCCESS_COLOR);

				loginButton.setEnabled(false);
				userText.setEnabled(false);
				passwordText.setEnabled(false);
				rememberMeCheckBox.setEnabled(false);

				javax.swing.Timer timer = new javax.swing.Timer(1500, event ->
				{
					openInventoryDashboard(frame, user);
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
					statusLabel.setText("✗ Invalid credentials. " + attemptsLeft + " attempt(s) remaining.");
					statusLabel.setForeground(ERROR_COLOR);
				}
				else
				{
					lockoutTime = LocalDateTime.now();
					statusLabel.setText("✗ Max attempts exceeded. Account locked for 5 minutes.");
					statusLabel.setForeground(ERROR_COLOR);

					loginButton.setEnabled(false);
					userText.setEnabled(false);
					passwordText.setEnabled(false);
					rememberMeCheckBox.setEnabled(false);

					timerLabel.setVisible(true);
					int totalSeconds = LOCKOUT_DURATION_MINUTES * 60;
					timerLabel.setText(String.format("⏱️ Lockout Timer: %02d:%02d remaining", LOCKOUT_DURATION_MINUTES, 0));
				}

				updateAttemptLabel(attemptLabel);
				passwordText.setText("");
				logActivity("LOGIN_FAILURE", username);
			}
		});

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

		ImageIcon icon = new ImageIcon(createAppIconImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH));
		JLabel iconLabel = new JLabel(icon);
		iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
		panel.add(iconLabel);

		JPanel texts = new JPanel();
		texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
		texts.setOpaque(false);

		JLabel titleLabel = new JLabel("Research & Analysis Wing");
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setForeground(PRIMARY_COLOR);
		texts.add(titleLabel);

		JLabel subtitleLabel = new JLabel("Weaponry & Equipment Management System");
		subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		subtitleLabel.setForeground(TEXT_SECONDARY);
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
	private static JTextField createStyledTextField(String placeholder, int columns)
	{
		JTextField field = new JTextField(columns);
		field.setFont(UI_FONT);
		field.setBorder(NORMAL_BORDER);
		field.setPreferredSize(new Dimension(0, 45));
		field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
		field.setBackground(new Color(60, 75, 95));
		field.setForeground(TEXT_PRIMARY);
		field.setCaretColor(PRIMARY_COLOR);

		boolean[] isPlaceholder = {true};

		field.setText(placeholder);
		field.setForeground(TEXT_SECONDARY);

		field.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if(isPlaceholder[0])
				{
					field.setText("");
					field.setForeground(TEXT_PRIMARY);
					field.setBackground(new Color(70, 90, 110));
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
					field.setForeground(TEXT_SECONDARY);
					field.setBackground(new Color(60, 75, 95));
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
		field.setPreferredSize(new Dimension(0, 45));
		field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
		field.setBackground(new Color(60, 75, 95));
		field.setForeground(TEXT_PRIMARY);
		field.setCaretColor(PRIMARY_COLOR);

		field.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				field.setBorder(FOCUS_BORDER);
				field.setBackground(new Color(70, 90, 110));
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				field.setBorder(NORMAL_BORDER);
				field.setBackground(new Color(60, 75, 95));
			}
		});
	}

	/**
	 * Create a styled button
	 */
	private static JButton createStyledButton(String text, Color baseColor, Color hoverColor)
	{
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				if(getModel().isPressed()) {
					g2.setColor(baseColor.darker());
				} else if(getModel().isArmed() || getModel().isSelected()) {
					g2.setColor(hoverColor);
				} else if(!isEnabled()) {
					g2.setColor(new Color(100, 100, 100));
				} else {
					g2.setColor(baseColor);
				}
				
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setForeground(TEXT_PRIMARY);
		button.setBorder(new EmptyBorder(12, 20, 12, 20));
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(0, 48));
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
		button.setContentAreaFilled(false);
		button.setOpaque(false);

		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

	private static int getSecondsUntilUnlock()
	{
		if(lockoutTime == null) return 0;

		LocalDateTime now = LocalDateTime.now();
		long secondsElapsed = java.time.temporal.ChronoUnit.SECONDS.between(lockoutTime, now);
		int remainingSeconds = (int) ((LOCKOUT_DURATION_MINUTES * 60) - secondsElapsed);

		return Math.max(0, remainingSeconds % 60);
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
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Gradient background (Orange → Red)
		GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, 64, 64, new Color(200, 80, 0));
		g.setPaint(gp);
		g.fillOval(2, 2, 60, 60);
		
		// Outer glow effect
		g.setColor(new Color(255, 255, 255, 100));
		g.setStroke(new BasicStroke(2));
		g.drawOval(4, 4, 56, 56);
		
		// Research & Analysis Wing Shield shape
		g.setColor(Color.WHITE);
		
		// Shield background
		g.fillPolygon(new int[]{32, 22, 20, 20, 32, 44, 44, 42}, new int[]{10, 20, 30, 42, 50, 42, 30, 20}, 8);
		
		// Inner design - weapons symbol
		g.setColor(PRIMARY_COLOR);
		g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		// Crosshairs
		g.drawLine(32, 18, 32, 48);
		g.drawLine(16, 32, 48, 32);
		
		// Center circle
		g.fillOval(28, 28, 8, 8);
		
		g.dispose();
		return img;
	}

	/**
	 * Gradient background panel with custom colors
	 */
	static class GradientPanel extends JPanel
	{
		private final Color color1;
		private final Color color2;

		GradientPanel(Color c1, Color c2)
		{
			this.color1 = c1;
			this.color2 = c2;
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			Paint old = g2.getPaint();
			g2.setPaint(new GradientPaint(0, 0, color1, 0, h, color2));
			g2.fillRect(0, 0, w, h);
			g2.setPaint(old);
		}
	}

	/**
	 * Rounded white card panel with shadow effect
	 */
	static class RoundedPanel extends JPanel
	{
		private final int radius;
		private final Color backgroundColor;
		private final int shadowSize;

		RoundedPanel(int radius, Color bg, int shadowSize)
		{
			this.radius = radius;
			this.backgroundColor = bg;
			this.shadowSize = shadowSize;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			
			// Multi-layer shadow for depth
			for(int i = shadowSize; i > 0; i--)
			{
				g2.setColor(new Color(0, 0, 0, 5));
				g2.fillRoundRect(i, i, w - i * 2 - 1, h - i * 2 - 1, radius + 2, radius + 2);
			}
			
			// Main background
			g2.setColor(backgroundColor);
			g2.fillRoundRect(0, 0, w - shadowSize - 1, h - shadowSize - 1, radius, radius);
			
			// Subtle border
			g2.setColor(new Color(230, 235, 240));
			g2.setStroke(new BasicStroke(1));
			g2.drawRoundRect(0, 0, w - shadowSize - 1, h - shadowSize - 1, radius, radius);
			
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
	private static void openInventoryDashboard(JFrame loginFrame, User user)
	{
		loginFrame.dispose();

		JFrame dashboardFrame = new JFrame("Research & Analysis Wing Weaponry Management - " + user.username());
		dashboardFrame.setSize(1200, 900);
		dashboardFrame.setLocationRelativeTo(null);
		dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new GradientPanel(BG_DARK, new Color(45, 60, 85));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		// Header
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setOpaque(false);

		JLabel welcomeLabel = new JLabel("🎖️ WEAPONRY INVENTORY SYSTEM");
		welcomeLabel.setFont(TITLE_FONT);
		welcomeLabel.setForeground(PRIMARY_COLOR);
		headerPanel.add(welcomeLabel);
		headerPanel.add(Box.createHorizontalGlue());

		JLabel userBadge = new JLabel("Agent: " + user.username());
		userBadge.setFont(LABEL_FONT);
		userBadge.setForeground(TEXT_SECONDARY);
		headerPanel.add(userBadge);

		mainPanel.add(headerPanel);
		mainPanel.add(Box.createVerticalStrut(15));

		// Create inventory tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(CARD_COLOR);
		tabbedPane.setForeground(TEXT_PRIMARY);
		tabbedPane.setFont(LABEL_FONT);

		// Weapon categories
		String[] categories = {"🔫 Pistols", "🔫 Assault Rifles", "🔫 SMG", "🔫 LMG", "🔫 Sniper", "💣 Grenades", "🏥 Medics", "📦 Miscellaneous"};
		
		for(String category : categories)
		{
			JPanel categoryPanel = createWeaponCategoryPanel(category);
			tabbedPane.addTab(category.replaceAll("🔫|💣|🏥|📦", "").trim(), categoryPanel);
		}

		mainPanel.add(tabbedPane);
		mainPanel.add(Box.createVerticalStrut(15));

		// Bottom button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setOpaque(false);

		JButton refreshButton = createStyledButton("🔄 Refresh", PRIMARY_COLOR, PRIMARY_LIGHT);
		JButton exportButton = createStyledButton("📊 Export Report", SECONDARY_COLOR, new Color(200, 110, 200));
		JButton logoutButton = createStyledButton("🚪 Logout", ERROR_COLOR, new Color(200, 50, 40));

		refreshButton.addActionListener(e -> showDialog("Refresh", "Inventory refreshed successfully!"));
		exportButton.addActionListener(e -> showDialog("Export", "Inventory report exported to secure server."));
		logoutButton.addActionListener(e ->
		{
			logActivity("LOGOUT", user.username());
			dashboardFrame.dispose();
			main(new String[]{});
		});

		buttonPanel.add(refreshButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(exportButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(logoutButton);

		mainPanel.add(buttonPanel);

		dashboardFrame.add(mainPanel);
		dashboardFrame.setVisible(true);
	}

	private static JPanel createWeaponCategoryPanel(String category)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 4, 15, 15));
		panel.setBackground(CARD_COLOR);
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));

		Map<String, Integer> weapons = getWeaponsForCategory(category);
		for(Map.Entry<String, Integer> entry : weapons.entrySet())
		{
			JPanel weaponCard = createWeaponCard(entry.getKey(), entry.getValue());
			panel.add(weaponCard);
		}

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setBackground(CARD_COLOR);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);

		JPanel containerPanel = new JPanel(new BorderLayout());
		containerPanel.setBackground(CARD_COLOR);
		containerPanel.add(scrollPane, BorderLayout.CENTER);
		return containerPanel;
	}

	private static Map<String, Integer> getWeaponsForCategory(String category)
	{
		Map<String, Integer> weapons = new LinkedHashMap<>();
		
		if(category.contains("Pistol"))
		{
			weapons.put("Glock 19", 15);
			weapons.put("SIG P226", 12);
			weapons.put("Beretta M9", 10);
			weapons.put("Ruger 9mm", 8);
		}
		else if(category.contains("Assault Rifle"))
		{
			weapons.put("INSAS 5.56mm", 20);
			weapons.put("AK-47", 18);
			weapons.put("M16A4", 16);
			weapons.put("HK416", 14);
		}
		else if(category.contains("SMG"))
		{
			weapons.put("MP5", 12);
			weapons.put("UMP45", 10);
			weapons.put("P90", 8);
			weapons.put("Vector 45 ACP", 6);
		}
		else if(category.contains("LMG"))
		{
			weapons.put("M249 SAW", 5);
			weapons.put("RPK", 4);
			weapons.put("M60", 3);
			weapons.put("MG42", 2);
		}
		else if(category.contains("Sniper"))
		{
			weapons.put("AWM 338", 4);
			weapons.put("Barrett M82", 3);
			weapons.put("Remington 700", 5);
			weapons.put("L96A1", 2);
		}
		else if(category.contains("Grenade"))
		{
			weapons.put("Frag Grenades", 50);
			weapons.put("Smoke Grenades", 40);
			weapons.put("Flashbangs", 35);
			weapons.put("EMP Grenades", 20);
		}
		else if(category.contains("Medic"))
		{
			weapons.put("First Aid Kits", 30);
			weapons.put("Trauma Packs", 25);
			weapons.put("Adrenaline Kits", 20);
			weapons.put("Blood Plasma Units", 15);
		}
		else
		{
			weapons.put("Combat Drones", 5);
			weapons.put("Thermal Scopes", 12);
			weapons.put("Body Armor", 20);
			weapons.put("Surveillance Kits", 8);
		}
		
		return weapons;
	}

	private static JPanel createWeaponCard(String weaponName, int count)
	{
		RoundedPanel card = new RoundedPanel(12, new Color(55, 70, 90), 5);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(new EmptyBorder(15, 15, 15, 15));
		card.setMaximumSize(new Dimension(250, 200));
		card.setPreferredSize(new Dimension(250, 200));

		// Weapon graphics
		JLabel weaponGraphic = new JLabel();
		weaponGraphic.setIcon(new ImageIcon(createWeaponGraphic(weaponName, 120, 120)));
		weaponGraphic.setAlignmentX(Component.CENTER_ALIGNMENT);
		card.add(weaponGraphic);
		card.add(Box.createVerticalStrut(10));

		// Weapon name
		JLabel nameLabel = new JLabel(weaponName);
		nameLabel.setFont(LABEL_FONT);
		nameLabel.setForeground(PRIMARY_COLOR);
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		card.add(nameLabel);
		card.add(Box.createVerticalStrut(8));

		// Stock display
		JPanel stockPanel = new JPanel();
		stockPanel.setOpaque(false);
		stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.X_AXIS));
		
		JLabel stockLabel = new JLabel("Stock: ");
		stockLabel.setForeground(TEXT_SECONDARY);
		stockPanel.add(stockLabel);
		
		JLabel countLabel = new JLabel(String.valueOf(count));
		countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		countLabel.setForeground(count > 10 ? SUCCESS_COLOR : WARN_COLOR);
		stockPanel.add(countLabel);
		
		JLabel unitsLabel = new JLabel(" units");
		unitsLabel.setForeground(TEXT_SECONDARY);
		stockPanel.add(unitsLabel);
		
		stockPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		card.add(stockPanel);

		return card;
	}

	private static BufferedImage createWeaponGraphic(String weaponName, int width, int height)
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background glow
		g.setColor(new Color(230, 126, 34, 30));
		g.fillRect(0, 0, width, height);

		g = createWeaponShape(g, weaponName, width, height);
		g.dispose();
		return img;
	}

	private static Graphics2D createWeaponShape(Graphics2D g, String weaponName, int w, int h)
	{
		g.setStroke(new BasicStroke(2));
		int centerX = w / 2;
		int centerY = h / 2;

		if(weaponName.contains("Pistol") || weaponName.contains("Glock") || weaponName.contains("SIG") || weaponName.contains("Beretta"))
		{
			drawPistol(g, centerX - 20, centerY - 25, 40, 50);
		}
		else if(weaponName.contains("Assault") || weaponName.contains("AK") || weaponName.contains("M16") || weaponName.contains("HK") || weaponName.contains("INSAS"))
		{
			drawAssaultRifle(g, centerX - 30, centerY - 20, 60, 40);
		}
		else if(weaponName.contains("SMG") || weaponName.contains("MP5") || weaponName.contains("UMP") || weaponName.contains("P90") || weaponName.contains("Vector"))
		{
			drawSMG(g, centerX - 25, centerY - 18, 50, 36);
		}
		else if(weaponName.contains("LMG") || weaponName.contains("M249") || weaponName.contains("RPK") || weaponName.contains("M60"))
		{
			drawLMG(g, centerX - 35, centerY - 22, 70, 44);
		}
		else if(weaponName.contains("Sniper") || weaponName.contains("AWM") || weaponName.contains("Barrett") || weaponName.contains("Remington") || weaponName.contains("L96"))
		{
			drawSniperRifle(g, centerX - 32, centerY - 18, 64, 36);
		}
		else if(weaponName.contains("Grenade") || weaponName.contains("Frag") || weaponName.contains("Smoke") || weaponName.contains("Flash") || weaponName.contains("EMP"))
		{
			drawGrenade(g, centerX - 12, centerY - 18, 24, 36);
		}
		else if(weaponName.contains("First Aid") || weaponName.contains("Trauma") || weaponName.contains("Adrenaline") || weaponName.contains("Plasma"))
		{
			drawMedical(g, centerX - 15, centerY - 20, 30, 40);
		}
		else
		{
			drawMisc(g, centerX - 20, centerY - 20, 40, 40);
		}

		return g;
	}

	private static void drawPistol(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(PRIMARY_COLOR);
		g.fillRect(x + 10, y + 5, 15, h - 15);
		g.fillRect(x + 5, y + 25, 20, 8);
		g.fillOval(x, y, 12, 12);
		g.setColor(new Color(200, 200, 200));
		g.fillRect(x + 12, y + 10, 8, h - 20);
	}

	private static void drawAssaultRifle(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(PRIMARY_COLOR);
		g.fillRect(x + 10, y + h / 2, w - 20, h / 4);
		g.fillRect(x, y + 5, w, 8);
		g.fillRect(x + w - 15, y + h / 2 - 5, 15, h / 2);
		g.setColor(new Color(200, 200, 200));
		g.fillRect(x + 5, y + h / 2 - 10, w - 10, 6);
	}

	private static void drawSMG(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(PRIMARY_COLOR);
		g.fillRect(x + 8, y + h / 2 - 2, w - 16, 12);
		g.fillRect(x + 2, y + 8, w - 4, 6);
		g.fillRect(x + w - 12, y + h / 2 - 8, 12, h / 2);
		g.setColor(new Color(200, 200, 200));
		g.fillRect(x + 8, y + 2, w - 16, 4);
	}

	private static void drawLMG(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(PRIMARY_COLOR);
		g.fillRect(x + 12, y + h / 2 - 3, w - 24, 14);
		g.fillRect(x + 2, y + 6, w - 4, 8);
		g.fillOval(x + w - 18, y + h / 2 - 8, 16, 16);
		g.setColor(new Color(200, 200, 200));
		g.fillRect(x + 10, y + 2, w - 20, 5);
	}

	private static void drawSniperRifle(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(PRIMARY_COLOR);
		g.fillRect(x + 10, y + h / 2 - 2, w - 20, 10);
		g.fillPolygon(new int[]{x, x + 15, x + 10}, new int[]{y + 5, y + 5, y + 15}, 3);
		g.fillRect(x + w - 12, y + 8, 12, h - 16);
		g.setColor(new Color(200, 200, 200));
		g.fillRect(x + 15, y + 4, w - 25, 3);
		g.drawOval(x + w - 15, y + 10, 10, 10);
	}

	private static void drawGrenade(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(new Color(241, 196, 15));
		g.fillOval(x + 3, y + 8, w - 6, w - 6);
		g.setColor(PRIMARY_COLOR);
		g.fillRect(x + w / 2 - 1, y, 2, 8);
		g.setColor(new Color(200, 200, 200));
		g.drawOval(x + 5, y + 10, w - 10, w - 10);
	}

	private static void drawMedical(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(ERROR_COLOR);
		g.fillRect(x, y, w, h);
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(2));
		g.drawLine(x + w / 2, y + 5, x + w / 2, y + h - 5);
		g.drawLine(x + 5, y + h / 2, x + w - 5, y + h / 2);
	}

	private static void drawMisc(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(SECONDARY_COLOR);
		g.fillRect(x + 5, y + 5, w - 10, h - 10);
		g.setColor(new Color(200, 200, 200));
		g.drawOval(x + 8, y + 8, 8, 8);
		g.drawOval(x + w - 16, y + 8, 8, 8);
		g.drawOval(x + 8, y + h - 16, 8, 8);
		g.drawOval(x + w - 16, y + h - 16, 8, 8);
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
	 * User record to store user information
	 */
	record User(String username, String passwordHash, String email, boolean isAdmin)
	{
	}

	/**
	 * Modern border with rounded corners
	 */
	static class ModernBorder extends AbstractBorder
	{
		private final Color color;
		private final int thickness;

		ModernBorder(Color color, int thickness)
		{
			this.color = color;
			this.thickness = thickness;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
			g2.setStroke(new BasicStroke(thickness));
			g2.drawRoundRect(x, y, w - 1, h - 1, 8, 8);
			g2.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c)
		{
			return new Insets(thickness, thickness, thickness, thickness);
		}
	}
}