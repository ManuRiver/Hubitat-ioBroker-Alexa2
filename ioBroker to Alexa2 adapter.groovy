/*
 * Hubitat GET to IObroker to Alexa2
 * reference on how install ioBroker https://github.com/ioBroker/ioBroker/blob/master/doc/INSTALL.md
 * reference to configure iobroker Alexa2 adapter by Appollon77:  https://github.com/Apollon77/ioBroker.alexa2/blob/master/README.md
 * 
 * 
 */
metadata {
    definition(name: "ioBroker to Alexa2 adapter", namespace: "iobroker", author: "manuriver", importUrl: "") {
        capability "Actuator"
        capability "Switch"
        capability "Sensor"
        capability "SpeechSynthesis"
        
        command "speakVolume", [[name: "volume*", type: "INTEGER", description: "set speak volume level", range: "1..100"]]
        command "generalVolume", [[name: "volume*", type: "INTEGER", description: "set general volume level", range: "1..100"]]
        
    }
}

preferences {
    section("ioBroker") {
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true    
        input "ioBrokerAddress", "text", title: "ioBroker Address", description: "(ie. http://192.168.30.4)", required: true
        input "ioBrokerPort", "text", title: "ioBroker Port", description: "IP port chosen forSimple RESTful API, ie. 8082", required: true
        input "ioBrokerAlexaDevice", "text", title: "ioBroker Alexa2 API", description: "use this format: alexa2.0.Echo-Devices.Serialnumber", required: true
        
        
        
        
        
    }
}

def logsOff() {
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable", [value: "false", type: "bool"])
}

def updated() {
    log.info "updated..."
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
}

def parse(String description) {
    if (logEnable) log.debug(description)
}

def on() 
{

        
    log.info "${device} sent enable doNotDisturb command : **${device.currentValue("switch")}**"   
    httpGet("${ioBrokerAddress}:${ioBrokerPort}/set/${ioBrokerAlexaDevice}.Commands.doNotDisturb?value=1")
         { resp ->
          def respData = resp.data
          log.debug respData
         } 
     sendEvent(name: "switch", value: "on", isStateChange: true)    
}

def off() 
{

        
    log.info "${device} sent disable doNotDisturb command : **${device.currentValue("switch")}**"   
    httpGet("${ioBrokerAddress}:${ioBrokerPort}/set/${ioBrokerAlexaDevice}.Commands.doNotDisturb?value=0")
         { resp ->
          def respData = resp.data
          log.debug respData
         } 
     sendEvent(name: "switch", value: "off", isStateChange: true)    
}

def speak(text)
{

    log.info "${device} sent text content to speak command : **${text}**"   
    httpGet(("${ioBrokerAddress}:${ioBrokerPort}/set/${ioBrokerAlexaDevice}.Commands.speak?value=")+ URLEncoder.encode(text, "UTF-8").replaceAll(/\+/, "%20"))
         { resp ->
          def respData = resp.data
          log.debug respData
         }    
    
}

void speakVolume(volume)
{

      log.info "${device} sent speak volume level command : **${volume}**"   
    httpGet("${ioBrokerAddress}:${ioBrokerPort}/set/${ioBrokerAlexaDevice}.Commands.speak-volume?value=${volume}")
       { resp ->
       def respData = resp.data
       log.debug respData
       }    
    sendEvent(name: "SpeakVolume", value: "${volume}", unit:"%", isStateChange: true)
}

void generalVolume(volume)
{

      log.info "${device} sent speak volume level command : **${volume}**"   
    httpGet("${ioBrokerAddress}:${ioBrokerPort}/set/${ioBrokerAlexaDevice}.Player.volume?value=${volume}")
       { resp ->
       def respData = resp.data
       log.debug respData
       }    
    sendEvent(name: "GeneralVolume", value: "${volume}", unit:"%", isStateChange: true)
}