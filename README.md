
# 💸 Expense Tracker - Java (Swing GUI)

A simple Java Swing-based desktop application to track daily expenses with a graphical user interface. The project demonstrates Java OOP principles, file handling, and basic GUI design using Swing.

---

## 📌 Table of Contents

- [Features](#features)
- [Demo](#demo)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)

---

## ✅ Features

- ➕ Add a new expense with description, amount, and category
- 📋 View all recorded expenses in a GUI table
- ❌ Delete expenses with a click
- 💰 Calculate total expenditure
- 💾 Persistent storage using text files
- 🖱️ Easy-to-use graphical interface via Java Swing

---

## 🧰 Tech Stack

- **Java** – Core logic and OOP
- **Java Swing** – GUI framework for the user interface
- **Binary File** – Used for local storage

---

## 🖥️ Installation

### Prerequisites:
- JDK 8 or later
- Java-compatible IDE (Eclipse, IntelliJ, NetBeans)

### Steps:
1. Clone the repository:
   ```bash
   git clone https://github.com/pranusri1903/Expense-Tracker---Java.git
   ```

2. Open the project in your IDE.

3. Compile and run the main GUI class file (e.g., `ExpenseTracker.java`).

---

## 🧪 Usage

- Launch the GUI window.
- Use the input fields and buttons to:
  - Add expense details
  - View and manage entries
  - See total expenses displayed dynamically
- All data is stored in `username_expenses.dat`.

---

## 📂 Project Structure

```
Expense-Tracker---Java/
├── Expense.java            # Model class
├── ExpenseTracker.java     # Swing GUI and application logic
├── expenses.txt            # Persistent data storage
└── README.md               # Documentation
```


## 🌱 Future Enhancements

- [ ] Category-wise filters and sorting
- [ ] Monthly or yearly analytics
- [ ] Export to CSV or Excel
- [ ] Database integration (SQLite/MySQL)
- [ ] Modern UI using JavaFX

---

## 🤝 Contributing

Contributions are welcome!  
To contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -m 'Add feature'`)
4. Push to the branch (`git push origin feature-name`)
5. Open a **Pull Request**

