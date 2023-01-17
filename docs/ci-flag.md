# CI Flag

We use the CI flag to enable some features or building steps only CI environment.

Currently, it's used:

* To enable Firebase app distribution
* To enable Firebase crashlytics
* Google services
* To remove the `applicationIdSuffix` on CI which will allow the correct parsing of
  `google-services.json`
* To calculate the version name