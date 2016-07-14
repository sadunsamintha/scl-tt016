Core Solution SAS/SCL application

========================
SWITCH BETWEEN SAS & SCL
========================

In the "global.properties" file in the project set the property "production.config.folder" to "productionConfig-SAS" or productionConfig-SCL
depending on usage.

The instructions above are for the core solution. For the customizations projects the instructions are the same, just
the profile name of the customization changes.

==================
CODE TYPE CHECKING
==================

To enable or disable code type checking for production modes: standard, refeed normal, refeed correction, export coding
change the attribute "activationBehavior" for the corresponding production config file.

For enable code type checking set the value "activationBehavior" = standardActivationBehaviorCodeTypeCheck
To disable code type checking set the value "activationBehavior" = standardActivationBehavior