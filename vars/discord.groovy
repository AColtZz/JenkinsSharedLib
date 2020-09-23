import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

def createGroup(users, groupName, groups)
{
   def jsonBuilder = new JsonBuilder()
   jsonBuilder group: ["users": "${users}"]

   def groupJSON = JsonOutput.toJson(group)
   groups.add(groupJSON)
}

def getGroup(groupName, groups)
{
   def jsonSlurper = new JsonSlurper()
   def data = []
   groups.each {
      data.add(jsonSlurper.parseText(jsonText))
   }

   return data[0]
}

def createMessage(title, messageColor, fields, footer = null, content = null)
{
   // Color must be decimal value
   def color = 0 // Transparant

   switch (messageColor) 
   {            
         case "green":
            color = 65280 // Green
            break
         case "yellow":
            color = 16776960 // Yellow
            break
         case "red":
            color = 16711680 // Red
            break
         default: 
            color = 0 // Transparant
            break
   }

   def body = [embeds: 
      [[
      title: title,
      color: color,
      fields: fields
      ]]
   ]
	
   if (footer)
   {
      body.embeds[0].footer = footer
   }

   if (content)
   {
      body.content = content
   }

   return JsonOutput.toJson(body).replace('"','""')
}

def sendMessage(message, webhook)
{
   bat(script: "curl -X POST -H \"Content-Type: application/json\" -d \"${message}\" ${webhook}")
}

def succeeded(config, platform, webhook)
{
   sendMessage(createMessage(":white_check_mark: BUILD SUCCEEDED :white_check_mark:",
                                     "green",
                                     [[name:"${config}(${platform}) ${env.JOB_BASE_NAME} has succeeded", 
                                     value:"Last Changelist: ${env.P4_CHANGELIST}"],
                                     [name:"Job url", 
                                     value:"${env.BUILD_URL}"]],
                                     [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"])
                                 , webhook)
}

def failed(config, platform, webhook)
{
   sendMessage(createMessage(":x: BUILD FAILED :x:",
                                     "red",
                                     [[name:"${config}(${platform}) ${env.JOB_BASE_NAME} has failed", 
                                     value:"Last Changelist: ${env.P4_CHANGELIST}"],
                                     [name:"Job url", 
                                     value:"${env.BUILD_URL}"]],
                                     [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"])
                                 , webhook)
}

def newReview(swarmUrl, webhook, description = null)
{
   sendMessage(createMessage(":warning: NEW REVIEW :warning:",
                                     "yellow",
                                     [[name:"A new review is ready", 
                                     value:"${swarmUrl}/reviews/${env.P4_REVIEW}"]],
                                     [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"],
                                     description)
                                 , webhook)
}