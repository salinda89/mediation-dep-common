package com.wso2telco.dep.common.mediation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.wso2telco.core.mnc.resolver.ConfigLoader;
import com.wso2telco.core.mnc.resolver.DataHolder;
import org.apache.synapse.MessageContext;
import com.wso2telco.core.mnc.resolver.MNCQueryClient;
import com.wso2telco.core.mnc.resolver.dao.OperatorDAO;
import com.wso2telco.core.msisdnvalidator.MSISDN;
import com.wso2telco.core.msisdnvalidator.MSISDNUtil;
import com.wso2telco.dep.operatorservice.model.OperatorEndPointDTO;
import com.wso2telco.dep.operatorservice.service.OparatorService;

public class EndpointRetrieverMediator extends AbstractCommonMediator {

	/** The operatorEndpoints. */
	private List<OperatorEndPointDTO> operatorEndpoints;

	private Set<String> countryLookUpOnHeader = new HashSet<String>();
	private Set<String> validOperators = new HashSet<String>();

	public boolean mediate(MessageContext synContext) {

		MNCQueryClient mncQueryclient = new MNCQueryClient();
		MSISDNUtil phoneUtil = new MSISDNUtil();
		StringBuffer msisdn = new StringBuffer();

		try {

			String operator = null;
			operatorEndpoints = new OparatorService().getOperatorEndpoints();
			String apiName = (String) synContext.getProperty("API_NAME");
			String requestMSISDN = (String) synContext.getProperty("MSISDN");
			String countryCodes = (String) synContext
					.getProperty("SEARCH_OPERATOR_ON_HEADER");
			String headerOperatorName = (String) synContext
					.getProperty("OPERATOR");
			String validOperatorList = (String) synContext
					.getProperty("VALID_OPERATORS");
			String mcc = (String) synContext.getProperty("mcc");
			String mnc = (String) synContext.getProperty("mnc");

			if (requestMSISDN != null && requestMSISDN.trim().length() > 0) {

				/**
				 * MSISDN provided at JSon body convert into Phone number
				 * object.
				 */
				MSISDN numberProto = phoneUtil.parse(requestMSISDN);

				/**
				 * obtain the country code form the phone number object
				 */
				int countryCode = numberProto.getCountryCode();

				loadCountryCodeList(countryCodes);

				/**
				 * if the country code within the header look up context , the
				 * operator taken from the header object
				 */
				if (countryLookUpOnHeader.contains(String.valueOf(countryCode))) {

					if (headerOperatorName != null
							&& headerOperatorName.trim().length() > 0) {

						operator = headerOperatorName;
						log.debug("operator pick from the Header : " + operator);
					} else {

						log.debug("the request doesnot obtain operator from the header");
					}
				}

				/**
				 * build the MSISDN
				 */
				msisdn.append("+").append(numberProto.getCountryCode())
						.append(numberProto.getNationalNumber());
			}

			/**
			 * if the operator still not selected the operator selection logic
			 * goes as previous. ie select from MCC_NUMBER_RANGE
			 */
			if (operator == null) {

				if (getNullOrTrimmedValue(mcc) != null
						&& getNullOrTrimmedValue(mnc) != null) {
					
					operator = OperatorDAO.getOperatorByMCCMNC(mcc, mnc);

					if (operator == null) {

						setErrorInformationToContext(synContext, "SVC0001",
								"A service error occurred. Error code is %1",
								"No valid operator found for given MCC and MNC",
								"400", "SERVICE_EXCEPTION");
						synContext.setProperty("ENDPOINT_ERROR", "true");
						return true;
					}
				} else {
					
					log.debug("unable to obtain operator from the header and check for mcc_number_range table "
							+ operator
							+ " mcc : null msisdn: "
							+ msisdn.toString());
					DataHolder.getInstance()
							.setMobileCountryConfig(
									ConfigLoader.getInstance()
											.getMobileCountryConfig());
					operator = mncQueryclient.QueryNetwork(null,
							msisdn.toString());
				}

			}

			if (operator == null) {

				setErrorInformationToContext(synContext, "SVC0001",
						"A service error occurred. Error code is %1",
						"No valid operator found for given MSISDN", "400",
						"SERVICE_EXCEPTION");
				synContext.setProperty("ENDPOINT_ERROR", "true");
				return true;
			}

			loadValidOperatorList(validOperatorList);

			if (!validOperators.contains(String.valueOf(operator))) {

				setErrorInformationToContext(synContext, "SVC0001",
						"A service error occurred. Error code is %1",
						"Requested service is not provisioned", "400",
						"SERVICE_EXCEPTION");
				synContext.setProperty("ENDPOINT_ERROR", "true");
				return true;
			}

			OperatorEndPointDTO validOperatorendpoint = getValidEndpoints(
					apiName, operator);

			if (validOperatorendpoint == null) {

				setErrorInformationToContext(synContext, "SVC0001",
						"A service error occurred. Error code is %1",
						"Requested service is not provisioned", "400",
						"SERVICE_EXCEPTION");
				synContext.setProperty("ENDPOINT_ERROR", "true");
				return true;
			}

			String apiEndpoint = validOperatorendpoint.getEndpoint();
			synContext.setProperty("OPERATOR_ENDPOINT",
					validOperatorendpoint.getEndpoint());
			synContext.setProperty("API_ENDPOINT", apiEndpoint);
			synContext.setProperty("OPERATOR_ID",
					validOperatorendpoint.getOperatorid());
			synContext.setProperty("OPERATOR_NAME", operator.toUpperCase());
		} catch (Exception e) {

			log.error("error in EndpointRetrieverMediator mediate : "
					+ e.getMessage());
			setErrorInformationToContext(
					synContext,
					"SVC0001",
					"A service error occurred. Error code is %1",
					"An internal service error has occured. Please try again later.",
					"500", "SERVICE_EXCEPTION");
			synContext.setProperty("INTERNAL_ERROR", "true");
			return true;
		}

		return true;
	}

	private void loadCountryCodeList(String countries) {

		if (countries != null) {

			// Split the comma separated country codes
			String[] countryArray = countries.split(",");
			for (String country : countryArray) {

				countryLookUpOnHeader.add(country.trim());
			}
		}
	}

	private void loadValidOperatorList(String validOperatorList) {

		if (validOperatorList != null) {

			// Split the comma separated operators
			String[] operatorArray = validOperatorList.split(",");
			for (String operator : operatorArray) {

				validOperators.add(operator.trim());
			}
		}
	}

	/**
	 * Gets the valid endpoints.
	 *
	 * @param api
	 *            the api
	 * @param validoperator
	 *            the validOperator
	 * @return the valid endpoints
	 */
	private OperatorEndPointDTO getValidEndpoints(String api,
			String validOperator) {

		OperatorEndPointDTO validoperendpoint = null;

		for (OperatorEndPointDTO d : operatorEndpoints) {

			if ((d.getApi().equalsIgnoreCase(api))
					&& (validOperator.equalsIgnoreCase(d.getOperatorcode()))) {

				validoperendpoint = d;
				break;
			}
		}

		return validoperendpoint;
	}

	private String getNullOrTrimmedValue(String input) {
		String output = null;

		if (input != null && input.trim().length() > 0) {
			output = input.trim();
		}

		return output;
	}
}
