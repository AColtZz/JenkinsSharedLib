@Library('JenkinsSharedLib@master') _

import groovyx.net.http.RESTClient
import groovy.util.Base64

def uploadToTeams(source, fileName, siteUrl, libraryName, credentialsId) {
    // Load HTTP Builder classes
    def httpBuilderClass = this.getClass().classLoader.loadClass('groovyx.net.http.RESTClient')
    def base64Class = this.getClass().classLoader.loadClass('groovy.util.Base64')

    def client = httpBuilderClass.newInstance()
    
    withCredentials([string(credentialsId: credentialsId, variable: 'credentials')]) {
        def auth = "${credentials}".getBytes(base64Class).encodeBase64().toString()

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
