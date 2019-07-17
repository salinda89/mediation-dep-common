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
package com.wso2telco.dep.common.mediation.dto;

import com.google.common.base.Joiner;

import java.util.Objects;

/**
 * dto to hold api information
 */
public class ApiInformation {
    public String apiPublisher;
    public String apiName;
    public String apiVersion;

    public ApiInformation(String apiPublisher, String apiName, String apiVersion) {
        this.apiPublisher = apiPublisher;
        this.apiName = apiName;
        this.apiVersion = apiVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiInformation that = (ApiInformation) o;
        return apiPublisher.equals(that.apiPublisher) &&
                apiName.equals(that.apiName) &&
                apiVersion.equals(that.apiVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiPublisher, apiName, apiVersion);
    }

    @Override
    public String toString() {
        return Joiner.on("").join("apiPublisher:", apiPublisher, " ,apiName:", apiName, " ,apiVersion:", apiVersion);
    }
}
