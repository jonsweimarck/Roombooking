# language: sv
# Detta är startpunkten för vårt Specification by Example-arbete.
# Fler scenarier (öppettider, bokning bakåt i tiden, avbokning m.m.)
# läggs till här allteftersom vi kommer överens om dem.

Egenskap: Boka studierum
  Som student vill jag boka ett ledigt rum
  så att jag har en garanterad plats att studera på

  Scenario: Lyckad bokning av ledigt rum
    Givet att rum "R204" är ledigt
    När Alva bokar "R204" mellan 10:00 och 11:00 på fredag
    Så ska bokningen bekräftas
    Och rummet ska visas som upptaget mellan 10:00 och 11:00 på fredag

  Scenario: Avslå överlappande bokning
    Givet att rum "R204" redan är bokat mellan 10:00 och 11:00 på fredag
    När Björn försöker boka "R204" mellan 10:30 och 11:30 på fredag
    Så ska bokningen avslås med anledningen "Överlappande bokning"
