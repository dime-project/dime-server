package com.ontotext.kim.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.ontotext.kim.KIMConstants;
import com.ontotext.kim.client.KIMException;

public class EntityPriority implements KIMConstants {
    protected final static String PRIORITY_CONF_FILE = "./config/entity-priority.conf";
    protected final String PRIORITY_CLASS_PREFIX = "priority.class.";
    protected final String PRIORITY_INSTANCE_PREFIX = "priority.instance.";
    protected final String PRIORITY_RULE_PREFIX = "priority.rule";

    protected Properties m_prop;
    protected int m_nDefaultInstancePriority;
    protected int m_nDefaultClassPriority;
    protected int m_nThreshold;
    protected boolean m_bFilterLookups;

    protected HashMap m_hClassPrio;
    protected HashMap m_hInstPrio;
    protected HashMap m_hRules;

    /**
     * read the config file
     * 
     * @throws Exception
     */
    public EntityPriority() {
        m_hClassPrio = new HashMap(50);
        m_hInstPrio = new HashMap(50);
        m_hRules = new HashMap(100);

        m_prop = new Properties();
    }
    
    public void init() throws Exception {
        m_prop.load(new FileInputStream(new File(System.getProperty(
                "kim.home.dir", "."), PRIORITY_CONF_FILE.substring(1))));
        m_nDefaultInstancePriority = Integer.parseInt(m_prop
                .getProperty("priority.default.instance"));
        m_nDefaultClassPriority = Integer.parseInt(m_prop
                .getProperty("priority.default.class"));
        m_nThreshold = Integer.parseInt(m_prop
                .getProperty("priority.threshold"));
        m_bFilterLookups = (m_prop.getProperty("priority.filterLookups") == null) ? false
                : Boolean.parseBoolean(m_prop
                        .getProperty("priority.filterLookups"));

        // init class and instance priority
        for (Iterator iter = m_prop.keySet().iterator(); iter.hasNext();) {
            String item = (String) iter.next();
            if (item.startsWith(PRIORITY_CLASS_PREFIX))
                addClassPriority(item);
            else if (item.startsWith(PRIORITY_INSTANCE_PREFIX))
                addInstancePriority(item);
        }

        // init pair-class priority
        for (int i = 1; true; i++) { //
            String sRuleName = m_prop.getProperty(PRIORITY_RULE_PREFIX + i
                    + ".name");
            if (sRuleName == null)
                break;
            String sClass1 = m_prop.getProperty(PRIORITY_RULE_PREFIX + i
                    + ".class1");
            String sClass2 = m_prop.getProperty(PRIORITY_RULE_PREFIX + i
                    + ".class2");
            int nDelta = Integer.parseInt(m_prop
                    .getProperty(PRIORITY_RULE_PREFIX + i + ".delta"));
            if (sClass1 == null || sClass2 == null)
                throw new RuntimeException(
                        "Invalid priority rule definition, rule " + i);
            m_hRules.put(sClass1 + "_" + sClass2, new Integer(nDelta));
            m_hRules.put(sClass2 + "_" + sClass1, new Integer(-nDelta));
        }
    }

    protected void addClassPriority(String sItemToParse) {
        String sClass = sItemToParse.substring(PRIORITY_CLASS_PREFIX.length());
        Integer nPrio = new Integer(m_prop.getProperty(sItemToParse));
        m_hClassPrio.put(sClass, nPrio);
    }

    protected void addInstancePriority(String sItemToParse) {
        String sInst = sItemToParse
                .substring(PRIORITY_INSTANCE_PREFIX.length());
        Integer nPrio = new Integer(m_prop.getProperty(sItemToParse));
        m_hInstPrio.put(sInst, nPrio);
    }

    public int getInstancePriority(String sInstanceURI) {
        Integer prio = (Integer) m_hInstPrio.get(stripNameSpace(sInstanceURI));
        return prio == null ? m_nDefaultInstancePriority : prio.intValue();
    }

    public int getClassPriority(String sClassURI) {
        Integer prio = (Integer) m_hClassPrio.get(stripNameSpace(sClassURI));
        return prio == null ? m_nDefaultInstancePriority : prio.intValue();
    }

    public int getPairDelta(String sClass1, String sClass2) {
        Integer nDelta = (Integer) m_hRules.get(stripNameSpace(sClass1) + "_"
                + stripNameSpace(sClass2));
        if (nDelta == null)
            return 0;
        return nDelta.intValue();
    }

    public int getThreshold() {
        return m_nThreshold;
    }

    public boolean getFilterLookups() {
        return m_bFilterLookups;
    }

    /**
     * Strips the namespace from the URI and retains only the local name.
     * 
     * @param sLongURI
     *            an URI
     * @return the localname of the URI
     * @throws KIMException
     */
    public static String stripNameSpace(String sLongURI) {
        String result;
        int index = sLongURI.indexOf('#');
        if (index < 0) {
            return sLongURI;
        }
        result = sLongURI.substring(index + 1);
        return result;
    } // stripNameSpace
}
