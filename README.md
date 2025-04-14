# IS25-AM17 – Galaxy Truckers

**Software Engineering Project – Politecnico di Milano (2023–2024)**  
**Team:** Davide Cutrupi • Tommaso D'Alessio • Simone De Carlo • Davide Di Tanna

---

## 🚀 Project Overview

This project is a digital adaptation of the board game *Galaxy Trucker* by Cranio Creations.  
The application is based on a **single-server architecture** capable of handling **multiple concurrent matches**, each supporting **2 to 4 players**.

Players can access the game through either a **Command-Line Interface (CLI)** or a **Graphical User Interface (GUI)**.

The project adheres to **software engineering best practices** and makes extensive use of **design patterns**, with a particular emphasis on the **MVC (Model-View-Controller)** paradigm.

Technical details regarding networking, communication protocols, and system architecture can be found in the project documentation.

---

## ✅ Features Implemented

Alongside the core gameplay mechanics, the project also integrates several of the game’s advanced modules. Out of the four optional modules, **three have been successfully implemented**.

---

## 📌 Development Progress

| Feature                     | Status |
|-----------------------------|--------|
| Base game rules             | ✅      |
| Full ruleset                | ⬜      |
| Socket-based networking     | ⬜      |
| RMI networking              | ⬜      |
| CLI                         | ⬜      |
| GUI                         | ⬜      |
| Support for multiple games  | ⬜      |
| Game state persistence (*)  | ⬜      |
| Resilience & recovery       | ⬜      |
| In-game chat                | ⬜      |

> (*) Persistence refers to the ability to save and restore game sessions.

---

## 📚 Documentation

All documentation is located in the `/deliverables` folder within this repository.

### UML Diagrams

The folder `/deliverables/final/uml` contains both the **UML Class Diagram** and key **UML Sequence Diagrams**.

- The **Class Diagram** outlines the main classes within the *Model* and relevant parts of the *Controller*.
- The **Sequence Diagrams** illustrate core interactions, such as joining a game and drawing cards.

### JavaDoc

The JavaDoc documentation can be found under `/images/javadoc`, and covers the most significant classes and methods.

---

## 🛠️ Technologies Used

| Tool/Library | Role                                |
|--------------|-------------------------------------|
| **Maven**    | Dependency management & build tool  |
| **JavaFX**   | GUI development                     |
| **JUnit**    | Unit testing                        |

---
