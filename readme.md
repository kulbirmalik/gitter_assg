# **GitterApplication**

GitterApp is a Spring Boot-based application designed to manage file staging, commit history, and diffs in a Git-like in-memory repository. 
It provides a CLI-style interface for interacting with staged files and committed changes, mimicking basic Git operations like add, commit, diff, status, and more.


## ✅ Features

Git-like file staging (CommandAddService) – Stage and unstage files using pattern matching.
Commit management (CommandCommitService) – Commit staged changes to an in-memory store.
Diff viewing (CommandDiffService) – Compare file states between current and committed versions.
Helper utilities (CommandHelperService) – Utility support for file management and processing.
Repository initialization (CommandInitService) – Initialize a new Gitter workspace.
Commit logs (CommandLogService) – View historical commits and their metadata.
File status display (CommandStatusService) – Show current file statuses (staged, modified, untracked).



## 🛠 Prerequisites

Ensure the following are installed:
Java 17 or later
Maven (for dependency management)
An IDE like IntelliJ IDEA or Eclipse (optional, for development)


## 🚀 Installation
1. Clone the repository
   git clone https://github.com/yourusername/gitterApp.git
   cd gitterApp

2. Install dependencies
   mvn install

3. Set the environment
   Ensure Java 17+ is set in your environment:
   java -version

4. Build the application
   mvn clean install

## ▶️ Running the Application
   mvn spring-boot:run
   This starts the GitterApp backend on the default port (typically http://localhost:8080).

## 🧪 Running Tests
    mvn test


## 📦 Available Commands
gitter init – Initializes the Gitter workspace.
gitter add <file-or-pattern> – Stages specified files.
gitter status – Displays the current file status.
gitter commit -m "<message>" – Commits staged changes with a message.
gitter diff [<file-or-path>] – Shows file differences (optionally filtered).
gitter log – Lists commit history.