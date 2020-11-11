/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.wso2.carbon.identity.organization.mgt.endpoint;

import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.factories.OrganizationsApiServiceFactory;

import io.swagger.annotations.ApiParam;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.BasicOrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;

import java.util.List;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OperationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserRoleMappingDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.RoleDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserStoreConfigDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/organizations")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(value = "/organizations",
                            description = "the organizations API")
public class OrganizationsApi {

    private final OrganizationsApiService delegate = OrganizationsApiServiceFactory.getOrganizationsApi();

    @GET
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve organizations created for this tenant which matches the "
            + "defined search criteria, if any.\n",
                                         notes = "This API is used to search and retrieve organizations created for "
                                                 + "this tenant which matches the defined search criteria, if any.\n",
                                         response = BasicOrganizationDTO.class,
                                         responseContainer = "List")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsGet(@Context SearchContext searchContext,
            @ApiParam(value = "Number of items to be skipped before starting to collect the result set. (Should be 0 "
                    + "or positive)") @QueryParam("offset") Integer offset,
            @ApiParam(value = "Max number of items to be returned. (Should be greater than 0)") @QueryParam("limit") Integer limit,
            @ApiParam(value = "Criteria to sort by. (name, description, displayName, status, lastModified, created, "
                    + "parentName, parentDisplayName)",
                      allowableValues = "{values=[name, description, displayName, status, lastModified, created, "
                              + "parentName, parentDisplayName]}") @QueryParam("sortBy") String sortBy,
            @ApiParam(value = "Ascending or Descending order. (ASC, DESC)",
                      allowableValues = "{values=[ASC, DESC]}") @QueryParam("sortOrder") String sortOrder,
            @ApiParam(value = "Comma separated list of attributes to be returned in the response. ('*' for all)") @QueryParam("attributes") String attributes,
            @ApiParam(value = "Whether to include the user's permissions for each organization in the response or "
                    + "not") @QueryParam("includePermissions") Boolean includePermissions) {
        return delegate
                .organizationsGet(searchContext, offset, limit, sortBy, sortOrder, attributes, includePermissions);
    }

    @POST
    @Path("/import")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create an organization without changing the underlying LDAP.\n",
                                         notes = "This API is used to create an organization to represent an exisitng"
                                                 + " organization of the underlying user store. Hence, this will not "
                                                 + "create any OU in the LDAP.\n",
                                         response = BasicOrganizationDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 201,
                                                message = "Successful Response"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsImportPost(@ApiParam(value = "This represents the organization to be added.",
                                                      required = true) OrganizationAddDTO organization) {
        return delegate.organizationsImportPost(organization);
    }

    @GET
    @Path("/{organization-id}/children")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get a list of child organization IDs for a given organization.\n",
                                         notes = "This API is used to retrieve children of an organization identified"
                                                 + " by the organization Id.\n",
                                         response = String.class,
                                         responseContainer = "List")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdChildrenGet(
            @ApiParam(value = "ID of the organization of which, the children are to be retrieved.",
                      required = true) @PathParam("organization-id") String organizationId) {
        return delegate.organizationsOrganizationIdChildrenGet(organizationId);
    }

    @DELETE
    @Path("/{organization-id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete an organization by ID.\n",
                                         notes = "This API is used to delete an organization, identified by the "
                                                 + "organization ID.\n",
                                         response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 204,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdDelete(@ApiParam(value = "ID of the organization to be deleted.",
                                                                required = true) @PathParam("organization-id") String organizationId) {
        return delegate.organizationsOrganizationIdDelete(organizationId);
    }

    @GET
    @Path("/{organization-id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get an existing organization identified by the organization Id.\n",
                                         notes = "This API is used to get an existing organization identified by the "
                                                 + "organization Id.\n",
                                         response = OrganizationDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdGet(
            @ApiParam(value = "ID of the organization of which, the fields are to be patched.",
                      required = true) @PathParam("organization-id") String organizationId,
            @ApiParam(value = "Whether to include the user's permissions for the organization in the response or not") @QueryParam("includePermissions") Boolean includePermissions) {
     return delegate.organizationsOrganizationIdGet(organizationId, includePermissions);
    }

    @PATCH
    @Path("/{organization-id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "This API is used to patch an existing organization, identified by "
            + "the organization Id.\n",
                                         notes = "This API is used to 'replace' the defined field of the organization"
                                                 + " identified by the organization Id.\n",
                                         response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 204,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdPatch(@ApiParam(value = "Id of the organization to be patched.",
                                                               required = true) @PathParam("organization-id") String organizationId,
            @ApiParam(value = "This represents the patch operation.",
                      required = true) List<OperationDTO> operations) {
        return delegate.organizationsOrganizationIdPatch(organizationId, operations);
    }

    @POST
    @Path("/{organization-id}/roles")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a user role mappings for an organization.\n",
            notes = "This API is used to create user role mappings for an organization.\n",
            response = void.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error") })

    public Response organizationsOrganizationIdRolesPost(@ApiParam(value = "ID of the organization of which, the user role mappings are added.",required=true ) @PathParam("organization-id")  String organizationId,
    @ApiParam(value = "This represents user role mappings." ,required=true ) UserRoleMappingDTO userRoles) {

        return delegate.organizationsOrganizationIdRolesPost(organizationId,userRoles);
    }

    @GET
    @Path("/{organization-id}/roles/{role-id}/users")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve the list of users who have specific role against an "
            + "organization.\n",
                                         notes = "This API is used to get the user list of an organization with a "
                                                 + "specific role.\n",
                                         response = UserDTO.class,
                                         responseContainer = "List")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdRolesRoleIdUsersGet(@ApiParam(value = "ID of the organization of which, the users will be returned.",required=true ) @PathParam("organization-id")  String organizationId,
    @ApiParam(value = "ID of the role of which, the user will be returned.",required=true ) @PathParam("role-id")  String roleId,
    @ApiParam(value = "Number of items to be skipped before starting to collect the result set. (Should be 0 or positive)") @QueryParam("offset")  Integer offset,
    @ApiParam(value = "Max number of items to be returned. (Should be greater than 0)") @QueryParam("limit")  Integer limit,
    @ApiParam(value = "Comma separated list of SCIM user attributes to be returned in the response.") @QueryParam("attributes")  String attributes,
    @ApiParam(value = "SCIM filtering to fine tune the search results. Support operations are 'eq', 'co', 'sw', 'ew'.") @QueryParam("filter")  String filter) {

        return delegate.organizationsOrganizationIdRolesRoleIdUsersGet(organizationId,roleId,offset,limit,attributes,filter);
    }

    @DELETE
    @Path("/{organization-id}/roles/{role-id}/users/{user-id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delte an organization user role mapping.\n",
                                         notes = "This API is used to delete user role mappings for an organization.\n",
                                         response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 204,
                                                message = "No Content"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdRolesRoleIdUsersUserIdDelete(@ApiParam(value = "ID of the organization of which, the user role mappings will be deleted.",required=true ) @PathParam("organization-id")  String organizationId,
    @ApiParam(value = "ID of the role of which, the user will be deleted.",required=true ) @PathParam("role-id")  String roleId,
    @ApiParam(value = "ID of the user.",required=true ) @PathParam("user-id")  String userId,
    @ApiParam(value = "Filter the role mappings to delete. Check whether sub organization's role mappings will be deleted or not.") @QueryParam("includeSubOrgs")  Boolean includeSubOrgs) {

        return delegate.organizationsOrganizationIdRolesRoleIdUsersUserIdDelete(organizationId,roleId,userId,includeSubOrgs);
    }

    @GET
    @Path("/{organization-id}/users/{user-id}/roles")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Retrieve the list of role that particular user has against an "
            + "organization.\n",
                                         notes = "This API is used to get the list of roles for a user for an "
                                                 + "organization.\n",
                                         response = RoleDTO.class,
                                         responseContainer = "List")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdUsersUserIdRolesGet(
            @ApiParam(value = "ID of the organization of which, the users will be returned.",
                      required = true) @PathParam("organization-id") String organizationId,
            @ApiParam(value = "ID of the user.",
                      required = true) @PathParam("user-id") String userId) {

        return delegate.organizationsOrganizationIdUsersUserIdRolesGet(organizationId, userId);
    }

    @GET
    @Path("/{organization-id}/userstore-configs")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get user store configurations of an organization identified by the "
            + "organization Id.\n",
                                         notes = "This API is used to retrieve user store configurations of an "
                                                 + "organization identified by the organization Id.\n",
                                         response = UserStoreConfigDTO.class,
                                         responseContainer = "List")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdUserstoreConfigsGet(
            @ApiParam(value = "ID of the organization of which, the user store configurations are to be retrieved.",
                      required = true) @PathParam("organization-id") String organizationId) {
        return delegate.organizationsOrganizationIdUserstoreConfigsGet(organizationId);
    }

    @PATCH
    @Path("/{organization-id}/userstore-configs")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Patch user store configurations of an organization identified by "
            + "the organization Id.\n",
                                         notes = "This API is used to patch user store configurations of an "
                                                 + "organization, identified by the organization Id.\n",
                                         response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 204,
                                                message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsOrganizationIdUserstoreConfigsPatch(
            @ApiParam(value = "ID of the organization of which, the user store configurations are to be patched.",
                      required = true) @PathParam("organization-id") String organizationId,
            @ApiParam(value = "This represents the patch operation.",
                      required = true) List<OperationDTO> operations) {
        return delegate.organizationsOrganizationIdUserstoreConfigsPatch(organizationId, operations);
    }

    @POST

    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new organization.\n",
                                         notes = "This API is used to create the organization defined in the user "
                                                 + "input.\n",
                                         response = BasicOrganizationDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 201,
                                                message = "Successful Response"),

            @io.swagger.annotations.ApiResponse(code = 400,
                                                message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401,
                                                message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409,
                                                message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500,
                                                message = "Server Error")
    })

    public Response organizationsPost(@ApiParam(value = "This represents the organization to be added.",
                                                required = true) OrganizationAddDTO organization) {
        return delegate.organizationsPost(organization);
    }
}
