package org.aerogear.android.ags.plugin

import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * This class will download the self-signed certificate and save it in debug/res/raw
 */
class AerogearMobileCoreSelfSignedCertificateHelperTask extends DefaultTask {

    @InputFile @Optional
    public File mcpConfigFile;

    @Input
    String certificateNamePattern = 'aerogear_'

    @Input
    String networkSecurityFileName = 'network_security_config.xml'

    @OutputDirectory
    File resXMlDir;


    AerogearMobileCoreSelfSignedCertificateHelperTask() {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    void action() {
        if (!mcpConfigFile.isFile()) {
            throw new GradleException("File $mcpConfigFile.name is missing. ")
        }

        project.logger.debug("Parsing $mcpConfigFile.path")


        def slurper = new JsonSlurper();

        def aerogear = slurper.parse(mcpConfigFile)

        def hosts = aerogear.services.collect { it.url }

        System.out.println (hosts)

        File configXmlDir = new File(resXMlDir, "xml");
        File configRawDir = new File(resXMlDir, "raw");
        if (!configXmlDir.exists() && !configXmlDir.mkdirs()) {
            throw new GradleException("Failed to create folder: " + configXmlDir);
        }

        if (!configRawDir.exists() && !configRawDir.mkdirs()) {
            throw new GradleException("Failed to create folder: " + configRawDir);
        }


        X509Certificate[] certChain
        def trustManager = [
                checkClientTrusted: { chain, authType -> },
                checkServerTrusted: { chain, authType -> certChain = chain },
                getAcceptedIssuers: { null }
        ] as X509TrustManager

        def context = SSLContext.getInstance("TLS")

        context.init(null, [trustManager] as TrustManager[], null)
        Map<String, String> certificateMap = new HashMap<>();
        hosts.each { String host ->
            try {
                URL aURL = new URL(host);
                int port = aURL.port
                if (port == -1) {
                    port = aURL.defaultPort
                }
                project.logger.info("Loading host : " + host);
                context.socketFactory.createSocket(aURL.host, port).with {
                    startHandshake()
                    close()
                }

                certChain.each { X509Certificate cert ->

                    certificateMap[cert.getSubjectDN().toString()] =
                            "-----BEGIN CERTIFICATE-----\n" +
                                    cert.encoded.encodeBase64(true).toString() +
                                    "-----END CERTIFICATE-----\n"
                }
            } catch (exception) {
                System.err.println("Could not get certificate for " + host)
                System.err.println(exception)
                throw exception
            }

        }

        writeValues(configXmlDir, configRawDir, certificateMap);

    }

    private void writeValues(File configXmlDir, File configRawDir, Map<String, String> certificateMap) {

        def networkSecurityFile = new File(configXmlDir, networkSecurityFileName);
        def writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)

            xml."network-security-config" {
                    "debug-overrides" {
                        "trust-anchors" {
                            certificateMap.keySet().each { String key ->
                                certificates src: "@raw/" + certificateNamePattern + AerogearMobileCoreSelfSignedCertificateHelperTask.escape(key);
                            }
                        }
                    }
                }
            

        certificateMap.keySet().each { String key ->
            def certFile = new File(configRawDir, certificateNamePattern + AerogearMobileCoreSelfSignedCertificateHelperTask.escape(key));
            certFile << certificateMap[key];
        }

        networkSecurityFile << writer.toString()

    }

    private static String escape(def key) {

        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(key);
        while(match.find())
        {
            String s= match.group();
            key=key.replaceAll("\\"+s, "");
        }
        return key.toLowerCase()
    }


}

