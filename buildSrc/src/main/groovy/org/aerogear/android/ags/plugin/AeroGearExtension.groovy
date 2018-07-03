package org.aerogear.android.ags.plugin

import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.tools.shell.util.Logger
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import java.util.logging.Level

class AeroGearExtension {

    /**
     * Enable downloading and saving certificates from Mobile Core services
     */
    boolean enableCertificateHelper = true

    /**
     * A pattern to prepend to downloaded certificate file names
     */
    String certificateNamePattern = 'aerogear_'

    /**
     * NetworkSecurity file
     */
    String networkSecurityFileName = 'network_security_config.xml'

    @TaskAction
    void action() {
        Logger.log(Level.ALL, "generate values");
    }


}
