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

package eu.dime.ps.contextprocessor.helper;

import java.util.Iterator;
import java.util.Set;

import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IScope;

public class ContextDataPrinter {
	
    public static void printContextElement(IContextElement ctxEl)
    {
	printContextElement(ctxEl, 0);
    }

    public static void printContextElement(IContextElement ctxEl, int indent)
    {

	String tab = "	  ";
	String tabToAdd = "";
	try
	{
	    for (int i = 0; i < indent; i++)
	    {
		tabToAdd += tab;
	    }

	    System.out.println(tabToAdd + "ContextElement:");
	    System.out.println(tabToAdd + "  entity = " + ctxEl.getEntity());
	    System.out.println(tabToAdd + "  scope = " + ctxEl.getScope());

	    Set keySet = ctxEl.getMetadata().keySet();
	    Iterator i = keySet.iterator();
	    System.out.println(tabToAdd + "  metadata{");
	    while (i.hasNext())
	    {
		IScope scope = (IScope) i.next();
		System.out.println(tabToAdd + "\t" + scope.toString() + " = " +
			ctxEl.getMetadata().getMetadatumValue(scope).getValue());
	    }
	    System.out.println(tabToAdd + "  }");

	    keySet = ctxEl.getContextData().keySet();
	    i = keySet.iterator();
	    System.out.println(tabToAdd + "  contextData{");
	    while (i.hasNext())
	    {
		IScope scope = (IScope) i.next();
		System.out.println(tabToAdd + "\t" + scope.toString() + " = " +
			ctxEl.getContextData().getContextValue(scope).getValue().getValue());
	    }
	    System.out.println(tabToAdd + "  }");

	}
	catch (Exception e)
	{
	    System.out.println(tabToAdd + "null");
	}
	System.out.println("");
    }

}
