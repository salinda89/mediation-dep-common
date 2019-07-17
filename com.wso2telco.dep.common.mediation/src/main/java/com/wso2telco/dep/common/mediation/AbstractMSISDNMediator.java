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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract mediator to cater common functionality of black list and white list mediators.
 */
public abstract class AbstractMSISDNMediator extends AbstractCommonMediator {

    /**
     * Extract some common set of data to process msisdn operation related mediators
     *
     * @param messageContext
     * @return
     */
    protected CommonMSISDNValidationContextData extractCommonMSISDNValidationContextData(MessageContext messageContext) {

        String regexPattern = (String) messageContext.getProperty("msisdnRegex");
        String regexGroupNumber = (String) messageContext.getProperty("msisdnRegexGroup");
        String msisdn = (String) messageContext.getProperty("paramValue");

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(msisdn);

        String formattedPhoneNumber = null;
        if (matcher.matches()) {
            formattedPhoneNumber = matcher.group(Integer.parseInt(regexGroupNumber));
        }

        return new CommonMSISDNValidationContextData(msisdn,
                (String) messageContext.getProperty("paramArray"),
                (String) messageContext.getProperty("MASKED_MSISDN"),
                (String) messageContext.getProperty("MASKED_MSISDN_SUFFIX"),
                (String) messageContext.getProperty("API_NAME"),
                (String) messageContext.getProperty("VERSION"),
                (String) messageContext.getProperty("API_PUBLISHER"), formattedPhoneNumber,
                Boolean.parseBoolean((String) messageContext.getProperty("USER_ANONYMIZATION")));
    }

    @Override
    protected MessageContext setErrorInformationToContext(MessageContext messageContext, String messageId,
                                                          String errorText, String errorVariable, String httpStatusCode,
                                                          String exceptionType) {
        /**
         * In MSISDN validation related mediators(blacklist, whitelist), need to set the {@link ContextPropertyName.MEDIATION_ERROR_TEXT}
         * Not the {@link ContextPropertyName.ERROR_TEXT}. because in the sequence file, concatenate some other stuff to the
         * mediation error text and insert it as {@link ContextPropertyName.ERROR_TEXT}
         */
        messageContext.setProperty(ContextPropertyName.MEDIATION_ERROR_TEXT, errorText);
        //Passing {@link ContextPropertyName.ERROR_TEXT} as null since it should not be set in this level
        return super.setErrorInformationToContext(messageContext, messageId, null, errorVariable, httpStatusCode, exceptionType);
    }

    protected class CommonMSISDNValidationContextData {

        String msisdn;
        String paramArray;
        String maskedMsisdn;
        String maskedMsisdnSuffix;
        String apiName;
        String apiVersion;
        String apiPublisher;
        String formattedPhoneNumber;
        boolean isUserAnonymize;

        public CommonMSISDNValidationContextData(String msisdn, String paramArray, String maskedMsisdn, String maskedMsisdnSuffix,
                                                 String apiName, String apiVersion, String apiPublisher, String formattedPhoneNumber,
                                                 boolean isUserAnonymize) {
            this.msisdn = msisdn;
            this.paramArray = paramArray;
            this.maskedMsisdn = maskedMsisdn;
            this.maskedMsisdnSuffix = maskedMsisdnSuffix;
            this.apiName = apiName;
            this.apiVersion = apiVersion;
            this.apiPublisher = apiPublisher;
            this.formattedPhoneNumber = formattedPhoneNumber;
            this.isUserAnonymize = isUserAnonymize;
        }
    }
}
