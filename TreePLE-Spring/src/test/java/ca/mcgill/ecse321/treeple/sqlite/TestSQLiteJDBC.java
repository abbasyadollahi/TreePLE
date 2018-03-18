package ca.mcgill.ecse321.treeple.sqlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse321.treeple.model.Location;
import ca.mcgill.ecse321.treeple.model.Municipality;
import ca.mcgill.ecse321.treeple.model.Species;
import ca.mcgill.ecse321.treeple.model.Tree;
import ca.mcgill.ecse321.treeple.model.User;
import ca.mcgill.ecse321.treeple.service.TreePLEService;

public class TestSQLiteJDBC {

    private static SQLiteJDBC sql;
    private static File dbFile;
    private static final String dbPath = "/output/treeple_test.db";
    
    private static TreePLEService service;
    
    private static JSONObject defaultUser;
    private static JSONObject defaultSpecies;
    private static JSONObject defaultLocation;
    private static JSONObject defaultMun;
    
    private static final int numUsers = 50;
    private static final int numSpecies = 50;
    private static final int numLocations = 50;
    private static final int numMunicipalities = 50;
    private static final int numTrees = 50;

    @BeforeClass
    public static void setUpBeforeClass() {
        sql = new SQLiteJDBC(dbPath);
        dbFile =  (new File(System.getProperty("user.dir") + dbPath)).getAbsoluteFile();
        sql.connect();
        
        service = new TreePLEService(sql);
    }

    @AfterClass
    public static void tearDownAfterClass() {
        if (sql != null) {
            sql.deleteDB();
            sql.closeConnection();
        }
    }
    
    @Before
    public void setUp() {
    	
    	// Default User Setup
    	defaultUser = new JSONObject();
    	defaultUser.put("username", "testUser");
    	defaultUser.put("password", "testPassword");
    	defaultUser.put("role", "Resident");
    	defaultUser.put("addresses", "2030 Mulberry Street, Montreal, QC, Canada J01 10J");
    	defaultUser.put("trees", "");
    	
    	// Default Species Setup
    	defaultSpecies = new JSONObject();
    	defaultSpecies.put("name", "Maple");
    	defaultSpecies.put("species", "pseudoplatanus");
    	defaultSpecies.put("genus", "Acer");
    	
    	// Default Location Setup
    	defaultLocation = new JSONObject();
    	defaultLocation.put("locationId", 1);
    	defaultLocation.put("latitude", 45.0);
    	defaultLocation.put("longitude", 45.0);
    	
    	// Default Municipality Setup
    	defaultMun = new JSONObject();
    	defaultMun.put("name", "Pointe-Claire");
    	defaultMun.put("totalTrees", 100);
    	defaultMun.put("borders", "1, 2");
    }

    @After
    public void tearDown() throws Exception {
    	service.resetDatabase();
    }
    
    @Test
    public void testResetDB() {
        assertEquals(true, sql.resetDB());
    }

    @Test
    public void testDeleteDB() {
        assertEquals(true, dbFile.exists());
        assertEquals(true, sql.deleteDB());
        assertEquals(false, dbFile.exists());
        assertEquals(true, sql.connect());
    }
    
    // ======================
    // USER TESTS
    // ======================
    
    @Test
    public void testInsertUser() {
    	
    	ArrayList<String> addrList = new ArrayList<String>();
    	
    	for (String addressId : defaultUser.getString("addresses").split(",")) {
            if (addressId != null && !addressId.replaceAll("\\s", "").isEmpty()) {
                addrList.add(addressId.replaceAll("\\s", ""));
            }
        }
    	
    	if (sql.getUser(defaultUser.getString("username")) != null) {
    		sql.deleteUser(defaultUser.getString("username"));
    	}
    	
    	boolean success = sql.insertUser(
    			defaultUser.getString("username"), 
    			defaultUser.getString("password"),
    			defaultUser.getString("role"), 
    			defaultUser.getString("addresses"), 
    			defaultUser.getString("trees"));
    	
    	assertEquals(true, success);
    	
    	User user = sql.getUser(defaultUser.getString("username"));
    	assertEquals(user.getUsername(), defaultUser.getString("username"));
    	assertEquals(user.getPassword(), defaultUser.getString("password"));
    	assertEquals(user.getRole().name(), defaultUser.getString("role"));
    	
    	int i = 0;
    	for (String s : user.getMyAddresses()) {
    		assertEquals(s, addrList.get(i));
    		i++;
    	}
    	
    	// New user should have no trees
    	assertEquals(user.getMyTrees().length, 0);
    }
    
