package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;

import com.wso2telco.dep.common.mediation.service.APIService;

public class NotificationURLInfoStatusUpdaterMediator extends AbstractCommonMediator {

	public boolean mediate(MessageContext synContext) {

		try {

			String notifyurldid = (String) synContext
					.getProperty("NOTIFY_URL_ID");

			APIService apiService = new APIService();
			apiService.updateNotificationURLInformationStatus(Integer
					.parseInt(notifyurldid));
		} catch (Exception e) {

			log.error("error in NotificationURLInfoStatusUpdaterMediator mediate : "
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
