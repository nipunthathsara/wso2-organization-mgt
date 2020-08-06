package org.wso2.carbon.identity.organization.mgt.endpoint.factories;

import org.wso2.carbon.identity.organization.mgt.endpoint.OrganizationsApiService;
import org.wso2.carbon.identity.organization.mgt.endpoint.impl.OrganizationsApiServiceImpl;

public class OrganizationsApiServiceFactory {

   private final static OrganizationsApiService service = new OrganizationsApiServiceImpl();

   public static OrganizationsApiService getOrganizationsApi()
   {
      return service;
   }
}
