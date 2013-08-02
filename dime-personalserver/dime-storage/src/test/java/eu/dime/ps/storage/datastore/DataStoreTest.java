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

package eu.dime.ps.storage.datastore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import eu.dime.ps.storage.datastore.engine.Db4oPersistenceEngine;
import eu.dime.ps.storage.datastore.impl.DataStoreProvider;
import eu.dime.ps.storage.datastore.types.DimeBinary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.db4o.constraints.UniqueFieldValueConstraintViolationException;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-cms-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class DataStoreTest {
	
	private static DataStore dataStore;
	
	@Rule
	public static TemporaryFolder tempFolder = new TemporaryFolder();
	
	//@Autowired
	private static DataStoreProvider dataStoreProvider;
	
	public void setDataStoreProvider(DataStoreProvider dataStoreProvider) {
		this.dataStoreProvider = dataStoreProvider;
	}
	
	@Before
	public void createDataStore(){
		dataStore = dataStoreProvider.getTenantStore(99999999, true);
		assertTrue(dataStore != null);
	}
	
	@BeforeClass
	public static void init(){
		Db4oPersistenceEngine engine = new Db4oPersistenceEngine(
				tempFolder.newFolder(System.getProperty("java.io.tmpdir") + 
						File.separator+"cms-test").getAbsolutePath());
		DataStoreTest.dataStoreProvider = new DataStoreProvider();
		DataStoreTest.dataStoreProvider.setDb4oPersistenceEngine(engine);
	}
		
	
	
	@After
	public void deleteContent(){
		dataStore.deleteAll();
	}
	
	@AfterClass
	public static void destroyTestDataStore(){
		boolean deleted = dataStoreProvider.deleteTenantStore(99999999);
		assertTrue(dataStoreProvider.getTenantStore(99999999, false) == null);		
	}
	
	
//	@AfterClass
//	public static void cleanUp() throws IOException{
//		// TODO:destroying of teststore should be optimized
//		String path = CMSInitHelper.getCMSFolder() + File.separator + "99999999";
//		org.apache.commons.io.FileUtils.deleteDirectory(new File(path));	
//	}
	
	@Test
	public void testGetFile() throws Exception {
		InputStream is = new FileInputStream(new java.io.File("src/main/resources/test-files/image.jpg"));
		dataStore.addFile("12345", "uri://testfile2", is);
		assertTrue(dataStore.fileExists("uri://testfile2"));
		
		InputStream is2 = dataStore.get("uri://testfile2");
		// assertTrue(IOUtils.contentEquals(is, is2)); is false, ??
	}

//	@Test
//	public void testGetAllFiles() throws Exception {
//		List<File> files = dataStore.getAllFiles();
//
//		assertTrue(dataStore.getAllFiles()!=null);
//		int size= files.size();
//		InputStream is = new FileInputStream(new java.io.File("src/main/resources/test-files/image.jpg"));
//		dataStore.addFile("123", "uri://test1", is, "test");
//		dataStore.addFile("456", "uri://test2", is, "test");
//		files = dataStore.getAllFiles();
//		assertTrue(files.size() > size);
//	}

	@Test
	public void testAddAndDeleteFile() throws Exception, UniqueFieldValueConstraintViolationException {
		InputStream is = new FileInputStream(new java.io.File("src/main/resources/test-files/image.jpg"));
		dataStore.addFile("12345", "uri://testfile2", is);
		assertTrue(dataStore.fileExists("uri://testfile2"));
		
		boolean deleted = dataStore.delete("uri://testfile2");
		assertTrue(deleted);
		assertFalse(dataStore.fileExists("uri://testfile2"));
	}

	
	@Test
	public void testAddAndUpdateFile() throws Exception {
		InputStream is = new FileInputStream(new java.io.File("src/main/resources/test-files/image2.jpg"));
		//DataStore datastore = DataStore.getTestInstance();
		dataStore.addFile("22222", "uri://testfile3", is);
		assertTrue(dataStore.fileExists("uri://testfile3"));

		dataStore.update("55555", "uri://testfile3", is);
		assertTrue(((DimeBinary) dataStore.getObject("uri://testfile3")).getHash().equals("55555"));	
	}
	
	@Test
	public void multiStoreTest(){
		InputStream is = null;
		try {
			is = new FileInputStream(new java.io.File("src/main/resources/test-files/image2.jpg"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//DataStoreProvider dp = new DataStoreProvider();
		DataStore ds1 = dataStoreProvider.getTenantStore(1, true);
		DataStore ds2 = dataStoreProvider.getTenantStore(2, true);
		DataStore ds3 = dataStoreProvider.getTenantStore(3, true);
		
		
		ds1.addFile("123", "uri:file1", is);
		
		assertTrue(ds1.fileExists("uri:file1"));
		assertFalse(ds2.fileExists("uri:file1"));
		try {
			is = new FileInputStream(new java.io.File("src/main/resources/test-files/image2.jpg"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ds3.addFile("123", "uri:file1", is);
		try {
			is = new FileInputStream(new java.io.File("src/main/resources/test-files/image2.jpg"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ds3.addFile("123", "uri:file2", is);

		try {
			ds1.delete("uri:file1");
			ds3.delete("uri:file1");
			ds3.delete("uri:file2");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dataStoreProvider.deleteTenantStore(1);
		dataStoreProvider.deleteTenantStore(2);
		dataStoreProvider.deleteTenantStore(3);

	}
	
//	@Test
//	public void testPerformance(){
//		DataStore ds1 = dataStoreProvider.getTenantStore(1);
//
//		InputStream is = null;
//		try {
//			is = new FileInputStream(new java.io.File("src/main/resources/test-files/image2.jpg"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		long t0 = System.currentTimeMillis();
//		//dataStore.addFile("123", "uri:file1", is, "2");
//
//		long t1 = System.currentTimeMillis();
//		
//		long t2 = System.currentTimeMillis();
//		ds1.addFile("123", "uri:file1", is);
//		
//		long t3 = System.currentTimeMillis();
//
//		dataStoreProvider.deleteTenantStore(1);
//		
//	}



}
