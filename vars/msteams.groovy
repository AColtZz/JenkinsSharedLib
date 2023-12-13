@Library('JenkinsSharedLib@master') _

import jenkins.model.Jenkins
import groovyx.net.http.RESTClient
import groovy.util.Base64

// Load the HTTP Builder library
def httpBuilderLib = Jenkins.instance.pluginManager.uberClassLoader.loadClass('groovyx.net.http.RESTClient')
def base64Lib = Jenkins.instance.pluginManager.uberClassLoader.loadClass('groovy.util.Base64')

def uploadToTeams(source, fileName, siteUrl, libraryName, credentialsId) {
    def client = new httpBuilderLib("https://${siteUrl}")

    withCredentials([string(credentialsId: credentialsId, variable: 'credentials')]) {
        def auth = "${credentials}".getBytes(base64Lib).encodeBase64().toString()

        def fileContent = new File(source).bytes

        def response = client.post(
            path: "/_api/web/lists/getbytitle('${libraryName}')/RootFolder/Files/Add(url='${fileName}.zip',overwrite=true)",
            headers: [
                'Authorization': "Basic ${auth}",
                'Accept': 'application/json;odata=verbose',
                'X-RequestDigest': 'FormDigestValue'
            ],
            body: [
                "length": fileContent.length,
                "content": fileContent
            ]
        )

        if (response.status == 200) {
            println "File uploaded successfully."
        } else {
            println "Error uploading file: ${response.status} - ${response.data}"
        }
    }
}
