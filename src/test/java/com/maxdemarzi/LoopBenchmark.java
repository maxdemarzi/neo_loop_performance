package com.maxdemarzi;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LoopBenchmark {

    private Service service;
    private GraphDatabaseService db;
    private static Random rand = new Random();

    @Param({"10000"})
    public int userCount;

    @Param({"100"})
    public int friendsCount;

    private HashMap<String, Long> nodes;

    @Setup
    public void prepare() throws IOException {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        service = new Service();
        service.migrate(db);
        populateDb(db);
        service.warmup(db);
    }

    @TearDown
    public void tearDown() {
        db.shutdown();
    }

    private void populateDb(GraphDatabaseService db) throws IOException {
        ArrayList<Node> users = new ArrayList<>();
        nodes = new HashMap<>();

        rand.setSeed(123456789);

        try (Transaction tx = db.beginTx()) {
            for (int i = 0; i < userCount; i++) {
                Node user = createNode(db, Labels.User, "username", "user" + String.valueOf(i));
                users.add(user);
                nodes.put("user" + String.valueOf(i), user.getId());
            }
            tx.success();
        }

        Transaction tx = db.beginTx();
        try  {
            for (int i = 0; i < userCount; i++){
                Node user =  users.get(i);

                for (int j = 0; j < friendsCount; j++) {
                    int randomItem = rand.nextInt(userCount);
                    user.createRelationshipTo(users.get(randomItem), RelationshipTypes.FRIENDS);
                }

                if(i % 1000 == 0){
                    tx.success();
                    tx.close();
                    tx = db.beginTx();
                }

            }
            tx.success();
        } finally {
            tx.close();
        }
    }
    private Node createNode(GraphDatabaseService db, Label label, String property, String value) {
        Node node = db.createNode(label);
        node.setProperty(property, value);
        return node;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void justLongsNodeId() throws IOException {
        service.loop0(10L, db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void justLongscachedUserId() throws IOException {
        service.loop0b(nodes.get("user10"), db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void justLongsbyEndNodeId() throws IOException {
        service.loop1("user10", db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void justLongsbyEndNodeGetId() throws IOException {
        service.loop2("user10", db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void iterateNormally() throws IOException {
        service.loop3("user10", db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void collectRels() throws IOException {
        service.loop4("user10", db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void sortNodeIds() throws IOException {
        service.loop5("user10", db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void sortNodes() throws IOException {
        service.loop6("user10", db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void cachedUsernames() throws IOException {
        service.loop7("user10", db);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 50)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void cacheEverything() throws IOException {
        service.loop8(nodes.get("user10"), db);
    }
}
