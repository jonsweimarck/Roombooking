# Kontext för Claude Code

Detta är ett lärprojekt: målet är att öva på flödet Claude Code → Specification
by Example → CI/CD → deployment på olika molnplattformar. Se @README.md för
arkitektur och arbetsprocess.

## Arbetsprocess (följ denna ordning)

1. Nya krav formuleras alltid som Gherkin-scenarier i
   `src/test/resources/features/` **tillsammans med utvecklaren** innan någon
   kod skrivs eller ändras.
2. Acceptanstest (Cucumber) kopplas direkt mot applikationslagret
   (`BookingService` eller motsvarande use case), inte mot isolerad domänlogik.
3. Enhetstester driver fram detaljerna i domänlagret.
4. Bygg bara det ett skrivet scenario faktiskt kräver - inga oanvända
   abstraktioner "för säkerhets skull".

## Kodstil

- Enkel, elegant Java. Hellre `record` och `sealed interface` än
  ceremoni-tunga klasshierarkier.
- Domänlagret (`domain/`) har inga ramverksberoenden.
- Frontend ska vara så enkel som möjligt - htmx + server-renderad HTML
  (Thymeleaf), inte en SPA-stack, om inget annat uttryckligen motiveras.
- Webblagret testas med `@WebMvcTest` + `MockMvc` mot ett `@MockBean`-stubbat
  servicelager (se `BookingControllerTest`/`AdminControllerTest`) - det som
  verifieras är den faktiskt renderade HTML:en (formulärfält, htmx-attribut,
  resultatfragment), inte bara `Model`-attribut. Affärslogiken testas separat
  i `*ServiceTest`/feature-scenarierna.

## Domänmodell - viktiga vägval

- **`TimeSlot` har inget kalenderdatum** - bara `DayOfWeek` + `LocalTime`.
  Bokningar är återkommande veckoslots, inte bokningar på ett specifikt
  datum. Det är ett medvetet val (litet lärprojekt, ingen anledning till
  kalenderkomplexitet) - inför inte ett riktigt datum utan att stämma av det
  först, eftersom det påverkar `TimeSlot`, `Booking` och alla Gherkin-steg
  som skriver veckodag istället för datum.
- **"Bakåt i tiden" är därför relativt till innevarande vecka**, inte ett
  absolut datum: `TimeSlot.hasPassed(nu, nuTid)` avslår bara en bokning om
  det är *samma* veckodag och starttiden redan är förbi. En tidigare
  veckodag (t.ex. boka måndag när det är onsdag) avser nästa förekomst,
  nästa vecka, och räknas inte som passerad. Se `bokning.feature` för de
  scenarier som pinnar fast det.
- Rum har inga öppettider - de är bokningsbara dygnet om. Beslutat
  medvetet, inget scenario för det ska läggas till.

## Säkerhet

- **`/admin/**` kräver HTTP Basic-inloggning** via `SecurityConfig` (en
  vanlig `SecurityFilterChain`-böna) - en admin-användare, lösenord från
  `roombooking.admin.password`/miljövariabeln `ROOMBOOKING_ADMIN_PASSWORD`.
  Valt medvetet framför en egen inloggningssida: att senare byta
  `httpBasic()` mot `oauth2Login()`/`oauth2ResourceServer()` blir då en
  omkonfigurering av samma filterkedja, inte en omskrivning - så länge vi
  håller oss till Spring Securitys standardmönster istället för att
  handrulla egna filter.
- CSRF är avstängt globalt - htmx-formulären skickar ingen CSRF-token, och
  autentiseringen är stateless Basic-auth per anrop, inte en inloggad
  session som CSRF-skyddet är till för.

## Deploy

- **Molnplattform: Clever Cloud** (valt framför Fly.io). Se README:s "Deploy
  till Clever Cloud" för detaljer och återstående steg.
- **Ingen Docker för den här deployen** - medvetet valt bort. Clever Cloud
  bygger alltid själv från en `Dockerfile` vid `git push` (går inte att bara
  peka på en redan publicerad image), vilket hade krävt registry-synlighet,
  inloggningsvariabler och hantering av en kapplöpning mellan CI:s
  image-publicering och Clever Clouds egen build - för många kopplingar
  utanför koden för vad det är värt. Använder istället Clever Clouds
  inbyggda Java/Maven-stöd (bygger direkt från källkoden). Jib/Docker
  (`pom.xml`, CI:s `publish-image`-jobb) finns kvar för lokal körning och en
  eventuell andra molnplattform, inte för Clever Cloud-deployen.
