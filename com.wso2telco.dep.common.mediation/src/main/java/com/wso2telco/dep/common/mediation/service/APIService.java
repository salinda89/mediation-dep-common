package com.wso2telco.dep.common.mediation.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.wso2telco.dep.common.mediation.dao.APIDAO;
import com.wso2telco.dep.common.mediation.dto.ApiInformation;
import framework.cache.AXPCacheBuilder;
import framework.cache.IAXPCacheLoader;
import framework.cache.IAXPLoadingCache;
import framework.logging.LazyLogger;
import framework.logging.LazyLoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class APIService {

	/**
	 * The cache use to hold api ids throughout the application
	 */
	private static IAXPLoadingCache<ApiInformation, String> apiIdCache = AXPCacheBuilder.newBuilder()
			.setNoTimeLimit(true).build(new ApiIdCacheLoader());

	APIDAO apiDAO;

	private final Log log = LogFactory.getLog(APIService.class);

	{
		apiDAO = new APIDAO();
	}

	public Integer storeServiceProviderNotifyURLService(String apiName,
			String notifyURL, String serviceProvider, String clientCorrelator,
			String operatorName) throws Exception {

		Integer newId = 0;

		try {

			newId = apiDAO.insertServiceProviderNotifyURL(apiName, notifyURL,
					serviceProvider, clientCorrelator, operatorName);
		} catch (Exception e) {

			throw e;
		}

		return newId;
	}

	public boolean validatePurchaseCategoryCode(String purchaseCategoryCode)
			throws Exception {

		boolean isvalid = false;

		try {

			List<String> validCategoris = apiDAO.getValidPurchaseCategories();

			if (validCategoris.size() > 0) {


				for (String category : validCategoris) {

					if (category.equalsIgnoreCase(purchaseCategoryCode)) {

						isvalid = true;
						break;
					}
				}
			}
		} catch (Exception e) {

			throw e;
		}

		return isvalid;
	}

	public Map<String, String> getNotificationURLInformation(int notifyurldid)
			throws Exception {

		Map<String, String> notificationURLInformation = null;

		try {

			notificationURLInformation = apiDAO
					.getNotificationURLInformation(notifyurldid);
		} catch (Exception e) {

			throw e;
		}

		if (notificationURLInformation != null) {

			return notificationURLInformation;
		} else {

			return Collections.emptyMap();
		}
	}

	public void updateNotificationURLInformationStatus(int notifyurldid)
			throws Exception {

		try {

			apiDAO.updateNotificationURLInformationStatus(notifyurldid);
		} catch (Exception e) {

			throw e;
		}
	}

	public String getAttributeValueForCode(String tableName,
			String operatorName, String attributeGroupCode, String attributeCode)
			throws Exception {
		String attributeValue = null;

		try {
			attributeValue = apiDAO.getAttributeValueForCode(tableName,
					operatorName, attributeGroupCode, attributeCode);
		} catch (Exception ex) {
			log.error("Error while retrieving attribute value", ex);
			throw ex;
		}

		return attributeValue;
	}

	public String getAPIId(String apiPublisher, String apiName,
			String apiVersion) throws Exception {
		String apiId;
		try {
			apiId = apiDAO.getAPIId(apiPublisher, apiName, apiVersion);
		} catch (Exception ex) {
			log.error("Error while retrieving API Id value", ex);
			throw ex;
		}
		return apiId;
	}

	/**
	 * This method helps to retrieve api id related to the given api information
	 * This has the caching support. If the given key is available in the cache, it will fetch the data from cache.
	 *
	 * @param apiInformation
	 * @return
	 */
	public String getAPIId(ApiInformation apiInformation) throws Exception {
		try {
			return apiIdCache.getData(apiInformation);
		} catch (Exception e) {
			log.error("Error while retrieving API Id value", e);
			throw e;
		}
	}

	/**
	 * Deprecated due to inefficient data loading in to the memory
	 * instead use {@link BlacklistService#filterBlacklistedMSISDNsByApiId(String, List)}
	 *
	 * @param apiId
	 * @param msisdn
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public boolean isBlackListedNumber(String apiId, String msisdn)
			throws Exception {
		try {
			List<String> msisdnArrayList = apiDAO.readBlacklistNumbers(apiId);
			return (msisdnArrayList.contains(msisdn) || msisdnArrayList.contains("tel3A+" + msisdn));
		} catch (Exception ex) {
			log.error("Error while checking whether the msisdn :" + msisdn + " is blacklisted", ex);
			throw ex;
		}
	}

	public String getSubscriptionID(String apiId, String applicationId)
			throws Exception {
		return String.valueOf(apiDAO.getSubscriptionId(apiId, applicationId));
	}

	public boolean isWhiteListed(String MSISDN, String applicationId,String subscriptionId, String apiId) throws Exception {
		return apiDAO.checkWhiteListed(MSISDN, applicationId, subscriptionId,apiId);
	}

	public Integer groupByApi(String sp, String app, String apiId, String operatorName, int year, int month) throws Exception {
		Integer currentQuotaLimit=null;
		try {
			currentQuotaLimit=apiDAO.groupByApi(sp, app, apiId, operatorName,year,month);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}

 		return currentQuotaLimit;
	}

	public Integer groupByApplication(String sp, String app, String operatorName, int year, int month)throws Exception {
		Integer currentQuotaLimit=null;
		try {
			currentQuotaLimit=apiDAO.groupByApp(sp, app, operatorName,year,month);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}

 		return currentQuotaLimit;
	}

	public Integer groupBySp(String sp, String operatorName, int year, int month) throws Exception {
		Integer currentQuotaLimit=null;
		try {
			currentQuotaLimit=apiDAO.groupBySp(sp, operatorName,year,month);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}

 		return currentQuotaLimit;
	}

	public Integer spLimit(String serviceProvider, String operatorName,Integer year,Integer month, String sqlDate)  throws Exception{
		try {
			 return apiDAO.spLimit(serviceProvider, operatorName,year,month,sqlDate);
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer applicationLimit(String serviceProvider, String application,String operatorName,Integer year,Integer month, String sqlDate) throws Exception {
		try {
			 return apiDAO.applicationLimit(serviceProvider, application, operatorName,year,month,sqlDate);
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer apiLimit(String serviceProvider, String application,String apiName, String operatorName,Integer year,Integer month, String sqlDate)  throws Exception{
		try {
			 return apiDAO.apiLimit(serviceProvider, application, apiName, operatorName,year,month,sqlDate);
		} catch (Exception e) {
			throw e;
		}
	}


	public static boolean inSPQuotaDateRange(String serviceProvider,String operatorName, String sqlDate) throws Exception{
		try {
			return APIDAO.inSPQuotaDateRange(serviceProvider, operatorName,sqlDate);
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean inAPPQuotaDateRange(String serviceProvider,String application, String operatorName, String sqlDate) throws Exception{
		try {
			return APIDAO.inAPPQuotaDateRange(serviceProvider,application, operatorName,sqlDate);
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean inAPIQuotaDateRange(String serviceProvider,String application, String apiName, String operatorName,String sqlDate) throws Exception{
		try {
			return APIDAO.inAPIQuotaDateRange(serviceProvider,application, apiName, operatorName,sqlDate);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Cache loader for api id
	 * load the missing data from the database
	 */
	private static class ApiIdCacheLoader implements IAXPCacheLoader<ApiInformation, String> {

		private static APIDAO apiDAO = new APIDAO();

		private LazyLogger logger = LazyLoggerFactory.getLogger(ApiIdCacheLoader.class);
		/**
		 * {@inheritDoc}
		 *
		 * @param cacheKeyList
		 * @return
		 * @throws Exception
		 */
		@Override
		public Map<ApiInformation, String> load(List<ApiInformation> cacheKeyList) throws Exception {

			Map<ApiInformation, String> apiInformationMap = Maps.newHashMap();
			for (ApiInformation apiInformation : cacheKeyList) {
				logger.debug(() -> Joiner.on(" ").join("Retrieving API id from db :", apiInformation.toString()));
				apiInformationMap.put(apiInformation, apiDAO.getAPIId(apiInformation.apiPublisher,
						apiInformation.apiName, apiInformation.apiVersion));
			}

			return apiInformationMap;
		}
	}
}
