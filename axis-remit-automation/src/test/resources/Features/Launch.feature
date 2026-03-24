Feature: Launch Website

  Scenario: Open Remit Website
    Given user launched the browser
    When user opens the URL "https://qaonerxm.remit.in/#/"
    Then page title should be displayed
    And close the browser