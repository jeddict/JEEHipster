/**
 * Copyright [2017] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jcode.cloud.generator;

import java.io.IOException;
import java.util.Map;
import org.jcode.docker.generator.DockerGenerator;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.FileUtil.expandTemplate;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Gaurav Gupta
 */
//@ServiceProvider(service = Generator.class)
//@Technology(label = "Cloud", panel = CloudConfigPanel.class, tabIndex = 2, sibling = {DockerGenerator.class})
public final class CloudGenerator extends DockerGenerator implements Generator {

    private static final String TEMPLATE = "org/jcode/cloud/template/";

    @ConfigData
    private CloudConfigData cloudConfig;

    @Override
    public void execute() throws IOException {
        if (!appConfigData.isCompleteApplication()) {
            return;
        }
        generateKubernetes();
    }
    
    private void generateKubernetes(){
        if (config.isDockerActivated() && cloudConfig.getKubernetesConfigData().isEnabled()) {
            try {
                handler.info("Kubernetes", "You can deploy all your apps by running:\n"
                        + "\t\t "+ Console.wrap("kubectl apply -f namespace.yml", BOLD)+"\n"
                        + "\t\t "+ Console.wrap("kubectl apply -f "+getApplicationName(), BOLD)+"\n"
                        + "\n\t\t"
                        + "Use these commands to find your application's IP addresses:\n"
                        + "\t\t "+ Console.wrap("kubectl get svc " + getApplicationName(), BOLD));

                handler.progress(Console.wrap(CloudGenerator.class, "MSG_Progress_Kubernetes_Generating", FG_RED, BOLD, UNDERLINE));
                FileObject targetFolder = project.getProjectDirectory().getFileObject("k8s");
                if(targetFolder == null){
                    targetFolder = project.getProjectDirectory().createFolder("k8s");
                }
                Map<String, Object> params = getParams();
                params.put("NAMESPACE", cloudConfig.getKubernetesConfigData().getNamespace());
                params.put("DOCKER_IMAGE", config.getDockerNamespace().replace("${project.groupId}", getPOMManager().getGroupId()) 
                        + "/" + getApplicationName()
                        + ":"  + getPOMManager().getVersion());
                params.put("APP_NAME", getApplicationName());

                handler.progress(expandTemplate(TEMPLATE + "kubernetes/db/_" + config.getDatabaseType().name().toLowerCase() + ".yml.ftl", targetFolder, getApplicationName()+"_"+config.getDatabaseType().name().toLowerCase()+".yml", params));
                handler.progress(expandTemplate(TEMPLATE + "kubernetes/_deployment.yml.ftl", targetFolder, getApplicationName()+"_deployment.yml", params));
                handler.progress(expandTemplate(TEMPLATE + "kubernetes/_service.yml.ftl", targetFolder, getApplicationName()+"_service.yml", params));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
