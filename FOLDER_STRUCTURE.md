# 📁 Java Folder Structure Explained

## Complete Java Package Structure

```
app/src/main/java/
└── me/                                    [Domain identifier - reversed]
    └── zubair/                            [Developer/organization name]
        └── taskmanager/                   [Application name - ROOT PACKAGE]
            ├── activities/                [UI Controllers - Screens]
            │   ├── AlarmActivity.java
            │   ├── MainActivity.java
            │   └── SplashActivity.java
            │
            ├── adapters/                  [Data-to-UI Adapters]
            │   └── TaskAdapter.java
            │
            ├── database/                  [Data Layer - Persistence]
            │   ├── TaskContract.java
            │   ├── TaskDbHelper.java
            │   └── TaskRepository.java
            │
            ├── fragments/                 [UI Components - Reusable Screens]
            │   ├── EditTaskFragment.java
            │   ├── HomeFragment.java
            │   ├── SettingsFragment.java
            │   ├── TaskDetailsFragment.java
            │   ├── TaskDialogFragment.java
            │   └── TaskListFragment.java
            │
            ├── models/                    [Data Models - POJOs]
            │   └── Task.java
            │
            ├── preferences/               [User Settings Storage]
            │   └── UserPreferencesManager.java
            │
            ├── receivers/                 [Broadcast Receivers - System Events]
            │   └── BootReceiver.java
            │
            ├── services/                  [Background Services]
            │   ├── AlarmReceiver.java
            │   └── NotificationService.java
            │
            └── utils/                     [Helper/Utility Classes]
                ├── FileHelper.java
                └── QuoteApiClient.java
```

---

## 📦 Package Naming Convention

### Root Package: `me.zubair.taskmanager`

This follows the **reverse domain naming convention**:
- **me** = Top-level domain (like .com, .org, .me)
- **zubair** = Developer/organization identifier
- **taskmanager** = Application name

**Why this convention?**
- Ensures global uniqueness (no two apps will have same package)
- Industry standard in Java/Android development
- Easy to identify app ownership

---

## 📂 Detailed Folder Breakdown

### 1️⃣ **activities/** (3 files)
**Purpose**: Contains Activity classes - full-screen UI controllers

**What are Activities?**
- Entry points for user interaction
- Represent complete screens in Android
- Manage Fragment lifecycles
- Handle system callbacks

**Files:**
- **`MainActivity.java`** (Main UI controller)
  - The primary screen of the app
  - Hosts fragments (Home, TaskList, Settings)
  - Manages bottom navigation
  - Handles fragment transactions
  
- **`SplashActivity.java`** (Launch screen)
  - First screen shown when app opens
  - Displays logo/branding for 2 seconds
  - Navigates to MainActivity
  - Creates professional first impression
  
- **`AlarmActivity.java`** (Full-screen alarm)
  - Shows when critical task alarm triggers
  - Can display over lock screen
  - Has dismiss/snooze buttons
  - Handles wake locks

**Naming Convention**: `*Activity.java`

---

### 2️⃣ **adapters/** (1 file)
**Purpose**: Contains Adapter classes - bridge between data and UI

**What are Adapters?**
- Connect data (like task list) to UI components (RecyclerView)
- Responsible for creating and binding view holders
- Handle item clicks and interactions
- Efficiently recycle views for performance

**Files:**
- **`TaskAdapter.java`** (RecyclerView adapter)
  - Takes list of Task objects
  - Creates task card views
  - Handles checkbox clicks (mark complete)
  - Handles long-press (edit/delete menu)
  - Updates UI when data changes

**Pattern**: Adapter Pattern (Gang of Four design pattern)

**Naming Convention**: `*Adapter.java`

---

### 3️⃣ **database/** (3 files)
**Purpose**: Contains database-related classes - data persistence layer

**What's in here?**
- Database schema definitions
- Database creation and upgrade logic
- Data access operations (CRUD)
- Repository pattern implementation

**Files:**
- **`TaskContract.java`** (Database contract/schema)
  - Defines table name: "tasks"
  - Defines column names: _id, title, description, priority, due_date, etc.
  - Constants for database structure
  - No logic, just definitions
  
- **`TaskDbHelper.java`** (SQLite database helper)
  - Extends SQLiteOpenHelper
  - Creates database on first run
  - Handles database upgrades (schema changes)
  - Provides writable/readable database instances
  
- **`TaskRepository.java`** (Data access layer)
  - Abstracts database operations
  - CRUD methods: insert(), update(), delete(), query()
  - Business logic for data operations
  - Single source of truth for data

**Pattern**: Repository Pattern - separates data logic from UI

