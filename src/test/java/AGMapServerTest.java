//import static org.junit.Assert.*;
//import org.junit.Test;
//import org.junit.Before;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileInputStream;
//import java.io.ObjectInputStream;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//
//public class AGMapServerTest {
//    static List<TestParams> params;
//    private static boolean initialized = false;
//    static final double doubleThreshhold = 0.0000000000001;
//
//    /**
//     * Initializes the student MapServer statically.
//     * Reads in the serialized <code>List</code> of TestParams.
//     * @throws Exception
//     */
//    @Before
//    public void setUp() throws Exception {
//        if (initialized) return;
//        MapServer.initialize();
//        FileInputStream fis = new FileInputStream("test_data_v2");
//        ObjectInputStream ois = new ObjectInputStream(fis);
//        params = (List<TestParams>) ois.readObject();
//        ois.close();
//        initialized = true;
//    }
//
////<<<<<<< HEAD
////    private void checkParamsMap(String err, Map<String, Object> m1, Map<String, Object> m2) {
////        for (String key : m1.keySet()) {
////            assertTrue(m2.containsKey(key));
////            Object o1 = m1.get(key);
////            Object o2 = m2.get(key);
////            if (o1 instanceof Double && o2 instanceof Double) {
////                assertTrue(err, Math.abs((Double)o1 - (Double)o2) < doubleThreshhold);
////            } else {
////                assertEquals(err, o1, o2);
////            }
////        }
////    }
////
////    /**
////     * Test the rastering functionality of the student code, by writing the ByteArrayOutputStream
////     * to a byte[] array and comparing it with the staff result byte-wise.
////     * @throws Exception
////     */
////    @Test
////    public void testGetMapRaster() throws Exception {
////        MapServer.clearRoute();
////        for (int i = 0; i < params.size(); i++) {
////            TestParams p = params.get(i);
////            ByteArrayOutputStream os = new ByteArrayOutputStream();
////            MapServer.getMapRaster(p.raster_params, os);
////            byte[] student_output = os.toByteArray();
////            assertArrayEquals("Raw image output differed for input: " + p.raster_params + ".\n See " +
////                    "example image " + i + ".\n", p.raster_output, student_output);
////        }
////    }
////
////    /**
////     * Check the student raster output parameters against the staff output parameters.
////     * @throws Exception
////     */
////    @Test
////    public void testGetMapRasterParams() throws Exception {
////        for (TestParams p : params) {
////            ByteArrayOutputStream os = new ByteArrayOutputStream();
////            Map<String, Object> student_raster_result = MapServer.getMapRaster(p.raster_params, os);
////            System.out.println(p.raster_result);
////            System.out.println(student_raster_result);
////            checkParamsMap("Returned parameters differed for input: " + p.raster_params + ".\n"
////                    , p.raster_result, student_raster_result);
////        }
////    }
////=======
//    private void checkParamsMap(String err, Map<String, Object> m1, Map<String, Object> m2) {
//        for (String key : m1.keySet()) {
//            assertTrue(m2.containsKey(key));
//            Object o1 = m1.get(key);
//            Object o2 = m2.get(key);
//            if (o1 instanceof Double) {
//                assertTrue(err, Math.abs((Double)o1 - (Double)o2) < doubleThreshhold);
//            } else {
//                assertEquals(err, o1, o2);
//            }
//        }
//    }
//
//    /**
//     * Test the rastering functionality of the student code, by writing the ByteArrayOutputStream
//     * to a byte[] array and comparing it with the staff result byte-wise.
//     * @throws Exception
//     */
//    @Test
//    public void testGetMapRaster() throws Exception {
//        MapServer.clearRoute();
//        for (int i = 0; i < params.size(); i++) {
//            TestParams p = params.get(i);
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            MapServer.getMapRaster(p.raster_params, os);
//            byte[] student_output = os.toByteArray();
//            assertArrayEquals("Raw image output differed for input: " + p.raster_params + ".\n See " +
//                    "example image " + i + ".\n", p.raster_output, student_output);
//        }
//    }
//
//    /**
//     * Check the student raster output parameters against the staff output parameters.
//     * @throws Exception
//     */
//    @Test
//    public void testGetMapRasterParams() throws Exception {
//        for (TestParams p : params) {
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            Map<String, Object> student_raster_result = MapServer.getMapRaster(p.raster_params, os);
//            checkParamsMap("Returned parameters differed for input: " + p.raster_params + ".\n"
//                    , p.raster_result, student_raster_result);
//        }
//    }
////>>>>>>> 4d09edff37369e50601cd0f5649d59e452082608
//
//
//    /**
//     * Test the routefinding functionality by comparing the node id list item by item.
//     * @throws Exception
//     */
//    @Test
//    public void testFindAndSetRoute() throws Exception {
//        for (TestParams p : params) {
//            List<Long> student_route_result = MapServer.findAndSetRoute(p.route_params);
//            assertEquals("Found route differs for input: " + p.raster_params + ".\n",
//                    p.route_result, student_route_result);
//        }
//    }
//
//    /**
//     * Test the route raster the same way the map raster is tested, except with the route pre-set
//     * before making the call to raster.
//     * @throws Exception
//     */
////    @Test
////    public void testRouteRaster() throws Exception {
////        for (int i = 0; i < params.size(); i++) {
////            TestParams p = params.get(i);
////            MapServer.findAndSetRoute(p.route_params);
////            ByteArrayOutputStream os = new ByteArrayOutputStream();
////            MapServer.getMapRaster(p.raster_params, os);
////            byte[] student_output = os.toByteArray();
////            assertArrayEquals("Raw image output differed for input: " + p.raster_params + "\nWith" +
////                    " route params: " + p.route_params + ".\n See " +
////                    "example image " + i + ".\n", p.route_raster, student_output);
////        }
////    }
//
//
//    /**
//     * Test Autocomplete for each prefix, comparing the sets of outputs against each other.
//     * @throws Exception
//     */
////    @Test
////    public void testGetLocationsByPrefix() throws Exception {
////        for (TestParams p : params) {
////            List<String> student_autocomplete_result = MapServer.getLocationsByPrefix(p
////                    .prefix_search_param);
////            HashSet<String> set_student = new HashSet<>(student_autocomplete_result);
////            HashSet<String> set_staff = new HashSet<>(p.autocomplete_results);
////
////            assertEquals("Autocompletion results differ for prefix " + p.prefix_search_param,
////                    set_staff, set_student);
////        }
////    }
////
////    /**
////     * Test location search by full search string, comparing the output lists against each other
////     * element by element; note that we assume the most reasonable construction of each of these
////     * lists, that is, that they are in order of the locations as they appear in the OSM file.
////     * @throws Exception
////     */
////    @Test
////    public void testGetLocations() throws Exception {
////        for (TestParams p : params) {
////            List<Map<String, Object>> student_search_result = MapServer.getLocations(p.actual_search_param);
////            Collections.sort(student_search_result,
////                    (Map<String, Object> o1, Map<String, Object> o2) ->
////                            ((Long) o1.get("id")).compareTo((Long) o2.get("id")));
////            assertEquals("Search results differ for search term: " + p.actual_search_param,
////                    p.actual_search_result, student_search_result);
////        }
////    }
//}
//
////[53055671, 53055669, 206093746, 206093737, 311881838, 311881839, 53113941, 687156445, 206093720, 687156444, 687156443, 206140573, 687156442, 686812170, 53144625, 687156441, 686812168, 247703639, 247703638, 53149956, 247703637, 53111730, 686812166, 2664661348, 697180293, 53085385, 2664661346, 2664661347, 53142555, 2664661345, 53100813, 206140572, 957600579, 957600576, 305541372, 957600568, 206140571, 2664661349, 957600561, 53078473, 957600555, 206140570, 206140569, 956500324, 206140568, 956500319, 53140454, 53149961, 683050103, 2664625370, 683050102, 53149962, 683050101, 683050100, 956500323, 53119040, 956500322, 256542899, 53149965, 256543149, 256543153, 956500321, 256543152, 956500320, 256543151, 256543150, 53119042, 256543310, 266636093, 256543311, 266636094, 256543313, 53065783, 256543314, 256543315, 256543317, 256543318, 256543319, 256543320, 256543321, 53099375, 956500325, 245068539, 245068908, 245068540, 53119037, 245068542, 245068543, 245068544, 245068545, 245068546, 245068547, 53119038, 266635755, 245067850, 266635756, 245067851, 245067852, 256543047, 53066396, 683050096, 53119046, 245067853, 245067854]>
////[53055671, 53055669, 206093746, 206093737, 311881838, 311881839, 53113941, 687156445, 206093720, 687156444, 687156443, 206140573, 687156442, 686812170, 53144625, 687156441, 686812168, 247703639, 247703638, 53149956, 247703637, 53111730, 686812166, 2664661348, 697180293, 53085385, 2664661346, 2664661347, 53142555, 2664661345, 53100813, 206140572, 957600579, 957600576, 305541372, 957600568, 206140571, 2664661349, 957600561, 53078473, 957600555, 206140570, 206140569, 956500324, 206140568, 956500319, 53140452, 53140454, 53149961, 683050103, 2664625370, 683050102, 53149962, 683050101, 683050100, 956500323, 53119040, 956500322, 256542899, 53149965, 256543149, 256543153, 956500321, 256543152, 956500320, 256543151, 256543150, 53119042, 256543310, 266636093, 256543311, 266636094, 256543313, 53065783, 256543314, 256543315, 256543317, 256543318, 256543319, 256543320, 256543321, 53099375, 956500325, 245068539, 245068908, 245068540, 53119037, 245068542, 245068543, 245068544, 245068545, 245068546, 245068547, 53119038, 266635755, 245067850, 266635756, 245067851, 245067852, 256543047, 53066396, 683050096, 53119046, 245067853, 245067854]
//
////<<<<<<< HEAD
//// <[53055671, 53055669, 206093746, 206093737, 311881838, 311881839, 53113941, 687156445, 206093720, 687156444, 687156443, 206140573, 687156442, 686812170, 53144625, 687156441, 686812168, 247703639, 247703638, 53149956, 247703637, 53111730, 686812166, 2664661348, 697180293, 53085385, 2664661346, 2664661347, 53142555, 2664661345, 53100813, 206140572, 957600579, 957600576, 305541372, 957600568, 206140571, 2664661349, 957600561, 53078473, 957600555, 206140570, 206140569, 956500324, 206140568, 956500319, 53140454, 53149961, 683050103, 2664625370, 683050102, 53149962, 683050101, 683050100, 956500323, 53119040, 956500322, 256542899, 53149965, 256543149, 256543153, 956500321, 256543152, 956500320, 256543151, 256543150, 53119042, 256543310, 266636093, 256543311, 266636094, 256543313, 53065783, 256543314, 256543315, 256543317, 256543318, 256543319, 256543320, 256543321, 53099375, 956500325, 245068539, 245068908, 245068540, 53119037, 245068542, 245068543, 245068544, 245068545, 245068546, 245068547, 53119038, 266635755, 245067850, 266635756, 245067851, 245067852, 256543047, 53066396, 683050096, 53119046, 245067853, 245067854]>
//// <[53055671, 53055669, 206093746, 206093737, 311881838, 311881839, 53113941, 687156445, 206093720, 687156444, 687156443, 206140573, 687156442, 686812170, 53144625, 687156441, 686812168, 247703639, 247703638, 53149956, 247703637, 53111730, 686812166, 2664661348, 697180293, 53085385, 2664661346, 2664661347, 53142555, 2664661345, 53100813, 206140572, 957600579, 957600576, 305541372, 957600568, 206140571, 2664661349, 957600561, 53078473, 957600555, 206140570, 206140569, 956500324, 206140568, 956500319, 53140452, 53140454, 53149961, 683050103, 2664625370, 683050102, 53149962, 683050101, 683050100, 956500323, 53119040, 956500322, 256542899, 53149965, 256543149, 256543153, 956500321, 256543152, 956500320, 256543151, 256543150, 53119042, 256543310, 266636093, 256543311, 266636094, 256543313, 53065783, 256543314, 256543315, 256543317, 256543318, 256543319, 256543320, 256543321, 53099375, 956500325, 245068539, 245068908, 245068540, 53119037, 245068542, 245068543, 245068544, 245068545, 245068546, 245068547, 53119038, 266635755, 245067850, 266635756, 245067851, 245067852, 256543047, 53066396, 683050096, 53119046, 245067853, 245067854]
//
//// [266433383, 53122181, 53099306, 53099304, 312431308, 312431297, 312431298, 1237053599, 53121454, 1237053615, 1237053647, 651063703, 1237053624, 53107946, 53085960, 1237053585, 1237053720, 53085999, 53088345, 1237053584, 53088338, 53106473, 1237053711, 53106431, 53121461, 53121462, 53096046, 53043874, 53096049, 53096051, 2820169740, 2820169756, 2820169751, 2820169731, 2820169753, 53082577, 623807841, 53064680, 3701955052, 2086760873, 53037909, 2086760870, 2086696624, 53037907, 275782472, 370473556, 2086667342]
//// [266433383, 53122181, 53099306, 53099304, 312431308, 312431297, 312431298, 1237053599, 53121454, 1237053615, 1237053647, 651063703, 1237053624, 53107946, 53085960, 1237053585, 1237053720, 53085999, 53088345, 1237053584, 53088338, 53106473, 1237053711, 53106431, 53121461, 53121462, 53095315, 2820169736, 2820169760, 53095316, 275791869, 88315233, 53145376, 275792097, 275792098, 2820169746, 53096051, 2820169740, 2820169756, 2820169751, 2820169731, 2820169753, 53082577, 623807841, 53064680, 3701955052, 2086760873, 53037909, 2086760870, 2086696624, 53037907, 275782472, 370473556, 2086667342]>
//
//// 53096046, 53043874, 53096049, 53096051, 2820169740, 2820169756, 2820169751, 2820169731, 2820169753, 53082577, 623807841, 53064680, 3701955052, 2086760873, 53037909, 2086760870, 2086696624, 53037907, 275782472, 370473556, 2086667342]
//// 53095315, 2820169736, 2820169760, 53095316, 275791869, 88315233, 53145376, 275792097, 275792098, 2820169746, 53096051, 2820169740, 2820169756, 2820169751, 2820169731, 2820169753, 53082577, 623807841, 53064680, 3701955052, 2086760873, 53037909, 2086760870, 2086696624, 53037907, 275782472, 370473556, 2086667342]>
////=======
//    /**
//     * Test location search by full search string, comparing the output lists against each other
//     * element by element; note that we assume the most reasonable construction of each of these
//     * lists, that is, that they are in order of the locations as they appear in the OSM file.
//     * @throws Exception
//     */
//    @Test
//    public void testGetLocations() throws Exception {
//        for (TestParams p : params) {
//            List<Map<String, Object>> student_search_result = MapServer.getLocations(p.actual_search_param);
//            assertEquals("Search results differ for search term: " + p.actual_search_param,
//                    p.actual_search_result, student_search_result);
//        }
//    }
//}
//>>>>>>> 4d09edff37369e50601cd0f5649d59e452082608
