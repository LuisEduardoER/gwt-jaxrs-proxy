# Overview #
Scans JAX-RS annotated REST resource interfaces and constructs client-side proxies for consumption of those resources, parsing response objects into JavaScript Overlay Types.  This allows for a RESTful web service to be used in a manner very similar to GWT-RPC, abstracting away path and resource information.

# Why? #
GWT's built-in RPC facility is fantastic. It is a great choice if you control both the server and the client and if you do not need to expose the server-side endpoints to other clients.

The gwt-jaxrs-proxy project came about when our team was co-developing both a RESTful web service and a UI using GWT. We found that by judicious use of interfaces on the server side for both model objects and JAX-RS-annotated service objects, we could use GWT's deferred binding to replicate much of the coding nicety of GWT-RPC in consuming our RESTful web service.

The big advantage we found in this approach was that the server-side could remain a vanilla RESTful web service that could be consumed unchanged by any number of other clients - customers, mobile devices, alternate UI frameworks, etc - while still maintaining a simplicity of consumption similar to GWT-RPC. We effectively decoupled our server implementation from the client.

# How? #
You will need to do the following:
  1. Separate your JAX-RS service classes into separate interfaces and implementations, applying the JAX-RS annotations to the interfaces (This is generally considered a good practice for RESTEasy, which our team uses)
  1. Separate your model objects into separate interfaces and implementations. Your server-side implementations can be JAXB-annotated for automatic marshalling, if your JAX-RS framework of choice supports it
  1. Make the service and model interfaces available to your GWT project
  1. Include gwt-jaxrs-proxy in your GWT module XML

Read the full [Instructions](Instructions.md) for more detail.