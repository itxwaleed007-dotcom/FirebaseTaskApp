# Firebase Task Manager

An Android task manager app built with **Java** and **Firebase**. Users can sign up, log in, and manage their personal tasks, which are saved in the cloud and synced in real time.

## Features

- Email and password Sign Up and Login (Firebase Authentication)
- Stays logged in between app launches
- Add, complete, and delete tasks (Cloud Firestore)
- Real-time updates with a Firestore snapshot listener
- Each user sees only their own tasks (secured with Firestore Security Rules)
- Clean Material Design UI with RecyclerView and floating action button

## Tech Stack

- Java
- Android Studio
- Firebase Authentication
- Cloud Firestore
- RecyclerView, Material Components

## Project Structure

- `LoginActivity` - Login and Sign Up screen
- `MainActivity` - Task list, add, complete, delete, and logout
- `TaskAdapter` - RecyclerView adapter for the task list
- `Task` - Task data model

## Setup

1. Clone this repository
2. Create a project in the [Firebase Console](https://console.firebase.google.com)
3. Add an Android app with package name `com.boots.firebasetaskapp`
4. Download `google-services.json` and place it in the `app/` folder
5. Enable **Email/Password** in Authentication and create a **Firestore** database
6. Publish the Firestore rules below, then build and run the app
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /tasks/{taskId} {
      allow create: if request.auth != null
                    && request.resource.data.userId == request.auth.uid;
      allow read, update, delete: if request.auth != null
                    && resource.data.userId == request.auth.uid;
    }
  }
}
```

## Author

Waleed Arshad - Android Developer

GitHub: [itxwaleed007-dotcom](https://github.com/itxwaleed007-dotcom)