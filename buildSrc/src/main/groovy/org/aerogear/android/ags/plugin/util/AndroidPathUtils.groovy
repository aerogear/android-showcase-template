/*
 * Copyright 2017 Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aerogear.android.ags.plugin.util


import org.aerogear.android.ags.plugin.AerogearMobileCoreSelfSignedCertificateHelperTask
import org.aerogear.android.ags.plugin.AeroGearExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

class AndroidPathUtils {


    static void handleVariant(Project project,
                              def variant,
                              AeroGearExtension mcpExtension) {


        File mcpConfigFile = null

        String variantName = "$variant.dirName";
        String[] variantTokens = variantName.split('/')

        List<String> fileLocation = new ArrayList<>()
        System.out.println((variantName));
        if (variantTokens.length == 2) {
            // If flavor and buildType are found.
            String flavorName = variantTokens[0]
            String buildType = variantTokens[1]
            fileLocation.add('src/' + flavorName + '/' + buildType)
            fileLocation.add('src/' + buildType + '/' + flavorName)
            fileLocation.add('src/' + flavorName)
            fileLocation.add('src/' + buildType)
        } else if (variantTokens.length == 1) {
            // If only buildType is found.
            fileLocation.add('src/' + variantTokens[0])
        }

        fileLocation.add('src/main')

        String searchedLocation = System.lineSeparator()
        for (String location : fileLocation) {

            File jsonFile = project.file(location +  '/assets/' + Constants.JSON_FILE_NAME)
            searchedLocation = searchedLocation + jsonFile.getPath() + System.lineSeparator()
            if (jsonFile.isFile()) {
                mcpConfigFile = jsonFile
                break
            }
        }

        if (mcpConfigFile == null) {
            mcpConfigFile = project.file(Constants.JSON_FILE_NAME)
            searchedLocation = searchedLocation + mcpConfigFile.getPath()
        }

        File mcpServicesDir =
                project.file("$project.buildDir/generated/res/mcpServices/$variant.dirName")

        if (mcpServicesDir.exists()) {
            mcpServicesDir.deleteDir()
        }

        if (!mcpServicesDir.mkdirs()) {
            throw new GradleException("Could not create $mcpServicesDir.path")
        }

        AerogearMobileCoreSelfSignedCertificateHelperTask selfSignedConfigTask = project.tasks
                .create("configuredCertificatesFor${variant.name.capitalize()}FeedHenryMCP",
                AerogearMobileCoreSelfSignedCertificateHelperTask)
        selfSignedConfigTask.mcpConfigFile = mcpConfigFile
        selfSignedConfigTask.certificateNamePattern = mcpExtension.certificateNamePattern;
        selfSignedConfigTask.networkSecurityFileName = mcpExtension.networkSecurityFileName
        selfSignedConfigTask.resXMlDir = mcpServicesDir
        variant.registerResGeneratingTask(selfSignedConfigTask, mcpServicesDir)



    }
}
