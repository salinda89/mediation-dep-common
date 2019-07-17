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
package com.wso2telco.dep.common.mediation.dao;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.wso2telco.core.dbutils.DbUtils;
import com.wso2telco.core.dbutils.util.DataSourceNames;
import com.wso2telco.dep.common.mediation.constant.MSISDNConstants;
import com.wso2telco.dep.common.mediation.sql.MediationSQLs;
import framework.logging.LazyLogger;
import framework.logging.LazyLoggerFactory;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access operations related to blacklist functionality.
 */
public final class BlacklistDAO {

    private LazyLogger logger = LazyLoggerFactory.getLogger(this.getClass());

    /**
     * Find the blacklisted msisdns for the given api and returns only the blacklisted msisdn list
     *
     * @param apiId
     * @param msisdnList
     * @return
     */
    public List<String> filterBlacklistedMSISDNsByApiId(String apiId, List<String> msisdnList) throws Exception {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<String> blacklistedList = Lists.newArrayList();
        try {
            connection = DbUtils.getDbConnection(DataSourceNames.WSO2AM_STATS_DB);
            preparedStatement = connection.prepareStatement(MediationSQLs.BLACKLIST_FILTER_SQL);
            preparedStatement.setString(1, apiId);
            preparedStatement.setString(2, Joiner.on(",").join(msisdnList));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                blacklistedList.add(resultSet.getString("MSISDN").replace(MSISDNConstants.BLACKLIST_MSISDN_PREFIX, ""));
            }

            return blacklistedList;
        } catch (SQLException e) {
            logger.error(Joiner.on(" ").join("Error occurred while doing sql operation.", e.getMessage()), e);
            throw e;
        } catch (NamingException e) {
            logger.error("Database lookup failed.", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while filterBlacklistedMSISDNsByApiId().", e);
            throw e;
        } finally {
            DbUtils.closeAllConnections(preparedStatement, connection, resultSet);
        }
    }
}
