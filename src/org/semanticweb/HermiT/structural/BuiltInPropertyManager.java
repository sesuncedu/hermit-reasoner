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

import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import javax.annotation.Nonnull;

public class BuiltInPropertyManager {
    protected final OWLDataFactory m_factory;
    protected final OWLObjectProperty m_topObjectProperty;
    protected final OWLObjectProperty m_bottomObjectProperty;
    protected final OWLDataProperty m_topDataProperty;
    protected final OWLDataProperty m_bottomDataProperty;

    public BuiltInPropertyManager(OWLDataFactory factory) {
        m_factory=factory;
        m_topObjectProperty=m_factory.getOWLObjectProperty(IRI.create(AtomicRole.TOP_OBJECT_ROLE.getIRI()));
        m_bottomObjectProperty=m_factory.getOWLObjectProperty(IRI.create(AtomicRole.BOTTOM_OBJECT_ROLE.getIRI()));
        m_topDataProperty=m_factory.getOWLDataProperty(IRI.create(AtomicRole.TOP_DATA_ROLE.getIRI()));
        m_bottomDataProperty=m_factory.getOWLDataProperty(IRI.create(AtomicRole.BOTTOM_DATA_ROLE.getIRI()));
    }
    public void axiomatizeBuiltInPropertiesAsNeeded(@Nonnull OWLAxioms axioms,boolean skipTopObjectProperty,boolean skipBottomObjectProperty,boolean skipTopDataProperty,boolean skipBottomDataProperty) {
        Checker checker=new Checker(axioms);
        if (checker.m_usesTopObjectProperty && !skipTopObjectProperty)
            axiomatizeTopObjectProperty(axioms);
        if (checker.m_usesBottomObjectProperty && !skipBottomObjectProperty)
            axiomatizeBottomObjectProperty(axioms);
        if (checker.m_usesTopDataProperty && !skipTopDataProperty)
            axiomatizeTopDataProperty(axioms);
        if (checker.m_usesBottomDataProperty && !skipBottomDataProperty)
            axiomatizeBottomDataProperty(axioms);
    }
    public void axiomatizeBuiltInPropertiesAsNeeded(@Nonnull OWLAxioms axioms) {
        axiomatizeBuiltInPropertiesAsNeeded(axioms,false,false,false,false);
    }
    protected void axiomatizeTopObjectProperty(@Nonnull OWLAxioms axioms) {
        // TransitiveObjectProperty( owl:topObjectProperty )
        axioms.m_complexObjectPropertyInclusions.add(new OWLAxioms.ComplexObjectPropertyInclusion(m_topObjectProperty));
        // SymmetricObjectProperty( owl:topObjectProperty )
        axioms.m_simpleObjectPropertyInclusions.add(new OWLObjectPropertyExpression[] { m_topObjectProperty,m_topObjectProperty.getInverseProperty() });
        // SubClassOf( owl:Thing ObjectSomeValuesFrom( owl:topObjectProperty ObjectOneOf( <internal:nam#topIndividual> ) ) )
        OWLIndividual newIndividual=m_factory.getOWLNamedIndividual(IRI.create("internal:nam#topIndividual"));
        OWLObjectOneOf oneOfNewIndividual=m_factory.getOWLObjectOneOf(newIndividual);
        OWLObjectSomeValuesFrom hasTopNewIndividual=m_factory.getOWLObjectSomeValuesFrom(m_topObjectProperty,oneOfNewIndividual);
        axioms.m_conceptInclusions.add(new OWLClassExpression[] { hasTopNewIndividual });
    }
    protected void axiomatizeBottomObjectProperty(@Nonnull OWLAxioms axioms) {
        axioms.m_conceptInclusions.add(new OWLClassExpression[] { m_factory.getOWLObjectAllValuesFrom(m_bottomObjectProperty,m_factory.getOWLNothing()) });
    }
    protected void axiomatizeTopDataProperty(@Nonnull OWLAxioms axioms) {
        OWLDatatype anonymousConstantsDatatype=m_factory.getOWLDatatype(IRI.create("internal:anonymous-constants"));
        OWLLiteral newConstant=m_factory.getOWLLiteral("internal:constant",anonymousConstantsDatatype);
        OWLDataOneOf oneOfNewConstant=m_factory.getOWLDataOneOf(newConstant);
        OWLDataSomeValuesFrom hasTopNewConstant=m_factory.getOWLDataSomeValuesFrom(m_topDataProperty,oneOfNewConstant);
        axioms.m_conceptInclusions.add(new OWLClassExpression[] { hasTopNewConstant });
    }
    protected void axiomatizeBottomDataProperty(@Nonnull OWLAxioms axioms) {
        axioms.m_conceptInclusions.add(new OWLClassExpression[] { m_factory.getOWLDataAllValuesFrom(m_bottomDataProperty,m_factory.getOWLDataComplementOf(m_factory.getTopDatatype())) });
    }

    protected class Checker implements OWLClassExpressionVisitor {
        public boolean m_usesTopObjectProperty;
        public boolean m_usesBottomObjectProperty;
        public boolean m_usesTopDataProperty;
        public boolean m_usesBottomDataProperty;

