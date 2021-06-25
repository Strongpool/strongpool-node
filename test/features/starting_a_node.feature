Feature: Starting a node

  Scenario: Starting a node with no arguments
    Given './spnctl start' was run
    Then the 'arweave' service should be running
