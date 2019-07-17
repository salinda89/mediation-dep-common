package com.wso2telco.dep.common.mediation;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.wso2telco.dep.common.mediation.dao.BlacklistDAO;
import com.wso2telco.dep.common.mediation.dto.ApiInformation;
import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.service.BlacklistService;
import com.wso2telco.dep.common.mediation.service.impl.BlacklistServiceImpl;
import com.wso2telco.dep.common.mediation.util.ContextPropertyName;
import com.wso2telco.dep.common.mediation.util.ExceptionType;
import com.wso2telco.dep.common.mediation.util.ErrorConstants;
import framework.logging.LazyLogger;
import framework.logging.LazyLoggerFactory;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;

import java.util.List;

/**
 * Mediator for blacklisted MSISDN validation.
 */
public class MSISDNBlacklistMediator extends AbstractMSISDNMediator {

	private LazyLogger logger = LazyLoggerFactory.getLogger(this.getClass());

	private BlacklistService blacklistService = new BlacklistServiceImpl(new BlacklistDAO());

	/**
	 * {@inheritDoc}
	 *
	 * @param messageContext
	 * @return
	 */
	public boolean mediate(MessageContext messageContext) {

	    CommonMSISDNValidationContextData commonContextData = extractCommonMSISDNValidationContextData(messageContext);
        String loggingMsisdn = commonContextData.isUserAnonymize ? commonContextData.maskedMsisdn
				: commonContextData.msisdn;
		String formattedPhoneNumber = commonContextData.isUserAnonymize ? commonContextData.maskedMsisdnSuffix
				: commonContextData.formattedPhoneNumber != null ? commonContextData.formattedPhoneNumber : null;

		APIService apiService = new APIService();

		try {
			String apiId = apiService.getAPIId(new ApiInformation(commonContextData.apiPublisher,
					commonContextData.apiName, commonContextData.apiVersion));
			List<String> blacklistedMSISDNs = blacklistService.filterBlacklistedMSISDNsByApiId(apiId, Lists.newArrayList(formattedPhoneNumber));
			if (!blacklistedMSISDNs.isEmpty()) {
                logger.info(() -> Joiner.on(" ").join(loggingMsisdn, "is BlackListed number for",
                        commonContextData.apiName, "API", commonContextData.apiVersion, "version"));

				messageContext.setProperty(SynapseConstants.ERROR_CODE, "POL0001:");
				messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, "Internal Server Error. Blacklisted Number");
                messageContext = setErrorInformationToContext(messageContext, ErrorConstants.POL0001, ErrorConstants.POL0001_TEXT,
						Joiner.on(" ").join("Blacklisted Number:", commonContextData.msisdn),
						Integer.toString(HttpStatus.SC_BAD_REQUEST), ExceptionType.POLICY_EXCEPTION.toString());
                messageContext.setProperty("BLACKLISTED_MSISDN", "true");
            } else {
				messageContext.setProperty("BLACKLISTED_MSISDN", "false");
			}
		} catch (Exception e) {
			logger.error("error in MSISDNBlacklistMediator mediate : " + e.getMessage());
			String errorVariable = commonContextData.paramArray != null ? commonContextData.paramArray
					: commonContextData.msisdn;
            messageContext = setErrorInformationToContext(messageContext, ErrorConstants.SVC0001, ErrorConstants.SVC0001_TEXT, errorVariable,
                    Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
            messageContext.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");
        }
		return true;
	}
}