**Naming Convention**: `*Contract.java`, `*DbHelper.java`, `*Repository.java`

---

### 4️⃣ **fragments/** (6 files)
**Purpose**: Contains Fragment classes - modular UI components

**What are Fragments?**
- Reusable portions of UI within activities
- Have their own lifecycle (similar to activities)
- Can be combined in different ways
- Easier to manage than multiple activities

**Files:**
- **`HomeFragment.java`** (Home screen)
  - Shows current time/date
  - Displays motivational quote
  - Task overview statistics
  - Developer info card
  
- **`TaskListFragment.java`** (Task list screen)
  - Shows all tasks in RecyclerView
  - Has FAB (Floating Action Button) to add tasks
  - Handles task item clicks → navigate to details
  - Handles long-press → show edit/delete menu
  
- **`TaskDetailsFragment.java`** (Task detail view)
  - Shows full task information
  - Mark complete/incomplete button
  - Edit task button
  - View attachments (if any)
  
- **`EditTaskFragment.java`** (Add/Edit task form)
  - Input fields: title, description
  - Date picker, time picker
  - Priority selector (Low/Medium/High)
  - Handles both ADD and EDIT modes
  - Save/Update button
  
- **`TaskDialogFragment.java`** (Quick add dialog - legacy)
  - Old simple add task dialog
  - Still in code but not currently used
  - Kept for reference/backup
  
- **`SettingsFragment.java`** (App settings)
  - User preferences
  - Notification settings
  - App customization options

**Pattern**: Fragment-based navigation (modern Android pattern)

**Naming Convention**: `*Fragment.java`

---

### 5️⃣ **models/** (1 file)
**Purpose**: Contains data model classes - Plain Old Java Objects (POJOs)

**What are Models?**
- Represent data structures
- No business logic (just getters/setters)
- Can be passed between components
- Mirror database schema

**Files:**
- **`Task.java`** (Task data model)
  - Fields: id, title, description, priority, dueDate, dueTime, isCompleted
  - Constructor(s)
  - Getters and setters
  - toString() method
  - Might implement Parcelable (for passing between activities)

**Pattern**: Data Transfer Object (DTO) / Value Object

**Naming Convention**: Singular noun representing entity (e.g., `Task.java`, `User.java`)

---

### 6️⃣ **preferences/** (1 file)
**Purpose**: Contains classes for managing user preferences/settings

**What's this for?**
- Persistent storage of user settings
- Wrapper around SharedPreferences
- Centralized preference management
- Type-safe preference access

**Files:**
- **`UserPreferencesManager.java`**
  - Save/load notification preferences
  - Save/load theme preferences
  - Save/load user settings
  - Provides convenient methods instead of raw SharedPreferences

**Pattern**: Manager/Helper pattern for SharedPreferences

**Naming Convention**: `*PreferencesManager.java` or `*PreferenceHelper.java`

---

### 7️⃣ **receivers/** (1 file)
**Purpose**: Contains BroadcastReceiver classes - listen to system events

**What are BroadcastReceivers?**
- Listen for system-wide broadcast announcements
- React to events like device boot, alarm triggers, etc.
- Run in background (even when app is closed)
- Short-lived - execute quickly and exit

**Files:**
- **`BootReceiver.java`** (Boot completion receiver)
  - Listens for BOOT_COMPLETED broadcast
  - Triggered when device finishes booting
  - Restarts NotificationService
  - Reschedules alarms for tasks
  - Ensures app functionality after reboot

**Pattern**: Observer pattern (Android system broadcasts)

**Naming Convention**: `*Receiver.java`

---

### 8️⃣ **services/** (2 files)
**Purpose**: Contains Service classes - background operations

**What are Services?**
- Long-running background operations
- Run without UI
- Can continue when app is not visible
- Handle time-sensitive operations

**Files:**
- **`NotificationService.java`** (Foreground service)
  - Runs continuously in background
  - Shows persistent notification (required for foreground service)
  - Checks for upcoming/due tasks
  - Sends notifications at appropriate times
  - Manages notification channels
  
- **`AlarmReceiver.java`** (Alarm broadcast receiver)
  - Actually a BroadcastReceiver (could be in receivers/ folder)
  - Triggered by AlarmManager at scheduled time
  - Receives alarm for specific task
  - Shows notification or starts AlarmActivity
  - Handles alarm dismissal/snoozing

**Pattern**: Service pattern for background work

**Naming Convention**: `*Service.java`, `*Receiver.java`

---

### 9️⃣ **utils/** (2 files)
**Purpose**: Contains utility/helper classes - reusable functionality

