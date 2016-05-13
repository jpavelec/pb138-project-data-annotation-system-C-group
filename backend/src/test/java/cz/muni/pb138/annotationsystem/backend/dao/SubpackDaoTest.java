package cz.muni.pb138.annotationsystem.backend.dao;

import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Josef Pavelec <jospavelec@gmail.com>
 */
public class SubpackDaoTest {

    public SubpackDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class SubpackDaoImpl.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        Subpack subpack = null;
        SubpackDaoImpl instance = null;
        instance.create(subpack);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getById method, of class SubpackDaoImpl.
     */
    @Test
    public void testGetById() throws Exception {
        System.out.println("getById");
        Long id = null;
        SubpackDaoImpl instance = null;
        Subpack expResult = null;
        Subpack result = instance.getById(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class SubpackDaoImpl.
     */
    @Test
    public void testUpdate() throws Exception {
        System.out.println("update");
        Subpack subpack = null;
        SubpackDaoImpl instance = null;
        instance.update(subpack);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class SubpackDaoImpl.
     */
    @Test
    public void testDelete() throws Exception {
        System.out.println("delete");
        Subpack subpack = null;
        SubpackDaoImpl instance = null;
        instance.delete(subpack);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}