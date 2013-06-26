package eu.dime.commons.dto;

import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;



@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class ContextData/*<T>*/ {

    @javax.xml.bind.annotation.XmlElement(name = "timeRef")
    public String timeRef;
    
    // TODO to be renamed to entries after PoC (this is always an array, so
    // 'entry' is misleading)
    @javax.xml.bind.annotation.XmlElement(name = "entry")
    public Collection<Context> entry;

    public ContextData() {
	super();
	this.entry = new LinkedList<Context>();
    }

    public ContextData(Context entry) {
	this();
	getEntries().add(entry);
    }

    public ContextData(Collection<Context> entries) {
	this();
	getEntries().addAll(entries);
    }

    public String getTimeRef() {
		return timeRef;
	}

	public void setTimeRef(String timeRef) {
		this.timeRef = timeRef;
	}

	public void addEntry(Context entry) {
	if (this.entry == null) {
	    this.entry = new LinkedList<Context>();
	}
	this.entry.add(entry);
    }

    public Collection<Context> getEntries() {
	if (entry == null) {
	    this.entry = new LinkedList<Context>();
	}
	return entry;
    }

    public void setEntry(Collection<Context> entries) {
	this.entry = entries;
    }

}