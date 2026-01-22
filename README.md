# Task Manager

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



## Contact

GitHub: [zubairali7893@gmail.com](https://github.com/Zubair-Ali-Sandhu)