    @Test
    public void testGetUser() {
    	
    	for (int i = 0; i < numUsers; i++) {
    		JSONObject newUser = new JSONObject();
    		newUser.put("username", defaultUser.getString("username") + i);
    		newUser.put("password", defaultUser.getString("password") + i);
    		newUser.put("role", defaultUser.getString("role"));
    		newUser.put("addresses", defaultUser.getString("addresses"));
    		newUser.put("trees", defaultUser.getString("trees"));
    		
    		boolean success = sql.insertUser(
    				newUser.getString("username"), 
        			newUser.getString("password"),
        			newUser.getString("role"), 
        			newUser.getString("addresses"), 
        			newUser.getString("trees"));
        	assertEquals(true, success);
    	}
    	
    	for (int i = 0; i < numUsers; i++) {
    		User user = sql.getUser(defaultUser.getString("username") + i);
    		assertNotEquals(null, user);
    		assertEquals(defaultUser.getString("username") + i, user.getUsername());
    		assertEquals(defaultUser.getString("password") + i, user.getPassword());
    		assertEquals(user.getRole().name(), defaultUser.getString("role"));
    	}
    }
    
    @Test
    public void testGetAllUsers() {
    	
    	for (int i = 0; i < numUsers; i++) {
    		JSONObject newUser = new JSONObject();
    		newUser.put("username", defaultUser.getString("username") + i);
    		newUser.put("password", defaultUser.getString("password") + i);
    		newUser.put("role", defaultUser.getString("role"));
    		newUser.put("addresses", defaultUser.getString("addresses"));
    		newUser.put("trees", defaultUser.getString("trees"));
    		
    		boolean success = sql.insertUser(
    				newUser.getString("username"), 
        			newUser.getString("password"),
        			newUser.getString("role"), 
        			newUser.getString("addresses"), 
        			newUser.getString("trees"));
        	assertEquals(true, success);
    	}
    	
    	ArrayList<User> users = sql.getAllUsers();
    	assertEquals(numUsers, users.size());
    	
    	int i = 0;
    	for (User user : users) {
    		assertEquals(user.getUsername(), defaultUser.getString("username") + i);
    		assertEquals(user.getPassword(), defaultUser.getString("password") + i);
    		assertEquals(user.getRole().name(), defaultUser.getString("role"));
    		i++;
    	}
    }
    
    @Test
    public void testDeleteUser() {

    	for (int i = 0; i < numUsers; i++) {
    		JSONObject newUser = new JSONObject();
    		newUser.put("username", defaultUser.getString("username") + i);
    		newUser.put("password", defaultUser.getString("password") + i);
    		newUser.put("role", defaultUser.getString("role"));
    		newUser.put("addresses", defaultUser.getString("addresses"));
    		newUser.put("trees", defaultUser.getString("trees"));
    		
    		boolean success = sql.insertUser(
    				newUser.getString("username"), 
        			newUser.getString("password"),
        			newUser.getString("role"), 
        			newUser.getString("addresses"), 
        			newUser.getString("trees"));
        	assertEquals(true, success);
    	}
    	
    	for (int i = 0; i < numUsers; i++) {
    		boolean success = sql.deleteUser(defaultUser.getString("username") + i);
    		assertEquals(true, success);
    	}
    	
    	// Check if all users removed
    	assertEquals(0, sql.getAllUsers().size());
    }
    
