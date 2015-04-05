# Crux Google Analytics Plugin #

## Introduction ##

This plugin enable Crux applications to use Google Analytics.

## Compatibility ##

**Crux +5.1.0**

## Install ##

**Add at POM.XML**

```
 <dependency>
   <groupId>org.cruxframework</groupId>
   <artifactId>crux-google-analytics</artifactId>
   <version>...</version>
 </dependency>
```

**Add at file**<pre>"<<PROJECT_NAME>>.gwt.xml"</pre> the following line

```
  <inherits name='org.cruxframework.crux.plugin.google.analytics.GoogleAnalytics' />
```

### Use ###

  * Loading the application, call method _init(userAccount)_ from class GoogleAnalytics at Controller. The _userAccount_ is the number of your Google Analytics acount. This method need be called only once.

  * Every page or action need to call the method of the API to regiter the action. A new page visited need call the _pageview_.