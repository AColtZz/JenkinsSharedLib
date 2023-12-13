@Library('JenkinsSharedLib@master') _

import groovyx.net.http.RESTClient

def uploadToTeams(source, fileName, siteUrl, libraryName, credentialsId) {
   def client = new RESTClient("https://${siteUrl}")

   withCredentials([string(credentialsId: credentialsId, variable: 'credentials')]) {
      def auth = "${credentials}".getBytes().encodeBase64().toString()

      def fileContent = new File(source).getBytes()

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
