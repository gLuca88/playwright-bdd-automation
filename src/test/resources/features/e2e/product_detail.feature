@product-detail
Feature: Dettaglio prodotto

  Come visitatore
  Voglio visualizzare tutti i dettagli di un prodotto
  In modo da ottenere maggiori informazioni prima di decidere di acquistarlo

  Background:
    Given il visitatore si trova nella home page


  @regression
  Scenario: Visualizzazione delle informazioni del prodotto
    Given il visitatore si trova nella pagina di dettaglio di un prodotto
    Then viene visualizzata l'immagine del prodotto
    And viene visualizzato il nome del prodotto
    And viene visualizzata la descrizione del prodotto
    And viene visualizzato il prezzo del prodotto
    And viene visualizzato il badge della categoria
    And viene visualizzato il badge del brand


  @regression @navigation
  Scenario: Visualizzazione e navigazione dei prodotti correlati
    Given il visitatore si trova nella pagina di dettaglio di un prodotto
    Then viene visualizzata la sezione dei prodotti correlati
    And viene visualizzato almeno un prodotto correlato
    When il visitatore seleziona un prodotto correlato
    Then viene visualizzata la pagina di dettaglio del prodotto correlato