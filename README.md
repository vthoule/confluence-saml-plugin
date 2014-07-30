# Confluence SAML 2.0 Plugin

A plugin for Atlassian Confluence to support connecting to a SAML 2.0 IdP.

This library adheres to [SemVer](http://semver.org). Pre v1.0.0 is considered alpha level software.


## Installation

The normal way to install this plugin is via the Universal Plugin Manager (UPM) in Confluence - search for SAML. The plugin can also be found in the [Atlassian Marketplace](https://marketplace.atlassian.com/plugins/com.bitium.confluence.SAML2Plugin) 


## Development Setup

Note: this assumes you have a JDK installed and configured. If not, look [here](https://developer.atlassian.com/display/DOCS/Set+up+the+SDK+Prerequisites+for+Linux+or+Mac)

### Install the Atlassian SDK

  [Homebrew Setup](https://developer.atlassian.com/display/DOCS/Install+the+Atlassian+SDK+on+a+Linux+or+Mac+System)
  1. ```brew tap atlassian/tap```
  2. ```brew install atlassian/tap/atlassian-plugin-sdk```
  
### Compile and run the plugin

  1. go to the directory where the source code was downloaded
  2. run ```atlas-run``` to build the plugin and start confluence
  3. after everything compiles and confluence starts, a URL will be displayed
  4. enter the url into your browser
    * username: ```admin```
    * password: ```admin```
  5. click the "cog" menu and select "add-ons"
  6. enter "SAML" into the search box and select "All Add-ons" from the dropdown 

### Packaging and installing manually

  1. In the project directory, run ```atlas-package``` to generate a jar
  2. In confluence, login and go to the "manage add-ons" admin area
  3. Select the option to upload a plugin
  4. Find the packaged jar under {project home}/target/SAML2Plugin-{version}.jar

## Reporting Bugs
  Please use [github issues](https://github.com/bitium/confluence-saml-plugin/issues) to log tickets

## Documentation
  Docs can be found in the [wiki](https://github.com/bitium/confluence-saml-plugin/wiki)

## Compatibility
  This plugin is compatible with the [Bitium IdP](https://bitium.com) but other IdP's that support SAML 2.0 may also work. If you have success using it with another system please let us know so we can list that in the wiki.

## Contributing

  1. Fork it
  2. Create your feature branch (`git checkout -b my-new-feature`)
  3. Commit your changes (`git commit -am 'Add some feature'`)
  4. Push to the branch (`git push origin my-new-feature`)
  5. Create new Pull Request

## Licensing
  This plugin is licensed under [GPLv3](LICENSE).



_&copy; Bitium, Inc 2014_
