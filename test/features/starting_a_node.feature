Feature: Starting a node

  Scenario: Starting a node with no arguments
    When I run './spnctl start'
    Then the node starts
