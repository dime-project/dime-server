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