    @Test
    public void testUpdateUserPassword() {
    	
    	for (int i = 0; i < numUsers; i++) {
    		JSONObject newUser = new JSONObject();
    		newUser.put("username", defaultUser.getString("username") + i);
    		newUser.put("password", defaultUser.getString("password") + i);
    		newUser.put("role", defaultUser.getString("role"));
    		newUser.put("addresses", defaultUser.getString("addresses"));
    		newUser.put("trees", defaultUser.getString("trees"));
    		
    		boolean success = sql.insertUser(
    				newUser.getString("username"), 
        			newUser.getString("password"),
        			newUser.getString("role"), 
        			newUser.getString("addresses"), 
        			newUser.getString("trees"));
        	assertEquals(true, success);
    	}
    	
    	String newPassword = "newPassword";
    	for (int i = 0; i < numUsers; i++) {
    		boolean success = sql.updateUserPassword(defaultUser.getString("username") + i, newPassword + i);
    		assertEquals(true, success);
    	}
    	
    	int i = 0;
    	for (User user : sql.getAllUsers()) {
    		assertEquals(newPassword + i, user.getPassword());
    		i++;
    	}
    }
    
    @Test
    public void testUpdateUserRole() {
    	
    	for (int i = 0; i < numUsers; i++) {
    		JSONObject newUser = new JSONObject();
    		newUser.put("username", defaultUser.getString("username") + i);
    		newUser.put("password", defaultUser.getString("password") + i);
    		newUser.put("role", defaultUser.getString("role"));
    		newUser.put("addresses", defaultUser.getString("addresses"));
    		newUser.put("trees", defaultUser.getString("trees"));
    		
    		boolean success = sql.insertUser(
    				newUser.getString("username"), 
        			newUser.getString("password"),
        			newUser.getString("role"), 
        			newUser.getString("addresses"), 
        			newUser.getString("trees"));
        	assertEquals(true, success);
    	}
    	
    	String newRole = "Scientist";
    	for (int i = 0; i < numUsers; i++) {
    		boolean success = sql.updateUserRole(defaultUser.getString("username") + i, newRole);
    		assertEquals(true, success);
    	}
    	
    	for (User user : sql.getAllUsers()) {
    		assertEquals(newRole, user.getRole().name());
    	}
    }
    
    @Test
    public void testUpdateUserAddresses() {
    	
    	for (int i = 0; i < numUsers; i++) {
    		JSONObject newUser = new JSONObject();
    		newUser.put("username", defaultUser.getString("username") + i);
    		newUser.put("password", defaultUser.getString("password") + i);
    		newUser.put("role", defaultUser.getString("role"));
    		newUser.put("addresses", defaultUser.getString("addresses"));
    		newUser.put("trees", defaultUser.getString("trees"));
    		
    		boolean success = sql.insertUser(
    				newUser.getString("username"), 
        			newUser.getString("password"),
        			newUser.getString("role"), 
        			newUser.getString("addresses"), 
        			newUser.getString("trees"));
        	assertEquals(true, success);
    	}
    	
    	String newAddress = "1234 Big Street, Toronto, ON, Canada H90 102";
    	for (int i = 0; i < numUsers; i++) {
    		boolean success = sql.updateUserAddresses(defaultUser.getString("username") + i, newAddress);
    		assertEquals(true, success);
    	}
    	
    	String[] newAddresses = newAddress.replaceAll(" ", "").split(",");
    	for (User user : sql.getAllUsers()) {
    		String[] addresses = user.getMyAddresses();
    		int i = 0;
    		for (String s : addresses) {
    			assertEquals(newAddresses[i], s);
    			i++;
    		}
    	}
    }
    
