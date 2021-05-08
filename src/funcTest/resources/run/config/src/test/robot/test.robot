*** Settings ***
Documentation    Suite description

*** Test Cases ***

A Test
  [Tags]  Ignore
  Log to console    Hello world

A second test
  Log to console    Goodbye
