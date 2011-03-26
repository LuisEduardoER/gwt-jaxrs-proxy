package com.paullindorff.gwt.jaxrs.rebind;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.paullindorff.gwt.jaxrs.client.jso.BaseJSO;
import com.paullindorff.gwt.jaxrs.client.proxy.RESTResourceProxy;
import com.paullindorff.gwt.jaxrs.client.proxy.ResponseReader;
import com.paullindorff.gwt.jaxrs.client.proxy.VoidResponseReader;

/**
 * Creates a client-side proxy for a WebServiceResource, implementing the associated REST interface
 * @author plindorff
 *
 */
public class RESTProxyCreator
{
	private static final String REST_SUFFIX = "REST";
	private static final String JSO_SUFFIX = "JSO";
	private static final String PROXY_SUFFIX = "Proxy";
	private static final String PROXY_SUFFIX_DELIM = "_";
	private static final char PATH_PARAM_START_DELIM = '{';
	private static final char PATH_PARAM_END_DELIM = '}';

	private JClassType resourceInterface;
	private JClassType responseReaderInterface;
	private JPackage restInterfacePackage;
	private JPackage jsoPackage;

	public RESTProxyCreator(JClassType resourceInterface, JClassType responseReaderInterface, JPackage restInterfacePackage, JPackage jsoPackage) {
		this.resourceInterface = resourceInterface;
		this.responseReaderInterface = responseReaderInterface;
		this.restInterfacePackage = restInterfacePackage;
		this.jsoPackage = jsoPackage;
	}

