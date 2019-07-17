package com.wso2telco.dep.common.mediation;

import com.google.common.base.Joiner;
import com.wso2telco.dep.common.mediation.dto.ApiInformation;
import com.wso2telco.dep.common.mediation.service.APIService;
import framework.logging.LazyLogger;
import framework.logging.LazyLoggerFactory;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;

/**
 * Mediator for whitelist msisdns
 */
public class MSISDNWhitelistMediator extends AbstractMSISDNMediator {

	private final LazyLogger logger = LazyLoggerFactory.getLogger(this.getClass());

	private final APIService apiService = new APIService();

	/**
	 * {@inheritDoc}
	 *
	 * @param messageContext
	 * @return
	 */
	public boolean mediate(MessageContext messageContext) {

		CommonMSISDNValidationContextData commonContextData = extractCommonMSISDNValidationContextData(messageContext);
		String appID = messageContext.getProperty("api.ut.application.id").toString();

		String formattedPhoneNumber;
		String loggingMsisdn;
		if (commonContextData.isUserAnonymize) {
			formattedPhoneNumber = commonContextData.maskedMsisdnSuffix;
			loggingMsisdn = commonContextData.maskedMsisdn;
		} else {
			formattedPhoneNumber = commonContextData.formattedPhoneNumber;
			loggingMsisdn = commonContextData.msisdn;
		}

		try {
			String apiID = apiService.getAPIId(new ApiInformation(commonContextData.apiPublisher,
					commonContextData.apiName, commonContextData.apiVersion));
			String subscriptionID = apiService.getSubscriptionID(apiID, appID);
			logger.debug(() -> "WhiteListHandler subscription id:" + subscriptionID);

			//TODO APIService#isWhiteListed() call is inefficient. refactor code
			if (apiService.isWhiteListed(formattedPhoneNumber, appID, subscriptionID, apiID)) {
				messageContext.setProperty("WHITELISTED_MSISDN", "true");
				logger.debug(() -> Joiner.on(" ").join(loggingMsisdn, "is whitelisted for AppId: ", appID,
						", SubscriptionId:", subscriptionID, ", ApiId:", apiID));
			} else {
				logger.info(() -> Joiner.on(" ").join("Not a WhiteListed number:", formattedPhoneNumber));
				messageContext.setProperty(SynapseConstants.ERROR_CODE, "POL0001:");
				messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, "Internal Server Error. Not a white listed" +
						" Number");
				messageContext = setErrorInformationToContext(messageContext, "SVC0004", " Not a whitelisted number. %1",
						commonContextData.msisdn, String.valueOf(HttpStatus.SC_BAD_REQUEST), "POLICY_EXCEPTION");
				messageContext.setProperty("WHITELISTED_MSISDN", "false");

			}
		} catch (Exception e) {
			logger.error(Joiner.on(" ").join("error in MSISDNWhitelistMediator mediate :", e.getMessage()));

			String errorVariable = commonContextData.msisdn;
			if (commonContextData.paramArray != null) {
				errorVariable = commonContextData.paramArray;
			}

			messageContext = setErrorInformationToContext(messageContext, "SVC0001", "A service error occurred. Error code is %1",
					errorVariable, String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), "SERVICE_EXCEPTION");
			messageContext.setProperty("INTERNAL_ERROR", "true");
		}
		return true;
	}
}
