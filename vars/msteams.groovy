def upload(script, auth_file, file_path, file_name, upload_folder) 
{
    bat(label: "running ${script}", script: "python ${script} \"${auth_file}\" \"${file_path}\" \"${file_name}\" \"${upload_folder}\"")
}