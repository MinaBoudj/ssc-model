package ssc.importer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;                    
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;           
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;      

import ssc.adapter.GitlabAdapter;
import ssc.adapter.SbomAdapter;
import ssc.model.ModelFactory;
import ssc.model.SoftwareSupplyChain;

public class SupplyChainImporter {
    public static void main(String[] args) throws Exception {
        // 1. Initialiser EMF pour gérer les .xmi
        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry()
          .getExtensionToFactoryMap()
          .put("xmi", new XMIResourceFactoryImpl());
        Resource resource = rs.createResource(URI.createFileURI("supplychain.xmi"));

        // 2. Créer la racine du modèle
        ModelFactory f = ModelFactory.eINSTANCE;          // ou SupplyChainFactory.eINSTANCE si c'est votre factory
        SoftwareSupplyChain ssc = f.createSoftwareSupplyChain();
        ssc.setName("MonProjet");
        resource.getContents().add(ssc);

        // 3. Appeler les adaptateurs
        Path sbomPath = Paths.get("target/bom.json");
        SbomAdapter.importSbom(f, ssc, sbomPath);
        GitlabAdapter.importGitlab(f, ssc, "my-project-id", "MY_TOKEN");

        // 4. Sauvegarder en XMI
        resource.save(Collections.emptyMap());
    }
}