	/**
	 * Entry point for proxy creation.  After the proxy class has been written, the fully-qualified class name of the proxy is returned.
	 * @param logger
	 * @param context
	 * @return
	 * @throws UnableToCompleteException
	 */
	public String create(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException
	{
		// look up corresponding REST interface for this resource
		TypeOracle typeOracle = context.getTypeOracle();
		JClassType restInterface = typeOracle.findType(restInterfacePackage.getName() + "." + resourceInterface.getSimpleSourceName() + REST_SUFFIX);

		if (restInterface == null)
		{
			logger.log(TreeLogger.ERROR, "could not find associated REST interface for " + resourceInterface.getQualifiedSourceName() + " in package " + restInterfacePackage.getName());
			throw new UnableToCompleteException();
		}

		// get root path specified by resource interface, if any
		String rootPath = "";
		if (resourceInterface.isAnnotationPresent(Path.class))
		{
			rootPath = resourceInterface.getAnnotation(Path.class).value();
			logger.log(TreeLogger.DEBUG, "found annotation indicating root path: " + rootPath);
		}

		// create the method map
		RESTInterfaceMapper mapper = new RESTInterfaceMapper(logger, typeOracle);
		Map<JMethod, JMethod> resourceToRESTMap = mapper.mapResourceMethods(logger, resourceInterface, restInterface);

		// get a source writer for the REST proxy class
		SourceWriter restSrcWriter = getRESTProxySourceWriter(logger, context, restInterface);
		if (restSrcWriter == null) {
			// srcWriter is null if the class already exists, just return the existing class
			logger.log(TreeLogger.DEBUG, "proxy class already exists: " + getRESTProxyQualifiedName(restInterface));
			return getRESTProxyQualifiedName(restInterface);
		}

		// create the REST proxy class methods
		generateRESTProxyMethods(restSrcWriter, typeOracle, rootPath, resourceToRESTMap);

		// commit the REST proxy class
		restSrcWriter.commit(logger);

		// get the set of return types specified by the resource interface
		Set<JType> resourceReturnTypes = new HashSet<JType>();
		for (JMethod method: resourceInterface.getOverridableMethods())
			resourceReturnTypes.add(method.getReturnType());

		logger.log(TreeLogger.DEBUG, "resourceReturnTypes: " + resourceReturnTypes);

		// generate a ResponseReader proxy for each eligible return type
		generateResponseReaderProxies(logger, context, typeOracle, resourceReturnTypes);

		// all done, return the REST proxy fully qualified class name
		String restProxyFQClassName = getRESTProxyQualifiedName(restInterface);
		logger.log(TreeLogger.DEBUG, "rest proxy successfully generated: " + restProxyFQClassName);
		return restProxyFQClassName;
	}

	/**
	 * Generates a proxy method with the signature specified by the REST interface for each method in the WebServiceResource interface
	 * @param writer
	 * @param typeOracle
	 * @param rootPath
	 * @param resourceToRESTMap
	 */
	private void generateRESTProxyMethods(SourceWriter writer, TypeOracle typeOracle, String rootPath, Map<JMethod, JMethod> resourceToRESTMap)
	{
		JMethod[] resourceMethods = resourceInterface.getOverridableMethods();
		for (JMethod resourceMethod : resourceMethods)
		{
			JMethod restMethod = resourceToRESTMap.get(resourceMethod);
			assert (restMethod != null);

			// erase type parameterization
			resourceMethod = GWTSourceUtils.getGenericVersion(resourceMethod);

			// for each resource method, generate the proxy method
			generateRESTProxyMethod(writer, typeOracle, rootPath, resourceMethod, restMethod);
		}
	}

	/**
	 * Generates a single proxy method, defined by the restMethod, using annotations in the resourceMethod as guidance
	 * @param writer
	 * @param typeOracle
	 * @param rootPath
	 * @param resourceMethod
	 * @param restMethod
	 */
	private void generateRESTProxyMethod(SourceWriter writer, TypeOracle typeOracle, String rootPath, JMethod resourceMethod, JMethod restMethod)
	{
		// blank line before the method definition
		writer.println();

		// ***** method signature START *****

		// access modifier and method name
		JType restReturnType = restMethod.getReturnType().getErasedType();
		writer.print("public ");
		writer.print(restReturnType.getQualifiedSourceName());
		writer.print(" ");
		writer.print(restMethod.getName() + "(");

		// parameter list
		boolean needsComma = false;
		NameFactory nameFactory = new NameFactory();
		JParameter[] asyncParams = restMethod.getParameters();
		for (int i = 0; i < asyncParams.length; ++i) {
			JParameter param = asyncParams[i];
	
			if (needsComma)
				writer.print(", ");
			else
				needsComma = true;
	
			JType paramType = param.getType().getErasedType();
			writer.print(paramType.getQualifiedSourceName());
			writer.print(" ");
	
			String paramName = param.getName();
			nameFactory.addName(paramName);
			writer.print(paramName);
		}

		// close the signature
		writer.println(")");
		// ***** method signature END *****

		// ***** method body START *****
		writer.println("{");
		writer.indent();

		// build REST URL path from annotations in WebServiceResource

		// get HttpMethod from annotation
		String httpMethod = getHTTPMethodFromAnnotation(resourceMethod);
		writer.println("String httpMethod = \"" + httpMethod + "\";");

		// get method-specific path from annotation
		String methodPath = "/";
		if (resourceMethod.isAnnotationPresent(Path.class))
			methodPath = resourceMethod.getAnnotation(Path.class).value();

		// combine paths
		String path = rootPath + methodPath;

		// strip out any regex validation from the path
		path = stripRegexFromPath(path);

		// strip off trailing slash
		if (path.endsWith("/"))
			path = path.substring(0, path.length()-1);

		// write path to proxy class
		writer.println("String path = \"" + path + "\";");
		writer.println("GWT.log(\"path=\" + path);");

		// loop through parameters, replacing path params and assembling any query params into a string
		writer.println("Object requestBody = null;");
		writer.println("StringBuilder query = new StringBuilder();");
		boolean needsAmpersand = false;
		boolean requestBodyPresent = false;
		boolean queryPresent = false;
		JType requestBodyType = null;
		for (JParameter param: resourceMethod.getParameters())
		{
			// check for a primitive parameter type
			boolean paramTypeIsPrimitive = (param.getType().isPrimitive() != null);

			if (param.isAnnotationPresent(PathParam.class))
			{
				// replace token specified by the path param with value passed to the method
				String pathToken = PATH_PARAM_START_DELIM + param.getAnnotation(PathParam.class).value() + PATH_PARAM_END_DELIM;

				writer.print("path = path.replace(\"" + pathToken + "\", ");
				// handle primitive vs. Object parameter types
				if (paramTypeIsPrimitive)
					writer.print("String.valueOf(" + param.getName() + ")");
				else
					writer.print("getStringValue(" + param.getName() + ", false)");
				writer.println(");");

			} else
			if (param.isAnnotationPresent(QueryParam.class))
			{
				// add query param value to the query string
				queryPresent = true;
				String queryParamName = param.getAnnotation(QueryParam.class).value();

				// check for a null if parameter is not a primitive...if the parameter value is null, don't add the name/value pair to the query string
				if (!paramTypeIsPrimitive)
				{
					writer.println("if (" + param.getName() + " != null) {");
					writer.indent();
				}

				writer.print("query.append(");
				if (needsAmpersand)
					writer.print("\"&\" + ");
				else
					needsAmpersand = true;
				
				writer.print("\"" + queryParamName + "=\" + ");
				if (paramTypeIsPrimitive)
					writer.print("String.valueOf(" + param.getName() + ")");
				else
					writer.print("getStringValue(" + param.getName() + ", true)");
				writer.println(");");

				if (!paramTypeIsPrimitive)
				{
					writer.outdent();
					writer.println("}");
				}
			} else
			if (param.isAnnotationPresent(Context.class))
			{
				// skip parameters with "Context" annotation, they are strictly for injection of context info (headers, uri information) into the server-side resource
			} else
			{
				// no annotation present, this object is for the request body
				requestBodyPresent = true;
				requestBodyType = param.getType();
				writer.println("requestBody = " + param.getName() + ";");
			}
		}

		// assemble the full proxy class URL for the method call
		if (queryPresent)
			writer.println("String fullPath = path + \"?\" + query.toString();");
		else
			writer.println("String fullPath = path;");

		// create a ResponseReader (a proxy will be created)
		JType resourceReturnType = resourceMethod.getReturnType();
		// a return type of "javax.ws.rs.core.Response" indicates we're returning some value in the header.
		// it is the responsibility of the RequestCallbackAdapter to read the status code and extract the appropriate header.
		// currently only used to return the "Location" header on a successful new object POST.
		if (isResponseReaderVoid(resourceReturnType))
			writer.println("ResponseReader responseReader = new VoidResponseReader();");
		else
			writer.println("ResponseReader responseReader = new " + getResponseReaderProxySimpleName(resourceReturnType) + "();");

		// get the AsyncCallback parameter, we'll need it if serialization throws an exception
	    JParameter callbackParam = asyncParams[asyncParams.length - 1];
	    String callbackParamName = callbackParam.getName();

	    // get the WebServiceTarget parameter
	    JParameter targetParam = asyncParams[asyncParams.length - 2];
	    String targetParamName = targetParam.getName();

	    // get the HTTPAuthentication parameter
	    JParameter authParam = asyncParams[asyncParams.length - 3];
	    String authParamName = authParam.getName();

		// perform the call to the superclass
		if (requestBodyPresent)
		{
			// serialize the request body object to a String

		    // call the associated JSO's toJSON method
			writer.println("try {");
			writer.indent();
			writer.println(requestBodyType.getSimpleSourceName() + "JSO jso = (" + requestBodyType.getSimpleSourceName() + "JSO)requestBody;");
			writer.println("String requestBodyString = jso.toJSON();");
			writer.println("performRESTRequest(httpMethod, fullPath, requestBodyString, responseReader, " + authParamName + ", " + targetParamName + ", " + callbackParamName + ");");
			writer.outdent();
			writer.println("} catch (Exception e) {");
			// if the toJSON call throws an exception, handle the failure in the callback
			writer.indent();
			writer.println("callback.onFailure(e);");
			writer.outdent();
			writer.println("}");
		}
		else
		{
			// perform the call
			writer.println("performRESTRequest(httpMethod, fullPath, responseReader, " + authParamName + ", " + targetParamName + ", " + callbackParamName + ");");
		}

		writer.outdent();
		writer.println("}");
		// ***** method body END *****
	}

	/**
	 * Generates a ResponseReader proxy for each eligible type listed, dispatching to the corresponding JSO implementation
	 * @param logger
	 * @param context
	 * @param typeOracle
	 * @param types
	 */
	private void generateResponseReaderProxies(TreeLogger logger, GeneratorContext context, TypeOracle typeOracle, Set<JType> types) throws UnableToCompleteException
	{
		for (JType type : types)
		{
			// if the type is a JAX-RS Response, an array or primitive, skip it - the client side implementation returns Void for these
			if (isResponseReaderVoid(type))
				continue;

			TreeLogger branch = logger.branch(TreeLogger.INFO, "generating response reader proxy for " + type.getSimpleSourceName());
			// lookup the corresponding JSO class for the return type interface
			String jsoName = getJSOQualifiedName(type.isInterface());
			if (jsoName == null)
			{
				branch.log(TreeLogger.ERROR, "invalid type specified (" + type.getQualifiedSourceName() + ").  JSOs must implement an interface.");
				throw new UnableToCompleteException();
			}

			JClassType jsoType = typeOracle.findType(jsoName);
			if (jsoType == null)
			{
				branch.log(TreeLogger.ERROR, "unable to find corresponding JSO class for interface " + type.getQualifiedSourceName());
				throw new UnableToCompleteException();
			}

			// get a source writer for the response reader class
			SourceWriter responseReaderSrcWriter = getResponseReaderProxySourceWriter(logger, context, type, jsoType);
			if (responseReaderSrcWriter == null)
			{
				// srcWriter is null if the class already exists
				branch.log(TreeLogger.INFO, "reader class already exists: " + getResponseReaderProxyQualifiedName(type.isInterface()));
			} else
			{
				// generate the proxy class method
				generateResponseReaderMethod(responseReaderSrcWriter, typeOracle, type, jsoType);

				// commit the proxy class
				responseReaderSrcWriter.commit(logger);
				branch.log(TreeLogger.INFO, "ResponseReader proxy successfully generated: " + getResponseReaderProxyQualifiedName(type.isInterface()));
			}
		}
	}

	/**
	 * Generates the "read" method for a ResponseReader JSO proxy, delegating to the JSO type to create itself from the given JSON string
	 * @param writer
	 * @param typeOracle
	 * @param returnType
	 * @param jsoClass
	 */
	private void generateResponseReaderMethod(SourceWriter writer, TypeOracle typeOracle, JType returnType, JClassType jsoClass)
	{
		// method signature: ResponseReader proxy returns the common object interface
		writer.println("public " + returnType.getSimpleSourceName() + " read(String response) {");
		writer.indent();
		// delegate to the corresponding JSO to create itself
		writer.println("return " + jsoClass.getSimpleSourceName() + ".create(response);");
		writer.outdent();
		writer.println("}");
	}

	/**
	 * Creates a SourceWriter for writing a new class definition
	 * This method returns null if a class with the same fully-qualified name already exists.
	 * @param logger
	 * @param ctx
	 * @param targetInterface
	 * @return
	 */
	private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext context, String packageName, String classSimpleName, String superClass, String[] implementedInterfaces, String[] imports)
	{
		// attempt to get a print writer for the package/class combination
		PrintWriter printWriter = context.tryCreate(logger, packageName, classSimpleName);
		if (printWriter == null) {
			// if printWriter is null, the class we're trying to create already exists
			return null;
		}

		// create a factory targeting the desired package and class name
		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, classSimpleName);

