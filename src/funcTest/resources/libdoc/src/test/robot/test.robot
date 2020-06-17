*** Settings ***
Documentation    Suite description

*** Keywords ***

Write to out
  [Documentation]  Write stuff to the output
  ...   Arguments:
  ...     - message:  The message to write.
  [Arguments]  ${message}
  Log to console  ${message}