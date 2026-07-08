# language: sv
Egenskap: Administrera rum
  Som administratör vill jag kunna lägga till nya rum via admin-sidan
  så att studenter kan boka dem

  Scenario: Administratör lägger till ett nytt rum
    Givet att rummet "R205" inte finns sedan tidigare
    När administratören lägger till rummet "R205"
    Så ska rummet "R205" gå att boka
