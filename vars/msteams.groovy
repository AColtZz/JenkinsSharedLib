def upload(script, auth_file, file_path, file_name, upload_folder) 
{
    def output = bat(label: "running ${script}", script: "python ${script} \"${auth_file}\" \"${file_path}\" \"${file_name}\" \"${upload_folder}\"")
    
    // Check if the script succeeded
    if (output == 0) {
        // Extract the URL from the output and set it to the environment variable
        def urlStartIndex = output.indexOf("URL:")
        if (urlStartIndex != -1) {
            env.UPLOAD_URL = output.substring(urlStartIndex + 5).trim()
            echo "File uploaded successfully. URL: ${env.UPLOAD_URL}"
        } else {
            echo "Failed to extract URL from the output: ${output}"
            // Handle failure if needed
        }
    }
}