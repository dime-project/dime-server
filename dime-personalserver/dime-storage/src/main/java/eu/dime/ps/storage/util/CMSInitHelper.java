package eu.dime.ps.storage.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.PersistenceException;

public class CMSInitHelper {

	private static Set<String> locales;

	private final static String DIME_FOLDER = ".dime";
	
	private final static String CMS_FOLDER = "cms";
	
	private final static String BLOB_FOLDER = "blob";

	
//	public static void initCMS(String name){
//		initLocale();
//		initDatabase(name);
//		setupContent();
//		setupRoles();
//		setupTenant(name);
//		setupAdministrator();
//	}
//	
//	public static void initCMS(){
//		initLocale();
//		initDatabase("dime-cms");
//		setupContent();
//		setupRoles();
//		setupAdministrator();
//	}
//	
//	public static void initCMSforTesting(String name){
//		initLocale();
//		initDatabase(name);
//		setupContent();
//		setupRoles();
//		setupAdministrator();
//	}
//	
//
//	private static void setupContent() {
//		if (Nodes.getRoot() == null) {
//			Folder folder = new Folder();
//			folder.setId("home");
//			folder.setTitle(I18N.get("dime-home"));
//			folder.setLastModified(new Date());
//			folder.setVisible(true);
//			Nodes.setRoot(folder);
//			Nodes.save(folder);
//		}
//	}
//
//	private static void setupRoles() {
//		if (Database.query(Role.class).isEmpty()) {
//			Role adminRole = new Role();
//			adminRole.setName(eu.dime.ps.storage.entities.Role.ADMIN.toString());
//			adminRole.setAdministrator(true);
//			String [] contents = Contents.getClassNamesForAvailableTypes();
//			if(contents == null){
//				contents = new String[1];
//				contents[0] = File.class.getName();
//			}
//			adminRole.setTypes(contents);
//			Database.save(adminRole);
//
//			Role editor = new Role();
//			editor.setName(eu.dime.ps.storage.entities.Role.GUEST.toString());
//			editor.setTypes(Functors.filter(
//					Contents.getClassNamesForAvailableTypes(),
//					new Predicate<String>() {
//						public boolean test(String type) {
//							return !(type.equals(Factory.class.getName())
//									|| type.equals(Script.class.getName()) || type
//									.equals(Transit.class.getName()));
//						}
//					}));
//			Database.save(editor);
//		}
//	}
//
//	private static void setupAdministrator() {
//		if (Users.queryAdministrators().isEmpty()) {
//			Role role = Database.queryUnique(Role.class, new Predicate<Role>() {
//				public boolean test(Role role) {
//					return role.getName().equals(eu.dime.ps.storage.entities.Role.OWNER.toString());
//				}
//			});
//			final User administrator = new User();
//			administrator.setRole(role);
//			administrator.setRoots(new Folder[] { (Folder) Nodes.getRoot() });
//			administrator.setName("admin");
//			administrator.setPassword("dimepass");
//			Database.save(administrator);
//
//		} 
//	}
//	
//	public static User setupTenant(String tenantId) {
//		User user = Users.queryByLogin(tenantId);
//		if (user ==null) {
//			Role role = Database.queryUnique(Role.class, new Predicate<Role>() {
//				public boolean test(Role role) {
//					return role.getName().equals(eu.dime.ps.storage.entities.Role.OWNER.toString());
//				}
//			});
//			final User tenant = new User();
//			if(role == null){
//				role = new Role();
//				role.setAdministrator(true);
//				role.setName(eu.dime.ps.storage.entities.Role.OWNER.toString());
//				String[] types = {File.class.getName()};
//				role.setTypes(types);
//				
//			}
//			tenant.setRole(role);
//			tenant.setRoots(new Folder[] { (Folder) Nodes.getRoot() });
//			tenant.setName(tenantId);
//			tenant.setLogin(tenantId);
//			tenant.setPassword("dimepass"); //TODO: when we have HSQLDB + encryption this needs to be changed
//			Database.save(tenant);
//			return tenant;
//		} 
//		return user;
//	}
//
//
//	protected static void initLocale() {
//		String localeCode = "";
//		locales = new HashSet<String>();
//		for (Locale locale : Locale.getAvailableLocales()) {
//			locales.add(locale.getLanguage());
//		}
//	}
//
//	protected static void initDatabase(String name) {
//		String databaseEngine = "eu.dime.ps.storage.jfix.db4o.engine.PersistenceEngineDb4o";
//		if (databaseEngine != null) {
//			Database.setPersistenceEngine(databaseEngine);
//		}
//
//		String databaseName = name;
//		if (databaseName != null) {
//			Database.open(databaseName);
//		} 
//	}
//	
////	/**
////	 * Once this method is called, a temporary testdatabase will be used
////	 * Call initDatabase() to switch back
////	 */
////	protected static void initTestDatabase(){
////		String databaseEngine = "eu.dime.ps.storage.jfix.db4o.engine.PersistenceEngineDb4o";
////		if (databaseEngine != null) {
////			Database.setPersistenceEngine(databaseEngine);
////		}
////		
////		String tmpdir = System.getProperty("java.io.tmpdir");
////		String databaseName = tmpdir + "dime-test-cms"; 
////		if (databaseName != null) {
////			Database.open(databaseName);
////		} 
////	}
//
//	protected static void initTimer() {
//		Timers.start();
//	}
//
//	public static void destroy() {
//		stopTimer();
//		closeDatabase();
//	}
//	
//	public static boolean deleteDatabase(String name){
//		String dbDir = getCMSFolder() + java.io.File.separator
//				+ name + java.io.File.separator;
//		return deleteDirectory(new java.io.File(dbDir));
//	}
//
//	protected static void stopTimer() {
//		Timers.stop();
//	}
//
//	protected static void closeDatabase() {
//		Database.close();
//	}



	private String buildURI(String uri, String query) {
		if (query != null) {
			return String.format("%s%s%s", uri, uri.contains("?") ? "&" : "?",
					query);
		} else {
			return uri;
		}
	}

	private static boolean deleteDirectory(java.io.File path) {
	    if( path.exists() ) {
	      java.io.File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }

	public static String getCMSFolder(){
		String dimeDir = System.getProperty("dime.appdata.basedir");
		if (dimeDir == null){
			String home = System.getProperty("user.home");
			if (home != null){
			dimeDir = home 
					+ java.io.File.separator 
					+ DIME_FOLDER;
			} else {
				throw new PersistenceException("Could not delete cms, " +
						"because <dime.appdata.basedir> and <user.home> are not defined.");
			}
		}
		return dimeDir + java.io.File.separator + CMS_FOLDER;
	}

	public static String getBlobFolder() {
		return BLOB_FOLDER;
	}
	
}
