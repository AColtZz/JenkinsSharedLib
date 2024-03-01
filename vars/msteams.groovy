def upload(script, auth_file, file_path, file_name, upload_folder) 
{
    def output = bat(label: "running ${script}", script: "python ${script} \"${auth_file}\" \"${file_path}\" \"${file_name}\" \"${upload_folder}\"")
    
    // Check if the script succeeded
    if (output == 0) {
        // Extract the URL from the output and set it to the environment variable
        env.UPLOAD_URL = output.substring(output.indexOf("URL:")).trim()
        echo "File uploaded successfully. URL: ${env.UPLOAD_URL}"
    }
}