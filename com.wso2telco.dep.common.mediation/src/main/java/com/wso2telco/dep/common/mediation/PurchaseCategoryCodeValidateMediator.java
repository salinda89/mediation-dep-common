package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import com.wso2telco.dep.common.mediation.service.APIService;

public class PurchaseCategoryCodeValidateMediator extends AbstractCommonMediator {

	public boolean mediate(MessageContext synContext) {

		try {

			String purchaseCategoryCode = (String) synContext
					.getProperty("purchaseCategoryCode");

			APIService apiService = new APIService();
			boolean isvalid = apiService
					.validatePurchaseCategoryCode(purchaseCategoryCode);

			if (!isvalid) {

				log.error("purchase category code : " + purchaseCategoryCode
						+ " is invalid");
				setErrorInformationToContext(synContext, "POL0001",
						"A policy error occurred. Error code is %1",
						"Invalid purchaseCategoryCode : "
								+ purchaseCategoryCode, "400",
						"POLICY_EXCEPTION");
				synContext.setProperty("PURCHASE_CATEGORY_VALIDATED", "false");
			}
		} catch (Exception e) {

			log.error("error in PurchaseCategoryCodeValidateMediator mediate : "
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