    @Test
    public void testUpdateUserTrees() {

    	JSONObject newUser = new JSONObject();
    	newUser.put("username", defaultUser.getString("username"));
    	newUser.put("password", defaultUser.getString("password"));
    	newUser.put("role", defaultUser.getString("role"));
    	newUser.put("addresses", defaultUser.getString("addresses"));
    	newUser.put("trees", defaultUser.getString("trees"));
    	boolean success = sql.insertUser(
    			newUser.getString("username"), 
    			newUser.getString("password"),
    			newUser.getString("role"), 
    			newUser.getString("addresses"), 
    			newUser.getString("trees"));
    	assertEquals(true, success);
    	
    	sql.insertLocation(1, 45.0, 45.0);
    	sql.insertLocation(2, 45.5, 45.5);
    	sql.insertSpecies("Maple", "pinus", "Arbus");
    	sql.insertMunicipality("Vaudreuil", 0, "46.0, 46.0");
    	sql.insertTree(
    			1,
    			10, 
    			20, 
    			defaultUser.getString("addresses"), 
    			"2018-12-22", 
    			"Residential", 
    			"Planted", 
    			"Private", 
    			"Maple", 
    			1, 
    			"Vaudreuil", 
    			"");
    	sql.insertTree(
    			2,
    			10, 
    			20, 
    			defaultUser.getString("addresses"), 
    			"2018-12-22", 
    			"Residential", 
    			"Planted", 
    			"Private", 
    			"Maple", 
    			2, 
    			"Vaudreuil", 
    			"");
    	
    	String trees = "1, 2";
    	success = sql.updateUserTrees(defaultUser.getString("username"), trees);
    	assertEquals(true, success);
    	
    	int[] testTrees = {1, 2};
    	for (User user : sql.getAllUsers()) {
    		Integer[] userTrees = user.getMyTrees();
    		int i = 0;
    		for (int tree : userTrees) {
    			assertEquals(testTrees[i], tree);
    			i++;
    		}
    	}
    }
    
    // ======================
    // SPECIES TESTS
    // ======================
    
    @Test
    public void testInsertSpecies() {
    	
    	boolean success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	
    	Species species = sql.getSpecies(defaultSpecies.getString("name"));
    	assertNotEquals(null, species);
    	assertEquals(defaultSpecies.getString("name"), species.getName());
    	assertEquals(defaultSpecies.getString("species"), species.getSpecies());
    	assertEquals(defaultSpecies.getString("genus"), species.getGenus());
    }
    
    @Test
    public void testUpdateSpecies() {
    	
    	boolean success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	
    	String newSpecies = "laurinum";
    	String newGenus = "Salix";
    	success = sql.updateSpecies(defaultSpecies.getString("name"), newSpecies, newGenus);
    	assertEquals(true, success);
    	
    	Species species = sql.getSpecies(defaultSpecies.getString("name"));
    	assertEquals(defaultSpecies.getString("name"), species.getName());
    	assertEquals(newSpecies, species.getSpecies());
    	assertEquals(newGenus, species.getGenus());
    }
    
    @Test
    public void testGetAllSpecies() {
    	
    	for (int i = 0; i < numSpecies; i++) {
    		boolean success = sql.insertSpecies(defaultSpecies.getString("name") + i, defaultSpecies.getString("species") + i, defaultSpecies.getString("genus") + i);
        	assertEquals(true, success);
    	}
    	
    	int i = 0;
    	for (Species species : sql.getAllSpecies()) {
    		assertEquals(defaultSpecies.getString("name") + i, species.getName());
    		assertEquals(defaultSpecies.getString("species") + i, species.getSpecies());
    		assertEquals(defaultSpecies.getString("genus") + i, species.getGenus());
    		i++;
    	}
    }
    
    @Test
    public void testGetSpecies() {
    	
    	for (int i = 0; i < numSpecies; i++) {
    		boolean success = sql.insertSpecies(defaultSpecies.getString("name") + i, defaultSpecies.getString("species") + i, defaultSpecies.getString("genus") + i);
        	assertEquals(true, success);
    	}
    	
    	for (int i = 0; i < numSpecies; i++) {
    		Species species = sql.getSpecies(defaultSpecies.getString("name") + i);
    		assertNotEquals(null, species);
    		assertEquals(defaultSpecies.getString("name") + i, species.getName());
    		assertEquals(defaultSpecies.getString("species") + i, species.getSpecies());
    		assertEquals(defaultSpecies.getString("genus") + i, species.getGenus());
    	}
    }
    
