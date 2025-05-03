package ssc.adapter;

import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;

import ssc.model.BuildTool;
import ssc.model.BuildToolType;
import ssc.model.Maillon;
import ssc.model.MaillonType;
import ssc.model.ModelFactory;
import ssc.model.RepositoryType;
import ssc.model.SoftwareSupplyChain;
import ssc.model.SourceCodeRepository;

public class GitlabAdapter {
    public static void importGitlab(ModelFactory f,
                                    SoftwareSupplyChain ssc,
                                    String projectId,
                                    String token) throws GitLabApiException {
        GitLabApi git = new GitLabApi("https://gitlab.com", token);

        // Récupère le Maillon Build créé précédemment
        Maillon build = ssc.getMaillons().stream()
            .filter(m -> m.getMaillon_type() == MaillonType.BUILD)
            .findFirst().orElseThrow();

        // Crée le BuildTool
        BuildTool tool = f.createBuildTool();
        tool.setName(BuildToolType.MAVEN);
        tool.setVersion("3.9.0");
        build.getBuildTool().add(tool);

        
        List<Commit> commits = git.getCommitsApi()
        	    .getCommits(projectId, 1, 1);
        
        if (!commits.isEmpty()) {
        	    Commit latest = commits.get(0);
            SourceCodeRepository repo = f.createSourceCodeRepository();
            repo.setRepo_type(RepositoryType.GIT);
            repo.setUrl("https://gitlab.com/mon-projet.git");
            repo.setCommit_hash(commits.get(0).getId());
            build.getRepos().add(repo);
        }
    }
}
