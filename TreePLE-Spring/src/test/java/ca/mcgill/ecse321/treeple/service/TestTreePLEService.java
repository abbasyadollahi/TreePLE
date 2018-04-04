package ca.mcgill.ecse321.treeple.service;

import static org.junit.Assert.*;

import java.sql.Date;

import org.json.*;
import org.junit.*;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.model.Tree.*;
import ca.mcgill.ecse321.treeple.model.User.*;
import ca.mcgill.ecse321.treeple.sqlite.SQLiteJDBC;

public class TestTreePLEService {

    private static SQLiteJDBC sql;
    private static TreePLEService service;
    private static final String dbPath = "/output/treeple_test.db";

    private static JSONObject testTree;
    private static JSONObject testUser;
    private static JSONObject testSpecies;
    private static JSONObject testLocation;
    private static JSONObject testMunicipality;

    @BeforeClass
    public static void setUpBeforeClass() {
        sql = new SQLiteJDBC(dbPath);
        sql.connect();
        service = new TreePLEService(sql);

        testTree = buildTestTree();
        testUser = buildTestUser();
        testSpecies = buildTestSpecies();
        testLocation = buildTestLocation();
        testMunicipality = buildTestMunicipality();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        sql.deleteDB();
        sql.closeConnection();
    }

    @After
    public void tearDown() throws Exception {
        service.resetDatabase();
    }

    @Test
    public void testSetMaxIdOnStartup() {
        assertEquals(true, service.setMaxId());
        assertEquals(1, Tree.getNextTreeId());
        assertEquals(1, Location.getNextLocationId());
        assertEquals(1, SurveyReport.getNextReportId());
    }


    // ==============================
    // CREATE USER TEST
    // ==============================

    @Test
    public void testCreateUser() {
        try {
            service.createUser(testUser);
            assertEquals(true, User.hasWithUsername(testUser.getString("username")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullUsername() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", (String) null);
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullPassword() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", (String) null);
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullRole() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", (String) null);
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullAddress() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", (String) null);

        service.createUser(user);
    }

    @Test
    public void testCreateUserBadRole() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "NotARealRole");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
            service.createUser(user);
        } catch (InvalidInputException e) {
            assertEquals("That role doesn't exist!", e.getMessage());
        }
    }

