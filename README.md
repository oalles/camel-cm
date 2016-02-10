# camel-cm

Camel component for the CM SMS Gateway. 

It allows to integrate [CM SMS API](https://dashboard.onlinesmsgateway.com/docs) in an application as a camel component. 

You must have a valid account.  More information are available at [CM Telecom](https://www.cmtelecom.com/support).

### URI Format

```
cm://sgw01.cm.nl/gateway.ashx?defaultFrom=DefaultSender&defaultMaxNumberOfParts=8&productToken=2fb82162-754c-4c1b-806d-9cb7efd677f4
```


### Endpoint Option

CM endpoints act like a **producer** and support the following options.

| Name  | Default Value | Description |
| ------------- | ------------- | ------------- |
| defaultMaxNumberOfParts  | 8 | A message can be truncated. Technically the gateway will first check if a message is larger than 160 characters, if so, the message will be cut into multiple 153 characters parts limited by these parameters. |
| responseProcessor  | null  | A reference in the registry to a ResponseProcessor |