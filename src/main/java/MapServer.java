import java.awt.Color;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Base64;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Collections;


/* Maven is used to pull in these dependencies. */
import com.google.gson.Gson;


import javax.imageio.ImageIO;

import static spark.Spark.*;

/**
 * This MapServer class is the entry point for running the JavaSpark web server for the BearMaps
 * application project, receiving API calls, handling the API call processing, and generating
 * requested images and routes.
 *
 * @author Alan Yao
 */
public class MapServer {
    /**
     * The root upper left/lower right longitudes and latitudes represent the bounding box of
     * the root tile, as the images in the img/ folder are scraped.
     * Longitude == x-axis; latitude == y-axis.
     */
    private static final int MAX_DEPTH = 7;
    private static int testNumber = 0;
    private static QuadTree quadTree = new QuadTree(MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT,
            MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT);
    private static LinkedList<Long> shortestPath = new LinkedList<>();
    private static ArrayList<GraphNode> shortestPathNodes = new ArrayList<>();
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    /**
     * Each tile is 256x256 pixels.
     */
    public static final int TILE_SIZE = 256;
    /**
     * HTTP failed response.
     */
    private static final int HALT_RESPONSE = 403;
    /**
     * Route stroke information: typically roads are not more than 5px wide.
     */
    public static final float ROUTE_STROKE_WIDTH_PX = 5.0f;
    /**
     * Route stroke information: Cyan with half transparency.
     */
    public static final Color ROUTE_STROKE_COLOR = new Color(108, 181, 230, 200);
    /**
     * The tile images are in the IMG_ROOT folder.
     */
    private static final String IMG_ROOT = "img/";
    /**
     * The OSM XML file path. Downloaded from <a href="http://download.bbbike.org/osm/">here</a>
     * using custom region selection.
     **/
    private static final String OSM_DB_PATH = "berkeley.osm";
    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside getMapRaster(). <br>
     * ullat -> upper left corner latitude,<br> ullon -> upper left corner longitude, <br>
     * lrlat -> lower right corner latitude,<br> lrlon -> lower right corner longitude <br>
     * w -> user viewport window width in pixels,<br> h -> user viewport height in pixels.
     **/
    private static final String[] REQUIRED_RASTER_REQUEST_PARAMS
            = {"ullat", "ullon", "lrlat", "lrlon", "w", "h"};
    /**
     * Each route request to the server will have the following parameters
     * as keys in the params map.<br>
     * start_lat -> start point latitude,<br> start_lon -> start point longitude,<br>
     * end_lat -> end point latitude, <br>end_lon -> end point longitude.
     **/
    private static final String[] REQUIRED_ROUTE_REQUEST_PARAMS
            = {"start_lat", "start_lon", "end_lat", "end_lon"};
    /* Define any static variables here. Do not define any instance variables of MapServer. */
    private static GraphDB g;

    /**
     * Place any initialization statements that will be run before the server main loop here.
     * Do not place it in the main function. Do not place initialization code anywhere else.
     * This is for testing purposes, and you may fail tests otherwise.
     **/
    public static void initialize() {
        g = new GraphDB(OSM_DB_PATH);
    }

