package ssc.adapter;

import java.io.IOException;

import org.cyclonedx.parsers.JsonParser;
import org.cyclonedx.model.Bom;
import java.nio.file.Path;


import org.cyclonedx.exception.ParseException;

import ssc.model.Artifact;
import ssc.model.ArtifactType;
import ssc.model.Component;
import ssc.model.Dependency;
import ssc.model.Maillon;
import ssc.model.MaillonType;
import ssc.model.ModelFactory;
import ssc.model.SBOM;
import ssc.model.SBOMFormat;
import ssc.model.SoftwareSupplyChain;

public class SbomAdapter {
    public static void importSbom(ModelFactory f,
                                  SoftwareSupplyChain ssc,
                                  Path sbomPath) throws IOException, ParseException {
        // Utilise le parser CycloneDX
        JsonParser parser = new JsonParser();
        Bom bom = parser.parse(sbomPath.toFile());

        // Trouve (ou crée) le Maillon Build
        Maillon build = f.createMaillon();
        build.setMaillon_type(MaillonType.BUILD);
        ssc.getMaillons().add(build);

        // Crée l’Artifact lié à cette SBOM
        Artifact art = f.createArtifact();
        art.setArtifact_type(ArtifactType.JAR);
        build.getArtifact().add(art);

        // Génère la SBOM dans le modèle
        SBOM modelBom = f.createSBOM();
        modelBom.setFormat(SBOMFormat.CYCLONEDX);
        build.getGenerates().add(modelBom);

        // Pour chaque composant de la SBOM
        bom.getComponents().forEach(cdxComp -> {
            // 1. Créer le Component EMF
            Component comp = f.createComponent();
            comp.setName(cdxComp.getName());
            comp.setVersion(cdxComp.getVersion());
            modelBom.getComponents().add(comp);

            // 2. Créer une Dependency et l’ajouter à l’Artifact
            Dependency dep = f.createDependency();
            dep.setScope("compile");
            dep.setDependency(art); // rattache au bon Artifact (important pour EMF)
            art.getDependsOn().add(dep);
        });

    }
}
