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
import org.semanticweb.HermiT.datatypes.DatatypeRegistry;
import org.semanticweb.HermiT.datatypes.MalformedLiteralException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a constants.
 */
public class Constant extends Term {
    private static final long serialVersionUID=-8143911431654640690L;

    protected final String m_lexicalForm;
    protected final String m_datatypeURI;
    protected final Object m_dataValue;

    protected Constant(String lexicalForm,String datatypeURI,Object dataValue) {
        m_lexicalForm=lexicalForm;
        m_datatypeURI=datatypeURI;
        m_dataValue=dataValue;
    }
    public String getLexicalForm() {
        return m_lexicalForm;
    }
    public String getDatatypeURI() {
        return m_datatypeURI;
    }
    public Object getDataValue() {
        return m_dataValue;
    }
    public boolean isAnonymous() {
        return "internal:anonymous-constants".equals(m_datatypeURI);
    }
    @Nonnull
    public String toString() {
        return toString(Prefixes.STANDARD_PREFIXES);
    }
    @Nonnull
    public String toString(@Nonnull Prefixes prefixes) {
        StringBuffer buffer=new StringBuffer();
        buffer.append('"');
        for (int index=0;index<m_lexicalForm.length();index++) {
            char c=m_lexicalForm.charAt(index);
            switch (c) {
            case '"':
                buffer.append("\\\"");
                break;
            case '\\':
                buffer.append("\\\\");
                break;
            default:
                buffer.append(c);
                break;
            }
        }
        buffer.append("\"^^");
        buffer.append(prefixes.abbreviateIRI(m_datatypeURI));
        return buffer.toString();
    }
    @Nullable
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    @Nonnull
    protected static InterningManager<Constant> s_interningManager=new InterningManager<Constant>() {
        protected boolean equal(@Nonnull Constant object1, @Nonnull Constant object2) {
            return object1.m_lexicalForm.equals(object2.m_lexicalForm) && object1.m_datatypeURI.equals(object2.m_datatypeURI);
        }
        protected int getHashCode(@Nonnull Constant object) {
            return object.m_lexicalForm.hashCode()+object.m_datatypeURI.hashCode();
        }
    };

    @Nullable
    public static Constant create(String lexicalForm,String datatypeURI) throws MalformedLiteralException {
        Object dataValue=DatatypeRegistry.parseLiteral(lexicalForm,datatypeURI);
        return s_interningManager.intern(new Constant(lexicalForm,datatypeURI,dataValue));
    }
    @Nullable
    public static Constant createAnonymous(String id) {
        return create(id,"internal:anonymous-constants");
    }
}
