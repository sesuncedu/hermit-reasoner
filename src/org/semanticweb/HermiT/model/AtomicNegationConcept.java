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
package org.semanticweb.HermiT.model;

import org.semanticweb.HermiT.Prefixes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a negation of an atomic concept.
 */
public class AtomicNegationConcept extends LiteralConcept {
    private static final long serialVersionUID=-4635386233266966577L;

    protected final AtomicConcept m_negatedAtomicConcept;
    
    protected AtomicNegationConcept(AtomicConcept negatedAtomicConcept) {
        m_negatedAtomicConcept=negatedAtomicConcept;
    }
    public AtomicConcept getNegatedAtomicConcept() {
        return m_negatedAtomicConcept;
    }
    public LiteralConcept getNegation() {
        return m_negatedAtomicConcept;
    }
    public boolean isAlwaysTrue() {
        return m_negatedAtomicConcept.isAlwaysFalse();
    }
    public boolean isAlwaysFalse() {
        return m_negatedAtomicConcept.isAlwaysTrue();
    }
    @Nonnull
    public String toString(@Nonnull Prefixes prefixes) {
        return "not("+m_negatedAtomicConcept.toString(prefixes)+")";
    }
    @Nullable
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    @Nonnull
    protected static InterningManager<AtomicNegationConcept> s_interningManager=new InterningManager<AtomicNegationConcept>() {
        protected boolean equal(@Nonnull AtomicNegationConcept object1, @Nonnull AtomicNegationConcept object2) {
            return object1.m_negatedAtomicConcept==object2.m_negatedAtomicConcept;
        }
        protected int getHashCode(@Nonnull AtomicNegationConcept object) {
            return -object.m_negatedAtomicConcept.hashCode();
        }
    };
    
    @Nullable
    public static AtomicNegationConcept create(AtomicConcept negatedAtomicConcept) {
        return s_interningManager.intern(new AtomicNegationConcept(negatedAtomicConcept));
    }
}
