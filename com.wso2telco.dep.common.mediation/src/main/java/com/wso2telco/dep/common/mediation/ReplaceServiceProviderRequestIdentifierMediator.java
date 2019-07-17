package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;

public class ReplaceServiceProviderRequestIdentifierMediator extends AbstractCommonMediator {

	public boolean mediate(MessageContext synContext) {

		try {

			String currentEndpoint = (String) synContext.getProperty("API_ENDPOINT");
			String resourcePath = (String) synContext.getProperty("RESOURCE");
			String uniqueRequestIdentifier = (String) synContext.getProperty("uniqueRequestIdentifier");
			String changedResourcePath = resourcePath.replaceAll("(?<=[?&;])requestIdentifier=[^&;]*",
					"requestIdentifier=" + uniqueRequestIdentifier);
			synContext.setProperty("API_ENDPOINT", currentEndpoint + changedResourcePath);
		} catch (Exception e) {

			log.error("error in ReplaceRequestIdetifierMediator mediate : "
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
