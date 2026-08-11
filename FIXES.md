# TeleHealth Platform — سجل الإصلاحات (Fix Log)

## الأخطاء اللي كانت موجودة وتم إصلاحها

### 1. Bean مفقود `patientSecurity` (كراش فوري)
`PatientController.getById` كان بيستخدم:
```java
@PreAuthorize("hasRole('DOCTOR') or @patientSecurity.isSelf(#patientId)")
```
لكن مفيش أي bean اسمه `patientSecurity` في المشروع كله → أي طلب `GET /patients/{id}` كان هيرمي
`BeanResolutionException` في وقت التشغيل.

**الحل:** `src/main/java/com/telehealth/config/security/PatientSecurity.java`

---

### 2. مفيش نظام تسجيل دخول خالص
- `SecurityConfig` بيسمح بـ `/auth/**` بدون توثيق.
- `JwtService.generateToken()` موجود وجاهز.
- لكن مفيش أي Controller بيستخدمه، ومفيش حقل `password` في `Patient` ولا `Doctor` أصلاً.

نتيجة: محدش كان يقدر يعمل login أو ياخد JWT حقيقي — كل الـ security layer كانت غير قابلة
للاستخدام كما هي.

**الحل:** موديول `auth` جديد بالكامل (`modules/auth/...`):
- `AppUser` (domain model) — منفصل عمدًا عن `Patient`/`Doctor` عشان يفضلوا نضاف من أي حاجة
  متعلقة بالتوثيق (Clean Architecture).
- `AuthApplicationService` — `login()` و `createCredentials()`.
- `AuthController` — `POST /auth/login`.
- جدول `app_users` جديد (Liquibase changelog `005-create-app-users-table.xml`).
- ربط تسجيل المريض/الدكتور (`PatientApplicationService` / `DoctorApplicationService`) عشان
  ينشئوا بيانات الدخول أوتوماتيك في نفس الـ transaction.
- إضافة حقل `password` لطلبات التسجيل (`PatientRequest.RegisterPatientRequest`,
  `DoctorCommand.RegisterDoctor`).

---

### 3. مفيش طريقة لعمل حساب Admin
Endpoints زي `/doctors/{id}/verify` و `/patients/{id}/suspend` بتتطلب `ROLE_ADMIN`، لكن
مفيش أي شخص كان يقدر يوصل للدور ده أصلاً.

**الحل:** `AdminBootstrapRunner` — بيزرع حساب admin افتراضي أول مرة التطبيق يشتغل لو مفيش
حساب موجود بالفعل. الإيميل والباسورد قابلين للتهيئة عبر:
```
TELEHEALTH_ADMIN_EMAIL
TELEHEALTH_ADMIN_PASSWORD
```
(القيم الافتراضية `admin@telehealth.local` / `ChangeMe123!` — **لازم تتغير في أي بيئة production**.)

---

### 4. Docker Compose كان هيفشل في الاتصال بالداتابيز
`docker-compose.yml` بيحط `SPRING_PROFILES_ACTIVE=docker`، لكن `application.yml` كان فيه:
```yaml
url: jdbc:postgresql://localhost:5432/telehealth_db
```
جوه الكونتينر، `localhost` بيشاور على الكونتينر نفسه مش على سيرفس الـ postgres، ومفيش
`application-docker.yml` كان بيبدل القيمة دي.

**الحل:** الـ host بقى قابل للتهيئة عبر متغير بيئة:
```yaml
url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:telehealth_db}
```
و `docker-compose.yml` بقى بيمرر `DB_HOST: postgres` (اسم السيرفس).

---

## إضافات

- **اختبارات** (كانت المشروع بدون أي اختبار خالص):
  - `TeleHealthApplicationTests` — smoke test بيتأكد إن الـ Spring context بيشتغل كامل بدون
    أخطاء (كان هيمسك مشكلة الـ bean الناقص فورًا لو كانت موجودة).
  - `AuthApplicationServiceTest` — اختبارات وحدة لتسجيل الدخول (نجاح/فشل/إيميل مكرر).

- **CI/CD** — `.github/workflows/ci.yml`:
  - Job 1: `mvn clean verify` مع خدمات Postgres و RabbitMQ حقيقية (زي docker-compose بالظبط).
  - Job 2: بناء صورة Docker بنفس الـ Dockerfile، ورفعها لـ GitHub Container Registry
    (`ghcr.io`) تلقائيًا عند الـ push على `main`.
  - يشتغل على: push/PR لـ `main` و `develop`، وكمان يدوي (`workflow_dispatch`).

---

## قبل ما تشغّل المشروع

1. **مهم:** غيّر `TELEHEALTH_ADMIN_PASSWORD` و `JWT_SECRET` قبل أي نشر حقيقي — القيم
   الافتراضية للتطوير المحلي بس.
2. `docker compose up --build` هيشغل Postgres + RabbitMQ + التطبيق مع بعض، ويشتغلوا مع بعض
   صح دلوقتي بعد إصلاح مشكلة الـ `DB_HOST`.
3. تسجيل مريض/دكتور جديد دلوقتي محتاج حقل `password` (8 أحرف على الأقل) في جسم الطلب.
4. للحصول على JWT: `POST /api/v1/auth/login` بـ `{ "email": "...", "password": "..." }`.
5. **لم يتم تشغيل `mvn build` فعليًا** داخل بيئة الإصلاح (السحابة اللي بيشتغل فيها Claude
   مقفولة على Maven Central). أول build حقيقي هيحصل تلقائيًا على GitHub Actions أول ما ترفع
   الكود. لو ظهر أي خطأ compile بسيط، ابعتلي رسالة الخطأ وأنا أصلحه فورًا.
