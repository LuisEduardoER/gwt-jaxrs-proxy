# Introduction #

A bit of prep work is required in order to get started; once the basic pattern is understood, however, adding additional representations and resources should be simple.

Terminology:
  * **Representation**: the client-facing version of an object
  * **Resource**: the object that maps URIs to representations

# Details #

### Import gwt-jaxrs-proxy into your GWT project and configure it ###
```
...
   <inherits name="com.paullindorff.gwt.jaxrs.gwt-jaxrs-proxy"/>

   <!-- point the proxy generator at the package containing your REST interfaces -->
   <define-configuration-property	name="sst.restInterfacePackage"	is-multi-valued="false" />
   <set-configuration-property		name="sst.restInterfacePackage" value="com.mystuff.client.rpc.rest" />

   <!-- point the proxy generator at the package containing your JSOs -->
   <define-configuration-property	name="sst.jsoPackage" is-multi-valued="false" />
   <set-configuration-property		name="sst.jsoPackage" value="com.mystuff.client.jso" />

   <!-- tell GWT that anything type-assignable to the marker resource interface should have a proxy generated in its place -->
   <generate-with class="com.paullindorff.gwt.jaxrs.rebind.RESTInterfaceProxyGenerator">
      <when-type-assignable class="com.mystuff.shared.resource.WebServiceResource" />
   </generate-with>

...
```

### Create a representation interface for the desired object ###
```
package com.mystuff.shared.representation;

public interface Thing {
   public Integer getId();
   public void setId(Integer id);
   public String getName();
   public void setName(String name);
}
```

### Create a marker interface for your shared resources ###
```
package com.mystuff.shared.resource;

public interface WebServiceResource {
}
```

### Create a JAX-RS annotated resource interface for the desired object ###
```
package com.mystuff.shared.resource;

@Path("/things")
public interface ThingResource extends WebServiceResource {
   @GET
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Thing getThing(@PathParam("id") Integer id);

   @POST
   @Path("/")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response createThing(Thing thing, @Context UriInfo uriInfo);

   @PUT
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateThing(@PathParam("id") Integer id, Thing thing);
}
```

### Create a GWT module descriptor for your shared interfaces ###
_NOTE_ make sure the descriptor and interfaces are visible to both your server and client projects
```
<?xml version="1.0" encoding="UTF-8"?>
<module rename-to="mystuff-shared">
   <source path="shared"/>
</module>
```

### Create a server-side implementation of your representation and resource ###
```
@XmlRootElement
public class ThingImpl implements Thing {
   private Integer id;
   private Integer name;

   @XmlElement
   public Integer getId() ...
   public void setId(Integer id) ...

   @XmlElement
   public String getName() ...
   public void setName(String name) ...
}

public class ThingResourceImpl implements ThingResource {
   public Thing getThing(@PathParam("id") Integer id) {
      // perform lookup from data store
      return thingIFound;
   }

   public Response createThing(Thing thing, @Context UriInfo uriInfo) {
      // validate the incoming Thing representation
      // store the Thing representation in the data store
      Integer id = thing.getId();  // assuming a generated id
      URI uri = uriInfo.getAbsolutePathBuilder().path(id).build();
      return Response.created(uri).build(); // return a "201-created" response
   }

   public void updateThing(@PathParam("id") Integer id, Thing thing) {
      // verify the id in the path and the id in the incoming representation match
      // update the Thing in the data store with the incoming Thing representation
   }
}
```

### Create a client-side JSO for the representation ###
```
package com.mystuff.client.jso;

public class ThingJSO extends BaseJSO implements Thing {
   protected ThingJSO() {} // required by GWT

   public static Thing create(String json) {
      return (ThingJSO)createObject(json);
   }

   public final Integer getId() {
      return getInteger("id");
   }

   public final void setId(Integer id) {
      setInteger("id", id);
   }

   public final String getName() {
      return getString("name");
   }

   public final void setName(String name) {
      setString("name", name);
   }
}
```

### Create a client-side REST interface for the resource ###
_NOTE_ the auth, target, and callback parameters will be explained shortly
```
package com.mystuff.client.rpc.rest;

public interface ThingResourceREST {
   public void getThing(Integer id, HTTPAuthentication auth, WebServiceTarget target, AsyncCallback<Thing> callback);

   public void createThing(Thing thing, HTTPAuthentication auth, WebServiceTarget target, AsyncCallback<Thing> callback);

   public void updateThing(Integer id, Thing thing, HTTPAuthentication auth, WebServiceTarget target, AsyncCallback<Thing> callback);
}
```

### Use it! ###
```
// first, some setup...
// create the REST interface proxy
ThingResourceREST things = GWT.create(ThingResource.class);
// create an instance of HTTPAuthentication
// currently only BasicAuthentication is available, but NoAuthentication and DigestAuthentication should be trivial to add
HTTPAuthentication auth = new BasicAuthentication("myuser", "mypass");
// create an instance of WebServiceTarget
// you can choose from HostModeProxyTarget, for going against hosted mode, or WebServiceTarget, for going against a 'live' web service
WebServiceTarget target = new HostModeProxyTarget("/mywebservice");

Thing newOne = new ThingJSO();
newOne.setName("my new thing");

things.createThing(newOne, auth, target, new AsyncCallback<String>() {
   public void onSuccess(String result) {
      GWT.log("created a thing! it can be found at: " + result);
   }

   public void onFailure(Throwable caught) {
      GWT.log("whoops! something bad happened...", caught);
   }
});

Thing thing = things.getThing(1, auth, target, new AsyncCallback<Thing>() {
   public void onSuccess(Thing result) {
      GWT.log("found a thing: " + result);
   }

   public void onFailure(Throwable caught) {
      GWT.log("whoops! something bad happened...", caught);
   }
});
```