/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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