# FIREBASE EXPORTING ALL DATA INTO JSON FILE USING NODE JS
1. Generate the New private Key project settings->service account
2. Open CMd and install [node-firebase-import-export](https://github.com/jloosli/node-firestore-import-export) globally . please make sure that you already have node installed. `npm install -g node-firestore-import-export`.
3. Export full database
   `firestore-export --accountCredentials path/to/credentials/file.json --backupFile /backups/myDatabase.json --prettyPrint`<br/>

4. Export with pretty printing
    `firestore-export --accountCredentials path/to/credentials/file.json --backupFile /backups/myDatabase.json --nodePath collectionA/document1/collectionCC`

## Import
1. `-a, --accountCredentials` <path> - path to Google Cloud account credentials JSON file. If missing, will look at the `GOOGLE_APPLICATION_CREDENTIALS` environment variable for the path.
2.` -b, --backupFile <path>- Filename` with backup data. (e.g. backups/full-backup.json).
3.` -n, --nodePath <path>-` Path to database node to start (e.g. collectionA/docB/collectionC).
4. `-y, --yes -` Unattended import without confirmation (like hitting "y" from the command line).
   
## Import full database
```shell
firestore-import --accountCredentials path/to/credentials/file.json --backupFile /backups/myDatabase.json
```
## Import to a specific path
```shell
firestore-import --accountCredentials path/to/credentials/file.json --backupFile /backups/myDatabase.json --nodePath collectionA/document1/collectionCC
```

### Clear
1. -a, --accountCredentials <path> - path to Google Cloud account credentials JSON file. If missing, will look at the GOOGLE_APPLICATION_CREDENTIALS environment variable for the path.
2. -n, --nodePath <path>- Path to database node to start (e.g. collectionA/docB/collectionC).
3. -y, --yes - Unattended clear without confirmation (like hitting "y" from the command line). Command will wait 5 seconds so you can Ctrl-C to stop.
4. -w, --noWait - Combine this with the --yes confirmation to not wait 5 seconds

Example
**Clear everything under a specific node**
```shell 
firestore-clear --accountCredentials path/to/credentials/file.json --yes
```
# FIREBASE DOWNLOADING ALL IMAGES INTO A  LOCAL COMPUTER
1. download the Cloud CLI and setup your poject id
2. [download](https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe)

[see your cloud bucket](https://console.cloud.google.com/storage/browser?authuser=2&project=firebaseAppName&prefix=)
change firebaseAppName(or select from select project option from top)
with your firebase app name.
```shell
syntax:gsutil cp -r gs://[bucketName]/ [locaDir]
Example1:gsutil cp -r gs://abc/ C:\Users\amol.jadhav\cloud
Example1:gsutil -m cp -r gs://appId.appspot.com/userProfileImages "E:\Images"
```

**Note**: You can also use the Firebase Admin Sdk to run commands on your firebase collections
and convert them in to excel sheet-[here](server.js)