package eu.dime.ps.controllers.search;

public class SearchResult {
	
	private Object element;
	
	public SearchResult(Object element) {
		this.element = element;
	}
	
	public Object getElement() {
		return element;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getElement(Class<T> returnType) {
		return (T) element;
	}
	
}
