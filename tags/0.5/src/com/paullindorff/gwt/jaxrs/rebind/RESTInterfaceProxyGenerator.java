package com.paullindorff.gwt.jaxrs.rebind;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.paullindorff.gwt.jaxrs.client.proxy.RequestBodyWriter;
import com.paullindorff.gwt.jaxrs.client.proxy.ResponseReader;

/**
 * Generator that produces proxies for the REST client version of a WebServiceResource interface
 * @author plindorff
 *
 */
public class RESTInterfaceProxyGenerator extends Generator
{
	private static final String PROPERTY_RESTINTERFACEPACKAGE = "sst.restInterfacePackage";
	private static final String PROPERTY_JSOPACKAGE = "sst.jsoPackage";
	private static final String RESPONSEREADER_CLASSNAME = ResponseReader.class.getCanonicalName();

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
	{
		// get the configuration property specifying the package that contains the REST interface definitions
		String restInterfacePackageName = lookupConfigurationProperty(logger, context, PROPERTY_RESTINTERFACEPACKAGE);
    	logger.log(TreeLogger.INFO, "using REST interface package: " + restInterfacePackageName);

		// get the configuration property specifying the package that contains the REST interface definitions
		String jsoPackageName = lookupConfigurationProperty(logger, context, PROPERTY_JSOPACKAGE);
    	logger.log(TreeLogger.INFO, "using JSO package: " + jsoPackageName);

    	// get the type oracle for retrieval of interface definitions
	    TypeOracle typeOracle = context.getTypeOracle();
	    assert (typeOracle != null);

	    // lookup the "resource" interface
	    JClassType resourceInterface = typeOracle.findType(typeName);
	    if (resourceInterface == null) {
	    	logger.log(TreeLogger.ERROR, "unable to find metadata for type '" + typeName + "'", null);
			throw new UnableToCompleteException();
		}

	    // check to make sure what we found is an interface (it should be, or we grabbed the wrong definition)
	    if (resourceInterface.isInterface() == null) {
			logger.log(TreeLogger.ERROR, resourceInterface.getQualifiedSourceName() + " is not an interface", null);
			throw new UnableToCompleteException();
	    }

	    // lookup the ResponseReader interface
	    JClassType responseReaderInterface = typeOracle.findType(RESPONSEREADER_CLASSNAME);
	    if (responseReaderInterface == null) {
	    	logger.log(TreeLogger.ERROR, "unable to find metadata for '" + RESPONSEREADER_CLASSNAME + "'", null);
			throw new UnableToCompleteException();
		}

	    // check to make sure what we found is an interface (it should be, or we grabbed the wrong definition)
	    if (responseReaderInterface.isInterface() == null) {
			logger.log(TreeLogger.ERROR, responseReaderInterface.getQualifiedSourceName() + " is not an interface", null);
			throw new UnableToCompleteException();
	    }

	    // lookup the package containing the REST interface definitions
	    JPackage restInterfacePackage = typeOracle.findPackage(restInterfacePackageName);
	    if (restInterfacePackage == null) {
	    	logger.log(TreeLogger.ERROR, "unable to find metadata for package '" + restInterfacePackageName + "'", null);
	    	throw new UnableToCompleteException();
	    }

	    // lookup the package containing the JSO implementations
	    JPackage jsoPackage = typeOracle.findPackage(jsoPackageName);
	    if (jsoPackage == null) {
	    	logger.log(TreeLogger.ERROR, "unable to find metadata for package '" + jsoPackageName + "'", null);
	    	throw new UnableToCompleteException();
	    }

	    // branch the logger for use by the proxyCreator
	    TreeLogger proxyLogger = logger.branch(TreeLogger.INFO, "generating proxy for resource interface '"
	    															+ resourceInterface.getQualifiedSourceName()
	    															+ "' in package '" + restInterfacePackage.getName()
	    															+ "'", null);

	    // delegate to the proxyCreator for implementation of the Proxy class for the REST interface that matches the Resource interface
	    RESTProxyCreator proxyCreator = new RESTProxyCreator(resourceInterface, responseReaderInterface, restInterfacePackage, jsoPackage);
	    return proxyCreator.create(proxyLogger,	context);
	}

	/**
	 * retrieve a configuration property, specified in a module XML file
	 * @param logger
	 * @param context
	 * @param name
	 * @return
	 * @throws UnableToCompleteException
	 */
	private String lookupConfigurationProperty(TreeLogger logger, GeneratorContext context, String name) throws UnableToCompleteException
	{
	    try
	    {
	    	ConfigurationProperty property = context.getPropertyOracle().getConfigurationProperty(name);
	    	if (property.getValues().size() > 0)
	    	{
	    		return property.getValues().get(0);
	    	} else
	    	{
		    	logger.log(TreeLogger.ERROR, "no value defined for property '" + name + "'");
		    	throw new UnableToCompleteException();
	    	}
	    } catch (BadPropertyValueException bpve)
	    {
	    	logger.log(TreeLogger.ERROR, "unable to retrieve property '" + name + "': " + bpve);
	    	throw new UnableToCompleteException();
	    }
	}
}
