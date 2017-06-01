package com.maxdemarzi;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.Schema;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Service {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private HashMap<Long, String> usernames = new HashMap<>();

    @GET
    @Path("/migrate")
    public String migrate(@Context GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Schema schema = db.schema();
            schema.constraintFor(Labels.User)
                    .assertPropertyIsUnique("username")
                    .create();
            tx.success();
        }
        // Wait for indexes to come online
        try (Transaction tx = db.beginTx()) {
            Schema schema = db.schema();
            schema.awaitIndexesOnline(1, TimeUnit.DAYS);
            tx.success();
        }
        return "Migrated!";
    }

    @GET
    @Path("/warmup")
    public String warmup(@Context GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            db.getAllNodes().iterator().stream().forEach(node -> {
                usernames.put(node.getId(), (String)node.getProperty("username"));});
        }
        return "Warmed Up!";
    }

    @GET
    @Path("/loop0/{id}")
    public Response loop0(@PathParam("id") Long id, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<Long> friends = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.getNodeById(id);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friends.add(rel.getEndNodeId());
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop0b/{id}")
    public Response loop0b(@PathParam("id") Long id, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<Long> friends = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.getNodeById(id);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friends.add(rel.getEndNodeId());
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop1/{username}")
    public Response loop1(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<Long> friends = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.findNode(Labels.User, "username", username);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friends.add(rel.getEndNodeId());
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop2/{username}")
    public Response loop2(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<Long> friends = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.findNode(Labels.User, "username", username);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friends.add(rel.getEndNode().getId());
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop3/{username}")
    public Response loop3(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<String> friends = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.findNode(Labels.User, "username", username);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friends.add((String)rel.getEndNode().getProperty("username"));
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop4/{username}")
    public Response loop4(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<String> friends = new HashSet<>();
        ArrayList<Relationship> friendRels = new ArrayList<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.findNode(Labels.User, "username", username);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friendRels.add(rel);
                }

                for (Relationship rel : friendRels) {
                    friends.add((String)rel.getEndNode().getProperty("username"));
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop5/{username}")
    public Response loop5(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<String> friends = new HashSet<>();
        ArrayList<Long> friendNodeIds = new ArrayList<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.findNode(Labels.User, "username", username);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friendNodeIds.add(rel.getEndNodeId());
                }

                Collections.sort(friendNodeIds);

                for (Long nodeId : friendNodeIds) {
                    friends.add((String)db.getNodeById(nodeId).getProperty("username"));
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop6/{username}")
    public Response loop6(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<String> friends = new HashSet<>();
        ArrayList<Node> friendNodes = new ArrayList<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.findNode(Labels.User, "username", username);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friendNodes.add(rel.getEndNode());
                }

                friendNodes.sort(Comparator.comparingLong(Node::getId));

                for (Node node : friendNodes) {
                    friends.add((String)node.getProperty("username"));
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop7/{username}")
    public Response loop7(@PathParam("username") String username, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<String> friends = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.findNode(Labels.User, "username", username);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friends.add(usernames.get(rel.getEndNodeId()));
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/loop8/{id}")
    public Response loop8(@PathParam("id") Long id, @Context GraphDatabaseService db) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Set<String> friends = new HashSet<>();

        try (Transaction tx = db.beginTx()) {
            final Node user = db.getNodeById(id);

            if (user != null) {
                for (Relationship rel : user.getRelationships(RelationshipTypes.FRIENDS, Direction.OUTGOING)) {
                    friends.add(usernames.get(rel.getEndNodeId()));
                }
            }
            tx.success();
        }
        results.add(new HashMap<String, Object>() {{ put("friends", friends);}});
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }
}