    @Test
    public void testDeleteSpecies() {
    	
    	for (int i = 0; i < numSpecies; i++) {
    		boolean success = sql.insertSpecies(defaultSpecies.getString("name") + i, defaultSpecies.getString("species") + i, defaultSpecies.getString("genus") + i);
        	assertEquals(true, success);
    	}
    	
    	for (int i = 0; i < numSpecies; i++) {
    		boolean success = sql.deleteSpecies(defaultSpecies.getString("name") + i);
        	assertEquals(true, success);
    	}
    	assertEquals(0, sql.getAllSpecies().size());
    }
    
    // ======================
    // LOCATIONS TESTS
    // ======================
    
	@Test
    public void testInsertLocation() {
    	
    	boolean success = sql.insertLocation(defaultLocation.getInt("locationId"), defaultLocation.getDouble("latitude"), defaultLocation.getDouble("longitude"));
    	assertEquals(true, success);
    	
    	Location location = sql.getLocation(defaultLocation.getInt("locationId"));
    	assertEquals(defaultLocation.getInt("locationId"), location.getLocationId());
    	assertEquals(defaultLocation.getDouble("latitude"), location.getLatitude(), 0);
    	assertEquals(defaultLocation.getDouble("longitude"), location.getLongitude(), 0);
    }
	
	@Test
	public void testUpdateLocation() {
		
		boolean success = sql.insertLocation(defaultLocation.getInt("locationId"), defaultLocation.getDouble("latitude"), defaultLocation.getDouble("longitude"));
    	assertEquals(true, success);
    	
    	double newLat = 20.09;
    	double newLong = 55.43;
    	success = sql.updateLocation(defaultLocation.getInt("locationId"), newLat, newLong);
    	assertEquals(true, success);
    	
    	Location location = sql.getLocation(defaultLocation.getInt("locationId"));
    	assertEquals(defaultLocation.getInt("locationId"), location.getLocationId());
    	assertEquals(newLat, location.getLatitude(), 0);
    	assertEquals(newLong, location.getLongitude(), 0);
	}
	
	@Test
	public void testGetAllLocations() {
		
		for (int i = 0; i < numLocations; i++) {
			boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
	    	assertEquals(true, success);
		}
		
		int i = 0;
		for (Location location : sql.getAllLocations()) {
			assertEquals(defaultLocation.getInt("locationId") + i, location.getLocationId());
	    	assertEquals(defaultLocation.getDouble("latitude") + i, location.getLatitude(), 0);
	    	assertEquals(defaultLocation.getDouble("longitude") + i, location.getLongitude(), 0);
	    	i++;
		}
	}
	
	@Test
	public void testGetLocation() {
		
		for (int i = 0; i < numLocations; i++) {
			boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
	    	assertEquals(true, success);
		}
		
		for (int i = 0; i < numLocations; i++) {
			Location location = sql.getLocation(defaultLocation.getInt("locationId") + i);
			assertEquals(defaultLocation.getInt("locationId") + i, location.getLocationId());
	    	assertEquals(defaultLocation.getDouble("latitude") + i, location.getLatitude(), 0);
	    	assertEquals(defaultLocation.getDouble("longitude") + i, location.getLongitude(), 0);
		}
	}
	
	@Test
	public void testGetMaxLocationId() {
		
		for (int i = 0; i < numLocations; i++) {
			boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
	    	assertEquals(true, success);
		}
		
		int maxId = sql.getMaxLocationId();
		assertNotEquals(null, maxId);
		assertNotEquals(-1, maxId);
		assertEquals(defaultLocation.getInt("locationId") + numLocations - 1, maxId);
	}
	
	@Test
	public void testDeleteLocation() {
		
		for (int i = 0; i < numLocations; i++) {
			boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
	    	assertEquals(true, success);
		}
		
		for (int i = 0; i < numLocations; i++) {
			boolean success = sql.deleteLocation(defaultLocation.getInt("locationId") + i);
	    	assertEquals(true, success);
		}
		assertEquals(0, sql.getAllLocations().size());
	}
	
