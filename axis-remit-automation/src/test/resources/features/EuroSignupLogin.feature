Feature: Eurozone Corridor - Signup and Login End-to-End

  Scenario: User successfully signs up, verifies account and logs in

    # Launch application
    Given user opens the axis remit page

    # -------- SIGNUP FLOW --------
    When user clicks on signup button
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
    And user enters OTP for signup
    And user clicks on verify button for signup
    Then user should see registration success message

    # -------- LOGIN FLOW --------
    When user navigates to sign in page
    And user enters registered email
    And user enters registered password
    And user clicks on login button
    Then user should receive OTP
    When user enters OTP for login
    And user clicks on verify button for login
    Then user should be logged in successfully