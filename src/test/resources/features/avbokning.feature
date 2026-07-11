# language: sv
Egenskap: Avboka rum
  Som student vill jag kunna avboka en bokning jag inte längre behöver
  så att rummet blir tillgängligt för andra

  Scenario: Avboka en bokning
    Givet att Alva har bokat "R204" mellan 10:00 och 11:00 på fredag
    När Alva avbokar bokningen
    Så ska rummet visas som ledigt mellan 10:00 och 11:00 på fredag
