# JenkinsSharedLib

A Shared Library to reuse functions between Jenkins Pipelines

## Groups system:

The Swarm and Discord scripts read from a JSON file containing groups.

### Format

The format for these groups look like this:

```
{
	"groups": [
		{
			"name": "<GROUPNAME>",
			"discordID": "<DISCORD_ID>",
			"swarmID": ["<SWARM_ID>", "<SWARM_ID>"],
			"type": "<GROUPTYPE>"
		}
	]
}
```

`<GROUPNAME>` is the name of the group. This could be "PR", "David" or "GENERAL" for example.

`<DISCORD_ID>` is the Discord ID of a "user", "role" or "channel" from the `<GROUPTYPE>`.

`<SWARM_ID>` is the Swarm ID of a user from the `<GROUPTYPE>`.

`<GROUPTYPE>` determines the type of the group. This can be "user", "role" or "channel".

### Example

Here is an example of a groups.json file:

```
{
	"groups": [
		{
			"name": "<ROLE_NAME>",
			"discordID": "<DISCORD_ROLE_ID>",
			"swarmID": ["<SWARM_ID_ONE>", "<SWARM_ID_TWO>"],
			"type": "role"
		},
		{
			"name": "<CHANNEL_NAME>",
			"discordID": "<DISCORD_CHANNEL_ID>",
			"swarmID": ["<SWARM_ID_ONE>", "<SWARM_ID_TWO>", "<SWARM_ID_THREE>"],
			"type": "channel"
		},
		{
			"name": "<USER_NAME>",
			"discordID": "<DISCORD_USER_ID>",
			"swarmID": ["<SWARM_ID>"],
			"type": "user"
		}
	]
}
```

## Scripts:

### log.groovy

Used to log messages to the console

**Functions:**

-  `log(message)` - Log a custom message
-  `log.warning(message)` - Log a warning
-  `log.error(message)` - Log an error
-  `log.currStage()` - Log the current stage

### p4v.groovy

Handles all Perforce related functions

**Functions:**

