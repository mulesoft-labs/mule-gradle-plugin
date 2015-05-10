/*
 * Copyright 2015 juancavallotti.
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

package com.mulesoft.build.util

import org.junit.Test

import static org.junit.Assert.*
import static org.hamcrest.Matchers.*

/**
 * Created by juancavallotti on 5/9/15.
 */
class TestGradleProjectUtils {

    @Test
    void testExtractVersion() {

        String testVersion = '3.6.2'

        MuleVersion version = GradleProjectUtils.stringToVersion(testVersion)

        assertThat('Major should be 3', version.major, equalTo(3))
        assertThat('Minor should be 6', version.minor, equalTo(6))
        assertThat('Patch should be 2', version.patch, equalTo(2))

    }

    @Test
    void testPatchVersionComparison() {
        String left = '3.5.0'
        String right = '3.5.1'

        int result = GradleProjectUtils.compareVersions(left, right)

        assertThat('Should be a negative number', result, lessThan(0))

        result = GradleProjectUtils.compareVersions(right, left)

        assertThat('Should be a positive number', result, greaterThan(0))
    }

    @Test
    void testMinorVersionComparison() {
        String left = '3.5.2'
        String right = '3.6.1'

        int result = GradleProjectUtils.compareVersions(left, right)

        assertThat('Should be a negative number', result, lessThan(0))

        result = GradleProjectUtils.compareVersions(right, left)

        assertThat('Should be a positive number', result, greaterThan(0))
    }

    @Test
    void testMajorVersionComparison() {
        String left = '2.8.0'
        String right = '3.5.1'

        int result = GradleProjectUtils.compareVersions(left, right)

        assertThat('Should be a negative number', result, lessThan(0))

        result = GradleProjectUtils.compareVersions(right, left)

        assertThat('Should be a positive number', result, greaterThan(0))

    }

    @Test
    void testLegacyDetection() {

        String legacy = '3.5.0'

        boolean result = GradleProjectUtils.isLegacyVersion(legacy)

        assertTrue('Version should be legacy', result)
    }
}
