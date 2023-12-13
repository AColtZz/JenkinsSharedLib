def upload(source, fileName, baseUrl, siteUrl, libraryName, clientID, clientSecret) {
    bat(label: "Upload files to Teams", script: """
        SET "AccessToken="
        FOR /f "tokens=1,2 delims=:, " %%U in ('
            curl ^
            --request POST ^
            --data "grant_type=client_credentials&client_id=${clientID}&client_secret=${clientSecret}&resource=https%3A%2F%2F${baseUrl}%2F" ^
            https://login.microsoftonline.com/yourtenant/oauth2/token ^| findstr /i "\"access_token\""
        ') DO SET "AccessToken=,%%~V"
        IF DEFINED AccessToken (SET "AccessToken=%AccessToken:~1%") ELSE (SET "AccessToken=n/a")
        SET AccessToken

        curl -X POST ^
        -H "Authorization: Bearer %AccessToken%" ^
        -H "Accept: application/json;odata=verbose" ^
        -F "file=@${source};type=application/x-zip-compressed" ^
        "https://${siteUrl}/_api/web/lists/getbytitle('${libraryName}')/RootFolder/Files/Add(url='${fileName}.zip',overwrite=true)"
    """)
}
