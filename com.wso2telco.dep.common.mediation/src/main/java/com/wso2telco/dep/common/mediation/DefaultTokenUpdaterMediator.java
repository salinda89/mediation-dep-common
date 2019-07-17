package com.wso2telco.dep.common.mediation;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import com.wso2telco.dep.operatorservice.service.OparatorService;

public class DefaultTokenUpdaterMediator extends AbstractCommonMediator {

	public boolean mediate(MessageContext synContext) {

		try {

			int operatorId = Integer.parseInt(String.valueOf(synContext
					.getProperty("OPERATOR_ID")));
			String accessToken = (String) synContext
					.getProperty("ACCESS_TOKEN");
			String refreshToken = (String) synContext
					.getProperty("REFRESH_TOKEN");
			long tokenValidity = Long.parseLong(String.valueOf(synContext
					.getProperty("TOKEN_VALIDITY")));
			long tokentime = new Date().getTime();

			OparatorService operatorService = new OparatorService();
			operatorService.updateOperatorToken(operatorId, refreshToken,
					tokenValidity, tokentime, accessToken);
		} catch (Exception e) {

			log.error("error in DefaultTokenUpdaterMediator mediate : "
					+ e.getMessage());
			setErrorInformationToContext(synContext, "SVC0001", "A service error occurred. Error code is %1",
					"An internal service error has occured. Please try again later.",
					Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), "SERVICE_EXCEPTION");
			synContext.setProperty("INTERNAL_ERROR", "true");
		}

		return true;
	}
}