**What goes here?**
- Helper methods used across app
- API clients
- File operations
- Date/time formatting
- Any reusable logic that doesn't fit elsewhere

**Files:**
- **`FileHelper.java`** (File operations helper)
  - Methods to save files
  - Methods to read files
  - File URI handling
  - Permission checking
  - Used for task attachments
  
- **`QuoteApiClient.java`** (API client for quotes)
  - Makes HTTP requests to quote API
  - Parses JSON response
  - Returns random motivational quotes
  - Handles network errors
  - Used by HomeFragment

**Pattern**: Helper/Utility pattern

**Naming Convention**: `*Helper.java`, `*Utils.java`, `*Client.java`, `*Manager.java`

---

## 🎯 Folder Organization Principles

### Why separate into folders?

1. **Separation of Concerns**
   - Each folder has a specific responsibility
   - Easy to find related code
   - Changes in one area don't affect others

2. **Scalability**
   - Easy to add new activities/fragments/etc.
   - Clear structure even as app grows
   - New developers can understand quickly

3. **Maintainability**
   - Bug in notifications? → Check `services/`
   - UI issue? → Check `fragments/` or `activities/`
   - Database problem? → Check `database/`

4. **Testability**
   - Can test each layer independently
   - Mock dependencies easily
   - Clear boundaries between components

---

## 📊 Component Interaction Flow

```
User Action
    ↓
[activities/] or [fragments/]  ← UI Layer
    ↓
[adapters/] (if list-based)    ← Presentation
    ↓
[database/] via [models/]      ← Data Layer
    ↓
[services/] or [receivers/]    ← Background Layer
    ↓
[utils/] (helpers)             ← Utility Layer
    ↓
[preferences/] (settings)      ← Storage Layer
```

**Example: Adding a Task**
1. User clicks FAB in `TaskListFragment` (fragments/)
2. Opens `EditTaskFragment` (fragments/)
3. User fills form, clicks Save
4. Creates `Task` object (models/)
5. Calls `TaskRepository.insert()` (database/)
6. Schedules alarm via `AlarmReceiver` (services/)
7. Returns to `TaskListFragment`
8. `TaskAdapter` (adapters/) updates UI

---

## 🏗️ Architecture Pattern Used

This app follows **Layered Architecture** with elements of **Repository Pattern**:

```
┌─────────────────────────────────────┐
│   Presentation Layer                │
│   (activities/, fragments/,         │
│    adapters/)                       │
├─────────────────────────────────────┤
│   Business Logic Layer              │
│   (database/TaskRepository)         │
├─────────────────────────────────────┤
│   Data Layer                        │
│   (database/TaskDbHelper,           │
│    models/, preferences/)           │
├─────────────────────────────────────┤
│   Background Layer                  │
│   (services/, receivers/)           │
├─────────────────────────────────────┤
│   Utility Layer                     │
│   (utils/)                          │
└─────────────────────────────────────┘
```

---

## 💡 Best Practices Demonstrated

1. ✅ **Package by Feature** (alternative to package by layer)
   - All task-related code together
   - Could split further (e.g., `tasks/`, `quotes/`, `alarms/`)

2. ✅ **Single Responsibility**
   - Each class has one job
   - `TaskAdapter` only adapts data to UI
   - `TaskRepository` only handles data access

3. ✅ **Clear Naming**
   - Suffix indicates type: `*Fragment`, `*Activity`, `*Adapter`
   - Purpose clear from name: `BootReceiver`, `FileHelper`

4. ✅ **Separation of Concerns**
   - UI doesn't know about SQLite details
   - Database doesn't know about Activities
   - Each layer independent

---

## 📚 Summary

### Folder Count: **9 folders** in total
1. `activities/` - 3 files
2. `adapters/` - 1 file
3. `database/` - 3 files
4. `fragments/` - 6 files
5. `models/` - 1 file
6. `preferences/` - 1 file
7. `receivers/` - 1 file
8. `services/` - 2 files
9. `utils/` - 2 files

**Total: 20 Java files**

### Terminology:
- **Package** = Folder in Java
- **me.zubair.taskmanager** = Root package
- **Sub-packages** = activities, adapters, database, etc.

---

## 🎓 Learning Tips

1. **Start with `models/`** - Understand the data structure first
2. **Then `database/`** - See how data is stored/retrieved
3. **Then `fragments/`** - See how data is displayed
4. **Finally `services/` and `receivers/`** - Background magic

**Remember**: Each folder represents a **layer** of the application. Understanding this layered approach is key to understanding modern Android development!

---

**Created**: January 2026  
**Purpose**: Educational reference for Task Manager app structure
