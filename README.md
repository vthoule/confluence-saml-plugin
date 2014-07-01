#### Setup

Note: this assumes you have a JDK installed and configured. If not, look [here](https://developer.atlassian.com/display/DOCS/Set+up+the+SDK+Prerequisites+for+Linux+or+Mac)

##### Install the Atlassian SDK

_Homebrew Setup_
see https://developer.atlassian.com/display/DOCS/Install+the+Atlassian+SDK+on+a+Linux+or+Mac+System
  1. ```brew tap atlassian/tap```
  2. ```brew install atlassian/tap/atlassian-plugin-sdk```
  
##### Compile and run the plugin

  1. go to the directory where the source code was downloaded
  2. run ```atlas-run``` to build the plugin and start confluence
  3. after everything compiles and confluence starts, a URL will be displayed
  4. enter the url into your browser
    * username: ```admin```
    * password: ```admin```
  5. click the "cog" menu and select "add-ons"
  6. enter "SAML" into the search box and select "All Add-ons" from the dropdown 