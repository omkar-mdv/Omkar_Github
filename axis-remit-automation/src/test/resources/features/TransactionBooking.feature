Feature: Booking a Transaction - Send Money

  As a logged-in user
  I want to book a money transfer
  So that I can send money successfully

  Scenario: User logs in and books a transaction by adding a new receiver

    # ---------- LOGIN USING CREDENTIALS ----------
    Given user opens the axis remit page
    When user navigates to sign in page
    And user enters registered email
    And user enters registered password
    And user clicks on login button
    Then user should receive OTP
    When user enters OTP for login
    And user clicks on verify button for login
    Then user should be logged in successfully

    # ---------- TRANSACTION BOOKING ----------
    When user clicks on Send Money
    And user clicks on Add New Receiver
    Then user should be on Receiver Details page

    When user selects receiver type
    And user enters receiver name
    And user selects receiver bank
    And user enters receiver account number
    And user enters receiver address
    And user selects receiver state
    And user enters receiver city
    And user enters receiver pincode
    And user enters receiver mobile
    And user enters receiver email
    And user selects relation with receiver
    And user selects purpose of sending money
    And user checks Send Payment Alert checkbox
    And user enters a message
    And user clicks on Next Step