// Copyright 2019 The OpenSDS Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may
// not use this file except in compliance with the License. You may obtain
// a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations
// under the License.
package org.opensds.storage.conection.rest.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.opensds.storage.conection.rest.constants.DMError;
import org.opensds.storage.conection.rest.exception.RestException;

public class RestInterfaceConfs {
    private static final Logger logger = Logger
            .getLogger(RestInterfaceConfs.class);

    // operationName , [restMethod,restRelativePath]
    public static final Map<String, List<String>> conf = new HashMap<String, List<String>>();

    // when the class is loading , load the properties once
    static {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = RestInterfaceConfs.class
                    .getResourceAsStream("restOperations.properties");
            properties.load(in);
            if (in != null) {
                for (Object key : properties.keySet()) {
                    String value = (String) properties.get(key);
                    String[] attrs = value.split(";");
                    List<String> list = new ArrayList<String>();
                    list.add(attrs[0]);
                    list.add(attrs[1]);
                    conf.put((String) key, list);
                }
            }
        } catch (IOException e) {
            logger.error("The config file loading error : " + e.getMessage(),
                    e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }
        }
    }

    public static boolean isValidOperation(String operationName)
            throws RestException {
        doCheck();
        return conf.get(operationName) == null ? false : true;
    }

    private static void doCheck() throws RestException {
        if (conf.size() == 0) {
            throw DMError.INTERNAL.OPERATION_CONFIG_FILE_LOADING_ERROR
                    .newException(RestInterfaceConfs.class.getResource("")
                            + "restOperations.properties");
        }
    }

    public static String getRestMethod(String operationName)
            throws RestException {
        doCheck();
        List<String> list = conf.get(operationName);
        if (list != null) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static String getRestRelativePath(String operationName)
            throws RestException {
        doCheck();
        List<String> list = conf.get(operationName);
        if (list != null) {
            return list.get(1);
        } else {
            return null;
        }
    }
}
