/**
 * Raunak Bhojwani and Dami Apoeso
 * Monday 2nd March 2015
 * LAB5 - Kevin Bacon Game
 */

import net.datastructures.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;


public class BaconGame<V, E> extends DirectedAdjListMap<V, E>{
	private HashMap<Integer, String> actors; //actorID, actor name
	private HashMap<Integer, String> movies; //movieID, movie name
	private HashMap<Integer, ArrayList<Integer>> movieActor; //ActorID, MovieID
	private AdjacencyListGraphMap<String, String> baconGraph; //
	//private DirectedAdjListMap<String, String> directedBaconGraph;
	private HashMap<String, Integer> currentGameNumbers;
	private int gameNumber;
	
	public BaconGame() {
		actors = new HashMap<Integer, String>();
		movies = new HashMap<Integer, String>();
		movieActor = new HashMap<Integer, ArrayList<Integer>>();
		baconGraph = new AdjacencyListGraphMap<String, String>();
		//directedBaconGraph = new DirectedAdjListMap<String, String>();
		currentGameNumbers = new HashMap<String, Integer>();
		gameNumber = new Integer(0);
		
	}
	public void addActors(String file) throws FileNotFoundException {
		Scanner actorFile = new Scanner(new File(file + ".txt"));
		while (actorFile.hasNextLine()) {
			//split each line into two and put into an array
			String current = actorFile.nextLine();
			String[] actor = current.split("\\|");
			//the first index in the array is the ID
			String strActorID = actor[0];
			int actorID = Integer.parseInt(strActorID);
			//second index is the name
			String actorName = actor[1];
			//put both ID and actor into the map
			actors.put(actorID, actorName);	
		}
		actorFile.close();
	}
	public void addMovies(String file) throws FileNotFoundException {
		Scanner movieFile = new Scanner(new File(file + ".txt"));
		while (movieFile.hasNextLine()) {
			//split each line into two and put into an array
			String current = movieFile.nextLine();
			String[] movie = current.split("\\|");
			//the first index in the array is the ID
			String strMovieID = movie[0];
			int movieID = Integer.parseInt(strMovieID);
			//second index is the name
			String movieName = movie[1];
			//put both ID and actor into the map
			movies.put(movieID, movieName);
			
			movieActor.put(movieID, new ArrayList<Integer>());
		}
		movieFile.close();
	}
	
	public void addActorMovie(String file) throws FileNotFoundException {
		Scanner actMovFile = new Scanner(new File(file + ".txt"));
		while (actMovFile.hasNextLine()) {
			String current = actMovFile.nextLine();
			String[] actMov = current.split("\\|");
			
			String strMovieID = actMov[0];
			int movieID = Integer.parseInt(strMovieID);
			
			String strActorID = actMov[1];
			int actorID = Integer.parseInt(strActorID);
			
			movieActor.get(movieID).add(actorID);
		}
		actMovFile.close();
	}
	
	public void createBaconGraph() {
		for (int actor: actors.keySet()) {
			baconGraph.insertVertex(actors.get(actor));
		}
		
		for (int movieID: movieActor.keySet()) {
			for (int actorID: movieActor.get(movieID)) {
				for (int nextActorID: movieActor.get(movieID)) {
					
					String firstActor = actors.get(actorID);
					String secondActor = actors.get(nextActorID);
					
					if (!baconGraph.areAdjacent(firstActor, secondActor) && nextActorID != actorID) {
						baconGraph.insertEdge(firstActor, secondActor, movies.get(movieID));
					}
				}
			}
		}
	}
	
