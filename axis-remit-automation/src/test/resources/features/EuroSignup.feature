Feature: Signup functionality

  Scenario: User successfully signs up and verifies account

    Given user opens the axis remit page
    When user clicks on signup button
    And user scrolls down
    And user selects country
    And user selects euro country
    And user enters name
    And user enters email
    And user enters password
    And user selects country code
    And user enters mobile number
    And user selects option
    And user selects nationality
    And user checks receive mail checkbox
    And user accepts terms and conditions
    And user clicks on register button
    And user enters OTP
    Then user clicks on verify button