- **Appen är länkad direkt mot GitHub-repot i Clever Clouds konsol** - inte
  triggad från CI. Det gick inte att länka GitHub i efterhand på en app som
  redan skapats som "Brand new app" (git-push-baserad); det valet görs vid
  appskapandet. `.github/workflows/ci.yml` har därför inget `deploy`-jobb -
  Clever Cloud bygger och deployar helt oberoende vid varje push till
  `master`. Medveten avvägning: ingen testgrind före deploy, till skillnad
  från ett CI-triggat upplägg.
- `clevercloud/maven.json` (`{"deploy": {"goal": "spring-boot:run"}}`)
  krävs för att Clever Cloud ska veta hur appen ska *köras*, inte bara
  byggas - utan den misslyckas deployen efter en lyckad `mvn package` med
  "goal is missing for deploying with maven".
- **PostgreSQL-tillägget måste länkas till den specifika appen** ("Service
  dependencies" i konsolen) - följer inte med automatiskt om appen skapas
  om. Utan länkningen faller `application.yml`:s datasource tillbaka på
  `localhost:5432` (det lokala docker-compose-värdet) och appen kraschar
  vid uppstart med en JDBC-anslutningsfel som annars ser ut som ett
  kodproblem.
- Deployen är **verifierad fungerande** (2026-07-11): riktig Postgres,
  GitHub-länkad autodeploy, `spring-boot:run` via `clevercloud/maven.json`.

## Kända fällor i det här projektet (redan lösta - undvik att återintroducera)

- **HikariCPs standardpoolstorlek (10) är för stor för Clever Clouds
  DEV-nivå av Postgres**, som har ett lågt anslutningstak - appen kraschar
  vid uppstart med "FATAL: too many connections for role ...". Extra
  lömskt vid omstart, då gammal och ny instans kan vara igång samtidigt en
  kort stund och dubblar antalet anslutningar. Löst genom
  `spring.datasource.hikari.maximum-pool-size: 3` i `application.yml` -
  gott och väl för den här lilla appens behov.
- **Surefire vs. Failsafe**: enhetstester heter `*Test.java` (körs av
  Surefire i `test`-fasen). Acceptanstester heter `*IT.java` (körs av
  Failsafe, kräver `mvn verify`). Fel namnmönster = testet körs aldrig, utan
  felmeddelande.
- **Gherkin på svenska kräver `# language: sv`** som absolut första rad i
  varje `.feature`-fil, annars misslyckas parsern med kryptiska
  "expected #FeatureLine"-fel.
- `junit-platform-suite-engine` måste vara ett explicit beroende, inte bara
  `junit-platform-suite` (annars kan `@Suite`-motorn saknas vid discovery).
- **Mockito + nya JDK-versioner**: `@MockBean`/`Mockito.mock(...)` på en
  konkret klass kan misslyckas med "Byte Buddy could not instrument all
  classes" om den lokala JDK:n är nyare än vad Spring Boot-BOM:ens
  `mockito.version` stödjer. Löst genom att låsa `mockito.version` och
  `net.bytebuddy:byte-buddy(-agent)` till nyare versioner i `pom.xml` - annars
  fungerar inga Mockito-mockar av konkreta klasser alls i webbslice-tester
  (`@WebMvcTest`). CI kör Java 21 där detta inte är trasigt - felet dyker
  bara upp lokalt om `JAVA_HOME` pekar på en nyare JDK än vad byte-buddy
  hunnit stödja.
- **`BookingService.book(...)` avslår bokningar bakåt i tiden relativt en
  injicerad `Clock`** (se ovan under Domänmodell). Alla ställen som
  konstruerar `BookingService` direkt i tester (`BookingSteps`,
  `RoomAdminSteps`, `AvbokningSteps`, `BookingServiceTest`) måste därför
  använda en **fast** `Clock.fixed(...)`, aldrig systemklockan - annars blir
  testerna flakiga beroende på vilken veckodag/tid de faktiskt körs.
  Cucumber-scenarier som går via Spring-kontexten (persistensscenarierna)
  får sin fasta klocka från `CucumberSpringConfiguration.FastKlockaFörTester`,
  som med `@Primary` skuggar produktionens `Clock`-böna
  (`Clock.systemDefaultZone()` i `RoomBookingApplication`).
- **`@WebMvcTest`-slice-tester ser inte `SecurityConfig` automatiskt.**
  Utan `@Import(SecurityConfig.class)` slår Spring Boots egen
  standardsäkerhet in istället - den kräver autentisering på *allt* bakom
  ett slumpat genererat lösenord - och redan gröna kontrollertester börjar
  plötsligt få 401. Se `BookingControllerTest`/`AdminControllerTest` för
  mönstret; gäller varje ny `@WebMvcTest`-klass.

## Nästa steg

Se README.md:s "Nästa steg"-sektion - hålls bara på ett ställe för att
undvika att de driver isär (vilket redan hänt en gång).
