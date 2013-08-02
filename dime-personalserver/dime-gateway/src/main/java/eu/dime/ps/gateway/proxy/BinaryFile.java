/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

package eu.dime.ps.gateway.proxy;

import java.io.InputStream;

public class BinaryFile {
	
	private InputStream byteStream;
	private String type;
	public InputStream getByteStream() {
		return byteStream;
	}
	public void setByteStream(InputStream byteStream) {
		this.byteStream = byteStream;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BinaryFile(InputStream byteStream, String type) {
		super();
		this.byteStream = byteStream;
		this.type = type;
	}
	public BinaryFile() {
		// TODO Auto-generated constructor stub
	}

}
