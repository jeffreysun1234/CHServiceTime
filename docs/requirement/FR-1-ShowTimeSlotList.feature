Feature: Show TimeSlot List UI

  Background:
    Given the app is started.

  Scenario: show TimeSlot list without data
    When there is not data
    Then show "No Data" in the list area

  Scenario: show TimeSlot list with data
    When there is some data
    Then show TimeSlot list

  Scenario: add TimeSlot
    When an "Add" icon is shown on the screen
    And click the "Add" icon
    Then show a empty detail TimeSlot screen
    And "Delete" button is not shown on the detail screen
    And show the current time (hour and minute)

  Scenario: edit TimeSlot
    Given there are some items in the TimeSlot list
    And LONG click a item
    Then show a detail TimeSlot screen with data
    And all data is correctly shown
