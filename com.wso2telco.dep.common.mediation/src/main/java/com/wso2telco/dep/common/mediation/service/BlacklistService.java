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
package com.wso2telco.dep.common.mediation.service;

import java.util.List;

/**
 * The facade for black list functionality related services.
 */
public interface BlacklistService {

    /**
     * Filter blacklisted msisdns from the given msisdn list for the given api id
     *
     * @param apiId
     * @param msisdnList
     * @return
     */
    List<String> filterBlacklistedMSISDNsByApiId(String apiId, List<String> msisdnList) throws Exception;
}
