/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.jaxrs.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.FeatureManager;
import org.wso2.carbon.device.mgt.common.push.notification.PushNotificationConfig;
import org.wso2.carbon.device.mgt.common.type.mgt.DeviceTypeMetaDefinition;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderServiceImpl;
import org.wso2.carbon.device.mgt.jaxrs.service.api.DeviceTypeManagementService;
import org.wso2.carbon.device.mgt.jaxrs.service.impl.util.DeviceMgtAPITestUtils;
import org.wso2.carbon.device.mgt.jaxrs.util.DeviceMgtAPIUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class holds the unit tests for the class {@link DeviceTypeManagementService}
 */
@PowerMockIgnore("javax.ws.rs.*")
@SuppressStaticInitializationFor({"org.wso2.carbon.device.mgt.jaxrs.util.DeviceMgtAPIUtils",
        "org.wso2.carbon.context.CarbonContext"})
@PrepareForTest({DeviceMgtAPIUtils.class, MultitenantUtils.class, CarbonContext.class,
        DeviceManagementProviderService.class})
public class DeviceTypeManagementServiceTest {

    private static final Log log = LogFactory.getLog(DeviceManagementServiceImplTest.class);
    private static final String TEST_DEVICE_TYPE = "TEST-DEVICE-TYPE";
    private static final int TEST_DEVICE_TYPE_ID = 12345;
    private static final String DEVICE_TYPE_DESCRIPTION = "TEST DESCRIPTION";
    private static final String MODIFIED_SINCE = "1234503934242";
    private DeviceTypeManagementService deviceTypeManagementService;
    private DeviceManagementProviderService deviceManagementProviderService;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() throws DeviceManagementException {
        log.info("Initializing DeviceTypeManagement tests");
        initMocks(this);
        this.deviceManagementProviderService = Mockito
                .mock(DeviceManagementProviderServiceImpl.class, Mockito.RETURNS_MOCKS);
        this.deviceTypeManagementService = new DeviceTypeManagementServiceImpl();
    }

    @Test(description = "Testing for existing device types.")
    public void testExistingDeviceType() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementService.getDeviceTypes("");
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(description = "Testing get existing device types error")
    public void testExistingDeviceTypesError() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Mockito.when(this.deviceManagementProviderService.getDeviceTypes()).thenThrow(new DeviceManagementException());

        Response response = this.deviceTypeManagementService.getDeviceTypes();
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Testing get existing device types error")
    public void testExistingDeviceTypesModifiedError() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Mockito.when(this.deviceManagementProviderService.getAvailableDeviceTypes()).thenThrow(new
                DeviceManagementException());

        Response response = this.deviceTypeManagementService.getDeviceTypes("");
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test case to retrieve the Features of specified device type.")
    public void testGetDeviceTypeFeatures() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementService.getFeatures(TEST_DEVICE_TYPE, MODIFIED_SINCE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(description = "Test case to test the error scenario when retrieving the Features of specified device type.")
    public void testGetDeviceTypeFeaturesError() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        FeatureManager featureManager = Mockito.mock(FeatureManager.class);
        Mockito.when(this.deviceManagementProviderService.getFeatureManager(Mockito.anyString())).thenReturn
                (featureManager);
        Mockito.when((featureManager).getFeatures()).thenThrow(new DeviceManagementException());
        Response response = this.deviceTypeManagementService.getFeatures(TEST_DEVICE_TYPE, MODIFIED_SINCE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
        Mockito.reset(featureManager);
    }

    @Test(description = "Test getting device type features when feature manager is null.")
    public void testGetDeviceTypeFeaturesWithNoFeatureManager() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Mockito.when(this.deviceManagementProviderService.getFeatureManager(Mockito.anyString())).thenReturn(null);
        Response response = this.deviceTypeManagementService.getFeatures(TEST_DEVICE_TYPE, MODIFIED_SINCE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test to get all the device types.")
    public void testGetDeviceTypes() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementService.getDeviceTypes();
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(description = "Test to get all the device types.")
    public void testGetDeviceTypesWithDeviceTypes() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);

        List<DeviceType> deviceTypes = DeviceMgtAPITestUtils.getDummyDeviceTypeList(5);
        Mockito.when(this.deviceManagementProviderService.getDeviceTypes()).thenReturn(deviceTypes);

        Response response = this.deviceTypeManagementService.getDeviceTypes();
        System.out.println(response.getEntity());
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test to get all the device types for the given name")
    public void testGetDeviceTypeByName() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementService.getDeviceTypeByName(TEST_DEVICE_TYPE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(description = "Test the scenario when there are no device types for the given name.")
    public void testGetDeviceTypeByNameError() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Mockito.when(this.deviceManagementProviderService.getDeviceType(Mockito.anyString())).thenReturn(null);

        Response response = this.deviceTypeManagementService.getDeviceTypeByName(TEST_DEVICE_TYPE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test the scenario when there are no device types for the given name.")
    public void testGetDeviceTypeByNameException() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Mockito.when(this.deviceManagementProviderService.getDeviceType(Mockito.anyString()))
                .thenThrow(new DeviceManagementException());

        Response response = this.deviceTypeManagementService.getDeviceTypeByName(TEST_DEVICE_TYPE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test to get all the device types when given name is null")
    public void testGetDeviceTypeByNameBadRequest() throws Exception {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementService.getDeviceTypeByName(null);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test(description = "Test to clear the sensitive metadata information of device type")
    public void testClearMetaEntryInfo() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method clearMetaEntryInfo = DeviceTypeManagementServiceImpl.class.getDeclaredMethod("clearMetaEntryInfo",
                DeviceType.class);
        clearMetaEntryInfo.setAccessible(true);

        DeviceType deviceType = new DeviceType();
        deviceType.setId(TEST_DEVICE_TYPE_ID);
        deviceType.setName(TEST_DEVICE_TYPE);

        DeviceTypeMetaDefinition deviceTypeMetaDefinition = new DeviceTypeMetaDefinition();
        deviceTypeMetaDefinition.setClaimable(true);
        deviceTypeMetaDefinition.setDescription(DEVICE_TYPE_DESCRIPTION);

        PushNotificationConfig pushNotificationConfig =
                new PushNotificationConfig(TEST_DEVICE_TYPE, true, null);
        deviceTypeMetaDefinition.setPushNotificationConfig(pushNotificationConfig);

        deviceType.setDeviceTypeMetaDefinition(deviceTypeMetaDefinition);

        DeviceType returned = (DeviceType) clearMetaEntryInfo.invoke(this.deviceTypeManagementService, deviceType);

        Assert.assertNotNull(returned.getDeviceTypeMetaDefinition());
    }
}
