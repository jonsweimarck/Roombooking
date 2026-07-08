# roombooking

Lärprojekt: Claude Code, Specification by Example och ett CI/CD-flöde som gör
det enkelt att flytta deployment mellan molnplattformar. Domänen (bokning av
studierum) är medvetet liten - fokus ligger på processen, inte appen.

## Arkitektur

Lagren är ordnade enligt DDD/hexagonal:

```
domain/          Rena domänobjekt, inga ramverksberoenden (Room, TimeSlot, Booking)
application/     Use cases och portar (BookingService, *Repository-interfaces)
infrastructure/  Adaptrar som implementerar portarna (just nu: in-memory)
web/             Tunt HTTP-lager (Controller + Thymeleaf/htmx)
```

`infrastructure` innehåller just nu bara in-memory-implementationer. Det är
ett medvetet val: vi bygger inte persistens förrän ett scenario kräver det
(t.ex. "bokningar ska överleva en omstart"). Nästa steg blir en
Postgres-baserad implementation, testdriven mot en riktig databas med
Testcontainers - inte mockad.

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

```
mvn spring-boot:run
```

Öppna http://localhost:8080 - formuläret postar via htmx utan sidladdning.

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

- [ ] Fler scenarier: öppettider, bokning bakåt i tiden, avbokning
- [ ] Postgres-adapter för `RoomRepository`/`BookingRepository`, testad med Testcontainers
- [ ] Ersätt in-memory-adaptrarna i produktionskonfigurationen
- [ ] Konkret deploy-steg i CI mot första molnplattformen (förslag: Fly.io - enklast att komma igång med)
- [ ] Andra molnplattformen för att verifiera portabiliteten (t.ex. Kubernetes-manifest)
