Feature: German Customer Registration

  As a German customer
  I want to register on the application
  So that I can create my account and access remittance services securely

  Scenario: User successfully completes German customer registration

    # Launch registration page
    Given user navigates to German registration page

    # -------- REGISTRATION FLOW --------
    When user clicks on German Block Account Type dropdown
    And user selects German Opportunity value from dropdown

    And user enters German PAN number
    Then user should see German OTP popup message
    And user clicks on German OK button on popup

    When user enters German OTP for PAN verification
    And user clicks on German Verify button for PAN verification

    And user enters German email address
    And user clicks on German Get OTP button

    Then user should receive German OTP for email verification
    And user clicks on German Verify button for email verification

    And user enters German password
    And user enters German confirm password

    And user selects German date of birth
    And user enters German address as "Mumbai"

    And user enters German passport number as "A09032"
    And user selects German passport expiry date

    And user selects German state from state dropdown
    And user selects German city from city dropdown

    And user enters German PIN code
    And user selects German account number from account number dropdown

    And user enters German first name
    And user enters German last name
    And user enters German middle name

    And user enters German mobile number
    And user enters German emergency email address

    And user clicks on German terms and conditions checkbox
    And user clicks on German I Accept button
    And user clicks on German Next button

    And user clicks on German Click Here button
    And user uploads German file using Robot class through Choose File option
    And user clicks on German Continue button

    Then user should see German popup message
    And user clicks on German OK button on popup

    And user clicks on German Final Next button

    Then user should see German successful message "Your registration was successful!"