Core Solution SAS/SCL application

========================
SWITCH BETWEEN SAS & SCL
========================

In order to run the application in SCL mode, navigate from the application root folder to the directory
"profiles\SCL-CORE-DEV\config" and rename the directory "productionConfig-SCL" to "productionConfig"

In order to run the application in SAS mode, navigate from the application root folder to the directory
"profiles\SCL-CORE-DEV\config" and rename the directory "productionConfig-SAS" to "productionConfig"

The instructions above are for the core solution. For the customizations projects the instructions are the same, just
the profile name of the customization changes.

==================
CODE TYPE CHECKING
==================

To enable or disable code type checking for production modes: standard, refeed normal, refeed correction, export coding
change the attribute "activationBehavior" for the corresponding production config file.

For enable code type checking set the value "activationBehavior" = standardActivationBehaviorCodeTypeCheck
To disable code type checking set the value "activationBehavior" = standardActivationBehavior