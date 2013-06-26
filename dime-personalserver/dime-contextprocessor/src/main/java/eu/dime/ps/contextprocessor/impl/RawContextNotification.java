package eu.dime.ps.contextprocessor.impl;

public class RawContextNotification {
	
	private Long tenant;
	private String itemID;
	private String name;
	private String itemType;
	private String operation;
	private String sender;
	private String target;

	public Long getTenant() {
		return tenant;
	}

	public void setTenant(Long tenant) {
		this.tenant = tenant;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getItemID() {
		return itemID;
	}

	public String getName() {
		return name;
	}

	public String getItemType() {
		return itemType;
	}

	public String getOperation() {
		return operation;
	}

	public String getSender() {
		return sender;
	}

	public String getTarget() {
		return target;
	}

}
