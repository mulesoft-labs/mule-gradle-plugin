/*
 * Copyright 2014 juancavallotti.
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
package com.mulesoft.build.domain

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*;

/**
 * Created by juancavallotti on 2/25/15.
 */
class TestDomainOperations {

    @Test
    public void testUpdateDomainName() {

        String newDomainName = 'the_new_domain_name'

        Project proj = ProjectBuilder.builder().withName('mule-domain').build()

        proj.apply plugin: MuleDomainPlugin

        proj.mule.domainName = newDomainName
        proj.mule.muleEnterprise = false

        proj.evaluate()


        String result = proj.mule.resolveDomainName()

        assertEquals('Domain name should be the new one', newDomainName, result)
    }


    @Test
    public void testDomainZipName() {

        String newDomainName = 'the_new_domain_name'

        Project proj = ProjectBuilder.builder().withName('mule-domain').build()

        proj.apply plugin: MuleDomainPlugin

        proj.mule.domainName = newDomainName
        proj.mule.muleEnterprise = false

        proj.version = '1.0.0'

        proj.evaluate()


        String result = proj.domainZip.archiveName

        assertEquals('Zip file name should be the new one', newDomainName + '.zip', result)
    }

}
