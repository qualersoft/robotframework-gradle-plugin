*** Settings ***
Resource            ../common.resource

*** Variables ***
${CNTRY_GER}

*** Keywords ***
Search country by name contains a country
  [Arguments]   ${name}   ${expectedCountry}
  ${resp}=     Get countries by name   ${name}
  Assert request succeed    ${resp}
  ${countries}=   Get countries from response   ${resp}
  Assert that '${countries}' contains '${expectedCountry}'

Assert that '${countries}' contains '${expectedCountry}'
  Assert country list contains  ${countries}  ${expectedCountry}

Create default country germany
  ${res}=   Create default country    GER
  Set Suite Variable    \${CNTRY_GER}   ${res}
