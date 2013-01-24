import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Graph {
	int UNLMTINT = 0x3fffffff; 
	int NOEDGE = 121234;

    int SizeofNode;
    int SizeofEdges;
    int[][] GraphMatrix;    
    int[][] GraphShortestMatrix;
    boolean isRdFromFile;
    String indexFile;
    String[] NodeLabel;

    /**
     * Calculate the shortest distance pairwise
     * @throws FileNotFoundException
     */
	public void shortestPathCalculate() throws FileNotFoundException {
	    if ( false == isRdFromFile ) {	        
	    	int n = SizeofNode;	        
	        for ( int i = 0; i < n; i++ ) {
	            GraphShortestMatrix[i][i] = 0;
	        }	        
	        for ( int k = 0; k < n; k++ ) {
	            for ( int i = 0; i < n; i++ ) {
	                for ( int j = 0; j < n; j++ ) {
	                    if ( GraphShortestMatrix[i][j] > GraphShortestMatrix[i][k] + GraphShortestMatrix[k][j] ) {
	                        GraphShortestMatrix[i][j] = GraphShortestMatrix[i][k] + GraphShortestMatrix[k][j];
	                    }
	                }
	            }
	        }
	    } else {
	    	//Read index from file
	    	ReadIndexFromFile(indexFile);
	    }
	}

	/**
	 * Read the index from file
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public void ReadIndexFromFile(String fileName) throws FileNotFoundException {
		File input = new File(fileName);
		Scanner in = new Scanner(input);
	    for ( int i = 0; i < SizeofNode; i++ ) {
	        for ( int j = 0; j < SizeofNode; j++ ) {
	        	GraphShortestMatrix[i][j] = in.nextInt();
	        }	 
	    }
	    in.close();
	}

	/**
	 * Constructor
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public Graph(String fileName) throws FileNotFoundException {
	    File fin = new File(fileName);
	    Scanner in = new Scanner(fin);
    	    
	    isRdFromFile = false;
	    
	    SizeofNode = in.nextInt();
	    
	    NodeLabel = new String[SizeofNode];
	    GraphMatrix = new int[SizeofNode][SizeofNode];
	    GraphShortestMatrix = new int[SizeofNode][SizeofNode];
	    
	    for(int j = 0; j < SizeofNode; j++) {
	        int t = in.nextInt();
	        NodeLabel[t] = in.next();
	        
	        for(int k = 0; k < SizeofNode; k++){
	            GraphMatrix[j][k] = NOEDGE;
	            GraphShortestMatrix[j][k] = UNLMTINT;
	        }
	    }
	    
	    while (in.hasNext()) {
	        int source = in.nextInt();
	        int target = in.nextInt();
	        GraphMatrix[source][target] = 1;
	       //GraphMatrix[target][source] = 1;	        
	        GraphShortestMatrix[source][target] = GraphMatrix[source][target];
	       //GraphShortestMatrix[target][source] = GraphMatrix[target][source];
	        
	    }
	    
	    in.close();
	    
	    shortestPathCalculate();
	}

	/**
	 * Contructor with index file
	 * @param fileName
	 * @param indexFile
	 * @throws FileNotFoundException
	 */
	public Graph(String fileName, String indexFile) throws FileNotFoundException {

		isRdFromFile = true;
		this.indexFile = indexFile;

	    File fin = new File(fileName);
	    Scanner in = new Scanner(fin);
    	    
	    isRdFromFile = false;
	    
	    SizeofNode = in.nextInt();
	    SizeofEdges = 0;
	    
	    NodeLabel = new String[SizeofNode];
	    GraphMatrix = new int[SizeofNode][SizeofNode];
	    GraphShortestMatrix = new int[SizeofNode][SizeofNode];
	    
	    for(int j = 0; j < SizeofNode; j++) {
	        int t = in.nextInt();
	        NodeLabel[t] = in.next();
	        
	        for(int k = 0; k < SizeofNode; k++){
	            GraphMatrix[j][k] = NOEDGE;
	            GraphShortestMatrix[j][k] = UNLMTINT;
	        }
	    }
	    
	    while (in.hasNext()) {
	        int source = in.nextInt();
	        int target = in.nextInt();
	        GraphMatrix[source][target] = 1;
	        GraphShortestMatrix[source][target] = GraphMatrix[source][target];
	        SizeofEdges++;
	    }
	    
	    in.close();
	    
	    shortestPathCalculate();	    
	}
}