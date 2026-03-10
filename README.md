# Library Management System (Client-Server)

A distributed library management system developed in Java using a client-server architecture.

## Overview

This project implements a computerized library system that manages books, users, and borrowing operations.  
The system allows library members and librarians to interact with the system through a graphical interface.

The system follows a **client-server architecture**, where multiple clients can access the library services simultaneously.

## Features

- Book search by title, subject, or keywords
- Borrow and return books
- Extend loan period
- Reserve books when all copies are borrowed
- Automatic reminders before return date
- User account management
- Borrowing history tracking
- Library reports and statistics

## Architecture

Client-Server architecture:

Client  
- User interface for readers and librarians

Server  
- Handles requests from clients
- Manages business logic
- Connects to the database

Database  
- Stores books, users, loans, reservations, and reports

## Technologies

- Java
- JavaFX (UI)
- Client-Server Networking
- MySQL
- Object-Oriented Programming (OOP)

## System Capabilities

- Multi-user system
- Real-time request handling
- User status management (active / frozen)
- Borrowing history tracking
- Automatic notifications

## Project Structure