	// ======================
    // MUNICIPALITIES TESTS
    // ======================
	
	@Test
	public void testInsertMunicipality() {
		boolean success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
		assertEquals(true, success);
		
		success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
		
		Municipality mun = sql.getMunicipality(defaultMun.getString("name"));
		assertEquals(defaultMun.getString("name"), mun.getName());
		assertEquals(defaultMun.getInt("totalTrees"), mun.getTotalTrees());
		
		int i = 0;
		int[] locIds = {1, 2};
		for (Location border : mun.getBorders()) {
			assertEquals(locIds[i], border.getLocationId());
			i++;
		}
	}
	
	@Test
	public void testUpdateMunicipality() {
		
		boolean success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
		assertEquals(true, success);
		
		success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
		
		success = sql.insertLocation(3, 33.7, 56.8);
		assertEquals(true, success);
		success = sql.insertLocation(4, 76.4, 64.1);
		assertEquals(true, success);
		
		int newTotal = 40;
		String newBorders = "3, 4";
		success = sql.updateMunicipality(defaultMun.getString("name"), newTotal, newBorders);
		assertEquals(true, success);
		
		Municipality mun = sql.getMunicipality(defaultMun.getString("name"));
		assertEquals(defaultMun.getString("name"), mun.getName());
		assertEquals(newTotal, mun.getTotalTrees());
		
		int i = 0;
		int[] locIds = {3, 4};
		for (Location border : mun.getBorders()) {
			assertEquals(locIds[i], border.getLocationId());
			i++;
		}
	}
	
	@Test
	public void testGetAllMunicipalities() {
		
		for (int i = 0; i < numMunicipalities; i++) {
			boolean success = sql.insertMunicipality(defaultMun.getString("name") + i, defaultMun.getInt("totalTrees") + i*10, defaultMun.getString("borders"));
			assertEquals(true, success);
		}
		
		boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
		
		int i = 0;
		for (Municipality mun : sql.getAllMunicipalities()) {
			assertEquals(defaultMun.getString("name") + i, mun.getName());
			assertEquals(defaultMun.getInt("totalTrees") + i*10, mun.getTotalTrees());
			
			int k = 0;
			int[] locIds = {1, 2};
			for (Location border : mun.getBorders()) {
				assertEquals(locIds[k], border.getLocationId());
				k++;
			}
			i++;
		}
	}
	
	@Test
	public void testGetMunicipality() {
		
		for (int i = 0; i < numMunicipalities; i++) {
			boolean success = sql.insertMunicipality(defaultMun.getString("name") + i, defaultMun.getInt("totalTrees") + i*10, defaultMun.getString("borders"));
			assertEquals(true, success);
		}
		
		boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
		
		for (int i = 0; i < numMunicipalities; i++) {
			Municipality mun = sql.getMunicipality(defaultMun.getString("name") + i);
			assertEquals(defaultMun.getString("name") + i, mun.getName());
			assertEquals(defaultMun.getInt("totalTrees") + i*10, mun.getTotalTrees());
			
			int k = 0;
			int[] locIds = {1, 2};
			for (Location border : mun.getBorders()) {
				assertEquals(locIds[k], border.getLocationId());
				k++;
			}
		}
	}
	
	@Test
	public void testDeleteMunicipality() {
		
		for (int i = 0; i < numMunicipalities; i++) {
			boolean success = sql.insertMunicipality(defaultMun.getString("name") + i, defaultMun.getInt("totalTrees") + i*10, defaultMun.getString("borders"));
			assertEquals(true, success);
		}
		
		for (int i = 0; i < numMunicipalities; i++) {
			boolean success = sql.deleteMunicipality(defaultMun.getString("name") + i);
			assertEquals(true, success);
		}
		assertEquals(0, sql.getAllMunicipalities().size());
	}
    
    // ======================
    // TREES TESTS
    // ======================
    
