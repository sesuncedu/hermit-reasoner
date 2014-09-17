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

/**
 * Represents the equality predicate.
 */
public class Equality implements DLPredicate,Serializable {
    private static final long serialVersionUID=8308051741088513244L;

    public static final Equality INSTANCE=new Equality();
    
    protected Equality () {
    }
    public int getArity() {
        return 2;
    }
    @Nonnull
    public String toString(Prefixes prefixes) {
        return "==";
    }
    @Nonnull
    public String toOrderedString(Prefixes prefixes) {
        return toString(prefixes);
    }
    @Nonnull
    public String toString() {
        return toString(Prefixes.STANDARD_PREFIXES);
    }
    @Nonnull
    protected Object readResolve() {
        return INSTANCE;
    }
    @Nonnull
    public static Equality create() {
        return INSTANCE;
    }
}
