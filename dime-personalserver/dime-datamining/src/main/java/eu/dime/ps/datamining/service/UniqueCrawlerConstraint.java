package eu.dime.ps.datamining.service;

/**
 * This class is used as a utility for the key in the maps of crawlers 
 * currently managed by the registry. At the moment the new unique constraint
 * is that the service account & path(s) cannot be the same.
 * Note: Needed to make the paths variable in ServiceCrawler implementation's
 * final because of this. They cannot change after construction.
 * 
 * @author Will Fleury
 */
public class UniqueCrawlerConstraint {

	private final Long tenantId;
    private final String accountIdentifier;
    private final PathDescriptor path;

    public UniqueCrawlerConstraint(ServiceCrawler crawler) {
        this(crawler.getTenant(), crawler.getAccountIdentifier(), crawler.getPath());
    }

    public UniqueCrawlerConstraint(Long tenantId, String accountIdentifier, PathDescriptor path) {
    	this.tenantId = tenantId;
        this.accountIdentifier = accountIdentifier;
        this.path = path;
    }

    public Long getTenant() {
    	return tenantId;
    }
    
    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public PathDescriptor getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UniqueCrawlerConstraint other = (UniqueCrawlerConstraint) obj;
        if ((this.tenantId == null) ? (other.tenantId != null)
                : !this.tenantId.equals(other.tenantId)) {
            return false;
        }
        if ((this.accountIdentifier == null) ? (other.accountIdentifier != null)
                : !this.accountIdentifier.equals(other.accountIdentifier)) {
            return false;
        }
        if ((this.path == null) ? (other.path != null)
                : !this.path.equals(other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.accountIdentifier != null ? this.accountIdentifier.hashCode() : 0);
        hash = 17 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UniqueCrawlerKey{"+"accountIdentifier="+accountIdentifier+", "+"path="+path+"}";
    }

}
