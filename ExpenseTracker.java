import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseTracker {
    public static void main(String[] args) {
        ExpenseTrackerSystem system = new ExpenseTrackerSystem();
        system.start();
    }
}

class ExpenseTrackerSystem {
    private Scanner scanner;
    private UserManager userManager;
    private User currentUser;
    private ExpenseManager expenseManager;
    
    public ExpenseTrackerSystem() {
        scanner = new Scanner(System.in);
        userManager = new UserManager();
        expenseManager = new ExpenseManager();
    }
    
    public void start() {
        loadData();
        boolean running = true;
        
        while (running) {
            if (currentUser == null) {
                displayLoginMenu();
                int choice = getUserChoice(3);
                
                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        loginUser();
                        break;
                    case 3:
                        running = false;
                        System.out.println("Thank you for using Expense Tracker. Goodbye!");
                        break;
                }
            } else {
                displayMainMenu();
                int choice = getUserChoice(6);
                
                switch (choice) {
                    case 1:
                        addExpense();
                        break;
                    case 2:
                        viewExpenses();
                        break;
                    case 3:
                        viewExpensesByCategory();
                        break;
                    case 4:
                        getCategorySummary();
                        break;
                    case 5:
                        logout();
                        break;
                    case 6:
                        running = false;
                        System.out.println("Thank you for using Expense Tracker. Goodbye!");
                        break;
                }
            }
        }
        
        saveData();
    }
    
    private void displayLoginMenu() {
        System.out.println("\n===== EXPENSE TRACKER =====");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void displayMainMenu() {
        System.out.println("\n===== EXPENSE TRACKER =====");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. View Expenses by Category");
        System.out.println("4. View Category Summary");
        System.out.println("5. Logout");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private int getUserChoice(int maxChoice) {
        int choice = -1;
        while (choice < 1 || choice > maxChoice) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < 1 || choice > maxChoice) {
                    System.out.print("Invalid choice. Try again: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }
        return choice;
    }
    
    private void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        if (userManager.usernameExists(username)) {
            System.out.println("Username already exists. Please choose another one.");
            return;
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        User newUser = new User(username, password);
        userManager.addUser(newUser);
        System.out.println("Registration successful! You can now login.");
    }
    
    private void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        User user = userManager.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            expenseManager.loadUserExpenses(currentUser.getUsername());
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }
    
    private void logout() {
        saveData();
        expenseManager.clearExpenses();
        currentUser = null;
        System.out.println("Logged out successfully.");
    }
    
    private void addExpense() {
        try {
            System.out.print("Enter date (MM/dd/yyyy): ");
            String dateStr = scanner.nextLine().trim();
            Date date = new SimpleDateFormat("MM/dd/yyyy").parse(dateStr);
            
            System.out.println("Select category:");
            String[] categories = {"Food", "Transportation", "Housing", "Utilities", "Entertainment", "Other"};
            for (int i = 0; i < categories.length; i++) {
                System.out.println((i + 1) + ". " + categories[i]);
            }
            System.out.print("Enter category number: ");
            int categoryChoice = getUserChoice(categories.length);
            String category = categories[categoryChoice - 1];
            
            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();
            
            System.out.print("Enter amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            
            Expense expense = new Expense(date, category, description, amount);
            expenseManager.addExpense(currentUser.getUsername(), expense);
            System.out.println("Expense added successfully!");
            
        } catch (ParseException e) {
            System.out.println("Invalid date format. Use MM/dd/yyyy format.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
    }
    
    private void viewExpenses() {
        List<Expense> expenses = expenseManager.getAllExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        
        displayExpensesSortingOptions();
        int sortChoice = getUserChoice(4);
        
        switch(sortChoice) {
            case 1: // Default (newest first)
                Collections.sort(expenses, Comparator.comparing(Expense::getDate).reversed());
                break;
            case 2: // Oldest first
                Collections.sort(expenses, Comparator.comparing(Expense::getDate));
                break;
            case 3: // Highest amount first
                Collections.sort(expenses, Comparator.comparing(Expense::getAmount).reversed());
                break;
            case 4: // Category
                Collections.sort(expenses, Comparator.comparing(Expense::getCategory));
                break;
        }
        
        displayExpenses(expenses);
    }
    
    private void displayExpensesSortingOptions() {
        System.out.println("Sort expenses by:");
        System.out.println("1. Date (newest first)");
        System.out.println("2. Date (oldest first)");
        System.out.println("3. Amount (highest first)");
        System.out.println("4. Category");
        System.out.print("Enter your choice: ");
    }
    
    private void viewExpensesByCategory() {
        String[] categories = {"Food", "Transportation", "Housing", "Utilities", "Entertainment", "Other"};
        System.out.println("Select category to view:");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Enter category number: ");
        int categoryChoice = getUserChoice(categories.length);
        String selectedCategory = categories[categoryChoice - 1];
        
        List<Expense> filteredExpenses = expenseManager.getExpensesByCategory(selectedCategory);
        if (filteredExpenses.isEmpty()) {
            System.out.println("No expenses found for the selected category.");
            return;
        }
        
        Collections.sort(filteredExpenses, Comparator.comparing(Expense::getDate).reversed());
        System.out.println("\nExpenses for category: " + selectedCategory);
        displayExpenses(filteredExpenses);
    }
    
    private void getCategorySummary() {
        Map<String, Double> categorySummary = expenseManager.calculateCategorySummary();
        if (categorySummary.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        
        double totalAmount = 0;
        System.out.println("\n===== CATEGORY SUMMARY =====");
        System.out.println(String.format("%-15s %-10s %-10s", "Category", "Amount", "Percentage"));
        System.out.println("-------------------------------------");
        
        // First, calculate the total amount
        for (double amount : categorySummary.values()) {
            totalAmount += amount;
        }
        
        // Then print each category with its percentage
        for (Map.Entry<String, Double> entry : categorySummary.entrySet()) {
            double percentage = (entry.getValue() / totalAmount) * 100;
            System.out.println(String.format("%-15s $%-9.2f %.1f%%", 
                entry.getKey(), entry.getValue(), percentage));
        }
        
        System.out.println("-------------------------------------");
        System.out.println(String.format("%-15s $%-9.2f 100.0%%", "TOTAL", totalAmount));
    }
    
    private void displayExpenses(List<Expense> expenses) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        System.out.println("\n===== EXPENSE LIST =====");
        System.out.println(String.format("%-12s %-15s %-20s %-10s", "Date", "Category", "Description", "Amount"));
        System.out.println("-------------------------------------------------------");
        
        for (Expense expense : expenses) {
            System.out.println(String.format("%-12s %-15s %-20s $%-9.2f", 
                sdf.format(expense.getDate()), 
                expense.getCategory(), 
                expense.getDescription(), 
                expense.getAmount()));
        }
        
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        System.out.println("-------------------------------------------------------");
        System.out.println(String.format("%-48s $%-9.2f", "TOTAL", total));
    }
    
    private void loadData() {
        userManager.loadUsers();
        // Expenses are loaded per user when they log in
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