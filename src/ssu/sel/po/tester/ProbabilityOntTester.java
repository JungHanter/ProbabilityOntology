package ssu.sel.po.tester;

import org.apache.jena.rdf.model.Model;
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

        Model resultModel = analyzer.analyze(HCTX_ONT_NAMESPACE);
        Model analyzedModel = analyzer.getAnalyzedModel();
        analyzedModel.add(resultModel);

        analyzer.printResult(analyzedModel);
    }
}