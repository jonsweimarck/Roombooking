# roombooking

Lärprojekt: Claude Code, Specification by Example och ett CI/CD-flöde som gör
det enkelt att flytta deployment mellan molnplattformar. Domänen (bokning av
studierum) är medvetet liten - fokus ligger på processen, inte appen.

## Arkitektur

Lagren är ordnade enligt DDD/hexagonal:

```
domain/          Rena domänobjekt, inga ramverksberoenden (Room, TimeSlot, Booking)
application/     Use cases och portar (BookingService, *Repository-interfaces)
infrastructure/  Adaptrar som implementerar portarna (in-memory + JPA/Postgres)
web/             Tunt HTTP-lager (Controller + Thymeleaf/htmx)
```

Både `BookingRepository` och `RoomRepository` har nu Postgres-baserade
implementationer (`JpaBookingRepository`/`JpaRoomRepository`), testdrivna mot
en riktig databas med Testcontainers - inte mockade (se `persistens.feature`
och `rum-persistens.feature`).

## Arbetsprocess

1. **Specification by Example** - `src/test/resources/features/bokning.feature`
   är sanningen om vad systemet ska göra. Nya scenarier skrivs tillsammans
   innan någon kod ändras.
2. **Acceptanstest** - Cucumber-stegen i `acceptance/` kopplar Gherkin direkt
   till applikationslagret (`BookingService`), så att testerna verkligen
   verifierar användarresan, inte bara isolerad logik.
3. **Enhetstest** - driver fram detaljerna i domänlagret (t.ex.
   `TimeSlot.overlaps`).
4. **CI/CD** - varje push kör `mvn verify` (enhets- + acceptanstester), bygger
   sedan en OCI-image med Jib (ingen Dockerfile) och publicerar den. Samma
   image kan köras på vilken container-plattform som helst.

### Varför två testfaser?

- **Enhetstester** (`*Test.java`) körs av Surefire i `test`-fasen -> `mvn test`
- **Acceptanstester** (`*IT.java`, t.ex. `CucumberIT`) körs av Failsafe i
  `integration-test`/`verify`-fasen -> kräver `mvn verify`

Detta är en vanlig Maven-fälla: Surefire matchar bara `*Test.java`/`*Tests.java`
och ignorerar tyst allt annat, utan att felmeddela. Genom att döpa
acceptanstesterna till `*IT.java` och binda Failsafe till dem blir
testpyramidens nivåer synliga direkt i build-livscykeln istället för att bara
finnas i mappstrukturen.

## Köra lokalt

Produktionskonfigurationen kräver en riktig Postgres (se `application.yml`).
Starta den med docker-compose innan appen startas:

```
docker compose up -d
mvn spring-boot:run
```

Öppna http://localhost:8080 - formuläret postar via htmx utan sidladdning.

Databasen är tom från början, så en bokning mot t.ex. "R204" avslås med
"Rummet finns inte" tills rummet finns i tabellen `room_entity`. Lägg till
det via admin-sidan på http://localhost:8080/admin/rum - den kräver HTTP
Basic-inloggning (`admin` / lösenordet i `ROOMBOOKING_ADMIN_PASSWORD`,
default `admin` lokalt).

## Köra tester

```
mvn verify
```

Kör både enhetstester (JUnit 5 + AssertJ) och acceptanstester (Cucumber, via
`CucumberTestRunner`).

## Bygga container-image lokalt

```
IMAGE_REGISTRY=localhost IMAGE_TAG=dev mvn compile jib:build -Djib.to.image=roombooking:dev
```

(Kräver Docker-daemon eller registry-åtkomst beroende på Jib-mål; se Jib-dokumentationen
för `jib:dockerBuild` om du bara vill bygga lokalt utan push.)

## Nästa steg (öppna för nästa session)

- [x] Postgres-adapter för `BookingRepository`, testad med Testcontainers
- [x] Postgres-adapter för `RoomRepository`, testad med Testcontainers
- [x] Ersätt in-memory-adaptrarna i produktionskonfigurationen
- [x] Admin-sida för att lägga till rum (`administrera-rum.feature`, `RoomAdminService`, `/admin/rum`)
- [x] Fler scenarier: bokning bakåt i tiden, avbokning (`avbokning.feature`) - öppettider behövs inte, rum är bokningsbara dygnet om
- [x] Autentisering för admin-sidan (`/admin/rum` kräver HTTP Basic-inloggning, se `SecurityConfig`)
- [ ] Konkret deploy-steg i CI mot första molnplattformen (förslag: Fly.io - enklast att komma igång med)
- [ ] Andra molnplattformen för att verifiera portabiliteten (t.ex. Kubernetes-manifest)
