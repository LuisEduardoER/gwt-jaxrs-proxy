package com.paullindorff.gwt.jaxrs.client.util;

/**
 * Base64 encoding utility using native JavaScript
 * Adapted from code at @see http://www.webtoolkit.info/javascript-base64.html
 * @author plindorff
 */
public class Base64 {
	private static String keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	public static native String encode(String input) /*-{
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;

		// first, utf-8 encode the string
		input = input.replace(/\r\n/g,"\n");
		var utftext = "";
		for (var n = 0; n < input.length; n++) {
			
			var c = input.charCodeAt(n);
			
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
		}

		// now, encode the utf-8 string as base64
		while (i < utftext.length) {
			
			chr1 = utftext.charCodeAt(i++);
			chr2 = utftext.charCodeAt(i++);
			chr3 = utftext.charCodeAt(i++);
			
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
			
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
			
			output = output +
			@com.paullindorff.gwt.jaxrs.client.util.Base64::keyStr.charAt(enc1) + 
			@com.paullindorff.gwt.jaxrs.client.util.Base64::keyStr.charAt(enc2) +
			@com.paullindorff.gwt.jaxrs.client.util.Base64::keyStr.charAt(enc3) +
			@com.paullindorff.gwt.jaxrs.client.util.Base64::keyStr.charAt(enc4);
		}
		
		return output;
	}-*/;
}
