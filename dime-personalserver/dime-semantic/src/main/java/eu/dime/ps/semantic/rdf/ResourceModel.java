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

package eu.dime.ps.semantic.rdf;

import java.util.Calendar;
import java.util.Date;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

public interface ResourceModel {

    public Resource getResourceIdentifier();

    public void setResourceIdentifier(Resource resourceIdentifier);

    /**
     * Get the underlying RDF2Go model holding the RDF statements.
     */
    public Model getModel();

    public void put(URI property, String value);

    public void put(URI property, Date value);

    public void put(URI property, Calendar value);

    public void put(URI property, boolean value);

    public void put(URI property, int value);

    public void put(URI property, long value);

    public void put(URI property, Node value);

    public void add(URI property, String value);

    public void add(URI property, Date value);

    public void add(URI property, Calendar value);

    public void add(URI property, boolean value);

    public void add(URI property, int value);

    public void add(URI property, long value);

    public void add(URI property, Node value);

    public String getString(URI property);

    public Date getDate(URI property);

    public Calendar getCalendar(URI property);

    public Boolean getBoolean(URI property);

    public Integer getInteger(URI property);

    public Long getLong(URI property);

    public URI getURI(URI property);

    public Node getNode(URI property);

    public void remove(URI property);

    public String getNamespace(String prefix);
    
//    public ResourceModel copy(Resource newIdentifier);
    
}
