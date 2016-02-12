# camel-cm

[Camel component](http://camel.apache.org/components.html) for the [CM SMS Gateway](https://www.cmtelecom.com). 

It allows to integrate [CM SMS API](https://dashboard.onlinesmsgateway.com/docs) in an application as a camel component. 

You must have a valid account.  More information are available at [CM Telecom](https://www.cmtelecom.com/support).

### URI Format

```
cm://sgw01.cm.nl/gateway.ashx?defaultFrom=DefaultSender&defaultMaxNumberOfParts=8&productToken=2fb82162-754c-4c1b-806d-9cb7efd677f4
```


### Endpoint Options

CM endpoints act like a **producer** and support the following options.

| Name  | Default Value | Description |
| ------------- | ------------- | ------------- |
| defaultMaxNumberOfParts  | 8 |  If it is a multipart message forces the max number. Technically the gateway will first check if a message is larger than 160 characters, if so, the message will be cut into multiple 153 characters parts limited by these parameters. |

### Tests

Tests provided so far show a valid [Spring Configuration](https://github.com/oalles/camel-cm/blob/master/src/test/java/org/apache/camel/component/cm/test/TestConfiguration.java). Notice that CM Component URI is built from properties in a [file].(https://github.com/oalles/camel-cm/blob/master/src/test/resources/cm-smsgw.properties). 