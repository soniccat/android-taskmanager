Feature: Login feature

  Scenario: At the begining I should see Rss Client
    Then I should see "Rss Client"

  Scenario: As a user I want to see an error box with the ok button after opening Rss Client
    When I press "Rss Client"
    Then I should see "Ok"

  Scenario: As a user I want to be able see owerflow menu in Rss Client
    When I press "Rss Client"
    Then I should see "Ok"
    Then I press "Ok"
    Then I see object "OverflowMenuButton"