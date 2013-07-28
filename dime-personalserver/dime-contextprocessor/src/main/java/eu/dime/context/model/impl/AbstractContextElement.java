package eu.dime.context.model.impl;

import eu.dime.context.model.api.*;

import java.io.Serializable;

/**
 * An abstract implementation of the context element.
 */
abstract public class AbstractContextElement implements IContextElement // which extends Serializable
{
    private final IEntity entity;
    private final IScope scope;
    private final String source;
    private final IMetadata metadata;

    /**
     * Empty constructor needed for serialization
     */
    public AbstractContextElement()
    {
	this(null, null, null, null);
    }

    public AbstractContextElement(
	    final IEntity entity,
	    final IScope scope,
	    final String source)
    {
	this(entity, scope, source, IMetadata.EMPTY_METADATA);
    }

    public AbstractContextElement(
	    final IEntity entity,
	    final IScope scope,
	    final String source,
	    final IMetadata metadata)
    {
	super();

	this.entity = entity;
	this.scope = scope;
	this.source = source;
	this.metadata = metadata;
    }

    public IEntity getEntity()
    {
	return entity;
    }

    public IScope getScope()
    {
	return scope;
    }

    public String getSource()
    {
	return source;
    }

    public IMetadata getMetadata()
    {
	return metadata;
    }
    
    public long getTimestampAsLong()
    {
    return Factory.timestampFromXMLString(getTimestampAsString());
    }
    
    public long getExpiresAsLong()
    {
    return Factory.timestampFromXMLString(getExpiresAsString());   	
    }

    public String getTimestampAsString()
    {
    final IMetadata metadata = getMetadata();
    final IMetadatum metadatum = metadata == null ?
    	null : metadata.getMetadatum(Factory.METADATA_TIMESTAMP_SCOPE);

    if(metadatum == null) return null;

    return metadatum.getValue().getValue().toString();
    }

    public String getExpiresAsString()
    {
    final IMetadata metadata = getMetadata();
    final IMetadatum metadatum = metadata == null ?
    		null : metadata.getMetadatum(Factory.METADATA_EXPIRES_SCOPE);

    if(metadatum == null) return null;

    return metadatum.getValue().getValue().toString();
    }
    
    public boolean isValid()
    {
    final long now = System.currentTimeMillis();
    	
    return getTimestampAsLong() < now && now < getExpiresAsLong();
    }
}
