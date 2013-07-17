package eu.dime.ps.gateway.userresolver.client.entities;

public class ErrorResponse {
	
	int code;
	private String error;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public ErrorResponse(int code, String error) {
		this.code = code;
		this.error = error;
	}

}
