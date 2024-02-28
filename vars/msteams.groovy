def upload(auth_file, file_path, file_name, upload_folder) 
{
    bat(label: "running ms_upload.py", script: "python ../scripts/ms_upload.py \"${auth_file}\" \"${file_path}\" \"${file_name}\" \"${upload_folder}\"")
}