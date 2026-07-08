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

## Kända fällor i det här projektet (redan lösta - undvik att återintroducera)

- **Surefire vs. Failsafe**: enhetstester heter `*Test.java` (körs av
  Surefire i `test`-fasen). Acceptanstester heter `*IT.java` (körs av
  Failsafe, kräver `mvn verify`). Fel namnmönster = testet körs aldrig, utan
  felmeddelande.
- **Gherkin på svenska kräver `# language: sv`** som absolut första rad i
  varje `.feature`-fil, annars misslyckas parsern med kryptiska
  "expected #FeatureLine"-fel.
- `junit-platform-suite-engine` måste vara ett explicit beroende, inte bara
  `junit-platform-suite` (annars kan `@Suite`-motorn saknas vid discovery).

## Nästa steg (se även README)

- Fler scenarier: öppettider, bokning bakåt i tiden, avbokning
- Postgres-adapter för repositoryn, testad med Testcontainers (inte mockad)
- Konkret deploy-steg i `.github/workflows/ci.yml` mot första molnplattformen
