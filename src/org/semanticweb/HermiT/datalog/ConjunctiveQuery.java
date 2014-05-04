package org.semanticweb.HermiT.datalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.Term;
import org.semanticweb.HermiT.model.Variable;
import org.semanticweb.HermiT.tableau.DLClauseEvaluator;
import org.semanticweb.HermiT.tableau.DLClauseEvaluator.Worker;
import org.semanticweb.HermiT.tableau.DependencySet;
import org.semanticweb.HermiT.tableau.ExtensionTable;
import org.semanticweb.HermiT.tableau.ExtensionTable.View;
import org.semanticweb.HermiT.tableau.HyperresolutionManager;
import org.semanticweb.HermiT.tableau.Node;

import javax.annotation.Nonnull;

public class ConjunctiveQuery {
    @Nonnull
    protected final DatalogEngine m_datalogEngine;
    protected final Atom[] m_queryAtoms;
    @Nonnull
    protected final Term[] m_answerTerms;
    protected final Term[] m_resultBuffer;
    @Nonnull
    protected final OneEmptyTupleRetrieval m_firstRetrieval;
    @Nonnull
    protected final QueryResultCollector[] m_queryResultCollector;
    @Nonnull
    protected final Worker[] m_workers;

    public ConjunctiveQuery(@Nonnull DatalogEngine datalogEngine,Atom[] queryAtoms, @Nonnull Term[] answerTerms) {
        if (!datalogEngine.materialize())
            throw new IllegalStateException("The supplied DL ontology is unsatisfiable.");
        m_datalogEngine=datalogEngine;
        m_queryAtoms=queryAtoms;
        m_answerTerms=answerTerms;
        m_resultBuffer=answerTerms.clone();
        m_firstRetrieval=new OneEmptyTupleRetrieval();
        m_queryResultCollector=new QueryResultCollector[1];
        HyperresolutionManager.BodyAtomsSwapper swapper=new HyperresolutionManager.BodyAtomsSwapper(DLClause.create(new Atom[0],queryAtoms));
        DLClause queryDLClause=swapper.getSwappedDLClause(0);
        QueryCompiler queryCompiler=new QueryCompiler(this,queryDLClause,answerTerms,datalogEngine.m_termsToNodes,datalogEngine.m_nodesToTerms,m_resultBuffer,m_queryResultCollector,m_firstRetrieval);
        m_workers=new Worker[queryCompiler.m_workers.size()];
        queryCompiler.m_workers.toArray(m_workers);
    }
    @Nonnull
    public DatalogEngine getDatalogEngine() {
        return m_datalogEngine;
    }
    public int getNumberOfQUeryAtoms() {
        return m_queryAtoms.length;
    }
    public Atom getQueryAtom(int atomIndex) {
        return m_queryAtoms[atomIndex];
    }
    public int getNumberOfAnswerTerms() {
        return m_answerTerms.length;
    }
    public Term getAnswerTerm(int termIndex) {
        return m_answerTerms[termIndex];
    }
    public void evaluate(QueryResultCollector queryResultCollector) {
        try {
            m_queryResultCollector[0]=queryResultCollector;
            m_firstRetrieval.open();
            int programCounter=0;
            while (programCounter<m_workers.length)
                programCounter=m_workers[programCounter].execute(programCounter);
        }
        finally {
            m_queryResultCollector[0]=null;
        }
    }

    protected static final class OneEmptyTupleRetrieval implements ExtensionTable.Retrieval {
        protected static final int[] s_noBindings=new int[0];
        protected static final Object[] s_noObjects=new Object[0];

        protected boolean m_afterLast;
        
        public OneEmptyTupleRetrieval() {
            m_afterLast=true;
        }
        @Nonnull
        public ExtensionTable getExtensionTable() {
            throw new UnsupportedOperationException();
        }
        @Nonnull
        public View getExtensionView() {
            return View.TOTAL;
        }
        public void clear() {
            throw new UnsupportedOperationException();
        }
        @Nonnull
        public int[] getBindingPositions() {
            return s_noBindings;
        }
        @Nonnull
        public Object[] getBindingsBuffer() {
            return s_noObjects;
        }
        @Nonnull
        public Object[] getTupleBuffer() {
            return s_noObjects;
        }
        @Nonnull
        public DependencySet getDependencySet() {
            throw new UnsupportedOperationException();
        }
        public boolean isCore() {
            return false;
        }
        public void open() {
            m_afterLast=false;
        }
        public boolean afterLast() {
            return m_afterLast;
        }
        public int getCurrentTupleIndex() {
            return m_afterLast ? -1 : 0;
        }
        public void next() {
            m_afterLast=true;
        }
    }
    
