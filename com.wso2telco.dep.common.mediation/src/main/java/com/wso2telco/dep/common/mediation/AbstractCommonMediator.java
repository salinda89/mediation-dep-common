/**
 * Copyright (c) 2019, APIGATE PVT (Ltd). (https://www.apigate.com/) All Rights Reserved.
 *
 * APIGATE Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.util.ContextPropertyName;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

/**
 * Abstract mediator class to hold common logic in mediators of the mediation module
 */
public abstract class AbstractCommonMediator extends AbstractMediator {

    /**
     * Set error information to the given message context
     *
     * @param messageContext
     * @param messageId
     * @param errorText
     * @param errorVariable
     * @param httpStatusCode
     * @param exceptionType
     * @return
     */
    protected MessageContext setErrorInformationToContext(MessageContext messageContext, String messageId,
                                                          String errorText, String errorVariable, String httpStatusCode,
                                                          String exceptionType) {

        messageContext.setProperty(ContextPropertyName.MESSAGE_ID, messageId);
        messageContext.setProperty(ContextPropertyName.ERROR_TEXT, errorText);
        messageContext.setProperty(ContextPropertyName.ERROR_VARIABLE, errorVariable);
        messageContext.setProperty(ContextPropertyName.HTTP_STATUS_CODE, httpStatusCode);
        messageContext.setProperty(ContextPropertyName.EXCEPTION_TYPE, exceptionType);

        return messageContext;
    }
}
