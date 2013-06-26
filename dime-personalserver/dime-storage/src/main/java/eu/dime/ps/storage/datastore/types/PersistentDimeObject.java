package eu.dime.ps.storage.datastore.types;

public abstract class PersistentDimeObject {
	/* identifier in the dime world (rdf store etc) */
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Override if things should be done before deletion, eg delete blobs
	 */
	public void onDelete() {		
	}
	
}
