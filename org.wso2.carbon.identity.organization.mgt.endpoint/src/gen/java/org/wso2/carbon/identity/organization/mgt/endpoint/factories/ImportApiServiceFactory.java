package org.wso2.carbon.identity.organization.mgt.endpoint.factories;

import org.wso2.carbon.identity.organization.mgt.endpoint.ImportApiService;
import org.wso2.carbon.identity.organization.mgt.endpoint.impl.ImportApiServiceImpl;

public class ImportApiServiceFactory {

   private final static ImportApiService service = new ImportApiServiceImpl();

   public static ImportApiService getImportApi()
   {
      return service;
   }
}
