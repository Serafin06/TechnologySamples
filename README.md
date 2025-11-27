# 🧪 TechSam - Technology Sample Management

Desktop application for managing technology samples with SQL Server database integration.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Architecture](#architecture)
- [Usage](#usage)
- [Database Structure](#database-structure)
- [Screenshots](#screenshots)

## 🎯 Overview

TechSam is a desktop application for managing production samples, handling:
- Orders (ZO)
- Printing (ZD)
- Lamination (ZL)
- Cutting (ZK)

It enables tracking order statuses, adding technical notes, and filtering data by various criteria.

## ✨ Features

### Core Functionality

- **📊 Sample Browser** - View samples with filtering and search capabilities
- **🔍 Advanced Filters** - Search by number, ART, recipe, branch, statuses
- **📅 Date Range** - Select time range (3/6/12 months or custom)
- **✏️ Technical Notes** - 4 editable note fields per sample
- **🎨 Visual Statuses** - Color-coded status badges (green/blue/orange/red)
- **🔄 Async Loading** - Background data loading with progress indicator
- **💾 Background Saving** - Non-blocking save operations
- **🟢 Connection Monitor** - Real-time database connection status indicator
- **📱 Responsive UI** - Compact, information-dense interface
- **🔓 Expandable Details** - Click to expand full sample information

### Status Types

| Status | Color | Description |
|--------|-------|-------------|
| **Zlecenie** (Order) | Variable | Main order status |
| **Drukowanie** (Printing) | Variable | Printing process status |
| **Laminacja** (Lamination) | Variable | Lamination process status |
| **Krajarki** (Cutting) | Variable | Cutting process status |

### Status States

- 🟢 **Completed** - Green
- 🔵 **In Progress** - Blue
- 🟠 **Planned** - Orange
- 🔴 **On Hold** - Red
- ⚫ **Cancelled** - Gray

## 🛠 Technologies

### Framework & Language

- **Kotlin** 1.9+
- **Compose Desktop** - UI framework
- **Coroutines** - Asynchronous operations

### Database

- **Hibernate ORM** - Database mapping
- **SQL Server** - Database engine
- **JDBC** - Database connectivity

### Architecture

- **MVVM** (Model-View-ViewModel)
- **Repository Pattern** - Data access layer
- **Service Layer** - Business logic
- **DTO Pattern** - Data transfer objects
- **SOLID Principles** - Clean code architecture

## 📦 Requirements

### System Requirements

- **Java JDK** 17 or higher
- **SQL Server** 2016 or higher
- **Windows/Linux/macOS** - Desktop OS

### Database Access

- Network access to SQL Server instance
- Valid database credentials
- Tables: `ZO`, `ZK`, `ZD`, `ZL`, `TODO_TABELA`

## 🚀 Installation

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/techsam.git
cd techsam
```

### 2. Build Project

```bash
./gradlew build
```

### 3. Run Application

```bash
./gradlew run
```

## ⚙️ Configuration

### Database Configuration

Edit `HibernateConfig.kt` with your database credentials:

```kotlin
object HibernateConfig {
    private const val DB_URL = "jdbc:sqlserver://YOUR_SERVER:1433;databaseName=YOUR_DB"
    private const val DB_USER = "your_username"
    private const val DB_PASSWORD = "your_password"
    
    // ...
}
```

### Connection Properties

```properties
hibernate.connection.driver_class=com.microsoft.sqlserver.jdbc.SQLServerDriver
hibernate.dialect=org.hibernate.dialect.SQLServerDialect
hibernate.show_sql=false
hibernate.format_sql=true
```

## 🏗 Architecture

### Project Structure

```
pl.rafapp.techSam/
├── Base/                      # Business logic layer
│   ├── ProbkaDTO.kt          # Data transfer objects
│   ├── ProbkaService.kt      # Service interface & implementation
│   ├── ProbkaRepository.kt   # Repository pattern
│   └── ProbkaMapper.kt       # Entity-DTO mapping
├── DataBase/                  # Database layer
│   ├── HibernateConfig.kt    # Hibernate configuration
│   ├── ZO.kt                 # Order entity
│   ├── ZK.kt                 # Cutting entity
│   ├── ZD.kt                 # Printing entity
│   ├── ZL.kt                 # Lamination entity
│   └── TodoTabela.kt         # Technical notes entity
└── UI/                        # Presentation layer
    ├── ProbkiScreen.kt       # Main screen
    ├── ProbkiViewModel.kt    # View model
    ├── ProbkaCard.kt         # Sample card component
    ├── FilterPanel.kt        # Filter controls
    ├── StateScreens.kt       # Loading/Error/Empty states
    └── AppColors.kt          # Theme & styling
```

### Layer Responsibilities

#### **Repository Layer** (`ProbkaRepository`)
- Database access
- Query execution
- Connection management

#### **Service Layer** (`ProbkaService`)
- Business logic
- Data transformation
- Transaction coordination

#### **ViewModel Layer** (`ProbkiViewModel`)
- UI state management
- Async operations (coroutines)
- Filter logic
- Connection monitoring

#### **UI Layer** (Compose components)
- User interface rendering
- User interaction handling
- Visual feedback

## 📖 Usage

### Starting the Application

1. Launch application
2. Wait for initial data load (progress bar displayed)
3. Green connection indicator = ready to use

### Filtering Samples

**Search Bar:**
- Enter order number, ART, or recipe name

**Date Range:**
- Select predefined range (3/6/12 months)
- Or choose custom date range

**Status Filters:**
- Filter by order status (ZO)
- Filter by printing status (ZD)
- Filter by lamination status (ZL)
- Filter by cutting status (ZK)

**Branch Filter:**
- Select Ignatki or Tychy

### Managing Technical Notes

1. **View Mode** - Click expand icon (📖) to show full notes
2. **Edit Mode** - Click edit icon (✏️) to enable editing
3. **Hover** - Hover over notes to see full text in tooltip
4. **Save** - Click save button to persist changes (background operation)

### Connection Status

**Indicator Colors:**
- 🟢 **Green** - Connected to database
- 🔴 **Red** - Connection lost
- ⚫ **Gray** - Checking connection

**Actions:**
- Click indicator to manually check connection
- Auto-refresh every 5 minutes

## 🗄 Database Structure

### Main Tables

#### **ZO** (Orders)
Main order table containing basic sample information.

```sql
Key Fields:
- ID, NUMER, ODDZIAL, ROK
- DATA (date)
- ART, RECEPTURA_1
- SZEROKOSC_1, GRUBOSC_11, GRUBOSC_21, GRUBOSC_31
- STAN (status), ILOSC, WYKONANA
- TERMIN_ZAK, DATA_ZAK
```

#### **ZD** (Printing)
Printing process tracking.

```sql
Key Fields:
- NUMER, ODDZIAL, ROK
- STAN, ILOSC, WYKONANA
- TERMIN_ZAK, DATA_ZAK
```

#### **ZL** (Lamination)
Lamination process tracking (can have multiple entries per order).

```sql
Key Fields:
- NUMER, ODDZIAL, ROK
- STAN, ILOSC, WYKONANA
- RECEPTURA_1, KOLORW_1
- TERMIN_ZAK, DATA_ZAK
```

#### **ZK** (Cutting)
Cutting process tracking.

```sql
Key Fields:
- NUMER, ODDZIAL, ROK
- STAN, ILOSC, WYKONANA
- TERMIN_ZAK, DATA_ZAK
```

#### **TODO_TABELA** (Technical Notes)
Custom technical notes storage.

```sql
Fields:
- NUMER, ODDZIAL, ROK (foreign key to ZO)
- TODO_KOLUMNA_1, TODO_KOLUMNA_2
- TODO_KOLUMNA_3, TODO_KOLUMNA_4
```

### Status Codes

| Code | Name | Description |
|------|------|-------------|
| 0 | Wykonane | Completed |
| 1 | W realizacji | In Progress |
| 2 | Planowane | Planned |
| 3 | Wstrzymane | On Hold |
| 4 | Anulowane | Cancelled |
| 5 | Do weryfikacji | Verification Needed |

### Branch Codes

| Code | Name |
|------|------|
| 11 | Ignatki |
| 12 | Tychy |

## 🎨 UI Customization

All visual elements are commented in code for easy customization:

```kotlin
// ROZMIAR: Change font size
fontSize = 12.sp

// ODSTĘP: Change spacing
horizontalArrangement = Arrangement.spacedBy(8.dp)

// PADDING: Change padding
modifier = Modifier.padding(12.dp)

// KOLOR: Change colors (in AppColors.kt)
val Primary = Color(0xFF2196F3)
```

## 🐛 Troubleshooting

### Connection Issues

**Problem:** Red connection indicator

**Solutions:**
1. Check SQL Server is running
2. Verify network connectivity
3. Confirm credentials in `HibernateConfig.kt`
4. Check firewall settings (port 1433)

### Performance Issues

**Problem:** Slow loading

**Solutions:**
1. Reduce date range (use 3 months instead of 12)
2. Check database indexes on `ZO.DATA`, `ZO.NUMER`
3. Verify SQL Server performance

### Display Issues

**Problem:** UI elements overlapping

**Solutions:**
1. Adjust window size (minimum 1400x900)
2. Check screen resolution
3. Modify padding/spacing values in code

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📧 Contact

RafApp

GitHub: @Serafin06

---

**Built with ❤️ using Kotlin & Compose Desktop**

## 📄 LICENSE (MIT)
MIT License

Copyright (c) 2025 RafApp

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.