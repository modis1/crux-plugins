# Crux Zooming Tools Plugin #

## Introduction ##

This plugin enable Zoom in/out feature that can be applied to any Widget inside a view or page.

## Compatibility ##

**GWT +2.5.1**

## Install ##

**Add at POM.XML**

```
 <dependency>
   <groupId>org.cruxframework.plugin</groupId>
   <artifactId>crux-zooming-tools</artifactId>
   <version>...</version>
 </dependency>
```

**Add at file**<pre>"<<PROJECT_NAME>>.gwt.xml"</pre> the following line

```
  <inherits name='org.cruxframework.crux.plugin.zoomingtools.CruxZoomingTools' />
```

### Use ###

**If you want to apply this feature for a Widget, you can use as:**

```

Zoom.getInstance().to(myWidget);
Zoom.getInstance().out();
```

**For a more generic approach you can add this feature to all of your dialogs (this will be available for crux versions up to 5.1.1). Let's take an example:**

```


AbstractDialogBox.addDefaultCloseHandler(new CloseHandler<PopupPanel>()
{
@Override
public void onClose(CloseEvent<PopupPanel> event)
{
Zoom.getInstance().out();
}
});
AbstractDialogBox.addDefaultOpenHandler(new OpenHandler<PopupPanel>()
{
@Override
public void onOpen(final OpenEvent<PopupPanel> event)
{
Zoom.getInstance().to(event.getTarget());
}
});

ConfirmDialog.addDefaultCloseHandler(new CloseHandler<ConfirmDialog>()
{
@Override
public void onClose(CloseEvent<ConfirmDialog> event)
{
Zoom.getInstance().out();
}
});
ConfirmDialog.addDefaultOpenHandler(new OpenHandler<ConfirmDialog>()
{
@Override
public void onOpen(final OpenEvent<ConfirmDialog> event)
{
Zoom.getInstance().to(event.getTarget().asWidget());
}
});

MessageDialog.addDefaultCloseHandler(new CloseHandler<MessageDialog>()
{
@Override
public void onClose(CloseEvent<MessageDialog> event)
{
Zoom.getInstance().out();
}
});
MessageDialog.addDefaultOpenHandler(new OpenHandler<MessageDialog>()
{
@Override
public void onOpen(final OpenEvent<MessageDialog> event)
{
Zoom.getInstance().to(event.getTarget().asWidget());
}
});

```