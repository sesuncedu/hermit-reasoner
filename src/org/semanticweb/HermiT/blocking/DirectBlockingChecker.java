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

import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.Concept;
import org.semanticweb.HermiT.model.DataRange;
import org.semanticweb.HermiT.tableau.Node;
import org.semanticweb.HermiT.tableau.Tableau;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DirectBlockingChecker {
    void initialize(Tableau tableau);
    void clear();
    boolean isBlockedBy(Node blocker,Node blocked);
    int blockingHashCode(Node node);
    boolean canBeBlocker(Node node);
    /**
     * @param node
     * @return true if node is a tree node
     */
    boolean canBeBlocked(Node node);
    boolean hasBlockingInfoChanged(Node node);
    void clearBlockingInfoChanged(Node node);
    boolean hasChangedSinceValidation(Node node);
    void setHasChangedSinceValidation(Node node, boolean hasChanged);
    void nodeInitialized(Node node);
    void nodeDestroyed(Node node);
    @Nullable
    Node assertionAdded(Concept concept,Node node,boolean isCore);
    @Nullable
    Node assertionRemoved(Concept concept,Node node,boolean isCore);
    @Nullable
    Node assertionAdded(DataRange range,Node node,boolean isCore);
    @Nullable
    Node assertionRemoved(DataRange range,Node node,boolean isCore);
    @Nullable
    Node assertionAdded(AtomicRole atomicRole,Node nodeFrom,Node nodeTo,boolean isCore);
    @Nullable
    Node assertionRemoved(AtomicRole atomicRole,Node nodeFrom,Node nodeTo,boolean isCore);
    @Nullable
    Node nodesMerged(Node mergeFrom,Node mergeInto);
    @Nullable
    Node nodesUnmerged(Node mergeFrom,Node mergeInto);
    @Nonnull
    BlockingSignature getBlockingSignatureFor(Node node);
}
