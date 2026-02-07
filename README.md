#  Task Manager - Android App

A full-featured task management application built from scratch using Java and Android Studio. Stay organized, never miss deadlines, and boost your productivity!

[![GitHub](https://img.shields.io/badge/GitHub-Zubair--Ali--Sandhu-blue?style=flat&logo=github)](https://github.com/Zubair-Ali-Sandhu/Task-Manager)
[![Android](https://img.shields.io/badge/Platform-Android-green?style=flat&logo=android)](https://www.android.com/)
[![Java](https://img.shields.io/badge/Language-Java-orange?style=flat&logo=java)](https://www.java.com/)

An Android task management application designed to help users organize tasks, receive timely notifications, and stay productive.

## Features

- **Task Management**: Create, edit, delete, and organize tasks with ease
- **Priority System**: Set task priorities (Low, Medium, High, Critical)
- **Due Date Reminders**: Get notifications for upcoming and due tasks
- **File Attachments**: Attach documents, images, and files to tasks
- **Alarm System**: Full-screen alerts for critical tasks
- **Motivational Quotes**: Daily motivation through integrated quote API
- **Persistent Notifications**: Background service ensures you never miss important deadlines
- **Clean Material Design UI**: Intuitive and user-friendly interface
- **Automatic Boot Recovery**: Service restarts after device reboot

## Technical Details

- **Language**: Java
- **Minimum SDK**: Android 8.0 (API level 26)
- **Target SDK**: Android 13 (API level 33)
- **Architecture**: Fragment-based UI with Repository pattern
- **Database**: SQLite with ContentProvider
- **Background Processing**: Services, BroadcastReceivers, AlarmManager
- **UI Components**: Material Design, RecyclerView, CardView
- **File Handling**: FileProvider for secure attachment sharing

## Implementation Highlights

- Background service with reliable notification delivery
- Wake lock management for critical notifications
- Multiple notification channels with different priority levels
- Full-screen alarms for critical tasks
- Custom file management system
- External API integration for motivational content
- Shared preferences for user settings

## Setup Instructions

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on an emulator or physical device

## Permissions

- `READ_EXTERNAL_STORAGE` & `WRITE_EXTERNAL_STORAGE`: For file attachments
- `RECEIVE_BOOT_COMPLETED`: For service restart after device reboot
- `INTERNET`: For fetching motivational quotes
- `VIBRATE`: For notification alerts
- `WAKE_LOCK`: For ensuring reliable alarm delivery

## Project Structure

```
app/src/main/java/me/zubair/taskmanager/
├── activities/       # Main app screens (MainActivity, SplashActivity, AlarmActivity)
├── fragments/        # UI fragments (Home, TaskList, TaskDetails, Settings, etc.)
├── adapters/         # RecyclerView adapters for task lists
├── database/         # SQLite database implementation (Contract, Helper, Repository)
├── models/           # Data models (Task class)
├── services/         # Background services (Notifications, AlarmReceiver)
├── receivers/        # BroadcastReceivers (BootReceiver)
├── preferences/      # SharedPreferences manager
└── utils/            # Helper classes (FileHelper, QuoteApiClient)
```

## Screenshots
<img width="400" height="800" alt="image" src="https://github.com/user-attachments/assets/d34147a5-9391-434f-99ef-1e940afc8e1a" />
<img width="400" height="800" alt="image" src="https://github.com/user-attachments/assets/c17545d9-0024-4cba-a01e-900d2dba0baf" />
<img width="400" height="800" alt="image" src="https://github.com/user-attachments/assets/2153d4cc-6f52-431a-b436-6e8a9f9990bf" />
<img width="400" height="800" alt="image" src="https://github.com/user-attachments/assets/a3841e2d-444e-478c-b482-0e5dfb3a7173" />








## Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/Zubair-Ali-Sandhu/Task-Manager/issues).

## License

This project is open source and available for educational purposes.

##  Author

**Zubair Ali Sandhu**

- GitHub: [@Zubair-Ali-Sandhu](https://github.com/Zubair-Ali-Sandhu)

---

⭐ Star this repo if you found it helpful!


## Contact

GitHub: [zubairali7893@gmail.com](https://github.com/Zubair-Ali-Sandhu)
