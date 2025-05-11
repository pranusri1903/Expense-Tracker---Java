import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ExpenseTracker {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ExpenseTrackerGUI();
        });
    }
}

class ExpenseTrackerGUI extends JFrame {
    private UserManager userManager;
    private User currentUser;
    private ExpenseManager expenseManager;

    // Login panel components
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    // Main panel components
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTable categoryTable;
    private DefaultTableModel categoryTableModel;

    // Add expense panel components
    private JTextField dateField;
    private JComboBox<String> categoryCombo;
    private JTextField descriptionField;
    private JTextField amountField;

    // Sort options
    private JComboBox<String> sortCombo;
    private JComboBox<String> filterCategoryCombo;

    public ExpenseTrackerGUI() {
        userManager = new UserManager();
        expenseManager = new ExpenseManager();
        userManager.loadUsers();

        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        createLoginPanel();
        setContentPane(loginPanel);

        // Handle window close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });

        setVisible(true);
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Expense Tracker Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Add components to form
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(registerButton, gbc);

        gbc.gridx = 1;
        formPanel.add(loginButton, gbc);

        // Add the form to the login panel
        loginPanel.add(formPanel, BorderLayout.CENTER);

        // Add action listeners
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());

        // Add key listener to password field
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());

        // Create header with welcome message and logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane for different views
        tabbedPane = new JTabbedPane();

        // Create expenses tab
        JPanel expensesPanel = createExpensesPanel();
        tabbedPane.addTab("Expenses", expensesPanel);

        // Create add expense tab
        JPanel addExpensePanel = createAddExpensePanel();
        tabbedPane.addTab("Add Expense", addExpensePanel);

        // Create summary tab
        JPanel summaryPanel = createSummaryPanel();
        tabbedPane.addTab("Category Summary", summaryPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createExpensesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create sort and filter options
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel sortLabel = new JLabel("Sort by:");
        sortCombo = new JComboBox<>(new String[] {
                "Date (newest first)",
                "Date (oldest first)",
                "Amount (highest first)",
                "Category"
        });

        JLabel filterLabel = new JLabel("Filter category:");
        String[] categories = {"All", "Food", "Transportation", "Housing", "Utilities", "Entertainment", "Other"};
        filterCategoryCombo = new JComboBox<>(categories);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> refreshExpenseTable());

        optionsPanel.add(sortLabel);
        optionsPanel.add(sortCombo);
        optionsPanel.add(Box.createHorizontalStrut(20));
        optionsPanel.add(filterLabel);
        optionsPanel.add(filterCategoryCombo);
        optionsPanel.add(applyButton);

        panel.add(optionsPanel, BorderLayout.NORTH);

        // Create expense table
        String[] columnNames = {"Date", "Category", "Description", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedExpense());
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAddExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create form components
        JLabel dateLabel = new JLabel("Date (MM/dd/yyyy):");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel descriptionLabel = new JLabel("Description:");
        JLabel amountLabel = new JLabel("Amount ($):");

        dateField = new JTextField(10);
        // Set current date as default
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        dateField.setText(sdf.format(new Date()));

        String[] categories = {"Food", "Transportation", "Housing", "Utilities", "Entertainment", "Other"};
        categoryCombo = new JComboBox<>(categories);
        descriptionField = new JTextField(20);
        amountField = new JTextField(10);

        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(e -> addExpense());

        // Add components to form
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(addButton, gbc);

        // Add empty border for padding
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Center the form
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create summary table
        String[] columnNames = {"Category", "Amount", "Percentage"};
        categoryTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(categoryTableModel);
        categoryTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCategorySummary());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userManager.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose another one.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(username, password);
        userManager.addUser(newUser);
        userManager.saveUsers();

        JOptionPane.showMessageDialog(this,
                "Registration successful! You can now login.",
                "Registration Success",
                JOptionPane.INFORMATION_MESSAGE);

        usernameField.setText("");
        passwordField.setText("");
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        User user = userManager.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            expenseManager.loadUserExpenses(currentUser.getUsername());

            createMainPanel();
            setContentPane(mainPanel);
            refreshExpenseTable();
            refreshCategorySummary();
            validate();

        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        saveData();
        expenseManager.clearExpenses();
        currentUser = null;

        usernameField.setText("");
        passwordField.setText("");
        setContentPane(loginPanel);
        validate();
    }

    private void addExpense() {
        try {
            String dateStr = dateField.getText().trim();
            Date date = new SimpleDateFormat("MM/dd/yyyy").parse(dateStr);

            String category = (String) categoryCombo.getSelectedItem();
            String description = descriptionField.getText().trim();

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Description cannot be empty.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountStr = amountField.getText().trim();
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Amount cannot be empty.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountStr);

            Expense expense = new Expense(date, category, description, amount);
            expenseManager.addExpense(currentUser.getUsername(), expense);

            JOptionPane.showMessageDialog(this,
                    "Expense added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear input fields except date
            descriptionField.setText("");
            amountField.setText("");

            // Refresh tables
            refreshExpenseTable();
            refreshCategorySummary();

            // Switch to expenses tab
            tabbedPane.setSelectedIndex(0);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use MM/dd/yyyy format.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid amount. Please enter a valid number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshExpenseTable() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        List<Expense> expenses = expenseManager.getAllExpenses();
        if (expenses.isEmpty()) {
            return;
        }

        // Filter by category if selected
        String selectedCategory = (String) filterCategoryCombo.getSelectedItem();
        if (!"All".equals(selectedCategory)) {
            List<Expense> filteredExpenses = new ArrayList<>();
            for (Expense e : expenses) {
                if (e.getCategory().equals(selectedCategory)) {
                    filteredExpenses.add(e);
                }
            }
            expenses = filteredExpenses;
        }

        // Sort expenses
        int sortOption = sortCombo.getSelectedIndex();
        switch (sortOption) {
            case 0: // Date (newest first)
                Collections.sort(expenses, Comparator.comparing(Expense::getDate).reversed());
                break;
            case 1: // Date (oldest first)
                Collections.sort(expenses, Comparator.comparing(Expense::getDate));
                break;
            case 2: // Amount (highest first)
                Collections.sort(expenses, Comparator.comparing(Expense::getAmount).reversed());
                break;
            case 3: // Category
                Collections.sort(expenses, Comparator.comparing(Expense::getCategory));
                break;
        }

        // Add rows to table
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[] {
                    sdf.format(expense.getDate()),
                    expense.getCategory(),
                    expense.getDescription(),
                    String.format("$%.2f", expense.getAmount())
            });
        }

        // Add total row
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        tableModel.addRow(new Object[] {
                "", "TOTAL", "", String.format("$%.2f", total)
        });
    }

    private void refreshCategorySummary() {
        categoryTableModel.setRowCount(0);

        Map<String, Double> categorySummary = expenseManager.calculateCategorySummary();
        if (categorySummary.isEmpty()) {
            return;
        }

        double totalAmount = 0;
        for (double amount : categorySummary.values()) {
            totalAmount += amount;
        }

        // Sort categories by amount (descending)
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(categorySummary.entrySet());
        Collections.sort(sortedEntries, Map.Entry.<String, Double>comparingByValue().reversed());

        // Add rows to table
        for (Map.Entry<String, Double> entry : sortedEntries) {
            double percentage = (entry.getValue() / totalAmount) * 100;
            categoryTableModel.addRow(new Object[] {
                    entry.getKey(),
                    String.format("$%.2f", entry.getValue()),
                    String.format("%.1f%%", percentage)
            });
        }

        // Add total row
        categoryTableModel.addRow(new Object[] {
                "TOTAL", String.format("$%.2f", totalAmount), "100.0%"
        });
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= expenseManager.getAllExpenses().size()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an expense to delete.",
                    "Selection Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Filter based on selected category, if any
            List<Expense> expenses = expenseManager.getAllExpenses();
            String selectedCategory = (String) filterCategoryCombo.getSelectedItem();
            if (!"All".equals(selectedCategory)) {
                List<Expense> filteredExpenses = new ArrayList<>();
                for (Expense e : expenses) {
                    if (e.getCategory().equals(selectedCategory)) {
                        filteredExpenses.add(e);
                    }
                }
                expenses = filteredExpenses;
            }

            // Sort the expenses the same way they are displayed
            int sortOption = sortCombo.getSelectedIndex();
            switch (sortOption) {
                case 0: // Date (newest first)
                    Collections.sort(expenses, Comparator.comparing(Expense::getDate).reversed());
                    break;
                case 1: // Date (oldest first)
                    Collections.sort(expenses, Comparator.comparing(Expense::getDate));
                    break;
                case 2: // Amount (highest first)
                    Collections.sort(expenses, Comparator.comparing(Expense::getAmount).reversed());
                    break;
                case 3: // Category
                    Collections.sort(expenses, Comparator.comparing(Expense::getCategory));
                    break;
            }

            // Delete the expense
            Expense expenseToDelete = expenses.get(selectedRow);
            expenseManager.deleteExpense(currentUser.getUsername(), expenseToDelete);

            // Refresh the UI
            refreshExpenseTable();
            refreshCategorySummary();
        }
    }

    private void saveData() {
        userManager.saveUsers();
        if (currentUser != null) {
            expenseManager.saveUserExpenses(currentUser.getUsername());
        }
    }
}

