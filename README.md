# Revamp Mediator

A custom implementation to convert the attribute values of payloads using WSO2 Class Mediator extensions.

[Medium Story](https://medium.com/@athiththan11/wso2-class-mediators-convert-payloads-946a4fc5aa4a)

Simply put, the mediation logic will do the following conversion

```json
// original payload

{
    "post": "WSO2 Class Mediator: Revamp",
    "dev": {
        "name": "medium"
    }
}
```

to

```json
// converted

{
    "post": "WSO2 Class Mediator: Revamp",
    "dev": {
        "name": "Athiththan"
    }
}
```

## Build

Build the project by running ...

```shell
mvn clean package
```

## Deploy

After a successful build, copy the `revamp-mediator-1.0.0.jar` artifact from the `target` folder and paste it inside `<API-M HOME>/repository/components/lib` folder. And specify the deployed class mediator inside your required synapse configuration file.

For this demo, I will be using the WSO2 API Manager (2.6.0) and I will choose the `_TokenAPI_.xml` from the API Synapse Configuraitons folder (You can find your `API Synapse Configurtions` inside the `<API-M HOME>/repository/deployment/server/synapse-configs/default/api` folder), which will look as follows ...

```xml
<api xmlns="http://ws.apache.org/ns/synapse" name="_WSO2AMTokenAPI_" context="/token">
    <resource methods="POST" url-mapping="/*" faultSequence="_token_fault_">
        <inSequence>
            <property name="uri.var.portnum" expression="get-property('keyManager.port')"/>
            <property name="uri.var.hostname" expression="get-property('keyManager.hostname')"/>
            <send>
                <endpoint>
                    <http uri-template="https://{uri.var.hostname}:{uri.var.portnum}/oauth2/token">
                        <timeout>
                            <duration>60000</duration>
                            <responseAction>fault</responseAction>
                        </timeout>
                    </http>
                </endpoint>
            </send>
        </inSequence>
        <outSequence>
            <send/>
        </outSequence>
    </resource>
    <handlers>
        <handler class="org.wso2.carbon.apimgt.gateway.handlers.ext.APIManagerCacheExtensionHandler"/>
        <handler class="org.wso2.carbon.apimgt.gateway.handlers.common.SynapsePropertiesHandler"/>
    </handlers>
</api>
```

Add our custom mediator inside the `inSequence` section, which will result in ...

```xml
    <inSequence>
        <!-- <property name="uri.var.portnum" expression="get-property('keyManager.port')"/>
        <property name="uri.var.hostname" expression="get-property('keyManager.hostname')"/>
        <send>
            <endpoint>
                <http uri-template="https://{uri.var.hostname}:{uri.var.portnum}/oauth2/token">
                    <timeout>
                        <duration>60000</duration>
                        <responseAction>fault</responseAction>
                    </timeout>
                </http>
            </endpoint>
        </send> -->

        <!-- convert dev.name to athiththan | revamp-mediator-1.0.0.jar -->
        <class name="com.athiththan.sample.CustomMediator" />
        <respond />
    </inSequence>
```

## Run

Start your WSO2 API Manager server by executing the command from your `<API-M HOME>/bin` folder

```shell
sh wso2server.sh
```

or

```shell
wso2am-2.6.0
```

## Test & Results

Use the following cUrl command to invoke the `token` endpoint to examine the mediation logic

```cUrl
curl -k --header "Content-Type: application/json" --request POST --data '{"post":"WSO2 Class Mediator: Revamp","dev":{"name":"medium"}}' https://<YOUR IP ADDRESS>/token
```

After a successful execution, You will find the results as follows

```json
{"post":"WSO2 Class Mediator: Revamp","dev":{"name":"Athiththan"}}
```

