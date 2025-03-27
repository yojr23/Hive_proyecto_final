🐝 Hive – Plataforma de Publicación y Descubrimiento de Actividades
Hive es una aplicación móvil desarrollada en Android Studio con Java, diseñada para conectar a personas a través de eventos y actividades. Permite a los usuarios publicar, buscar y confirmar asistencia a eventos en diversas categorías como deportes, juegos y más.

🚀 Tecnologías clave

Lenguaje: Java

Base de datos en la nube: Firebase Firestore

Autenticación y almacenamiento: Firebase Authentication & Firestore

Gestión de código: Git con Bitbucket

Arquitectura: MVVM (Model-View-ViewModel)

Patrones de diseño: Singleton, Observer y Callback

Interfaz gráfica: XML + Material Design

Código modular: Uso de Adapter, ViewModel y SessionManager para una estructura escalable

Generación de UUIDs: Identificación única de usuarios y eventos

📱 Características principales
✅ Registro y autenticación de usuarios con Firebase Authentication
✅ Creación y publicación de actividades con detalles como fecha, hora y ubicación
✅ Búsqueda avanzada de eventos con filtros por categoría y ubicación
✅ Confirmación de asistencia con un solo clic
✅ Escáner QR para visualizar eventos de forma rápida
✅ Sistema de ranking para destacar a los usuarios más activos
✅ Calendario integrado para organizar eventos personales

⚙️ Arquitectura del Proyecto
Hive sigue un enfoque modular y escalable basado en MVVM:

Model: Representa los datos y lógica del negocio (Firebase Firestore, User, Event).

ViewModel: Gestiona la lógica de presentación y comunicación con los modelos de datos.

View: Actividades y fragmentos en XML que manejan la interfaz de usuario.

Repository: Encapsula la lógica de acceso a datos y comunicación con Firebase.

📂 Estructura del Código
graphql
Copiar
Editar
📂 Hive  
 ├── 📂 adapters         # Adaptadores para RecyclerViews  
 ├── 📂 models           # Clases de datos (User, Event, UserSession)  
 ├── 📂 utils            # Clases auxiliares como SignUpHelper y SessionManager  
 ├── 📂 views            # Actividades y Fragmentos XML  
 ├── 📂 viewmodels       # Lógica de presentación para MVVM  
 └── 📂 services         # Servicios de backend y Firebase  
🔥 Configuración e Instalación
Clona el repositorio

sh
Copiar
Editar
git clone https://bitbucket.org/tu_usuario/hive.git
cd hive
Abre el proyecto en Android Studio

Configura Firebase

Descarga el archivo google-services.json desde Firebase y colócalo en app/

Ejecuta la app en un emulador o dispositivo físico

sh
Copiar
Editar
./gradlew build
📌 Roadmap y Mejoras Futuras
🔹 Integración con notificaciones push con Firebase Cloud Messaging (FCM)
🔹 Implementación de pagos dentro de la app para eventos premium
🔹 Mejoras en el ranking de usuarios con un sistema de gamificación

🎯 Sobre el desarrollo
Desarrollado con pasión por yojr23, con un enfoque en experiencia de usuario, escalabilidad y rendimiento.
