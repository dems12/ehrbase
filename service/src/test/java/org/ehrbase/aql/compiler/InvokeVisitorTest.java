/*
 * Copyright (c) 2020 Stefan Spiska (Vitasystems GmbH) and Luis Marco-Ruiz (Hannover Medical School).
 *
 * This file is part of project EHRbase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehrbase.aql.compiler;

import org.ehrbase.aql.definition.I_VariableDefinition;
import org.ehrbase.aql.definition.I_VariableDefinitionHelper;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InvokeVisitorTest {

    @Test
    public void getWhereExpression() {

        {
            WhereVisitor cut = new WhereVisitor();
            String aql = "select a  from EHR  [ehr_id/value = '26332710-16f3-4b54-aae9-4d11c141388c'] contains COMPOSITION a[openEHR-EHR-COMPOSITION.health_summary.v1]" +
                    "where a/composer/name='Tony Stark'";
            ParseTree tree = QueryHelper.setupParseTree(aql);
            cut.visit(tree);

            List<Object> whereExpression = cut.getWhereExpression();
            assertThat(whereExpression).size().isEqualTo(3);

            I_VariableDefinition where1 = (I_VariableDefinition) whereExpression.get(0);
            I_VariableDefinition expected = I_VariableDefinitionHelper.build("composer/name", null, "a", false, false, false);
            I_VariableDefinitionHelper.checkEqualWithoutFuncParameters(where1, expected);

            assertThat(whereExpression.get(1)).isEqualTo("=");

            assertThat(whereExpression.get(2)).isEqualTo("'Tony Stark'");

        }

        {
            WhereVisitor cut = new WhereVisitor();
            String aql = "SELECT o/data[at0002]/events[at0003] AS systolic " +
                    "FROM EHR [ehr_id/value='1234'] " +
                    "CONTAINS COMPOSITION c [openEHR-EHR-COMPOSITION.encounter.v1] " +
                    "CONTAINS OBSERVATION o [openEHR-EHR-OBSERVATION.blood_pressure.v1] " +
                    "WHERE o/data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/value > 140";
            ParseTree tree = QueryHelper.setupParseTree(aql);
            cut.visit(tree);

            List<Object> whereExpression = cut.getWhereExpression();
            assertThat(whereExpression).size().isEqualTo(3);

            I_VariableDefinition where1 = (I_VariableDefinition) whereExpression.get(0);
            I_VariableDefinition expected = I_VariableDefinitionHelper.build("data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/value", null, "o", false, false, false);
            I_VariableDefinitionHelper.checkEqualWithoutFuncParameters(where1, expected);

            assertThat(whereExpression.get(1)).isEqualTo(">");

            assertThat(whereExpression.get(2)).isEqualTo("140");

        }

        {
            WhereVisitor cut = new WhereVisitor();
            String aql = "SELECT o/data[at0002]/events[at0003] AS systolic " +
                    "FROM EHR [ehr_id/value='1234'] " +
                    "CONTAINS COMPOSITION c [openEHR-EHR-COMPOSITION.encounter.v1] " +
                    "CONTAINS OBSERVATION o [openEHR-EHR-OBSERVATION.blood_pressure.v1] " +
                    "WHERE o/data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/value > " +
                    "c/data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/value";
            ParseTree tree = QueryHelper.setupParseTree(aql);
            cut.visit(tree);

            List<Object> whereExpression = cut.getWhereExpression();
            assertThat(whereExpression).size().isEqualTo(3);

            I_VariableDefinition where1 = (I_VariableDefinition) whereExpression.get(0);
            I_VariableDefinition expected1 = I_VariableDefinitionHelper.build("data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/value", null, "o", false, false, false);
            I_VariableDefinitionHelper.checkEqualWithoutFuncParameters(where1, expected1);

            assertThat(whereExpression.get(1)).isEqualTo(">");

            I_VariableDefinition where2 = (I_VariableDefinition) whereExpression.get(2);
            I_VariableDefinition expected2 = I_VariableDefinitionHelper.build("data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/value", null, "c", false, false, false);
            I_VariableDefinitionHelper.checkEqualWithoutFuncParameters(where2, expected2);

        }

        {
            WhereVisitor cut = new WhereVisitor();
            String aql = "select a  from EHR  [ehr_id/value = '26332710-16f3-4b54-aae9-4d11c141388c'] contains COMPOSITION a[openEHR-EHR-COMPOSITION.health_summary.v1]" +
                    "where a/composer/name='Tony Stark' and a/name/value='Immunisation summary'";
            ParseTree tree = QueryHelper.setupParseTree(aql);
            cut.visit(tree);

            List<Object> whereExpression = cut.getWhereExpression();
            assertThat(whereExpression).size().isEqualTo(7);

            I_VariableDefinition where1 = (I_VariableDefinition) whereExpression.get(0);
            I_VariableDefinition expected1 = I_VariableDefinitionHelper.build("composer/name", null, "a", false, false, false);
            I_VariableDefinitionHelper.checkEqualWithoutFuncParameters(where1, expected1);

            assertThat(whereExpression.get(1)).isEqualTo("=");

            assertThat(whereExpression.get(2)).isEqualTo("'Tony Stark'");

            assertThat(whereExpression.get(3)).isEqualTo("and");

            I_VariableDefinition where5 = (I_VariableDefinition) whereExpression.get(4);
            I_VariableDefinition expected5 = I_VariableDefinitionHelper.build("name/value", null, "a", false, false, false);
            I_VariableDefinitionHelper.checkEqualWithoutFuncParameters(where5, expected5);


            assertThat(whereExpression.get(5)).isEqualTo("=");

            assertThat(whereExpression.get(6)).isEqualTo("'Immunisation summary'");

        }

        {
            WhereVisitor cut = new WhereVisitor();
            String aql = "SELECT o/data[at0002]/events[at0003] AS systolic " +
                    "FROM EHR [ehr_id/value='1234'] " +
                    "CONTAINS COMPOSITION c " +
                    "CONTAINS OBSERVATION o [openEHR-EHR-OBSERVATION.blood_pressure.v1] " +
                    "WHERE c/archetype_details/template_id/value matches {'Flormidal', INVOKE(https://highmed.de/ts/ValueSet/23/$expand?filter=abdo), 'Kloralhidrat'}";
            ParseTree tree = QueryHelper.setupParseTree(aql);
            cut.visit(tree);

            List<Object> whereExpression = cut.getWhereExpression();
            assertThat(whereExpression).size().isEqualTo(13);

            I_VariableDefinition where1 = (I_VariableDefinition) whereExpression.get(0);
            I_VariableDefinition expected1 = I_VariableDefinitionHelper.build("archetype_details/template_id/value", null, "c", false, false, false);
            I_VariableDefinitionHelper.checkEqualWithoutFuncParameters(where1, expected1);

            assertThat(whereExpression.get(1)).isEqualTo(" IN ");

            assertThat(whereExpression.get(2)).isEqualTo("(");

            assertThat(whereExpression.get(3)).isEqualTo("'Flormidal'");

            assertThat(whereExpression.get(4)).isEqualTo(",");

            assertThat(whereExpression.get(5)).isEqualTo("48377-6");
            
            assertThat(whereExpression.get(6)).isEqualTo(",");
            
            assertThat(whereExpression.get(7)).isEqualTo("27478-7");
            
            assertThat(whereExpression.get(8)).isEqualTo(",");

            assertThat(whereExpression.get(9)).isEqualTo("52539-9");
            
            assertThat(whereExpression.get(10)).isEqualTo(",");
           
            assertThat(whereExpression.get(11)).isEqualTo("'Kloralhidrat'");

            assertThat(whereExpression.get(12)).isEqualTo(")");


        }
    }
}