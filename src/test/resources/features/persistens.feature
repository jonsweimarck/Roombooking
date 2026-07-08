# language: sv
Egenskap: Bokningar överlever en omstart
  Som verksamhetsägare vill jag att bokningar sparas varaktigt
  så att de inte försvinner om applikationen startas om

  Scenario: Bokningar överlever en omstart av applikationen
    Givet att ett rum "R204" har en bokning
    När applikationen startas om
    Så ska bokningen fortfarande finnas för "R204"