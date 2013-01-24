import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class GADDI {
	public static void main(String[] args) throws FileNotFoundException {
	    Graph pdbGraph = new Graph("db");
	    GraphStatistic dbGraphStatistic = new GraphStatistic(pdbGraph, 2);
	    Graph qGraph = new Graph("query");
	    GraphStatistic qGraphStatistic = new GraphStatistic(qGraph, 2);
	    GADDI gaddi = new GADDI(dbGraphStatistic, qGraphStatistic, 3);
	}
	
	int UNLMTINT = 0x3fffffff; 
	int NOEDGE = 121234;	    

    HashSet<HashMap<Integer, Integer>> mMatchSet;
    HashMap<Integer, Integer> mCurMatch;
    
    HashMap<Integer, HashSet<Integer>> mDelMap;
    HashSet<Integer> mDelSet;
    
    HashSet<Integer> mCandSet;
    ArrayList<HashSet<Integer>> mTrueList;
    ArrayList<HashSet<Integer>> mFalseList;
    
    GraphStatistic mpdbSt;
    GraphStatistic mpqSt;
    
    int[] dfsQgraphNodes;
    int L; 

	public GADDI(GraphStatistic pdbGraphSt, GraphStatistic pqGraphSt, int _L) {
	    mpdbSt = pdbGraphSt;
	    mpqSt = pqGraphSt;
	    
	    mTrueList =  new ArrayList<HashSet<Integer>>(mpdbSt.SizeofNode);
	    mFalseList = new ArrayList<HashSet<Integer>>(mpdbSt.SizeofNode);

	    for ( int i = 0; i < mpdbSt.SizeofNode; i++ ) {
	        mTrueList.add(new HashSet<Integer>());
	        mFalseList.add(new HashSet<Integer>());
	    }
	    
	    mMatchSet = new HashSet<HashMap<Integer, Integer>>();
	    mCurMatch = new HashMap<Integer, Integer>();
	    
	    mDelMap = new HashMap<Integer, HashSet<Integer>>();
	    mDelSet = new HashSet<Integer>();
	    
	    dfsQgraphNodes = new int[mpqSt.SizeofNode];
	    L = _L;
	    
	    mCandSet = new HashSet<Integer>();
	    
	    //dfsSearchQgraph();
	    for (int i = 0; i < mpqSt.SizeofNode; i++)
	    	dfsQgraphNodes[i] = i;
	    
	    for ( int i = 0; i < mpdbSt.SizeofNode; i++ ) {
	        mCandSet.add(i);
	    }
	    
	    dynamicMatching( 0 );
	    	    
	    Iterator<HashMap<Integer, Integer>>  it = mMatchSet.iterator();
	    
	    HashMap<Integer, Integer> tempMap;
	    int i = 1;
	    while (it.hasNext()) {
	        System.out.println("Match " + i++);
	        tempMap = it.next();
	        for (Integer first : tempMap.keySet()) {
	            System.out.println("qIdx: " + first + " dbIdx: " + tempMap.get(first));
	        }
	    }
	}

	void dfsSearchQgraph() {
	    int nodeIdx = 1;
	    int currentNode = 0;
	    boolean isNewNodeAdded = false;
	    dfsQgraphNodes[0] = 0;
	    
	    int[][] qGraphMatrix = new int[mpqSt.SizeofNode][];
	    for ( int i = 0; i < mpqSt.SizeofNode; i++ ) {
	        qGraphMatrix[i] = new int [mpqSt.SizeofNode];
	        for ( int j = 0; j < mpqSt.SizeofNode; j++ ) {
	            qGraphMatrix[i][j] = mpqSt.GraphMatrix[i][j];
	        }
	    }
	    
	    HashSet<Integer> alreadySearched = new HashSet<Integer>();
	    HashSet<Integer> toBeSearched = new HashSet<Integer>();
	    LinkedList<Integer> currentPath = new LinkedList<Integer>();
	    
	    for ( int i = 1; i < mpqSt.SizeofNode; i++ ) {
	        toBeSearched.add(i);
	    }
	    
	    alreadySearched.add(0);    
	    currentPath.add(0);
	    
	    while ( false == toBeSearched.isEmpty() ) {
	        if ( true == currentPath.isEmpty() ) {
	            Iterator<Integer> it = toBeSearched.iterator();
	            Integer t = it.next();
	            it.remove();
	            alreadySearched.add(t);
	            currentNode = t;
	            currentPath.add(currentNode);
	        } else {
	            currentNode = currentPath.getLast();
	        }
	        
	        isNewNodeAdded = false;
	        
	        for ( int i = 0; i < mpqSt.SizeofNode; i++ ) {
	            if ( NOEDGE != qGraphMatrix[currentNode][i] ) {
	                qGraphMatrix[currentNode][i] = NOEDGE;
	                if ( toBeSearched.contains(i)) {
	                    toBeSearched.remove(i);
	                    alreadySearched.add(i);
	                    currentPath.add(i);
	                    isNewNodeAdded = true;
	                    dfsQgraphNodes[nodeIdx++]=i;
	                    break;
	                }
	            }
	        }
	        
	        if ( false == isNewNodeAdded ) {
	            currentPath.remove(currentPath.getLast());
	        }
	    }
	}

	void shrinkCand( int qIdx, int dbIdx) {
	    HashMap<String, List<Integer>> qNDSmap = new HashMap<String ,List<Integer>>();
	    String label = "";
	    int cnt = 0;
	    boolean isObserv2Satisfied = false;

	    mDelSet.add(dbIdx);
	    mCandSet.remove(dbIdx);
	        
	    for ( int i = 0; i < mpqSt.SizeofNode; i++ ) {
	        if ( i != qIdx ) {
	            label = mpqSt.NodeLabel[i];
	            cnt = mpqSt.ndsMatrixTri[qIdx][i];
	            if (!qNDSmap.containsKey(label)) {
	            	qNDSmap.put(label, new LinkedList<Integer>());	            	
	            }
            	qNDSmap.get(label).add(cnt);
	        }
	    }
	    
	    for ( int i = 0; i < mpdbSt.SizeofNode; i++ ) {
	        if ( L >= mpdbSt.GraphShortestMatrix[dbIdx][i] ) {
	            isObserv2Satisfied = false;
	            label = mpdbSt.NodeLabel[i];
	            cnt  = mpdbSt.ndsMatrixTri[dbIdx][i];
	            
	            if (qNDSmap.containsKey(label)) {
	            	for (Integer j : qNDSmap.get(label)) {
	            		if (j <= cnt ) {
	                    	isObserv2Satisfied = true;
	                	}
	            	}
	            }
	            
	            if ( false == isObserv2Satisfied ) {
	                mCandSet.remove(i);
	                mDelSet.add(i);
	            }
	        }
	    }
	}

	void addMatch( int qIdx, int dbIdx ) {
	    mCurMatch.put(qIdx, dbIdx);
	}

	boolean isInTrueList( int qIdx, int dbIdx) {
	    if (mTrueList.get(dbIdx).contains(qIdx)) {
	        return true;
	    }
	    return false;
	}

	boolean isInFalseList(int qIdx, int dbIdx) {
	    if (mFalseList.get(dbIdx).contains(qIdx) ) {
	        return true;
	    }
	    return false;
	}

	boolean isSameLabelAdjacency (int qIdx, int dbIdx) {
	    String qLabel, dbLabel;
	    qLabel = mpqSt.NodeLabel[qIdx];
	    dbLabel = mpdbSt.NodeLabel[dbIdx];
	    if (!qLabel.equals(dbLabel)) {
	        return false;
	    }
	    
	    for (Integer first : mCurMatch.keySet()) {
	        if ( NOEDGE != mpqSt.GraphMatrix[first][qIdx] && 
	             NOEDGE == mpdbSt.GraphMatrix[mCurMatch.get(first)][dbIdx] ) {
	            return false;
	        }
	    }
	    
	    for (String s : mpqSt.neighborCntMatrix.get(qIdx).keySet()) {
	        if (!mpdbSt.neighborCntMatrix.get(dbIdx).containsKey(s) || mpqSt.neighborCntMatrix.get(qIdx).get(s) > 
	              mpdbSt.neighborCntMatrix.get(dbIdx).get(s) ) {
	            return false;
	        }
	    }
	    return true;
	}

	void addToTrueList ( int qIdx, int dbIdx ) {
	    mTrueList.get(dbIdx).add(qIdx);
	}

	void addToFalseList ( int qIdx, int dbIdx ) {
	    mFalseList.get(dbIdx).add(qIdx);
	}

	boolean isInequalitySatisfied (int qIdx, int dbIdx) {
	    int cnt = -1;
	    String label = "";
	    HashMap<String, List<Integer>> qMulMap = new HashMap<String, List<Integer>>();
	    HashMap<String, List<Integer>> dbMulMap = new HashMap<String, List<Integer>>();
	    
	    boolean flag = false;
	    
	    for ( int i = 0; i < mpqSt.SizeofNode; i++ ) {
	        if ( NOEDGE != mpqSt.GraphMatrix[qIdx][i] ) {
	            cnt = mpqSt.ndsMatrixTri[qIdx][i];
	            label = mpqSt.NodeLabel[i];
	            if (!qMulMap.containsKey(label)) {
	            	qMulMap.put(label, new LinkedList<Integer>());
	                qMulMap.get(label).add(cnt);	            	
	            }
	            else {
	            	int pos = 0;
	            	List<Integer> c = qMulMap.get(label);
	                while (pos < c.size() && c.get(pos) < cnt) {
	                    pos++;
	                }
	                if (pos == c.size())
	                	c.add(cnt);
	                else
	                	c.add(pos, cnt);
	            }
	        }
	    }
	    
	    for ( int i = 0; i < mpdbSt.SizeofNode; i++ ) {
	        if ( NOEDGE != mpdbSt.GraphMatrix[dbIdx][i] ) {
	            cnt = mpdbSt.ndsMatrixTri[dbIdx][i];
	            label = mpdbSt.NodeLabel[i];
	            if (!dbMulMap.containsKey(label))
	            	dbMulMap.put(label, new LinkedList<Integer>());
	            dbMulMap.get(label).add(cnt);
	        }
	    }
	    
	    for (String s : qMulMap.keySet()) {
	        List<Integer> cnts = qMulMap.get(s);
        	
        	if (!dbMulMap.containsKey(s)) {
        		return false;
        	}
	        
	        for (Integer i : cnts) {
	        	
	        	flag = false;
	        	
	        	for (Integer j : dbMulMap.get(s))
	        		if (j >= i) {
	        			flag = true;
	        			break;
	        		}
	        	
		        if ( false == flag ) {
		            return false;
		        }	        	
	        }
	    }
	    return true;
	}

	void dynamicMatching(int i) {
	    int lastMatchDb = -1;

	    if ( i == mpqSt.SizeofNode ) {
	        mMatchSet.add(mCurMatch);
	        
	        int lastMatchQ = dfsQgraphNodes[i - 1];
	        
	        mDelSet.clear();
	        
	        mDelSet = mDelMap.get(lastMatchQ);
	        
	        mCandSet.addAll(mDelSet);
	        
	        mDelSet.clear();
	        mDelMap.remove(lastMatchQ);
	        mCurMatch.remove(lastMatchQ);
	        return;
	    }

	    int qIdx = dfsQgraphNodes[i];
	    Iterator<Integer> it = mCandSet.iterator();
	    
	    while ( it.hasNext() ) {
	    	Integer value = it.next();
	        if ( true == isSameLabelAdjacency ( qIdx, value ) ) {
	            if ( true == isInFalseList ( qIdx, value ) ) {
	                continue;
	            } 
	            
	            if ( true == isInTrueList ( qIdx, value ) ) {
	                addMatch(qIdx, value );
	                lastMatchDb = value;
	                shrinkCand(qIdx, value);
	                
	                HashSet<Integer> tempCopy = new HashSet<Integer>();
	                for (Integer j : mDelSet)
	                	tempCopy.add(j);
	                
	                mDelMap.put(qIdx, tempCopy);
	                mDelSet.clear();
	                
	                dynamicMatching(i + 1);
	                
	                continue;
	            }
	            
	            if ( true == isInequalitySatisfied(qIdx, value) ) {
	                addMatch(qIdx, value);
	                lastMatchDb = value;
	            
	                addToTrueList(qIdx, value);
	                
	                shrinkCand(qIdx, value);
	                
	                HashSet<Integer> tempCopy = new HashSet<Integer>();
	                for (Integer j : mDelSet)
	                	tempCopy.add(j);
	                
	                mDelMap.put(qIdx, tempCopy);
	                mDelSet.clear();
	                
	                dynamicMatching(i + 1);
	                
	                continue;
	            } else {	            
	                addToFalseList(qIdx, value);
	            }
	        } 
	    }
	    
	    if ( 0 != i ) {
	        int lastMatchQ = dfsQgraphNodes[i - 1];
	        
	        mDelSet.clear();
	        
	        mDelSet = mDelMap.get(lastMatchQ);

            mCandSet.addAll(mDelSet);
	        
	        mDelSet.clear();
	        
	        mDelMap.remove(lastMatchQ);
	        
	        mCurMatch.remove(lastMatchQ);
	    }
	}
}