    public static void main(String[] args) {
        initialize();
        staticFileLocation("/page");
        /* Allow for all origin requests (since this is not an authenticated server, we do not
         * care about CSRF).  */
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });

        /* Define the raster endpoint for HTTP GET requests. I use anonymous functions to define
         * the request handlers. */
        get("/raster", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_RASTER_REQUEST_PARAMS);
            /* The png image is written to the ByteArrayOutputStream */
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            /* getMapRaster() does almost all the work for this API call */
            Map<String, Object> rasteredImgParams = getMapRaster(params, os);
            /* On an image query success, add the image data to the response */
            if (rasteredImgParams.containsKey("query_success")
                    && (Boolean) rasteredImgParams.get("query_success")) {
                String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
                rasteredImgParams.put("b64_encoded_image_data", encodedImage);
            }
            /* Encode response to Json */
            Gson gson = new Gson();
            return gson.toJson(rasteredImgParams);
        });

        /* Define the routing endpoint for HTTP GET requests. */
        get("/route", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_ROUTE_REQUEST_PARAMS);
            LinkedList<Long> route = findAndSetRoute(params);
            return !route.isEmpty();
        });

        /* Define the API endpoint for clearing the current route. */
        get("/clear_route", (req, res) -> {
            clearRoute();
            return true;
        });

        /* Define the API endpoint for search */
        get("/search", (req, res) -> {
            Set<String> reqParams = req.queryParams();
            String term = req.queryParams("term");
            Gson gson = new Gson();
            /* Search for actual location data. */
            if (reqParams.contains("full")) {
                List<Map<String, Object>> data = getLocations(term);
                return gson.toJson(data);
            } else {
                /* Search for prefix matching strings. */
                List<String> matches = getLocationsByPrefix(term);
                return gson.toJson(matches);
            }
        });

        /* Define map application redirect */
        get("/", (request, response) -> {
            response.redirect("/map.html", 301);
            return true;
        });
    }

    /**
     * Validate & return a parameter map of the required request parameters.
     * Requires that all input parameters are doubles.
     *
     * @param req            HTTP Request
     * @param requiredParams TestParams to validate
     * @return A populated map of input parameter to it's numerical value.
     */
    private static HashMap<String, Double> getRequestParams(
            spark.Request req, String[] requiredParams) {
        Set<String> reqParams = req.queryParams();
        HashMap<String, Double> params = new HashMap<>();
        for (String param : requiredParams) {
            if (!reqParams.contains(param)) {
                halt(HALT_RESPONSE, "Request failed - parameters missing.");
            } else {
                try {
                    params.put(param, Double.parseDouble(req.queryParams(param)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    halt(HALT_RESPONSE, "Incorrect parameters - provide numbers.");
                }
            }
        }
        return params;
    }


    /**
     * Handles raster API calls, queries for tiles and rasters the full image. <br>
     * <p>
     * The rastered photo must have the following properties:
     * <ul>
     * <li>Has dimensions of at least w by h, where w and h are the user viewport width
     * and height.</li>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * <li>If a current route exists, lines of width ROUTE_STROKE_WIDTH_PX and of color
     * ROUTE_STROKE_COLOR are drawn between all nodes on the route in the rastered photo.
     * </li>
     * </ul>
     * Additional image about the raster is returned and is to be included in the Json response.
     * </p>
     *
     * @param params Map of the HTTP GET request's query parameters - the query bounding box and
     *               the user viewport width and height.
     * @param os     An OutputStream that the resulting png image should be written to.
     * @return A map of parameters for the Json response as specified:
     * "raster_ul_lon" -> Double, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Double, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Double, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Double, the bounding lower right latitude of the rastered image <br>
     * "raster_width"  -> Double, the width of the rastered image <br>
     * "raster_height" -> Double, the height of the rastered image <br>
     * "depth"         -> Double, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image string. <br>
     * "query_success" -> Boolean, whether an image was successfully rastered. <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public static Map<String, Object> getMapRaster(Map<String, Double> params, OutputStream os) {
        HashMap<String, Object> rasteredImageParams = new HashMap<>();


        ArrayList<QuadTree.Node> listOfFiles = getListOfFiles(params, quadTree);
//        ArrayList<String> listOfFilesStrings = new ArrayList<>();
//        for (QuadTree.Node n: listOfFiles) {
//            listOfFilesStrings.add(quadTree.getFileName(n));
//        }
//        System.out.println(listOfFilesStrings);

        Collections.sort(listOfFiles);
//        ArrayList<String> sortedStrings = new ArrayList<>();
//        for (QuadTree.Node n : listOfFiles) {
//            sortedStrings.add(quadTree.getFileName(n));
//        }
//        System.out.println(sortedStrings);
        QuadTree.Node firstTile = listOfFiles.get(0);
        QuadTree.Node lastTile = listOfFiles.get(listOfFiles.size() - 1);
        rasteredImageParams.put("raster_ul_lon", quadTree.getUllon(firstTile));
        rasteredImageParams.put("raster_ul_lat", quadTree.getUllat(firstTile));

        rasteredImageParams.put("raster_lr_lon", quadTree.getLrlon(lastTile));
        rasteredImageParams.put("raster_lr_lat", quadTree.getLrlat(lastTile));

        int depth = quadTree.getDepth(firstTile);

        rasteredImageParams.put("depth", depth);

        double dpp = quadTree.calcDppNode(firstTile);
        double imageWidth = ((quadTree.getLrlon(lastTile) - quadTree.getUllon(firstTile))) / dpp;


        int width = (int) imageWidth / TILE_SIZE;
        int height = listOfFiles.size() / width;

        double imageHeight = TILE_SIZE * height;


        int imageWidthInt = (int) imageWidth;
        int imageHeightInt = (int) imageHeight;
        rasteredImageParams.put("raster_width", imageWidthInt);
        rasteredImageParams.put("raster_height", imageHeightInt);

        BufferedImage result = new BufferedImage(
                imageWidthInt, imageHeightInt,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = result.getGraphics();

        try {
            int x = 0;
            int y = 0;
            for (QuadTree.Node fileName : listOfFiles) {
                BufferedImage im =
                        ImageIO.read((convertStringToFile(quadTree.getFileName(fileName))));
                graphics.drawImage(im, x, y, null);
                x += TILE_SIZE;
                if (x >= result.getWidth()) {
                    x = 0;
                    y += im.getHeight();
                }

            }

            createRoute(result, shortestPathNodes, rasteredImageParams);



            ImageIO.write(result, "png", os);

        } catch (IOException e) {
            e.printStackTrace();
        }

        rasteredImageParams.put("query_success", true);


        return rasteredImageParams;
    }

    private static void createRoute(BufferedImage im, ArrayList<GraphNode> path,
                                    HashMap<String, Object> output) {
//        System.out.println("working?");
//        System.out.println(path.size());
        if (path.isEmpty()) {
            return;
        }

        testNumber++;
        Graphics graphics = im.getGraphics();
        Stroke s = new BasicStroke(MapServer.ROUTE_STROKE_WIDTH_PX,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        ((Graphics2D) graphics).setStroke(s);
        graphics.setColor(ROUTE_STROKE_COLOR);

        double ullonImage = (double) output.get("raster_ul_lon");
        double ullatImage = (double) output.get("raster_ul_lat");
        double lrlonImage = (double) output.get("raster_lr_lon");
        double lrlatImage = (double) output.get("raster_lr_lat");
        int imageWidth = (int) output.get("raster_width");
        int imageHeight = (int) output.get("raster_height");

        for (int i = 0; i < shortestPathNodes.size() - 1; i++) {
            GraphNode connect1 = shortestPathNodes.get(i);
            GraphNode connect2 = shortestPathNodes.get(i + 1);

            int xCoord1 = (int) calcXPixel(ullonImage, lrlonImage,
                    connect1.getLongitude(), imageWidth);
            int yCoord1 = (int) calcYPixel(ullatImage, lrlatImage,
                    connect1.getLatitude(), imageHeight);
            int xCoord2 = (int) calcXPixel(ullonImage, lrlonImage,
                    connect2.getLongitude(), imageWidth);
            int yCoord2 = (int) calcYPixel(ullatImage, lrlatImage,
                    connect2.getLatitude(), imageHeight);
            System.out.println("Testnumber: " + testNumber);
            System.out.println("Node1ID: " + connect1.getId());
            System.out.println("Node2ID: " + connect2.getId());

            System.out.println("x1: " + xCoord1);
            System.out.println("y1: " + yCoord1);
            System.out.println("x2: " + xCoord2);
            System.out.println("y2: " + yCoord2);

            System.out.println("----------------------");

            graphics.drawLine(xCoord1, yCoord1, xCoord2, yCoord2);
//            System.out.println("hello");

//            graphics.drawRect(0, 0, 4, 5);



        }

    }

    private static double calcXPixel(double imageUllon, double imageLron,
                                     double currentNodeLon, double imageWidth) {
        double ratio = (currentNodeLon - imageUllon) / (imageLron - imageUllon);
        return ratio * imageWidth;

    }

    private static double calcYPixel(double imageUllat, double imageLrlat,
                                     double currentNodeLat, int imageHeight) {
        double ratio = (imageUllat - currentNodeLat) / (imageUllat - imageLrlat);
        return ratio * imageHeight;
    }

    private static File convertStringToFile(String fileName) {

        String filename = "img/" + fileName + ".png";
        File f = new File(filename);
        return f;

    }

    public static double getDpp(double ullon, double lrlon, double width) {
        return (lrlon - ullon) / width;
    }

    public static ArrayList getListOfFiles(Map<String, Double> params, QuadTree q) {
        // TODO Test this algorithm

        ArrayList<QuadTree.Node> fileNodes = new ArrayList<>();
        ArrayDeque<QuadTree.Node> intersected = new ArrayDeque<>();
        double dpp = getDpp(params.get("ullon"), params.get("lrlon"), params.get("w"));

        QuadTree.Node pointer = q.getRoot();
        if (q.satisfiesDepth(pointer, dpp)) {
            fileNodes.add(pointer);
        }
        intersected.add(pointer);
        ArrayList<QuadTree.Node> children = q.getChildren(intersected.removeFirst());
        int depth = 1;
        while (depth <= MAX_DEPTH) {
            for (QuadTree.Node child : children) {
                if (q.hasIntersection(child, params.get("ullon"), params.get("ullat"),
                        params.get("lrlon"), params.get("lrlat"))) {
                    if (q.satisfiesDepth(child, dpp) || depth == MAX_DEPTH) {
                        fileNodes.add(child);
                    } else {
                        intersected.add(child);
                    }
                }
            }

            if (intersected.isEmpty()) {
                break;
            }
            depth = q.getDepth(intersected.getFirst()) + 1;
            children = q.getChildren(intersected.removeFirst());


        }


        return fileNodes;
    }

    /**
     * Searches for the shortest route satisfying the input request parameters, sets it to be the
     * current route, and returns a <code>LinkedList</code> of the route's node ids for testing
     * purposes. <br>
     * The route should start from the closest node to the start point and end at the closest node
     * to the endpoint. Distance is defined as the euclidean between two points (lon1, lat1) and
     * (lon2, lat2).
     *
     * @param params from the API call described in REQUIRED_ROUTE_REQUEST_PARAMS
     * @return A LinkedList of node ids from the start of the route to the end.
     */
    public static LinkedList<Long> findAndSetRoute(Map<String, Double> params) {
        double startLat = params.get("start_lat");
        double startLon = params.get("start_lon");
        double endLat = params.get("end_lat");
        double endLon = params.get("end_lon");

        GraphNode startNode = GraphNode.getClosestGraphNode(g.getNodes(), startLat, startLon);
        GraphNode endNode = GraphNode.getClosestGraphNode(g.getNodes(), endLat, endLon);

        if (startNode.equals(endNode)) {
            return shortestPath;
        }

        PriorityQueue<AStar> minPQ = new PriorityQueue<>();

        double p = calculateHeuristic(startNode, endNode);

        AStar currentAstarNode = new AStar(startNode, endNode, null, 0.0, p);
        minPQ.add(currentAstarNode);

        GraphNode currentNode = minPQ.peek().getCurrentNode();


        while (!currentNode.equals(endNode)) {
            currentAstarNode = minPQ.poll();
            currentNode = currentAstarNode.getCurrentNode();
//            if (currentNode.equals(endNode)) {
//                break;
//            }
            currentNode.setMarkedToTrue();
            HashSet<GraphNode> connectedNodes = currentNode.getConnectedNodes();


            for (GraphNode neighbor : connectedNodes) {
                if (!neighbor.isMarked()) {
                    double calcG = calculateG(neighbor, currentAstarNode);
                    double h = calculateHeuristic(neighbor, endNode);
                    double priority = calcG + h;
                    AStar node = new AStar(neighbor, endNode, currentAstarNode, calcG, priority);
                    minPQ.add(node);
                }
            }


        }
        for (GraphNode node : g.getNodes().values()) {
            node.setMarkedToFalse();
        }

        clearRoute();
        shortestPathNodes = new ArrayList<>();

//        HashSet<Long> solution = new HashSet<>();

        while (currentAstarNode != null) {
            shortestPath.add(0, currentAstarNode.getCurrentNode().getId());
            shortestPathNodes.add(0, currentAstarNode.getCurrentNode());
            currentAstarNode = currentAstarNode.getParent();
        }

//        System.out.println(shortestPath);


        return shortestPath;


    }

    public static double calculateHeuristic(GraphNode currentNode, GraphNode endNode) {
        return GraphNode.calcDistance(currentNode, endNode);
    }

    public static double calculateG(GraphNode currentNode, AStar parentNode) {
        double calcG = parentNode.getG()
                + GraphNode.calcDistance(parentNode.getCurrentNode(), currentNode);
        return calcG;
    }

    public static double calculatePriority(GraphNode currentNode,
                                           GraphNode parentNode, GraphNode endNode) {
        double h = calculateHeuristic(currentNode, endNode);
        double calcG = parentNode.getG()
                + GraphNode.calcDistance(parentNode, currentNode);
        currentNode.setG(calcG);
        return calcG + h;
    }

    /**
     * Clear the current found route, if it exists.
     */
    public static void clearRoute() {
        shortestPath.clear();
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     *
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public static List<String> getLocationsByPrefix(String prefix) {
        return g.getSearchResults(prefix);

    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     *
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public static List<Map<String, Object>> getLocations(String locationName) {
        return new LinkedList<>();
    }
}
