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
package com.wso2telco.dep.common.mediation.service.impl;

import com.wso2telco.dep.common.mediation.constant.MSISDNConstants;
import com.wso2telco.dep.common.mediation.dao.BlacklistDAO;
import com.wso2telco.dep.common.mediation.service.BlacklistService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link BlacklistService}
 */
public final class BlacklistServiceImpl implements BlacklistService {

    private BlacklistDAO blacklistDAO;

    public BlacklistServiceImpl(BlacklistDAO blacklistDAO) {
        this.blacklistDAO = blacklistDAO;
    }

    /**
     * {@inheritDoc}
     *
     * @param apiId
     * @param msisdnList
     * @return
     */
    @Override
    public List<String> filterBlacklistedMSISDNsByApiId(String apiId, List<String> msisdnList) throws Exception {

        //If a prefix is available, then append it to all the msisdns and search with the prefix.
        //TODO in the old code, there was a chack with the prefix and without the prefix. please verify it
        msisdnList = updateMsisdnListWithPrefix(msisdnList);
        return blacklistDAO.filterBlacklistedMSISDNsByApiId(apiId, msisdnList);
    }

    private List<String> updateMsisdnListWithPrefix(List<String> msisdnList) {
        return msisdnList.stream().map(msisdn -> MSISDNConstants.BLACKLIST_MSISDN_PREFIX + msisdn)
                .collect(Collectors.toList());
    }
}
