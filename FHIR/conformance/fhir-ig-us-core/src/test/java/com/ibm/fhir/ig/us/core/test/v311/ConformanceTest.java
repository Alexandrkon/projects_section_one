/*
 * (C) Copyright IBM Corp. 2019, 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.fhir.ig.us.core.test.v311;

import static com.ibm.fhir.model.type.String.string;
import static com.ibm.fhir.path.evaluator.FHIRPathEvaluator.SINGLETON_FALSE;
import static com.ibm.fhir.path.evaluator.FHIRPathEvaluator.SINGLETON_TRUE;
import static com.ibm.fhir.validation.util.FHIRValidationUtil.countErrors;
import static com.ibm.fhir.validation.util.FHIRValidationUtil.countInformation;
import static com.ibm.fhir.validation.util.FHIRValidationUtil.countWarnings;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

import org.testng.annotations.Test;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.CarePlan;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.resource.OperationOutcome.Issue;
import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.resource.Resource;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.HumanName;
import com.ibm.fhir.model.type.Identifier;
import com.ibm.fhir.model.type.Narrative;
import com.ibm.fhir.model.type.Uri;
import com.ibm.fhir.model.type.Xhtml;
import com.ibm.fhir.model.type.code.AdministrativeGender;
import com.ibm.fhir.model.type.code.IssueSeverity;
import com.ibm.fhir.model.type.code.NarrativeStatus;
import com.ibm.fhir.path.FHIRPathNode;
import com.ibm.fhir.path.evaluator.FHIRPathEvaluator;
import com.ibm.fhir.validation.FHIRValidator;

public class ConformanceTest {

    @Test
    public void testConformsToWithEmptyContext() throws Exception {
        try (InputStream in = ConformanceTest.class.getClassLoader().getResourceAsStream("JSON/311/tests/us-core-patient.json")) {
            Patient patient = FHIRParser.parser(Format.JSON).parse(in);
            List<Issue> issues = FHIRValidator.validator().validate(patient);
            issues.forEach(System.out::println);
            assertEquals(issues.size(), 2);
        }
    }

    @Test
    public void testUSCorePulseOximetry() throws Exception {
        /**
         * This test is used to diagnose and test Code 'L/min' is invalid
         */
        try (Reader r = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("JSON/311/Observation-satO2-fiO2.json"))) {
            Resource resource = FHIRParser.parser(Format.JSON).parse(r);
            List<Issue> issues = FHIRValidator.validator().validate(resource);
            issues.forEach(item -> {
                if (item.getSeverity().getValue().equals("error")) {
                    System.out.println(item);
                }
            });
            assertEquals(countErrors(issues), 0);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testUSCoreMedicationRequest() throws Exception {
        /**
         * This test is used to diagnose and test MedicationRequest which throws an error.
         */
        try (Reader r = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("JSON/311/MedicationRequest-uscore-mo2.json"))) {
            Resource resource = FHIRParser.parser(Format.JSON).parse(r);
            List<Issue> issues = FHIRValidator.validator().validate(resource);
            issues.forEach(item -> {
                if (item.getSeverity().getValue().equals("error")) {
                    System.out.println(item);
                }
            });
            assertEquals(countErrors(issues), 0);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testUSCoreCarePlan() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("JSON/311/tests/us-core-careplan.json")) {
            CarePlan carePlan = FHIRParser.parser(Format.JSON).parse(in);
            List<Issue> issues = FHIRValidator.validator().validate(carePlan);
            issues.forEach(System.out::println);
            assertEquals(countErrors(issues), 0);
        }
    }

    @Test
    public void testUSCoreValidation1() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("JSON/311/tests/us-core-patient-no-name-asserted.json")) {
            Patient patient = FHIRParser.parser(Format.JSON).parse(in);
            List<Issue> issues = FHIRValidator.validator().validate(patient);
            issues.forEach(System.out::println);
            assertEquals(countErrors(issues), 1);
        }
    }

    @Test
    public void testUSCoreValidation2() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("JSON/311/tests/us-core-patient-no-name-not-asserted.json")) {
            Patient patient = FHIRParser.parser(Format.JSON).parse(in);
            List<Issue> issues = FHIRValidator.validator().validate(patient);
            issues.forEach(System.out::println);
            assertEquals(countErrors(issues), 0);
        }
    }

    @Test
    public void testUSCoreValidation5() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("JSON/311/tests/Pamela954_Johns824_4818eca9-c6d2-4fa0-a234-7244e620391e.json")) {
            Bundle bundle = FHIRParser.parser(Format.JSON).parse(in);
            FHIRValidator validator = FHIRValidator.validator();
            List<Issue> issues = validator.validate(bundle);
            issues.stream()
                .filter(issue -> issue.getSeverity().equals(IssueSeverity.ERROR))
                .forEach(System.out::println);
            assertEquals(countErrors(issues), 0);
        }
    }

    @Test
    public void testUSCoreValidationSmoking() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("JSON/311/tests/smoking.json")) {
            Observation bundle = FHIRParser.parser(Format.JSON).parse(in);
            FHIRValidator validator = FHIRValidator.validator();
            List<Issue> issues = validator.validate(bundle);
            assertEquals(countErrors(issues), 0);
        }
    }

    @Test
    public void testUSCoreValidation6() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("JSON/311/tests/1.json")) {
            Observation observation = FHIRParser.parser(Format.JSON).parse(in);
            List<Issue> issues = FHIRValidator.validator().validate(observation);
            assertEquals(countErrors(issues), 0);
        }
    }

    /**
     * Test the FHIRPath 'conformsTo' function on a valid US Core Ethnicity Extension
     */
    @Test
    public void testUSCoreEthnicityExtension1() throws Exception {
        Extension extension = Extension.builder()
                .extension(Extension.builder()
                    .url("ombCategory")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("2135-2"))
                        .display(string("Hispanic or Latino"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("detailed")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("2169-1"))
                        .display(string("Colombian"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("text")
                    .value(string("Hispanic or Latino - Colombian"))
                    .build())
                .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity")
                .build();

        System.out.println(extension);

        FHIRPathEvaluator evaluator = FHIRPathEvaluator.evaluator();
        Collection<FHIRPathNode> result = evaluator.evaluate(extension, "conformsTo('http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity|3.1.1')");
        System.out.println("result: " + result);

        List<Issue> issues = evaluator.getEvaluationContext().getIssues();
        issues.forEach(System.out::println);

        assertEquals(result, SINGLETON_TRUE);
        assertEquals(issues.size(), 0);
    }

    /**
     * Test the FHIRPath 'conformsTo' function on a US Core Ethnicity Extension with an invalid detailed ethnicity code
     */
    @Test
    public void testUSCoreEthnicityExtension2() throws Exception {
        Extension extension = Extension.builder()
                .extension(Extension.builder()
                    .url("ombCategory")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("2135-2"))
                        .display(string("Hispanic or Latino"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("detailed")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("xxx"))
                        .display(string("Colombian"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("text")
                    .value(string("Hispanic or Latino - Colombian"))
                    .build())
                .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity")
                .build();

        System.out.println(extension);

        FHIRPathEvaluator evaluator = FHIRPathEvaluator.evaluator();
        Collection<FHIRPathNode> result = evaluator.evaluate(extension, "conformsTo('http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity|3.1.1')");
        System.out.println("result: " + result);

        List<Issue> issues = evaluator.getEvaluationContext().getIssues();
        issues.forEach(System.out::println);

        assertEquals(result, SINGLETON_FALSE);
        assertEquals(issues.size(), 2);
    }

    /**
     * Test the FHIRValidator on a US Core Patient with a valid US Core Ethnicity Extension
     */
    @Test
    public void testUSCoreEthnicityExtension3() throws Exception {
        Patient patient = Patient.builder()
                .extension(Extension.builder()
                    .extension(Extension.builder()
                        .url("ombCategory")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("2135-2"))
                            .display(string("Hispanic or Latino"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("detailed")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("2169-1"))
                            .display(string("Colombian"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("text")
                        .value(string("Hispanic or Latino - Colombian"))
                        .build())
                    .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity")
                    .build())
                .identifier(Identifier.builder()
                    .system(Uri.of("http://someuri.org"))
                    .value(string("someValue"))
                    .build())
                .name(HumanName.builder()
                    .given(string("John"))
                    .family(string("Doe"))
                    .build())
                .gender(AdministrativeGender.MALE)
                .build();

        FHIRValidator validator = FHIRValidator.validator();
        List<Issue> issues = validator.validate(patient, "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient|3.1.1");

        issues.forEach(System.out::println);

        assertEquals(countWarnings(issues), 1);
        assertEquals(countErrors(issues), 0);
    }

    /**
     * Test the FHIRValidator on a US Core Patient with a US Core Ethnicity Extension containing an invalid detailed ethnicity code
     */
    @Test
    public void testUSCoreEthnicityExtension4() throws Exception {
        Patient patient = Patient.builder()
                .text(Narrative.builder()
                    .div(Xhtml.xhtml("<div xmlns=\"http://www.w3.org/1999/xhtml\">Generated</div>"))
                    .status(NarrativeStatus.GENERATED)
                    .build())
                .extension(Extension.builder()
                    .extension(Extension.builder()
                        .url("ombCategory")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("2135-2"))
                            .display(string("Hispanic or Latino"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("detailed")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("xxx"))
                            .display(string("Colombian"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("text")
                        .value(string("Hispanic or Latino - Colombian"))
                        .build())
                    .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity")
                    .build())
                .identifier(Identifier.builder()
                    .system(Uri.of("http://someuri.org"))
                    .value(string("someValue"))
                    .build())
                .name(HumanName.builder()
                    .given(string("John"))
                    .family(string("Doe"))
                    .build())
                .gender(AdministrativeGender.MALE)
                .build();

        FHIRValidator validator = FHIRValidator.validator();
        List<Issue> issues = validator.validate(patient, "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient|3.1.1");

        issues.forEach(System.out::println);

        assertEquals(countWarnings(issues), 0);
        assertEquals(countErrors(issues), 2);
        assertEquals(countInformation(issues), 1);
    }

    /**
     * Test the FHIRPath 'conformsTo' function on a valid US Core Race Extension
     */
    @Test
    public void testUSCoreRaceExtension1() throws Exception {
        Extension extension = Extension.builder()
                .extension(Extension.builder()
                    .url("ombCategory")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("1002-5"))
                        .display(string("American Indian or Alaska Native"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("detailed")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("1735-0"))
                        .display(string("Alaska Native"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("text")
                    .value(string("American Indian or Alaska Native - Alaska Native"))
                    .build())
                .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race")
                .build();

        System.out.println(extension);

        FHIRPathEvaluator evaluator = FHIRPathEvaluator.evaluator();
        Collection<FHIRPathNode> result = evaluator.evaluate(extension, "conformsTo('http://hl7.org/fhir/us/core/StructureDefinition/us-core-race|3.1.1')");
        System.out.println("result: " + result);

        List<Issue> issues = evaluator.getEvaluationContext().getIssues();
        issues.forEach(System.out::println);

        assertEquals(result, SINGLETON_TRUE);
        assertEquals(issues.size(), 0);
    }

    /**
     * Test the FHIRPath 'conformsTo' function on a US Core Race Extension with an invalid detailed race code
     */
    @Test
    public void testUSCoreRaceExtension2() throws Exception {
        Extension extension = Extension.builder()
                .extension(Extension.builder()
                    .url("ombCategory")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("1002-5"))
                        .display(string("American Indian or Alaska Native"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("detailed")
                    .value(Coding.builder()
                        .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                        .code(Code.of("xxx"))
                        .display(string("Alaska Native"))
                        .build())
                    .build())
                .extension(Extension.builder()
                    .url("text")
                    .value(string("American Indian or Alaska Native - Alaska Native"))
                    .build())
                .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race")
                .build();

        System.out.println(extension);

        FHIRPathEvaluator evaluator = FHIRPathEvaluator.evaluator();
        Collection<FHIRPathNode> result = evaluator.evaluate(extension, "conformsTo('http://hl7.org/fhir/us/core/StructureDefinition/us-core-race|3.1.1')");
        System.out.println("result: " + result);

        List<Issue> issues = evaluator.getEvaluationContext().getIssues();
        issues.forEach(System.out::println);

        assertEquals(result, SINGLETON_FALSE);
        assertEquals(issues.size(), 2);
    }

    /**
     * Test the FHIRValidator on a US Core Patient with a valid US Core Race Extension
     */
    @Test
    public void testUSCoreRaceExtension3() throws Exception {
        Patient patient = Patient.builder()
                .extension(Extension.builder()
                    .extension(Extension.builder()
                        .url("ombCategory")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("1002-5"))
                            .display(string("American Indian or Alaska Native"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("detailed")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("1735-0"))
                            .display(string("Alaska Native"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("text")
                        .value(string("American Indian or Alaska Native - Alaska Native"))
                        .build())
                    .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race")
                    .build())
                .identifier(Identifier.builder()
                    .system(Uri.of("http://someuri.org"))
                    .value(string("someValue"))
                    .build())
                .name(HumanName.builder()
                    .given(string("John"))
                    .family(string("Doe"))
                    .build())
                .gender(AdministrativeGender.MALE)
                .build();

        FHIRValidator validator = FHIRValidator.validator();
        List<Issue> issues = validator.validate(patient, "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient");

        issues.forEach(System.out::println);

        assertEquals(countWarnings(issues), 1);
        assertEquals(countErrors(issues), 0);
    }

    /**
     * Test the FHIRValidator on a US Core Patient with a US Core Race Extension containing an invalid detailed race code
     */
    @Test
    public void testUSCoreRaceExtension4() throws Exception {
        Patient patient = Patient.builder()
                .extension(Extension.builder()
                    .extension(Extension.builder()
                        .url("ombCategory")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("1002-5"))
                            .display(string("American Indian or Alaska Native"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("detailed")
                        .value(Coding.builder()
                            .system(Uri.of("urn:oid:2.16.840.1.113883.6.238"))
                            .code(Code.of("xxx"))
                            .display(string("Alaska Native"))
                            .build())
                        .build())
                    .extension(Extension.builder()
                        .url("text")
                        .value(string("American Indian or Alaska Native - Alaska Native"))
                        .build())
                    .url("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race")
                    .build())
                .identifier(Identifier.builder()
                    .system(Uri.of("http://someuri.org"))
                    .value(string("someValue"))
                    .build())
                .name(HumanName.builder()
                    .given(string("John"))
                    .family(string("Doe"))
                    .build())
                .gender(AdministrativeGender.MALE)
                .build();

        FHIRValidator validator = FHIRValidator.validator();
        List<Issue> issues = validator.validate(patient, "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient");

        issues.forEach(System.out::println);

        assertEquals(countWarnings(issues), 1);
        assertEquals(countErrors(issues), 2);
        assertEquals(countInformation(issues), 1);
    }

}