    @Test
    public void testInsertTree() {
    	
    	int treeId = 1;
    	int height = 10;
    	int diameter = 20;
    	String address = defaultUser.getString("addresses");
    	String datePlanted = "2001-12-22";
    	String land = "Residential";
    	String status = "Planted";
    	String ownership = "Private";
    	String species = "Maple";
    	int location = 3;
    	String municipality = "Pointe-Claire";
    	String reports = "";
    	
    	boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
    	
    	success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
    	assertEquals(true, success);
    	success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
    	assertEquals(true, success);
    	
    	success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
    	assertEquals(true, success);
    	
    	Tree tree = sql.getTree(treeId);
    	
    	assertEquals(tree.getTreeId(), treeId);
    	assertEquals(tree.getHeight(), height);
    	assertEquals(tree.getDiameter(), diameter);
    	assertEquals(tree.getAddress(), address);
    	assertEquals(tree.getDatePlanted().toString(), datePlanted);
    	assertEquals(tree.getLand().name(), land);
    	assertEquals(tree.getStatus().name(), status);
    	assertEquals(tree.getOwnership().name(), ownership);
    	assertEquals(tree.getSpecies().getName(), species);
    	assertEquals(tree.getLocation().getLocationId(), location);
    	assertEquals(tree.getMunicipality().getName(), municipality);
    }
    
    @Test
    public void testUpdateTree() {
    	
    	int treeId = 1;
    	int height = 10;
    	int diameter = 20;
    	String address = defaultUser.getString("addresses");
    	String datePlanted = "2001-12-22";
    	String land = "Residential";
    	String status = "Planted";
    	String ownership = "Private";
    	String species = "Maple";
    	int location = 3;
    	String municipality = "Pointe-Claire";
    	String reports = "";
    	
    	boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
    	
    	success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
    	assertEquals(true, success);
    	success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
    	assertEquals(true, success);
    	
    	success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
    	assertEquals(true, success);
    	
    	int newHeight = 39;
    	int newDiameter = 15;
    	String newLand = "Park";
    	String newStatus = "Diseased";
    	String newOwnership = "Public";
    	String newSpecies = "Willow";
    	String newMun = "Vaudreuil";
    	
    	success = sql.insertLocation(4, 34.9, 64.5);
		assertEquals(true, success);
		success = sql.insertLocation(5, 80.8, 30.2);
		assertEquals(true, success);
    	
    	success = sql.insertSpecies(newSpecies, "laudpiud", "Alele");
    	assertEquals(true, success);
    	success = sql.insertMunicipality(newMun, 0, "4, 5");
    	assertEquals(true, success);
    	
    	success = sql.updateTree(treeId, newHeight, newDiameter, newLand, newStatus, newOwnership, newSpecies, newMun, reports);
    	assertEquals(true, success);
    	
    	Tree tree = sql.getTree(treeId);
    	assertEquals(tree.getTreeId(), treeId);
    	assertEquals(tree.getHeight(), newHeight);
    	assertEquals(tree.getDiameter(), newDiameter);
    	assertEquals(tree.getAddress(), address);
    	assertEquals(tree.getDatePlanted().toString(), datePlanted);
    	assertEquals(tree.getLand().name(), newLand);
    	assertEquals(tree.getStatus().name(), newStatus);
    	assertEquals(tree.getOwnership().name(), newOwnership);
    	assertEquals(tree.getSpecies().getName(), newSpecies);
    	assertEquals(tree.getLocation().getLocationId(), location);
    	assertEquals(tree.getMunicipality().getName(), newMun);
    }
    
