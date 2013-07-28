package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IMetadatum;

abstract public class AbstractMetadata implements IMetadata
{
    public IValue getMetadatumValue(IScope scopeKey)
    {
	final IMetadatum metadatum = getMetadatum(scopeKey);

	return metadatum == null ? null : metadatum.getValue();
    }
}
