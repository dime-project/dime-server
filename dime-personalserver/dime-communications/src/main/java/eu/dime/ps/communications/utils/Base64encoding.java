/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.ps.communications.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64encoding {

	public static String encode(String id) throws UnsupportedEncodingException {
		return Base64.encodeBase64URLSafeString(id.getBytes("UTF-8"));
	}

	public static String decode(String encodedId) throws UnsupportedEncodingException {
		byte[] bytes = Base64.decodeBase64(encodedId);
		String decodedDbId = new String(bytes, "UTF-8");
		return decodedDbId;
	}

}
