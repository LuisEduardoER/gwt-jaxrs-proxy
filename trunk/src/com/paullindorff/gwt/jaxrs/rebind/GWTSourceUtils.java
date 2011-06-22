package com.paullindorff.gwt.jaxrs.rebind;

import javax.ws.rs.core.Context;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * This utility class contains those methods lifted verbatim from GWT source
 * @author plindorff
 *
 */
public class GWTSourceUtils {
	/**
	 * <b>NOTE: from com.google.gwt.user.rebind.rpc.RemoteServiceAsyncValidator</b>, v1.6,
	  modified to skip parameters with the JAX-RS Context annotation
	 * @param method
	 * @return
	 */
	public static String computeInternalSignature(JMethod method, TreeLogger logger) {
		StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		sb.append(method.getName());
		JParameter[] params = method.getParameters();
		for (JParameter param : params) {
			if (param.isAnnotationPresent(Context.class)) {
				logger.log(TreeLogger.INFO, "skipping param '" + param.getName() + "': Context annotation found");
			} else {
				sb.append("/");
				JType paramType = param.getType();
				sb.append(paramType.getErasedType().getQualifiedSourceName());
			}
		}
		return sb.toString();
	}

	/**
	 * <b>NOTE: from com.google.gwt.user.rebind.rpc.Shared</b>, v1.6
	 * 
	 * Computes a good name for a class related to the specified type, such that
	 * the computed name can be a top-level class in the same package as the
	 * specified type.
	 * 
	 * <p>
	 * This method does not currently check for collisions between the synthesized
	 * name and an existing top-level type in the same package. It is actually
	 * tricky to do so, because on subsequent runs, we'll view our own generated
	 * classes as collisions. There's probably some trick we can use in the future
	 * to make it totally bulletproof.
	 * </p>
	 * 
	 * @param type the name of the base type, whose name will be built upon to
	 *          synthesize a new type name
	 * @param suffix a suffix to be used to make the new synthesized type name
	 * @return an array of length 2 such that the first element is the package
	 *         name and the second element is the synthesized class name
	 */
	public static String[] synthesizeTopLevelClassName(JClassType type, String suffix) {
		// Gets the basic name of the type. If it's a nested type, the type name
		// will contains dots.
		//
		String className;
		String packageName;

		JType leafType = type.getLeafType();
		if (leafType.isPrimitive() != null) {
			className = leafType.getSimpleSourceName();
			packageName = "";
		} else {
			JClassType classOrInterface = leafType.isClassOrInterface();
			assert (classOrInterface != null);
			className = classOrInterface.getName();
			packageName = classOrInterface.getPackage().getName();
		}

		JArrayType isArray = type.isArray();
		if (isArray != null) {
			className += "_Array_Rank_" + isArray.getRank();
		}

		// Add the meaningful suffix.
		//
		className += suffix;

		// Make it a top-level name.
		//
		className = className.replace('.', '_');

		return new String[] {packageName, className};
	}

	/**
	 * <b>NOTE: from com.google.gwt.user.rebind.rpc.ProxyCreator</b>, v1.6<br>
	 * (this method did not exist on its own, but is instead the extraction of logic
	 *   from GWT's ProxyCreator used to generate the Async service interfaces for GWT-RPC;
	 *   the same logic is required for our REST proxy generation, so it was extracted as a unit)
	 * @param method
	 * @return
	 */
	public static JMethod getGenericVersion(JMethod method) {
		JMethod result = method;
		JClassType enclosingType = method.getEnclosingType();
		JParameterizedType isParameterizedType = enclosingType.isParameterized();
		if (isParameterizedType != null) {
			JMethod[] methods = isParameterizedType.getMethods();
			for (int i = 0; i < methods.length; ++i) {
				if (methods[i] == method) {
					/*
					* Use the generic version of the method to ensure that the server
					* can find the method using the erasure of the generic signature.
					*/
					result = isParameterizedType.getBaseType().getMethods()[i];
				}
			}
		}

		return result;
	}
}
