*** Settings ***
Documentation    Suite description for test1

*** Test Cases ***
Create some output test
  [Documentation]  This is a simple documentation
  [Tags]  DocTag
  Write to out  "This is a test message"

*** Keywords ***
Write to out
  [Documentation]  Write stuff to the output
  ...   Arguments:
  ...     - message:  The message to write.
  [Arguments]  ${message}
  Log to console  ${message}