# language: sv
# Detta är startpunkten för vårt Specification by Example-arbete.
# Rum saknar öppettider - de går att boka dygnet om. Fler scenarier
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

  Scenario: Avslå bokning bakåt i tiden på innevarande dag
    Givet att klockan är 14:00 på onsdag
    När Alva försöker boka "R204" mellan 10:00 och 11:00 på onsdag
    Så ska bokningen avslås med anledningen "Kan inte boka bakåt i tiden"

  Scenario: Tillåt bokning av tidigare veckodag - avser nästa vecka
    Givet att klockan är 14:00 på onsdag
    När Alva bokar "R204" mellan 10:00 och 11:00 på måndag
    Så ska bokningen bekräftas
