/* Copyright 2008, 2009, 2010 by the Oxford University Computing Laboratory

   This file is part of HermiT.

   HermiT is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   HermiT is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with HermiT.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.semanticweb.HermiT.blocking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.Concept;
import org.semanticweb.HermiT.model.DataRange;
import org.semanticweb.HermiT.tableau.ExtensionTable;
import org.semanticweb.HermiT.tableau.Node;
import org.semanticweb.HermiT.tableau.NodeType;
import org.semanticweb.HermiT.tableau.Tableau;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SingleDirectBlockingChecker implements DirectBlockingChecker,Serializable {
    private static final long serialVersionUID=9093753046859877016L;

    @Nonnull
    protected final SetFactory<AtomicConcept> m_atomicConceptsSetFactory;
    @Nonnull
    protected final List<AtomicConcept> m_atomicConceptsBuffer;
    protected Tableau m_tableau;
    protected ExtensionTable.Retrieval m_binaryTableSearch1Bound;

    public SingleDirectBlockingChecker() {
        m_atomicConceptsSetFactory=new SetFactory<AtomicConcept>();
        m_atomicConceptsBuffer=new ArrayList<AtomicConcept>();
    }
    public void initialize(@Nonnull Tableau tableau) {
        m_tableau=tableau;
        m_binaryTableSearch1Bound=tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,true },ExtensionTable.View.TOTAL);
    }
    public void clear() {
        m_atomicConceptsSetFactory.clearNonpermanent();
        m_binaryTableSearch1Bound.clear();
    }
    public boolean isBlockedBy(@Nonnull Node blocker, @Nonnull Node blocked) {
        return
            !blocker.isBlocked() &&
            blocker.getNodeType()==NodeType.TREE_NODE &&
            blocked.getNodeType()==NodeType.TREE_NODE &&
            ((SingleBlockingObject)blocker.getBlockingObject()).getAtomicConceptsLabel()==((SingleBlockingObject)blocked.getBlockingObject()).getAtomicConceptsLabel();
    }
    public int blockingHashCode(@Nonnull Node node) {
        return ((SingleBlockingObject)node.getBlockingObject()).m_atomicConceptsLabelHashCode;
    }
    public boolean canBeBlocker(@Nonnull Node node) {
        return node.getNodeType()==NodeType.TREE_NODE;
    }
    public boolean canBeBlocked(@Nonnull Node node) {
        return node.getNodeType()==NodeType.TREE_NODE;
    }
    public boolean hasBlockingInfoChanged(@Nonnull Node node) {
        return ((SingleBlockingObject)node.getBlockingObject()).m_hasChanged;
    }
    public void clearBlockingInfoChanged(@Nonnull Node node) {
        ((SingleBlockingObject)node.getBlockingObject()).m_hasChanged=false;
    }
    public void nodeInitialized(@Nonnull Node node) {
        if (node.getBlockingObject()==null)
            node.setBlockingObject(new SingleBlockingObject(node));
        ((SingleBlockingObject)node.getBlockingObject()).initialize();
    }
    public void nodeDestroyed(@Nonnull Node node) {
        ((SingleBlockingObject)node.getBlockingObject()).destroy();
    }
    @Nullable
    public Node assertionAdded(Concept concept, @Nonnull Node node,boolean isCore) {
        if (concept instanceof AtomicConcept) {
            ((SingleBlockingObject)node.getBlockingObject()).addAtomicConcept((AtomicConcept)concept);
            return node;
        }
        else
            return null;
    }
    @Nullable
    public Node assertionRemoved(Concept concept, @Nonnull Node node,boolean isCore) {
        if (concept instanceof AtomicConcept) {
            ((SingleBlockingObject)node.getBlockingObject()).removeAtomicConcept((AtomicConcept)concept);
            return node;
        }
        else
            return null;
    }
    @Nullable
    public Node assertionAdded(DataRange range,Node node,boolean isCore) {
        return null;
    }
    @Nullable
    public Node assertionRemoved(DataRange range,Node node,boolean isCore) {
        return null;
    }
    @Nullable
    public Node assertionAdded(AtomicRole atomicRole,Node nodeFrom,Node nodeTo,boolean isCore) {
        return null;
    }
    @Nullable
    public Node assertionRemoved(AtomicRole atomicRole,Node nodeFrom,Node nodeTo,boolean isCore) {
        return null;
    }
    @Nullable
    public Node nodesMerged(Node mergeFrom,Node mergeInto) {
        return null;
    }
    @Nullable
    public Node nodesUnmerged(Node mergeFrom,Node mergeInto) {
        return null;
    }
    @Nonnull
    public BlockingSignature getBlockingSignatureFor(@Nonnull Node node) {
        return new SingleBlockingSignature(this,node);
    }
    @Nullable
    protected Set<AtomicConcept> fetchAtomicConceptsLabel(Node node) {
        m_atomicConceptsBuffer.clear();
        m_binaryTableSearch1Bound.getBindingsBuffer()[1]=node;
        m_binaryTableSearch1Bound.open();
        Object[] tupleBuffer=m_binaryTableSearch1Bound.getTupleBuffer();
        while (!m_binaryTableSearch1Bound.afterLast()) {
            Object concept=tupleBuffer[0];
            if (concept instanceof AtomicConcept)
                m_atomicConceptsBuffer.add((AtomicConcept)concept);
            m_binaryTableSearch1Bound.next();
        }
        Set<AtomicConcept> result=m_atomicConceptsSetFactory.getSet(m_atomicConceptsBuffer);
        m_atomicConceptsBuffer.clear();
        return result;
    }
    public boolean hasChangedSinceValidation(Node node) {
        return false;
    }
    public void setHasChangedSinceValidation(Node node,boolean hasChanged) {
        // do nothing
    }

    protected final class SingleBlockingObject implements Serializable {
        private static final long serialVersionUID=-5439737072100509531L;

        protected final Node m_node;
        protected boolean m_hasChanged;
        @Nullable
        protected Set<AtomicConcept> m_atomicConceptsLabel;
        protected int m_atomicConceptsLabelHashCode;

        public SingleBlockingObject(Node node) {
            m_node=node;
        }
        public void initialize() {
            m_atomicConceptsLabel=null;
            m_atomicConceptsLabelHashCode=0;
            m_hasChanged=true;
        }
        public void destroy() {
            if (m_atomicConceptsLabel!=null) {
                m_atomicConceptsSetFactory.removeReference(m_atomicConceptsLabel);
                m_atomicConceptsLabel=null;
            }
        }
        @Nullable
        public Set<AtomicConcept> getAtomicConceptsLabel() {
            if (m_atomicConceptsLabel==null) {
                m_atomicConceptsLabel=SingleDirectBlockingChecker.this.fetchAtomicConceptsLabel(m_node);
                m_atomicConceptsSetFactory.addReference(m_atomicConceptsLabel);
            }
            return m_atomicConceptsLabel;
        }
        public void addAtomicConcept(@Nonnull AtomicConcept atomicConcept) {
            if (m_atomicConceptsLabel!=null) {
                // invalidate, recompute real label later if necessary
                m_atomicConceptsSetFactory.removeReference(m_atomicConceptsLabel);
                m_atomicConceptsLabel=null;
            }
            m_atomicConceptsLabelHashCode+=atomicConcept.hashCode();
            m_hasChanged=true;
        }
        public void removeAtomicConcept(@Nonnull AtomicConcept atomicConcept) {
            if (m_atomicConceptsLabel!=null) {
                // invalidate, recompute real label later if necessary
                m_atomicConceptsSetFactory.removeReference(m_atomicConceptsLabel);
                m_atomicConceptsLabel=null;
            }
            m_atomicConceptsLabelHashCode-=atomicConcept.hashCode();
            m_hasChanged=true;
        }
    }

    protected static class SingleBlockingSignature extends BlockingSignature implements Serializable {
        private static final long serialVersionUID=-7349489846772132258L;

        @Nullable
        protected final Set<AtomicConcept> m_atomicConceptsLabel;

        public SingleBlockingSignature(@Nonnull SingleDirectBlockingChecker checker, @Nonnull Node node) {
            m_atomicConceptsLabel=((SingleBlockingObject)node.getBlockingObject()).getAtomicConceptsLabel();
            checker.m_atomicConceptsSetFactory.makePermanent(m_atomicConceptsLabel);
        }
        public boolean blocksNode(@Nonnull Node node) {
            return ((SingleBlockingObject)node.getBlockingObject()).getAtomicConceptsLabel()==m_atomicConceptsLabel;
        }
        public int hashCode() {
            return m_atomicConceptsLabel.hashCode();
        }
        public boolean equals(Object that) {
            if (this==that)
                return true;
            if (!(that instanceof SingleBlockingSignature))
                return false;
            return m_atomicConceptsLabel==((SingleBlockingSignature)that).m_atomicConceptsLabel;
        }
    }
}
