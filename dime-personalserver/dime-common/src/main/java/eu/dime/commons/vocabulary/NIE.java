package eu.dime.commons.vocabulary;

public interface NIE {

	public static final String NS_NIE = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#";

    /**
     * Label: InformationElement 
     * Comment: A unit of content the user works with. This is a superclass for all interpretations of a DataObject. 
     */
    public static final String InformationElement = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement";

    /**
     * Label: DataSource 
     * Comment: A superclass for all entities from which DataObjects can be extracted. Each entity represents a native application or some other system that manages information that may be of interest to the user of the Semantic Desktop. Subclasses may include FileSystems, Mailboxes, Calendars, websites etc. The exact choice of subclasses and their properties is considered application-specific. Each data extraction application is supposed to provide it's own DataSource ontology. Such an ontology should contain supported data source types coupled with properties necessary for the application to gain access to the data sources.  (paths, urls, passwords  etc...) 
     */
    public static final String DataSource = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource";

    /**
     * Label: DataObject 
     * Comment: A unit of data that is created, annotated and processed on the user desktop. It represents a native structure the user works with. The usage of the term 'native' is important. It means that a DataObject can be directly mapped to a data structure maintained by a native application. This may be a file, a set of files or a part of a file. The granularity depends on the user. This class is not intended to be instantiated by itself. Use more specific subclasses. 
     */
    public static final String DataObject = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject";

    /**
     * Label: characterSet 
     * Comment: Characterset in which the content of the InformationElement was created. Example: ISO-8859-1, UTF-8. One of the registered character sets at http://www.iana.org/assignments/character-sets. This characterSet is used to interpret any textual parts of the content. If more than one characterSet is used within one data object, use more specific properties. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String characterSet = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#characterSet";

    /**
     * Label: rootElementOf 
     * Comment: DataObjects extracted from a single data source are organized into a containment tree. This property links the root of that tree with the datasource it has been extracted from 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource 
     */
    public static final String rootElementOf = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#rootElementOf";

    /**
     * Label: informationElementDate 
     * Comment: A point or period of time associated with an event in the lifecycle of an Information Element. A common superproperty for all date-related properties of InformationElements in the NIE Framework. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String informationElementDate = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#informationElementDate";

    /**
     * Label: legal 
     * Comment: A common superproperty for all properties that point at legal information about an Information Element 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String legal = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#legal";

    /**
     * Label: isStoredAs 
     * Comment: Links the information element with the DataObject it is stored in. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String isStoredAs = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#isStoredAs";

    /**
     * Label: language 
     * Comment: Language the InformationElement is expressed in. This property applies to the data object in its entirety. If the data object is divisible into parts expressed in multiple languages - more specific properties should be used. Users are encouraged to use the two-letter code specified in the RFC 3066 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String language = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#language";

    /**
     * Label: copyright 
     * Comment: Content copyright 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String copyright = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#copyright";

    /**
     * Label: modified 
     * Comment: Date the DataObject was changed in any way.  Note that this date refers to the modification of the DataObject itself (i.e. the physical representation). Compare with nie:contentModified. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String modified = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#modified";

    /**
     * Label: created 
     * Comment: Date of creation of the DataObject. Note that this date refers to the creation of the DataObject itself (i.e. the physical representation). Compare with nie:contentCreated. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String created = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#created";

    /**
     * Label: lastModified 
     * Comment: Last modification date of the DataObject. Note that this date refers to the modification of the DataObject itself (i.e. the physical representation). Compare with nie:contentLastModified. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String lastModified = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#lastModified";

    /**
     * Label: mimeType 
     * Comment: The mime type of the resource, if available. Example: "text/plain". See http://www.iana.org/assignments/media-types/. This property applies to data objects that can be described with one mime type. In cases where the object as a whole has one mime type, while it's parts have other mime types, or there is no mime type that can be applied to the object as a whole, but some parts of the content have mime types - use more specific properties. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String mimeType = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#mimeType";

    /**
     * Label: version 
     * Comment: The current version of the given data object. Exact semantics is unspecified at this level. Use more specific subproperties if needed. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String version = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#version";

    /**
     * Label: interpretedAs 
     * Comment: Links the DataObject with the InformationElement it is interpreted as. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     */
    public static final String interpretedAs = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#interpretedAs";

    /**
     * Label: links 
     * Comment: A linking relation. A piece of content links/mentions a piece of data 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String links = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#links";

    /**
     * Label: generator 
     * Comment: Software used to "generate" the contents. E.g. a word processor name. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String generator = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#generator";

    /**
     * Label: isPartOf 
     * Comment: Generic property used to express containment relationships between DataObjects. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of DataObjects to use those specific subproperties. Note to the developers: Please be aware of the distinction between containment relation and provenance. The isPartOf relation models physical containment, a nie:DataObject (e.g. an nfo:Attachment) is a 'physical' part of an nie:InformationElement (a nmo:Message). Also, please note the difference between physical containment (isPartOf) and logical containment (isLogicalPartOf) the former has more strict meaning. They may occur independently of each other. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     */
    public static final String isPartOf = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#isPartOf";

    /**
     * Label: disclaimer 
     * Comment: A disclaimer 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String disclaimer = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#disclaimer";

    /**
     * Label: generatorOption 
     * Comment: A common superproperty for all settings used by the generating software. This may include compression settings, algorithms, autosave, interlaced/non-interlaced etc. Note that this property has no range specified and therefore should not be used directly. Always use more specific properties. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     */
    public static final String generatorOption = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#generatorOption";

