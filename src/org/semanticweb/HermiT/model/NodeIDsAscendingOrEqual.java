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

import java.io.Serializable;

import org.semanticweb.HermiT.Prefixes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a predicate that whether the IDs of the argument nodes are all strictly ascending or all equal.
 */
public class NodeIDsAscendingOrEqual implements DLPredicate,Serializable {
    private static final long serialVersionUID=7197886700065386931L;

    protected final int m_arity;
    
    protected NodeIDsAscendingOrEqual(int arity) {
        m_arity=arity;
    }
    public int getArity() {
        return m_arity;
    }
    @Nullable
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }
    @Nonnull
    public String toString(Prefixes prefixes) {
        return "NodeIDsAscendingOrEqual";
    }
    @Nonnull
    protected static InterningManager<NodeIDsAscendingOrEqual> s_interningManager=new InterningManager<NodeIDsAscendingOrEqual>() {
        protected boolean equal(@Nonnull NodeIDsAscendingOrEqual object1, @Nonnull NodeIDsAscendingOrEqual object2) {
            return object1.m_arity==object2.m_arity;
        }
        protected int getHashCode(@Nonnull NodeIDsAscendingOrEqual object) {
            return object.m_arity;
        }
    };
    
    @Nullable
    public static NodeIDsAscendingOrEqual create(int arity) {
        return s_interningManager.intern(new NodeIDsAscendingOrEqual(arity));
    }
}
