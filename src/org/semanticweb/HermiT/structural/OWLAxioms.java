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
package org.semanticweb.HermiT.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.SWRLAtom;

import javax.annotation.Nonnull;

public class OWLAxioms {
    @Nonnull
    public final Set<OWLClass> m_classes;
    @Nonnull
    public final Set<OWLObjectProperty> m_objectProperties;
    @Nonnull
    public final Set<OWLObjectProperty> m_objectPropertiesOccurringInOWLAxioms;
    @Nonnull
    public final Set<OWLObjectPropertyExpression> m_complexObjectPropertyExpressions;
    @Nonnull
    public final Set<OWLDataProperty> m_dataProperties;
    @Nonnull
    public final Set<OWLNamedIndividual> m_namedIndividuals;
    @Nonnull
    public final Collection<OWLClassExpression[]> m_conceptInclusions;
    @Nonnull
    public final Collection<OWLDataRange[]> m_dataRangeInclusions;
    @Nonnull
    public final Collection<OWLObjectPropertyExpression[]> m_simpleObjectPropertyInclusions;
    @Nonnull
    public final Collection<ComplexObjectPropertyInclusion> m_complexObjectPropertyInclusions;
    @Nonnull
    public final Collection<OWLObjectPropertyExpression[]> m_disjointObjectProperties;
    @Nonnull
    public final Set<OWLObjectPropertyExpression> m_reflexiveObjectProperties;
    @Nonnull
    public final Set<OWLObjectPropertyExpression> m_irreflexiveObjectProperties;
    @Nonnull
    public final Set<OWLObjectPropertyExpression> m_asymmetricObjectProperties;
    @Nonnull
    public final Collection<OWLDataPropertyExpression[]> m_dataPropertyInclusions;
    @Nonnull
    public final Collection<OWLDataPropertyExpression[]> m_disjointDataProperties;
    @Nonnull
    public final Collection<OWLIndividualAxiom> m_facts;
    @Nonnull
    public final Set<OWLHasKeyAxiom> m_hasKeys;
    @Nonnull
    public final Set<String> m_definedDatatypesIRIs; // contains custom datatypes from DatatypeDefinition axioms
    @Nonnull
    public final Collection<DisjunctiveRule> m_rules;

    public OWLAxioms() {
        m_classes=new HashSet<OWLClass>();
        m_objectProperties=new HashSet<OWLObjectProperty>();
        m_objectPropertiesOccurringInOWLAxioms=new HashSet<OWLObjectProperty>();
        m_complexObjectPropertyExpressions=new HashSet<OWLObjectPropertyExpression>();
        m_dataProperties=new HashSet<OWLDataProperty>();
        m_namedIndividuals=new HashSet<OWLNamedIndividual>();
        m_conceptInclusions=new ArrayList<OWLClassExpression[]>();
        m_dataRangeInclusions=new ArrayList<OWLDataRange[]>();
        m_simpleObjectPropertyInclusions=new ArrayList<OWLObjectPropertyExpression[]>();
        m_complexObjectPropertyInclusions=new ArrayList<ComplexObjectPropertyInclusion>();
        m_disjointObjectProperties=new ArrayList<OWLObjectPropertyExpression[]>();
        m_reflexiveObjectProperties=new HashSet<OWLObjectPropertyExpression>();
        m_irreflexiveObjectProperties=new HashSet<OWLObjectPropertyExpression>();
        m_asymmetricObjectProperties=new HashSet<OWLObjectPropertyExpression>();
        m_disjointDataProperties=new ArrayList<OWLDataPropertyExpression[]>();
        m_dataPropertyInclusions=new ArrayList<OWLDataPropertyExpression[]>();
        m_facts=new HashSet<OWLIndividualAxiom>();
        m_hasKeys=new HashSet<OWLHasKeyAxiom>();
        m_definedDatatypesIRIs=new HashSet<String>();
        m_rules=new HashSet<DisjunctiveRule>();
    }

    public static class ComplexObjectPropertyInclusion {
        public final OWLObjectPropertyExpression[] m_subObjectProperties;
        public final OWLObjectPropertyExpression m_superObjectProperty;

        public ComplexObjectPropertyInclusion(OWLObjectPropertyExpression[] subObjectProperties,OWLObjectPropertyExpression superObjectPropery) {
            m_subObjectProperties=subObjectProperties;
            m_superObjectProperty=superObjectPropery;
        }
        public ComplexObjectPropertyInclusion(OWLObjectPropertyExpression transitiveObjectProperty) {
            m_subObjectProperties=new OWLObjectPropertyExpression[] { transitiveObjectProperty,transitiveObjectProperty };
            m_superObjectProperty=transitiveObjectProperty;
        }
    }

    public static class DisjunctiveRule {
        public final SWRLAtom[] m_body;
        public final SWRLAtom[] m_head;

        public DisjunctiveRule(SWRLAtom[] body,SWRLAtom[] head) {
            m_body=body;
            m_head=head;
        }
        @Nonnull
        public String toString() {
            StringBuffer buffer=new StringBuffer();
            boolean first=true;
            for (SWRLAtom atom : m_body) {
                if (first)
                    first=false;
                else
                    buffer.append(" /\\ ");
                buffer.append(atom.toString());
            }
            buffer.append(" -: ");
            first=true;
            for (SWRLAtom atom : m_head) {
                if (first)
                    first=false;
                else
                    buffer.append(" \\/ ");
                buffer.append(atom.toString());
            }
            return buffer.toString();
        }
    }
}