    public static class QueryAnswerCallback implements Worker {
        protected final ConjunctiveQuery m_conjunctiveQuery;
        protected final Map<Node,Term> m_nodesToTerms;
        protected final Term[] m_resultBuffer;
        protected final QueryResultCollector[] m_queryResultCollector;
        protected final int[][] m_copyAnswers;
        protected final Object[] m_valuesBuffer;
        
        public QueryAnswerCallback(ConjunctiveQuery conjunctiveQuery,Map<Node,Term> nodesToTerms,Term[] resultBuffer,QueryResultCollector[] queryResultCollector,int[][] copyAnswers,Object[] valuesBuffer) {
            m_conjunctiveQuery=conjunctiveQuery;
            m_nodesToTerms=nodesToTerms;
            m_resultBuffer=resultBuffer;
            m_queryResultCollector=queryResultCollector;
            m_copyAnswers=copyAnswers;
            m_valuesBuffer=valuesBuffer;
        }
        public int execute(int programCounter) {
            for (int copyIndex=m_copyAnswers.length-1;copyIndex>=0;--copyIndex)
                m_resultBuffer[m_copyAnswers[copyIndex][1]]=m_nodesToTerms.get((Node)m_valuesBuffer[m_copyAnswers[copyIndex][0]]);
            m_queryResultCollector[0].processResult(m_conjunctiveQuery,m_resultBuffer);
            return programCounter+1;
        }
        @Nonnull
        public String toString() {
            return "Call query consumer";
        }
    }

    protected static final class QueryCompiler extends DLClauseEvaluator.ConjunctionCompiler {
        @Nonnull
        protected final ConjunctiveQuery m_conjunctiveQuery;
        @Nonnull
        protected final Term[] m_answerTerms;
        protected final Map<Node,Term> m_nodesToTerms;
        protected final Term[] m_resultBuffer;
        protected final QueryResultCollector[] m_queryResultCollector;

        public QueryCompiler(@Nonnull ConjunctiveQuery conjunctiveQuery, @Nonnull DLClause queryDLClause, @Nonnull Term[] answerTerms, @Nonnull Map<Term,Node> termsToNodes,Map<Node,Term> nodesToTerms,Term[] resultBuffer,QueryResultCollector[] queryResultCollector, @Nonnull ExtensionTable.Retrieval oneEmptyTupleRetrieval) {
            super(new DLClauseEvaluator.BufferSupply(),new DLClauseEvaluator.ValuesBufferManager(Collections.singleton(queryDLClause),termsToNodes),null,conjunctiveQuery.m_datalogEngine.m_extensionManager,queryDLClause.getBodyAtoms(),getAnswerVariables(answerTerms));
            m_conjunctiveQuery=conjunctiveQuery;
            m_answerTerms=answerTerms;
            m_nodesToTerms=nodesToTerms;
            m_resultBuffer=resultBuffer;
            m_queryResultCollector=queryResultCollector;
            generateCode(0,oneEmptyTupleRetrieval);
        }
        protected void compileHeads() {
            List<int[]> copyAnswers=new ArrayList<int[]>();
            for (int index=0;index<m_answerTerms.length;++index) {
                Term answerTerm=m_answerTerms[index];
                if (answerTerm instanceof Variable) {
                    int answerVariableIndex=m_variables.indexOf(answerTerm);
                    copyAnswers.add(new int[] { answerVariableIndex,index });
                }
            }
            m_workers.add(new QueryAnswerCallback(m_conjunctiveQuery,m_nodesToTerms,m_resultBuffer,m_queryResultCollector,copyAnswers.toArray(new int[copyAnswers.size()][]),m_valuesBufferManager.m_valuesBuffer));
        }
        
        @Nonnull
        protected static List<Variable> getAnswerVariables(@Nonnull Term[] answerTerms) {
            List<Variable> result=new ArrayList<Variable>();
            for (Term answerTerm : answerTerms)
                if (answerTerm instanceof Variable)
                    result.add((Variable)answerTerm);
            return result;
        }
    }
    
}
