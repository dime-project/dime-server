/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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
public class Data<T> {

    @javax.xml.bind.annotation.XmlElement(name = "startIndex")
    public Integer startIndex;

    @javax.xml.bind.annotation.XmlElement(name = "itemsPerPage")
    public Integer itemsPerPage;

    @javax.xml.bind.annotation.XmlElement(name = "totalResults")
    public Integer totalResults;

    // TODO to be renamed to entries after PoC (this is always an array, so
    // 'entry' is misleading)
    @javax.xml.bind.annotation.XmlElement(name = "entry")
    public Collection<T> entry;

    public Data() {
	super();
	this.itemsPerPage = 0;
	this.startIndex = 0;
	this.totalResults = 0;
	this.entry = new LinkedList<T>();
    }

    public Data(Integer startIndex, Integer itemsPerPage, Integer totalResults) {
	this();
	this.startIndex = startIndex;
	this.itemsPerPage = itemsPerPage;
	this.totalResults = totalResults;
    }

    public Data(Integer startIndex, Integer totalResults, T entry) {
	this(startIndex, 1, totalResults);
	getEntries().add(entry);
    }

    public Data(Integer startIndex, Integer totalResults, Collection<T> entries) {
	this(startIndex, entries.size(), totalResults);
	getEntries().addAll(entries);
    }

    public void addEntry(T entry) {
	if (this.entry == null) {
	    this.entry = new LinkedList<T>();
	}
	this.entry.add(entry);
	this.itemsPerPage++;
	this.totalResults++;
    }

    public Integer getStartIndex() {
	return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
	this.startIndex = startIndex;
    }

    public Integer getItemsPerPage() {
	return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
	this.itemsPerPage = itemsPerPage;
    }

    public Integer getTotalResults() {
	return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
	this.totalResults = totalResults;
    }

    public Collection<T> getEntries() {
	if (entry == null) {
	    this.entry = new LinkedList<T>();
	}
	return entry;
    }

    public void setEntry(Collection<T> entries) {
	this.entry = entries;
    }

}