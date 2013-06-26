/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl;

import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IMetadatum;

import java.util.Set;
import java.util.HashSet;
import java.util.Date;

/**
 * Default implementation of the {@link IMetadata} interface with the
 * timestamp and expires values only.
 */
class DefaultTimestampAndExpiresMetadata implements IMetadata
{
    IMetadatum creationTimestampMetadatum;
    IMetadatum expiryTimestampMetadatum;

    DefaultTimestampAndExpiresMetadata(long millisecondsBeforeExpiry)
    {
	this(System.currentTimeMillis(), millisecondsBeforeExpiry);
    }

    DefaultTimestampAndExpiresMetadata(long creationTimestamp, long millisecondsBeforeExpiry)
    {
	if(millisecondsBeforeExpiry < 0L) millisecondsBeforeExpiry = 0L;

	final long expiryTimestamp = creationTimestamp + millisecondsBeforeExpiry;

	final String creationTimestampAsXMLString
		= Factory.timestampAsXMLString(new Date(creationTimestamp));
	final String expiryTimestampAsXMLString
		= Factory.timestampAsXMLString(new Date(expiryTimestamp));

	creationTimestampMetadatum = Factory.createMetadatum(
		Factory.METADATA_TIMESTAMP_SCOPE,
		Factory.createValue(creationTimestampAsXMLString));

	expiryTimestampMetadatum = Factory.createMetadatum(
		Factory.METADATA_EXPIRES_SCOPE,
		Factory.createValue(expiryTimestampAsXMLString));
    }

    public IValue getMetadatumValue(IScope scopeKey)
    {
	if(Factory.METADATA_TIMESTAMP_SCOPE.equals(scopeKey))
	{
	    return creationTimestampMetadatum.getValue();
	}
	else if(Factory.METADATA_EXPIRES_SCOPE.equals(scopeKey))
	{
	    return expiryTimestampMetadatum.getValue();
	}
	else
	{
	    return null;
	}
    }

    public IMetadatum getMetadatum(IScope scopeKey)
    {
	if(Factory.METADATA_TIMESTAMP_SCOPE.equals(scopeKey))
	{
	    return creationTimestampMetadatum;
	}
	else if(Factory.METADATA_EXPIRES_SCOPE.equals(scopeKey))
	{
	    return expiryTimestampMetadatum;
	}
	else
	{
	    return null;
	}
    }

    public Set keySet()
    {
	final Set keySet = new HashSet();

	keySet.add(Factory.METADATA_TIMESTAMP_SCOPE);
	keySet.add(Factory.METADATA_EXPIRES_SCOPE);

	return keySet;
    }

    public String toString()
    {
	return "[creationTimestampMetadatum -> " + creationTimestampMetadatum +
		"expiryTimestampMetadatum -> " + expiryTimestampMetadatum + "]";
    }
}
