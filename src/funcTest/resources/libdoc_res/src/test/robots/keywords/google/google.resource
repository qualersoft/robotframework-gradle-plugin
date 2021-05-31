*** Settings ***
Resource  ../common.resource

*** Keywords ***
Do google search
  [Documentation]   Performs a google search with the given term
  [Arguments]   ${term}
  Input term    ${term}
  Execute search

The title should contain
  [Documentation]   Check that the title of the browser contains the term
  [Arguments]   ${term}
  Title should contain  ${term}
