Feature: Detail TimeSlot Screen

  Scenario: save a TimeSlot data
    Given the detail screen is shown
    And input all data
    And click "Save" button
    Then the detail screen is closed
    And the saved data is shown in the TimeSlot list