package graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.impl.path.AllSimplePaths;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;


public class GraphDatabase {
	private static final String FS = System.getProperty("file.separator");
	private static final String USER_ID = "user_id";
	private static GraphDatabaseService graphDatabase = null;

	public static void startGraphDatabase() {
		String home = System.getenv("HOME");
    	String graphDbPath = home + FS + ".tcommerce" + FS + "graphdb" + FS;
		graphDatabase = (EmbeddedGraphDatabase) new GraphDatabaseFactory()
				.newEmbeddedDatabase(graphDbPath);
		registerShutdownHook(graphDatabase);
	}

	public static void registerShutdownHook(
			final GraphDatabaseService graphDatabase) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDatabase.shutdown();
			}
		});
	}

	public static void shutDown() {
		if (graphDatabase != null) {
			graphDatabase.shutdown();
		}
	}

	public static void addUserAndFriends(long announcer, long[] friends) {
		Transaction tx = graphDatabase.beginTx();
		try {
			Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
			Node node = graphDatabase.createNode();
			Node existing = usersIndex.putIfAbsent(node, USER_ID, announcer);
			if (existing != null) {
				node = existing;
			}

			for (long friend : friends) {
				Node friendNode = graphDatabase.createNode();
				existing = usersIndex.putIfAbsent(friendNode, USER_ID, friend);
				if (existing != null) {
					friendNode = existing;
				}
				node.createRelationshipTo(friendNode, RelTypes.KNOWS);
			}
			tx.success();
		} finally {
			tx.finish();
		}

	}

	public static int findConnectionBetweet(long from, long to) {
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node fromNode = usersIndex.get(USER_ID, from).getSingle();
		Node toNode = usersIndex.get(USER_ID, to).getSingle();
		if (fromNode != null && toNode != null) {
			 Expander expander = Traversal.expanderForTypes(
					RelTypes.KNOWS, Direction.OUTGOING);
			PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(
					expander, 5);
			Path path = pathFinder.findSinglePath(fromNode, toNode);
			return path == null ? -1 :  path.length();
		} else {
			return -1;
		}

	}

	private static enum RelTypes implements RelationshipType {
		KNOWS
	}

	public static void main(String[] args) {
		startGraphDatabase();
		findConnectionBetweet(273572038, 273572038);
		shutDown();
	}
}