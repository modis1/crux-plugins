# Crux Super Error Handler Plugin #

## Introduction ##

This plugin enables Crux applications to display deferred binding error messages in client side. Using this, users will not have to inspect DevMode log in order to find for an error, they will be displayed right inside a Crux view or page.

## Compatibility ##

**Crux 5.1.0**

## Install ##

**Add at POM.XML**

```
 <dependency>
   <groupId>org.cruxframework.plugin</groupId>
   <artifactId>crux-super-errorhandler</artifactId>
   <version>...</version>
 </dependency>
```

**Add at file**<pre>"<<PROJECT_NAME>>.gwt.xml"</pre> the following line

```
  <inherits name="org.cruxframework.crux.plugin.errorhandler.SuperErrorHandler"/>
```

**Run instrumentation procedure. You have to run it only once (if you remove the gwt-dev jar obviously you should run again) and will allow Crux classes to be injected in the gwt-dev jar:**

```

org.cruxframework.crux.plugin.errorhandler.launcher.SuperErrorHandlerInstrumentator.main();
```

**This procedure will create a gwt-dev backup jar in the same folder of the instrumented jar. If you want to revert the operation, just replace the new file for the backup one.**

### Use ###

  * Just open your application and start coding. Once an error occurs it will be displayed right on your screen.