package com.athiththan.sample;

import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class CustomMediator extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext synCtx) {

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) synCtx)
                .getAxis2MessageContext();

        /**
         * the request payload will be in the following format 
         * {"post":"WSO2 Class Mediator: Revamp","dev":{"name":"medium"}}
         */

        try {
            JSONObject jsonObject = new JSONObject(JsonUtil.jsonPayloadToString(axis2MessageContext));
            if (jsonObject.has("dev") && jsonObject.getJSONObject("dev").has("name")
                    && "medium".equals((String) jsonObject.getJSONObject("dev").get("name"))) {
                jsonObject.getJSONObject("dev").put("name", "Athiththan");
            }

            JsonUtil.newJsonPayload(axis2MessageContext, jsonObject.toString(), true, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
