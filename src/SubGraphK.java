public class SubGraphK {

	int UNLMTINT = 0x3fffffff; 
	int NOEDGE = 121234;
	
	Graph mpGraph;
	int mCentralNodeCnt;
	int[] mpCentralNode;
	
	int[] mpIdxFlag;
	
	int mk;
    
	int[] mpIdxInSrcGraph;
    
	int[][] mppSubGraphMatrix;
    int[][] mShortestMatrix;
	
    int mNodeCnt;   //number of nodes in the intersection
	int mTriangleCnt;  //number of triangle in the intersection
	int mRectangleCnt; // number of rectangle in the intersection

	public SubGraphK(Graph pGraph, int[] pCentralNode, int nodeCnt, int k, int[][] shortestMatrix) {
	    mpGraph = pGraph;
	    mCentralNodeCnt = nodeCnt;
	    mk = k;
	    mpCentralNode = new int [nodeCnt];
	    mShortestMatrix = shortestMatrix;
	    
	    for ( int i = 0; i < nodeCnt; i++ ) {
	        mpCentralNode[i] = pCentralNode[i];
	    }
	    
	    setAndSubGraph();
	    computeTriangleCnt();
	    computeRectangleCnt();
	}

	int getTriangleCnt() {
	    return mTriangleCnt;
	}

	int getRectangleCnt() {
	    return mRectangleCnt;
	}

	void setAndSubGraph() {
	    int _i, _j;
	    mpIdxFlag = new int [mpGraph.SizeofNode];
	    mNodeCnt = 0;
	    
	    for ( int i = 0; i < mpGraph.SizeofNode; i++ ) {
	        if ( true == isInIntersection(i) ) {
	            mpIdxFlag[i] = 1;
	            mNodeCnt++;
	        }
	    }
	    
	    mpIdxInSrcGraph = new int [mNodeCnt];
	    mppSubGraphMatrix  = new int[mNodeCnt][];
	    
	    int idxCnt = 0;
	    for ( int i = 0; i < mpGraph.SizeofNode; i++ ) {
	        if ( mpIdxFlag[i] != 0 ) {
	            mpIdxInSrcGraph[idxCnt++] = i;
	        }
	    }

	    for ( int i = 0; i < mNodeCnt; i++ ) {
	        mppSubGraphMatrix[i] = new int[mNodeCnt];
	        for ( int j = 0; j < mNodeCnt; j++ ) {
	            _i = mpIdxInSrcGraph[i];
	            _j = mpIdxInSrcGraph[j];
	            mppSubGraphMatrix[i][j] = mpGraph.GraphMatrix[_i][_j];
	        }
	    }
	}

	void computeRectangleCnt() {
	    mRectangleCnt = 0;
	    if ( mNodeCnt < 4 ) {
	        return;
	    }
	    for ( int i = 0; i < mNodeCnt-3; i++ ){
	        for ( int j = i+1; j < mNodeCnt-2; j++ ) {
	            for ( int k = j+1; k < mNodeCnt-1; k++ ) {
	                for ( int l = k+1; l < mNodeCnt; l++ ) {
	                    mRectangleCnt += isRectangle(i,j,k,l);
	                }
	            }
	        }
	    }
	}

	void computeTriangleCnt() {
	    mTriangleCnt = 0;
	    if ( mNodeCnt < 3 ) {
	        return;
	    }
	    for ( int i = 0; i < mNodeCnt - 2; i++ ){
	        for ( int j = i+1; j < mNodeCnt - 1; j++ ) {
	            for ( int k = j+1; k < mNodeCnt; k++ ) {
	                mTriangleCnt += isTriangle(i,j,k);
	            }
	        }
	    }
	}
	
	int isTriangle(int idx1, int idx2, int idx3) {
	    if ( mppSubGraphMatrix[idx1][idx2] != NOEDGE &&
	         mppSubGraphMatrix[idx1][idx3] != NOEDGE &&
	         mppSubGraphMatrix[idx2][idx3] != NOEDGE ) {
	        return 1;
	    }
	    return 0;
	}

	int isRectangle(int idx1, int idx2, int idx3, int idx4) {
	    int ret = 0;
	    if ( mppSubGraphMatrix[idx1][idx2] != NOEDGE &&
	         mppSubGraphMatrix[idx2][idx3] != NOEDGE &&
	         mppSubGraphMatrix[idx3][idx4] != NOEDGE &&
	         mppSubGraphMatrix[idx4][idx1] != NOEDGE ) {
	        ret++;
	    }
	    if ( mppSubGraphMatrix[idx1][idx3] != NOEDGE &&
	         mppSubGraphMatrix[idx3][idx2] != NOEDGE &&
	         mppSubGraphMatrix[idx2][idx4] != NOEDGE &&
	         mppSubGraphMatrix[idx4][idx1] != NOEDGE ) {
	        ret++;
	    }
	    if ( mppSubGraphMatrix[idx1][idx2] != NOEDGE &&
	         mppSubGraphMatrix[idx2][idx4] != NOEDGE &&
	         mppSubGraphMatrix[idx4][idx3] != NOEDGE &&
	         mppSubGraphMatrix[idx3][idx1] != NOEDGE ) {
	        ret++;
	    }
	    return ret;
	}	

	boolean isInIntersection(int node) {
	    boolean ret = true;
	    int idx;
	    for ( int i = 0; i < mCentralNodeCnt; i++ ) {
	        idx = mpCentralNode[i];
	        if ( mShortestMatrix[idx][node] > mk ) {
	            ret = false;
	            break;
	        }
	    }
	    return ret;
	}
}