    /**
     * Label: description 
     * Comment: A textual description of the resource. This property may be used for any metadata fields that provide some meta-information or comment about a resource in the form of a passage of text. This property is not to be confused with nie:plainTextContent. Use more specific subproperties wherever possible. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String description = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#description";

    /**
     * Label: contentCreated 
     * Comment: The date of the content creation. This may not necessarily be equal to the date when the DataObject (i.e. the physical representation) itself was created. Compare with nie:created property. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String contentCreated = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contentCreated";

    /**
     * Label: title 
     * Comment: Name given to an InformationElement 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String title = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#title";

    /**
     * Label: lastRefreshed 
     * Comment: Date when information about this data object was retrieved (for the first time) or last refreshed from the data source. This property is important for metadata extraction applications that don't receive any notifications of changes in the data source and have to poll it regularly. This may lead to information becoming out of date. In these cases this property may be used to determine the age of data, which is an important element of it's dependability.  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String lastRefreshed = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#lastRefreshed";

    /**
     * Label: dataSource 
     * Comment: Marks the provenance of a DataObject, what source does a data object come from. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource 
     */
    public static final String dataSource = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#dataSource";

    /**
     * Label: depends 
     * Comment: Dependency relation. A piece of content depends on another piece of data in order to be properly understood/used/interpreted. 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String depends = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#depends";

    /**
     * Label: modified 
     * Comment: The date of a modification of the original content (not its corresponding DataObject or local copy). Compare with nie:modified. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String contentModified = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contentModified";

    /**
     * Label: contentLastModified 
     * Comment: The date of the last modification of the original content (not its corresponding DataObject or local copy). Compare with nie:lastModified. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#dateTime 
     */
    public static final String contentLastModified = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contentLastModified";

    /**
     * Label: keyword 
     * Comment: Adapted DublinCore: The topic of the content of the resource, as keyword. No sentences here. Recommended best practice is to select a value from a controlled vocabulary or formal classification scheme.  
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String keyword = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#keyword";

    /**
     * Label: isLogicalPartOf 
     * Comment: Generic property used to express 'logical' containment relationships between DataObjects. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of InformationElement to use those specific subproperties. Note the difference between 'physical' containment (isPartOf) and logical containment (isLogicalPartOf) 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     */
    public static final String isLogicalPartOf = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#isLogicalPartOf";

    /**
     * Label: identifier 
     * Comment: An unambiguous reference to the InformationElement within a given context. Recommended best practice is to identify the resource by means of a string conforming to a formal identification system. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String identifier = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#identifier";

    /**
     * Label: plainTextContent 
     * Comment: Plain-text representation of the content of a InformationElement with all markup removed. The main purpose of this property is full-text indexing and search. Its exact content is considered application-specific. The user can make no assumptions about what is and what is not contained within. Applications should use more specific properties wherever possible. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String plainTextContent = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#plainTextContent";

    /**
     * Label: html content 
     * Comment: The HTML content of an information element. This property can be used to store text including formatting in a generic fashion. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String htmlContent = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#htmlContent";

    /**
     * Label: comment 
     * Comment: A user comment about an InformationElement. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String comment = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#comment";

    /**
     * Label: relatedTo 
     * Comment: A common superproperty for all relations between a piece of content and other pieces of data (which may be interpreted as other pieces of content). 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String relatedTo = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#relatedTo";

    /**
     * Label: contentSize 
     * Comment: The size of the content. This property can be used whenever the size of the content of an InformationElement differs from the size of the DataObject. (e.g. because of compression, encoding, encryption or any other representation issues). The contentSize in expressed in bytes. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#integer 
     */
    public static final String contentSize = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contentSize";

    /**
     * Label: license 
     * Comment: Terms and intellectual property rights licensing conditions. 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String license = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#license";

    /**
     * Label: subject 
     * Comment: An overall topic of the content of a InformationElement 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String subject = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#subject";

    /**
     * Label: coreGraph 
     * Comment: Connects the data object with the graph that contains information about it. Deprecated in favor of a more generic nao:isDataGraphFor. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#InstanceBase 
     */
    public static final String coreGraph = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#coreGraph";

    /**
     * Label: hasPart 
     * Comment: Generic property used to express 'physical' containment relationships between DataObjects. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of DataObjects to use those specific subproperties. Note to the developers: Please be aware of the distinction between containment relation and provenance. The hasPart relation models physical containment, an InformationElement (a nmo:Message) can have a 'physical' part (an nfo:Attachment).  Also, please note the difference between physical containment (hasPart) and logical containment (hasLogicalPart) the former has more strict meaning. They may occur independently of each other. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     */
    public static final String hasPart = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#hasPart";

    /**
     * Label: licenseType 
     * Comment: The type of the license. Possible values for this field may include "GPL", "BSD", "Creative Commons" etc. 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final String licenseType = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#licenseType";

    /**
     * Label: byteSize 
     * Comment: The overall size of the data object in bytes. That means the space taken by the DataObject in its container, and not the size of the content that is of interest to the user. For cases where the content size is different (e.g. in compressed files the content is larger, in messages the content excludes headings and is smaller) use more specific properties, not necessarily subproperties of this one. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.w3.org/2001/XMLSchema#integer 
     */
    public static final String byteSize = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#byteSize";

    /**
     * Label: hasLogicalPart 
     * Comment: Generic property used to express 'logical' containment relationships between InformationElements. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of InformationElement to use those specific subproperties. Note the difference between 'physical' containment (hasPart) and logical containment (hasLogicalPart) 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement 
     */
    public static final String hasLogicalPart = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#hasLogicalPart";

    /**
     * Label: url 
     * Comment: URL of a DataObject. It points to the location of the object. A typial usage is FileDataObject. In cases where creating a simple file:// or http:// URL for a file is difficult (e.g. for files inside compressed archives) the applications are encouraged to use conventions defined by Apache Commons VFS Project at http://jakarta.apache.org/  commons/ vfs/ filesystems.html. 
     * Comment: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final String url = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#url";

}