    @Test
    public void testCreateUserResidentialWithEmptyAddress() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "   ");

        try {
            service.createUser(user);
        } catch (InvalidInputException e) {
            assertEquals("Address cannot be empty!", e.getMessage());
        }
    }


    // ==============================
    // CREATE SPECIES TEST
    // ==============================

    @Test
    public void testCreateSpecies() {
        try {
            service.createSpecies(testSpecies);
            assertEquals(true, Species.hasWithName(testSpecies.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateSpeciesNullSpeciesNullGenus() {
        JSONObject species = new JSONObject();
        species.put("name", "Maple");
        species.put("species", (String) null);
        species.put("genus", (String) null);

        try {
            service.createSpecies(species);
            assertEquals(true, Species.hasWithName(species.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void testCreateSpeciesEmptyName() throws Exception {
        JSONObject species = new JSONObject();
        species.put("name", (String) null);
        species.put("species", "Acer Pseudoplatanus");
        species.put("genus", "Acer");

        service.createSpecies(species);
    }


    // ==============================
    // CREATE MUNICIPALITY TEST
    // ==============================

    @Test
    public void testCreateMunicipality() {
        try {
            service.createMunicipality(testMunicipality);
            assertEquals(true, Municipality.hasWithName(testMunicipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateMunicipalityEmptyBorders() {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);
        municipality.put("borders", new JSONArray());

        try {
            service.createMunicipality(municipality);
            assertEquals(true, Municipality.hasWithName(municipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testCreateMunicipalityTwoBorders() throws Exception {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);

        JSONArray borders = new JSONArray();
        borders.put(new JSONArray(new double[]{45.397067, -74.152067}));
        borders.put(new JSONArray(new double[]{45.411974, -74.152188}));
        municipality.put("borders", borders);

        service.createMunicipality(municipality);
    }


    // ==============================
    // CREATE TREE TEST
    // ==============================

    @Test
    public void testCreateTree() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        try {
            JSONObject testTreeObj = testTree.getJSONObject("tree");
            Tree tree = service.createTree(testTree);

            assertEquals(testTreeObj.getInt("treeId") + 1, Tree.getNextTreeId());
            assertEquals(testTreeObj.getInt("treeId") + testMunicipality.getJSONArray("borders").length() + 1, Location.getNextLocationId());
            assertEquals(testTreeObj.getInt("treeId") + 1, SurveyReport.getNextReportId());
            assertEquals(testTreeObj.getInt("height"), tree.getHeight());
            assertEquals(testTreeObj.getInt("diameter"), tree.getDiameter());
            assertEquals(Date.valueOf(testTreeObj.getString("datePlanted")), tree.getDatePlanted());
            assertEquals(Land.valueOf(testTreeObj.getString("land")), tree.getLand());
            assertEquals(Status.valueOf(testTreeObj.getString("status")), tree.getStatus());
            assertEquals(Ownership.valueOf(testTreeObj.getString("ownership")), tree.getOwnership());
            assertEquals(testTreeObj.getString("species"), tree.getSpecies().getName());
            assertEquals(testTreeObj.getDouble("latitude"), tree.getLocation().getLatitude(), 0);
            assertEquals(testTreeObj.getDouble("longitude"), tree.getLocation().getLongitude(), 0);
            assertEquals(testTreeObj.getString("municipality"), tree.getMunicipality().getName());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateTreeWrongDateFormat() throws Exception {
        try {
            JSONObject tree = new JSONObject(testTree.toString());
            tree.getJSONObject("tree").put("datePlanted", "18-03-2018");

            service.createTree(tree);
        } catch (InvalidInputException e) {
            assertEquals("Date doesn't match YYYY-(M)M-(D)D format!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeEmptyUser() throws Exception {
        try {
            JSONObject tree = new JSONObject(testTree.toString());
            tree.put("user", "   ");

            service.createTree(tree);
        } catch (InvalidInputException e) {
            assertEquals("User is not logged in/Username is missing!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeInvalidLocation() throws Exception {
        try {
            JSONObject tree = new JSONObject(testTree.toString());
            tree.getJSONObject("tree").put("latitude", "91");
            tree.getJSONObject("tree").put("longitude", "-181");

            service.createTree(tree);
        } catch (InvalidInputException e) {
            assertEquals("Invalid Google Maps API request!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeUserNonExistant() throws Exception {
        try {
            service.createTree(testTree);
        } catch (InvalidInputException e) {
            assertEquals("User does not exist!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeSpeciesNonExistant() throws Exception {
        service.createUser(testUser);

        try {
            service.createTree(testTree);
        } catch (InvalidInputException e) {
            assertEquals("Species does not exist!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeMunicipalityNonExistant() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);

        try {
            service.createTree(testTree);
        } catch (InvalidInputException e) {
            assertEquals("Municipality does not exist!", e.getMessage());
        }
    }


    // ==============================
    // GET USER TEST
    // ==============================

    @Test
    public void testGetUserByUsername() throws Exception {
        service.createUser(testUser);

        try {
            User user = service.getUserByUsername(testUser.getString("username"));

            assertEquals(testUser.getString("username"), user.getUsername());
            assertEquals(testUser.getString("password"), user.getPassword());
            assertEquals(UserRole.valueOf(testUser.getString("role")) ,user.getRole());
            assertEquals(testUser.getString("myAddresses"), user.getMyAddress(0));
            assertEquals(1, user.getMyAddresses().length);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetUserByUsernameNull() throws Exception {
        String username = null;

        try {
            service.getUserByUsername(username);
        } catch (InvalidInputException e) {
            assertEquals("Username cannot be empty!", e.getMessage());
        }
    }

    @Test
    public void testGetUserByUsernameEmpty() throws Exception {
        String username = "                   ";

        try {
            service.getUserByUsername(username);
        } catch (InvalidInputException e) {
            assertEquals("Username cannot be empty!", e.getMessage());
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testGetUserByUsernameNonExistant() throws Exception {
        service.getUserByUsername("Filip");
    }


    // ==============================
    // GET MUNICIPALITY TEST
    // ==============================

    @Test
    public void testGetMunicipalityByName() throws Exception {
        service.createMunicipality(testMunicipality);

        try {
            Municipality municipality = service.getMunicipalityByName(testMunicipality.getString("name"));

            assertEquals(testMunicipality.getString("name"), municipality.getName());
            assertEquals(testMunicipality.getInt("totalTrees"), municipality.getTotalTrees());

            JSONArray borders = testMunicipality.getJSONArray("borders");
            for (int i = 0; i < borders.length(); i++) {
                assertEquals(borders.getJSONArray(i).getDouble(0), municipality.getBorder(i).getLatitude(), 0);
                assertEquals(borders.getJSONArray(i).getDouble(1), municipality.getBorder(i).getLongitude(), 0);
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetMunicipalityByNameEmpty() throws Exception {
        String name = "      ";

        try {
            service.getMunicipalityByName(name);
        } catch (InvalidInputException e) {
            assertEquals("Name cannot be empty!", e.getMessage());
        }
    }

    @Test
    public void testGetMunicipalityByNameNull() throws Exception{
        String name = null;

        try {
            service.getMunicipalityByName(name);
        } catch (InvalidInputException e) {
            assertEquals("Name cannot be empty!", e.getMessage());
        }

    }

    @Test(expected = InvalidInputException.class)
    public void testGetMunicipalityByNameNonExistant() throws Exception{
        service.getMunicipalityByName("Laval");
    }


    // ==============================
    // GET TREE TEST
    // ==============================

    @Test
    public void testGetTreeById() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = testTree.getJSONObject("tree");
            Tree tree = service.getTreeById(testTreeObj.getInt("treeId"));

            assertEquals(testTreeObj.getInt("height"), tree.getHeight());
            assertEquals(testTreeObj.getInt("diameter"), tree.getDiameter());
            assertEquals(Date.valueOf(testTreeObj.getString("datePlanted")), tree.getDatePlanted());
            assertEquals(Land.valueOf(testTreeObj.getString("land")), tree.getLand());
            assertEquals(Status.valueOf(testTreeObj.getString("status")), tree.getStatus());
            assertEquals(Ownership.valueOf(testTreeObj.getString("ownership")), tree.getOwnership());
            assertEquals(testTreeObj.getString("species"), tree.getSpecies().getName());
            assertEquals(testTreeObj.getDouble("latitude"), tree.getLocation().getLatitude(), 0);
            assertEquals(testTreeObj.getDouble("longitude"), tree.getLocation().getLongitude(), 0);
            assertEquals(testTreeObj.getString("municipality"), tree.getMunicipality().getName());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetTreeByIdNegativeId() throws Exception {
        try {
            service.getTreeById(-1);
        } catch (InvalidInputException e) {
            assertEquals("Tree's ID cannot be negative!", e.getMessage());
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testGetTreeByIdNonExistantTree() throws Exception {
        service.getTreeById(100);
    }


    // ==============================
    // UPDATE TREE TEST
    // ==============================



    // ==============================
    // UPDATE USER TEST
    // ==============================



    // ==============================
    // UPDATE SPECIES TEST
    // ==============================

    @Test
    public void testUpdateSpecies() throws Exception {
        service.createSpecies(testSpecies);

        JSONObject newSpecies = new JSONObject();
        newSpecies.put("name", testSpecies.getString("name"));
        newSpecies.put("species", "Salix Alba");
        newSpecies.put("genus", "Willow");

        try {
            Species species = service.updateSpecies(newSpecies);

            assertEquals(newSpecies.getString("name"), species.getName());
            assertEquals(newSpecies.getString("species"), species.getSpecies());
            assertEquals(newSpecies.getString("genus"), species.getGenus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateSpeciesNullSpeciesNullGenus() throws Exception {
        service.createSpecies(testSpecies);

        JSONObject newSpecies = new JSONObject();
        newSpecies.put("name", testSpecies.getString("name"));
        newSpecies.put("species", (String) null);
        newSpecies.put("genus", (String) null);

        try {
            Species species = service.updateSpecies(newSpecies);

            assertEquals(newSpecies.getString("name"), species.getName());
            assertEquals("", species.getSpecies());
            assertEquals("", species.getGenus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void testUpdateSpeciesNullName() throws Exception {
        JSONObject species = new JSONObject();
        species.put("name", (String) null);
        species.put("species", "Acer Pseudoplatanus");
        species.put("genus", "Acer");

        service.updateSpecies(species);
    }

    @Test
    public void testUpdateSpeciesEmptyName() throws Exception {
        JSONObject newSpecies = new JSONObject();
        newSpecies.put("name", "     ");
        newSpecies.put("species", "Acer Pseudoplatanus");
        newSpecies.put("genus", "Acer");

        try {
            service.updateSpecies(newSpecies);
        } catch (Exception e) {
            assertEquals("Species name cannot be empty!", e.getMessage());
        }

    }


    // ==============================
    // UPDATE MUNICIPALITY TEST
    // ==============================

    /*
    @Test
    public void testUpdateMunicipality() throws Exception {
        JSONObject oldMunicipality = new JSONObject();
        oldMunicipality.put("name", testMunicipality.get("name"));
        oldMunicipality.put("totalTrees", testMunicipality.get("totalTrees"));
        JSONArray borders = new JSONArray();

        borders.put(new JSONArray(new double[]{35.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{35.481864, -73.773715}));
        borders.put(new JSONArray(new double[]{35.460268, -73.750029}));
        borders.put(new JSONArray(new double[]{35.481208, -73.723422}));
        borders.put(new JSONArray(new double[]{35.459034, -73.683652}));
        borders.put(new JSONArray(new double[]{35.526536, -73.651208}));
        borders.put(new JSONArray(new double[]{35.522407, -73.730198}));
        borders.put(new JSONArray(new double[]{35.528407, -73.730198}));
        borders.put(new JSONArray(new double[]{35.538407, -73.730198}));

        oldMunicipality.put("borders", borders);

        service.createMunicipality(oldMunicipality);
        try {
            service.updateMunicipality(testMunicipality);
            assertEquals(true, Municipality.hasWithName(testMunicipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateMunicipalityEmptyBorders() {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);
        municipality.put("borders", new JSONArray());

        try {
            service.createMunicipality(municipality);
            assertEquals(true, Municipality.hasWithName(municipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testUpdateMunicipalityTwoBorders() throws Exception {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);

        JSONArray borders = new JSONArray();
        borders.put(new JSONArray(new double[]{45.397067, -74.152067}));
        borders.put(new JSONArray(new double[]{45.411974, -74.152188}));
        municipality.put("borders", borders);

        service.createMunicipality(municipality);
    }*/


    // ==============================
    // DELETE USER TEST
    // ==============================

    @Test
    public void testDeleteUser() throws Exception {
        service.createUser(testUser);

        JSONObject deleteUser = new JSONObject();
        deleteUser.put("username", testUser.getString("username"));

        try {
            User user = service.deleteUser(deleteUser);

            assertEquals(testUser.getString("username"), user.getUsername());
            assertEquals(testUser.getString("password"), user.getPassword());
            assertEquals(UserRole.valueOf(testUser.getString("role")), user.getRole());
            assertEquals(testUser.getString("myAddresses"), user.getMyAddress(0));
            assertEquals(false, User.hasWithUsername(testUser.getString("username")));
            try {
                service.getUserByUsername(testUser.getString("username"));
            } catch (InvalidInputException e) {
                assertEquals("That username doesn't exist!", e.getMessage());
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteUserEmpty() throws Exception {
        JSONObject deleteUser = new JSONObject();
        deleteUser.put("username", "      ");

        try {
            service.deleteUser(deleteUser);
        } catch (InvalidInputException e) {
            assertEquals ("User is not logged in/Username is missing!", e.getMessage());
        }
    }

    @Test(expected = JSONException.class)
    public void testDeleteUserNull() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", (String) null);

        service.deleteUser(user);
    }

    @Test
    public void testDeleteUserNonExistant() throws Exception {
        JSONObject deleteUser = new JSONObject();
        deleteUser.put("username", testUser.getString("username"));

        try {
            service.deleteUser(deleteUser);
        } catch (InvalidInputException e) {
            assertEquals("That username doesn't exist!", e.getMessage());
        }
    }


    // ==============================
    // DELETE SPECIES TEST
    // ==============================

    @Test
    public void testDeleteSpecies() throws Exception {
        service.createSpecies(testSpecies);

        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", testSpecies.getString("name"));

        try {
            Species species = service.deleteSpecies(deleteSpecies);

            assertEquals(testSpecies.getString("name"), species.getName());
            assertEquals(testSpecies.getString("species"), species.getSpecies());
            assertEquals(testSpecies.getString("genus"), species.getGenus());
            assertEquals(false, Species.hasWithName(testSpecies.getString("name")));
            // try {
            //     service.getSpeciesByName(testSpecies.getString("name"));
            // } catch (InvalidInputException e) {
            //     assertEquals("No Species with that name exists!", e.getMessage());
            // }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteSpeciesEmpty() throws Exception {
        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", "      ");

        try {
            service.deleteSpecies(deleteSpecies);
        } catch (InvalidInputException e) {
            assertEquals("Species' name is missing!", e.getMessage());
        }
    }

    @Test(expected = JSONException.class)
    public void testDeleteSpeciesNull() throws Exception {
        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", (String) null);

        service.deleteSpecies(deleteSpecies);
    }

    @Test
    public void testDeleteSpeciesNonExistant() throws Exception {
        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", testSpecies.getString("name"));

        try {
            service.deleteSpecies(deleteSpecies);
        } catch (InvalidInputException e) {
            assertEquals("No Species with that name exists!", e.getMessage());
        }
    }


    // ==============================
    // DELETE LOCATION TEST
    // ==============================

    @Test
    public void testDeleteLocation() throws Exception {
        int locationsBefore = Location.getNextLocationId();

        Location locationObj = new Location(testLocation.getDouble("latitude"), testLocation.getDouble("longitude"));
        sql.insertLocation(locationObj.getLocationId(), locationObj.getLatitude(), locationObj.getLongitude());


        JSONObject deleteLocation = new JSONObject();
        deleteLocation.put("locationId", locationObj.getLocationId());

        try {
            Location location = service.deleteLocation(deleteLocation);
            int locationsAfter = Location.getNextLocationId();

            assertEquals(testLocation.getDouble("latitude"), location.getLatitude(), 0);
            assertEquals(testLocation.getDouble("longitude"), location.getLongitude(), 0);
            assertEquals(locationsBefore, locationsAfter);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteLocationNegativeId() throws Exception {
        JSONObject deleteLocation = new JSONObject();
        deleteLocation.put("locationId", -1);

        try {
            service.deleteLocation(deleteLocation);
        } catch (InvalidInputException e) {
            assertEquals("Location's ID cannot be negative or zero!", e.getMessage());
        }
    }

    @Test
    public void testDeleteLocationNonExistant() throws Exception {
        JSONObject deleteLocation = new JSONObject();
        deleteLocation.put("locationId", testLocation.getInt("locationId"));

        try {
            service.deleteLocation(deleteLocation);
        } catch (InvalidInputException e) {
            assertEquals("No Location with that ID exists!", e.getMessage());
        }
    }


    // ==============================
    // DELETE MUNICIPALITY TEST
    // ==============================

    @Test
    public void testDeleteMunicipality() throws Exception {
        service.createMunicipality(testMunicipality);

        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", testMunicipality.getString("name"));

        try {
            Municipality municipality = service.deleteMunicipality(deleteMunicipality);

            assertEquals(testMunicipality.getString("name"), municipality.getName());
            assertEquals(testMunicipality.getInt("totalTrees"), municipality.getTotalTrees());
            assertEquals(false, Municipality.hasWithName(testMunicipality.getString("name")));

            for (int i = 0; i < municipality.getBorders().size(); i++) {
                assertEquals(testMunicipality.getJSONArray("borders").getJSONArray(i).getDouble(0),
                             municipality.getBorder(i).getLatitude(), 0);
                assertEquals(testMunicipality.getJSONArray("borders").getJSONArray(i).getDouble(1),
                             municipality.getBorder(i).getLongitude(), 0);
            }
            try {
                service.getMunicipalityByName(testMunicipality.getString("name"));
            } catch (InvalidInputException e) {
                assertEquals("No Municipality with that name exists!", e.getMessage());
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteMunicipalityEmpty() throws Exception {
        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", "    ");

        try {
            service.deleteMunicipality(deleteMunicipality);
        } catch (InvalidInputException e) {
            assertEquals("Municipality's name is missing!", e.getMessage());
        }
    }

    @Test(expected = JSONException.class)
    public void testDeleteMunicipalityNull() throws Exception {
        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", (String) null);

        service.deleteMunicipality(deleteMunicipality);
    }

    @Test
    public void testDeleteMunicipalityNonExistant() throws Exception {
        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", testMunicipality.getString("name"));

        try {
            service.deleteMunicipality(deleteMunicipality);
        } catch (InvalidInputException e) {
            assertEquals("No Municipality with that name exists!", e.getMessage());
        }
    }


    // ==============================
    // DELETE TREE TEST
    // ==============================

    @Test
    public void testDeleteTree() throws Exception {
        int treesBefore = Tree.getNextTreeId();
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject treeObj = testTree.getJSONObject("tree");
        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", testUser.getString("username"));
        deleteTree.put("treeId", treeObj.getInt("treeId"));

        try {
            Tree tree = service.deleteTree(deleteTree);
            int treesAfter = Tree.getNextTreeId();

            assertEquals(treeObj.getInt("height"), tree.getHeight());
            assertEquals(treeObj.getInt("diameter"), tree.getDiameter());
            assertEquals(Date.valueOf(treeObj.getString("datePlanted")), tree.getDatePlanted());
            assertEquals(Land.valueOf(treeObj.getString("land")), tree.getLand());
            assertEquals(Status.valueOf(treeObj.getString("status")), tree.getStatus());
            assertEquals(Ownership.valueOf(treeObj.getString("ownership")), tree.getOwnership());
            assertEquals(treeObj.getString("species"), tree.getSpecies().getName());
            assertEquals(treeObj.getDouble("latitude"), tree.getLocation().getLatitude(), 0);
            assertEquals(treeObj.getDouble("longitude"), tree.getLocation().getLongitude(), 0);
            assertEquals(treeObj.getString("municipality"), tree.getMunicipality().getName());
            assertEquals(treesBefore, treesAfter);
        } catch (InvalidInputException e) {
            fail();
        }
    }

    @Test
    public void testDeleteTreeNonExistantTree() throws Exception {
        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", testUser.getString("username"));
        deleteTree.put("treeId", testTree.getJSONObject("tree").getInt("treeId"));

        try {
           service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
           assertEquals("No Tree with that ID exists!", e.getMessage());
        }
    }

    @Test
    public void testDeleteTreeNonExistantUser() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", "IDontExist");
        deleteTree.put("treeId", testTree.getJSONObject("tree").getInt("treeId"));

        try {
            service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
            assertEquals("That username doesn't exist!", e.getMessage());
        }
    }

    @Test
    public void testDeleteTreeUserDoesntOwnTree() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject user = new JSONObject(testUser.toString());
        user.put("username", "RandomUser");
        service.createUser(user);

        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", user.getString("username"));
        deleteTree.put("treeId", testTree.getJSONObject("tree").getInt("treeId"));

        try {
            service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
            assertEquals("This Tree wasn't planted by you!", e.getMessage());
        }
    }


    // ==============================
    // BUILD TEST OBJECTS API
    // ==============================

    public static JSONObject buildTestTree() {
        JSONObject testTree = new JSONObject();
        JSONObject tree = new JSONObject();

        tree.put("treeId", 1);
        tree.put("height", 420);
        tree.put("diameter", 40);
        tree.put("datePlanted", "2018-03-16");
        tree.put("land", "Residential");
        tree.put("status", "Planted");
        tree.put("ownership", "Private");
        tree.put("species", "Weeping Willow");
        tree.put("latitude", 45.515883);
        tree.put("longitude", -73.685552);
        tree.put("municipality", "Saint-Laurent");

        testTree.put("user", "Abbas");
        testTree.put("tree", tree);

        return testTree;
    }

    public static JSONObject buildTestUser() {
        JSONObject testUser = new JSONObject();

        testUser.put("username", "Abbas");
        testUser.put("password", "ecse321pw");
        testUser.put("role", "Resident");
        testUser.put("scientistKey", "");
        testUser.put("myAddresses", "H4L3N1");

        return testUser;
    }

    public static JSONObject buildTestSpecies() {
        JSONObject testSpecies = new JSONObject();

        testSpecies.put("name", "Weeping Willow");
        testSpecies.put("species", "Salix Babylonica");
        testSpecies.put("genus", "Salix");

        return testSpecies;
    }

    public static JSONObject buildTestLocation() {
        JSONObject testLocation = new JSONObject();

        testLocation.put("locationId", 1);
        testLocation.put("latitude", 45.515897);
        testLocation.put("longitude", -73.685548);

        return testLocation;
    }

    public static JSONObject buildTestMunicipality() {
        JSONObject testMunicipality = new JSONObject();
        JSONArray borders = new JSONArray();

        borders.put(new JSONArray(new double[]{45.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{45.481864, -73.773715}));
        borders.put(new JSONArray(new double[]{45.460268, -73.750029}));
        borders.put(new JSONArray(new double[]{45.481208, -73.723422}));
        borders.put(new JSONArray(new double[]{45.459034, -73.683652}));
        borders.put(new JSONArray(new double[]{45.526536, -73.651208}));
        borders.put(new JSONArray(new double[]{45.522407, -73.730198}));

        testMunicipality.put("name", "Saint-Laurent");
        testMunicipality.put("totalTrees", 55);
        testMunicipality.put("borders", borders);

        return testMunicipality;
    }
}
