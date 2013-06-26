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
