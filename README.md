# Farmer and Goat Registration System

A small Java full-stack project for keeping farmer contact details, goats, and vaccination history in one MySQL database. It uses Spring Boot, Spring MVC, Spring Data JPA, Spring Security, Thymeleaf, HTML/CSS/JavaScript, and Maven.

## 1. What you need

- Java Development Kit (JDK) 21
- Apache Maven 3.6.3 or later
- MySQL Server 8 or later, running on this computer

Open PowerShell and check the tools:

```powershell
java -version
mvn -version
```

The workspace used to prepare this project has Java 21. Maven was downloaded temporarily for verification, and no MySQL server was listening locally, so the build and app flow were tested with the test-only H2 database. Connect to your own MySQL Server using the steps below before presenting the MySQL-backed version.

If Maven is missing, install it with Chocolatey (`choco install maven`) or Scoop (`scoop install main/maven`). You can also download the binary archive from the [official Maven download page](https://maven.apache.org/download.cgi), extract it, and add its `bin` folder to your Windows `PATH`. Open a new PowerShell window and confirm with `mvn -version`.

## 2. Create the database

If MySQL is not installed, download MySQL Community Server for Windows from the [official MySQL downloads page](https://dev.mysql.com/downloads/mysql/). Run the MSI installer and MySQL Configurator, set up a server on port `3306`, and choose a root password you can remember. MySQL's [Windows installation guide](https://dev.mysql.com/doc/refman/8.4/en/windows-installation.html) walks through the server setup. Install MySQL Workbench too if you want a graphical place to run SQL.

Start MySQL Server. In MySQL Workbench, open a query window and run the SQL in `database/setup.sql`. The only SQL setup needed is creation of the empty database; when the app starts, JPA creates the tables from the Java entity classes.

The default connection is:

- Database: `farmer_goat_register`
- Server: `localhost:3306`
- Username: `root`
- Password: empty

If your MySQL root account has a password, use the PowerShell variables below. Do not put your real password in this project or commit it.

## 3. Run the application

In PowerShell, move to the project folder, set the local MySQL username and password, then run Spring Boot:

```powershell
cd "D:\Farmer and Goat Register"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-mysql-password"
mvn spring-boot:run
```

Replace `your-local-mysql-password` with the password configured on your own MySQL installation. If your MySQL account has no password, use `$env:DB_PASSWORD = ""`. Keep this PowerShell window open while using the website. The first start downloads Java libraries through Maven and may take a few minutes. When the log says the application has started, visit <http://localhost:8080>.

If `mvn` is not recognized, install Apache Maven, close and reopen PowerShell so its PATH refreshes, then check `mvn -version` again. Maven's official installation page is <https://maven.apache.org/install.html>.

To run the automated application flow check (it uses a temporary in-memory database and does not need MySQL):

```powershell
mvn test
```

To stop the server, focus the PowerShell window and press `Ctrl+C`.

## 4. Demo sign-in

The first time the application starts, it adds example data to an empty database and creates this demonstration account:

- Username: `demo`
- Password: `demo12345`

The demo password is for a local classroom presentation only. The stored password is hashed with BCrypt. To choose a different local demo password before the first start, set `$env:APP_DEMO_PASSWORD = "your-demo-password"`. The example user is created only when it does not already exist, so changing this variable later does not reset an existing account's password.

The sample farmers, goats, and vaccination dates are generated at startup, not stored as SQL inserts. If you want to reload the sample records, use an empty database. Back up any real records before dropping a database.

## 5. Project map

```text
src/main/java/com/farmgoat/registration/
  FarmerGoatRegistrationApplication.java  starts Spring Boot
  config/                                  login rules and demo-data setup
  model/                                   Farmer, Goat, Vaccination, AppUser database entities
  repository/                              Spring Data interfaces for MySQL reads and writes
  web/                                     page controllers and request handling
  web/form/                                validated form fields sent by the website
src/main/resources/
  application.properties                  MySQL and application settings
  templates/                               Thymeleaf HTML pages
  static/css/styles.css                   responsive green theme
  static/js/app.js                        small browser-side form and message behavior
database/setup.sql                         creates the empty MySQL database
pom.xml                                    Maven project and dependency list
```

### How the pieces fit together

1. A visitor submits a login form. Spring Security checks the username and compares the entered password with the BCrypt hash saved in `app_users`.
2. A controller receives a page request. For example, `FarmerController` handles farmer pages and checks the form values.
3. The controller calls a repository. Spring Data JPA turns calls such as `findAll()` into database queries.
4. A model class such as `Farmer` maps to a database table. One farmer can have many goats; each goat can have many vaccination records.
5. Thymeleaf combines the returned data with HTML templates and sends a complete page to the browser.

The separate `FarmerForm`, `GoatForm`, and `VaccinationForm` classes hold only the values a user may enter. This keeps form validation simple and prevents the browser from changing database relationships directly.

## 6. Feature checklist

- Sign in and sign out: `/login`; sign-out button in the top bar.
- Dashboard totals: farmer, goat, and vaccination record counts.
- Add and view farmers: Farmers → Add farmer; open any farmer for the profile.
- Register multiple goats: use “Register a goat” from a farmer profile.
- Search: farmer list searches name, phone, and village; goat list searches tag, name, breed, and farmer.
- Goat detail: includes tag, breed, gender, date of birth, farmer contact/location, and vaccination history.
- Add vaccination: from a goat profile; the server validates the dates.
- Responsive green website: pages adapt to mobile and desktop widths.
- MySQL persistence: entity tables are created automatically when the app connects.

## 7. Troubleshooting

- **`mvn` is not recognized:** install Maven and reopen PowerShell.
- **Connection refused:** make sure MySQL Server is running and listening on port 3306.
- **Access denied for user:** set the correct `DB_USERNAME` and `DB_PASSWORD` in the same PowerShell window before starting the app.
- **Unknown database:** run `database/setup.sql` in MySQL Workbench.
- **Port 8080 is already in use:** stop the other app using that port, or run `$env:SERVER_PORT = "8081"` before `mvn spring-boot:run`, then open <http://localhost:8081>.
- **Login keeps failing:** use the demo account above. If that account was first created with a different `APP_DEMO_PASSWORD`, its stored password does not change automatically.

This project is intended as a beginner-friendly internship demo. For a real production deployment, configure a strong account, switch off demo-data creation, use a restricted MySQL account, and review deployment security settings.

## 8. Short presentation explanation

> “This project is a Farmer and Goat Registration System built with Java and Spring Boot. It lets a staff member sign in, register farmers, add several goats under each farmer, and record vaccination dates. Spring MVC handles the website pages, Spring Data JPA connects the Java records to MySQL tables, and Thymeleaf displays the data in the browser. Spring Security protects the pages and stores passwords as BCrypt hashes. The dashboard counts the records, and a goat profile brings its owner details and vaccination history together.”