		// add any specified imports
		if (imports != null)
		{
			for (String typeName : imports)
				composerFactory.addImport(typeName);
		}

		// add a superclass, if specified
		if (superClass != null)
			composerFactory.setSuperclass(superClass);

		// implement any specified interface
		if (implementedInterfaces != null)
		{
			for (String intfName : implementedInterfaces)
				composerFactory.addImplementedInterface(intfName);
		}

		return composerFactory.createSourceWriter(context, printWriter);
	}

	/**
	 * Creates a SourceWriter for a generated implementation of the given REST interface
	 * @param logger
	 * @param context
	 * @param targetInterface
	 * @return
	 */
	private SourceWriter getRESTProxySourceWriter(TreeLogger logger, GeneratorContext context, JClassType targetInterface)
	{
		// get the package where the targetInterface resides.  the generated proxy will also logically reside in this package
		JPackage targetPackage = targetInterface.getPackage();
		String packageName = targetPackage == null ? "" : targetPackage.getName();

		// create the proxy class name
		String classSimpleName = getRESTProxySimpleName(targetInterface);

		// add imports required by our generated proxy; if the code were writing out uses a class not in the same package, we need to be sure to add it here
		String[] imports = {
			RESTResourceProxy.class.getCanonicalName(),
			ResponseReader.class.getCanonicalName(),
			VoidResponseReader.class.getCanonicalName(),
			SerializationException.class.getCanonicalName(),
			GWT.class.getCanonicalName(),
			jsoPackage.getName() + ".*",							// catch-all import for all JSOs.  JSOs are responsible for writing themselves to JSON if sent as a request body.
			ResponseReader.class.getPackage().getName() + ".*",		// catch-all import for all ResponseReader proxies
		};

		// extend the common RESTResourceProxy parent class for common functionality; this greatly eases our pain when writing out source line-by-line
		String superClass = RESTResourceProxy.class.getSimpleName();

		// implement the target REST interface
		String[] implementedInterfaces = {
			targetInterface.getErasedType().getQualifiedSourceName()
		};

		return getSourceWriter(logger, context, packageName, classSimpleName, superClass, implementedInterfaces, imports);
	}

	/**
	 * Creates a SourceWriter for a generated implementation of ResponseReader for the given JSO type
	 * @param logger
	 * @param context
	 * @param returnType
	 * @param jsoType
	 * @return
	 */
	private SourceWriter getResponseReaderProxySourceWriter(TreeLogger logger, GeneratorContext context, JType returnType, JClassType jsoType)
	{
		// get the package where the ResponseReader interface resides.  the generated proxy will also logically reside in this package
		JPackage targetPackage = responseReaderInterface.getPackage();
		String packageName = targetPackage == null ? "" : targetPackage.getName();

		// get the proxy class name, which will contain the target object name as well
		String classSimpleName = getResponseReaderProxySimpleName(returnType);

		// add imports required by our generated proxy; if the code were writing out uses a class not in the same package, we need to be sure to add it here
		String[] imports = {
			responseReaderInterface.getQualifiedSourceName(),
			BaseJSO.class.getCanonicalName(),
			returnType.getQualifiedSourceName(),
			jsoType.getQualifiedSourceName()
		};

		// implement the ResponseReader interface
		String[] implementedInterfaces = {
			responseReaderInterface.getErasedType().getQualifiedSourceName()
		};

		return getSourceWriter(logger, context, packageName, classSimpleName, null, implementedInterfaces, imports);

	}

	/**
	 * Constructs the fully-qualified class name for the REST proxy class
	 * @param c
	 * @return
	 */
	private String getRESTProxyQualifiedName(JClassType c) {
		String[] name = GWTSourceUtils.synthesizeTopLevelClassName(c, PROXY_SUFFIX_DELIM + PROXY_SUFFIX);
		return name[0].length() == 0 ? name[1] : name[0] + "." + name[1];
	}

	/**
	 * Constructs the "simple" (name only) class name for the REST proxy class
	 * @param c
	 * @return
	 */
	private String getRESTProxySimpleName(JClassType c) {
		String[] name = GWTSourceUtils.synthesizeTopLevelClassName(c, PROXY_SUFFIX_DELIM + PROXY_SUFFIX);
		return name[1];
	}

	/**
	 * Constructs the fully-qualified class name for the ResponseReader proxy class
	 * @param c
	 * @return
	 */
	private String getResponseReaderProxyQualifiedName(JType subType) {
		String[] name = GWTSourceUtils.synthesizeTopLevelClassName(responseReaderInterface, PROXY_SUFFIX_DELIM + subType.getSimpleSourceName() + PROXY_SUFFIX_DELIM + PROXY_SUFFIX);
		return name[0].length() == 0 ? name[1] : name[0] + "." + name[1];
	}

	/**
	 * Constructs the "simple" (name only) class name for the ResponseReader proxy class
	 * @param c
	 * @return
	 */
	private String getResponseReaderProxySimpleName(JType subType) {
		String[] name = GWTSourceUtils.synthesizeTopLevelClassName(responseReaderInterface, PROXY_SUFFIX_DELIM + subType.getSimpleSourceName() + PROXY_SUFFIX_DELIM + PROXY_SUFFIX);
		return name[1];
	}

	/**
	 * Constructs the fully-qualified class name for the JSO implementation of the specified interface
	 * @param c
	 * @return
	 */
	private String getJSOQualifiedName(JClassType c) {
		return jsoPackage.getName() + "." + getJSOSimpleName(c);
	}

	/**
	 * Constructs the "simple" (name only) class name for the JSO implementation of the specified interface
	 * @param c
	 * @return
	 */
	private String getJSOSimpleName(JClassType c) {
		return c.getSimpleSourceName() + JSO_SUFFIX;
	}

	/**
	 * RESTEasy allows for validation of path parameters with regular expressions, as in the following:<br>
	 * 		<code>
	 * 			@PathParam("/{eventID: [0-9]+}")
	 * 		</code>
	 * While this is very helpful on the server side, we don't need to validate in our client proxy -- we want just the parameter name.
	 * This method distills the above down to: "/{eventID}", which is ready for token substitution.
	 * @param path
	 * @return
	 */
	private String stripRegexFromPath(String path)
	{
		// regex validators are denoted by a colon after the path parameter name
		if (path.contains(":"))
		{
			// path contains a regex validator in the form  {eventID: [0-9]+}
			StringBuilder sb = new StringBuilder();
			// a string split is a quick way to identify places where a regex needs to be removed, as well as remove the colon characters
			String[] segments = path.split(":");

			// each resultant segment (except the first), will start with the regex.
			// the regex ends right before the next slash, 
			for (String segment: segments)
			{
				if (segment.contains("}"))	// all but the first segment will
				{
					if (segment.endsWith("}"))
					{
						// the last part of the path is all regex, ignore it and just close the path param
						sb.append("}");
					} else
					{
						// ignore everything until the next slash
						sb.append(segment.substring(segment.indexOf("}/")));
					}
				} else
				{
					// add the first segment as-is
					sb.append(segment);
				}
			}

			return sb.toString();
		} else
		{
			// no regex found in the path, just return it
			return path;
		}
	}

	/**
	 * Returns a String representation of the HTTPMethod based on the existence of one of the appropriate JAX-RS annotations
	 * @param method
	 * @return
	 */
	private String getHTTPMethodFromAnnotation(JMethod method)
	{
		String httpMethod;

		if (method.isAnnotationPresent(GET.class))
			httpMethod = "GET";
		else if (method.isAnnotationPresent(POST.class))
			httpMethod = "POST";
		else if (method.isAnnotationPresent(PUT.class))
			httpMethod = "PUT";
		else if (method.isAnnotationPresent(DELETE.class))
			httpMethod = "DELETE";
		else if (method.isAnnotationPresent(HEAD.class))
			httpMethod = "HEAD";
		else
			httpMethod = "GET";		// default to GET

		return httpMethod;
	}

	private boolean isResponseReaderVoid(JType type) {
		return ((type.getQualifiedSourceName().equals(javax.ws.rs.core.Response.class.getCanonicalName()))
			|| (type.isPrimitive() != null)
			|| (type.isArray() != null));
	}

	private boolean isBodyParameter(JParameter parameter) {
		return (!(parameter.isAnnotationPresent(PathParam.class)
				|| parameter.isAnnotationPresent(QueryParam.class)
				|| parameter.isAnnotationPresent(Context.class)));
	}
}
