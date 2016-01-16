package ssu.sel.po.tester;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import ssu.sel.po.ProbabilityAnalyzer;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 6..
 */
public class ProbabilityOntTester {
    public static final String HEALTH_CONTEXT_ONTOLOGY = "ProbabilityOntology/data/HealthContextAligned.owl";
    public static final String HCTX_ONT_NAMESPACE = "http://soft.ssu.ac.kr/ontology/health_context#";

    public static void main(String[] args) {
        ProbabilityAnalyzer analyzer = new ProbabilityAnalyzer();

        analyzer.addAlignmentSchemaOntology(HEALTH_CONTEXT_ONTOLOGY, HCTX_ONT_NAMESPACE, "hctx", "RDF/XML");
        analyzer.addAlignmentDataOntology(HEALTH_CONTEXT_ONTOLOGY, HCTX_ONT_NAMESPACE, "hctx", "RDF/XML");

        analyzer.addAlignmentDataOntology(makeTestLearningDataset(analyzer.getAlignedSchemaOntModel(), 10),
                HCTX_ONT_NAMESPACE, "hctx");
        analyzer.addAlignmentDataOntology(makeTestAnalysintInputDataset(analyzer.getAlignedSchemaOntModel(), 3),
                HCTX_ONT_NAMESPACE, "hctx");

        Model resultModel = analyzer.analyze(HCTX_ONT_NAMESPACE);
        Model analyzedModel = analyzer.getAnalyzedModel();
        analyzedModel.add(resultModel);

        analyzer.printResult(analyzedModel);
    }

    public static OntModel makeTestAnalysintInputDataset(OntModel schemaModel, int dataSetSize) {
        OntModel dataModel = ModelFactory.createOntologyModel();

        Resource classHealthDataAcquisition = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#HealthDataAcquisition");

        Resource classAge = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Age");
        Resource classGender = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Gender");
        Resource classSystolic = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Systolic");
        Resource classDiastolic = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Diastolic");

        Resource classPerson = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Person");

        ObjectProperty propHasHealthDataAss = schemaModel.getObjectProperty("http://soft.ssu.ac.kr/ontology/health_context#hasHealthDataAssociation");
        ObjectProperty propDataIs = schemaModel.getObjectProperty("http://soft.ssu.ac.kr/ontology/health_context#dataIs");
        ObjectProperty propDataOwner = schemaModel.getObjectProperty("http://soft.ssu.ac.kr/ontology/health_context#dataOwner");

        DatatypeProperty propCategory = schemaModel.getDatatypeProperty("http://soft.ssu.ac.kr/ontology/health_context#category");
        DatatypeProperty propValue = schemaModel.getDatatypeProperty("http://soft.ssu.ac.kr/ontology/health_context#value");

        for(int i=0; i<dataSetSize; i++) {
            int age = randomValue(10, 80);
            String gender = random.nextBoolean()? "MALE" : "FEMALE";
            int systolic = randomValue(70, 190);
            int diastolic = randomValue(40, 115);

            int dataCase = randomValue(0,3);
            //0->all, 1->except age, 2->except gender, 3->except age and gender

            Resource person = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_PS_" + i);

            Resource ageRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_AGE_"+i);
            Resource genRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_GEN_"+i);
            Resource sysRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_SYS_"+i);
            Resource diaRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_DIA_"+i);

            int k = 0;
            Resource ageRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_DL_"+i+"_"+(k++));
            Resource genRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_DL_"+i+"_"+(k++));
            Resource sysRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_DL_"+i+"_"+(k++));
            Resource diaRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "ANAL_DL_"+i+"_"+(k++));

            dataModel.add(person, RDF.type, classPerson);

            if(dataCase==1||dataCase==3) dataModel.add(ageRes, RDF.type, classAge);
            if(dataCase==2||dataCase==3) dataModel.add(genRes, RDF.type, classGender);
            dataModel.add(sysRes, RDF.type, classSystolic);
            dataModel.add(diaRes, RDF.type, classDiastolic);

            if(dataCase==1||dataCase==3) dataModel.add(ageRelship, RDF.type, classHealthDataAcquisition);
            if(dataCase==2||dataCase==3) dataModel.add(genRelship, RDF.type, classHealthDataAcquisition);
            dataModel.add(sysRelship, RDF.type, classHealthDataAcquisition);
            dataModel.add(diaRelship, RDF.type, classHealthDataAcquisition);

            if(dataCase==1||dataCase==3) dataModel.add(ageRes, propValue, dataModel.createTypedLiteral(age));
            if(dataCase==2||dataCase==3) dataModel.add(genRes, propCategory, dataModel.createLiteral(gender));
            dataModel.add(sysRes, propValue, dataModel.createTypedLiteral(systolic));
            dataModel.add(diaRes, propValue, dataModel.createTypedLiteral(diastolic));

            if(dataCase==1||dataCase==3) dataModel.add(ageRelship, propDataOwner, person);
            if(dataCase==2||dataCase==3) dataModel.add(genRelship, propDataOwner, person);
            dataModel.add(sysRelship, propDataOwner, person);
            dataModel.add(diaRelship, propDataOwner, person);
            if(dataCase==1||dataCase==3) dataModel.add(ageRelship, propDataIs, ageRes);
            if(dataCase==2||dataCase==3) dataModel.add(genRelship, propDataIs, genRes);
            dataModel.add(sysRelship, propDataIs, sysRes);
            dataModel.add(diaRelship, propDataIs, diaRes);

            if(dataCase==1||dataCase==3) dataModel.add(person, propHasHealthDataAss, ageRelship);
            if(dataCase==2||dataCase==3) dataModel.add(person, propHasHealthDataAss, genRelship);
            dataModel.add(person, propHasHealthDataAss, sysRelship);
            dataModel.add(person, propHasHealthDataAss, diaRelship);
        }