        public Checker(@Nonnull OWLAxioms axioms) {
            for (OWLClassExpression[] inclusion : axioms.m_conceptInclusions)
                for (OWLClassExpression description : inclusion)
                    description.accept(this);
            for (OWLObjectPropertyExpression[] inclusion : axioms.m_simpleObjectPropertyInclusions) {
                visitProperty(inclusion[0]);
                visitProperty(inclusion[1]);
            }
            for (OWLAxioms.ComplexObjectPropertyInclusion inclusion : axioms.m_complexObjectPropertyInclusions) {
                for (OWLObjectPropertyExpression subObjectProperty : inclusion.m_subObjectProperties)
                    visitProperty(subObjectProperty);
                visitProperty(inclusion.m_superObjectProperty);
            }
            for (OWLObjectPropertyExpression[] disjoint : axioms.m_disjointObjectProperties)
                for (int index=0;index<disjoint.length;index++)
                    visitProperty(disjoint[index]);
            for (OWLObjectPropertyExpression property : axioms.m_reflexiveObjectProperties)
                visitProperty(property);
            for (OWLObjectPropertyExpression property : axioms.m_irreflexiveObjectProperties)
                visitProperty(property);
            for (OWLObjectPropertyExpression property : axioms.m_asymmetricObjectProperties)
                visitProperty(property);
            for (OWLDataPropertyExpression[] inclusion : axioms.m_dataPropertyInclusions) {
                visitProperty(inclusion[0]);
                visitProperty(inclusion[1]);
            }
            for (OWLDataPropertyExpression[] disjoint : axioms.m_disjointDataProperties)
                for (int index=0;index<disjoint.length;index++)
                    visitProperty(disjoint[index]);
            FactVisitor factVisitor=new FactVisitor();
            for (OWLIndividualAxiom fact : axioms.m_facts)
                fact.accept(factVisitor);
        }
        protected void visitProperty(@Nonnull OWLObjectPropertyExpression object) {
            if (object.getNamedProperty().equals(m_topObjectProperty))
                m_usesTopObjectProperty=true;
            else if (object.getNamedProperty().equals(m_bottomObjectProperty))
                m_usesBottomObjectProperty=true;
        }

        protected void visitProperty(@Nonnull OWLDataPropertyExpression object) {
            if (object.asOWLDataProperty().equals(m_topDataProperty))
                m_usesTopDataProperty=true;
            else if (object.asOWLDataProperty().equals(m_bottomDataProperty))
                m_usesBottomDataProperty=true;
        }

        public void visit(OWLClass object) {
        }

        public void visit(@Nonnull OWLObjectComplementOf object) {
            object.getOperand().accept(this);
        }

        public void visit(@Nonnull OWLObjectIntersectionOf object) {
            for (OWLClassExpression description : object.getOperands())
                description.accept(this);
        }

        public void visit(@Nonnull OWLObjectUnionOf object) {
            for (OWLClassExpression description : object.getOperands())
                description.accept(this);
        }

        public void visit(OWLObjectOneOf object) {
        }

        public void visit(@Nonnull OWLObjectSomeValuesFrom object) {
            visitProperty(object.getProperty());
            object.getFiller().accept(this);
        }

        public void visit(@Nonnull OWLObjectHasValue object) {
            visitProperty(object.getProperty());
        }

        public void visit(@Nonnull OWLObjectHasSelf object) {
            visitProperty(object.getProperty());
        }

        public void visit(@Nonnull OWLObjectAllValuesFrom object) {
            visitProperty(object.getProperty());
            object.getFiller().accept(this);
        }

        public void visit(@Nonnull OWLObjectMinCardinality object) {
            visitProperty(object.getProperty());
            object.getFiller().accept(this);
        }

        public void visit(@Nonnull OWLObjectMaxCardinality object) {
            visitProperty(object.getProperty());
            object.getFiller().accept(this);
        }

        public void visit(@Nonnull OWLObjectExactCardinality object) {
            visitProperty(object.getProperty());
            object.getFiller().accept(this);
        }

        public void visit(@Nonnull OWLDataHasValue object) {
            visitProperty(object.getProperty());
        }

        public void visit(@Nonnull OWLDataSomeValuesFrom object) {
            visitProperty(object.getProperty());
        }

        public void visit(@Nonnull OWLDataAllValuesFrom object) {
            visitProperty(object.getProperty());
        }

        public void visit(@Nonnull OWLDataMinCardinality object) {
            visitProperty(object.getProperty());
        }

        public void visit(@Nonnull OWLDataMaxCardinality object) {
            visitProperty(object.getProperty());
        }

        public void visit(@Nonnull OWLDataExactCardinality object) {
            visitProperty(object.getProperty());
        }

        protected class FactVisitor extends OWLAxiomVisitorAdapter {

            public void visit(OWLSameIndividualAxiom object) {
            }

            public void visit(OWLDifferentIndividualsAxiom object) {
            }

            public void visit(@Nonnull OWLClassAssertionAxiom object) {
                object.getClassExpression().accept(Checker.this);
            }

            public void visit(@Nonnull OWLObjectPropertyAssertionAxiom object) {
                visitProperty(object.getProperty());
            }

            public void visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom object) {
                visitProperty(object.getProperty());
            }

            public void visit(@Nonnull OWLDataPropertyAssertionAxiom object) {
                visitProperty(object.getProperty());
            }

            public void visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom object) {
                visitProperty(object.getProperty());
            }
        }
    }
}