    @Test
    public void testGetAllTrees() {
    	
    	int treeId = 1;
    	int height = 10;
    	int diameter = 20;
    	String address = defaultUser.getString("addresses");
    	String datePlanted = "2001-12-22";
    	String land = "Residential";
    	String status = "Planted";
    	String ownership = "Private";
    	String species = "Maple";
    	int location = 3;
    	String municipality = "Pointe-Claire";
    	String reports = "";
    	
    	boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
    	
    	success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
    	assertEquals(true, success);
    	success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
    	assertEquals(true, success);
    	
    	success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
    	assertEquals(true, success);
    	
    	for (Tree tree : sql.getAllTrees()) {
    		assertEquals(tree.getTreeId(), treeId);
        	assertEquals(tree.getHeight(), height);
        	assertEquals(tree.getDiameter(), diameter);
        	assertEquals(tree.getAddress(), address);
        	assertEquals(tree.getDatePlanted().toString(), datePlanted);
        	assertEquals(tree.getLand().name(), land);
        	assertEquals(tree.getStatus().name(), status);
        	assertEquals(tree.getOwnership().name(), ownership);
        	assertEquals(tree.getSpecies().getName(), species);
        	assertEquals(tree.getLocation().getLocationId(), location);
        	assertEquals(tree.getMunicipality().getName(), municipality);
    	}
    }
    
    @Test
    public void testGetTree() {
    	int treeId = 1;
    	int height = 10;
    	int diameter = 20;
    	String address = defaultUser.getString("addresses");
    	String datePlanted = "2001-12-22";
    	String land = "Residential";
    	String status = "Planted";
    	String ownership = "Private";
    	String species = "Maple";
    	int location = 3;
    	String municipality = "Pointe-Claire";
    	String reports = "";
    	
    	boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
    	
    	success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
    	assertEquals(true, success);
    	success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
    	assertEquals(true, success);
    	
    	success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
    	assertEquals(true, success);
    	
    	Tree tree = sql.getTree(treeId);
    	assertEquals(tree.getTreeId(), treeId);
    	assertEquals(tree.getHeight(), height);
    	assertEquals(tree.getDiameter(), diameter);
    	assertEquals(tree.getAddress(), address);
    	assertEquals(tree.getDatePlanted().toString(), datePlanted);
    	assertEquals(tree.getLand().name(), land);
    	assertEquals(tree.getStatus().name(), status);
    	assertEquals(tree.getOwnership().name(), ownership);
    	assertEquals(tree.getSpecies().getName(), species);
    	assertEquals(tree.getLocation().getLocationId(), location);
    	assertEquals(tree.getMunicipality().getName(), municipality);
    }
    
    @Test
    public void testGetMaxTreeId() {
    	
    	int treeId1 = 1;
    	int height = 10;
    	int diameter = 20;
    	String address = defaultUser.getString("addresses");
    	String datePlanted = "2001-12-22";
    	String land = "Residential";
    	String status = "Planted";
    	String ownership = "Private";
    	String species = "Maple";
    	int location1 = 3;
    	String municipality = "Pointe-Claire";
    	String reports = "";
    	
    	boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
    	
    	success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
    	assertEquals(true, success);
    	success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
    	assertEquals(true, success);
    	
    	for (int i = 0; i < numTrees; i++) {
    		success = sql.insertTree(treeId1 + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
        	assertEquals(true, success);
    	}
    	assertEquals(numTrees + treeId1 - 1, sql.getMaxTreeId());
    }
    
    @Test
    public void testDeleteTree() {
    	
    	int treeId = 1;
    	int height = 10;
    	int diameter = 20;
    	String address = defaultUser.getString("addresses");
    	String datePlanted = "2001-12-22";
    	String land = "Residential";
    	String status = "Planted";
    	String ownership = "Private";
    	String species = "Maple";
    	int location = 3;
    	String municipality = "Pointe-Claire";
    	String reports = "";
    	
    	boolean success = sql.insertLocation(1, 40.9, 34.5);
		assertEquals(true, success);
		success = sql.insertLocation(2, 50.8, 40.2);
		assertEquals(true, success);
    	
    	success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
    	assertEquals(true, success);
    	success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
    	assertEquals(true, success);
    	success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
    	assertEquals(true, success);
    	
    	success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
    	assertEquals(true, success);
    	
    	success = sql.deleteTree(treeId);
    	assertEquals(true, success);
    	
    	assertEquals(0, sql.getAllTrees().size());
    }
    
}
