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

package eu.dime;

import java.io.File;

import org.ontoware.rdf2go.util.VocabularyWriter;

public class VocabWriter {

	VocabularyWriter writer = new VocabularyWriter();
	
	public void generate(String inputFile, String outputDir, String uri, String packageName, String className) throws Exception {
		File destDir = new File(outputDir);
		destDir.mkdirs();

		String[] params = new String[12];
		params[0] = "-i";
		params[1] = inputFile;
		params[2] = "-o";
		params[3] = destDir.getAbsolutePath();
		params[4] = "-a";
		params[5] = uri;
		params[6] = "-n";
		params[7] = className;
		params[8] = "--package";
		params[9] = packageName;
		params[10] = "-namespacestrict";
		params[11] = "true";
		
		writer.go(params);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String[][] vocabs = new String[][] {
				new String[] { "http://xmlns.com/foaf/0.1/", "vocabularies/foaf.rdf", "FOAF" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2011/10/05/dao#", "vocabularies/dao.trig", "DAO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2011/10/05/dcon#", "vocabularies/dcon.trig", "DCON" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2011/10/05/ddo#", "vocabularies/ddo.trig", "DDO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2011/10/05/dlpo#", "vocabularies/dlpo.trig", "DLPO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2011/10/05/dpo#", "vocabularies/dpo.trig", "DPO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2011/10/05/drmo#", "vocabularies/drmo.trig", "DRMO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2011/10/05/duho#", "vocabularies/duho.trig", "DUHO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/08/15/nao#", "vocabularies/nao.trig", "NAO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#", "vocabularies/ncal.trig", "NCAL" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#", "vocabularies/nco.trig", "NCO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2010/04/30/ndo#", "vocabularies/ndo.trig", "NDO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#", "vocabularies/nexif.trig", "NEXIF" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#", "vocabularies/nfo.trig", "NFO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#", "vocabularies/nid3.trig", "NID3" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#", "vocabularies/nie.trig", "NIE" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2009/02/19/nmm#", "vocabularies/nmm.trig", "NMM" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#", "vocabularies/nmo.trig", "NMO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#", "vocabularies/nrl.trig", "NRL" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2009/11/08/nso#", "vocabularies/nso.trig", "NSO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2010/01/25/nuao#", "vocabularies/nuao.trig", "NUAO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#", "vocabularies/pimo.trig", "PIMO" },
				new String[] { "http://vocab.deri.ie/ppo#", "vocabularies/ppo.rdf", "PPO" },
				new String[] { "http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#", "vocabularies/tmo.trig", "TMO" },
				new String[] { "http://ogp.me/ns#", "vocabularies/ogp.me.n3", "OG" }
		};
		
		VocabWriter writer = new VocabWriter();
		
		for (String[] vocab : vocabs) {
			writer.generate(
					"/Users/ismael/Documents/workspace/dime-personalserver/dime-semantic/src/main/resources/" + vocab[1],
					"/Users/ismael/Documents/workspace/dime-personalserver/dime-semantic/gensrc/main/java/eu/dime/ps/semantic/vocabulary/",
					vocab[0],
					"eu.dime.ps.semantic.vocabulary",
					vocab[2]);
		}
		
	}
	
}
