# Crux Bootstrap Plugin #

## Introduction ##

This plugin provides access to all Twitter Bootstrap's CSS and Javascript classes. Using these styles you can stylize any HTML element and a great amount of Crux's widgets.

## Compatibility ##

**GWT +2.5.1**

**Crux Framework +5.1.0**

## Install ##

**Add the following dependency at POM.XML**

```
<dependency>
  <groupId>org.cruxframework.plugin</groupId>
  <artifactId>crux-bootstrap</artifactId>
  <version>x.x.x</version>
</dependency>
```

**Add at file**<pre>"<<PROJECT_NAME>>.gwt.xml"</pre> the following line

```
  <inherits name='org.cruxframework.crux.plugin.bootstrap.CruxBootstrap' />
```

### Use ###

**A complete list of bootstrap's features and styles can be found at its official web site (http://getbootstrap.com/)**

**You can either apply a style into an HTML element or into a Crux Widget. Example:**

 HTML Element:
```

<input type="button" id="button1" class="btn btn-primary btn-large">Button 1

Unknown end tag for &lt;/button&gt;


```

 Crux Widget:
```

<crux:button id="buttonCrux" styleName="btn btn-primary btn-large" text="Crux Button" />
```
**As you can notice, there's only a little difference between these two approaches.**

## Change Bootstrap's version ##
**By Default, this plugin works with Bootstrap v3.1.1, but you can download its source code and compile a version using Bootstrap v2.3.2.
To proceed with this compilation, only you need to do is change a single line at the file ModuleLoader.java.
For example, the following code will compile a version using Bootstrap 3.1.1:**

```

public class ModuleLoader implements EntryPoint
{

@Override
public void onModuleLoad()
{
//injectBoostrap2();
injectBoostrap3();
}
....
}
```

 To compile a version using Bootstrap v2.3.2, just change the commented line. Example:

```

public class ModuleLoader implements EntryPoint
{

@Override
public void onModuleLoad()
{
injectBoostrap2();
//injectBoostrap3();
}
....
}
```

## Known limitations ##