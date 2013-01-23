import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;


public class GADDI {
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
	    dfsQgraphNodes = new int[mpqSt.SizeofNode];
	    L = _L;
	    
	    dfsSearchQgraph();
	    
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
	    set < int > :: iterator it;
	    HashSet<Integer> qLabelSet;
	    multimap < int, int > qNDSmap;
	    multimap < int, int > :: iterator it1;
	    int label;
	    int cnt;
	    bool isObserv2Satisfied;
	    it = mCandSet.begin();
	    int longestDist = mpqSt.maxShortestDistances[qIdx];
	    mDelSet.insert(dbIdx);
	    mCandSet.erase(dbIdx);
	    while( it != mCandSet.end() ) {
	        if ( mpdbSt.GraphShortestMatrix[dbIdx][*it] > longestDist ) {
	            set < int > :: iterator _it = it;
	            it++;
	            mDelSet.insert(*_it);
	            mCandSet.erase(_it);
	        } else {
	            it++;
	        }
	    }
	    
	    for ( int i = 0; i < mpqSt.SizeofNode; i++ ) {
	        if ( i != qIdx ) {
	            label = mpqSt.NodeLabel[i];
	            cnt = mpqSt.ndsMatrixTri[qIdx][i];
	            qNDSmap.insert(pair<int, int>(label,cnt));
	        }
	    }
	    
	    for ( int i = 0; i < mpdbSt.SizeofNode; i++ ) {
	        if ( L >= mpdbSt.GraphShortestMatrix[dbIdx][i] ) {
	            isObserv2Satisfied = false;
	            label = mpdbSt.NodeLabel[i];
	            cnt  = mpdbSt.ndsMatrixTri[dbIdx][i];
	            multimap<int,int>::iterator lowit=qNDSmap.lower_bound(label);
	            multimap<int,int>::iterator upit=qNDSmap.upper_bound(label);
	            for ( it1 = lowit; it1 != upit; it1++ ) {
	                if ( it1->second <= cnt ) {
	                    isObserv2Satisfied = true;
	                }
	            }
	            if ( false == isObserv2Satisfied ) {
	                mCandSet.erase(i);
	                mDelSet.insert(i);
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
	    //map < int, int > :: iterator it;
	    qLabel = mpqSt.NodeLabel[qIdx];
	    dbLabel = mpdbSt.NodeLabel[dbIdx];
	    if ( qLabel.equals(dbLabel)) {
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
	    int cnt, label;
	    multimap < int, int > qMulMap;
	    multimap < int, int > dbMulMap;
	    multimap < int, int > :: iterator lowit;
	    multimap < int, int > :: iterator upit;
	    multimap < int, int > :: iterator it;
	    multimap < int, int > :: iterator dbit;
	    bool flag;
	    for ( int i = 0; i < mpqSt->SizeofNode; i++ ) {
	        if ( NOEDGE != mpqSt->GraphMatrix[qIdx][i] ) {
	            cnt = mpqSt->ndsMatrixTri[qIdx][i];
	            label = mpqSt->NodeLabel[i];
	            it = qMulMap.find ( label );
	            if ( qMulMap.end() == it ) {
	                qMulMap.insert(pair<int, int> ( label, cnt ) );
	            } else {
	                while ( it->first == label && it->second < cnt) {
	                    it++;
	                }
	                it--;
	                qMulMap.insert(it, pair<int,int> ( label, cnt ) );
	            }
	        }
	    }
	    for ( int i = 0; i < mpdbSt->SizeofNode; i++ ) {
	        if ( NOEDGE != mpdbSt->GraphMatrix[dbIdx][i] ) {
	            cnt = mpdbSt->ndsMatrixTri[dbIdx][i];
	            label = mpdbSt->NodeLabel[i];
	            dbMulMap.insert(pair<int, int> ( label, cnt ) );
	        }
	    }
	    for ( it = qMulMap.begin(); it != qMulMap.end(); it++ ) {
	        label = it->first;
	        cnt = it->second;
	        lowit = dbMulMap.lower_bound(label);
	        upit = dbMulMap.upper_bound(label);
	        flag = false;
	        if ( dbMulMap.end() == dbMulMap.find(label) ) {
	            return false;
	        }
	        for ( dbit = lowit; dbit != upit; dbit++ ) {
	            if ( dbit->second >= cnt ) {
	                flag = true;
	                break;
	            }
	        }
	        if ( false == flag ) {
	            return false;
	        }
	    }
	    return true;
	}

	void dynamicMatching(int i) {
	    int lastMatchDb;
	    int size = mCurMatch.size();
	    set < int > :: iterator it;
	    if ( i == mpqSt->SizeofNode ) {
	        mMatchSet.insert(mCurMatch);
	        int lastMatchQ=dfsQgraphNodes[i-1];
	        matchMapT :: iterator matchIt = mCurMatch.find(lastMatchQ);
	        mDelSet.clear();
	        deleteMapT::iterator delMapit = mDelMap.find(lastMatchQ);
	        mDelSet = delMapit->second;
	        deleteSetT :: iterator delIt;
	        for ( delIt = mDelSet.begin(); delIt != mDelSet.end(); delIt++ ) {
	            mCandSet.insert(*delIt);
	        }
	        mDelSet.clear();
	        mDelMap.erase(delMapit);
	        mCurMatch.erase(matchIt);
	        return;
	    }
	    int qIdx = dfsQgraphNodes[i];
	    it = mCandSet.begin();
	    while ( it != mCandSet.end()) {
	        //
	        if ( 1 == isSameLabelAdjacency ( qIdx, *it ) ) {
	            if ( 1 == isInFalseList ( qIdx, *it ) ) {
	                it++;
	                continue;
	            } 
	            if ( 1 == isInTrueList ( qIdx, *it ) ) {
	                addMatch(qIdx, *it );
	                lastMatchDb = *it;
	                shrinkCand(qIdx,*it);
	                mDelMap.insert(pair<int,deleteSetT>(qIdx,mDelSet));
	                mDelSet.clear();
	                dynamicMatching(i+1);
	                it = mCandSet.find( lastMatchDb );
	                it++;
	                continue;
	            }
	            if ( 1 == isInequalitySatisfied(qIdx, *it ) ) {
	                addMatch( qIdx, *it );
	                lastMatchDb = *it;
	                addToTrueList( qIdx, *it );
	                shrinkCand(qIdx, *it);
	                mDelMap.insert(pair<int,deleteSetT>(qIdx,mDelSet));
	                mDelSet.clear();
	                dynamicMatching(i+1);
	                it = mCandSet.find( lastMatchDb );
	                it++;
	                continue;
	            } else {
	                addToFalseList( qIdx, *it );
	            }
	        } 
	        it++;
	    }
	    if ( 0 != i ) {
	        int lastMatchQ=dfsQgraphNodes[i-1];
	        matchMapT :: iterator matchIt = mCurMatch.find(lastMatchQ);
	        mDelSet.clear();
	        deleteMapT::iterator delMapit = mDelMap.find(lastMatchQ);
	        mDelSet = delMapit->second;
	        deleteSetT :: iterator delIt;
	        for ( delIt = mDelSet.begin(); delIt != mDelSet.end(); delIt++ ) {
	            mCandSet.insert(*delIt);
	        }
	        mDelSet.clear();
	        mDelMap.erase(delMapit);
	        mCurMatch.erase(matchIt);
	    }
	}
}
