package eu.dime.context.model.impl;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Arrays;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.*;
import eu.dime.context.model.impl.values.*;

/**
 * A convenience class providing access to the construction of model artifacts.
 * The supported artifacts are:
 * <ul>
 *   <li>{@link eu.dime.context.model.impl.ContextElement}</li>
 *   <li>{@link ContextValue}</li>
 *   <li>{@link Metadatum}</li>
 * </ul>
 */
public class Factory
{
    public static final IScope METADATA_TIMESTAMP_SCOPE
	    = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);

    public static final IScope METADATA_EXPIRES_SCOPE
	    = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);

    public static final String LOCAL_IP;
    static
    {
	String localIP;
	try
	{
	    localIP = InetAddress.getLocalHost().getHostAddress();
	}
	catch (UnknownHostException uhe)
	{
	    localIP = "127.0.0.1";
	}

	LOCAL_IP = localIP;
    }

    /**
     * Creates a new instance of {@link IContextElement}, as specified by the
     * arguments.
     *
     * @param entity the element's entity
     * @param scope the element's scope
     * @param source the element's source (i.e. component which generated it)
     * @param contextValueMap the element's value map (i.e. a map of its value
     * IDs pointing to the {@link IContextData}s themselves
     * @param metadata the element's metadata map (i.e. a map of its
     * metadata IDs pointing to the {@link IMetadata} themselves
     *
     * @return the newly constructed {@link IContextElement} unless an
     * exception occurs (i.e. because of invalid arguments).
     */
    static public IContextElement createContextElement(
	    final IEntity entity,
	    final IScope scope,
	    final Object source,
	    final ContextValueMap contextValueMap,
	    final IMetadata metadata)
    {
	if(entity == null || scope == null 
		|| source == null || contextValueMap == null
		|| metadata == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	final Set metadataKeys = metadata.keySet();
	if(metadataKeys.contains(METADATA_TIMESTAMP_SCOPE)
		&& metadataKeys.contains(METADATA_EXPIRES_SCOPE))
	{
	    // if the metadata already includes timestamp and expires metadata
	    // then just create the context element
	    return new ContextElement(entity, scope, 
		    source.toString(), contextValueMap, metadata);
	}
	else
	{
	    // else add the missing metadata before creating the context element
	    final IMetadata completeMetadata = createDefaultMetadata(metadata);

	    return new ContextElement(entity, scope, 
		    source.toString(), contextValueMap, completeMetadata);
	}
    }

    /**
     * Constructs a new
     * {@link eu.dime.context.model.api.IContextElement}. This method
     * is equivalent to calling {@link #createContextElement(IEntity, IScope,
     * Object, ContextValueMap, IMetadata)})})} where the
     * MetadataMap is assigned to a default instantiation which includes the
     * metadata for <i>timestamp</i> and <i>expires</i> only.
     *
     * @param entity the element's entity
     * @param scope the element's scope
     * @param source the element's source (i.e. component which generated it)
     * @param contextValueMap the element's value map (i.e. a map of its value
     * IDs pointing to the {@link IContextData}s themselves
     *
     * @return the newly constructed {@link IContextElement} unless an
     * exception occurs (i.e. because of invalid arguments).
     */
    static public IContextElement createContextElement(
	    final IEntity entity,
	    final IScope scope,
	    final Object source,
	    final ContextValueMap contextValueMap)
    {
	return createContextElement(entity, scope,
		source, contextValueMap, createDefaultMetadata());
    }

    /**
     * Constructs a new
     * {@link eu.dime.context.model.api.IContextElement}. This method
     * is equivalent to calling {@link #createContextElement(IEntity, IScope,
     * Object, ContextValueMap, IMetadata)})})} where the
     * MetadataMap is assigned to a default instantiation which includes the
     * metadata for <i>timestamp</i> and <i>expires</i> only.
     *
     * @param entity the element's entity
     * @param scope the element's scope
     * @param source the element's source (i.e. component which generated it)
     * @param contextValueMap the element's value map (i.e. a map of its value
     * IDs pointing to the {@link IContextData}s themselves
     * @param millisecondsBeforeExpiry the number of milliseconds after
     * creation that the context element expires (set in attached
     * {@link IMetadata} object); the timestamp is automatically set to the
     * current time
     *
     * @return the newly constructed {@link IContextElement} unless an
     * exception occurs (i.e. because of invalid arguments).
     */
    static public IContextElement createContextElement(
	    final IEntity entity,
	    final IScope scope,
	    final Object source,
	    final ContextValueMap contextValueMap,
	    final long millisecondsBeforeExpiry)
    {
	IMetadata metadata = createDefaultMetadata(millisecondsBeforeExpiry);

	return createContextElement(entity, scope, source,
		contextValueMap, metadata);
    }

    /**
     * Constructs a new
     * {@link eu.dime.context.model.api.IContextElement}. This method
     * is equivalent to calling {@link #createContextElement(IEntity, IScope,
     * Object, ContextValueMap, IMetadata)})})} where the
     * MetadataMap is assigned to a default instantiation which includes the
     * metadata for <i>timestamp</i> and <i>expires</i> only.
     *
     * @param entity the element's entity
     * @param scope the element's scope
     * @param source the element's source (i.e. component which generated it)
     * @param contextValueMap the element's value map (i.e. a map of its value
     * IDs pointing to the {@link IContextData}s themselves
     * @param timestamp the timestamp to be assigned in the metadata as
     * creation time (set in attached {@link IMetadata} object)
     * @param millisecondsBeforeExpiry the number of milliseconds after
     * creation that the context element expires (set in attached
     * {@link IMetadata} object)
     *
     * @return the newly constructed {@link IContextElement} unless an
     * exception occurs (i.e. because of invalid arguments).
     */
    static public IContextElement createContextElement(
	    final IEntity entity,
	    final IScope scope,
	    final Object source,
	    final ContextValueMap contextValueMap,
	    final long timestamp,
	    final long millisecondsBeforeExpiry)
    {
	IMetadata metadata = createDefaultMetadata(timestamp, millisecondsBeforeExpiry);

	return createContextElement(entity, scope, source,
		contextValueMap, metadata);
    }

    /**
     * Constructs an instance of {@link ContextValueMap}.
     *
     * @param hashMap the {@link Map} of this context values set
     *
     * @return the newly constructed {@link ContextValueMap}
     */
    static public ContextValueMap createContextValueMap(final Map hashMap)
    {
	return new ContextValueMap(hashMap);
    }

    static public ContextValueMap createContextValueMap(final IContextData contextData)
    {
	return new ContextValueMap(contextData);
    }

    /**
     * Constructs an instance of {@link MetadataMap}.
     *
     * @param hashMap the {@link Map} of this metadata set
     *
     * @return the newly constructed {@link IMetadata}
     */
    static public IMetadata createMetadata(final Map hashMap)
    {
	return new MetadataMap(hashMap);
    }

    /** Default value of time before expiry is 1 hour */
    public static final long DEFAULT_MILLISECONDS_BEFORE_EXPIRY = 60 * 60 * 1000L;

    /**
     * Creates a default instance of {@link IMetadata} where the only values
     * mapped are concerning the <i>timestamp</i> and the <i>expires</i>
     * values of the corresponding {@link IContextElement}.
     *
     * The <i>timestamp</i> is set to "now" and the <i>expires</i> is adjusted
     * to {@link #DEFAULT_MILLISECONDS_BEFORE_EXPIRY} milliseconds (an hour)
     * after creation.
     *
     * @return a default implementation of the {@link IMetadata}
     */
    static public IMetadata createDefaultMetadata()
    {
	return createDefaultMetadata(DEFAULT_MILLISECONDS_BEFORE_EXPIRY);
    }

    /**
     * Creates a default instance of {@link IMetadata} where the values of the
     * <i>timestamp</i> and the <i>expires</i> are added to those of the given
     * metadata if missing.
     *
     * If the <i>timestamp</i> is missing then it is set to "now" and if the
     * <i>expires</i> is missing it is adjusted to
     * {@link #DEFAULT_MILLISECONDS_BEFORE_EXPIRY} milliseconds (an hour)
     * after creation.
     *
     * @param metadata the original metadata
     *
     * @return a default implementation of the {@link IMetadata} containing the
     * timestamp and expiry values in addition to those in the original one
     */
    static IMetadata createDefaultMetadata(final IMetadata metadata)
    {
	final Map returnedMetadata = new HashMap();

	final Set originalMetadataKeys = metadata.keySet();
	for(final Iterator iterator = originalMetadataKeys.iterator();
							iterator.hasNext(); )
	{
	    final IScope scope = (IScope) iterator.next();
	    returnedMetadata.put(scope, metadata.getMetadatum(scope));
	}

	// check for missing timestamp ...
	if(!originalMetadataKeys.contains(METADATA_TIMESTAMP_SCOPE))
	{
	    // set timestamp as now
	    final String creationTimestampAsXMLString
		    = Factory.timestampAsXMLString(new Date());

	    final IMetadatum creationTimestampMetadatum
		    = Factory.createMetadatum(
			Factory.METADATA_TIMESTAMP_SCOPE,
			Factory.createValue(creationTimestampAsXMLString));

	    returnedMetadata.put(
		    METADATA_TIMESTAMP_SCOPE, creationTimestampMetadatum);
	}

	// check for missing expiry ...
	if(!originalMetadataKeys.contains(METADATA_EXPIRES_SCOPE))
	{
	    final long expiryTimestamp = System.currentTimeMillis()
		    + DEFAULT_MILLISECONDS_BEFORE_EXPIRY;

	    final String expiryTimestampAsXMLString
		    = Factory.timestampAsXMLString(new Date(expiryTimestamp));

	    final IMetadatum expiryTimestampMetadatum
		    = Factory.createMetadatum(
			Factory.METADATA_EXPIRES_SCOPE,
			Factory.createValue(expiryTimestampAsXMLString));

	    returnedMetadata.put(
		    METADATA_EXPIRES_SCOPE, expiryTimestampMetadatum);
	}

	return Factory.createMetadata(returnedMetadata);
    }

    /**
     * Creates a default instance of {@link IMetadata} where the only values
     * mapped are concerning the <i>timestamp</i> and the <i>expires</i>
     * values of the corresponding {@link IContextElement}.
     *
     * The <i>timestamp</i> is set to "now" and the <i>expires</i> is adjusted
     * to a number of milliseconds after creation, as specified by the
     * parameter.
     *
     * @param millisecondsBeforeExpiry the number of milliseconds after the
     * creation of the metadata at which the expiry time is set
     * @return a default implementation of the {@link IMetadata}
     */
    static public IMetadata createDefaultMetadata(final long millisecondsBeforeExpiry)
    {
	return new DefaultTimestampAndExpiresMetadata(millisecondsBeforeExpiry);
    }

    static public IMetadata createDefaultMetadata(
	    final long timestamp, final long millisecondsBeforeExpiry)
    {
	return new DefaultTimestampAndExpiresMetadata(timestamp, millisecondsBeforeExpiry);
    }

    /**
     * Constructs an instance of {@link Value}.
     *
     * @param value the value of this IValue object
     *
     * @return the newly constructed {@link IValue}
     * @throws NullPointerException if the specified value is null
     * @throws IllegalArgumentException if the specified value is not of a
     * defined type (i.e. Boolean, Integer, Long, Double or String)
     */
    static public IValue createValue(final Object value)
    {
	if(value == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}
	IValue res = null;

	final ValueType valueType;
	if (value instanceof Boolean)
	{
	    valueType = ValueType.BOOLEAN_VALUE_TYPE;
	    res = createValue(valueType, value.toString());
	}
	else if (value instanceof Integer)
	{
	    valueType = ValueType.INTEGER_VALUE_TYPE;
	    res = createValue(valueType, value.toString());
	}
	else if (value instanceof Long)
	{
	    valueType = ValueType.LONG_VALUE_TYPE;
	    res = createValue(valueType, value.toString());
	}
	else if (value instanceof Double)
	{
	    valueType = ValueType.DOUBLE_VALUE_TYPE;
	    res = createValue(valueType, value.toString());
	}
	else if (value instanceof String)
	{
	    valueType = ValueType.STRING_VALUE_TYPE;
	    res = createValue(valueType, value.toString());
	}
	else if (value instanceof Boolean [])
	{
	    valueType = ValueType.ARRAY_OF_BOOLEANS_VALUE_TYPE;
	    Boolean[] bArr = (Boolean[])value;
	    boolean[] boolArr = new boolean[bArr.length];
	    for (int i=0;i<bArr.length; i++)
	    	boolArr[i] = bArr[i];
	    res = createValue(valueType, new ArrayOfBooleansValue(boolArr).toString());
	}
	else if (value instanceof Integer [])
	{
	    valueType = ValueType.ARRAY_OF_INTEGERS_VALUE_TYPE;
	    Integer[] iArr = (Integer[])value;
	    int[] intArr = new int[iArr.length];
	    for (int i=0;i<iArr.length; i++)
	    	intArr[i] = iArr[i];
	    res = createValue(valueType, new ArrayOfIntegersValue(intArr).toString());
	}
	else if (value instanceof Long [])
	{
	    valueType = ValueType.ARRAY_OF_LONGS_VALUE_TYPE;
	    Long[] lArr = (Long[])value;
	    long[] longArr = new long[lArr.length];
	    for (int i=0;i<lArr.length; i++)
	    	longArr[i] = lArr[i];
	    res = createValue(valueType, new ArrayOfLongsValue(longArr).toString());
	}
	else if (value instanceof Double [])
	{
	    valueType = ValueType.ARRAY_OF_DOUBLES_VALUE_TYPE;
	    Double[] dArr = (Double[])value;
	    double[] doubleArr = new double[dArr.length];
	    for (int i=0;i<dArr.length; i++)
	    	doubleArr[i] = dArr[i];
	    res = createValue(valueType, new ArrayOfDoublesValue(doubleArr).toString());
	}
	else if (value instanceof String [])
	{
	    valueType = ValueType.ARRAY_OF_STRINGS_VALUE_TYPE;
	    res = createValue(valueType, new ArrayOfStringsValue((String[])value).toString());
	}
	else
	{
	    throw new IllegalArgumentException("Type not supported: "
		    + value.getClass());
	}

	return res;
	//return createValue(valueType, value.toString());
    }
/*    static public IValue createValue(final Object value)
    {
	if(value == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	final ValueType valueType;
	if (value instanceof Boolean)
	{
	    valueType = ValueType.BOOLEAN_VALUE_TYPE;
	}
	else if (value instanceof Integer)
	{
	    valueType = ValueType.INTEGER_VALUE_TYPE;
	}
	else if (value instanceof Long)
	{
	    valueType = ValueType.LONG_VALUE_TYPE;
	}
	else if (value instanceof Double)
	{
	    valueType = ValueType.DOUBLE_VALUE_TYPE;
	}
	else if (value instanceof String)
	{
	    valueType = ValueType.STRING_VALUE_TYPE;
	}
	else if (value instanceof Boolean [])
	{
	    valueType = ValueType.ARRAY_OF_BOOLEANS_VALUE_TYPE;
	}
	else if (value instanceof Integer [])
	{
	    valueType = ValueType.ARRAY_OF_INTEGERS_VALUE_TYPE;
	}
	else if (value instanceof Long [])
	{
	    valueType = ValueType.ARRAY_OF_LONGS_VALUE_TYPE;
	}
	else if (value instanceof Double [])
	{
	    valueType = ValueType.ARRAY_OF_DOUBLES_VALUE_TYPE;
	}
	else if (value instanceof String [])
	{
	    valueType = ValueType.ARRAY_OF_STRINGS_VALUE_TYPE;
	}
	else
	{
	    throw new IllegalArgumentException("Type not supported: "
		    + value.getClass());
	}

	return createValue(valueType, value.toString());
    }*/

    static public IValue createValue(final ValueType valueType, final String valueS)
    {
	if(ValueType.BOOLEAN_VALUE_TYPE == valueType)
	{
	    return new BooleanValue(Boolean.valueOf(valueS));
	}
	else if(ValueType.INTEGER_VALUE_TYPE == valueType)
	{
	    return new IntegerValue(Integer.parseInt(valueS));
	}
	else if(ValueType.LONG_VALUE_TYPE == valueType)
	{
	    return new LongValue(Long.parseLong(valueS));
	}
	else if(ValueType.FLOAT_VALUE_TYPE == valueType)
	{
	    return new FloatValue(Float.parseFloat(valueS));
	}
	else if(ValueType.DOUBLE_VALUE_TYPE == valueType)
	{
	    return new DoubleValue(Double.parseDouble(valueS));
	}
	else if(ValueType.STRING_VALUE_TYPE == valueType)
	{
	    return new StringValue(valueS);
	}
	else if(ValueType.ARRAY_OF_BOOLEANS_VALUE_TYPE == valueType)
	{
	    return ArrayOfBooleansValue.parse(valueS);
	}
	else if(ValueType.ARRAY_OF_INTEGERS_VALUE_TYPE == valueType)
	{
	    return ArrayOfIntegersValue.parse(valueS);
	}
	else if(ValueType.ARRAY_OF_LONGS_VALUE_TYPE == valueType)
	{
	    return ArrayOfLongsValue.parse(valueS);
	}
	else if(ValueType.ARRAY_OF_FLOATS_VALUE_TYPE == valueType)
	{
	    return ArrayOfFloatsValue.parse(valueS);
	}
	else if(ValueType.ARRAY_OF_DOUBLES_VALUE_TYPE == valueType)
	{
	    return ArrayOfDoublesValue.parse(valueS);
	}
	else if(ValueType.ARRAY_OF_STRINGS_VALUE_TYPE == valueType)
	{
	    return ArrayOfStringsValue.parse(valueS);
	}
	else
	{
	    throw new IllegalArgumentException("Unknown value type: " + valueType);
	}
    }

    static public IValue createValue(final boolean value)
    {
	return new Value(value);
    }

    static public IValue createValue(final int value)
    {
	return new Value(value);
    }

    static public IValue createValue(final long value)
    {
	return new Value(value);
    }

    static public IValue createValue(final double value)
    {
	return new Value(value);
    }
    
    static public IValue createValue(final boolean[] value)
    {
	return new ArrayOfBooleansValue(value);
    }

    static public IValue createValue(final int[] value)
    {
	return new ArrayOfIntegersValue(value);
    }

    static public IValue createValue(final long[] value)
    {
	return new ArrayOfLongsValue(value);
    }

    static public IValue createValue(final double[] value)
    {
	return new ArrayOfDoublesValue(value);
    }

    /**
     * Creates an instance of {@link IEntity} which corresponds to the given
     * string.
     *
     * If the given string is of the type "http://url#entityPath", then the
     * resulting entity's Ontology is "http://url" and the entity itself is
     * encoded by the "entityPath" string.
     *
     * Otherwise, if the given string is of the type "#entityPath", then the
     * resulting entity's Ontology is the default one:
     * {@link  Constants#DEFAULT_ONTOLOGY}
     * (e.g., http://servername/Ontology_v0_1.xml).
     *
     * @param entity a non-null, non empty string
     * @return an instance of {@link IEntity} corresponding to the input string
     */
    static public IEntity createEntity(final String entity)
    {
	if(entity == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	if(entity.startsWith("#"))
	{
	    // assume default ontology
	    return Entity.createEntity(Constants.DEFAULT_ONTOLOGY /*IOntologyService.DEFAULT_ONTOLOGY*/
		    + entity);
	}
	else // assert entity.startsWith("http://")
	{
	    return Entity.createEntity(entity);
	}
    }

    /**
     * Creates an instance of {@link IScope} which corresponds to the given
     * string.
     *
     * If the given string is of the type "http://url#entityPath", then the
     * resulting scope's Ontology is "http://url" and the entity itself is
     * encoded by the "entityPath" string.
     *
     * Otherwise, if the given string is of the type "#entityPath", then the
     * resulting scope's Ontology is the default one:
     * {@link  Constants#DEFAULT_ONTOLOGY}
     * (e.g., http://servername/Ontology_v0_1.xml).
     *
     * @param scope a non-null, non empty string
     * @return an instance of {@link IScope} corresponding to the input string
     */
    static public IScope createScope(final String scope)
    {
	if(scope == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	if(scope.startsWith("#"))
	{
	    // assume default ontology
	    return Scope.createScope(Constants.DEFAULT_ONTOLOGY /*IOntologyService.DEFAULT_ONTOLOGY*/ + scope);
	}
	else // assert scope.startsWith("http://")
	{
	    return Scope.createScope(scope);
	}
    }

    static public IContextValue createContextValue(
	    final IScope scope,
	    final IValue value)
    {
	return new ContextValue(scope, value);
    }

    static public IMetadatum createMetadatum(
	    final IScope scope,
	    final IValue value)
    {
	return new Metadatum(scope, value);
    }

    static public IContextDataset createContextDataset(final IContextElement contextElement)
    {
	return new ContextDataset(contextElement);
    }

    static public IContextDataset createContextDataset(final IContextElement contextElement, final String timeRef)
    {
	return new ContextDataset(contextElement,timeRef);
    }
    
    static public IContextDataset createContextDataset(final IContextElement [] contextElements)
    {
	return new ContextDataset(contextElements);
    }
    
    static public IContextDataset createContextDataset(final IContextElement [] contextElements, final String timeRef)
    {
	return new ContextDataset(contextElements,timeRef);
    }
    
    public static final SimpleDateFormat cacheDateFormat
    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat simpleDateFormat
	    = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    static public String timestampAsXMLString(final Date date)
    {
	final String str = simpleDateFormat.format(date);

	// fixes the format from "2009-01-09T10:10:32+0200" to "2009-01-09T10:10:32+02:00"
	return (str.substring(0, str.length() - 2) + ":" + str.substring(str.length() - 2, str.length()));
    }

    static public long timestampFromXMLString(final String timestampS)
    {
	final String str = timestampS.substring(0, timestampS.length() - 3)
		+ timestampS.substring(timestampS.length() - 2, timestampS.length());

	try
	{
	    return simpleDateFormat.parse(str).getTime();
	}
	catch (ParseException pe)
	{
	    throw new RuntimeException(pe);
	}
    }

/*    static public long timestampFromContextElement(final IContextElement contextElement)
    {
	if(contextElement == null) throw new NullPointerException();

	final IMetadata metadata = contextElement.getMetadata();
	final IMetadatum metadatum = metadata == null ?
		null : metadata.getMetadatum(METADATA_TIMESTAMP_SCOPE);

	if(metadatum == null) return 0;

	final String timestampS = metadatum.getValue().getValue().toString();

	return timestampFromXMLString(timestampS);
	
    }
    
    static public long expiresFromContextElement(final IContextElement contextElement)
    {
	if(contextElement == null) throw new NullPointerException();

	final IMetadata metadata = contextElement.getMetadata();
	final IMetadatum metadatum = metadata == null ?
		null : metadata.getMetadatum(METADATA_EXPIRES_SCOPE);

	if(metadatum == null) return 0;

	final String expS = metadatum.getValue().getValue().toString();

	return timestampFromXMLString(expS);
	
    }*/

    /**
     * It creates a new replica of the specified context element but with
     * updated metadata (i.e., new timestamp and millisecondsBeforeExpiry). The
     * new metadata is created using default values as specified in method
     * {@link #createDefaultMetadata()}.
     *
     * @param contextElement the one to be cloned
     * @return a replica of the given context element with updated metadata
     */
    static public IContextElement cloneContextElementWithNewMetadata(
	    final IContextElement contextElement)
    {
	if(contextElement == null) throw new NullPointerException("Illegal null argument");

	return Factory.createContextElement(
		contextElement.getEntity(),
		contextElement.getScope(),
		contextElement.getSource(),
		getContextValueMap(contextElement.getContextData()),
		createDefaultMetadata());
    }

    /**
     * It creates a new replica of the specified context element but with
     * updated metadata (i.e., new timestamp and millisecondsBeforeExpiry). The
     * new metadata must be explicitly specified in the invocation as arguments.
     *
     * @param contextElement the one to be cloned
     * @param timestamp the new value to be set in metadata
     * @param millisecondsBeforeExpiry the new value to be set in metadata
     * @return a replica of the given context element with updated metadata
     */
    static public IContextElement cloneContextElementWithNewMetadata(
	    final IContextElement contextElement,
	    final long timestamp,
	    final long millisecondsBeforeExpiry)
    {
	if(contextElement == null) throw new NullPointerException("Illegal null argument");

	return Factory.createContextElement(
		contextElement.getEntity(),
		contextElement.getScope(),
		contextElement.getSource(),
		getContextValueMap(contextElement.getContextData()),
		createDefaultMetadata(timestamp, millisecondsBeforeExpiry));
    }

    static private ContextValueMap getContextValueMap(final IContextData contextData)
    {
	final Map map = new HashMap();

	final Set keys = contextData.keySet();
	for(Iterator keyIterator = keys.iterator(); keyIterator.hasNext(); )
	{
	    final IScope key = (IScope) keyIterator.next();
	    map.put(key, contextData.getContextValue(key));
	}

	return createContextValueMap(map);
    }
    
    /**
     * It creates a new replica of the specified context element but with
     * updated metadata (i.e., new timestamp and millisecondsBeforeExpiry),
     * resynchronized on the specified refTime.
     *
     * @param contextElement the one to be cloned
     * @param refTime the new value to be set in metadata
     * @return a replica of the given context element with updated metadata
     */
    static public IContextElement cloneResynchdContextElement(
	    final IContextElement contextElement,
	    final long refTime)
    {
    	long ts = contextElement.getTimestampAsLong();
    	long exp = contextElement.getExpiresAsLong();
    	return Factory.cloneContextElementWithNewMetadata(contextElement, refTime, exp-ts);
    }
    
    /**
     * It creates a new replica of the specified context element but with
     * updated metadata (i.e., new timestamp and millisecondsBeforeExpiry),
     * resynchronized on the specified refTime.
     *
     * @param contextElement the one to be cloned
     * @param sentAtParam the reference time set by the client (i.e. sentAt, -1 if not present)
     * @param refTime the reference time of reception (i.e. timeRef of the server)
     * @return a replica of the given context element with updated metadata
     */
    static public IContextElement cloneResynchdContextElement(
	    final IContextElement contextElement,
	    final long sentAtParam,
	    final long refTime) throws Exception
    {
    	long ts = contextElement.getTimestampAsLong();
    	long exp = contextElement.getExpiresAsLong();
    	
    	long sentAt;
        if (sentAtParam==-1) // sentAt not present
        	sentAt = ts;
        else if (sentAtParam>=ts){ 
        	sentAt = sentAtParam;
        }else
        	throw new Exception("timestamp in a context element cannot be more recent than timeRef");
        long newTimeStamp = ts + refTime - sentAt;
           
    	return Factory.cloneContextElementWithNewMetadata(contextElement, newTimeStamp, exp-ts);
    }
}
