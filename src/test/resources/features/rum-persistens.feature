# language: sv
Egenskap: Rum överlever en omstart

  Scenario: Rum överlever en omstart av applikationen
    Givet att rummet "R204" finns
    När applikationen startas om
    Så ska rummet "R204" fortfarande gå att boka