	public DirectedAdjListMap<String, String> BFS() {
		//Create new queue, and new directed graph
		LinkedList<Vertex<String>> fringe = new LinkedList<Vertex<String>>();
		DirectedAdjListMap<String, String> directedBaconGraph = new DirectedAdjListMap<String, String>();
		
		//initialize hashmap of currentGame numbers so every bacon number is initially set to -1
		int baconNumber = 0;
		for (int actorID : actors.keySet()) {
			currentGameNumbers.put(actors.get(actorID), 0);
		}

		//insert root into an empty queue Q and into a new directed graph T
		fringe.add(baconGraph.getVertex("Kevin Bacon"));
		directedBaconGraph.insertVertex("Kevin Bacon");
		
		//until the queue is empty
		while (!fringe.isEmpty()) {
			
			//dequeue Q to get next vertex v to process
			Vertex<String> current = fringe.poll();
			
			//for each edge e that is incident to v in G
			for (Edge<String> edge: baconGraph.incidentEdges(current)) {
				
				//let v' be the other end of the edge
				Vertex<String> opposite = baconGraph.opposite(current, edge);
				
				//if v' is not in T
				if (!directedBaconGraph.vertexInGraph(opposite.element())) {
					
					//add v' to T and add an edge with the same label as e from v' to v in T
					directedBaconGraph.insertVertex(opposite.element());
					directedBaconGraph.insertDirectedEdge(opposite.element(), current.element(), edge.element());
					
					//set the bacon number of the next vertex to that of the current vertex + 1
					baconNumber = currentGameNumbers.get(current.element()) + 1;
					currentGameNumbers.put(opposite.element(), baconNumber);
					
					//enqueue v' in Q
					fringe.add(opposite);
				}
			}
		}
		return directedBaconGraph;		
	}
	
	public void traverse(String actor, DirectedAdjListMap<String, String> graph) {
		LinkedList<String> vPath = new LinkedList<String>();
		ArrayList<String> ePath = new ArrayList<String>();
		if (!graph.vertexInGraph(actor) && currentGameNumbers.containsKey(actor)) {
			System.out.println(actor + "'s number is infinity");
			return;
		}
		else if (!graph.vertexInGraph(actor)) {
			System.out.println(actor + " is not in database");
			return;
		}
		Vertex<String> current = graph.getVertex(actor);
		while (graph.outDegree(current) != 0) {
			for (Edge<String> e: graph.incidentEdgesOut(current)) {
				ePath.add(e.toString());
				vPath.add(current.toString());
				current = graph.opposite(current, e);
			}
		}
		vPath.add(current.element());
		
//		System.out.println(vPath);
		System.out.println("\n");
		System.out.println(actor + "'s bacon number is " + currentGameNumbers.get(actor) + ".");
		for (int i=0; i < vPath.size() -1; i++) {
			System.out.println(vPath.get(i) + " appeared in " + ePath.get(i) + " with " + vPath.get(i+1) + ".");
		}
	}
	

	public static void main(String [] args) {
		BaconGame bacon = new BaconGame();
		try {
//			bacon.addActors("actorsTest");
			bacon.addActors("actors");
//			bacon.addMovies("moviesTest");
			bacon.addMovies("movies");
//			bacon.addActorMovie("movie-actorsTest");
			bacon.addActorMovie("movie-actors");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		bacon.createBaconGraph();
		System.out.println("\n");
//		System.out.println(bacon.baconGraph.toString());
//		System.out.println("\n");
//		System.out.println(bacon.BFS().toString());
		
		String actor;  // an actor's name
	    Scanner input = new Scanner(System.in);
	    char command; // a command
	    
		
		do {
			System.out.print("Press 'c' to continue, or press 'q' to quit: ");
            command = input.nextLine().charAt(0);
            
            switch (command) {
            	case 'q': // Quit
            		System.out.println("Bye!");
            		break;
            		
            	case 'c': // Continue
            		System.out.print("Please enter any actor, or press 'q' to quit: ");
        		    actor = input.nextLine();
        		    
        			bacon.traverse(actor, bacon.BFS());
        			System.out.println('\n');
                    break;
                    
            	default:
                    System.out.println("Huh?");
            }
        } while (command != 'q');
	}
	
}



