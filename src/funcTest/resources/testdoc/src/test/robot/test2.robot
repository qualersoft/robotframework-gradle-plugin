*** Settings ***
Documentation    Suite description for test2

*** Test Cases ***
Create some more output test
  [Documentation]  This is another simple documentation
  Write to out  "This is a test message"

*** Keywords ***
Write to out
  [Documentation]  Write stuff to the output
  ...   Arguments:
  ...     - message:  The message to write.
  [Arguments]  ${message}
  Log to console  ${message}