class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class UserManager {
    private static final String USER_DATA_FILE = "users.dat";
    private List<User> users;

    public UserManager() {
        users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void loadUsers() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }
}

class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date date;
    private String category;
    private String description;
    private double amount;

    public Expense(Date date, String category, String description, double amount) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Expense other = (Expense) obj;
        return date.equals(other.date) &&
                category.equals(other.category) &&
                description.equals(other.description) &&
                Double.compare(amount, other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, category, description, amount);
    }
}

class ExpenseManager {
    private List<Expense> expenses;

    public ExpenseManager() {
        expenses = new ArrayList<>();
    }

    public void addExpense(String username, Expense expense) {
        expenses.add(expense);
        saveUserExpenses(username);
    }

    public void deleteExpense(String username, Expense expense) {
        expenses.remove(expense);
        saveUserExpenses(username);
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> filteredExpenses = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getCategory().equals(category)) {
                filteredExpenses.add(expense);
            }
        }
        return filteredExpenses;
    }

    public Map<String, Double> calculateCategorySummary() {
        Map<String, Double> summary = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double currentAmount = summary.getOrDefault(category, 0.0);
            summary.put(category, currentAmount + expense.getAmount());
        }

        return summary;
    }

    public void clearExpenses() {
        expenses.clear();
    }

    @SuppressWarnings("unchecked")
    public void loadUserExpenses(String username) {
        String filename = username + "_expenses.dat";
        File file = new File(filename);

        if (!file.exists()) {
            expenses = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            expenses = (List<Expense>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading expense data: " + e.getMessage());
            expenses = new ArrayList<>();
        }
    }

    public void saveUserExpenses(String username) {
        String filename = username + "_expenses.dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            System.err.println("Error saving expense data: " + e.getMessage());
        }
    }
}