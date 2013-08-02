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

package eu.dime.ps.datamining.crawler;


import java.io.Serializable;

/**
 * This is an abstract class which represents various types of data which is 
 * passed to the data mining module for processing. 
 * 
 * The data could be entire documents, mp3 meta data, contacts etc.. 
 * 
 * @author Will Fleury
 */
public abstract class Data implements Serializable {
    
    /**
     * This is the hash of whatever data is represented.
     */
    protected String hash;
    
    public Data() { }
    
    public Data(String hash) {
        this.hash = hash;
    }
    

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
