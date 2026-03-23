Feature: Login Functionality

Scenario: Login and Logout with valid credentials
    Given user launched the browser
    When user opens the URL "http://admin-demo.nopcommerce.com/"
    And user enters email "admin@yourstore.com" and password "admin"
    And clicks on the login button
    Then the page title should be "Dashboard / nopCommerce administratio"

    When user clicks on the logout button
    Then page title should be "nopCommerce demo store. Login"

    And close the browser
    
    