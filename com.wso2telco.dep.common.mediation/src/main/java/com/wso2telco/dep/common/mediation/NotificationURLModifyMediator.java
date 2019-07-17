package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import com.wso2telco.dep.common.mediation.service.APIService;

public class NotificationURLModifyMediator extends AbstractCommonMediator {

	public boolean mediate(MessageContext synContext) {

		try {

			Integer id = 0;
			String generatedNotifyURL = null;
			String apiName = (String) synContext.getProperty("API_NAME");
			String spNotifyURL = (String) synContext.getProperty("notifyURL");
			String clientCorrelator = (String) synContext.getProperty("clientCorrelator");
			String notificationURL = (String) synContext
					.getProperty("NOTIFICATION_URL");
			String serviceProvider = (String) synContext.getProperty("USER_ID");
			String operatorName = (String) synContext.getProperty("OPERATOR_NAME");

			APIService apiService = new APIService();
			id = apiService.storeServiceProviderNotifyURLService(apiName,
					spNotifyURL, serviceProvider, clientCorrelator, operatorName);
			generatedNotifyURL = notificationURL + "/" + id;

			synContext.setProperty("generatedNotifyURL", generatedNotifyURL);
			synContext.setProperty("notifyURLTableId", id);
		} catch (Exception e) {

			log.error("error in NotificationURLModifyMediator mediate : "
					+ e.getMessage());
			synContext = setErrorInformationToContext(
					synContext,
					"SVC0001",
					"A service error occurred. Error code is %1",
					"An internal service error has occured. Please try again later.",
					"500", "SERVICE_EXCEPTION");
			synContext.setProperty("INTERNAL_ERROR", "true");
		}

		return true;
	}
}
