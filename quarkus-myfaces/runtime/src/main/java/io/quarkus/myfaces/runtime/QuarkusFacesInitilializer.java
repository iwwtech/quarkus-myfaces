/*
 * Copyright 2019 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.myfaces.runtime;

import java.util.Map;

import javax.enterprise.inject.spi.CDI;
import javax.faces.annotation.FacesConfig;
import javax.faces.model.DataModel;
import javax.servlet.ServletContext;

import org.apache.myfaces.cdi.config.FacesConfigBeanHolder;
import org.apache.myfaces.cdi.model.FacesDataModelClassBeanHolder;
import org.apache.myfaces.spi.FactoryFinderProviderFactory;
import org.apache.myfaces.webapp.FaceletsInitilializer;

import io.quarkus.myfaces.runtime.spi.QuarkusFactoryFinderProviderFactory;

/**
 * Custom FacesInitializer to execute our integration code, always before MyFaces starts.
 * With ServletListeners or other ways, we would have order/priority problems.
 */
public class QuarkusFacesInitilializer extends FaceletsInitilializer {

    @Override
    public void initFaces(ServletContext servletContext) {
        FacesConfigBeanHolder facesConfigBeanHolder = CDI.current().select(FacesConfigBeanHolder.class).get();
        facesConfigBeanHolder.setFacesConfigVersion(FacesConfig.Version.JSF_2_3);

        FactoryFinderProviderFactory.setInstance(new QuarkusFactoryFinderProviderFactory());

        // see FacesDataModelExtension
        FacesDataModelClassBeanHolder holder = CDI.current().select(FacesDataModelClassBeanHolder.class).get();
        for (Map.Entry<Class<? extends DataModel>, Class<?>> typeInfo : QuarkusMyFacesRecorder.FACES_DATA_MODELS.entrySet()) {
            holder.addFacesDataModel(typeInfo.getValue(), typeInfo.getKey());
        }
        holder.getClassInstanceToDataModelWrapperClassMap();

        super.initFaces(servletContext);
    }
}
