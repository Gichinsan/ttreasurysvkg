# ttreasurysvkg

Team Treasury System für kleine Fußballmannschaften.

## Überblick

Dieses Projekt ist eine Webanwendung zur Verwaltung von Mannschaftsfinanzen, Teammitgliedern, Trainern, Trikotsätzen und Turnierplanung. Die Anwendung ist mit Spring Boot umgesetzt und bietet eine einfache Oberfläche für organisatorische Aufgaben im Vereinsalltag.

## Hauptfunktionen

- Verwaltung von Benutzerkonten und Rollen
- Verwaltung von Konten und Transaktionen der Mannschaftskasse
- Teamverwaltung mit Mitgliederlisten
- Verwaltung von Trainern und Trainingszeiten
- Erstellung und Pflege von Trikotsätzen
- Turnierplanung und Turnier-Übersichten
- Import und Export von Daten wie CSV, Excel und PDF
- Upload von Bildern und anderen Dateien

## Rollen und Berechtigungen

### Admin
- Verwalten von Nutzern und Rollen
- Zuweisen von Berechtigungen für andere Nutzer

### Kassenwart
- Zugriff auf allgemeine Verwaltung und Mannschaftskasse
- Verwaltung von Transaktionen und Konten
- Bei der Registrierung wird automatisch eine eindeutige Kontonummer erzeugt

### Trainer
- Zugriff auf Trikotsätze, Teamübersicht, Turnierplanung und Trainingsmeldungen

## Technischer Stack

- Java 21
- Spring Boot 3.4.3
- Spring MVC und Thymeleaf
- Spring Security
- Spring Data JPA
- Apache Derby als eingebettete Datenbank
- iText für PDF-Erzeugung
- Apache POI für Excel-Operationen
- Thumbnailator für Bildverarbeitung

## Voraussetzungen

- JDK 21 oder höher
- Maven oder die mitgelieferte Maven-Wrapper-Datei

## Ausführen der Anwendung

Mit Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Oder im gebauten Zustand:

```bash
./mvnw clean package
java -jar target/ttreasurysvkg-1.9.1.jar
```

## Konfiguration

- Die Anwendung läuft standardmäßig auf Port 9014
- Die Datenbank ist als eingebettete Derby-Datenbank konfiguriert
- Die lokalen Daten liegen im Ordner database
- Die Standard-Sprachdateien befinden sich unter src/main/resources

## Projektstruktur

- src/main/java: Backend-Logik, Controller und Services
- src/main/resources/templates: Thymeleaf-Templates
- src/main/resources/static: statische Dateien wie CSS, JavaScript und Bilder
- src/test/java: Tests

## Hinweise

Die Anwendung ist bewusst auf den Einsatz in kleinen Fußballteams ausgelegt und bietet eine einfache, aber funktionale Verwaltungslösung für den täglichen Betrieb.
