# Task-Management-Android-App

## Project Description
A Task Management App for multiple users. Each user must sign up using an email address and a password. Once logged in, users can:

- Add a to-do task.
- View current tasks in the list.
- Mark completed tasks.

The application includes the following functionalities:

---

## 1. Signup, Sign in, and Logout

This layout provides “Sign in” and “Sign Up” buttons.

### a. Sign In
- The main page allows users to enter their email and password (registered in the database) to sign in. 
- Includes a checkbox labeled **“Remember Me”** to save the email in shared preferences so that users don't need to retype their email address next time.

### b. Sign Up
- Redirects users to a sign-up form with the following required fields:
  1. **Email Address**: Must be in the correct email format (used as the primary key).
  2. **First Name**: Minimum 5 characters, maximum 20 characters.
  3. **Last Name**: Minimum 5 characters, maximum 20 characters.
  4. **Password**: 
     - Minimum 6 characters, maximum 12 characters.
     - Must contain at least one number, one lowercase letter, and one uppercase letter.
  5. **Confirm Password**: Should match the password field.

- If all required fields are filled with valid data, the user is registered successfully. Otherwise, an error message is displayed.

---

## 2. Home Layout

The Home Layout is a **Navigation Drawer Activity** that displays the to-do tasks for today. The navigation bar includes the following functionalities:

### a. Today
Displays the to-do tasks for today (main page).

### b. New Task
Allows users to add a new to-do task. Task components include:
- Task Title
- Task Description
- Due Date and Time
- Priority Level
- Completion Status
- Reminder Icon
- Action Icons (Edit, Delete)

### c. All
Displays all to-do tasks sorted chronologically and grouped by day.

### d. Completed
Displays all completed tasks sorted chronologically and grouped by day.

### e. Search
Allows users to filter and display to-do tasks within a specified date range (start date and end date).

### f. Profile
Allows users to view and edit their profiles. Note: Only the email address and password can be edited.

### g. Logout
Logs out the user and redirects them to the sign-in page.

---

## 3. Other Features

### a. Task Management
- Users can modify any displayed to-do tasks (in any menu), mark them as completed, share them via email, or delete them.

### b. Search Bar
- A search bar is provided to filter tasks based on keywords in the task title or description.

### c. Notifications
- Users can set notification alerts for any tasks.

### d. Completion Animation
- When all tasks for today are marked as completed, the app displays a congratulations message using toast messages and an engaging short animation.

### e. Task Import
- A button is available to import a list of tasks using a REST API.

### f. Dark and Light Modes
- The application supports both dark and light modes.

### g. Task Prioritization
- Users can set task priorities (**High**, **Medium**, **Low**) to help organize tasks based on urgency. 
- The default priority is **Medium**, and tasks can be sorted based on priority for each day.

### h. Customizable Notifications
- Users can customize notification times for tasks (e.g., a day before or a few hours before).
- Includes an option to snooze reminders for additional time.

---

