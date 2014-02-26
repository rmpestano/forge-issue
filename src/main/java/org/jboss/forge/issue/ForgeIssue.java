package org.jboss.forge.issue;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jboss.forge.git.GitFacet;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 */
@Alias("issue")
@RequiresFacet(JavaSourceFacet.class)
public class ForgeIssue implements Plugin
{
   @Inject
   private ShellPrompt prompt;

   @Inject
    Project project;

   @DefaultCommand
   public void defaultCommand(@PipeIn String in, PipeOut out)
   {
      out.println("Executed default command.");
   }

   @Command
   public void gitCommand(@PipeIn String in, PipeOut out, @Option String... args)
   {
      if(project.hasFacet(GitFacet.class)){
          RevCommit commit = null;
          try {
              FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();
              Repository repository = repoBuilder.setGitDir(new File(project.getProjectRoot().getChildDirectory(".git").getFullyQualifiedName())).readEnvironment().build();
              RevWalk walk = new RevWalk(repository);
              commit = walk.parseCommit(repository.resolve(Constants.HEAD));
              out.println("printing lastest revision::"+commit) ;
          }catch (Exception e){
             e.printStackTrace();
          }
      }  else{
          out.println("Project:"+project.getProjectRoot() + " is not a git project") ;
      }
   }

   @Command
   public void astCommand(@PipeIn String in, PipeOut out,
                          @Option(name = "source", required = true, type = PromptType.JAVA_CLASS) JavaResource resource
                          ) throws FileNotFoundException {
       JavaSource<?> source = resource.getJavaSource();
       CompilationUnit comp = (CompilationUnit) source.getInternal();
       out.println(source.getQualifiedName() + " AST:"+comp.getAST()) ;
   }
}
