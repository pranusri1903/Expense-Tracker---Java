
# 💸 Expense Tracker - Java

A simple **command-line Expense Tracker** built in Java that allows users to add, view, and manage daily expenses. This project showcases the use of **Java classes, file handling, OOP principles**, and a basic interactive menu system.

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
- 📋 View all recorded expenses
- ❌ Delete expenses by ID
- 💰 Calculate total expenditure
- 💾 Data persistence using file handling
- 🧭 Menu-driven CLI interface

---

## 📽️ Demo

> *(Add screenshots or a video demo if available)*  
Example menu:
```
1. Add Expense  
2. View Expenses  
3. Delete Expense  
4. Get Total Expense  
5. Exit  
```

---

## 🧰 Tech Stack

- **Java** – Core logic and file I/O
- **Text File** – Used as local storage (`expenses.txt`)
- **CLI** – Command-line user interface

---

## 🖥️ Installation

### Prerequisites:
- JDK 8 or later
- Java-compatible IDE (Eclipse, IntelliJ, etc.)

### Steps:
1. Clone the repository:
   ```bash
   git clone https://github.com/pranusri1903/Expense-Tracker---Java.git
   ```

2. Open the project in your IDE.

3. Compile and run the `ExpenseTracker.java` file.

---

## 🧪 Usage

Upon running the program, follow the on-screen menu options to:

- **Add** a new expense by entering details.
- **View** all stored expenses from the `expenses.txt` file.
- **Delete** an expense by its ID or index.
- **Get total** expenses calculated dynamically.

---

## 📂 Project Structure

```
Expense-Tracker---Java/
├── Expense.java            # Model class representing an expense
├── ExpenseTracker.java     # Main logic and user interface
├── expenses.txt            # Persistent file storage
└── README.md               # Documentation
```

---

## 🌱 Future Enhancements

- [ ] Add **category-wise expense filtering**
- [ ] Implement **monthly expense reports**
- [ ] Create a **GUI version** using JavaFX or Swing
- [ ] Export data to **CSV or Excel**
- [ ] Use **database (e.g., SQLite)** for storage instead of flat file

---

## 🤝 Contributing

Contributions are welcome!  
To contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -m 'Add feature'`)
4. Push to the branch (`git push origin feature-name`)
5. Open a **Pull Request**


