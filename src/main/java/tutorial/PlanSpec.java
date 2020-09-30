package tutorial;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.task.AnyTask;
import com.atlassian.bamboo.specs.builders.task.ArtifactDownloaderTask;
import com.atlassian.bamboo.specs.builders.task.ArtifactItem;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.DownloadItem;
import com.atlassian.bamboo.specs.builders.task.MavenTask;
import com.atlassian.bamboo.specs.builders.task.ScpTask;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.MapBuilder;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {
        //By default credentials are read from the '.credentials' file.
    	
        BambooServer bambooServer = new BambooServer("http://18.237.40.117:8085");
        
        Plan plan = new PlanSpec().createPlan();

        bambooServer.publish(plan);
        
        Deployment myDeployment = new PlanSpec().createDeployment();
        bambooServer.publish(myDeployment);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());

        bambooServer.publish(planPermission);
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Permissions permission = new Permissions()
                .userPermissions("bamboo", PermissionType.ADMIN, PermissionType.CLONE, PermissionType.EDIT)
                //.groupPermissions("bamboo-admin", PermissionType.ADMIN)
                .loggedInUserPermissions(PermissionType.VIEW)
                .anonymousUserPermissionView();
        return new PlanPermissions(planIdentifier.getProjectKey(), planIdentifier.getPlanKey()).permissions(permission);
    }

    Project project() {
        return new Project()
                .name("Project Bamboo123")
                .key("PRJB23");
    }
    Plan createPlan() {
        return new Plan(
                project(),
                "Plan Name", "PLANKEYS1")
        		.linkedRepositories("Petclinic")
        		.triggers(new RepositoryPollingTrigger())
               // .pollEvery(10, TimeUnit.MINUTES));
        		//.triggers(Trigger<repository triggers>)
                .description("Plan created from (enter repository url of your plan)")
                .stages(
                        new Stage("Stage 1")
                                .jobs(new Job("Build and run",  
                                		new BambooKey("RUN"))
                                .artifacts(new Artifact()
                                        .name("petclinic")
                                        .copyPattern("**/*.war")
                                        .location("target")
                                        .shared(true)
                                		.required(true))
                                        .tasks(
                            				
												new VcsCheckoutTask()
													.checkoutItems(new CheckoutItem().defaultRepository()),
													
												new ArtifactDownloaderTask()
				                                    .artifacts(new DownloadItem()
				                                            .allArtifacts(true)),
												new MavenTask()
																	.goal("clean package")
																	.jdk("JDK 1.8")
																	.executableLabel("Maven 3"),
																	
												new ScriptTask().inlineBody("echo Hello world!"),
												
												new AnyTask(new AtlassianModule("ch.mibex.bamboo.sonar4bamboo:sonar4bamboo.maven3task"))
			                                    .configuration(new MapBuilder()
			                                            .put("incrementalFileForInclusionList", "")
			                                            .put("chosenSonarConfigId", "1")
			                                            .put("useGradleWrapper", "")
			                                            .put("sonarMainBranch", "master")
			                                            .put("useNewGradleSonarQubePlugin", "")
			                                            .put("sonarJavaSource", "")
			                                            .put("sonarProjectName", "")
			                                            .put("buildJdk", "JDK")
			                                            .put("gradleWrapperLocation", "")
			                                            .put("sonarLanguage", "")
			                                            .put("sonarSources", "")
			                                            .put("useGlobalSonarServerConfig", "true")
			                                            .put("incrementalMode", "")
			                                            .put("sonarPullRequestAnalysis", "")
			                                            .put("failBuildForBrokenQualityGates", "")
			                                            .put("msbuilddll", "")
			                                            .put("sonarTests", "")
			                                            .put("incrementalNoPullRequest", "incrementalModeFailBuildField")
			                                            .put("failBuildForSonarErrors", "")
			                                            .put("sonarProjectVersion", "")
			                                            .put("sonarBranch", "")
			                                            .put("executable", "maven")
			                                            .put("illegalBranchCharsReplacement", "_")
			                                            .put("failBuildForTaskErrors", "true")
			                                            .put("incrementalModeNotPossible", "incrementalModeRunFullAnalysis")
			                                            .put("sonarJavaTarget", "")
			                                            .put("environmentVariables", "")
			                                            .put("incrementalModeGitBranchPattern", "")
			                                            .put("legacyBranching", "")
			                                            .put("replaceSpecialBranchChars", "")
			                                            .put("additionalProperties", "")
			                                            .put("autoBranch", "true")
			                                            .put("sonarProjectKey", "")
			                                            .put("incrementalModeBambooUser", "")
			                                            .put("overrideSonarBuildConfig", "")
			                                            .put("workingSubDirectory", "")
			                                            .build()) ,
			                                    
			                                    new ScriptTask()
			                                    //.interpreter(ScriptTaskProperties.Interpreter.BINSH_OR_CMDEXE)
			                                    .enabled(false)
			                                    .inlineBody("curl -v -u admin:admin123 --upload-file ${bamboo.path}/petclinic.war http://18.236.232.129:8081/nexus/content/repositories/Bamboo/3/petclinic.war"),
			                                new AnyTask(new AtlassianModule("com.atlassian.bamboo.plugins.tomcat.bamboo-tomcat-plugin:deployAppTask"))
			                                    .configuration(new MapBuilder()
			                                            .put("appVersion", "")
			                                            .put("tomcatUrl", "http://54.202.213.82:8090/manager")
			                                            .put("warFilePath", "/target/petclinic.war")
			                                            .put("tomcatUsername", "admin")
			                                            .put("deploymentTag", "")
			                                            .put("encTomcatPassword", "6SSTqKJm4iU=")
			                                            .put("appContext", "/mypet")
			                                            .put("tomcat6", "")
			                                            .build()) 
			                                    
			                                    
			                                    
			                                  
											
											)));
											//  new ScriptTask().inlineBody("echo Hello world!"))));
											
        
				      
								        
    }
    
     Deployment createDeployment() {
        return new Deployment(new PlanIdentifier("PRJB23", "PLANKEYS1"),
                    "myDeployment")
//        		.releaseNaming(new ReleaseNaming("release-1.1")
//        		        .autoIncrement(true))
//        		    .environments(new Environment("QA")
//        		        .tasks(new ArtifactDownloaderTask()
//        		            .artifacts(new DownloadItem()
//        		                .allArtifacts(true)), new ScriptTask()
//        		            .inlineBody("echo hello"), new ScpTask()
//        		            .host("myserver")
//        		            .username("admin")
//        		            .authenticateWithPassword("admin")
//        		            .fromArtifact(new ArtifactItem()
//        		                .allArtifacts())
//        		            .toRemotePath("/remote-dir")));
        				
        
        		
                  .releaseNaming(new ReleaseNaming("release-1.1")
                            .autoIncrement(true))
                    .environments(new Environment("Development")
                            .tasks(
                            		new ArtifactDownloaderTask()
                            	            .artifacts(new DownloadItem()
                            	                .allArtifacts(true)),                            		
                          
								new AnyTask(new AtlassianModule("com.atlassian.bamboo.plugins.tomcat.bamboo-tomcat-plugin:deployAppTask"))
                                    .configuration(new MapBuilder()
                                           .put("appVersion", "")
                                            .put("tomcatUrl", "http://54.202.213.82:8090/manager")
                                            .put("warFilePath", "/petclinic.war")
                                          .put("tomcatUsername", "admin")
                                            .put("deploymentTag", "")
                                           .put("encTomcatPassword", "6SSTqKJm4iU=")
                                      .put("appContext", "/mypet")
                                            .put("tomcat6", "")
                                            .build()) 
								
                            		));
       			
    }
    				
    
}
