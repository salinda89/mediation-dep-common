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
package com.wso2telco.dep.common.mediation.sql;

/**
 * Temporary class to hold sqls until introducing a property file
 */
public class MediationSQLs {

    /**
     * Black list functionality related sqls
     */

    /**
     * The sql used to filter blacklisted numbers against a api from the given msisdn list
     */
    public static final String BLACKLIST_FILTER_SQL = "select MSISDN from blacklistmsisdn where API_ID = ? AND MSISDN in( ? )";
}
