import java.util.ArrayList;
import java.util.HashMap;

class GraphStatistic {
	int UNLMTINT = 0x3fffffff; 
	int NOEDGE = 121234;
	int GROUP_CNT = 30;
	
    Graph mpGraph;
    int[][] GraphShortestMatrix;
    
    int[][] ndsMatrixTri;
    int[][] ndsMatrixRect;
        
    int[][] GraphMatrix;
    
    int[] maxShortestDistances;
    
    int SizeofNode;
    
    int[] nodesDegree;

    int K;
    
    String[] NodeLabel;
    
    int longestShortestDistance;

    ArrayList<HashMap<String, Integer>> neighborCntMatrix;

    /**
     * Count the degree and label
     */
    void getDegreeNeighborLabelCnt() {
        for ( int i = 0; i < SizeofNode; i++ ) {
            for ( int j = 0; j < SizeofNode; j++ ) {
                if ( GraphMatrix[i][j] != NOEDGE ) {
                    nodesDegree[i]++;
                    if (!neighborCntMatrix.get(i).containsKey(NodeLabel[j]))
                    	neighborCntMatrix.get(i).put(NodeLabel[j], 1);
                    else {
                    	neighborCntMatrix.get(i).put(NodeLabel[j], neighborCntMatrix.get(i).get(NodeLabel[j]) + 1);
                    }                    	
                }
            }
        }
    }

    public GraphStatistic(Graph pGraph, int _K ) {
        K = _K;
        
        mpGraph = pGraph;
        
        longestShortestDistance = 0;
        
        SizeofNode = mpGraph.SizeofNode;
        
        NodeLabel = mpGraph.NodeLabel;
        
        GraphMatrix = mpGraph.GraphMatrix;

        GraphShortestMatrix = mpGraph.GraphShortestMatrix;

        ndsMatrixTri = new int[SizeofNode][];
        ndsMatrixRect = new int[SizeofNode][];
        
        nodesDegree = new int [SizeofNode];
        
        maxShortestDistances = new int [SizeofNode];
        
        neighborCntMatrix = new ArrayList<HashMap<String, Integer>>(SizeofNode);
        
        for ( int i = 0; i < SizeofNode; i++ ) {
        	
        	neighborCntMatrix.add(new HashMap<String, Integer>());
        	
        	maxShortestDistances[i] = 0;
            
            nodesDegree[i] = 0;
            
            ndsMatrixTri[i] = new int[SizeofNode];
            ndsMatrixRect[i] = new int[SizeofNode];
            
            for ( int j = 0; j < SizeofNode; j++ ) {
                ndsMatrixTri[i][j] = 0;
                ndsMatrixRect[i][j] = 0;
            }
        }
        
        shortestPathCalculate();
        ndsCalculate();
        getDegreeNeighborLabelCnt();
    }

    void shortestPathCalculate() {
        for ( int i = 0; i < SizeofNode; i++ ) {
            for ( int j = 0; j < SizeofNode; j++ ) {
                if ( UNLMTINT != GraphShortestMatrix[i][j]
                   && maxShortestDistances[i] < GraphShortestMatrix[i][j] ) {
                    maxShortestDistances[i] = GraphShortestMatrix[i][j];
                }
            }
            if ( longestShortestDistance < maxShortestDistances[i] ) {
                longestShortestDistance = maxShortestDistances[i];
            }
        }
    }
    
    void ndsCalculate() {
        int[] pNode = new int[2];
        for ( int i = 0; i < SizeofNode; i++ ) {
            for ( int j = 0; j < SizeofNode; j++ ) {
                if ( i == j ) {
                    ndsMatrixTri[i][j] = UNLMTINT;
                    ndsMatrixRect[i][j] = UNLMTINT;
                    continue;
                }                
                if ( 2*K > GraphShortestMatrix[i][j] ) {
                    pNode[0]=i;
                    pNode[1]=j;
                    SubGraphK qSubGraphK = new SubGraphK(mpGraph, pNode, 2, K, GraphShortestMatrix );
                    ndsMatrixTri[i][j] = qSubGraphK.getTriangleCnt();
                    ndsMatrixRect[i][j] = qSubGraphK.getRectangleCnt();
                }
            }
        }
    }
}