        return dataModel;
    }

    public static OntModel makeTestLearningDataset(OntModel schemaModel, int dataSetSize) {
        OntModel dataModel = ModelFactory.createOntologyModel();

        Resource classHealthDataLearning = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#HealthDataLearning");
        Resource classHealthContextLearning = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#HealthContextLearning");

        Resource classBloodPressure = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#BloodPressure");
        classBPHypo = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Hypotesion");
        classBPPreHyper = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Prehypertension");
        classBPHyper1 = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#HypertensionStage1");
        classBPHyper2 = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#HypertensionStage2");
        classBPNormal = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Normal");

        Resource classAge = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Age");
        Resource classGender = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Gender");
        Resource classSystolic = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Systolic");
        Resource classDiastolic = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Diastolic");

        Resource classPerson = schemaModel.getResource("http://soft.ssu.ac.kr/ontology/health_context#Person");

        ObjectProperty propHasHealthDataAss = schemaModel.getObjectProperty("http://soft.ssu.ac.kr/ontology/health_context#hasHealthDataAssociation");
        ObjectProperty propHasHealthCtxAss = schemaModel.getObjectProperty("http://soft.ssu.ac.kr/ontology/health_context#hasHealthContextAssociation");
        ObjectProperty propDataIs = schemaModel.getObjectProperty("http://soft.ssu.ac.kr/ontology/health_context#dataIs");
        ObjectProperty propDataOwner = schemaModel.getObjectProperty("http://soft.ssu.ac.kr/ontology/health_context#dataOwner");

        DatatypeProperty propCategory = schemaModel.getDatatypeProperty("http://soft.ssu.ac.kr/ontology/health_context#category");
        DatatypeProperty propValue = schemaModel.getDatatypeProperty("http://soft.ssu.ac.kr/ontology/health_context#value");


        for(int i=0; i<dataSetSize; i++) {
            int age = randomValue(10, 80);
            String gender = random.nextBoolean()? "MALE" : "FEMALE";
            int systolic = randomValue(70, 190);
            int diastolic = randomValue(40, 115);
            Resource bpCtxRes = decideBPCtx(age, gender, systolic, diastolic);
            Resource person = schemaModel.createResource(HCTX_ONT_NAMESPACE + "PS_" + i);

            Resource ageRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "AGE_"+i);
            Resource genRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "GEN_"+i);
            Resource sysRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "SYS_"+i);
            Resource diaRes = schemaModel.createResource(HCTX_ONT_NAMESPACE + "DIA_"+i);

            int k = 0;
            Resource ageRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "DL_"+i+"_"+(k++));
            Resource genRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "DL_"+i+"_"+(k++));
            Resource sysRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "DL_"+i+"_"+(k++));
            Resource diaRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "DL_"+i+"_"+(k++));
            Resource ctxRelship = schemaModel.createResource(HCTX_ONT_NAMESPACE + "CL_"+i+"_"+0);

            dataModel.add(person, RDF.type, classPerson);

            dataModel.add(ageRes, RDF.type, classAge);
            dataModel.add(genRes, RDF.type, classGender);
            dataModel.add(sysRes, RDF.type, classSystolic);
            dataModel.add(diaRes, RDF.type, classDiastolic);

            dataModel.add(ageRelship, RDF.type, classHealthDataLearning);
            dataModel.add(genRelship, RDF.type, classHealthDataLearning);
            dataModel.add(sysRelship, RDF.type, classHealthDataLearning);
            dataModel.add(diaRelship, RDF.type, classHealthDataLearning);
            dataModel.add(ctxRelship, RDF.type, classHealthContextLearning);

            dataModel.add(ageRes, propValue, dataModel.createTypedLiteral(age));
            dataModel.add(genRes, propCategory, dataModel.createLiteral(gender));
            dataModel.add(sysRes, propValue, dataModel.createTypedLiteral(systolic));
            dataModel.add(diaRes, propValue, dataModel.createTypedLiteral(diastolic));

            dataModel.add(ageRelship, propDataOwner, person);
            dataModel.add(genRelship, propDataOwner, person);
            dataModel.add(sysRelship, propDataOwner, person);
            dataModel.add(diaRelship, propDataOwner, person);
            dataModel.add(ctxRelship, propDataOwner, person);
            dataModel.add(ageRelship, propDataIs, ageRes);
            dataModel.add(genRelship, propDataIs, genRes);
            dataModel.add(sysRelship, propDataIs, sysRes);
            dataModel.add(diaRelship, propDataIs, diaRes);
            dataModel.add(ctxRelship, propDataIs, bpCtxRes);

            dataModel.add(person, propHasHealthDataAss, ageRelship);
            dataModel.add(person, propHasHealthDataAss, genRelship);
            dataModel.add(person, propHasHealthDataAss, sysRelship);
            dataModel.add(person, propHasHealthDataAss, diaRelship);
            dataModel.add(person, propHasHealthCtxAss, ctxRelship);
        }

        return dataModel;
    }

    private static Resource classBPHypo = null;
    private static Resource classBPNormal = null;
    private static Resource classBPPreHyper = null;
    private static Resource classBPHyper1 = null;
    private static Resource classBPHyper2 = null;
    public static Resource decideBPCtx(int age, String gender, int systolic, int diastolic) {
        if (age < 13) {
            if (systolic < 75 || diastolic < 50) return classBPHypo;
            else if (systolic < 110 || diastolic < 80) return classBPNormal;
            else if (systolic < 130 || diastolic < 90) return classBPPreHyper;
            else if (systolic < 150 || diastolic < 100) return classBPHyper1;
            else return classBPHyper2;
        } else if (age > 60) {
            if(gender.equals("MALE")) {
                if (systolic < 95 || diastolic < 65) return classBPHypo;
                else if (systolic < 125 || diastolic < 85) return classBPNormal;
                else if (systolic < 145 || diastolic < 95) return classBPPreHyper;
                else if (systolic < 165 || diastolic < 105) return classBPHyper1;
                else return classBPHyper2;
            } else {
                if (systolic < 115 || diastolic < 50) return classBPHypo;
                else if (systolic < 135 || diastolic < 90) return classBPNormal;
                else if (systolic < 145 || diastolic < 100) return classBPPreHyper;
                else if (systolic < 165 || diastolic < 110) return classBPHyper1;
                else return classBPHyper2;
            }
        } else {
            if (systolic < 90 || diastolic < 60) return classBPHypo;
            else if (systolic < 120 || diastolic < 80) return classBPNormal;
            else if (systolic < 140 || diastolic < 90) return classBPPreHyper;
            else if (systolic < 160 || diastolic < 100) return classBPHyper1;
            else return classBPHyper2;
        }
    }

    private static Random random = new Random(System.currentTimeMillis());
    public static int randomValue(int min, int max) {
        return min + random.nextInt(max-min+1);
    }
}