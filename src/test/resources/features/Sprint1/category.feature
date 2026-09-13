@category
Feature: Navigazione prodotti per categoria

  Come visitatore
  Voglio visualizzare i prodotti appartenenti a una specifica categoria
  In modo da poter trovare prodotti di una determinata tipologia

  Background:
    Given il visitatore si trova nella home page


  @smoke @navigation
  Scenario: Navigazione a una categoria
    When il visitatore seleziona una categoria
    Then viene visualizzata la pagina della categoria selezionata


  @regression
  Scenario: Visualizzazione del nome della categoria
    Given il visitatore si trova nella pagina di una categoria
    Then il nome della categoria selezionata viene visualizzato come titolo della pagina


  @regression
  Scenario: Visualizzazione dei prodotti della categoria
    Given il visitatore si trova nella pagina di una categoria
    Then vengono visualizzati solo prodotti appartenenti alla categoria selezionata