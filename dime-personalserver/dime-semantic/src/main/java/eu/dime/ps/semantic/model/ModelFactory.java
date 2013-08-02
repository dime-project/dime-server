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

package eu.dime.ps.semantic.model;

/**
 * The ModelFactory brings together in one place the creation of 
 * the factories for the different vocabularies used in digital.me,
 * which have been transformed into Java classes/attributes.
 * 
 * Vocabularies supported are NAO, NIE, NCO, PIMO, DCON, PPO, etc. 
 * 
 * @author Ismael Rivera
 *
 */
public class ModelFactory {

	private DAOFactory daoFactory;
	private DCONFactory dconFactory;
	private DDOFactory ddoFactory;
	private DLPOFactory dlpoFactory;
	private DPOFactory dpoFactory;
	private DUHOFactory duhoFactory;
	private GEOFactory geoFactory;
	private NAOFactory naoFactory;
	private NCALFactory ncalFactory;
	private NCOFactory ncoFactory;
	private NDOFactory ndoFactory;
	private NEXIFFactory nexifFactory;
	private NFOFactory nfoFactory;
	private NID3Factory nid3Factory;
	private NIEFactory nieFactory;
	private NMMFactory nmmFactory;
	private NMOFactory nmoFactory;
	private NRLFactory nrlFactory;
	private NSOFactory nsoFactory;
	private NUAOFactory nuaoFactory;
	private PIMOFactory pimoFactory;
	private PPOFactory ppoFactory;
	private TMOFactory tmoFactory;
	private RDFG1Factory rdfg1Factory;

	/**
	 * @return a factory for the DAO vocabulary
	 */
	public DAOFactory getDAOFactory() {
		if (daoFactory == null) {
			daoFactory = new DAOFactory();
		}
		return daoFactory;
	}
	
	/**
	 * @return a factory for the DCON vocabulary
	 */
	public DCONFactory getDCONFactory() {
		if (dconFactory == null) {
			dconFactory = new DCONFactory();
		}
		return dconFactory;
	}
	
	/**
	 * @return a factory for the DDO vocabulary
	 */
	public DDOFactory getDDOFactory() {
		if (ddoFactory == null) {
			ddoFactory = new DDOFactory();
		}
		return ddoFactory;
	}
	
	/**
	 * @return a factory for the DLPO vocabulary
	 */
	public DLPOFactory getDLPOFactory() {
		if (dlpoFactory == null) {
			dlpoFactory = new DLPOFactory();
		}
		return dlpoFactory;
	}
	
	/**
	 * @return a factory for the DPO vocabulary
	 */
	public DPOFactory getDPOFactory() {
		if (dpoFactory == null) {
			dpoFactory = new DPOFactory();
		}
		return dpoFactory;
	}
	/**
	 * @return a factory for the DUHO vocabulary
	 */
	public DUHOFactory getDUHOFactory() {
		if (duhoFactory == null) {
			duhoFactory = new DUHOFactory();
		}
		return duhoFactory;
	}
	
	/**
	 * @return a factory for the NAO vocabulary
	 */
	public NAOFactory getNAOFactory() {
		if (naoFactory == null) {
			naoFactory = new NAOFactory();
		}
		return naoFactory;
	}
	
	/**
	 * @return a factory for the NCAL vocabulary
	 */
	public NCALFactory getNCALFactory() {
		if (ncalFactory == null) {
			ncalFactory = new NCALFactory();
		}
		return ncalFactory;
	}
	
	/**
	 * @return a factory for the NCO vocabulary
	 */
	public NCOFactory getNCOFactory() {
		if (ncoFactory == null) {
			ncoFactory = new NCOFactory();
		}
		return ncoFactory;
	}
	
	/**
	 * @return a factory for the NDO vocabulary
	 */
	public NDOFactory getNDOFactory() {
		if (ndoFactory == null) {
			ndoFactory = new NDOFactory();
		}
		return ndoFactory;
	}
	
	/**
	 * @return a factory for the NEXIF vocabulary
	 */
	public NEXIFFactory getNEXIFFactory() {
		if (nexifFactory == null) {
			nexifFactory = new NEXIFFactory();
		}
		return nexifFactory;
	}
	
	/**
	 * @return a factory for the NFO vocabulary
	 */
	public NFOFactory getNFOFactory() {
		if (nfoFactory == null) {
			nfoFactory = new NFOFactory();
		}
		return nfoFactory;
	}
	
	/**
	 * @return a factory for the NID3 vocabulary
	 */
	public NID3Factory getNID3Factory() {
		if (nid3Factory == null) {
			nid3Factory = new NID3Factory();
		}
		return nid3Factory;
	}
	
	/**
	 * @return a factory for the NIE vocabulary
	 */
	public NIEFactory getNIEFactory() {
		if (nieFactory == null) {
			nieFactory = new NIEFactory();
		}
		return nieFactory;
	}
	
	/**
	 * @return a factory for the NMM vocabulary
	 */
	public NMMFactory getNMMFactory() {
		if (nmmFactory == null) {
			nmmFactory = new NMMFactory();
		}
		return nmmFactory;
	}
	
	/**
	 * @return a factory for the NMO vocabulary
	 */
	public NMOFactory getNMOFactory() {
		if (nmoFactory == null) {
			nmoFactory = new NMOFactory();
		}
		return nmoFactory;
	}
	
	/**
	 * @return a factory for the NRL vocabulary
	 */
	public NRLFactory getNRLFactory() {
		if (nrlFactory == null) {
			nrlFactory = new NRLFactory();
		}
		return nrlFactory;
	}
	
	/**
	 * @return a factory for the NUAO vocabulary
	 */
	public NUAOFactory getNUAOFactory() {
		if (nuaoFactory == null) {
			nuaoFactory = new NUAOFactory();
		}
		return nuaoFactory;
	}
	
	/**
	 * @return a factory for the PIMO vocabulary
	 */
	public PIMOFactory getPIMOFactory() {
		if (pimoFactory == null) {
			pimoFactory = new PIMOFactory();
		}
		return pimoFactory;
	}
	
	/**
	 * @return a factory for the PPO vocabulary
	 */
	public PPOFactory getPPOFactory() {
		if (ppoFactory == null) {
			ppoFactory = new PPOFactory();
		}
		return ppoFactory;
	}

	/**
	 * @return a factory for the RDFG-1 vocabulary
	 */
	public RDFG1Factory getRDFG1Factory() {
		if (rdfg1Factory == null) {
			rdfg1Factory = new RDFG1Factory();
		}
		return rdfg1Factory;
	}
	
	/**
	 * @return a factory for the TMO vocabulary
	 */
	public TMOFactory getTMOFactory() {
		if (tmoFactory == null) {
			tmoFactory = new TMOFactory();
		}
		return tmoFactory;
	}
	
	/**
	 * @return a factory for the GEO vocabulary
	 */
	public GEOFactory getGEOFactory() {
		if (geoFactory == null) {
			geoFactory = new GEOFactory();
		}
		return geoFactory;
	}
	
	/**
	 * @return a factory for the NSO vocabulary
	 */
	public NSOFactory getNSOFactory() {
		if (nsoFactory == null) {
			nsoFactory = new NSOFactory();
		}
		return nsoFactory;
	}


}
