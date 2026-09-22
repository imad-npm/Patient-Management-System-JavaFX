# PMS — Patient Management System

A desktop patient management application built with JavaFX and Apache Derby.

## Features

- Patient management
- Medical consultations
- Prescriptions and medicines
- Appointments
- Doctor management
- JavaFX graphical interface
- Local Apache Derby database

## Technologies

- Java
- JavaFX
- Apache Derby
- Maven
- FXML
- CSS

## Project Structure

```text
PMS1/
├── src/
│   └── application/
│       ├── patients/
│       ├── consultations/
│       ├── medicines/
│       ├── rdv/
│       ├── parametres/
│       └── MainController.java
├── pom.xml
├── README.md
└── ...

How to Run

Make sure Java and Maven are installed.

From the project root, run:

mvn javafx:run

Maven will download the required dependencies and launch the JavaFX application.

The application uses a local Apache Derby database, which is initialized automatically when the application starts.