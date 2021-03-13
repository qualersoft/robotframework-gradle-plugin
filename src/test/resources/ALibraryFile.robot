*** Settings ***

Resource            ./ALibraryFile.resource


*** Keywords ***

My Foo Bar Keyword
    [Documentation]    Does so and so
    [Arguments]        ${arg1}
    Do this
    Do that
    [Return]           Some value