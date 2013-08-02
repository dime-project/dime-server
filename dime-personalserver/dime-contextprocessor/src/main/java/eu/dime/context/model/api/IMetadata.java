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

package eu.dime.context.model.api;

import java.util.Set;
import java.util.Collections;
import java.io.Serializable;

public interface IMetadata extends Serializable
{
    public static final IMetadata EMPTY_METADATA = new IMetadata() {
	public IValue getMetadatumValue(IScope scopeKey) { return null; }
	public IMetadatum getMetadatum(IScope scopeKey) { return null; }
	public Set keySet() { return Collections.EMPTY_SET; }
    };

    /**
     * @param scopeKey the {@link eu.dime.context.model.impl.Scope}
     * used as a key for storing the Metadatum in the metadata set. The key is
     * the Scope field of the Metadatum object.
     * @return the {@link IValue} of the Metadatum object identified by
     * scopeKey
     */
    public IValue getMetadatumValue(final IScope scopeKey);

    /**
     * @param scopeKey the {@link eu.dime.context.model.impl.Scope}
     * used as a key for storing the Metadatum in the metadata set. The key is
     * the Scope field of the Metadatum object.
     * @return the entire Metadatum object identified by the scopeKey
     */
    public IMetadatum getMetadatum(final IScope scopeKey);

    /**
     * Returns the Set of the stored keys. The returned set contains objects
     * of type {@link IScope}.
     *
     * @return an instance of {@link Set} containing {@link IScope}s
     * representing the keys of the stored metadata
     */
    public Set keySet();
}
