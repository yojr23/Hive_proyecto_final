# 🐝 Hive – Social Event Networking App  

## 📌 Project Overview  
Hive is a mobile application designed to enhance social event experiences. It allows users to scan QR codes at events, register attendance, and connect with others in the city. The platform enables seamless event tracking and fosters real-time social interactions.  

## 🎯 What makes Hive different?  
🔹 **Seamless event check-ins** with fast and secure QR code scanning.  
🔹 **Personalized event recommendations** based on user interests.  
🔹 **Real-time networking** to connect with attendees at live events.  

## 📊 Key Features  
✅ **QR Code Scanning** – Instantly register at events by scanning a unique code.  
✅ **Event Discovery** – Find and join social gatherings happening nearby.  
✅ **User Profiles & Networking** – Connect with attendees and expand your social circle.  
✅ **Event Insights** – Track participation history and engagement metrics.  
✅ **Real-Time Notifications** – Stay updated on upcoming events and activity within the app.  

## 🛠️ Technologies Used  
- **Java (Android SDK)** – Core development of the mobile application.  
- **Firebase Authentication** – Secure login and user management.  
- **Firebase Firestore** – Cloud database for event and user data storage.  
- **Google ML Kit (QR Scanner)** – Fast QR code recognition and processing.  
- **Room Database (SQLite)** – Offline storage for seamless user experience.  
- **Jetpack Components** – ViewModel, LiveData, and Navigation for structured development.  

## 🏗️ App Architecture  
Hive follows an **MVVM (Model-View-ViewModel) architecture**, ensuring clean code and easy scalability:  

   📂 Hive
   
   ├── 📂 adapters # RecyclerView adapters for dynamic UI elements
   
   ├── 📂 models # Data models (User, Event, UserSession)
   
   ├── 📂 utils # Helper classes (SignUpHelper, SessionManager)
   
   ├── 📂 views # UI activities and fragments
   
   ├── 📂 viewmodels # Business logic and data handling
   
   ├── 📂 services # Firebase and network-related operations

## 🚀 Impact & Achievements  
🔹 **Reduced event check-in time by 60%** using QR scanning.  
🔹 **Improved event engagement** with real-time social features.  
🔹 **Scalable architecture** for city-wide event adoption.  

## 💻 How to Run the Project  

### Clone this repository:  
```bash
git clone https://github.com/your_username/hive_app.git
cd hive_app

