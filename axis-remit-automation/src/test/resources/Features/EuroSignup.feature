Feature: Signup functionality

  Scenario Outline: User successfully signs up and verifies account
    Given user opens the axis remit page "<url>"
    When user clicks on "<signup_button>"
    And user scrolls down
    And user selects country "<country>"
    And user selects euro country "<euroCountry>"
    And user enters name "<name>"
    And user enters email "<email>"
    And user enters password "<password>"
    And user selects country code "<countryCode>"
    And user enters mobile number "<mobile>"
    And user selects option "<option>"
    And user selects nationality "<nationality>"
    And user checks receive mail checkbox
    And user accepts terms and conditions
    And user clicks on "<register_button>"
    And user enters OTP "<otp>"
    Then user clicks on "<verify_button>"

    Examples:
      | url                        | signup_button | country  | euroCountry | name       | email               | password      | countryCode   | mobile     | option | nationality | register_button | otp    | verify_button  |
      | https://qaonerxm.remit.in/ | SIGN UP       | Eurozone | Austria     | Atell Bijy | atell11@yopmail.com | Test@12345678 | 49 (Eurozone) | 7845673245 | No     | Indian      | REGISTER NOW    | 123456 | VERIFY ACCOUNT |
