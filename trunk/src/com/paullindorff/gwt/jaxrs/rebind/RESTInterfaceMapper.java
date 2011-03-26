package com.paullindorff.gwt.jaxrs.rebind;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.paullindorff.gwt.jaxrs.client.proxy.HTTPAuthentication;
import com.paullindorff.gwt.jaxrs.client.proxy.WebServiceTarget;

/**
 * Maps a WebServiceResource interface to the corresponding client-side REST interface, validating the signature and augmenting parameters as necessary
 * @author plindorff
 *
 */
public class RESTInterfaceMapper
{
	private JClassType httpAuthenticationClass;
	private JClassType webServiceTargetClass;
	private JClassType asyncCallbackClass;

	public RESTInterfaceMapper(TreeLogger logger, TypeOracle typeOracle) throws UnableToCompleteException
	{
		try {
			// ensure we have access to required class metadata
			httpAuthenticationClass = typeOracle.getType(HTTPAuthentication.class.getName());
			webServiceTargetClass = typeOracle.getType(WebServiceTarget.class.getName());
			asyncCallbackClass = typeOracle.getType(AsyncCallback.class.getName());
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, null, e);
			throw new UnableToCompleteException();
		}
	}

	public Map<JMethod, JMethod> mapResourceMethods(TreeLogger logger, JClassType resourceInterface, JClassType restInterface) throws UnableToCompleteException
	{
		Map<JMethod, JMethod> resourceToRESTMap = new HashMap<JMethod, JMethod>();

		// get methods defined by both interfaces
		JMethod[] resourceMethods = resourceInterface.getOverridableMethods();
		JMethod[] restMethods = restInterface.getOverridableMethods();

		// compare method counts
		TreeLogger branch = logger.branch(TreeLogger.DEBUG, "mapping the Resource interface '"+ resourceInterface.getQualifiedSourceName()
															+ "' to its REST version '" + restInterface.getQualifiedSourceName() + "'", null);

		//TODO: do we need this check?  what if we don't want some resource methods available on the client?
		if (restMethods.length != resourceMethods.length)
		{
			branch.log(TreeLogger.ERROR, "REST interface has different number of overridable methods than the Resource interface");
			throw new UnableToCompleteException();
		}

		// put rest methods in a map (signature string -> method object) for retrieval during the method->method mapping step
		Map<String, JMethod> restMethodMap = new HashMap<String, JMethod>();
		for (JMethod restMethod: restMethods)
			restMethodMap.put(GWTSourceUtils.computeInternalSignature(restMethod), restMethod);

		// assemble the resource/rest map (resource method object -> rest method object)
		for (JMethod resourceMethod: resourceMethods)
		{
			// compute the corresponding rest method signature from the resource method
			String restMethodSignature = computeRESTMethodSignature(resourceMethod, httpAuthenticationClass, webServiceTargetClass, asyncCallbackClass);
			// get the rest method from the restMethodMap via the computed signature
			JMethod restMethod = restMethodMap.get(restMethodSignature);

			if (restMethod == null)
			{
				branch.log(TreeLogger.ERROR, "No matching REST method for resourceMethod '" + GWTSourceUtils.computeInternalSignature(resourceMethod) + "'; expecting '" + restMethodSignature + "'");
				throw new UnableToCompleteException();
			}

			// check that the rest method return type is void
			JType restMethodReturnType = restMethod.getReturnType();
			if (restMethodReturnType != JPrimitiveType.VOID)
			{
				branch.log(TreeLogger.ERROR, "REST method must have a return type of 'void': " + GWTSourceUtils.computeInternalSignature(resourceMethod));
				throw new UnableToCompleteException();
			}

			// add the method mapping
			resourceToRESTMap.put(resourceMethod, restMethod);
		}

		return resourceToRESTMap;
	}

	/**
	 * computes the corresponding "REST" method signature for a resource method, building upon the resourceMethod's own signature
	 * @param resourceMethod
	 * @param httpAuthenticationClass
	 * @param webServiceTargetClass
	 * @param asyncCallbackClass
	 * @return
	 */
	private String computeRESTMethodSignature(JMethod resourceMethod, JClassType httpAuthenticationClass, JClassType webServiceTargetClass, JClassType asyncCallbackClass)
	{
	    return GWTSourceUtils.computeInternalSignature(resourceMethod)
	    + "/" + httpAuthenticationClass.getQualifiedSourceName()
	    + "/" + webServiceTargetClass.getQualifiedSourceName()
	    + "/" + asyncCallbackClass.getQualifiedSourceName();

	}
}
