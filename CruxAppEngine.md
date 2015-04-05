# AppEngine Plugin #

## Introduction ##

This plugin allow Crux applications to host REST services on Google App Engine.

## Compatibility ##

**Crux +5.2**

## Install ##

**Add at POM.XML**

```
   <dependency>
       <groupId>org.cruxframework.plugin</groupId>
       <artifactId>crux-appengine</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
```

**Add at file**<pre>"Crux.properties"</pre> the following line

```
restServiceResourceStateHandler=org.cruxframework.crux.plugin.appengine.rest.AppengineResourceStateHandler
```