-  `init(p4credential, p4host, p4workspace, p4viewMapping, cleanForce = true)` - Syncs Perforce workspace (**_Should be called before all other p4v functions!_**)
-  `clean()` - Cleans workspace default changelist (**_Don't use other p4v functions after calling this function!_**)
-  `createTicket()` - Creates a valid ticket for Perforce/Swarm operations
-  `unshelve(id)` - Unshelves a shelved changelist
-  `getChangelistDescr(id)` - Get the description from a changelist
-  `getCurrChangelistDescr()` - Get the description from the current changelist

### swarm.groovy

Allows operations on the swarm server

**Functions:**

-  `init(swarmUser, p4ticket, swarmUrl)` - Initializes swarm data (**_Should be called before all other swarm functions!_**)
-  `clear()` - Clears swarm data (**_Don't use other swarm functions after calling this function!_**)
-  `getParticipantsOfGroup(groupName, group)` - Get participants from a group in a JSON file
-  `getParticipantsOfGroups(groupNames, groups)` - Get participants from multiple groups in a JSON file
-  `createReview(id, participants = null)` - Create a review from a shelved changelist
-  `getReviewID(curlResponse)` - Get the ID of a review
-  `getReviewAuthor(curlResponse)` - Get the author of a review
-  `upVote(id)` - Upvotes a swarm review
-  `downVote(id)` - Downvotes a swarm review
-  `comment(id, comment)` - Comments on a swarm review
-  `needsReview(id)` - Sets the state of a review to "needsReview"
-  `needsRevision(id)` - Sets the state of a review to "needsRevision"
-  `approve(id)` - Approve a review
-  `archive(id)` - Archive a review
-  `reject(id)` - Reject a review
-  `setState(id, state)` - Set a review to a custom state

### ue5.groovy

Handles all Unreal Engine 5 related operations

**Functions**

-  `build(ue5EngineRoot, ue5ProjectName, ue5Project, config, platform, outputDir, blueprintOnly = false, logFile = "${env.WORKSPACE}\\Logs\\UE5Build-${env.BUILD_NUMBER}.txt")` - Build a (blueprintOnly) Unreal Engine 5 project
-  `runAllTests(config = "Development", platform = "Win64")` - Runs all tests defined in an Unreal Engine 5 project
-  `runNamedTests(testNames, config = "Development", platform = "Win64")` - Runs named tests defined in an Unreal Engine 5 project
-  `runFilteredTests(testFilter, config = "Development", platform = "Win64")` - Runs all tests in a filter. Valid filters are: Engine, Smoke, Stress, Perf & Product
-  `runAutomationCommand(testCommand, config = "Development", platform = "Win64")` - Runs an automation command from the Unreal Engine 5 command line
-  `fixupRedirects(platform = "Win64")` - Fixs up all redirects in an Unreal Engine 5 project

### ue4.groovy

Handles all Unreal Engine 4 related operations

**Functions**

-  `build(ue4EngineRoot, ue4ProjectName, ue4Project, config, platform, outputDir, blueprintOnly = false, logFile = "${env.WORKSPACE}\\Logs\\UE4Build-${env.BUILD_NUMBER}.txt")` - Build a (blueprintOnly) Unreal Engine 4 project
-  `runAllTests(config = "Development", platform = "Win64")` - Runs all tests defined in an Unreal Engine 4 project
-  `runNamedTests(testNames, config = "Development", platform = "Win64")` - Runs named tests defined in an Unreal Engine 4 project
-  `runFilteredTests(testFilter, config = "Development", platform = "Win64")` - Runs all tests in a filter. Valid filters are: Engine, Smoke, Stress, Perf & Product
-  `runAutomationCommand(testCommand, config = "Development", platform = "Win64")` - Runs an automation command from the Unreal Engine 4 command line
-  `fixupRedirects(platform = "Win64")` - Fixs up all redirects in an Unreal Engine 4 project

### vs.groovy

Uses MSBuild to compile Visual Studio projects

**Functions:**

-  `build(MSBuildPath, projectPath, config, platform)` - Builds Visual Studio project

### discord.groovy

Handles communication between Jenkins and Discord

**Functions:**

-  `createGroup(groupName, groupDiscordID, groupSwarmID, groupType, groupsList)` - Adds a JSON group object to a list
-  `mentionGroup(groupName, groups)` - Mention a group on discord (**_use with discord.createMessage_**)
-  `mentionGroups(groupNames, groups)` - Mention multiple groups on discord (**_use with discord.createMessage_**)
-  `swarmIDtoDiscordID(swarmID, groups)` - Convert a swarm ID to a discord ID
-  `createMessage(title, messageColor, fields, footer = null, content = null)` - Send a message to discord (**_use with discord.sendMessage_**)
-  `sendMessage(message, webhook)` - Uses cURL to send a message to discord
-  `succeeded(config, platform, webhook)` - Sends build information to discord if the build succeeds
-  `failed(config, platform, webhook)` - Sends build information to discord if the build fails
-  `newReview(id, author, swarmUrl, webhook, buildStatus = "not built", description = null)` - Sends review information to discord when a new review is ready

### zip.groovy

Used to archive files into a zip folder using 7z

**Functions:**

-  `pack(source, archiveName, use7z = true)` - Packs the content of the source folder to <archiveName>.zip (Uses 7z by default)
-  `unpack(archiveName, destination, use7z = true)` - Unpacks the content of a zip file to the destination folder (Uses 7z by default)

### gdrive.groovy

Sends files to Google Drive using cURL

**Functions:**

-  `upload(source, fileName, clientID, clientSecret, refreshToken, parents)` - Uploads files to a folder in Google Drive (Shared Drives are supported)

### sentry.groovy

Tool to diagnose, fix and optimize performance and debug crashes. More information: https://sentry.io/

**Functions:**

-  `upload(sentryCLIPath, authToken, organisation, project, outputFolder)` - Uploads debug symbols to Sentry server

### steam.groovy

Uploads packaged projects to Steam

**Functions:**

-  `init(steamCredential, steamCmdPath)` - Initializes steam data (**_Should be called before all other steam functions!_**)
-  `createDepotManifest(depotID, contentRoot, localPath = "*", depotPath = ".", isRecursive = true, exclude = "*.pdb")` - Creates the depot manifest
-  `createAppManifest(appID, depotID, contentRoot, description = "", isPreview = false, localContentPath = "", branch = "", outputDir= "output")` - Creates app manifest
-  `tryDeploy(appManifest)` - Tries to deploy to Steam using SteamGuard
-  `deploy(appManifest, steamGuard = null)` - Deploy to Steam (**_Prefer using tryDeploy when trying to deploy to Steam!_**)

# Included Scripts/Files:
- Useful python script: `ms_upload.py` used by the msteams.groovy function(s)
- Useful file: `ms_upload.json` used credentials manager in jenkins

### msteams.groovy

Sends files to sharepoint using MS Graph API, you will need to register/create an app through [Azure Portal](https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationsListBlade). Make sure that under *API Permissions* you grant permission to MS Graph Application = `Files.Read.All`, `Files.ReadWrite.All`, `Sites.ReadWrite.All`, `Sites.Selected` and finally under *Certificates & secrets* create a new client secret to use in the .json file. you can find other details such as tenant_id/app_id under the *Overview* tab of your application. Finally your site_name will look something like example: `VerdantGames` in this case it comes from `https://verdantgames.sharepoint.com/sites/VerdantGames`.

**Functions:**
-  `upload(auth_file, file_path, file_name, upload_folder)` - Uploads files to a folder in Microsoft SharePoint

1. **auth_file:**

   -  _Description:_ The jenkins credentials file json provided in the 'scripts/ms_upload.json' as example fill out the details and upload file to jenkins
   -  _Example:_ `'<secret_file credential string example: 1238ds213-asds8a2-dasd8273-sadskjds>'`

2. **file_path:**

   -  _Description:_ The local path of your file to upload.
   -  _Example:_ `'path/to/your/file/to/upload.zip'`
   -  _Note:_ This can be different from the actual local file name.

3. **file_name:**

   -  _Description:_ The name of the file to upload.
   -  _Example:_ `'upload.zip'`

4. **upload_folder:**

   -  _Description:_ The upload directory in ms sharepoint.
   -  _Example:_ `'General/Uploads'`
