# Crux Google Analytics Plugin #

## Introduction ##

This plugin enable Jboss Application server to be used to debug Crux applications.

## Compatibility ##

**Crux +5.2-SNAPSHOT**

## Install ##

**Add at POM.XML**

```
 <dependency>
   <groupId>org.cruxframework.plugin</groupId>
   <artifactId>crux-jboss</artifactId>
   <version>...</version>
 </dependency>
```

**Add at file**<pre>"web.xml"</pre> the following line

```
  <listener>
       <listener-class>org.cruxframework.crux.plugin.jboss.listener.JNDIRegistryListener</listener-class>
  </listener>
```

Just before the other Crux Listener.
```
  <listener>
       <listener-class>org.cruxframework.crux.core.server.InitializerListener</listener-class>
  </listener>
```