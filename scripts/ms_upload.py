import os
import sys
import json
try:
    import requests
    from tqdm import tqdm
except ImportError:
    print("Some required libraries are missing. Installing them now...")
    
    # Attempt to install requests
    try:
        import pip
        pip.main(['install', 'requests'])
        import requests
    except ImportError:
        sys.exit("Failed to install 'requests'. Please install it manually using 'pip install requests'.")

    # Attempt to install tqdm
    try:
        pip.main(['install', 'tqdm'])
        from tqdm import tqdm
    except ImportError:
        sys.exit("Failed to install 'tqdm'. Please install it manually using 'pip install tqdm'.")

# Upload a file to the SharePoint document library using the Microsoft Graph API
auth_file = sys.argv[1] #Custom Secret Auth File
file_path = sys.argv[1] #local file path
file_name = sys.argv[2] #filename
folder_name = sys.argv[3] #drive folder path/name

tenant_id = json.loads(auth_file).get('tenant_id')
client_id = json.loads(auth_file).get('client_id')
client_secret = json.loads(auth_file).get('client_secret')
site_name = json.loads(auth_file).get('site_name')

# Authenticate and get an access token
def authenticate_graph_api():
    # auth = open(AUTHFILE, "r").read()
    auth_url = f'https://login.microsoftonline.com/{tenant_id}/oauth2/v2.0/token'
    data = {
        'grant_type': 'client_credentials',
        'client_id': client_id,
        'client_secret': client_secret,
        'scope': 'https://graph.microsoft.com/.default'
    }
    response = requests.post(auth_url, data=data)

    # Check the response status code
    if response.status_code == 200:
        # Parse and print the JSON response
        json_response = response.json()
        access_token = json_response['access_token']
        print(f"Access token retrieved successfully!")
        #print(f"Response:\n{json_response}")
        return access_token
    else:
        print(f"\nFailed to retrieve access token. Status code: {response.status_code}")
        sys.exit(1)

# GET Request for the site_id
def get_site_id(access_token):
    headers = {
        'Authorization': f'Bearer {access_token}',
        'Accept': 'application/json',
    }
    site_url = f'https://graph.microsoft.com/v1.0/sites/root:/sites/{site_name}/'
    response = requests.get(site_url, headers=headers)

    # Check the response status code
    if response.status_code == 200:
        # Parse and print the JSON response
        json_response = response.json()
        site_id = json_response['id'].split(",",2)[1]
        print(f"Site ID retrieved successfully!")
        #print(f"Response:\n{json_response}")
        print(f"Site ID: {site_id}")
        return site_id
    else:
        print(f"\nFailed to retrieve site information. Status code: {response.status_code}")
        sys.exit(1)

# GET Request for the drive_id
def get_drive_id(access_token, site_id):    
    headers = {
        'Authorization': f'Bearer {access_token}',
        'Accept': 'application/json',
    }
    drive_url = f'https://graph.microsoft.com/v1.0/sites/{site_id}/drives/'
    response = requests.get(drive_url, headers=headers)

    # Check the response status code
    if response.status_code == 200:
        # Parse and print the JSON response
        json_response = response.json()
        drive_id = json_response['value'][0]['id']
        print(f"Drive ID retrieved successfully!")
        #print(f"Response:\n{json_response}")
        print(f"Drive ID: {drive_id}")
        return drive_id
    else:
        print(f"\nFailed to retrieve drive information. Status code: {response.status_code}")
        sys.exit(1)

# PUT Upload to the drive according to documentation (https://learn.microsoft.com/en-us/graph/api/driveitem-createuploadsession?view=graph-rest-1.0)
def upload_to_drive(access_token, site_id, drive_id):
    # Set headers for the initial request
    headers = {
        'Authorization': f'Bearer {access_token}',
        'Content-Type': 'application/octet-stream',
    }

    # Initialize the upload session
    upload_session_url = f'https://graph.microsoft.com/v1.0/sites/{site_id}/drives/{drive_id}/items/root:/{folder_name}/{file_name}:/createUploadSession'
    response = requests.post(upload_session_url, headers=headers)

    if response.status_code != 200:
        print(f"\nFailed to create upload session. Status code: {response.status_code}\n")
        print(f"Response: {response.text}")
        sys.exit(1)

    # Parse the JSON response
    session_data = response.json()
    upload_url = session_data['uploadUrl']

    # Set up file information
    file_size = os.path.getsize(file_path)
    max_chunk_size = 60 * 320 * 1024  # 60 MiB

    # Calculate the chunk size based on the requirements
    chunk_size = min(max_chunk_size, file_size)
    chunk_size = (chunk_size // 320) * 320  # Ensure it's a multiple of 320 KiB

    next_expected_range = session_data.get('nextExpectedRanges')[0]

    # Upload chunks until there are no more expected ranges
    with tqdm(total=file_size, unit='B', unit_scale=True, desc="Uploading", dynamic_ncols=True) as pbar:
        with open(file_path, 'rb') as file:
            while next_expected_range:
                start_byte = int(next_expected_range.split('-')[0])
                
                # Read the chunk from the file, ensuring not to read beyond the end of the file
                chunk = file.read(chunk_size)

                # Set headers for the chunk upload
                headers['Content-Length'] = str(len(chunk))
                headers['Content-Range'] = f'bytes {start_byte}-{start_byte + len(chunk) - 1}/{file_size}'

                # print(f" Uploading bytes: {start_byte}-{start_byte + len(chunk) - 1}")
                response = requests.put(upload_url, headers=headers, data=chunk, stream=True, timeout=60)

                pbar.update(len(chunk))  # Update progress bar for each chunk

                if response.status_code == 200 or response.status_code == 201:
                    # File uploaded successfully
                    print(f"\nFile uploaded.")
                    print(f"URL: {response.json().get('webUrl')}")
                    sys.exit(0)

                # Check the response status code
                if response.status_code != 202:
                    print(f"Failed to upload file. Status code: {response.status_code}")
                    print(f"Response: {response.text}")
                    sys.exit(1)

                # Parse the new session data from the response
                session_data = response.json()
                next_expected_range = session_data.get('nextExpectedRanges')[0]

def main():
    access_token = authenticate_graph_api()
    site_id = get_site_id(access_token)
    drive_id = get_drive_id(access_token, site_id)
    upload_to_drive(access_token, site_id, drive_id)
    
if __name__ == '__main__':
    main()