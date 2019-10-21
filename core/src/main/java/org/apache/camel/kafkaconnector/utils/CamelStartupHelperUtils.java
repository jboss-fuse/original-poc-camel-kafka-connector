/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.kafkaconnector.utils;

import java.util.Map;

public final class CamelStartupHelperUtils {

    private CamelStartupHelperUtils() {
        
    }

    public static CamelStartupHelper instantiateHelper(String helperClass, Map<String, String> props) throws Exception {
        if (helperClass == null) {
            return null;
        }

        Class<?> clazz = Class.forName(helperClass);

        Object object = clazz.newInstance();
        if (object instanceof CamelStartupHelper) {


            CamelStartupHelper helper = (CamelStartupHelper) object;

            helper.init(props);

            return helper;
        }

        throw new Exception(String.format("Invalid startup helper class: %s does not implement %s",
                clazz.getName(), CamelStartupHelper.class.getName()));
    }
}
