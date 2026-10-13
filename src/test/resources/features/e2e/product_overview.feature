@product-overview
Feature: Panoramica prodotti

  Come visitatore
  Voglio visualizzare una panoramica di tutti i prodotti disponibili
  In modo da poter consultare il catalogo e trovare prodotti di mio interesse

  Background:
    Given il visitatore si trova nella home page

  @smoke
  Scenario: Visualizzazione del catalogo prodotti
    Then viene visualizzata la griglia dei prodotti
    And  viene visualizzato almeno un prodotto


  @regression
  Scenario: Visualizzazione delle informazioni dei prodotti
    Given il catalogo prodotti è visualizzato
    Then  ogni prodotto visualizza un'immagine
    And   ogni prodotto visualizza il nome
    And   ogni prodotto visualizza il prezzo

  @smoke @navigation
  Scenario: Navigazione al dettaglio di un prodotto
    Given il catalogo prodotti è visualizzato
    When il visitatore seleziona un prodotto
    Then viene visualizzata la pagina di dettaglio del prodotto selezionato
