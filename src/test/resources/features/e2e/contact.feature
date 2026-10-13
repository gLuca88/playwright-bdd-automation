@contact
Feature: Modulo di contatto

  Come visitatore
  Voglio inviare un messaggio tramite il modulo di contatto
  In modo da poter richiedere assistenza o altre informazioni

  Background:
    Given il visitatore si trova nella pagina dei contatti

  @smoke
  Scenario: Visualizzazione del modulo di contatto
    Then viene visualizzato il modulo di contatto

  @regression
  Scenario: Visualizzazione dei campi obbligatori
    Then vengono visualizzati tutti i campi obbligatori del modulo di contatto

  @regression @validation
  Scenario: Validazione dei campi obbligatori
    When il visitatore invia il modulo di contatto senza compilare i campi obbligatori
    Then viene visualizzato un errore per ogni campo obbligatorio


  @regression @validation
  Scenario Outline: Validazione del formato email
    Given il visitatore compila gli altri campi obbligatori con dati validi
    When inserisce nel campo email "<email>"
    And invia il modulo di contatto
    Then l'esito della validazione email è "<esito>"

    Examples:
      | email                     | esito      |
      | testuser                  | non valida |
      | testuser@                 | non valida |
      | @example.com              | non valida |
      | test user@example.com     | non valida |
      | testuser@example.com      | valida     |
      | test.user@example.com     | valida     |
      | test+user@example.com     | valida     |
      | testuser@mail.example.com | valida     |

  @regression
  Scenario: Visualizzazione delle opzioni del subject
    Then il subject contiene tutte le opzioni previste

  @regression @validation
  Scenario Outline: Validazione della lunghezza minima del messaggio
    Given il visitatore compila i campi obbligatori escluso il messaggio con dati validi
    When inserisce un messaggio di <lunghezza> caratteri
    And invia il modulo di contatto
    Then l'esito della validazione del messaggio è "<esito>"

    Examples:
      | lunghezza | esito      |
      | 10        | non valido |
      | 49        | non valido |
      | 50        | valido     |
      | 51        | valido     |
      | 60        | valido     |

  @smoke @regression
  Scenario: Invio corretto del modulo di contatto
    Given il visitatore compila tutti i campi obbligatori con dati validi
    When invia il modulo di contatto
    Then viene visualizzato un messaggio di conferma
    And il modulo di contatto non viene più visualizzato